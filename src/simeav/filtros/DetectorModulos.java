/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav.filtros;

import org.opencv.core.Mat;
import simeav.Diagrama;

/**
 *
 * @author Nacha
 */
public abstract class DetectorModulos {
    /* 
       Detecta los modulos en la imagen original y los agrega al diagrama.
       Genera una imagen negra con cuadrados blancos, que luego sera usada
       para detectar los conectores.
    */
    public abstract Mat detectarModulos(Mat original, Diagrama diagrama);
}
