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
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


/**
 *
 * @author Nacha
 */
public class Simeav {

    
    Mat imagenOriginal;
    Mat imagenCuadrados;
    
    Mat setImagenOriginal(File selectedFile) {
        imagenOriginal = Highgui.imread(selectedFile.getAbsolutePath());
        imagenCuadrados = getCuadrados(imagenOriginal);
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

    Mat getImagenOriginal() {
        return imagenOriginal;
    }
    
    Mat getImagenCuadrados() {
        return imagenCuadrados;
    }

    private Mat getCuadrados(Mat imagenOriginal) {
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Mat jerarquia = new Mat();
        Imgproc.cvtColor(imagenOriginal, imGrises, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Canny(imGrises, bw, 100, 150, 5, true);
        Imgproc.threshold(imGrises, bw, 200, 250, Imgproc.THRESH_BINARY);
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(bw.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        ArrayList<MatOfPoint> cp = new ArrayList<>(contornos.size());
        ArrayList<Rect> rectangulos = new ArrayList<>(contornos.size());
        int i = 0;
        for (MatOfPoint contorno : contornos) {
            MatOfPoint2f contorno2f = new MatOfPoint2f();
            contorno2f.fromList(contorno.toList());
            MatOfPoint2f c = new MatOfPoint2f();
            Imgproc.approxPolyDP(contorno2f, c, 3, true);
            cp.add(new MatOfPoint(c.toArray()));
            rectangulos.add(Imgproc.boundingRect(cp.get(i))); 
            i++;
        }
        Mat resultado = Mat.zeros(imGrises.size(), CvType.CV_8UC3);
        for(i = 0; i < contornos.size(); i++){
            Scalar color = new Scalar(180, 170, 5);
            Imgproc.drawContours(resultado, cp, i, color, 1, 8, jerarquia, 0, new Point());
            Core.rectangle(resultado, rectangulos.get(i).tl(), rectangulos.get(i).br(), color, 2, 8, 0);
        }
        return resultado;
    }

    
}
