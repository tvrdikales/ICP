package icp;

import icp.graphics.logic.Tank;
import icp.graphics.Terrain;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import icp.graphics.screens.MessageScreen;
import icp.graphics.screens.StartScreen;
import icp.net.client.Client;
import icp.net.server.Server;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;

public class ICP implements GLEventListener {

    public static final float SKYBOX_SIZE = 40f;

    /**
     * Možné pohyby kamery(hráèe) pomocí W,A,S,D
     */
    public enum E_Direction {
        FORWARD, BACKWARD, LEFT, RIGHT;
    }

    /**
     * Násobitel rychlosti otáèení
     */
    public final double ROTATION_MULTIPLIER = 1;

    /**
     * Udává, zdali je zobrazena obrazovka s výbìrem role (øidiè/støelec) po
     * startu aplikace
     */
    public boolean menuDisplayed = true;

    private double uhel = 0;

    private GLU glu = new GLUgl2();
    private GLUT glut = new GLUT();

    /**
     * Souøadnice bodu, na který kouká kamera
     */
    private float camDirX, camDirY, camDirZ;
    /**
     * Souøadnice bodu, na kterém se nachází kamera
     */
    private float camPosX, camPosY, camPosZ;
    /**
     * Poslední známé souøadnice kurzoru myši
     */
    private int cursorX, cursorY;
    /**
     * Úhel ve stupních, který svírá kamera s osou X v rovinì Y=0
     */
    private double angleX;
    /**
     * Úhel ve stupních, který svírá kamera s osou -Z v rovinì X=0
     */
    private double angleY;
    /**
     * Velikost hracího okna. Defaultnì dle nastavených hodnot, za bìhu je možné
     * mìnit
     */
    private int windowWidth = 800;
    private int windowHeight = 600;

    /**
     * Grafické objekty scény
     */
    private StartScreen startScreen = new StartScreen(glut, this);
    private MessageScreen messageScreen = null;
    private Skybox skybox;
    private Terrain terrain;
    private Tank tank;

    private Server server;
    private Client client;
    private long time = 0;
    private int frames = 0;
    private ActiveKey activeKey;

    /**
     * Vytvoøení okna, navìšení listenerù
     *
     * @param args
     */
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        System.setProperty("java.library.path", "C:\\opencv\\build\\java\\x64\\");

        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
        System.loadLibrary("opencv_java249");

        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLWindow canvas = GLWindow.create(caps);

