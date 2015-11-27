package icp.graphics.logic;

import icp.graphics.Box;
import com.jogamp.opengl.GL2;
import icp.ActiveKey;
import icp.graphics.Terrain;
import java.awt.event.KeyEvent;
import javafx.geometry.Point3D;
import jogamp.graph.font.typecast.ot.table.GlyfDescript;

/**
 *
 * @author spartan
 */
public class Tank extends Box implements I_Logic {

    private final Terrain terrain;
    private Point3D moveVector;
    private float actualSpeed;
    private float acceleration;
    private float maximalSpeed;
    private double yRotation;
    private ActiveKey activeKey;
    private Boolean yLeftRotation;

    public Tank(float xStart, float yStart, float zStart, float xSize, float ySize, float zSize, GL2 gl, Terrain terrain, float acceleration, float maximalSpeed, double yRotation, ActiveKey activeKey) {
        super(xStart, yStart, zStart, xSize, ySize, zSize, gl);
        this.terrain = terrain;
        this.moveVector = new Point3D(0, 0, 0);
        this.actualSpeed = (float) 0;
        this.maximalSpeed = maximalSpeed;
        this.acceleration = acceleration;
        this.yRotation = yRotation;
        this.activeKey = activeKey;

        this.setRed((float) 0.7);
        this.setBlue((float) 0.4);
        this.setGreen((float) 0.7);
    }

    public void turnLeft() {
        this.yLeftRotation = true;
    }

    public void turnRight() {
        this.yLeftRotation = false;
    }

    public void turnUp() {
        this.yLeftRotation = null;
    }

    public void turnDown() {
        this.yLeftRotation = null;
    }

    /**
     * Smìr pohybu, nezáleží na velikost, jen pokud je [0,0,0] tak se nemá dále
     * pohybovat
     *
     * @param moveVector
     */
    public void setMoveVector(Point3D moveVector) {
        this.moveVector = moveVector;
    }

