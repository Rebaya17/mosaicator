/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.util.HashMap;

import java.awt.image.BufferedImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.OpenCVFrameConverter;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * Mosaic class.
 */
public class Mosaic {
    private int gap;
    private int samplingLevel;
    private FFmpegFrameGrabber grabber;
    private VideoMetadata videoMetadata;
    private HashMap<Integer, Integer[]> framesSampling;
    
    /* Frame converters */
    private static final Java2DFrameConverter TO_BUFFERED_IMAGE = new Java2DFrameConverter();
    private static final OpenCVFrameConverter.ToMat TO_MAT = new OpenCVFrameConverter.ToMat();
    
    
    /**
     * Mosaic Constructor.
     */
    public Mosaic() {
        grabber = null;
        gap = 0;
        samplingLevel = 0;
        videoMetadata = null;
        framesSampling = new HashMap<>();
    }
    
    /**
     * Open video from absolute path.
     * @param video Video file.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception FrameGrabber exception.
     */
    public void openVideo(File video) throws FrameGrabber.Exception {
        /* Close any previous video */
        if (grabber != null) closeVideo();
        
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
        framesSampling.clear();
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
        grabber.setFrameNumber(frameNumber);
        return TO_BUFFERED_IMAGE.convert(grabber.grab());
    }
    
    /**
     * Process the video each certain frames with the specified sampling level
     * and store the result.
     * @param interval Frames between samples.
     * @param level Sampling level.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    public void processVideo(int interval, int level) throws FrameGrabber.Exception {
        if ((gap == interval) && (samplingLevel == level)) return;
       
        gap = interval;
        samplingLevel = level;
        framesSampling.clear();
        
        int max = videoMetadata.frames();
        for (int index = 0; index < max; index += gap) {
            grabber.setFrameNumber(index);
            Mat frame = TO_MAT.convert(grabber.grab());
            
            
        }
    }
}
