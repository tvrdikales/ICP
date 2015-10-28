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
    private int xStart, yStart, zStart;
    private int xSize, ySize, zSize;

    public Box(int xStart, int yStart, int zStart, int xSize, int ySize, int zSize, GL2 gl) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.zStart = zStart;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.setVerticesSize(24);
        this.fillVertices();
        
    }
    
    private void fillVertices(){
        
        
        
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
        
        
        
        
    }
    
    
}
