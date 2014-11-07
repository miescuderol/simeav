/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import org.opencv.core.Mat;

/**
 *
 * @author Nacha
 */
public class InfoImagen {
    private String nombre;
    private Mat imagen;
    
    public InfoImagen(String nombre, Mat imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public Mat getImagen() {
        return imagen;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
    
    
}