        final ICP main = new ICP(canvas);
        main.start(canvas);
    }

    public ICP(GLWindow canvas) {
        canvas.setTitle("ICP");
        canvas.setSize(800, 600);
        activeKey = new ActiveKey();
        canvas.addWindowListener(new com.jogamp.newt.event.WindowAdapter() {

            @Override
            public void windowDestroyed(com.jogamp.newt.event.WindowEvent we) {
                super.windowDestroyed(we); //To change body of generated methods, choose Tools | Templates.
                System.exit(0);
            }
        });

        canvas.addGLEventListener(this);
        canvas.addKeyListener(new KeyAdapter() {
            
            
            
            @Override
            public void keyReleased(KeyEvent ke) {
                super.keyReleased(ke); //To change body of generated methods, choose Tools | Templates.
                activeKey.setDeactive(ke.getKeyCode());
            }

            @Override
            public void keyPressed(com.jogamp.newt.event.KeyEvent ke) {
                super.keyPressed(ke); //To change body of generated methods, choose Tools | Templates.
                activeKey.setActive(ke.getKeyCode());
                
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_W:
                        tank.turnUp();
                        //updateCameraPosition(E_Direction.FORWARD);
                        break;
                    case KeyEvent.VK_S:
                        tank.turnDown();
                        //updateCameraPosition(E_Direction.BACKWARD);
                        break;
                    case KeyEvent.VK_A:
                        tank.turnLeft();
                        //updateCameraPosition(E_Direction.LEFT);
                        break;
                    case KeyEvent.VK_D:
                        tank.turnRight();
                        //updateCameraPosition(E_Direction.RIGHT);
                        break;
                    case KeyEvent.VK_UP:

                        updateCameraDirection(cursorX, cursorY + 1);
                        break;
                    case KeyEvent.VK_DOWN:

                        updateCameraDirection(cursorX, cursorY - 1);
                        break;
                    case KeyEvent.VK_LEFT:

                        updateCameraDirection(cursorX - 1, cursorY);
                        break;
                    case KeyEvent.VK_RIGHT:

                        updateCameraDirection(cursorX + 1, cursorY);
                        break;
                }

            }
        });

        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent me) {
                super.mouseMoved(me); //To change body of generated methods, choose Tools | Templates.

                updateCameraDirection(me.getX(), me.getY()); // kamera se hýbe i v menu z dùvodu že se mi to vcelku líbí

                if (menuDisplayed) {
                    startScreen.updateMenuItemIfMouseOver(me.getX(), me.getY());
                } else {

                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                super.mousePressed(me); //To change body of generated methods, choose Tools | Templates.
                if (menuDisplayed) {
                    StartScreen.MenuItem pressedItem = startScreen.getMenuItemOnLocation(me.getX(), me.getY());
                    if (pressedItem != null) {
                        switch (pressedItem) {
                            case SERVER:
                                setMessageScreen(new MessageScreen(glut, ICP.this, "Waiting for shooter"));
                                try {
                                    server = new Server(ICP.this);
                                    server.start();
                                } catch (IOException ex) {
                                    Logger.getLogger(ICP.class.getName()).log(Level.SEVERE, null, ex);
                                    System.exit(0);
                                }
                                break;
                            case CLIENT:
                                setMessageScreen(new MessageScreen(glut, ICP.this, "Connecting to server"));
                                client = new Client(ICP.this);
                                client.start();
                                break;
                            default:
                        }
                    }
                } else {

                }
            }
        });
    }

    public void start(GLWindow canvas) {
        canvas.setVisible(true);

        AnimatorBase animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        try {
            GL gl = drawable.getGL();
            gl.getGL2().glEnable(GL.GL_DEPTH_TEST);

            camPosX = 5;
            camPosY = 1.2f;
            camPosZ = 0;

            camDirX = 0;
            camDirY = 0;
            camDirZ = 1;

            angleX = 0;
            angleY = 0;

            cursorX = Integer.MAX_VALUE;
            cursorY = Integer.MAX_VALUE;

            // inicializace objektù scény
            skybox = new Skybox(ICP.SKYBOX_SIZE, drawable.getGL().getGL2());

            // naètou se mapy, ve høe bude nìjaké GUI a možnost volby
            ArrayList<String> paths = Terrain.getPathToMaps();
            terrain = new Terrain(5, 5, 20, 20, 0.7f, paths.get(0));

            this.tank = new Tank(1f, 1f, 1f, 0.2f, 0.2f, 0.3f, gl.getGL2(), terrain, 0.01f, 0.01f, 180, activeKey);

//            for (double x = -2.5; x <= 2.5; x += 0.5) {
//                for (double y = -2.5; y <= 2.5; y += 0.5) {
//                    System.out.println(x+", "+y+" => "+this.terrain.heightInPosition(x, y));
//                }
//            }
        } catch (IOException ex) {
            Logger.getLogger(ICP.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        if (System.nanoTime() - time > 1000000000) {
            time = System.nanoTime();
            //System.out.println("frames = " + frames);
            frames = 0;
        }
        frames++;

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // nastavení kamery
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60, 16 / 9, 1, 100);

//        camPosX = tank.getXStart()+tank.getXSize();
//        camPosY = tank.getYStart();
//        camPosZ = tank.getZStart()+tank.getZSize();
//        
//        camDirX = camPosX;
//        camDirY = camPosY;
//        camDirZ = camPosZ+5;
        glu.gluLookAt(camPosX, camPosY + 0.2, camPosZ, camDirX, camDirY, camDirZ, 0, 1, 0);
//        glu.gluLookAt(tank.getXStart()+camPosX, tank.getYStart()+camPosY, tank.getZStart()+camPosZ, tank.getXStart()+camDirX, tank.getYStart()+camDirY, tank.getZStart()+camDirZ, 0, 1, 0);
        //skybox.draw(gl); // skybox se pøi zobrazeném menu vykresluje z dùvodu že se mi to vcelku líbí víc než statický obrázek
//        if (messageScreen != null) {
//            messageScreen.draw(gl);
//
//            return;
//        }
//
//        if (menuDisplayed) {
//            startScreen.draw(gl);
//            return;
//        }

        // vykreslení objektù
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        terrain.draw(gl);
        //camPosY = terrain.heightByNewPos(camDirX, camPosZ);
        //terrain.setRotateAngle(new Point3D(0, 0, 0));
        //tank.setRotateByPoint(new Point3D(tank.getXStart(), tank.getYStart(), tank.getZStart()));
        //uhel += 4;
        //tank.setRotateAngle(new Point3D(uhel, 0, 0));
        tank.draw(gl);
        //tank.setRotateAngle(new Point3D(10, uhel, 0));
        //tank.setRotateByPoint(new Point3D(tank.getXStart(), tank.getYStart(), tank.getZStart()));
        
        //tank.setRotateByPoint(new Point3D(tank.getXStart(), tank.getYStart(), tank.getZStart()));
        //tank.setRotateByPoint(new Point3D(tank.getXStart(), tank.getYStart(), tank.getZStart()));
        //tank.setRotateAngle(new Point3D(30, 0, 0));
        // vypoètení objektù
        //tank.evaluateNextStep(7);

        //tank.evaluateNextStep(17);
        //System.out.println(box.collision(new Vertex(-2, 0, 0)));
        gl.glLoadIdentity();
        gl.glTranslated(camPosX, camPosY, camPosZ);
        skybox.draw(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        windowHeight = h;
        windowWidth = w;
        startScreen.setVertexModified(true);
    }

    /**
     * Metoda pro posunutí kamery(hráèe)
     *
     * @param direction Smìr, kterým je kamera posunuta vùèi smìru, kterým se
     * dívá
     */
    private void updateCameraPosition(E_Direction direction) {
        float c = 0.04f;
        switch (direction) {
            case FORWARD:
                camPosX += c * Math.cos(angleX);
                camPosZ -= c * Math.sin(angleX);
                camDirX += c * Math.cos(angleX);
                camDirZ -= c * Math.sin(angleX);
                break;
            case BACKWARD:
                camPosX -= c * Math.cos(angleX);
                camPosZ += c * Math.sin(angleX);
                camDirX -= c * Math.cos(angleX);
                camDirZ += c * Math.sin(angleX);
                break;
            case LEFT:
                camPosX += c * Math.cos(angleX + (Math.PI / 2));
                camPosZ -= c * Math.sin(angleX + (Math.PI / 2));
                camDirX += c * Math.cos(angleX + (Math.PI / 2));
                camDirZ -= c * Math.sin(angleX + (Math.PI / 2));
                break;
            case RIGHT:
                camPosX += c * Math.cos(angleX - (Math.PI / 2));
                camPosZ -= c * Math.sin(angleX - (Math.PI / 2));
                camDirX += c * Math.cos(angleX - (Math.PI / 2));
                camDirZ -= c * Math.sin(angleX - (Math.PI / 2));
                break;
        }

        //System.out.println("camPosX = " + camPosX);
        //System.out.println("camPosZ = " + camPosZ);
    }

    /**
     * Metoda pøevádìjící 2D souøadnice kurzoru myši na 3D souøadnice urèující,
     * jakým smìrem je natoèena kamera
     *
     * @param x X souøadnice kurzoru
     * @param y Y souøadnice kurzoru
     */
    private void updateCameraDirection(int x, int y) {
        if (cursorX != Integer.MAX_VALUE && cursorY != Integer.MAX_VALUE) {
            angleX -= (((x - cursorX) * ROTATION_MULTIPLIER) / 180) * Math.PI;
            angleY -= (((y - cursorY) * ROTATION_MULTIPLIER) / 180) * Math.PI;
            angleY = angleY < -Math.PI / 2 ? -Math.PI / 2 : angleY;
            angleY = angleY > Math.PI / 2 ? Math.PI / 2 : angleY;

            camDirX = (float) (camPosX + Math.cos(angleX));
            camDirY = (float) (camPosY + Math.sin(angleY));
            camDirZ = (float) (camPosZ - Math.sin(angleX));
        }
        cursorX = x;
        cursorY = y;

        //System.out.println("angleX = " + angleX + "(" + (angleX / Math.PI) * 180 + ")");
        //System.out.println("angleY = " + angleY + "(" + (angleY / Math.PI) * 180 + ")");
    }

    public void destroyMessage() {
        messageScreen = null;
    }

    public float getCamPosX() {
        return camPosX;
    }

    public float getCamPosY() {
        return camPosY;
    }

    public float getCamPosZ() {
        return camPosZ;
    }

    public synchronized void setMessageScreen(MessageScreen screen) {
        this.messageScreen = screen;
    }

    public GLUT getGLUT() {
        return glut;
    }
}
