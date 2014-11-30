/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav.filtros.instanciaciones;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import simeav.filtros.Preprocesador;

/**
 *
 * @author Nacha
 */
public class PreprocesadorEstandar extends Preprocesador{

    @Override
    public Mat preprocesar(Mat original, int umbral) {
        Mat resultado = redimensionar(original);
        resultado = umbralar(resultado, umbral);
        return resultado;
    }

    private Mat redimensionar(Mat original) {    
        Size orig = original.size();
        if(orig.height > 1200 && orig.width > 1500){
            double tamX, tamY;
            double relX = orig.width/800;
            double relY = orig.height/600;
            if(orig.width/orig.height > 800/600){
                tamX = 800;
                tamY = orig.height/relX;
            }
            else {
                tamX = orig.width/relY;
                tamY = 600;
            }
            Mat resultado = new Mat();
            Imgproc.resize(original, resultado, new Size(tamX, tamY));
            return resultado;
        }
        return original;
    }

    private Mat umbralar(Mat original, int umbral) {
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(original, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imGrises, bw, new Size(1,1), 0);
        Imgproc.threshold(bw, bw, umbral, 250, Imgproc.THRESH_BINARY);
        Imgproc.cvtColor(bw, bw, Imgproc.COLOR_GRAY2RGB);
        return bw;
    }
    
    
}
