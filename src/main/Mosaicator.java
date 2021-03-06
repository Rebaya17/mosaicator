/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import model.Multimedia;
import view.MainWindow;
import view.AboutDialog;
import controller.Controller;


/**
 * Mosaicator class.
 */
public class Mosaicator {
    /**
     * Main function.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        /* MVC objects */
        Multimedia video;
        MainWindow mainWindow;
        AboutDialog about;
        Controller controller;
        
        /* Inicialize objects */
        video = new Multimedia();
        mainWindow = new MainWindow();
        about = new AboutDialog(mainWindow);
        controller = new Controller();
        
        /* Initialize MVC */
        controller.setMultimedia(video);
        controller.setMainWindow(mainWindow);
        controller.setAbout(about);
        controller.initMVC();
    }
}
