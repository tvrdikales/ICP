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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICP implements GLEventListener {

    public static final boolean DEBUG = true;

    /**
     * Mo�n� pohyby kamery(hr��e) pomoc� W,A,S,D
     */
    public enum E_Direction {

        FORWARD, BACKWARD, LEFT, RIGHT;
    }

    /**
     * N�sobitel rychlosti ot��en�
     */
    public final double ROTATION_MULTIPLIER = 1;

    private GLU glu;
    /**
     * Skybox
     */
    private Skybox skybox;
    private Terrain terrain;

    /**
     * Sou�adnice bodu, na kter� kouk� kamera
     */
    private double camDirX, camDirY, camDirZ;
    /**
     * Sou�adnice bodu, na kter�m se nach�z� kamera
     */
    private double camPosX, camPosY, camPosZ;
    /**
     * Posledn� zn�m� sou�adnice kurzoru my�i
     */
    private int cursorX, cursorY;
    /**
     * �hel ve stupn�ch, kter� sv�r� kamera s osou X v rovin� Y=0
     */
    private double angleX;
    /**
     * �hel ve stupn�ch, kter� sv�r� kamera s osou -Z v rovin� X=0
     */
    private double angleY;

    /**
     * Vytvo�en� okna, nav�en� listener�
     *
     * @param args
     */
    public static void main(String[] args) {
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
            camPosX = 0;
            camPosY = 5;
            camPosZ = 0;

            camDirX = 1;
            camDirY = 0;
            camDirZ = 0;

            angleX = 0;
            angleY = 0;

            cursorX = Integer.MAX_VALUE;
            cursorY = Integer.MAX_VALUE;

            glu = new GLU();
            skybox = new Skybox(500, drawable.getGL().getGL2());
            terrain = new Terrain(1.0f, 100,drawable.getGL().getGL2());
        } catch (IOException ex) {
            Logger.getLogger(ICP.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClearColor(0, 0, 0, 0);

        // nastaven� kamery
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-20, 20, -20, 20, 0, 800);
        glu.gluLookAt(camPosX, camPosY, camPosZ, camDirX, camDirY, camDirZ, 0, 1, 0);

        // vykreslen� objekt�
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        terrain.draw(gl);
        skybox.draw(gl);
        gl.glTranslated(camPosX, camPosY, camPosZ);
        
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    /**
     * Metoda pro posunut� kamery(hr��e)
     *
     * @param direction Sm�r, kter�m je kamera posunuta v��i sm�ru, kter�m se
     * d�v�
     */
    private void updateCameraPosition(E_Direction direction) {
        switch (direction) {
            case FORWARD:
                camPosX += Math.cos(angleX);
                camPosZ -= Math.sin(angleX);
                camDirX += Math.cos(angleX);
                camDirZ -= Math.sin(angleX);
                break;
            case BACKWARD:
                camPosX -= Math.cos(angleX);
                camPosZ += Math.sin(angleX);
                camDirX -= Math.cos(angleX);
                camDirZ += Math.sin(angleX);
                break;
            case LEFT:
                camPosX += Math.cos(angleX + (Math.PI / 2));
                camPosZ -= Math.sin(angleX + (Math.PI / 2));
                camDirX += Math.cos(angleX + (Math.PI / 2));
                camDirZ -= Math.sin(angleX + (Math.PI / 2));
                break;
            case RIGHT:
                camPosX += Math.cos(angleX - (Math.PI / 2));
                camPosZ -= Math.sin(angleX - (Math.PI / 2));
                camDirX += Math.cos(angleX - (Math.PI / 2));
                camDirZ -= Math.sin(angleX - (Math.PI / 2));
                break;
        }
        if (DEBUG) {
            System.out.println("camPosX = " + camPosX);
            System.out.println("camPosZ = " + camPosZ);
        }
    }

    /**
     * Metoda p�ev�d�j�c� 2D sou�adnice kurzoru my�i na 3D sou�adnice ur�uj�c�,
     * jak�m sm�rem je nato�ena kamera
     *
     * @param x X sou�adnice kurzoru
     * @param y Y sou�adnice kurzoru
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