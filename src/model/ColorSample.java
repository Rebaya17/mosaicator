/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Sampling metadata class.
 */
public class ColorSample {
    private final int id;
    private final int[] sample;
    
    /**
     * Color samples constructor.
     * @param n Sampling identification.
     * @param samples Array of samples sorted from left to right and up to down.
     */
    public ColorSample(int n, int[] samples) {
        id = n;
        sample = samples.clone();
    }
    
    /**
     * Calculate the manhattan distance between two rgb colors.
     * @param RGBp Color p.
     * @param RGBq Color q.
     * @return Distance between two rgb colors.
     */
    public static int manhattan(int RGBp, int RGBq) {
        int dR = ((RGBp >>  16) & 0xFF) - ((RGBq >>  16) & 0xFF);
        int dG = ((RGBp >>   8) & 0xFF) - ((RGBq >>   8) & 0xFF);
        int dB = ( RGBp         & 0xFF) - ( RGBq         & 0xFF);
        return Math.abs(dR) + Math.abs(dG) + Math.abs(dB);
    }
    
    /**
     * Get the sampling identification.
     * @return Sampling identification.
     */
    public int id() {
        return id;
    }
    
    /**
     * Calculate the sum of each distance between current sample and another.
     * @param other Othe data set.
     * @return Sum of each distance between current data and other data set.
     */
    public int distancesSum(int[] other) {
        int distance = 0;
        for (int i = 0; i < sample.length; i++)
            distance += manhattan(sample[i], other[i]);
        
        return distance;
    }
}
