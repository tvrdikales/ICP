/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp.camera;

import com.sun.imageio.plugins.common.I18N;
import icp.Mathematic;
import static java.awt.Color.gray;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author spartan
 */
public class Camera extends Thread {

    VideoCapture videoCapture;
    Mat imageInCamera;
    ShowFrame showFrame = null;
    int x;
    private int frameCount;
    private Long lastTimeMillis = null;
    Point pointDetection;
    private final int xResolution, yResolution;
    private boolean showInFrame;
    private int foundPixels;
    private final DriverRotation sberneVlakno;
    private int necoReknu;

    /**
     *
     * @param xResolution Rozlišení kamery v ose X
     * @param yResolution Rozlišení kamery v ose Y
     * @param showInFrame Má se zobrazit kamera a hledaný bod b novém oknì?
     * (výpoèetnì nároèné)
     *
     */
    public Camera(int xResolution, int yResolution, boolean showInFrame, DriverRotation sberneVlakno) {
        this.x = 0;
        this.pointDetection = null;
        System.loadLibrary("opencv_java249");
        imageInCamera = new Mat();
        this.xResolution = xResolution;
        this.yResolution = yResolution;
        this.showInFrame = showInFrame;
        this.foundPixels = 0;
        this.sberneVlakno = sberneVlakno;
        try {
            this.videoCapture = new VideoCapture(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public double getFrameRation() {
        if (lastTimeMillis == null) {
            return 0;
        } else {
            return (double) 1000 / (double) (System.currentTimeMillis() - lastTimeMillis);
        }
    }

    private void showCameraAndCross() {
        if (this.showFrame == null) {
            this.showFrame = new ShowFrame(200, 150);
        }
        if (this.pointDetection == null) {
            this.showFrame.setCross(null, null);
        } else {
            this.showFrame.setCross((int) pointDetection.getX(), (int) pointDetection.getY());
        }
        BufferedImage buff = matToBufferedImage(imageInCamera);
        this.showFrame.setImage(buff);
        this.showFrame.repaint();
    }
    
    

    @Override
    public void run() {
        if (videoCapture.open(0)) {
            boolean wset = videoCapture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, this.xResolution);
            boolean hset = videoCapture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, this.yResolution);
            while (true) {
                //x++;
                //System.out.println(x);
                videoCapture.read(imageInCamera);
                //System.out.println("kanály: "+imageInCamera.channels());
                if (!imageInCamera.empty()) {
                    //Imgproc.cvtColor(imageInCamera, imageInCamera, Imgproc.COLOR_RGB2GRAY, 0); // transformace do šedi
                    
                    this.searchColor(E_Color.YELLOW);
                    if (this.showInFrame == true) {
                        this.showCameraAndCross();
                    }
                    
                    

                    //break;
                } else {
                    System.out.println("je to prázdný!");
                }
                //try {
                //System.out.println(x);
                //Thread.sleep(200);
                //} catch (InterruptedException ex) {
                //Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
                //}
                this.frameCount++;
                //System.out.println("frame ration : " + this.getFrameRation() + " | " + pointDetection.getX() + " : " + pointDetection.getY());
                this.lastTimeMillis = System.currentTimeMillis();
            }
        } else {
            System.out.println("nejde");
        }
    }

    private void searchColor(E_Color color) {
        int last10 = 1;
        int xAverage = 0;
        long xCount = 0;
        long xSum = 0;
        long ySum = 0;
        int yAverage = 0;
        long yCount = 0;
        ArrayList<Point> pointsDetected = new ArrayList<>();
        for (int row = 0; row < this.imageInCamera.rows(); row++) {
            for (int col = 0; col < this.imageInCamera.cols(); col++) {
                double[] colors = this.imageInCamera.get(row, col);
                if (colors.length == 3) {
                    double red = colors[2];
                    double green = colors[1];
                    double blue = colors[0];

                    switch (color) {
                        case BLUE:
                            if ((blue > 1.5 * green) && (blue > 1.5 * red)) {
                                pointsDetected.add(new Point(col, row));
                            }
                            break;
                        case RED:
                            if ((red > 2 * green) && (red > 2 * blue)) {
                                pointsDetected.add(new Point(col, row));
                            }
                            break;

                        case YELLOW:
                            if (red > 1.5 * blue && green > 1.5 * blue && red > 110 && green > 110) {
                                pointsDetected.add(new Point(col, row));
                                this.imageInCamera.put(row, col, new double[]{0, 0, 0});
                            }
                            break;
                    }
                }
            }
        }

        // lepší možná udìlat i filtr ètvercové sítì výše v kódu
        //System.out.println(pointsDetected.size() + " x");
        this.foundPixels = pointsDetected.size();
        
        
        
        pointsDetected = this.makeClusterDistance(pointsDetected, 0.75, 4);
        Point average = this.getAverage(pointsDetected);
        //System.out.println(pointsDetected.size() + " x");
        //System.out.println("------");

        if (average != null) {
            if (this.pointDetection == null) {
                this.pointDetection = new Point();
            }
            this.pointDetection.setLocation((int) average.getX(), (int) average.getY());
            this.sberneVlakno.setValues(foundPixels, this.imageInCamera.cols(), this.imageInCamera.rows(), pointDetection);
        }
    }

    private Point getAverage(ArrayList<Point> points) {
        if (points.size() >= 1) {
            double xSum = 0, ySum = 0;
            for (Point point : points) {
                xSum += point.getX();
                ySum += point.getY();
            }
            double xAvg = xSum / points.size();
            double yAvg = ySum / points.size();
            return new Point((int) xAvg, (int) yAvg);
        } else {
            return null;
        }
    }

    /**
     * Pomocí nìkolika krokù postupnì odebírá nejvzdálenìjší body od
     * prùmìru
     *
     * @param points
     * @param reduceParameter vyjádøení, kolik nejbližších bodù od
     * centroidu bude v novém shluku Zadává se parametr x e (0,1>, tj. x = 1
     * -> není vynechán žádný bod. Pokud x > 1, x = 1
     * @param numberOfSteps Poèet krokù, kdy se odebírají body,
     * nespl??ující podmínku
     * @return
     */
    private ArrayList<Point> makeClusterDistance(ArrayList<Point> points, double reduceParameter, int numberOfSteps) {
        for (int i = 0; i < numberOfSteps; i++) {
            points = makeClusterDistanceOneStep(points, reduceParameter);
            //System.out.println(points.size());
        }
        return points;
    }

    /**
     * Základní prvek shlukové analýzy, oøeže pøíliš vzdálené body
     *
     * @param points
     * @param reduceParameter vyjádøení, kolik nejbližších bodù od
     * centroidu bude v novém shluku Zadává se parametr x e (0,1>, tj. x = 1
     * -> není vynechán žádný bod = nejvzdálenìjší bod od prùmìru
     */
    private ArrayList<Point> makeClusterDistanceOneStep(ArrayList<Point> points, double reduceParameter) {
        if (points == null || points.isEmpty()) {
            return new ArrayList<>();
        }
        if (reduceParameter > 1) {
            reduceParameter = 1;
        }
        // kolikátý prvek je hranicí vzdáleností pøi propuštìní bodù do shluku
        // -1 kvùli indexùm
        int endIndex = Mathematic.lowerPart(reduceParameter * points.size());

        // vypoètení prùmìru
        double xSum = 0, ySum = 0;
        for (Point point : points) {
            xSum += point.getX();
            ySum += point.getY();
        }
        double xAvg = xSum / points.size();
        double yAvg = ySum / points.size();

        // vypoèteme a seøadíme vzdálenosti všech bodù
        ArrayList<Double> pointsDistanceFromAverage = new ArrayList<>();
        for (Point point : points) {
            pointsDistanceFromAverage.add(point.distance(xAvg, yAvg));
        }

        Collections.sort(pointsDistanceFromAverage);

        //System.out.println("max. Distance : "+maxDistance);
        ArrayList<Point> pointsToReturn = new ArrayList<>();
        double maxDistanceToReturn = pointsDistanceFromAverage.get(endIndex);

        for (Point point : points) {
            // body, které nepøesahují max. vzdálenost budou vráceny
            if (point.distance(xAvg, yAvg) < maxDistanceToReturn) {
                pointsToReturn.add(point);
            }
        }

        return pointsToReturn;

    }

    /**
     * Pøevede matici dle poètu kanálù do Obrázku (externí funkce)
     *
     * @param bgr Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_INT_RGB or TYPE_BYTE_GRAY
     */
    public static BufferedImage matToBufferedImage(Mat bgr) {
        int width = bgr.width();
        int height = bgr.height();
        BufferedImage image;
        WritableRaster raster;

        if (bgr.channels() == 1) {
            //System.out.println("nejsou tam barvy");
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            raster = image.getRaster();

            byte[] px = new byte[1];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bgr.get(y, x, px);
                    raster.setSample(x, y, 0, px[0]);
                }
            }
        } else {
            //System.out.println("jsou tam barvy");
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            raster = image.getRaster();

            byte[] px = new byte[3];
            int[] rgb = new int[3];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bgr.get(y, x, px);
                    rgb[0] = px[2];
                    rgb[1] = px[1];
                    rgb[2] = px[0];
                    raster.setPixel(x, y, rgb);
                }
            }
        }

        return image;
    }

    public int getFoundPixels() {
        return foundPixels;
    }

    public Point getPointDetection() {
        return pointDetection;
    }

    public int getxResolution() {
        return xResolution;
    }

    public int getyResolution() {
        return yResolution;
    }

}
