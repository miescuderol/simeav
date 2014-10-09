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
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


/**
 *
 * @author Nacha
 */
public class Simeav {

    
    Mat imagenOriginal;
    Mat imagenCuadrados;
    Mat imagenBinaria;
    Mat imagenLineas;
    
    Mat setImagenOriginal(File selectedFile, int th) {
        imagenOriginal = Highgui.imread(selectedFile.getAbsolutePath());
        calcularBinaria(th);
        detectarCuadrados();
        detectarLineas(th);
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
        detectarCuadrados();
        return imagenCuadrados;
    }
    
    Mat getImagenBinaria(int i) {
        calcularBinaria(i);
        return imagenBinaria;
    }
    
    Mat getImagenLineas(int th) {
        detectarLineas(th);
        return imagenLineas;
    }

    private void detectarCuadrados() {
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(this.imagenBinaria.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
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
        Mat resultado = Mat.zeros(this.imagenOriginal.size(), CvType.CV_8UC3);
        for(i = 0; i < contornos.size(); i++){
            Scalar color = new Scalar(180, 170, 5);
            Imgproc.drawContours(resultado, cp, i, color, 1, 8, jerarquia, 0, new Point());
            Core.rectangle(resultado, rectangulos.get(i).tl(), rectangulos.get(i).br(), color, 2, 8, 0);
        }
        this.imagenCuadrados = resultado;
    }

    private void detectarLineas(int th){
        Mat lineas = new Mat();
        Mat bw = new Mat();
        Imgproc.HoughLinesP(this.imagenBinaria, lineas, 1, 3.1416/180, 50, 50, th);
        Mat resultado = Mat.zeros(this.imagenOriginal.size(), CvType.CV_8UC3);
        for(int i = 0; i < lineas.size().width; i++){
            double[] linea = lineas.get(0, i);
            Core.line(resultado, new Point(linea[0], linea[1]), new Point(linea[2], linea[3]), new Scalar(180, 170, 5), 1);
        }
        this.imagenLineas = resultado;
    }
    
    private void calcularBinaria(int i){
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(this.imagenOriginal, imGrises, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Canny(imGrises, bw, 100, 150, 5, true);
        Imgproc.GaussianBlur(imGrises, bw, new Size(5,5), 0);
        Imgproc.threshold(bw, bw, 200, 250, Imgproc.THRESH_BINARY_INV);
        Mat kernel = Imgproc.getStructuringElement(1, new Size(3,3));
        Imgproc.dilate(bw, bw, kernel, new Point(-1,-1), i);
        Imgproc.erode(bw, bw, kernel, new Point(-1,-1), i);
        this.imagenBinaria = bw;
    }
}
