/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp;

/**
 *
 * @author spartan
 */
public class Mathematic {
    
    public static int upperPart(double number) {
        double predictNumber = (int) number + 1;

        //
        if (((double) lowerPart(number)) == number) {
            return lowerPart(number);
        } else {
            return (int) (number + 1);
        }
    }

    public static int lowerPart(double number) {
        return (int) number;
    }
}
