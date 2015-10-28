package icp;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.sun.prism.impl.BufferUtil;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

/**
 * Objekt reprezentující terén po kterém se hráè pohybuje
 *
 * @author Ales
 */
public class Terrain implements I_DrawableGrob {

    private int bufferSize;
    private FloatBuffer vertices;
    private FloatBuffer colors;
    private int squareCount;

    /**
     * Konstruktor vytváøející ètvercovou plochu složenou z jednotlivých
     * ètvereèkù
     *
     * @param squareSize Velikost jednoho ètvereèku,
     * @param squareCount Poèet ètvereèkù v jednom smìru
     */
    public Terrain(float squareSize, int squareCount, GL2 gl) {
        this.squareCount = squareCount;
        // naètení výškové mapy, prozatím jen flatland

        // 4 body na quad * 3 (X,Y,Z) * poèet quadù celkem
        bufferSize = 3 * 4 * (squareCount) * (squareCount) + 1;
        vertices = BufferUtil.newFloatBuffer(bufferSize);
        colors = BufferUtil.newFloatBuffer(bufferSize);
        
        Mat bitmapImage = Highgui.imread("./textures\\map.bmp", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        
        
        System.out.println("");
        
        float startX = -squareSize * (squareCount / 2);
        float startZ = squareSize * (squareCount / 2);

        for (int i = 0; i < squareCount; i++) {
            for (int j = 0; j < squareCount; j++) {
                vertices.put(startX + squareSize * i);
                vertices.put(0);
                //vertices.put(((float)img2.get(i, j)[0])/(float)256*(ICP.SKYBOX_SIZE/4f));
                vertices.put(startZ - squareSize * j);

                float green = (float)Math.random();
                
                colors.put(0);
                colors.put(0);
                colors.put(0);

                vertices.put(startX + (squareSize * i) + squareSize);
                vertices.put(0);
                //vertices.put(((float)img2.get(i+1, j)[0])/(float)256*(ICP.SKYBOX_SIZE/4f));
                vertices.put(startZ - squareSize * j);

                colors.put(0);
                colors.put(0);
                colors.put(0);

                vertices.put(startX + (squareSize * i) + squareSize);
                vertices.put(0);
                //vertices.put(((float)img2.get(i+1, j+1)[0])/(float)256*(ICP.SKYBOX_SIZE/4f));
                vertices.put(startZ - ((squareSize * j) + squareSize));

                colors.put(0);
                colors.put(0);
                colors.put(0);

                vertices.put(startX + squareSize * i);
                vertices.put(0);
                //vertices.put(((float)img2.get(i, j+1)[0])/(float)256*(ICP.SKYBOX_SIZE/4f));
                vertices.put(startZ - ((squareSize * j) + squareSize));

                colors.put(0);
                colors.put(0);
                colors.put(0);
            }
        }
    }

    @Override
    public void draw(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        vertices.rewind();
        colors.rewind();
        //gl.glDrawArrays(GL2.GL_QUADS, 0, bufferSize);
        int ii = 0;
        float[] array = new float[bufferSize];
        vertices.get(array);

        while (ii < bufferSize - 1) {
            gl.glVertex3f(array[ii], array[ii + 1], array[ii + 2]);
            gl.glVertex3f(array[ii + 3], array[ii + 4], array[ii + 5]);
            gl.glVertex3f(array[ii + 6], array[ii + 7], array[ii + 8]);
            gl.glVertex3f(array[ii + 9], array[ii + 10], array[ii + 11]);
            ii = ii + 12;
        }
        gl.glEnd();
    }

}
