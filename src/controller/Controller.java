/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JSlider;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.javacv.FrameGrabber;
import javax.imageio.ImageIO;

import model.Multimedia;
import view.MainWindow;
import view.AboutDialog;

/**
 * Controller class.
 */
public class Controller extends WindowAdapter implements ActionListener, ChangeListener {
    private Multimedia video;
    private MainWindow mainWindow;
    private AboutDialog about;
    private final JFileChooser fileChooser;
    
    /**
     * Controller constructor.
     */
    public Controller() {
        video = null;
        mainWindow = null;
        about = null;
        fileChooser = new JFileChooser();
    }
    
    /**
     * Perform components action commands.
     * @param e Action event
     */
    // <editor-fold defaultstate="collapsed" desc="Perform components action commands">
    @Override
    public void actionPerformed(ActionEvent e) {
        /* Get command */
        String command = e.getActionCommand();
        
        /* Command validation */
        if (command == null)
        {
            System.err.println("Error: null command");
            return;
        }
        
        /* Handle command */
        System.out.println("Command: " + command);
        switch (command)
        {
            /* Menu bar */
            /* Fiile menu */
            case "fileMenu": return;
            case "newFile": newFile(); return;
            case "openFile": openFile(); return;
            case "save": save(); return;
            case "saveAs": saveAs(); return;
            case "exportMosaicMenu": exportMosaic(); return;
            case "exportPiecesMenu": exportPieces(); return;
            case "close": close(); return;
            case "exit": exit(); return;
            
            /* About menu */
            case "helpMenu": return;
            case "about": about(); return;
            
            /* Tabbed pane */
            /* Frame tab */
            case "openVideo": openVideo(); return;
            
            /* Mosaic tab */
            case "generate": generate(); return;
            case "exportMosaicButton": exportMosaic(); return;
            
            /* Pieces tab */
            case "exportPiecesButton": exportPieces(); return;
            
            /* Unknow */
            default: System.err.println("Error: unknow command \"" + command + "\"");
        }
    }
    // </editor-fold>
    

    /**
     * Perform state changed
     * @param e Change event
     */
    @Override
    // <editor-fold defaultstate="collapsed" desc="Perform component state changed">
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        
        if ((source instanceof JSlider) || (source instanceof JSpinner)) {
            try {
                mainWindow.setFrame(video.getFrame(mainWindow.getFrameNumber()));
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "No se puede obtener el cuadro:\n", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // </editor-fold>
    
    /**
     * Close connections and terminate application.
     * @param e Event.
     */
    @Override
    // <editor-fold defaultstate="collapsed" desc="Window closing">
    public void windowClosing(WindowEvent e) {
        exit();
    }
    // </editor-fold>
    
    /**
     * Initialize Model-View-Controller.
     */
    // <editor-fold defaultstate="collapsed" desc="Initialize Model-View-Controller">
    public void initMVC()
    {
        /* Check MVC objects */
        boolean good = true;
        good &= (video != null);
        good &= (mainWindow != null);
        good &= (mainWindow != null);
        
        if (good) {
            /* Establish connection with controller */
            mainWindow.setController(this);

            /* Show main window */
            mainWindow.setExtendedState(MainWindow.MAXIMIZED_BOTH);
            mainWindow.setVisible(true);
            
            /* Set components to default closed state */
            mainWindow.reset();
        }
        else {
            System.err.println("Error: could not initialize MVC application.");
            JOptionPane.showMessageDialog(null, "No se puede iniciar la aplicación MVC", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    // </editor-fold>
    
    
    /**
     * MVC setters
     */
    
    /**
     * Set the mosaic model.
     * @param model Mosaic model.
     */
    public void setMosaic(Multimedia model) {
        video = model;
    }
    
    /**
     * Set the main window.
     * @param view Main window.
     */
    public void setMainWindow(MainWindow view) {
        mainWindow = view;
    }
    
    /**
     * Set the about dialog.
     * @param view About dialog.
     */
    public void setAbout(AboutDialog view) {
        about = view;
    }
    
    
    /**
     * Components action commands.
     */
    
    /**
     * Discard everything and create a new Mosaicator project by opening a new
     * video.
     */
    public void newFile() {
        close();
        openVideo();
    }
    
    /**
     * Open new Mosaicator project file.
     */
    public void openFile() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.resetChoosableFileFilters();
        
        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                // Read Mosaicator project file
                File file = fileChooser.getSelectedFile();
                video.open(null);
                mainWindow.setMosaic(null, null);
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(mainWindow, "Ha ocurrido un error abriendo el archivo:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Save Mosaicator project file.
     */
    public void save() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.resetChoosableFileFilters();
        
        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            
        }
    }
    
    /**
     * Save Mosaicator project file as.
     */
    public void saveAs() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.resetChoosableFileFilters();
        
        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            
        }
    }
    
    /**
     * Export mosaic as image.
     */
    public void exportMosaic() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.resetChoosableFileFilters();
        
        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                ImageIO.write(video.getMosaic(), "png", new File(path + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Export posaic pieces as image.
     */
    public void exportPieces() {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.resetChoosableFileFilters();
        
        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            
        }
    }
    
    /**
     * Close current Mosaicator project and release resources.
     */
    public void close() {
        mainWindow.reset();
        try {
            video.close();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(mainWindow, "Ha ocurrido un error cerrando el archivo:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Release resources and exit the Mosaicator application.
     */
    public void exit() {
        mainWindow.dispose();
        close();
    }
    
    /**
     * Show about dialog.
     */
    public void about() {
        about.setVisible(true);
    }
    
    /**
     * Open video file.
     */
    public void openVideo() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.resetChoosableFileFilters();
        
        if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                video.open(fileChooser.getSelectedFile());
                mainWindow.setVideoMetadata(video.getMetadata());
                mainWindow.setFrame(video.getFrame(0));
                mainWindow.setMosaic(null, null);
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(mainWindow, "Ha ocurrido un error abriendo el archivo:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Generate mosaic.
     */
    public void generate() {
        try {
            video.mosaicate(mainWindow.getFrameNumber(), mainWindow.getDivisions(), mainWindow.getGap(), mainWindow.getScale(), mainWindow.getSamplingLevel());
            mainWindow.setMosaic(video.getMosaic(), video.getSourceFrames());
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(mainWindow, "Ha ocurrido un error creando el mosaico:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
