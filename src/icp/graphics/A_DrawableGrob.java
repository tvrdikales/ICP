package icp.graphics;

import icp.graphics.primitives.Vertex;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javafx.geometry.Point3D;

/**
 *
 * @author spartan
 */
public abstract class A_DrawableGrob {

    private FloatBuffer vertices;
    private FloatBuffer colors;
    private IntBuffer indices = null;
    private int indicesCount = 0;

    private double collisionXMin, collisionXMax;
    private double collisionYMin, collisionYMax;
    private double collisionZMin, collisionZMax;

    private int verticesSize;
    private boolean vertexModified;

    private Point3D rotateAngle = new Point3D(0, 0, 0);
    private Point3D rotateByPoint = new Point3D(0, 0, 0);

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
        this.vertices = Buffers.newDirectFloatBuffer(size * 3);
        this.colors = Buffers.newDirectFloatBuffer(size * 3);
    }

    public void enableIndicesDraw(int[] indices) {
        this.indices = Buffers.newDirectIntBuffer(indices);
        this.indices.rewind();
        this.indicesCount = indices.length;
    }

    public void disableIndicesDraw() {
        this.indices = null;
    }

    public void putVertices(Vertex... vertices) {
        for (Vertex vertex : vertices) {
            this.vertices.put((float) vertex.getX());
            this.vertices.put((float) vertex.getY());
            this.vertices.put((float) vertex.getZ());

            this.colors.put(vertex.getRedComponent());
            this.colors.put(vertex.getGreenComponent());
            this.colors.put(vertex.getBlueComponent());
        }
    }

    public void draw(GL2 gl) {
        // pokud nastala zmìna, je potøeba znovu naèíst všechny vertexy
        if (this.vertexModified == true) {
            //System.out.println("Pøepoèítávám znovu velikost : "+this.verticesSize);
            // nastavení velikost "nového" floatBufferu
            this.vertices = Buffers.newDirectFloatBuffer(this.verticesSize * 3);
            this.fillVertices();
            // s každou zmìnou se zmìní hranice
            this.evaluateCollisionBorder();
            this.vertexModified = false;
            vertices.rewind();
            colors.rewind();
        }

        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glTranslated(rotateByPoint.getX(), rotateByPoint.getY(), rotateByPoint.getZ());
        gl.glRotated(this.rotateAngle.getY(), 0, 1, 0);
        gl.glRotated(this.rotateAngle.getX(), 1, 0, 0);

        gl.glRotated(this.rotateAngle.getZ(), 0, 0, 1);
        gl.glTranslated(-rotateByPoint.getX(), -rotateByPoint.getY(), -rotateByPoint.getZ());

        if (indices == null) {
            drawVertexArray(gl);
        } else {
            drawIndexedVertexArray(gl);
        }

        gl.glPopMatrix();

    }

    /**
     * Funkce, která pøepoèítá hranièní body pro kolizi
     */
    private void evaluateCollisionBorder() {
        if (verticesSize > 3) {
            collisionXMax = vertices.get(0);
            collisionXMin = collisionXMax;

            collisionYMax = vertices.get(1);
            collisionYMin = collisionYMax;

            collisionZMax = vertices.get(2);
            collisionZMin = collisionZMax;
        }

        for (int i = 0; i < verticesSize / 3; i++) {
            float x = vertices.get(i * 3);
            float y = vertices.get(i * 3 + 1);
            float z = vertices.get(i * 3 + 2);

            if (x > collisionXMax) {
                collisionXMax = x;
            }
            if (x < collisionXMin) {
                collisionXMin = x;
            }

            if (y > collisionYMax) {
                collisionYMax = y;
            }
            if (y < collisionYMin) {
                collisionYMin = y;
            }

            if (z > collisionZMax) {
                collisionZMax = z;
            }
            if (z < collisionZMin) {
                collisionZMin = z;
            }

        }
    }

    /**
     * Vypoète, zda je bod v kolizi s objektem Momentálnì se nejedná o pøímou
     * kolizi ale o nutnou podmínku! (Probém natoèené krabice)
     *
     * @param vertex
     * @return
     */
    public boolean collision(Vertex vertex) {
        if ((vertex.getX() < collisionXMin) || (vertex.getX() > collisionXMax)
                || (vertex.getY() < collisionYMin) || (vertex.getY() > collisionYMax)
                || (vertex.getZ() < collisionZMin) || (vertex.getZ() > collisionZMax)) {
            return false;
        } else {
            return true;
        }
    }

    public void writeCollisions() {
        System.out.println(this.collisionXMin + " <-> " + this.collisionXMax + ", " + this.collisionYMin + " <-> " + this.collisionYMax + ", " + this.collisionZMin + " <-> " + this.collisionZMax);
    }

    /**
     * Kolize celého objektu s jiným, ke kolizi staèí jediný vertex uvnitø
     * druhého objektu. Opìt jde jen o nutnou podmínku kolize
     *
     * @param drawableGrob
     * @return
     */
    public boolean collision(A_DrawableGrob drawableGrob) {
        if (drawableGrob.getVerticesSize() >= 3) {

            // žádný z vertexù nesmí být v kolizi s objektem, jinak koliduje
            boolean collision = false;
            for (int i = 0; i < verticesSize / 3; i++) {
                float x = drawableGrob.getVertices().get(i * 3);
                float y = drawableGrob.getVertices().get(i * 3 + 1);
                float z = drawableGrob.getVertices().get(i * 3 + 2);
                collision = this.collision(new Vertex(x, y, z));
                // pokud došlo ke kolizi, není nutné procházet další body
                if (collision == true) {
                    return true;
                }
            }
            return collision;
        } else {
            // s prázdným tìlesem (neexistujícím) se nelze støetnout
            return false;
        }
    }

    private void drawVertexArray(GL2 gl) {

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);

        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
        gl.glColorPointer(3, GL2.GL_FLOAT, 0, colors);

        gl.glDrawArrays(GL2.GL_QUADS, 0, verticesSize);  // poèet vertexù v poli
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

        vertices.rewind();
        colors.rewind();
    }

    private void drawIndexedVertexArray(GL2 gl) {
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL2.GL_INDEX_ARRAY);   // enable vertex indices 

        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
        gl.glColorPointer(3, GL2.GL_FLOAT, 0, colors);
        gl.glIndexPointer(GL2.GL_INT, 0, indices);

        gl.getGL2ES1().glDrawElements(GL2.GL_QUADS, indicesCount, GL2.GL_UNSIGNED_INT, indices);

        gl.glDisableClientState(GL2.GL_INDEX_ARRAY);   // enable vertex indices 
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
    }

    /**
     * Nastaví úhel rotace
     *
     * @param rotate
     */
    public void setRotateAngle(Point3D rotate) {
        this.rotateAngle = rotate;
    }

    /**
     * Nastaví bod rotace
     *
     * @param rotateByPoint
     */
    public void setRotateByPoint(Point3D rotateByPoint) {
        this.rotateByPoint = rotateByPoint;
    }

}
