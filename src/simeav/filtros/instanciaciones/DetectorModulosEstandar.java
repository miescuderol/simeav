/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav.filtros.instanciaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import simeav.Diagrama;
import simeav.Utils;
import simeav.filtros.DetectorModulos;

/**
 *
 * @author Nacha
 */
public class DetectorModulosEstandar extends DetectorModulos{

    @Override
    public Mat detectarModulos(Mat original, Diagrama diagrama) {
        Imgproc.blur(original, original, new Size(15,15));
        original = Utils.dilate(original);
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        ArrayList<MatOfPoint> cp = new ArrayList<>(contornos.size());
        Map<Integer, Rect> rectangulos = new HashMap<>();
        Integer id_cuadrado = 0;
        Mat resultado = Mat.zeros(original.size(), CvType.CV_8U);
        for (int i = contornos.size()-1; i >= 0; i--) {
            if(jerarquia.get(0, i)[3] > -1){
                MatOfPoint2f contorno2f = new MatOfPoint2f();
                contorno2f.fromList(contornos.get(i).toList());
                MatOfPoint2f c = new MatOfPoint2f();
                Imgproc.approxPolyDP(contorno2f, c, 3, true);
                cp.add(new MatOfPoint(c.toArray()));
                int lados = cp.get(cp.size()-1).height();
                if((4 <= lados) && lados < 12){
                    rectangulos.put(id_cuadrado, Imgproc.boundingRect(new MatOfPoint(c.toArray()))); 
                    Point tl = new Point(rectangulos.get(id_cuadrado).tl().x - 20, rectangulos.get(id_cuadrado).tl().y - 20);
                    Point br = new Point(rectangulos.get(id_cuadrado).br().x + 20, rectangulos.get(id_cuadrado).br().y + 20);
                    Core.rectangle(resultado, tl, br, new Scalar(255, 255, 255), -1);
                    diagrama.addModulo(i, new Rect(tl, br));
                    Imgproc.drawContours(resultado, contornos, i, new Scalar(0, 0, 0), -1);
                    id_cuadrado++;
                } 
            }
        }
        return resultado;
    }
    
    
    
}
