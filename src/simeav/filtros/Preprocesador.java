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
public abstract class Preprocesador {
    public abstract Mat preprocesar(Mat original, int umbral);
}
