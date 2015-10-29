package icp;

import com.jogamp.opengl.GL2;
import com.sun.prism.impl.BufferUtil;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
    private static String PATH_TO_MAPS = "./textures/maps/";

    /**
     * Konstruktor vytváøející ètvercovou plochu složenou z jednotlivých
     * ètvereèkù
     *
     * @param squareSize Velikost jednoho ètvereèku,
     * @param squareCount Poèet ètvereèkù v jednom smìru
     * @param filePath Cesta k mapì, ze které je vytvoøen terén
     */
    public Terrain(float squareSize, int squareCount, String filePath) {

        this.squareCount = squareCount;
        // naètení výškové mapy, prozatím jen flatland

        // 4 body na quad * 3 (X,Y,Z) * poèet quadù celkem
        //bufferSize = 3 * 4 * (squareCount) * (squareCount) + 1;
        bufferSize = 3 * 4 * (squareCount) * (squareCount);
        vertices = BufferUtil.newFloatBuffer(bufferSize);
        colors = BufferUtil.newFloatBuffer(bufferSize);

        Mat bitmapImage = Highgui.imread(filePath, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

        float startX = -squareSize * (squareCount / 2);
        float startZ = squareSize * (squareCount / 2);

        for (int i = 0; i < squareCount; i++) {
            for (int j = 0; j < squareCount; j++) {
                vertices.put(startX + squareSize * i);
                //vertices.put(0);
                vertices.put(((float) bitmapImage.get(i, j)[0]) / (float) 256 * (ICP.SKYBOX_SIZE / 4f));
                vertices.put(startZ - squareSize * j);

                float green = (float) Math.random();

                colors.put(0);
                colors.put(0);
                colors.put(0);

                vertices.put(startX + (squareSize * i) + squareSize);
                //vertices.put(0);
                vertices.put(((float) bitmapImage.get(i + 1, j)[0]) / (float) 256 * (ICP.SKYBOX_SIZE / 4f));
                vertices.put(startZ - squareSize * j);

                colors.put(0);
                colors.put(0);
                colors.put(0);

                vertices.put(startX + (squareSize * i) + squareSize);
                //vertices.put(0);
                vertices.put(((float) bitmapImage.get(i + 1, j + 1)[0]) / (float) 256 * (ICP.SKYBOX_SIZE / 4f));
                vertices.put(startZ - ((squareSize * j) + squareSize));

                colors.put(0);
                colors.put(0);
                colors.put(0);

                vertices.put(startX + squareSize * i);
                //vertices.put(0);
                vertices.put(((float) bitmapImage.get(i, j + 1)[0]) / (float) 256 * (ICP.SKYBOX_SIZE / 4f));
                vertices.put(startZ - ((squareSize * j) + squareSize));

                colors.put(0);
                colors.put(0);
                colors.put(0);
            }
        }
        
//        IntBuffer vertexArray = IntBuffer.allocate(1);
//        gl.glGenVertexArrays(1, vertexArray);
//        gl.glBindVertexArray(vertexArray.get(0));
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

        for (int i = 0; i < this.bufferSize; i = i + 3) {
            gl.glVertex3f(array[i], array[i + 1], array[i + 2]);
        }
        gl.glEnd();
    }

    /**
     * Naète všechny mapy z adresáøe (Terrain.PATH_TO_MAPS)
     * @return 
     */
    public static ArrayList<String> getPathToMaps() {
        // mìl by asi být Set
        ArrayList<String> paths = new ArrayList<>();

        File folder = new File(PATH_TO_MAPS);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                paths.add(PATH_TO_MAPS+file.getName());
            }
        }

        return paths;
    }

}
