/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp.graphics.primitives;

import java.util.HashMap;

/**
 *
 * @author spartan
 */
public class Map2D {
    private final HashMap<Integer, HashMap<Integer,Double>> map;
    

    public Map2D() {
        this.map = new HashMap<>();
    }
    
    public void add(int x, int y, double value){
        if (!this.map.containsKey(x)){
            this.map.put(x, new HashMap<Integer, Double>());
        }
        this.map.get(x).put(y, value);
    }
    
    public double getValue(int x, int y){
        return this.map.get(x).get(y);
    }
    
    public int getXSize(){
        return this.map.keySet().size();
    }
    
    public int getZSize(){
        return this.map.get(0).keySet().size();
    }
    
    
}
