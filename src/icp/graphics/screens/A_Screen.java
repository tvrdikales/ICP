package icp.graphics.screens;

import com.jogamp.opengl.util.gl2.GLUT;
import icp.ICP;
import icp.graphics.A_DrawableGrob;

/**
 *
 * @author Ales
 */
public abstract class A_Screen extends A_DrawableGrob {

    protected GLUT glut;
    protected ICP parentWindow;
    
    public A_Screen(GLUT glut, ICP parentWindow) {
        this.glut = glut;
        this.parentWindow = parentWindow;
    }    
}
