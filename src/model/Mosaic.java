/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

/**
 * Mosaic class.
 */
public class Mosaic {
    private FFmpegFrameGrabber grabber;
    private final VideoMetadata metadata;
    
    /**
     * Mosaic Constructor.
     */
    public Mosaic() {
        metadata = new VideoMetadata();
        grabber = null;
    }
    
    /**
     * Open video from absolute path.
     * @param video Video file.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception FrameGrabber exception.
     */
    public void openVideo(File video) throws FrameGrabber.Exception {
        /* Close any previous video */
        if (grabber != null) {
            grabber.close();
            grabber = null;
        }
        
        /* Video file metadata */
        metadata.path = video.getAbsolutePath();
        metadata.name = video.getName();
        int point = metadata.name.indexOf('.');
        if (point != -1) metadata.name = metadata.name.substring(0, point);
        metadata.size = video.length();
        
        /* Open video */
        grabber = new FFmpegFrameGrabber(metadata.path);
        grabber.start();
        
        /* Video multimedia metadata */
        metadata.format = grabber.getFormat().toUpperCase();
        metadata.width = grabber.getImageWidth();
        metadata.height = grabber.getImageHeight();
        metadata.aspectRatio = grabber.getAspectRatio();
        metadata.length = grabber.getLengthInTime();
        metadata.frames = grabber.getLengthInFrames();
        metadata.fps = grabber.getFrameRate();
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
        metadata.clear();
    }
    
    /**
     * Get video metadata.
     * @return Video metadata.
     */
    public VideoMetadata getVideoMetadata () {
        try {
            return metadata.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Mosaic.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
