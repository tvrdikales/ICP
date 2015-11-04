package icp;

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
import com.jogamp.opengl.util.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICP implements GLEventListener {

    public static final boolean DEBUG = false;

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
    
    public static final float SKYBOX_SIZE = 40f;

    private GLU glu;
    /**
     * Skybox
     */
    private Skybox skybox;
    private Terrain terrain;
    private RandomBox box;

    /**
     * Souøadnice bodu, na který kouká kamera
     */
    private double camDirX, camDirY, camDirZ;
    /**
     * Souøadnice bodu, na kterém se nachází kamera
     */
    private double camPosX, camPosY, camPosZ;
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

    private long time = 0;
    private int frames = 0;

    /**
     * Vytvoøení okna, navìšení listenerù
     *
     * @param args
     */
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        System.out.println(System.getProperty("java.library.path"));
        System.setProperty("java.library.path", "C:\\opencv\\build\\java\\x64\\");

        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
        System.loadLibrary("opencv_java249");

        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLWindow canvas = GLWindow.create(caps);

        canvas.setTitle("ICP");
        canvas.setSize(800, 600);
        canvas.setVisible(true);
        canvas.addWindowListener(new com.jogamp.newt.event.WindowAdapter() {

            @Override
            public void windowDestroyed(com.jogamp.newt.event.WindowEvent we) {
                super.windowDestroyed(we); //To change body of generated methods, choose Tools | Templates.
                System.exit(0);
            }
        });

        final ICP main = new ICP();
        canvas.addGLEventListener(main);
        canvas.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(com.jogamp.newt.event.KeyEvent ke) {
                super.keyPressed(ke); //To change body of generated methods, choose Tools | Templates.
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_W:
                        main.updateCameraPosition(E_Direction.FORWARD);
                        break;
                    case KeyEvent.VK_S:
                        main.updateCameraPosition(E_Direction.BACKWARD);
                        break;
                    case KeyEvent.VK_A:
                        main.updateCameraPosition(E_Direction.LEFT);
                        break;
                    case KeyEvent.VK_D:
                        main.updateCameraPosition(E_Direction.RIGHT);
                        break;
                    case KeyEvent.VK_UP:
                        main.updateCameraDirection(main.cursorX, main.cursorY + 1);
                        break;
                    case KeyEvent.VK_DOWN:
                        main.updateCameraDirection(main.cursorX, main.cursorY - 1);
                        break;
                    case KeyEvent.VK_LEFT:
                        main.updateCameraDirection(main.cursorX - 1, main.cursorY);
                        break;
                    case KeyEvent.VK_RIGHT:
                        main.updateCameraDirection(main.cursorX + 1, main.cursorY);
                        break;
                }

            }
        });

        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent me) {
                super.mouseMoved(me); //To change body of generated methods, choose Tools | Templates.
                main.updateCameraDirection(me.getX(), me.getY());
            }
        });

        AnimatorBase animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        try {
            drawable.getGL().getGL2().glEnable(GL.GL_DEPTH_TEST);

            camPosX = 0;
            camPosY = 0;
            camPosZ = 0;

            camDirX = 1;
            camDirY = 0;
            camDirZ = 0;

            angleX = 0;
            angleY = 0;

            cursorX = Integer.MAX_VALUE;
            cursorY = Integer.MAX_VALUE;

            glu = new GLU();
            skybox = new Skybox(ICP.SKYBOX_SIZE, drawable.getGL().getGL2());
            
            // naètou se mapy, ve høe bude nìjaké GUI a možnost volby
            ArrayList<String> paths = Terrain.getPathToMaps(); 
            System.out.println(paths.get(0));
            terrain = new Terrain(5, 5, 10, 10, 3f, paths.get(0));
            
            
            
            this.box = new RandomBox((float)-1, (float)-1, (float)-4, (float)2, (float)2, (float)2, drawable.getGL().getGL2());
            
        } catch (IOException ex) {
            Logger.getLogger(ICP.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (System.nanoTime() - time > 1000000000) {
            time = System.nanoTime();
            //System.out.println("frames = " + frames);
            frames = 0;
        }
        frames++;

        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // nastavení kamery
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-3, 3, -3, 3, -10, 100);
        glu.gluLookAt(camPosX, camPosY, camPosZ, camDirX, camDirY, camDirZ, 0, 1, 0);

        
        // vykreslení objektù
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        box.draw(gl);
        terrain.draw(gl);
        
        gl.glLoadIdentity();
        gl.glTranslated(camPosX, camPosY, camPosZ);
        skybox.draw(gl);
        
        // smazat, jen pro testování
        
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    /**
     * Metoda pro posunutí kamery(hráèe)
     *
     * @param direction Smìr, kterým je kamera posunuta vùèi smìru, kterým se
     * dívá
     */
    private void updateCameraPosition(E_Direction direction) {
        float c = 0.02f;
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
        if (DEBUG) {
            System.out.println("camPosX = " + camPosX);
            System.out.println("camPosZ = " + camPosZ);
        }
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

            camDirX = camPosX + Math.cos(angleX);
            camDirY = camPosY + Math.sin(angleY);
            camDirZ = camPosZ - Math.sin(angleX);
        }
        cursorX = x;
        cursorY = y;

        if (DEBUG) {
            System.out.println("angleX = " + angleX + "(" + (angleX / Math.PI) * 180 + ")");
            System.out.println("angleY = " + angleY + "(" + (angleY / Math.PI) * 180 + ")");
        }
    }
}
