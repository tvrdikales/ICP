package icp.graphics;

import icp.graphics.primitives.Vertex;
import icp.Mathematic;
import icp.graphics.primitives.Map2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Point3D;
import javafx.util.Pair;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

/**
 * Objekt reprezentuj�c� ter�n po kter�m se hr�� pohybuje
 *
 * @author Ales
 */
public class Terrain extends A_DrawableGrob {

    private static final String PATH_TO_MAPS = "./textures/maps/";

    private final Mat bitmapImage;
    private final double squareToPixelX, squareToPixelZ;
    private final double minImageColor, maxImageColor;
    private final float height;
    private final int squareCountX, squareCountZ;
    private final float sizeX, sizeZ;
    private final Map2D map2D;

    /**
     * Konstruktor vytv��ej�c� �tvercovou plochu slo�enou z jednotliv�ch
     * �tvere�k�
     *
     * Pokud je po�et vytvo�en�ch �tverc� v�razn� ni��� ne� po�et pixel�
     * obr�zku, z kter�ho je ter�n vytv��en, jsou n�sledn� do v�sledku po��t�ny
     * jen 2 v�hov� vyhodnocen� nejbli��� pixely, resp. ve 2 rozm�rech 4
     *
     * @param sizeX Velikost cel�ho ter�nu (x)
     * @param sizeZ Velikost cel�ho ter�nu (z)
     * @param squareCountX Po�et �tverc� na ose x
     * @param squareCountZ po�et �tverc� na ose z
     * @param height maxim�ln� v��ka na map�, minim�ln� je V�DY 0
     * @param filePath cesta k map�
     */
    public Terrain(float sizeX, float sizeZ, int squareCountX, int squareCountZ, float height, String filePath) {
        // 4 body na quad * po�et quad� celkem
        this.squareCountX = squareCountX;
        this.squareCountZ = squareCountZ;
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;

        this.setVerticesSize((squareCountX + 1) * (squareCountZ + 1));

        this.height = height;
        this.bitmapImage = Highgui.imread(filePath, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

        double maxImageColor1 = bitmapImage.get(0, 0)[0];
        double minImageColor1 = bitmapImage.get(0, 0)[0];
        for (int i = 0; i < bitmapImage.rows(); i++) {
            for (int j = 0; j < bitmapImage.cols(); j++) {
                if (maxImageColor1 < bitmapImage.get(i, j)[0]) {
                    maxImageColor1 = bitmapImage.get(i, j)[0];
                }
                if (minImageColor1 > bitmapImage.get(i, j)[0]) {
                    minImageColor1 = bitmapImage.get(i, j)[0];
                }
            }
        }

        // pokud jsou stejn�, do�lo by ve funkci heightFromMap() k d�len� nulou, 
        // z�rove� by platilo image.get(i,j) = minImageColor = maxImageColor, pro i,j e Image
        // Za tohoto p�edpokladu, je mo�n� libovoln� zv��it maxImageColor
        // rovn� mo�no zm�nit na if ve funkci s v�sledkem v��ku = 0, z�ejm� i rychlej��
        if (minImageColor1 == maxImageColor1) {
            this.maxImageColor = maxImageColor1 + 1;
        } else {
            this.maxImageColor = maxImageColor1;
        }
        this.minImageColor = minImageColor1;

        // p�evodov� pom�r na pixely z mapy, my�lenkov� p�ed�l�no na body, proto bitmapImage.rows-1
        this.squareToPixelX = (double) (bitmapImage.rows() - 1) / (double) (squareCountX);
        this.squareToPixelZ = (double) (bitmapImage.cols() - 1) / (double) (squareCountZ);
        this.map2D = new Map2D();
    }

    /**
     * Nefunguje, velk� odchylka, zvl�t� pokud po�et pixel� v mat je v�razn�
     * vy��� ne� rozli�en� mapy
     * 
     * @deprecated 
     *
     * @param x
     * @param z
     * @return
     */
    public float heightInPosition(double x, double z) {
        if (x > sizeX / 2 || x < -sizeX / 2 || z > sizeZ / 2 || z < -sizeZ / 2) {
            return -1;
        } else {
            return this.heightFromMap((x + sizeX / 2) / (sizeX / squareCountX), (z + sizeZ / 2) / (sizeZ / squareCountZ));
        }
    }

    public float heightByNewPos(double x, double z) {
        if (x > sizeX / 2 || x < -sizeX / 2 || z > sizeZ / 2 || z < -sizeZ / 2) {
            return -1;
        } else {
            return heightFromMapByMap(x, z);
        }
    }

    private float heightFromMapByMap(double x, double z) {
        //System.out.println(200*squareToPixelX+" : "+200*squareToPixelZ+" | "+bitmapImage.rows()+" : "+bitmapImage.cols());
        //return (float) (((bitmapImage.get((int) (squareToPixelX * x), (int) (squareToPixelZ * (z)))[0]) - minImageColor) / (maxImageColor - minImageColor));
        //System.out.println(squareToPixelX+" vs. "+squareToPixelZ);

        // p�esn� vypo��tan� um�st�n� na pixelu v re�ln�ch ��slech
        int xSize = this.map2D.getXSize();
        int zSize = this.map2D.getZSize();
        
        double directX = ((x + sizeX / 2) / sizeX) * (xSize - 1);
        double directZ = ((-z + sizeZ / 2) / sizeZ) * (zSize - 1);

        //System.out.println(directX + " : " + directZ);
        int lowerPartX = Mathematic.lowerPart(directX);
        int upperPartX = lowerPartX + 1;
        int lowerPartZ = Mathematic.lowerPart(directZ);
        int upperPartZ = lowerPartZ + 1;

        // �patn� p�eveden� ��sla m��e o velmi malou ��st p�es�hnout max. hodnotu
        if (lowerPartX > xSize - 1) {
            lowerPartX = xSize - 1;
        }

        if (lowerPartZ > zSize - 1) {
            lowerPartZ = zSize - 1;
        }
        // posledn� ��sla jsou v�dy nastaveny v��e a mus� b�t zad�ny na 
        //max. mo�nou hodnotu, z�rove� podl�haj� stejn�mu vlivu jako v��e uveden�
        if (upperPartX > xSize - 1) {
            upperPartX = xSize - 1;
        }

        if (upperPartZ > zSize - 1) {
            upperPartZ = zSize - 1;
        }

        double leftTopPixelWeight = (upperPartX - directX) * (upperPartZ - directZ);
        double rightTopPixelWeight = (directX - lowerPartX) * (upperPartZ - directZ);
        double leftBottomPixelWeight = (upperPartX - directX) * (directZ - lowerPartZ);
        double rightBottomPixelWeight = (directX - lowerPartX) * (directZ - lowerPartZ);

        // pokud se horn� a doln� pixel pro v�po�et shoduj�, v��e uveden� v�po�ty
        // jsou nepou�iteln� (jejich sum = 0)
        if ((lowerPartX == upperPartX) && (lowerPartZ == upperPartZ)) {
            // pokud se shoduj� v�echny, je lhostejn�, jak se rozd�l� v�hy
            // nav�c se z�ejm� jedn� o pixel image[maxX, maxY]
            leftBottomPixelWeight = 1;
            rightBottomPixelWeight = 0;
            leftTopPixelWeight = 0;
            rightTopPixelWeight = 0;
        } else if (lowerPartX == upperPartX) {
            // x-ov� hodnoty se shoduj� -> jsou s P(1) na dan� x-ov� sou�adnici
            // -> sta�� pouze 2 hodnoty (horn� a doln� pixel k sob� pat��c� dvojice)
            leftTopPixelWeight = (upperPartZ - directZ);
            leftBottomPixelWeight = (directZ - lowerPartZ);
            rightTopPixelWeight = 0;
            rightBottomPixelWeight = 0;
        } else if (lowerPartZ == upperPartZ) {
            // z-ov� hodnoty se shoduj� -> jsou s P(1) na dan� y-ov� sou�adnici
            // -> sta�� pouze 2 hodnoty (prav� a lev� pixel, k sob� pat��c� dvojice)
            leftTopPixelWeight = (upperPartX - directX);
            rightTopPixelWeight = (directX - lowerPartX);
            leftBottomPixelWeight = 0;
            rightBottomPixelWeight = 0;
        }

        // v�sledek ze �ty� pixel� pomoc� v�hy, le��c� <minImageColor, maxImageColor>
        double resultFromForPixels = this.map2D.getValue(lowerPartX, lowerPartZ) * leftTopPixelWeight + this.map2D.getValue(upperPartX, lowerPartZ) * rightTopPixelWeight + this.map2D.getValue(lowerPartX, upperPartZ) * leftBottomPixelWeight + this.map2D.getValue(upperPartX, upperPartZ) * rightBottomPixelWeight;

        //System.out.println(directX + ", " + directZ + " -> " + resultFromForPixels + " | " + leftTopPixelWeight + "," + leftBottomPixelWeight + "," + rightTopPixelWeight + "," + rightBottomPixelWeight);
        //System.out.println(directX+" - "+x+" | "+directZ+" - "+z);
        //System.out.println(directX - (int) directX);
        // ze 4 bod� linern� funkce
        return (float)resultFromForPixels;
    }

    private float heightFromMap(double x, double z) {
        //System.out.println(200*squareToPixelX+" : "+200*squareToPixelZ+" | "+bitmapImage.rows()+" : "+bitmapImage.cols());
        //return (float) (((bitmapImage.get((int) (squareToPixelX * x), (int) (squareToPixelZ * (z)))[0]) - minImageColor) / (maxImageColor - minImageColor));
        //System.out.println(squareToPixelX+" vs. "+squareToPixelZ);

        // p�esn� vypo��tan� um�st�n� na pixelu v re�ln�ch ��slech
        double directX = squareToPixelX * x;
        double directZ = squareToPixelZ * z;

        //System.out.println(directX + " : " + directZ);
        int lowerPartX = Mathematic.lowerPart(directX);
        int upperPartX = lowerPartX + 1;
        int lowerPartZ = Mathematic.lowerPart(directZ);
        int upperPartZ = lowerPartZ + 1;

        // �patn� p�eveden� ��sla m��e o velmi malou ��st p�es�hnout max. hodnotu
        if (lowerPartX > bitmapImage.rows() - 1) {
            lowerPartX = bitmapImage.rows() - 1;
        }

        if (lowerPartZ > bitmapImage.cols() - 1) {
            lowerPartZ = bitmapImage.cols() - 1;
        }
        // posledn� ��sla jsou v�dy nastaveny v��e a mus� b�t zad�ny na 
        //max. mo�nou hodnotu, z�rove� podl�haj� stejn�mu vlivu jako v��e uveden�
        if (upperPartX > bitmapImage.rows() - 1) {
            upperPartX = bitmapImage.rows() - 1;
        }

        if (upperPartZ > bitmapImage.cols() - 1) {
            upperPartZ = bitmapImage.cols() - 1;
        }

        double leftTopPixelWeight = (upperPartX - directX) * (upperPartZ - directZ);
        double rightTopPixelWeight = (directX - lowerPartX) * (upperPartZ - directZ);
        double leftBottomPixelWeight = (upperPartX - directX) * (directZ - lowerPartZ);
        double rightBottomPixelWeight = (directX - lowerPartX) * (directZ - lowerPartZ);

        // pokud se horn� a doln� pixel pro v�po�et shoduj�, v��e uveden� v�po�ty
        // jsou nepou�iteln� (jejich sum = 0)
        if ((lowerPartX == upperPartX) && (lowerPartZ == upperPartZ)) {
            // pokud se shoduj� v�echny, je lhostejn�, jak se rozd�l� v�hy
            // nav�c se z�ejm� jedn� o pixel image[maxX, maxY]
            leftBottomPixelWeight = 1;
            rightBottomPixelWeight = 0;
            leftTopPixelWeight = 0;
            rightTopPixelWeight = 0;
        } else if (lowerPartX == upperPartX) {
            // x-ov� hodnoty se shoduj� -> jsou s P(1) na dan� x-ov� sou�adnici
            // -> sta�� pouze 2 hodnoty (horn� a doln� pixel k sob� pat��c� dvojice)
            leftTopPixelWeight = (upperPartZ - directZ);
            leftBottomPixelWeight = (directZ - lowerPartZ);
            rightTopPixelWeight = 0;
            rightBottomPixelWeight = 0;
        } else if (lowerPartZ == upperPartZ) {
            // z-ov� hodnoty se shoduj� -> jsou s P(1) na dan� y-ov� sou�adnici
            // -> sta�� pouze 2 hodnoty (prav� a lev� pixel, k sob� pat��c� dvojice)
            leftTopPixelWeight = (upperPartX - directX);
            rightTopPixelWeight = (directX - lowerPartX);
            leftBottomPixelWeight = 0;
            rightBottomPixelWeight = 0;
        }

        // v�sledek ze �ty� pixel� pomoc� v�hy, le��c� <minImageColor, maxImageColor>
        double resultFromForPixels = this.bitmapImage.get(lowerPartX, lowerPartZ)[0] * leftTopPixelWeight + this.bitmapImage.get(upperPartX, lowerPartZ)[0] * rightTopPixelWeight + this.bitmapImage.get(lowerPartX, upperPartZ)[0] * leftBottomPixelWeight + this.bitmapImage.get(upperPartX, upperPartZ)[0] * rightBottomPixelWeight;

        //System.out.println(directX + ", " + directZ + " -> " + resultFromForPixels + " | " + leftTopPixelWeight + "," + leftBottomPixelWeight + "," + rightTopPixelWeight + "," + rightBottomPixelWeight);
        //System.out.println(directX+" - "+x+" | "+directZ+" - "+z);
        //System.out.println(directX - (int) directX);
        // ze 4 bod� linern� funkce
        return (float) (this.height * (resultFromForPixels - this.minImageColor) / (this.maxImageColor - this.minImageColor));
    }

    /**
     * Na�te v�echny mapy z adres��e (Terrain.PATH_TO_MAPS)
     *
     * @return
     */
    public static ArrayList<String> getPathToMaps() {
        // m�l by asi b�t Set
        ArrayList<String> paths = new ArrayList<>();

        File folder = new File(PATH_TO_MAPS);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                paths.add(PATH_TO_MAPS + file.getName());
            }
        }

        return paths;
    }

    @Override
    public void fillVertices() {
        float startX = -sizeX / 2;
        float startZ = sizeZ / 2;

        float squareSizeX = sizeX / squareCountX;
        float squareSizeZ = sizeZ / squareCountZ;

        int[] indices = new int[4 * squareCountX * squareCountZ];
        int indicesIndex = 0;
        for (int i = 0; i < squareCountX + 1; i++) {
            for (int j = 0; j < squareCountZ + 1; j++) {
                Vertex v = new Vertex(startX + squareSizeX * i, this.heightFromMap(i, j), startZ - squareSizeZ * j, (float) 0.4, (float) 0.8, (float) 0.3);
                putVertices(v);

                this.map2D.add(i, j, v.getY());

                if ((i < squareCountX) && (j < squareCountZ)) {
                    indices[indicesIndex++] = (i) + ((squareCountX + 1) * j);
                    indices[indicesIndex++] = (i + 1) + ((squareCountX + 1) * j);
                    indices[indicesIndex++] = (i + 1) + ((squareCountX + 1) * (j + 1));
                    indices[indicesIndex++] = (i) + ((squareCountX + 1) * (j + 1));
                }
            }
        }
        setVertexModified(true);
        enableIndicesDraw(indices);
    }
}
