/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Video metadata class.
 */
public class VideoMetadata implements Cloneable {
    public String path;
    public String name;
    public long size;
    public String format;
    public int width;
    public int height;
    public double aspectRatio;
    public long length;
    public int frames;
    public double fps;
    
    /**
     * Empty video metadata before constructor.
     */
    {
        clear();
    }
    
    /**
     * Clear metadata
     */
    public void clear() {
        path = "";
        name = "";
        size = 0;
        format = "";
        width = 0;
        height = 0;
        length = 0;
        frames = 0;
        fps = 0;
    }
    
    /**
     * Clone video metadata.
     * @return Current video metadata clon.
     * @throws CloneNotSupportedException CLone not suppoerted exception.
     */
    @Override
    public VideoMetadata clone() throws CloneNotSupportedException {
        return (VideoMetadata) super.clone();
    }
    
    
    /**
     * Static members
     */
    
    /**
     * Locate the frame on the microseconds time scale.
     * @param frame Number of frame.
     * @param totalFrames Number of total frames.
     * @param totalMicroseconds Number of total microseconds.
     * @return Associated time in microseconds.
     */
    public static long frameToMicroseconds(int frame, int totalFrames, long totalMicroseconds) {
        double ratio = (double) frame / (double) totalFrames;
        return (long) (ratio * totalMicroseconds);
    }
    
    /**
     * Get the frame number in the given microseconds time.
     * @param microseconds Time in microseconds.
     * @param totalMicroseconds Number of total microseconds.
     * @param totalFrames Number of total frames.
     * @return Associated time in microseconds.
     */
    public static int microsecondsToFrame(long microseconds, long totalMicroseconds, int totalFrames) {
        double ratio = (double) microseconds / (double) totalMicroseconds;
        return (int) (ratio * totalFrames);
    }
    
    /**
     * Get the frame number in the given time.
     * @param hours Hours.
     * @param minutes Minutes.
     * @param seconds Seconds.
     * @param miliseconds Miliseconds.
     * @param microseconds Microseconds.
     * @param totalMicroseconds Number of total microseconds.
     * @param totalFrames Number of total frames.
     * @return Associated time in microseconds.
     */
    public static int timeToFrame(long hours, long minutes, long seconds, long miliseconds, long microseconds, long totalMicroseconds, int totalFrames) {
        double ratio = (double) timeToMicroseconds(hours, minutes, seconds, miliseconds, microseconds) / (double) totalMicroseconds;
        return (int) (ratio * totalFrames);
    }
    
    /**
     * Convert time in microseconds.
     * @param hours Hours.
     * @param minutes Minutes.
     * @param seconds Seconds.
     * @param miliseconds Miliseconds.
     * @param microseconds Microseconds.
     * @return Time in Microseconds.
     */
    public static long timeToMicroseconds(long hours, long minutes, long seconds, long miliseconds, long microseconds) {
        return hours * 3600000000L + minutes * 60000000L + seconds * 1000000L + miliseconds * 1000L + microseconds;
    }
    
    /**
     * Convert hours to microseconds.
     * @param hours Hours.
     * @return Equivalente in microseconds.
     */
    public static long hoursToMicroseconds(long hours) {
        return hours * 3600000000L;
    }
    
    /**
     * Convert minutes to microseconds.
     * @param minutes Minutes.
     * @return Equivalente in microseconds.
     */
    public static long minutesToMicroseconds(long minutes) {
        return minutes * 60000000L;
    }
    
    /**
     * Convert seconds to microseconds.
     * @param seconds Seconds.
     * @return Equivalente in microseconds.
     */
    public static long secondsToMicroseconds(long seconds) {
        return seconds * 1000000L;
    }
    
    /**
     * Convert miliseconds to microseconds.
     * @param miliseconds Miliseconds.
     * @return Equivalente in microseconds.
     */
    public static long milisecondsToMicroseconds(long miliseconds) {
        return miliseconds * 1000L;
    }
    
    /**
     * Convert microseconds to hours.
     * @param microseconds Microseconds.
     * @return Equivalente in hours.
     */
    public static long microsecondsToHours(long microseconds) {
        return microseconds / 3600000000L;
    }
    
    /**
     * Convert microseconds to minutes.
     * @param microseconds Microseconds.
     * @return Equivalente in minutes.
     */
    public static long microsecondsToMinutes(long microseconds) {
        return microseconds / 60000000L;
    }
    
    /**
     * Convert microseconds to seconds.
     * @param microseconds Microseconds.
     * @return Equivalente in seconds.
     */
    public static long microsecondsToSeconds(long microseconds) {
        return microseconds / 1000000L;
    }
    
    /**
     * Convert microseconds to miliseconds.
     * @param microseconds Microseconds.
     * @return Equivalente in miliseconds.
     */
    public static long microsecondsToMiliseconds(long microseconds) {
        return microseconds / 1000L;
    }
    
    /**
     * Convert bytes in kilobytes.
     * @param bytes Bytes.
     * @return Equivalent in kilobytes.
     */
    public static long bytesToKilobytes(long bytes) {
        return bytes >> 10;
    }
    
    /**
     * Convert bytes in megabytes
     * @param bytes Bytes
     * @return Equivalent in megabytes
     */
    public static long bytesToMegabytes(long bytes) {
        return bytes >> 20;
    }
    
    /**
     * Convert bytes in gigabytes
     * @param bytes Bytes
     * @return Equivalent in gigabytes
     */
    public static long bytesToGigabytes(long bytes) {
        return bytes >> 30;
    }
    
    /**
     * Getters
     */
    
    /**
     * Get the lenght in hours.
     * @return Lenght in hours.
     */
    public long getHours() {
        return microsecondsToHours(length);
    }
    
    /**
     * Get the lenght in minutes.
     * @return Lenght in minutes.
     */
    public long getMinutes() {
        return microsecondsToMinutes(length);
    }
    
    /**
     * Get the lenght in seconds.
     * @return Lenght in seconds.
     */
    public long getSeconds() {
        return microsecondsToSeconds(length);
    }
    
    /**
     * Get the lenght in miliseconds.
     * @return Lenght in miliseconds.
     */
    public long getMiliseconds() {
        return microsecondsToMiliseconds(length);
    }
    
    /**
     * Get the lenght in microseconds.
     * @return Lenght in microseconds.
     */
    public long getMicroseconds() {
        return length;
    }
    
    /**
     * Get the size in gigabytes.
     * @return Size in gigabytes.
     */
    public long getGigabytes() {
        return bytesToGigabytes(size);
    }
    
    /**
     * Get the size in megabytes.
     * @return Size in megabytes.
     */
    public long getMegabytes() {
        return bytesToMegabytes(size);
    }
    
    /**
     * Get the size in kilobytes.
     * @return Size in kilobytes.
     */
    public long getKilobytes() {
        return bytesToKilobytes(size);
    }
    
    /**
     * Get the size in bytes.
     * @return Size in bytes.
     */
    public long getBytes() {
        return size;
    }
}
