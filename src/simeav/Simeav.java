/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Nacha
 */
public class Simeav {

    File imagenOriginal;
    
    Object setImagenOriginal(File selectedFile) {
        imagenOriginal = selectedFile;
        return imagenOriginal;
    }

    void guardar(File selectedFile) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(selectedFile.getAbsolutePath(), "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Simeav.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    File getImagenOriginal() {
        return imagenOriginal;
    }

    
}
