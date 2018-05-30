/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;

/**
 * Video metadata class.
 */
public final class Metadata {
    private final String absolutePath;
    private final long size;
    private final String formats;
    private final int width;
    private final int height;
    private final long microseconds;
    private final int frames;
    private final double fps;
    
    /**
     * Constructor
     * @param file Video file.
     * @param format Video format.
     * @param w Frame width.
     * @param h Frame height.
     * @param mcs Video length in microseconds.
     * @param f Number of frames.
     * @param fraps Frames per second.
     */
    public Metadata(File file, String format, int w, int h, long mcs, int f ,double fraps) {
        absolutePath = file.getAbsolutePath();
        size = file.length();
        formats = format;
        width = w;
        height = h;
        microseconds = mcs;
        frames = f;
        fps = fraps;
    }
    
    
    /**
     * Static members
     */
    
    /**
     * Convert time in microseconds.
     * @param h Hours.
     * @param m Minutes.
     * @param s Seconds.
     * @param ms Miliseconds.
     * @param mcs Microseconds.
     * @return Time in Microseconds.
     */
    public static long timeToMicroseconds(long h, long m, long s, long ms, long mcs) {
        return h * 3600000000L + m * 60000000L + s * 1000000L + ms * 1000L + mcs;
    }
    
    /**
     * Convert microseconds to hours.
     * @param mcs Microseconds.
     * @return Equivalente in hours.
     */
    public static double microsecondsToHours(long mcs) {
        return (double) mcs / (double) 3600000000L;
    }
    
    /**
     * Convert microseconds to minutes.
     * @param mcs Microseconds.
     * @return Equivalente in minutes.
     */
    public static double microsecondsToMinutes(long mcs) {
        return (double) mcs / (double) 60000000L;
    }
    
    /**
     * Convert microseconds to seconds.
     * @param mcs Microseconds.
     * @return Equivalente in seconds.
     */
    public static double microsecondsToSeconds(long mcs) {
        return (double) mcs / (double) 1000000L;
    }
    
    /**
     * Convert microseconds to miliseconds.
     * @param mcs Microseconds.
     * @return Equivalente in miliseconds.
     */
    public static double microsecondsToMiliseconds(long mcs) {
        return (double) mcs / (double) 1000L;
    }
    
    
    /**
     * Getters
     */
    
    /**
     * Get video absolute path.
     * @return Absolute path.
     */
    public String path() {
        return absolutePath;
    }
    
    /**
     * Get video name without extension.
     * @return Name of the file without extension.
     */
    public String name() {
        int nameBegin = absolutePath.lastIndexOf('/') + 1;
        if (nameBegin == 0) nameBegin = absolutePath.lastIndexOf('\\') + 1;
        
        int nameEnd = absolutePath.lastIndexOf('.');
        if (nameEnd == -1) nameEnd = absolutePath.length();
        
        return absolutePath.substring(nameBegin, nameEnd);
    }
    
    
    /**
     * Get the size in gigabytes.
     * @return Size in gigabytes.
     */
    public double gigabytes() {
        return (double) size / 1073741824.0;
    }
    
    /**
     * Get the size in megabytes.
     * @return Size in megabytes.
     */
    public double megabytes() {
        return (double) size / 1048576.0;
    }
    
    /**
     * Get the size in kilobytes.
     * @return Size in kilobytes.
     */
    public double kilobytes() {
        return (double) size / 1024.0;
    }
    
    /**
     * Get the size in bytes.
     * @return Size in bytes.
     */
    public long bytes() {
        return size;
    }
    
    
    /**
     * Get video formats.
     * @return Video formats.
     */
    public String formats() {
        return formats;
    }
    
    /**
     * Get the frame width.
     * @return Frame width.
     */
    public int width() {
        return width;
    }
    
    /**
     * Get the frame height.
     * @return Frame height.
     */
    public int height() {
        return height;
    }
    
    /**
     * Get the aspect ratio.
     * @return Aspect ratio.
     */
    public double AspecRatio () {
        return (double) width / (double) height;
    }
    
    
    /**
     * Locate the frame on the microseconds time scale.
     * @param frame Number of frame.
     * @return Associated time in microseconds.
     */
    public long microsecondsFromFrame(int frame) {
        double ratio = (double) frame / (double) frames;
        return (long) (ratio * microseconds);
    }
    
    /**
     * Get the frame number in the given microseconds time.
     * @param mcs Time in microseconds.
     * @return Associated time in microseconds.
     */
    public int frameFromMicrosecond(long mcs) {
        double ratio = (double) mcs / (double) microseconds;
        return (int) (ratio *frames);
    }
    
    /**
     * Get the frame number in the given time.
     * @param h Hours.
     * @param m Minutes.
     * @param s Seconds.
     * @param ms Miliseconds.
     * @param mcs Microseconds.
     * @return Associated time in microseconds.
     */
    public int frameFromTime(long h, long m, long s, long ms, long mcs) {
        double ratio = (double) timeToMicroseconds(h, m, s, ms, mcs) / (double) microseconds;
        return (int) (ratio * frames);
    }
    
    /**
     * Get the lenght in hours.
     * @return Lenght in hours.
     */
    public double hours() {
        return microsecondsToHours(microseconds);
    }
    
    /**
     * Get the lenght in minutes.
     * @return Lenght in minutes.
     */
    public double minutes() {
        return microsecondsToMinutes(microseconds);
    }
    
    /**
     * Get the lenght in seconds.
     * @return Lenght in seconds.
     */
    public double seconds() {
        return microsecondsToSeconds(microseconds);
    }
    
    /**
     * Get the lenght in miliseconds.
     * @return Lenght in miliseconds.
     */
    public double miliseconds() {
        return microsecondsToMiliseconds(microseconds);
    }
    
    /**
     * Get the lenght in microseconds.
     * @return Lenght in microseconds.
     */
    public long microseconds() {
        return microseconds;
    }
    
    
    /**
     * Get the total number of frames.
     * @return Total number of frames.
     */
    public int frames() {
        return frames;
    }
    
    /**
     * Get video frame rate.
     * @return Frame rate.
     */
    public double fps() {
        return fps;
    }
}
