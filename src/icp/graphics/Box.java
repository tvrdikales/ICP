package icp.graphics;

import icp.graphics.primitives.Vertex;
import com.jogamp.opengl.GL2;

/**
 *
 * @author spartan
 */
public class Box extends A_DrawableGrob {

    private float xStart, yStart, zStart;
    private float xSize , ySize, zSize;
    private float red = 1, green = 1, blue = 1;

    public Box(float xStart, float yStart, float zStart, float xSize, float ySize, float zSize, GL2 gl) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.zStart = zStart;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;

        this.setVerticesSize(6*4);
    }

    public void setRed(float red) {
        this.red = red;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getZStart() {
        return zStart;
    }

    public float getZSize() {
        return zSize;
    }

    public float getYStart() {
        return yStart;
    }

    public float getYSize() {
        return ySize;
    }

    public float getXStart() {
        return xStart;
    }

    public float getXSize() {
        return xSize;
    }

    /**
     * Posun X souøadnice objektu
     *
     * @param change O kolik se má posunout
     */
    public void changeXPosition(float change) {
        this.xStart += change;
    }

    /**
     * Posun Y souøadnice objektu
     *
     * @param change O kolik se má posunout
     */
    public void changeYPosition(float change) {
        this.yStart += change;
    }

    /**
     * Posun Z souøadnice objektu
     *
     * @param change O kolik se má posunout
     */
    public void changeZPosition(float change) {
        this.zStart += change;
    }

    public void setXStart(float xStart) {
        this.xStart = xStart;
    }

    public void setYStart(float yStart) {
        this.yStart = yStart;
    }

    public void setZStart(float zStart) {
        this.zStart = zStart;
    }

    @Override
    public void fillVertices() {

        // defaultní barva vertexu je 1,1,1
        Vertex bottomLeftFront = new Vertex(xStart, yStart, zStart, red, green+0.1f, blue);
        Vertex bottomRightFront = new Vertex(xStart + xSize, yStart, zStart, red, green, blue);
        Vertex bottomRightBack = new Vertex(xStart + xSize, yStart, zStart + zSize, red+0.2f, green, blue);
        Vertex bottomLeftBack = new Vertex(xStart, yStart, zStart + zSize, red, green, blue);
        Vertex topLeftFront = new Vertex(xStart, yStart + ySize, zStart, red, green, blue);
        Vertex topRightFront = new Vertex(xStart + xSize, yStart + ySize, zStart, red, green, blue+0.3f);
        Vertex topRightBack = new Vertex(xStart + xSize, yStart + ySize, zStart + zSize, red, green, blue);
        Vertex topLeftBack = new Vertex(xStart, yStart + ySize, zStart + zSize, red, green, blue);

        //front
        this.putVertices(bottomLeftFront);
        this.putVertices(bottomRightFront);
        this.putVertices(topRightFront);
        this.putVertices(topLeftFront);

        // back
        this.putVertices(bottomRightBack);
        this.putVertices(bottomLeftBack);
        this.putVertices(topLeftBack);
        this.putVertices(topRightBack);

        // left
        this.putVertices(bottomLeftBack);
        this.putVertices(bottomLeftFront);
        this.putVertices(topLeftFront);
        this.putVertices(topLeftBack);

        // right
        this.putVertices(bottomRightFront);
        this.putVertices(bottomRightBack);
        this.putVertices(topRightBack);
        this.putVertices(topRightFront);

        // bottom
        this.putVertices(bottomRightFront);
        this.putVertices(bottomLeftFront);
        this.putVertices(bottomLeftBack);
        this.putVertices(bottomRightBack);

        // top
        this.putVertices(topLeftFront);
        this.putVertices(topRightFront);
        this.putVertices(topRightBack);
        this.putVertices(topLeftBack);
    }
}
