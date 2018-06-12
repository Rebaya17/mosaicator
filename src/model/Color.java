/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Color class.
 */
public class Color {
    private int space;
    
    public static final int RGB = 0;
    public static final int CIELAB = 1;
    
    public float x;
    public float y;
    public float z;
    
    /**
     * Dafault constructor
     */
    public Color() {
        x = 0.0F;
        y = 0.0F;
        z = 0.0F;
        space = RGB;
    }
    
    /**
     * Copy constructor.
     * @param c Source color.
     */
    public Color(Color c) {
        x = c.x;
        y = c.y;
        z = c.z;
        space = c.space;
    }
    
    /**
     * Color constructor.
     * @param r Red.
     * @param g Green.
     * @param b Blue.
     * @param model Color space model.
     */
    public Color(float r, float g, float b, int model) {
        x = r;
        y = g;
        z = b;
        space = model;
    }
    
    /**
     * Convert RGB to CIELAB color space.
     * @param r Red.
     * @param g Green.
     * @param b Blue.
     * @return 
     */
    public static Color toCIELAB(float r, float g, float b) {
        float u = r / 255.0F;
        float v = g / 255.0F;
        float s = b / 255.0F;

        u = (float) (u > 0.04045F ? Math.pow((u + 0.055F) / 1.055F, 2.4F) : u / 12.92F);
        v = (float) (v > 0.04045F ? Math.pow((v + 0.055F) / 1.055F, 2.4F) : v / 12.92F);
        s = (float) (s > 0.04045F ? Math.pow((s + 0.055F) / 1.055F, 2.4F) : s / 12.92F);

        float x = (u * 0.4124F + v * 0.3576F + s * 0.1805F) / 0.95047F;
        float y = (u * 0.2126F + v * 0.7152F + s * 0.0722F) / 1.00000F;
        float z = (u * 0.0193F + v * 0.1192F + s * 0.9505F) / 1.08883F;

        x = (float) (x > 0.008856F ? Math.pow(x, 1.0F / 3.0F) : (7.787F * x) + 16.0F / 116.0F);
        y = (float) (y > 0.008856F ? Math.pow(y, 1.0F / 3.0F) : (7.787F * y) + 16.0F / 116.0F);
        z = (float) (z > 0.008856F ? Math.pow(z, 1.0F / 3.0F) : (7.787F * z) + 16.0F / 116.0F);
        
        return new Color(x, y, z, CIELAB);
    }
    
    /**
     * Calculate the color difference between this and another color.
     * @param c Another color.
     * @return Color deifference.
     */
    public float difference(Color c) {
        float diff = Float.POSITIVE_INFINITY;
        if (space != c.space) return diff;
        
        switch(space) {
            /* Manhattan distance */
            case RGB:
                diff = Math.abs(x - c.x) + Math.abs(y - c.y) + Math.abs(z - c.z);
                break;
            
            /* DELTA E distance */
            case CIELAB:
                float dl = x - c.x;
                float da = y - c.y;
                float db = z - c.z;
                float c1 = (float) Math.sqrt(y * y + z * z);
                float c2 = (float) Math.sqrt(c.y * c.y + c.z * c.z);
                float deltaC = c1 - c2;
                float deltaH = da * da + db * db - deltaC * deltaC;
                deltaH = (deltaH < 0 ? 0 : (float) Math.sqrt(deltaH));
                float sc = 1.0F + 0.045F * c1;
                float sh = 1.0F + 0.015F * c1;
                float deltaLKlsl = dl / (1.0F);
                float deltaCkcsc = deltaC / (sc);
                float deltaHkhsh = deltaH / (sh);
                float i = deltaLKlsl * deltaLKlsl + deltaCkcsc * deltaCkcsc + deltaHkhsh * deltaHkhsh;
                diff = (i < 0.0F ? 0.0F : (int) Math.sqrt(i));
                break;
            
            /* Euclidean distance */
            default:
                float dx = x - c.x;
                float dy = y - c.y;
                float dz = z - c.z;
                diff = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                break;
        }
        
        return diff;
    }
}
