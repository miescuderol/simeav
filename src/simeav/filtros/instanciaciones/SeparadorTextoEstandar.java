/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav.filtros.instanciaciones;

import java.util.ArrayList;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import simeav.Utils;
import simeav.filtros.SeparadorTexto;

/**
 *
 * @author Nacha
 */
public class SeparadorTextoEstandar extends SeparadorTexto{
      

    
    @Override
    public void separarTexto(Mat origin, int umbral_radio) {
        Mat original = origin.clone();
        imagenSinTexto = original.clone();
        imagenTexto = Mat.zeros(original.size(), CvType.CV_8U);
        imagenTexto = imagenTexto.setTo(new Scalar(255, 255, 255));
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(original, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imGrises, bw, new Size(1,1), 0);
        Imgproc.threshold(bw, bw, 200, 250, Imgproc.THRESH_BINARY_INV);
        ArrayList<MatOfPoint> contornos = Utils.detectarContornos(bw);
        for(int i = 0; i < contornos.size(); i++){
            MatOfPoint2f contorno2f = new MatOfPoint2f();
            contorno2f.fromList(contornos.get(i).toList());
            float[] radius = new float[1];
            Point centro = new Point();
            Imgproc.minEnclosingCircle(contorno2f, centro, radius);
            double a = Imgproc.contourArea(contornos.get(i));
            if(radius[0] <= umbral_radio){
                Imgproc.drawContours(imagenSinTexto, contornos, i, new Scalar(255, 255, 255), -1);
                Imgproc.drawContours(imagenTexto, contornos, i, new Scalar(0, 0, 0), -1);
            }
        }
    }
    
    
}
