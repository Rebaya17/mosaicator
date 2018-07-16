/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.util.HashMap;

import java.io.File;
import javax.imageio.ImageIO;

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
    private long time;
    private Metadata metadata;
    private FFmpegFrameGrabber grabber;
    private BufferedImage mosaic;
    private PieceInfo[] piece;
    private final HashMap<Integer, Color[]> framesSamples;
    private static final Java2DFrameConverter TO_BUFFERED_IMAGE = new Java2DFrameConverter();
    
    /**
     * Piece information class.
     */
    private static class PieceInfo {
        public boolean used;
        public int frame;
        public int row;
        public int col;
        
        /**
         * Piece information Constructor.
         * @param id Frame number.
         * @param i Row.
         * @param j Column.
         */
        public PieceInfo(int id, int i, int j) {
            used = false;
            frame = id;
            row = i;
            col = j;
        }
    }
    
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
        time = 0;
        metadata = null;
        grabber = null;
        mosaic = null;
        piece = null;
        framesSamples = new HashMap<>();
        
        /* Disable FFmpeg verbose */
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }
    
    /**
     * Initialize timer.
     * @param message Message to show.
     * @return Time at begin.
     */
    private long timerStart(String message) {
        if ((message == null) || (message.isEmpty())) message = "Init timer";
        System.out.print("\t" + message + ":...");
        time = System.currentTimeMillis();
        return time;
    }
    
    /**
     * Stop the timer.
     * @return Time elapsed.
     */
    private long timerStop() {
        time = System.currentTimeMillis() - time;
        System.out.println("\t" + time / 1000.0F + " seconds elapsed approx.");
        return time;
    }
    
    /**
     * Calculate the average color of an image.
     * @param src Source image.
     * @param xi Initial column.
     * @param yi Initial row.
     * @param xf Final column.
     * @param yf Final row.
     * @return Average color.
     */
    private Color averageColor(BufferedImage src, int xi, int yi, int xf, int yf) {
        float acumR = 0.0F;
        float acumG = 0.0F;
        float acumB = 0.0F;
        int sum = 0;
        
        for (int i = xi; i < xf; i++) {
            for (int j = yi; j < yf; j++) {
                int color = src.getRGB(i, j);
                float u = (color >> 16) & 0xFF;
                float v = (color >>  8) & 0xFF;
                float s =  color        & 0xFF;
                sum++;
                
                if (deltaE) {
                    float r = u / 255.0F;
                    float g = v / 255.0F;
                    float b = s / 255.0F;
                    
                    r = (float) (r > 0.04045F ? Math.pow((r + 0.055F) / 1.055F, 2.4F) : r / 12.92F);
                    g = (float) (g > 0.04045F ? Math.pow((g + 0.055F) / 1.055F, 2.4F) : g / 12.92F);
                    b = (float) (b > 0.04045F ? Math.pow((b + 0.055F) / 1.055F, 2.4F) : b / 12.92F);

                    float x = (r * 0.4124F + g * 0.3576F + b * 0.1805F) / 0.95047F;
                    float y = (r * 0.2126F + g * 0.7152F + b * 0.0722F) / 1.00000F;
                    float z = (r * 0.0193F + g * 0.1192F + b * 0.9505F) / 1.08883F;

                    acumR += (float) (x > 0.008856F ? Math.pow(x, 1.0F / 3.0F) : (7.787F * x) + 16.0F / 116.0F);
                    acumG += (float) (y > 0.008856F ? Math.pow(y, 1.0F / 3.0F) : (7.787F * y) + 16.0F / 116.0F);
                    acumB += (float) (z > 0.008856F ? Math.pow(z, 1.0F / 3.0F) : (7.787F * z) + 16.0F / 116.0F);
                } else {
                    acumR += u;
                    acumG += v;
                    acumB += s;
                }
            }
        }
        
        float a = acumR / sum;
        float b = acumG / sum;
        float c = acumB / sum;
        return deltaE ? new Color(a, b, c, Color.CIELAB) : new Color(a, b, c, Color.RGB);
    }
    
    /**
     * Sample frames each interval with the currnt sampling level.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    private void sampleFrames() throws FrameGrabber.Exception {
        /* Reset members */
        framesSamples.clear();
        int top = metadata.frames();
        int width = metadata.width();
        int height = metadata.height();
        
        /* Dimensions of the subframes */
        float dx = (float) width / (float) samplingLevel;
        float dy = (float) height / (float) samplingLevel;
        
        
        /* For each frame */
        for (int i = 0; i < top; i += gap) {
            BufferedImage frame = getFrame(i);
            if (frame == null) break;
            
            Color[] average = new Color[samplingLevel * samplingLevel];
            int k = 0;
            float xi = 0.0F;
            float yi = 0.0F;
            float xf;
            float yf;
            
            /* For each subframe */
            for (int col = 0; col < samplingLevel; col++, xi += dx, yi = 0.0F) {
                xf = (int) (xi + dx + 0.5F);
                if (xf > width) xf = width;
                
                for (int row = 0; row < samplingLevel; row++, yi += dy, k++) {
                    yf = (int) (yi + dy + 0.5F);
                    if (yf > height) yf = height;
                    
                    int px = (int) (xi + 0.5F);
                    int py = (int) (yi + 0.5F);
                    
                    average[k] = averageColor(frame, (int) px, (int) py, (int) xf, (int) yf);
                }
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
    private Color[][] sampleFrame() throws FrameGrabber.Exception {
        /* Get frame to mosaicate */
        BufferedImage frame = getFrame(frameID);
        if (frame == null) return null;
        
        int width = metadata.width();
        int height = metadata.height();
        
        /* Dimensions of the pieces*/
        float dx = (float) width / (float) divisions;
        float dy = (float) height / (float) divisions;
        
        /* Dimensions of the subpieces */
        float dw = dx / (float) samplingLevel;
        float dz = dy / (float) samplingLevel;
        
        Color[][] frameSample = new Color[divisions * divisions][samplingLevel * samplingLevel];
        int k = 0;
        int l = 0;
        float xi = 0.0F;
        float yi = 0.0F;
        float wi = 0.0F;
        float zi = 0.0F;
        float wf;
        float zf;
        
        /* For each piece */
        for (int colD = 0; colD < divisions; colD++, xi += dx, yi = 0.0F)
            for (int rowD = 0; rowD < divisions; rowD++, yi += dy, wi = xi, k++, l = 0)
                /* For each subpiece */
                for (int colL = 0; colL < samplingLevel; colL++, wi += dw, zi = yi) {
                    wf = (int) (wi + dw + 0.5F);
                    if (wf > width) wf = (int) (width + 0.5F);
                    
                    for (int rowL = 0; rowL < samplingLevel; rowL++, zi += dz, l++) {
                        zf = (int) (zi + dz + 0.5F);
                        if (zf > height) zf = (int) (height * 0.5F);
                        
                        int pw = (int) (wi + 0.5F);
                        int pz = (int) (zi + 0.5F);
                        
                        frameSample[k][l] = averageColor(frame, pw, pz, (int) wf, (int) zf);
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
        Color[][] frameSample = sampleFrame();
        
        int top = frameSample.length;
        piece = new PieceInfo[top];
        
        /* For each destiny */
        for (int i = 0; i < top; i++) {
            float min = Float.POSITIVE_INFINITY;
            int nearest = 0;
            
            /* For each sampled frame */
            for (HashMap.Entry<Integer, Color[]> sampled : framesSamples.entrySet()) {
                float distance = 0;
                int frameNumber = sampled.getKey();
                Color[] average = sampled.getValue();
                
                /* Manhattan distance */
                for (int j = 0; j < average.length; j++) {
                    if (frameSample[i][j] == null) {
                        distance = Float.POSITIVE_INFINITY;
                        break;
                    }
                    
                    distance += frameSample[i][j].difference(average[j]);
                }
                
                /* Get min distance */
                if (distance < min) {
                    min = distance;
                    nearest = frameNumber;
                }
            }
            
            /* Store nearest */
            piece[i] = new PieceInfo(nearest, i / divisions, i % divisions);
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
        float d = divisions / scale;
        float pieceWidth = (frameWidth * scale) / divisions;
        float pieceHeight = (frameHeight * scale) / divisions;
        
        /* Dimensions of the mosaic */
        int mosaicWidth = (int) (frameWidth * scale + 0.5F);
        int mosaicHeight = (int) (frameHeight * scale + 0.5F);
        mosaic = new BufferedImage(mosaicWidth, mosaicHeight, BufferedImage.TYPE_3BYTE_BGR);
        
        /* For each piece */
        for (int i = 0; i < piece.length; i++) {
            PieceInfo info = piece[i];
            if (info.used) continue;
            
            int id = info.frame;
            BufferedImage frame = getFrame(id);
            
            /* Fill the mosaic */
            for (int j = i; j < piece.length; j++) {
                info = piece[j];
                if (info.frame != id) continue;
                
                info.used = true;
                float row = info.row * pieceWidth;
                float col = info.col * pieceHeight;

                float x = row;
                float y = col;

                for (float w = 0.0F; w < frameWidth; w += d, x++, y = col)
                    for (float z = 0.0F; z < frameHeight; z += d, y++) {
                        int px = (int) x;
                        int py = (int) y;
                        int pw = (int) w;
                        int pz = (int) z;

                        if (px >= mosaicWidth) px = mosaicWidth - 1;
                        if (py >= mosaicHeight) py = mosaicHeight - 1;

                        mosaic.setRGB(px, py, frame.getRGB(pw, pz));
                    }
            }
        }
        
        /* Reset flag */
        for (PieceInfo info : piece) info.used = false;
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
        piece = null;
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
        if ((gap != interval) || (samplingLevel != level)|| (deltaE != cielab) || (scale == 0.0F)) {
            gap = interval;
            deltaE = cielab;
            samplingLevel = level;
            scale = factor;
            
            timerStart("Processing video");
            
            sampleFrames();
            build = true;
            
            timerStop();
        }
        
        /* Search source frames */
        if ((frameID != frameNumber) || (divisions != div) || build) {
            frameID = frameNumber;
            divisions = div;
            
            timerStart("Processing frame");
            
            searchSources();
            build = true;
            
            timerStop();
        }
        
        /* Build mosaic */
        if((scale != factor) || build) {
            scale = factor;
            
            timerStart("Building mosaic");
            
            buildMosaic();
            
            timerStop();
        }
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
        BufferedImage frame = TO_BUFFERED_IMAGE.convert(grabber.grab());
        if (frame == null) return null;
        
        ColorModel colorModel = frame.getColorModel();
        return new BufferedImage(colorModel, frame.copyData(null), colorModel.isAlphaPremultiplied(), null);
    }
    
    /**
     * Get the source frames array.
     * @param pieceWidth Width of the pieces
     * @return Source frames array.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception Frame grabber exception.
     */
    public BufferedImage[] getPieces(int pieceWidth) throws FrameGrabber.Exception {
        if (frameID == -1) return null;
        
        /* Scaled pieces */
        ArrayList<BufferedImage> scaledPiece = new ArrayList<>();
        
        /* Dimensions of the frame */
        int width = metadata.width();
        int height = metadata.height();
        
        /* Dimensions of the pieces */
        float scaleFactor = (float) pieceWidth / (float) width;
        int pieceHeight = (int) (height * scaleFactor + 0.5F);
        
        float offset = 1.0F / scaleFactor;
        
        timerStart("Building preview");
        
        /* For each piece */
        for (int i = 0; i < piece.length; i++) {
            PieceInfo info = piece[i];
            if (info.used) continue;
            
            int id = info.frame;
            BufferedImage source = getFrame(id);
            BufferedImage frame = new BufferedImage(pieceWidth, pieceHeight, source.getType());
            
            /* Scale frame */
            float x = 0.0F;
            float y = 0.0F;
            
            for (int w = 0; w < pieceWidth; w++, x += offset, y = 0.0F)
                for (int z = 0; z < pieceHeight;  z++, y += offset)
                    frame.setRGB(w, z, source.getRGB((int) x, (int) y));

            scaledPiece.add(frame);
            
            /* Mark frames with the same id */
            for (int j = i; j < piece.length; j++) {
                info = piece[j];
                if (info.frame == id)
                    info.used = true;
            }
        }
        
        timerStop();
        
        /* Reset flag */
        for (PieceInfo info : piece) info.used = false;
        
        /* To array */
        BufferedImage[] pieces = new BufferedImage[scaledPiece.size()];
        scaledPiece.toArray(pieces);
        return pieces;
    }
    
    /**
     * Get the photomosaic.
     * @return Photomosaic.
     */
    public BufferedImage getMosaic() {
        return mosaic;
    }
    
    /**
     * Save current frame.
     * @param path Path to save.
     * @throws IOException IO Exception.
     */
    public void saveFrame(String path) throws IOException {
        ImageIO.write(getFrame(frameID), "png", new File(path + ".png"));
    }
    
    /**
     * Save current mosaic.
     * @param path Path to save.
     * @throws IOException IO Exception.
     */
    public void saveMosaic(String path) throws IOException {
        ImageIO.write(mosaic, "png", new File(path + ".png"));
    }
    
    /**
     * Save pieces.
     * @param path Path to save.
     * @throws IOException IO Exception.
     */
    public void savePieces(String path) throws IOException {
        timerStart("Saving pieces");
        
        /* For each piece */
        for (int i = 0; i < piece.length; i++) {
            PieceInfo info = piece[i];
            if (info.used) continue;
            
            /* Save frame */
            int id = info.frame;
            ImageIO.write(getFrame(id), "png", new File(String.format("%s%04d.png", path, i)));
            
            /* Mark frames with the same id */
            for (int j = i; j < piece.length; j++) {
                info = piece[j];
                if (info.frame == id)
                    info.used = true;
            }
        }
        
        timerStop();
        
        /* Reset flag */
        for (PieceInfo info : piece) info.used = false;
    }
}
