/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp.camera;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tøída asi nepotøebná, možná pøedìlat na abstraktní..
 * @author spartan
 */
public class DriverRotation{

    private double rotation;
    private int numberOfFoundPixels;
    private Point detectedPoint;

    public DriverRotation() {
        this.rotation = 0;
    }

    /**
     * Nastaví všechny potøebné hodnoty pro vypoètení rotace
     * @param numberOfFoundPixels
     * @param xCameraResolution
     * @param yCameraResolution
     * @param detectedPoint 
     */
    public void setValues(int numberOfFoundPixels, int xCameraResolution, int yCameraResolution, Point detectedPoint) {
        this.detectedPoint = detectedPoint;
        this.numberOfFoundPixels = numberOfFoundPixels;
        if (numberOfFoundPixels > 100 && detectedPoint != null) {
            this.rotationFunction((detectedPoint.getX() - (double) xCameraResolution / 2) / ((double) xCameraResolution / 2));
        }
    }
    
    /**
     * Funkce, která mìní rotaci ze vstupu
     * @param baseRotation rotace <-1,1>
     */
    private void rotationFunction(double baseRotation){
        this.rotation = Math.pow(baseRotation,3);
    }

    /**
     * Vrací rotaci <-1,1>
     * @return 
     */
    public double getRotation() {
        return rotation;
    }
    
    

}
