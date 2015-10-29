/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp;

import com.jogamp.opengl.GL2;
import com.sun.prism.impl.BufferUtil;
import java.nio.FloatBuffer;

/**
 *
 * @author spartan
 */
public abstract class A_DrawableGrob implements I_DrawableGrob {

    private FloatBuffer vertices;
    private FloatBuffer colors;
    private int verticesSize;
    private boolean vertexModified;

    public A_DrawableGrob() {
        this.vertexModified = true;
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public int getVerticesSize() {
        return verticesSize;
    }

    public void setVertexModified(boolean vertexModified) {
        this.vertexModified = vertexModified;
    }

    public abstract void fillVertices();

    public FloatBuffer getColors() {
        return colors;
    }

    public void setColors(FloatBuffer colors) {
        this.colors = colors;
    }

    public void setVerticesSize(int size) {
        this.verticesSize = size;
        this.vertices = BufferUtil.newFloatBuffer(size);
    }

    public void putVertices(float point) {
        this.vertices.put(point);
    }

    @Override
    public void draw(GL2 gl) {
        
        // pokud nastala zmìna, je potøeba znovu naèíst všechny vertexy
        if (this.vertexModified == true){
            //System.out.println("Pøepoèítávám znovu velikost : "+this.verticesSize);
            // nastavení velikost "nového" floatBufferu
            this.vertices = BufferUtil.newFloatBuffer(this.verticesSize);
            this.fillVertices();
            this.vertexModified = false;
        }

        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(1.0f, 1.0f, 0.0f);

        this.vertices.rewind();
        float[] array = new float[this.verticesSize];
        vertices.get(array);

        for (int i = 0; i < this.verticesSize; i = i + 3) {
            gl.glVertex3f(array[i], array[i + 1], array[i + 2]);
        }
        gl.glEnd();
    }

}
