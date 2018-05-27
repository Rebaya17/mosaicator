/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import model.Mosaic;
import view.MainWindow;
import view.AboutDialog;
import controller.Controller;


/**
 * Mosaicator class.
 * 
 * @author Erick
 */
public class Mosaicator {
    /**
     * Main function.
     * 
     * @author Erick
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        /* MVC objects */
        Mosaic mosaic;
        MainWindow mainWindow;
        AboutDialog about;
        Controller controller;
        
        /* Inicialize objects */
        mosaic = new Mosaic();
        mainWindow = new MainWindow();
        about = new AboutDialog(mainWindow);
        controller = new Controller();
        
        /* Initialize MVC */
        controller.setMosaic(mosaic);
        controller.setMainWindow(mainWindow);
        controller.setAbout(about);
        controller.initMVC();
    }
}