    @Override
    public void draw(GL2 gl) {
        this.setVertexModified(true);
        super.draw(gl); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param point Výchozí bod
     * @param moveVector Smìrový vektor
     * @param actualSpeed Aktuální rychlost
     * @return
     */
    private Point3D predictPoint(Point3D point, double yRotation, float actualSpeed) {
        double xMove = Math.sin(yRotation / 180 * Math.PI);
        double zMove = Math.cos(yRotation / 180 * Math.PI);
        double x = point.getX() + xMove * (actualSpeed);
        double z = point.getZ() + zMove * (actualSpeed);
        return new Point3D(x, this.terrain.heightByNewPos(x, z), z);
    }

    @Override
    public void evaluateNextStep(int millis) {
        this.actualSpeed += (this.maximalSpeed - this.actualSpeed) * this.acceleration;

        // nové zatoèení
        if (this.yLeftRotation != null) {
            if (this.yLeftRotation == true) {
                this.yRotation++;
            } else {
                this.yRotation--;
            }
        }

        Point3D predictStartPoint = predictPoint(new Point3D(this.getXStart(), this.getYStart(), this.getZStart()), this.yRotation, actualSpeed);
        Point3D predictEndPoint = predictPoint(new Point3D(predictStartPoint.getX() + this.getXSize() * Math.sin(yRotation * Math.PI / 180), predictStartPoint.getY(), predictStartPoint.getZ() + this.getXSize() * Math.cos(yRotation * Math.PI / 180)), this.yRotation, 0);

        double atan = Math.atan((predictEndPoint.getY() - predictStartPoint.getY()) / (Math.sqrt(Math.pow(predictEndPoint.getX() - predictStartPoint.getX(), 2) + Math.pow(predictEndPoint.getZ() - predictStartPoint.getZ(), 2))));

        double heighRotation = atan * 180 / Math.PI;

        this.setXStart((float) predictStartPoint.getX());
        this.setYStart((float) predictStartPoint.getY());
        this.setZStart((float) predictStartPoint.getZ());

        System.out.println(predictStartPoint.getX()+", "+predictStartPoint.getY()+", "+predictStartPoint.getZ()+" -> "+predictEndPoint.getX()+", "+predictEndPoint.getY()+", "+predictEndPoint.getZ()+" -> "+heighRotation);
        
        this.setRotateByPoint(predictStartPoint);
        this.setRotateAngle(new Point3D(-heighRotation, this.yRotation, 0));

//        float y1Predict = this.terrain.heightByNewPos(xPredict, zPredict);
//        float y2Predict = this.terrain.heightByNewPos(xPredict, zPredict + this.getZSize());
//        float y3Predict = this.terrain.heightByNewPos(xPredict + this.getXSize(), zPredict);
//        float y4Predict = this.terrain.heightByNewPos(xPredict + super.getXSize(), zPredict + this.getZSize());
        // pomocné výpisy, souøadnice, vzdálenost z poslední frame
        //System.out.println("jedu na: " + this.getXStart() + ", " + this.getZStart() + " -> " + yPredictMax + " sklada se z :" + y1Predict + ", " + y2Predict + ", " + y3Predict + ", " + y4Predict);
        //System.out.println(startPoint.distance(new Point3D(this.getXStart(), this.getYStart(), this.getZStart())));
    }

    /**
     * Stará funkce, tank jede bez rotací
     *
     * @deprecated
     */
    private void step() {
        this.actualSpeed += (this.maximalSpeed - this.actualSpeed) * this.acceleration;

        this.moveVector = this.moveVector.normalize();
        // odhad, kde bude v následujícím snímku
        Point3D startPoint = new Point3D(this.getXStart(), this.getYStart(), this.getZStart());
        float xPredict = this.getXStart() + (float) this.moveVector.getX() * (this.actualSpeed);
        float zPredict = this.getZStart() + (float) this.moveVector.getZ() * (this.actualSpeed);

        float yActual = this.getYStart();

        float y1Predict = this.terrain.heightByNewPos(xPredict, zPredict);
        float y2Predict = this.terrain.heightByNewPos(xPredict, zPredict + this.getZSize());
        float y3Predict = this.terrain.heightByNewPos(xPredict + this.getXSize(), zPredict);
        float y4Predict = this.terrain.heightByNewPos(xPredict + super.getXSize(), zPredict + this.getZSize());

        float yPredictMax = y1Predict;
        if (y2Predict > yPredictMax) {
            yPredictMax = y2Predict;
        }
        if (y3Predict > yPredictMax) {
            yPredictMax = y3Predict;
        }
        if (y4Predict > yPredictMax) {
            yPredictMax = y4Predict;
        }

        float yPredict = yPredictMax;
        float yDifference = yPredict - yActual;

        // výpoèet rychlosti
        Point3D realMoveVector = new Point3D(this.moveVector.getX() * this.actualSpeed, yDifference, this.moveVector.getZ() * this.actualSpeed);
        realMoveVector = realMoveVector.normalize();
        this.changeXPosition((float) realMoveVector.getX() * this.actualSpeed);
        this.changeZPosition((float) realMoveVector.getZ() * this.actualSpeed);

        y1Predict = this.terrain.heightByNewPos(this.getXStart(), this.getZStart());
        y2Predict = this.terrain.heightByNewPos(this.getXStart(), this.getZStart() + this.getZSize());
        y3Predict = this.terrain.heightByNewPos(this.getXStart() + this.getXSize(), this.getZStart());
        y4Predict = this.terrain.heightByNewPos(this.getXStart() + super.getXSize(), this.getZStart() + this.getZSize());

        yPredictMax = y1Predict;
        if (y2Predict > yPredictMax) {
            yPredictMax = y2Predict;
        }
        if (y3Predict > yPredictMax) {
            yPredictMax = y3Predict;
        }
        if (y4Predict > yPredictMax) {
            yPredictMax = y4Predict;
        }

        this.setYStart(yPredictMax);
    }
}
