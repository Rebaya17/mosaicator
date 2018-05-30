/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mosaic class.
 */
public class Mosaic {
    private int frame;
    private FFmpegFrameGrabber grabber;
    private VideoMetadata videoMetadata;
    private final Java2DFrameConverter toBufferedImage;
    
    /**
     * Mosaic Constructor.
     */
    public Mosaic() {
        grabber = null;
        videoMetadata = null;
        toBufferedImage = new Java2DFrameConverter();
    }
    
    /**
     * Open video from absolute path.
     * @param video Video file.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception FrameGrabber exception.
     */
    public void openVideo(File video) throws FrameGrabber.Exception {
        /* Null file close current video */
        if (video == null) {
            closeVideo();
            return;
        }
        
        /* Close any previous video */
        if (grabber != null) {
            grabber.close();
            grabber = null;
        }
        
        /* Video file metadata */
        String path = video.getAbsolutePath();
        
        /* Open video */
        grabber = new FFmpegFrameGrabber(path);
        grabber.start();
        
        /* Video multimedia metadata */
        videoMetadata = new VideoMetadata(video, grabber.getFormat().toUpperCase(), grabber.getImageWidth(), grabber.getImageHeight(), grabber.getLengthInTime(), grabber.getLengthInFrames(), grabber.getFrameRate());
    }
    
    /**
     * Close current video, release resources que reset metadata.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception FrameGrabber exception.
     */
    public void closeVideo () throws FrameGrabber.Exception {
        /* Close video */
        if (grabber != null) {
            grabber.close();
            grabber = null;
        }
        
        /* Clear metadata */
        videoMetadata = null;
    }
    
    /**
     * Get video metadata.
     * @return Video metadata.
     */
    public VideoMetadata getVideoMetadata () {
        return videoMetadata;
    }
    
    /**
     * Get the frame especified by number.
     * @param frameNumber Number of frame.
     * @return Buffered image of selected frame.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    public BufferedImage getFrame(int frameNumber) throws FrameGrabber.Exception {
        if (grabber == null) return null;
        
        frame = frameNumber;
        grabber.setFrameNumber(frameNumber);
        return toBufferedImage.convert(grabber.grab());
    }
    
    /**
     * Process the video each certain frames with the specified sampling level
     * and store the result.
     * @param gap Frames between samples.
     * @param samplingLevel Sampling level.
     */
    public void processVideo(int gap, int samplingLevel) {
        
    }
}
