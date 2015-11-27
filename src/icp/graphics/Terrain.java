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
 * Objekt reprezentující terén po kterém se hráè pohybuje
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
     * Konstruktor vytváøející ètvercovou plochu složenou z jednotlivých
     * ètvereèkù
     *
     * Pokud je poèet vytvoøených ètvercù výraznì nižší než poèet pixelù
     * obrázku, z kterého je terén vytváøen, jsou následnì do výsledku poèítány
     * jen 2 váhovì vyhodnocené nejbližší pixely, resp. ve 2 rozmìrech 4
     *
     * @param sizeX Velikost celého terénu (x)
     * @param sizeZ Velikost celého terénu (z)
     * @param squareCountX Poèet ètvercù na ose x
     * @param squareCountZ poèet ètvercù na ose z
     * @param height maximální výška na mapì, minimální je VŽDY 0
     * @param filePath cesta k mapì
     */
    public Terrain(float sizeX, float sizeZ, int squareCountX, int squareCountZ, float height, String filePath) {
        // 4 body na quad * poèet quadù celkem
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

        // pokud jsou stejné, došlo by ve funkci heightFromMap() k dìlení nulou, 
        // zároveò by platilo image.get(i,j) = minImageColor = maxImageColor, pro i,j e Image
        // Za tohoto pøedpokladu, je možné libovolnì zvýšit maxImageColor
        // rovnìž možno zmìnit na if ve funkci s výsledkem výšku = 0, zøejmì i rychlejší
        if (minImageColor1 == maxImageColor1) {
            this.maxImageColor = maxImageColor1 + 1;
        } else {
            this.maxImageColor = maxImageColor1;
        }
        this.minImageColor = minImageColor1;

        // pøevodový pomìr na pixely z mapy, myšlenkovì pøedìláno na body, proto bitmapImage.rows-1
        this.squareToPixelX = (double) (bitmapImage.rows() - 1) / (double) (squareCountX);
        this.squareToPixelZ = (double) (bitmapImage.cols() - 1) / (double) (squareCountZ);
        this.map2D = new Map2D();
    }

    /**
     * Nefunguje, velká odchylka, zvláštì pokud poèet pixelù v mat je výraznì
     * vyšší než rozlišení mapy
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

        // pøesnì vypoèítané umístìní na pixelu v reálných èíslech
        int xSize = this.map2D.getXSize();
        int zSize = this.map2D.getZSize();
        
        double directX = ((x + sizeX / 2) / sizeX) * (xSize - 1);
        double directZ = ((-z + sizeZ / 2) / sizeZ) * (zSize - 1);

        //System.out.println(directX + " : " + directZ);
        int lowerPartX = Mathematic.lowerPart(directX);
        int upperPartX = lowerPartX + 1;
        int lowerPartZ = Mathematic.lowerPart(directZ);
        int upperPartZ = lowerPartZ + 1;

        // špatné pøevedení èísla mùže o velmi malou èást pøesáhnout max. hodnotu
        if (lowerPartX > xSize - 1) {
            lowerPartX = xSize - 1;
        }

        if (lowerPartZ > zSize - 1) {
            lowerPartZ = zSize - 1;
        }
        // poslední èísla jsou vždy nastaveny výše a musí být zadány na 
        //max. možnou hodnotu, zároveò podléhají stejnému vlivu jako výše uvedené
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

        // pokud se horní a dolní pixel pro výpoèet shodují, výše uvedené výpoèty
        // jsou nepoužitelné (jejich sum = 0)
        if ((lowerPartX == upperPartX) && (lowerPartZ == upperPartZ)) {
            // pokud se shodují všechny, je lhostejné, jak se rozdìlí váhy
            // navíc se zøejmì jedná o pixel image[maxX, maxY]
            leftBottomPixelWeight = 1;
            rightBottomPixelWeight = 0;
            leftTopPixelWeight = 0;
            rightTopPixelWeight = 0;
        } else if (lowerPartX == upperPartX) {
            // x-ové hodnoty se shodují -> jsou s P(1) na dané x-ové souøadnici
            // -> staèí pouze 2 hodnoty (horní a dolní pixel k sobì patøící dvojice)
            leftTopPixelWeight = (upperPartZ - directZ);
            leftBottomPixelWeight = (directZ - lowerPartZ);
            rightTopPixelWeight = 0;
            rightBottomPixelWeight = 0;
        } else if (lowerPartZ == upperPartZ) {
            // z-ové hodnoty se shodují -> jsou s P(1) na dané y-ové souøadnici
            // -> staèí pouze 2 hodnoty (pravý a levý pixel, k sobì patøící dvojice)
            leftTopPixelWeight = (upperPartX - directX);
            rightTopPixelWeight = (directX - lowerPartX);
            leftBottomPixelWeight = 0;
            rightBottomPixelWeight = 0;
        }

        // výsledek ze ètyø pixelù pomocí váhy, ležící <minImageColor, maxImageColor>
        double resultFromForPixels = this.map2D.getValue(lowerPartX, lowerPartZ) * leftTopPixelWeight + this.map2D.getValue(upperPartX, lowerPartZ) * rightTopPixelWeight + this.map2D.getValue(lowerPartX, upperPartZ) * leftBottomPixelWeight + this.map2D.getValue(upperPartX, upperPartZ) * rightBottomPixelWeight;

        //System.out.println(directX + ", " + directZ + " -> " + resultFromForPixels + " | " + leftTopPixelWeight + "," + leftBottomPixelWeight + "," + rightTopPixelWeight + "," + rightBottomPixelWeight);
        //System.out.println(directX+" - "+x+" | "+directZ+" - "+z);
        //System.out.println(directX - (int) directX);
        // ze 4 bodù linerní funkce
        return (float)resultFromForPixels;
    }

    private float heightFromMap(double x, double z) {
        //System.out.println(200*squareToPixelX+" : "+200*squareToPixelZ+" | "+bitmapImage.rows()+" : "+bitmapImage.cols());
        //return (float) (((bitmapImage.get((int) (squareToPixelX * x), (int) (squareToPixelZ * (z)))[0]) - minImageColor) / (maxImageColor - minImageColor));
        //System.out.println(squareToPixelX+" vs. "+squareToPixelZ);

        // pøesnì vypoèítané umístìní na pixelu v reálných èíslech
        double directX = squareToPixelX * x;
        double directZ = squareToPixelZ * z;

        //System.out.println(directX + " : " + directZ);
        int lowerPartX = Mathematic.lowerPart(directX);
        int upperPartX = lowerPartX + 1;
        int lowerPartZ = Mathematic.lowerPart(directZ);
        int upperPartZ = lowerPartZ + 1;

        // špatné pøevedení èísla mùže o velmi malou èást pøesáhnout max. hodnotu
        if (lowerPartX > bitmapImage.rows() - 1) {
            lowerPartX = bitmapImage.rows() - 1;
        }

        if (lowerPartZ > bitmapImage.cols() - 1) {
            lowerPartZ = bitmapImage.cols() - 1;
        }
        // poslední èísla jsou vždy nastaveny výše a musí být zadány na 
        //max. možnou hodnotu, zároveò podléhají stejnému vlivu jako výše uvedené
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

        // pokud se horní a dolní pixel pro výpoèet shodují, výše uvedené výpoèty
        // jsou nepoužitelné (jejich sum = 0)
        if ((lowerPartX == upperPartX) && (lowerPartZ == upperPartZ)) {
            // pokud se shodují všechny, je lhostejné, jak se rozdìlí váhy
            // navíc se zøejmì jedná o pixel image[maxX, maxY]
            leftBottomPixelWeight = 1;
            rightBottomPixelWeight = 0;
            leftTopPixelWeight = 0;
            rightTopPixelWeight = 0;
        } else if (lowerPartX == upperPartX) {
            // x-ové hodnoty se shodují -> jsou s P(1) na dané x-ové souøadnici
            // -> staèí pouze 2 hodnoty (horní a dolní pixel k sobì patøící dvojice)
            leftTopPixelWeight = (upperPartZ - directZ);
            leftBottomPixelWeight = (directZ - lowerPartZ);
            rightTopPixelWeight = 0;
            rightBottomPixelWeight = 0;
        } else if (lowerPartZ == upperPartZ) {
            // z-ové hodnoty se shodují -> jsou s P(1) na dané y-ové souøadnici
            // -> staèí pouze 2 hodnoty (pravý a levý pixel, k sobì patøící dvojice)
            leftTopPixelWeight = (upperPartX - directX);
            rightTopPixelWeight = (directX - lowerPartX);
            leftBottomPixelWeight = 0;
            rightBottomPixelWeight = 0;
        }

        // výsledek ze ètyø pixelù pomocí váhy, ležící <minImageColor, maxImageColor>
        double resultFromForPixels = this.bitmapImage.get(lowerPartX, lowerPartZ)[0] * leftTopPixelWeight + this.bitmapImage.get(upperPartX, lowerPartZ)[0] * rightTopPixelWeight + this.bitmapImage.get(lowerPartX, upperPartZ)[0] * leftBottomPixelWeight + this.bitmapImage.get(upperPartX, upperPartZ)[0] * rightBottomPixelWeight;

        //System.out.println(directX + ", " + directZ + " -> " + resultFromForPixels + " | " + leftTopPixelWeight + "," + leftBottomPixelWeight + "," + rightTopPixelWeight + "," + rightBottomPixelWeight);
        //System.out.println(directX+" - "+x+" | "+directZ+" - "+z);
        //System.out.println(directX - (int) directX);
        // ze 4 bodù linerní funkce
        return (float) (this.height * (resultFromForPixels - this.minImageColor) / (this.maxImageColor - this.minImageColor));
    }

    /**
     * Naète všechny mapy z adresáøe (Terrain.PATH_TO_MAPS)
     *
     * @return
     */
    public static ArrayList<String> getPathToMaps() {
        // mìl by asi být Set
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
