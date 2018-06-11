/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.util.HashMap;

import java.awt.image.BufferedImage;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Java2DFrameConverter;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

/**
 * Mosaic class.
 */
public class Multimedia {
    private int frameID;
    private int divisions;
    private int gap;
    private int samplingLevel;
    private boolean deltaE;
    private float scale;
    private Metadata metadata;
    private FFmpegFrameGrabber grabber;
    private BufferedImage mosaic;
    private int[] sourceFrameNumber;
    private BufferedImage[] sourceFrame;
    private final HashMap<Integer, Integer[]> framesSamples;
    private static final Java2DFrameConverter TO_BUFFERED_IMAGE = new Java2DFrameConverter();
    
    /**
     * Mosaic Constructor.
     */
    public Multimedia() {
        frameID = -1;
        divisions = 0;
        gap = 0;
        samplingLevel = 0;
        deltaE = false;
        scale = 0.0F;
        metadata = null;
        grabber = null;
        mosaic = null;
        sourceFrameNumber = null;
        sourceFrame = null;
        framesSamples = new HashMap<>();
        
        /* Disable FFmpeg verbose */
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }
    
    /**
     * Calculate the average color of an image.
     * @param src Source image.
     * @param offset Pixel offset.
     * @return Average color.
     */
    private int averageColor(BufferedImage src, int offset) {
        int width = src.getWidth();
        int height = src.getHeight();
        int acumR = 0;
        int acumG = 0;
        int acumB = 0;
        int sum = 0;
        
        for (int x = 0; x < width; x += offset) {
            for (int y = 0; y < height; y += offset) {
                int rgb = src.getRGB(x, y);
                acumR += (rgb >> 16) & 0x00FF0000;
                acumG += (rgb >>  8) & 0x0000FF00;
                acumB +=  rgb        & 0x000000FF;
                sum++;
            }
        }
        
        return ((acumR / (sum)) << 16) | ((acumG / sum) << 8) | (acumB / sum);
    }
    
    /**
     * Sample frames each interval with the currnt sampling level.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    private void sampleFrames() throws FrameGrabber.Exception {
        /* Reset members */
        framesSamples.clear();
        int top = metadata.frames();
        
