/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 * File Chooser class
 *
 * @author Erick
 */
public class FileChooser extends javax.swing.JFileChooser
{
    
    /* Constant static filter variables */
    private static final javax.swing.filechooser.FileNameExtensionFilter IMAGES = new javax.swing.filechooser.FileNameExtensionFilter("Archivo de imagen (*.PPM; *.PGM; *.PBM; *.PNM; *.BMP; *.PNG; *.JPG; *.JPEG; *.RLE)", "PPM", "PGM", "PBM", "PNM", "BMP", "PNG", "JPG", "JPEG", "RLE");
    private static final javax.swing.filechooser.FileNameExtensionFilter NETPBM = new javax.swing.filechooser.FileNameExtensionFilter("Netpbm (*.PPM; *.PGM; *.PBM; *.PNM; *.RLE)", "PPM", "PGM", "PBM", "PNM");
    private static final javax.swing.filechooser.FileNameExtensionFilter JPG = new javax.swing.filechooser.FileNameExtensionFilter("Joint Photographic Experts Group (*.JPG; *.JPEG)", "JPG", "JPEG");
    private static final javax.swing.filechooser.FileNameExtensionFilter BMP = new javax.swing.filechooser.FileNameExtensionFilter("Windows bitmap (*.BMP)", "BMP");
    private static final javax.swing.filechooser.FileNameExtensionFilter PNG = new javax.swing.filechooser.FileNameExtensionFilter("Portable Network Graphics (*.PNG)", "PNG");
    private static final javax.swing.filechooser.FileNameExtensionFilter PPM = new javax.swing.filechooser.FileNameExtensionFilter("Portable pixmap (*.PPM; *.PNM)",  "PPM", "PNM");
    private static final javax.swing.filechooser.FileNameExtensionFilter PGM = new javax.swing.filechooser.FileNameExtensionFilter("Portable graymap (*.PGM; *.PNM)", "PGM", "PNM");
    private static final javax.swing.filechooser.FileNameExtensionFilter PBM = new javax.swing.filechooser.FileNameExtensionFilter("Portable bitmap (*.PBM; *.PNM)",  "PBM", "PNM");
    private static final javax.swing.filechooser.FileNameExtensionFilter RLE = new javax.swing.filechooser.FileNameExtensionFilter("Run-length encoding (*.RLE)", "RLE");
    
    /**
     * Before new instance
     * 
     * @author Erick
     */
    {
        setName("FileChooser");
        addPropertyChangeListener(javax.swing.JFileChooser.FILE_FILTER_CHANGED_PROPERTY, (java.beans.PropertyChangeEvent evt) -> {
            if (getDialogType() != javax.swing.JFileChooser.SAVE_DIALOG) return;
            
            String path = getClientProperty("path").toString();
            path = path.substring(0, path.lastIndexOf('.')).toUpperCase();
            
            switch (getFilter())
            {
                case "JPG": path += ".jpg"; break;
                case "PNG": path += ".png"; break;
                case "BMP": path += ".bmp"; break;
                case "PNM": path += ".pnm"; break;
                case "PPM": path += ".ppm"; break;
                case "PGM": path += ".pgm"; break;
                case "PBM": path += ".pbm"; break;
                case "RLE": path += ".rle"; break;
                default:    path = getClientProperty("path").toString();
            }
            
            setSelectedFile(new java.io.File(path));
        });
    }
    
    /**
     * Constructor
     *
     * @author Erick
     */
    public FileChooser()
    {
        /* Set the Windows look and feel */
        //<editor-fold defaultstate="collapsed" desc="Look and feel setting code">
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Windows".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e)
        {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }
        //</editor-fold>
    }

    /**
     * Get selected format filter
     *
     * @author Erick
     * @return Selected format filter
     */
    public String getFilter()
    {
        String description;
        
        if (getFileFilter() == null) return "UNK";
        
        description = getFileFilter().getDescription();
        
        if (description.equals(NETPBM.getDescription())) return "PNM";
        if (description.equals(JPG.getDescription())) return "JPG";
        if (description.equals(PNG.getDescription())) return "PNG";
        if (description.equals(BMP.getDescription())) return "BMP";
        if (description.equals(PPM.getDescription())) return "PPM";
        if (description.equals(PGM.getDescription())) return "PGM";
        if (description.equals(PBM.getDescription())) return "PBM";
        if (description.equals(RLE.getDescription())) return "RLE";

        return "UNK";
    }
    
    /**
     * Set file format filters
     *
     * @author Erick
     * @param filter Format of file
     * @param depth Image bit depth
     */
    public void setFilter(String filter, int depth)
    {
        resetChoosableFileFilters();
        setAcceptAllFileFilterUsed(false);

        /* Add file filters */
        switch (depth)
        {
            case 0:
                setAcceptAllFileFilterUsed(true);
                addChoosableFileFilter(IMAGES);
            case 3:
            case 24:
                addChoosableFileFilter(JPG);
                addChoosableFileFilter(PNG);
                addChoosableFileFilter(BMP);
                addChoosableFileFilter(PPM);
            case 8:
                addChoosableFileFilter(PGM);
            case 1:
                addChoosableFileFilter(PBM);
            default:
                addChoosableFileFilter(RLE);
        }
        
        
        /* Set new filter */
        if (filter == null) return;
        if (!filter.equals("RLE"))
        {
                 if (depth == 8) filter = "PGM";
            else if (depth == 1) filter = "PBM";
        }
        
        switch (filter)
        {
            case "PNG": setFileFilter(PNG); return;
            case "BMP": setFileFilter(PNG); return;
            case "PPM":
            case "PNM": setFileFilter(PPM);
        }
    }
}
