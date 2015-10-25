package icp;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.sun.prism.impl.BufferUtil;
import java.nio.FloatBuffer;

/**
 * Objekt reprezentuj�c� ter�n po kter�m se hr�� pohybuje
 *
 * @author Ales
 */
public class Terrain implements I_DrawableGrob {

    private int bufferSize;
    private FloatBuffer vertices;
    private FloatBuffer colors;
    private int squareCount;

    /**
     * Konstruktor vytv��ej�c� �tvercovou plochu slo�enou z jednotliv�ch
     * �tvere�k�
     *
     * @param squareSize Velikost jednoho �tvere�ku,
     * @param squareCount Po�et �tvere�k� v jednom sm�ru
     */
    public Terrain(float squareSize, int squareCount) {
        this.squareCount = squareCount;
        // na�ten� v��kov� mapy, prozat�m jen flatland

        // 4 body na quad * 3 (X,Y,Z) * po�et quad� celkem
        bufferSize = 3 * 4 * (squareCount) * (squareCount);
        vertices = BufferUtil.newFloatBuffer(bufferSize);
        colors = BufferUtil.newFloatBuffer(bufferSize);

        for (int i = 0; i < squareCount; i++) {
            for (int j = 0; j < squareCount; j++) {
                vertices.put(squareSize * i);
                vertices.put(0);
                vertices.put(-squareSize * j);

                colors.put(0);
                colors.put(1);
                colors.put(0);

                vertices.put((squareSize * i) + squareSize);
                vertices.put(0);
                vertices.put(-squareSize * j);

                colors.put(0);
                colors.put(1);
                colors.put(0);

                vertices.put((squareSize * i) + squareSize);
                vertices.put(0);
                vertices.put(-((squareSize * j) + squareSize));

                colors.put(0);
                colors.put(1);
                colors.put(0);

                vertices.put(squareSize * i);
                vertices.put(0);
                vertices.put(-((squareSize * j) + squareSize));

                colors.put(0);
                colors.put(1);
                colors.put(0);
            }
        }
    }

    @Override
    public void draw(GL2 gl) {
        // nen� kompletn� - nefunguje !!!
        gl.glGenBuffers(bufferSize, null);
        gl.glEnable(GL2.GL_VERTEX_ARRAY);
        gl.glEnable(GL2.GL_COLOR_ARRAY);
        vertices.rewind();
        colors.rewind();
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, colors);
        
        gl.glDrawArrays(GL2.GL_QUADS, 0, squareCount * squareCount);
    }

}
