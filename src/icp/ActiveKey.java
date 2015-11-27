/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp;

import com.jogamp.newt.event.KeyEvent;
import java.util.HashMap;

/**
 * Reprezentuje aktivní klávesy
 * @author spartan
 */
public class ActiveKey {
    private final HashMap<Integer, Boolean> hashMap = new HashMap<>();
    
    
    public void setActive(int key){
        if (!this.hashMap.containsKey(key)){
            this.hashMap.put(key, Boolean.TRUE);
        }
    }
    
    public void setDeactive(int key){
        if (this.hashMap.containsKey(key)){
            this.hashMap.remove(key);
        }
    }
    
    public boolean isActive(int keyCode){
        return this.hashMap.containsKey(keyCode);
    }
}
