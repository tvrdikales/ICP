/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import java.nio.FloatBuffer;

/**
 *
 * @author spartan
 */
public class Box extends A_DrawableGrob{
    private float xStart, yStart, zStart;
    private float xSize, ySize, zSize;

    public Box(float xStart, float yStart, float zStart, float xSize, float ySize, float zSize, GL2 gl) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.zStart = zStart;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.setVerticesSize(72);
        
    }

    public float getZStart() {
        return zStart;
    }

    public float getZSize() {
        return zSize;
    }

    public float getYStart() {
        return yStart;
    }

    public float getYSize() {
        return ySize;
    }

    public float getXStart() {
        return xStart;
    }

    public float getXSize() {
        return xSize;
    }
    
    public void changeXPosition(float change){
        this.xStart += change;
    }
    
    public void changeYPosition(float change){
        this.yStart += change;
    }
    
    public void changeZPosition(float change){
        this.zStart += change;
    }

    public void setXStart(float xStart) {
        this.xStart = xStart;
    }

    public void setYStart(float yStart) {
        this.yStart = yStart;
    }

    public void setZStart(float zStart) {
        this.zStart = zStart;
    }
    
    
    
    
    @Override
    public void fillVertices(){
        
        //front
        this.putVertices(xStart);
        this.putVertices(yStart);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart);
        
        this.putVertices(xStart);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart);
        
        // back
        this.putVertices(xStart);
        this.putVertices(yStart);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart+zSize);
        
        // left
        this.putVertices(xStart);
        this.putVertices(yStart);
        this.putVertices(zStart);
        
        this.putVertices(xStart);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart);
        
        this.putVertices(xStart);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart);
        this.putVertices(yStart);
        this.putVertices(zStart+zSize);
        
        // right
        this.putVertices(xStart+xSize);
        this.putVertices(yStart);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart);
        this.putVertices(zStart+zSize);
        
        // bottom
        this.putVertices(xStart);
        this.putVertices(yStart);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart);
        this.putVertices(yStart);
        this.putVertices(zStart+zSize);
        
        // top
        this.putVertices(xStart);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart);
        
        this.putVertices(xStart+xSize);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart+zSize);
        
        this.putVertices(xStart);
        this.putVertices(yStart+ySize);
        this.putVertices(zStart+zSize);
        
        
    }
    
    
}
