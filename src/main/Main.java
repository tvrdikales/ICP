package main;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import static com.jogamp.opengl.GLProfile.GL3;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.jogamp.opengl.util.*;

public class Main implements GLEventListener {
int degrees;
    GLU glu;

    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("AWT Window Test");
        frame.setSize(800, 600);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(new Main());

        AnimatorBase animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    public Main() {
        glu = new GLU();
        degrees=0;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        render(drawable);
        //degrees++;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    }

    private void render(GLAutoDrawable drawable) {
        float i = (float) -0.25;
        float y = (float) 0.25;

        GL2 gl = drawable.getGL().getGL2();        

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glRotatef(degrees, 0, 1, 0);
        // draw a skybox
        gl.glBegin(GL2.GL_QUADS);

        // bottom
        gl.glColor3f(1, 1, 1);
        gl.glVertex3f(i, i, i);
        gl.glVertex3f(i, i, y);
        gl.glVertex3f(y, i, y);
        gl.glVertex3f(y, i, i);
        // top
        gl.glColor3f(1, i, 1);
        gl.glVertex3f(i, y, i);
        gl.glVertex3f(y, y, i);
        gl.glVertex3f(y, y, y);
        gl.glVertex3f(i, y, y);
        // left
        gl.glColor3f(1, 1, 1);
        gl.glVertex3f(i, i, i);
        gl.glVertex3f(i, y, i);
        gl.glVertex3f(i, y, y);
        gl.glVertex3f(i, i, y);
        // right
        gl.glColor3f(1, 0, 0);
        gl.glVertex3f(y, i, i);
        gl.glVertex3f(y, i, y);
        gl.glVertex3f(y, y, y);
        gl.glVertex3f(y, y, i);        
        // back
        gl.glColor3f(0, 0, 1);
        gl.glVertex3f(i, i, y);
        gl.glVertex3f(i, y, y);
        gl.glVertex3f(y, y, y);
        gl.glVertex3f(y, i, y);
        // front
        gl.glColor3f(1, 1, 0);
        gl.glVertex3f(i, i, i);
        gl.glVertex3f(y, i, i);
        gl.glVertex3f(y, y, i);
        gl.glVertex3f(i, y, i);

        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        //glu.gluPerspective(0, 10, 1, 1000);
        
//        
//        gl.glMatrixMode(gl.GL_MODELVIEW);
//        gl.glLoadIdentity();
        gl.glEnd();
        glu.gluLookAt(0, 0, 0, -1, -1,-1, 0, 1, 0);
        
    }
}
