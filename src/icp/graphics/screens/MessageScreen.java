package icp.graphics.screens;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import icp.ICP;
import icp.graphics.A_DrawableGrob;
import icp.graphics.primitives.Vertex;

/**
 *
 * @author Ales
 */
public class MessageScreen extends A_Screen {

    private String message;

    public MessageScreen(GLUT glut, ICP icp, String message) {
        super(glut, icp);
        this.message = message;
        this.setVertexModified(true);
        this.setVerticesSize(4);
    }

    @Override
    public void fillVertices() {
        int windowWidthQuater = parentWindow.getWindowWidth() / 4;
        int windowHeightQuater = parentWindow.getWindowHeight() / 4;

        Vertex leftTop = new Vertex(-windowWidthQuater, windowHeightQuater, 1, 0, 0, 1);
        Vertex leftBottom = new Vertex(-windowWidthQuater, -windowHeightQuater, 1, 0, 0, 1);
        Vertex rightBottom = new Vertex(windowWidthQuater, -windowHeightQuater, 1, 0, 0, 1);
        Vertex rightTop = new Vertex(windowWidthQuater, windowHeightQuater, 1, 0, 0, 1);

        putVertices(leftTop, leftBottom, rightBottom, rightTop);
    }

    @Override
    public void draw(GL2 gl) {
        setVertexModified(true);
        gl.glWindowPos2i(parentWindow.getWindowWidth() / 3, parentWindow.getWindowHeight() / 2);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, message);

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(-parentWindow.getWindowWidth() / 2, parentWindow.getWindowWidth() / 2, -parentWindow.getWindowHeight() / 2, parentWindow.getWindowHeight() / 2, -1, 100);
        super.draw(gl);
        gl.glPopMatrix();
    }

}