        /* Dimensions of the subframes */
        int offset = (int) (samplingLevel / scale);
        float dx = (float) metadata.width() / (float) samplingLevel;
        float dy = (float) metadata.height() / (float) samplingLevel;
        
        
        /* For each frame */
        for (int i = 0; i < top; i += gap) {
            BufferedImage frame = getFrame(i);
            if (frame == null) break;
            
            Integer[] average = new Integer[samplingLevel * samplingLevel];
            int k = 0;
            float x = 0.0F;
            float y = 0.0F;
            
            /* For each subframe */
            for (int col = 0; col < samplingLevel; col++, x += dx,  y = 0.0F)
                for (int row = 0; row < samplingLevel; row++, y += dy, k++) {
                    float px = x + 0.5F;
                    float py = y + 0.5F;
                    float subWidth = px + dx;
                    float subHeight = py + dy;
                    average[k] = averageColor(frame.getSubimage((int) px, (int) py, (int) subWidth, (int) subHeight), offset);
                }
            /* Put frame sample */
            framesSamples.put(i, average);
        }
    }
    
    /**
     * Sample the selected frame with the currnt sampling level.
     * @return Samples array with each color mean by level.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    private Integer[][] sampleFrame() throws FrameGrabber.Exception {
        /* Get frame to mosaicate */
        BufferedImage frame = getFrame(frameID);
        if (frame == null) return null;
        
        /* Dimensions of the pieces*/
        float dx = (float) metadata.width() / (float) divisions;
        float dy = (float) metadata.height() / (float) divisions;
        
        /* Dimensions of the subpieces */
        float dw = dx / (float) samplingLevel;
        float dz = dy / (float) samplingLevel;
        
        Integer[][] frameSample = new Integer[divisions * divisions][samplingLevel * samplingLevel];
        int k = 0;
        int l = 0;
        float x = 0.0F;
        float y = 0.0F;
        float w = 0.0F;
        float z = 0.0F;
        
        /* For each piece */
        for (int colD = 0; colD < divisions; colD++, x += dx, y = 0.0F) 
            for (int rowD = 0; rowD < divisions; rowD++, y += dy, k++, l = 0) {
                float px = x + 0.5F;
                float py = y + 0.5F;
                float pieceWidth = px + dx;
                float pieceHeight = py + dy;
                BufferedImage piece = frame.getSubimage((int) x, (int) y, (int) pieceWidth, (int) pieceHeight);
                
                /* For each subpiece */
                for (int colL = 0; colL < samplingLevel; colL++, w += dw, z = 0.0F)
                    for (int rowL = 0; rowL < samplingLevel; rowL++, z += dz, l++) {
                        float pw = w + 0.5F;
                        float pz = z + 0.5F;
                        float subWidth = pw + dw;
                        float subHeight = pz + dz;
                        frameSample[k][l] = averageColor(piece.getSubimage((int) w, (int) z, (int) subWidth, (int) subHeight), 1);
                    }
        }
        
        return frameSample;
    }
    
    /**
     * Search source frames to mosaic and save on sourceFrame.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    private void searchSources() throws FrameGrabber.Exception {
        /* Sample selected frame */
        Integer[][] frameSample = sampleFrame();
        
        int top = frameSample.length;
        sourceFrameNumber = new int[top];
        
        /* For each destiny */
        for (int i = 0; i < top; i++) {
            int min = Integer.MAX_VALUE;
            int nearest = 0;
            
            /* For each sampled frame */
            for (HashMap.Entry<Integer, Integer[]> sampled : framesSamples.entrySet()) {
                int distance = 0;
                int frameNumber = sampled.getKey();
                Integer[] average = sampled.getValue();
                
                /* Manhattan distance */
                for (int j = 0; j < average.length; j++) {
                    if (frameSample[i][j] == null) {
                        distance = Integer.MAX_VALUE;
                        break;
                    }
                    
                    int rgbDst = frameSample[i][j];
                    int rgbSrc = average[j];
                    int r = Math.abs(((rgbSrc >> 16) & 0xFF) - ((rgbDst >> 16) & 0xFF));
                    int g = Math.abs(((rgbSrc >>  8) & 0xFF) - ((rgbDst >>  8) & 0xFF));
                    int b = Math.abs(( rgbSrc        & 0xFF) - ( rgbDst        & 0xFF));
                    
                    distance += r + g + b;
                }
                
                /* Get min distance */
                if (distance < min) {
                    min = distance;
                    nearest = frameNumber;
                }
            }
            
            /* Store nearest */
            sourceFrameNumber[i] = nearest;
        }
    }
    
    /**
     * Build a photomosaic from the selected source frames.
     * @throws FrameGrabber.Exception FrameGrabber exception.
     */
    private void buildMosaic() throws FrameGrabber.Exception {
        /* Dimensions of the frames */
        int frameWidth = metadata.width();
        int frameHeight = metadata.height();
        
        /* Dimensions of the pieces */
        int offset = (int) (samplingLevel / scale);
        float dx = (float) frameWidth / (float) samplingLevel;
        float dy = (float) frameHeight / (float) samplingLevel;
        
        /* Dimensions of the mosaic */
        int mosaicWidth = (int) Math.ceil(frameWidth * scale);
        int mosaicHeight = (int) Math.ceil(frameHeight * scale);
        mosaic = new BufferedImage(mosaicWidth, mosaicHeight, BufferedImage.TYPE_3BYTE_BGR);
        
        int k = 0;
        float x = 0.0F;
        float y = 0.0F;
        float w = 0.0F;
        float z = 0.0F;
        
        /* For each piece */
        for (int col = 0; col < divisions; col++, x += dx, y = 0.0F) {
            for (int row = 0; row < divisions; row++, y += dy, k++, w = 0.0F) {
                BufferedImage piece = getFrame(sourceFrameNumber[k]);
                
                /* For each scaled pixel */
                for (int i = 0; w < frameWidth; i++, w += offset, z = 0.0F)
                    for (int j = 0; z < frameWidth; j++, z += offset) {
                        float px = x + i + 0.5F;
                        float py = y + j + 0.5F;
                        float pw = w + 0.5F;
                        float pz = z + 0.5F;
                        
                        mosaic.setRGB((int) px, (int) py, piece.getRGB((int) pw, (int) pz));
                    }
            }
        }
    }
    
    /**
     * Open video from absolute path.
     * @param video Video file.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception FrameGrabber exception.
     */
    public void open(File video) throws FrameGrabber.Exception {
        /* Close any previous video */
        if (grabber != null) close();
        
        /* Video file metadata */
        String path = video.getAbsolutePath();
        
        /* Open video */
        grabber = new FFmpegFrameGrabber(path);
        grabber.start();
        
        /* Video multimedia metadata */
        metadata = new Metadata(video, grabber.getFormat().toUpperCase(), grabber.getImageWidth(), grabber.getImageHeight(), grabber.getLengthInTime(), grabber.getLengthInFrames(), grabber.getFrameRate());
    }
    
    /**
     * Close current video, release resources que reset metadata.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception FrameGrabber exception.
     */
    public void close () throws FrameGrabber.Exception {
        /* Close video */
        if (grabber != null) grabber.close();
        
        /* Clear metadata */
        frameID = -1;
        divisions = 0;
        gap = 0;
        samplingLevel = 0;
        deltaE = false;
        scale = 0.0F;
        metadata = null;
        grabber = null;
        mosaic = null;
        sourceFrameNumber = null;
        sourceFrame = null;
        framesSamples.clear();
    }
    
    /**
     * Create a photomosaic from video frames sampled with the current
     * level. If level is less or equal zero makes total sampling.
     * @param frameNumber Number of the frame to mosaicate.
     * @param div Number of divisions.
     * @param interval Interval of frames to sample.
     * @param level Sampling level.
     * @param cielab Color space
     * @param factor Scale of the mosaic.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    public void mosaicate(int frameNumber, int div, int interval, int level, boolean cielab, float factor) throws FrameGrabber.Exception {
        boolean build = false;
        
        /* Process video frames */
        if ((gap != interval) || (samplingLevel != level)|| (deltaE != cielab)  || (scale != factor)) {
            gap = interval;
            deltaE = cielab;
            samplingLevel = level;
            scale = factor;
            sampleFrames();
            build = true;
        }
        
        /* Search source frames */
        if ((frameID != frameNumber) || (divisions != div) || (scale != factor) || build) {
            frameID = frameNumber;
            divisions = div;
            searchSources();
            build = true;
        }
        
        /* Build mosaic */
        if(build) buildMosaic();
    }
    
    /**
     * Get video metadata.
     * @return Video metadata.
     */
    public Metadata getMetadata () {
        return metadata;
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
     * Get the source frames array.
     * @return Source frames array.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    public BufferedImage[] getSourceFrames() throws FrameGrabber.Exception {
        if (frameID == -1) return null;
        return sourceFrame;
    }
    
    /**
     * Get the photomosaic.
     * @return Photomosaic.
     */
    public BufferedImage getMosaic() {
        return mosaic;
    }
}
