package icp;

import com.jogamp.opengl.GL2;

/**
 * Rozhran� pro v�echny objekty, kter� jsou vykreslov�ny pomoc� openGL
 *
 * @author Ales
 */
public interface I_DrawableGrob {

    public abstract void draw(GL2 gl);
}
