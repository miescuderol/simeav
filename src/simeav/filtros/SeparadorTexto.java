/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav.filtros;

import org.opencv.core.Mat;

/**
 *
 * @author Nacha
 */
public abstract class SeparadorTexto {
    protected Mat imagenTexto;
    protected Mat imagenSinTexto;

    
    public abstract void separarTexto(Mat original, int umbral_radio);
    
    public Mat getImagenTexto(){
        return imagenTexto;
    }
    
    public Mat getImagenSinTexto(){
        return imagenSinTexto;
    }
    
}
