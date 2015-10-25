package icp;

import com.jogamp.opengl.GL2;

/**
 * Rozhraní pro všechny objekty, které jsou vykreslovány pomocí openGL
 *
 * @author Ales
 */
public interface I_DrawableGrob {

    public abstract void draw(GL2 gl);
}
