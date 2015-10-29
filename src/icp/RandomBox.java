/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp;

import com.jogamp.opengl.GL2;

/**
 *
 * @author spartan
 */
public class RandomBox extends Box{
    
    private double specialPosition;
    private final float xStartPosition;
    private final float yStartPosition;

    public RandomBox(float xStart, float yStart, float zStart, float xSize, float ySize, float zSize, GL2 gl) {
        super(xStart, yStart, zStart, xSize, ySize, zSize, gl);
        this.xStartPosition = xStart;
        this.yStartPosition = yStart;
        this.specialPosition = 0;
    }

    @Override
    public void draw(GL2 gl) {
        
        specialPosition += 0.03;
        
        this.setXStart(xStartPosition+(float)Math.sin(specialPosition));
        this.setYStart(yStartPosition+(float)Math.sin(specialPosition));
        
        this.setVertexModified(true);
        
        super.draw(gl); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
