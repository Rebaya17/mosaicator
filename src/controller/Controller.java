/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import model.Mosaic;
import view.MainWindow;
import view.AboutDialog;

/**
 * Controller class.
 * 
 * @author Erick
 */
public class Controller extends WindowAdapter implements ActionListener, ChangeListener {
    private Mosaic mosaic;
    private MainWindow mainWindow;
    private AboutDialog about;
    
    /**
     * Controller constructor.
     * 
     * @author Erick
     */
    public Controller() {
        mosaic = null;
        mainWindow = null;
        about = null;
    }
    
    /**
     * Perform components action commands.
     *
     * @author Erick
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
            case "newFile": return;
            case "openFile": return;
            case "save": return;
            case "saveAs": return;
            case "exportMosaicMenu": return;
            case "exportPiecesMenu": return;
            case "close": return;
            case "exit": exit(); return;
            
            /* About menu */
            case "helpMenu": return;
            case "about": about(); return;
            
            /* Tabbed pane */
            /* Frame tab */
            case "openVideo": return;
            
            /* Mosaic tab */
            case "generate": return;
            case "exportMosaicButton": return;
            
            /* Pieces tab */
            case "exportPiecesButton": return;
            
            /* Unknow */
            default: System.err.println("Error: unknow command \"" + command + "\"");
        }
    }
    // </editor-fold>
    

    /**
     * Perform state changed
     *
     * @author Erick
     * @param e Change event
     */
    @Override
    // <editor-fold defaultstate="collapsed" desc="Perform component state changed">
    public void stateChanged(ChangeEvent e) {
        
    }
    // </editor-fold>
    
    /**
     * Close connections and terminate application.
     * 
     * @author Erick
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
     *
     * @author Erick
     */
    // <editor-fold defaultstate="collapsed" desc="Initialize Model-View-Controller">
    public void initMVC()
    {
        /* Check MVC objects */
        boolean good = true;
        good &= (mosaic != null);
        good &= (mainWindow != null);
        good &= (mainWindow != null);
        
        if (good) {
            /* Establish connection with controller */
            mainWindow.setController(this);

            /* Show main window */
            mainWindow.setExtendedState(MainWindow.MAXIMIZED_BOTH);
            mainWindow.setVisible(true);
        }
        else {
            System.err.println("Error: could not initialize MVC application.");
            JOptionPane.showMessageDialog(null, "No se puede iniciar la aplicación MVC", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    // </editor-fold>
    
    
    /**
     * Setters
     */
    
    /**
     * Set the mosaic model.
     * 
     * @author Erick
     * @param model Mosaic model.
     */
    public void setMosaic(Mosaic model) {
        mosaic = model;
    }
    
    /**
     * Set the main window.
     * 
     * @author Erick
     * @param view Main window.
     */
    public void setMainWindow(MainWindow view) {
        mainWindow = view;
    }
    
    /**
     * Set the about dialog.
     * 
     * @author Erick
     * @param view About dialog.
     */
    public void setAbout(AboutDialog view) {
        about = view;
    }
    
    
    /**
     * Components action commands
     */
    
    /**
     * Exit application.
     * 
     * @author Erick
     */
    public void exit() {
        mainWindow.dispose();
    }
    
    /**
     * Show about dialog.
     * 
     * @author Erick
     */
    public void about() {
        about.setVisible(true);
    }
}
