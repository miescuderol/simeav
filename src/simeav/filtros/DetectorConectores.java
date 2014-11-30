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
public abstract class DetectorConectores {
    /*
        Detecta los conectores entre modulos de la imagen original.
        mascaraModulos determina en donde se encuntran los modulos.
        Agrega los conectores detectados al diagrama.
    */
    public abstract Mat detectarConectores(Mat original, Mat mascaraModulos, Diagrama diagrama);
}
