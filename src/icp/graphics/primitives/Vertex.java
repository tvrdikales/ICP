package icp.graphics.primitives;

import javafx.geometry.Point3D;

/**
 * Tøída reprezentující vertex ve 3D prostoru
 *
 * @author Ales
 */
public class Vertex extends Point3D {

    private float r=1, g=1, b=1;

    public Vertex(float x, float y, float z) {
        super(x, y, z);
    }

    public Vertex(float x, float y, float z, float r, float g, float b) {
        super(x, y, z);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float getRedComponent() {
        return r;
    }

    public float getGreenComponent() {
        return g;
    }

    public float getBlueComponent() {
        return b;
    }
}
