package icp.graphics.screens;

import icp.graphics.primitives.Vertex;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import icp.ICP;
import java.awt.Rectangle;

/**
 *
 * @author Ales
 */
public class StartScreen extends A_Screen {

    public enum MenuItem {

        CLIENT("Shooter (client)", 130),
        SERVER("Driver (server)", 120);

        private int textWidth;
        private String text;
        private Rectangle bounds;

        private MenuItem(String text, int textWidth) {
            this.text = text;
            this.textWidth = textWidth;
        }

        public String getText() {
            return text;
        }

        public int getTextWidth() {
            return textWidth;
        }
    }

    private boolean clientHover = false;
    private boolean serverHover = false;

    public StartScreen(GLUT glutInstance, ICP parentWindow) {
        super(glutInstance, parentWindow);

        this.setVerticesSize(8);
        this.setVertexModified(true);
    }

    @Override
    public void fillVertices() {
        // bylo by lepší generovat vertexy univerzálnì na základì poètu zobrazených položek...
        if (!serverHover) {
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), 1 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), 1 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), 2 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), 2 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
        } else {
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), 1 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), 1 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), 2 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), 2 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
        }

        if (!clientHover) {
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), -1 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), -1 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), -2 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), -2 * (parentWindow.getWindowHeight() / 8), 1, 0, 0, 1));

        } else {
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), -1 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), -1 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
            putVertices(new Vertex((parentWindow.getWindowWidth() / 4), -2 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
            putVertices(new Vertex(-(parentWindow.getWindowWidth() / 4), -2 * (parentWindow.getWindowHeight() / 8), 1, 1, 0, 1));
        }
    }

    @Override
    public void draw(GL2 gl) {
        gl.glWindowPos2i((parentWindow.getWindowWidth() / 2) - MenuItem.SERVER.getTextWidth() / 2, (int) (1.35 * (parentWindow.getWindowHeight() / 2)));
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, MenuItem.SERVER.getText());
        gl.glWindowPos2i((parentWindow.getWindowWidth() / 2 - MenuItem.CLIENT.getTextWidth() / 2), (int) (0.6 * (parentWindow.getWindowHeight() / 2)));
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, MenuItem.CLIENT.getText());

        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(-parentWindow.getWindowWidth() / 2, parentWindow.getWindowWidth() / 2, -parentWindow.getWindowHeight() / 2, parentWindow.getWindowHeight() / 2, -1, 100);
        super.draw(gl);
        gl.glPopMatrix();
    }

    public void updateMenuItemIfMouseOver(int x, int y) {
        x = x - (parentWindow.getWindowWidth() / 2);
        y = (parentWindow.getWindowHeight() / 2) - y;

        clientHover = false;
        serverHover = false;
        
        setVertexModified(true);
        
        if (x >= -(parentWindow.getWindowWidth() / 4) & x <= (parentWindow.getWindowWidth() / 4)) {

            if (y >= 1 * (parentWindow.getWindowHeight() / 8) & y <= 2 * (parentWindow.getWindowHeight() / 8)) {
                serverHover = true;
                return;

            }

            if (y >= -2 * (parentWindow.getWindowHeight() / 8) & y <= -1 * (parentWindow.getWindowHeight() / 8)) {
                clientHover = true;
            }
        }

    }

    public MenuItem getMenuItemOnLocation(int x, int y) {
        if(clientHover == true) return MenuItem.CLIENT;
        if(serverHover == true) return MenuItem.SERVER;
        return null;
    }
}
