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
import org.opencv.imgproc.Moments;


/**
 *
 * @author Nacha
 */
public class Simeav {
 
    private Mat imagenOriginal;
    private Mat imagenCuadrados;
    private Mat imagenBinaria;
    private Mat imagenLineas;
    private Mat imagenVertices;
    private Mat imagenContornos;
    
    Mat setImagenOriginal(File selectedFile, int th) {
        imagenOriginal = Highgui.imread(selectedFile.getAbsolutePath());
        imagenBinaria = calcularBinaria(imagenOriginal);
        imagenBinaria = dilate(imagenBinaria);
        imagenCuadrados = detectarCuadrados(imagenBinaria);
        imagenLineas = detectarLineas(imagenBinaria);
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
    
    Mat getImagenCuadrados(Mat imagen) {
        return detectarCuadrados(imagen);
    }
    
    Mat getImagenCuadrados(){
        return this.imagenCuadrados;
    }
    
    Mat getImagenBinaria(Mat imagen) {
        return calcularBinaria(imagen);
    }
    
    Mat getImagenBinaria(){
        return this.imagenBinaria;
    }
    
    Mat getImagenLineas(Mat imagen) {
        return detectarLineas(imagen);
    }
    
    Mat getImagenLineas(){
        return this.imagenLineas;
    }
    
    Mat getImagenVertices(Mat imagen){
        return imagenVertices;
    }
    
    Mat getImagenVertices(){
        return this.imagenVertices;
    }
    
    Mat getErode(Mat imagen){
        return erode(imagen);
    }
    
    Mat getDilate(Mat imagen){
        return dilate(imagen);
    }
    
    Mat getContornos() {
        return imagenContornos;
    }
    
    private ArrayList<MatOfPoint> detectarContornos(Mat original) {
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("jerarquia"
                + jerarquia.dump());
        return contornos;
    }

    private Mat detectarCuadrados(Mat original) {
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
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
        Mat resultado = Mat.zeros(original.size(), CvType.CV_8UC3);
        imagenContornos = Mat.zeros(original.size(), CvType.CV_8UC3);
        for(i = 0; i < contornos.size(); i++){
            Scalar color = new Scalar(180, 170, 5);
            Imgproc.drawContours(imagenContornos, contornos, i, color, 1, 8, jerarquia, 0, new Point());
            Imgproc.drawContours(resultado, cp, i, color, 1, 8, jerarquia, 0, new Point());
            Core.rectangle(resultado, rectangulos.get(i).tl(), rectangulos.get(i).br(), color, 2, 8, 0);
        }
        return resultado;
    }

    private Mat detectarLineas(Mat original){
        Mat lineas = new Mat();
        Mat bw = new Mat();
        ArrayList<Point> vertices = detectarVertices(original);
        Mat resultado = Mat.zeros(original.size(), CvType.CV_8UC3);
        for(int i = 0; i < vertices.size(); i++){
            for(int j = 0; j < vertices.size(); j++){
                System.out.println("vertice " + i + ": "
                        + vertices.get(i).x + ", " + vertices.get(i).y
                        + " vertice " + j + ": "
                        + vertices.get(j).x + ", " + vertices.get(j).y);
                Core.line(resultado, new Point(vertices.get(i).x, vertices.get(i).y), new Point(vertices.get(j).x, vertices.get(j).y), new Scalar(180, 170, 5), 1);
            }
        }
        return resultado;
    }

    
    private ArrayList<Point> detectarVertices(Mat original){
        MatOfPoint corners = new MatOfPoint();
        Imgproc.goodFeaturesToTrack(original, corners, 100, 0.01, 0, new Mat(), 2, false, 0.04);
        Mat vertices = Mat.zeros(original.size(), CvType.CV_8UC3);
        for(int i = 0; i < corners.height(); i++){
            Core.circle(vertices, new Point(corners.get(i, 0)), 8, new Scalar(180, 170, 5), -1);
        }
        
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(vertices, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(imGrises, bw, 100, 150, 5, true);
        
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(bw.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("contornos"
                + contornos.size());
        ArrayList<Moments> mu = new ArrayList<>(contornos.size());
        for(int i = 0; i < contornos.size(); i++){
            mu.add(i, Imgproc.moments(contornos.get(i), false));
        }
        ArrayList<Point> mc = new ArrayList<>(contornos.size());
        for(int i = 0; i < contornos.size(); i++){
            mc.add(i, new Point(mu.get(i).get_m10()/mu.get(i).get_m00(), mu.get(i).get_m01()/mu.get(i).get_m00()));
        }
        Mat resultado = Mat.zeros(original.size(), CvType.CV_8UC3);
        for(int i = 0; i < contornos.size(); i++){
            Scalar color = new Scalar(180, 170, 5);
//            Imgproc.drawContours(resultado, contornos, i, color, 2, 8, jerarquia, 0, new Point());
            Core.circle(resultado, mc.get(i), 4, color, -1, 8, 0);
        }
        imagenVertices = resultado;
        return mc;
    }
    
    
    private Mat calcularBinaria(Mat original){
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(original, imGrises, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Canny(imGrises, bw, 100, 150, 5, true);
        Imgproc.GaussianBlur(imGrises, bw, new Size(5,5), 0);
        Imgproc.threshold(bw, bw, 200, 250, Imgproc.THRESH_BINARY_INV);
        Imgproc.blur(bw, bw, new Size(15,15));
        return bw;
//        dilate();
    }
    
    private Mat erode(Mat original){
        Mat destino = new Mat();
        Mat kernel = Imgproc.getStructuringElement(1, new Size(3,3));
        Imgproc.erode(original, destino, kernel);
        return destino;
    }
    
    private Mat dilate(Mat original){
        Mat destino = new Mat();
        Mat kernel = Imgproc.getStructuringElement(1, new Size(3,3));
        Imgproc.dilate(original, destino, kernel);
        return destino;
    }


}
