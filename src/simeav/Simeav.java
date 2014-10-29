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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
    private Diagrama diagrama;
    private Map<Integer, Rect> rectangulos;
    
    Simeav(){
        diagrama = new Diagrama();
    }
    
    Mat setImagenOriginal(File selectedFile, int th) {
        imagenOriginal = Highgui.imread(selectedFile.getAbsolutePath());
        imagenBinaria = calcularBinaria(imagenOriginal);
        imagenBinaria = dilate(imagenBinaria);
        imagenCuadrados = detectarCuadrados(imagenBinaria);
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
    
    Mat getConectores() {
        return detectarConectores(borrarCuadrados());
    }
    
    Mat dibujarGrafo(){
        Mat grafo = Mat.zeros(imagenBinaria.size(), CvType.CV_8U);
        ArrayList<Modulo> modulos = diagrama.getModulos();
        for(int i = 0; i < modulos.size(); i++){
            Rect rect = modulos.get(i).getRectangulo();
            Core.rectangle(grafo, rect.tl(), rect.br(), new Scalar(182,170,5), 3);
        }
        int cant = diagrama.getCantConectores();
        for(int i = 0; i < cant; i++){
            Conector c = diagrama.getConector(i);
            Core.line(grafo, c.getDesde(), c.getHasta(), new Scalar(180,170, 5), 2);
        }
        return grafo;
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
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        ArrayList<MatOfPoint> cp = new ArrayList<>(contornos.size());
        rectangulos = new HashMap<>();
        Integer id_cuadrado = 0;
        for (int i = 0; i < contornos.size(); i++) {
            System.out.println(jerarquia.get(0, i)[3]);
            if(jerarquia.get(0, i)[3] > -1){
                MatOfPoint2f contorno2f = new MatOfPoint2f();
                contorno2f.fromList(contornos.get(i).toList());
                MatOfPoint2f c = new MatOfPoint2f();
                Imgproc.approxPolyDP(contorno2f, c, 3, true);
                cp.add(new MatOfPoint(c.toArray()));
                System.out.println("tamaño cp: " + cp.get(cp.size()-1).size());
                int lados = cp.get(cp.size()-1).height();
                if((4 <= lados) && lados < 12){
                    rectangulos.put(id_cuadrado, Imgproc.boundingRect(new MatOfPoint(c.toArray()))); 
                    id_cuadrado++;
                } 
            }
        }
        Mat resultado = Mat.zeros(original.size(), CvType.CV_8U);
        imagenContornos = Mat.zeros(original.size(), CvType.CV_8UC3);
        for(int i = 0; i < rectangulos.size(); i++){
            Scalar color = new Scalar(180, 170, 5);
            Scalar blanco = new Scalar(255,255,255);
            Imgproc.drawContours(imagenContornos, contornos, i, color, 1, 8, jerarquia, 0, new Point());
//            Imgproc.drawContours(resultado, cp, i, color, 1, 8, jerarquia, 0, new Point());
            Point tl = new Point(rectangulos.get(i).tl().x - 20, rectangulos.get(i).tl().y - 20);
            Point br = new Point(rectangulos.get(i).br().x + 20, rectangulos.get(i).br().y + 20);
            diagrama.addModulo(i, new Rect(tl, br));
            Core.rectangle(resultado, tl, br, blanco, -1, 8, 0);
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
        Imgproc.findContours(bw.clone(), contornos, jerarquia, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        ArrayList<Point> mc = getCentros(contornos);
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

    private Mat borrarCuadrados() {
        Mat imGrises = new Mat();
        Imgproc.cvtColor(imagenOriginal, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imGrises, imGrises, 200, 250, Imgproc.THRESH_BINARY_INV);
        return imGrises.setTo(new Scalar(0, 0, 0), imagenCuadrados);
    }

    private Mat detectarConectores(Mat sinCuadrados) {
        //elimino puntos que pueden haber quedado de la eliminacion de cuadrados
        sinCuadrados = dilate(sinCuadrados);
        sinCuadrados = dilate(sinCuadrados);
        sinCuadrados = dilate(sinCuadrados);
        ArrayList<MatOfPoint> contornos = detectarContornos(sinCuadrados);
        for(int i = 0; i < contornos.size(); i++){
            double area = Imgproc.contourArea(contornos.get(i));
            System.out.println("area contorno: " + area);
            if(area <= 50){
                Imgproc.drawContours(sinCuadrados, contornos, i, new Scalar(0, 0, 0), -1);
            }
        }
      
        Mat conectores = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
        Random rand = new Random();
        Mat contorno = Mat.zeros(imagenCuadrados.size(), CvType.CV_8UC3);
        Mat intersec = new Mat();
        contornos = detectarContornos(sinCuadrados);
        ArrayList<MatOfPoint> contornos_intersec = new ArrayList<>();
        int r, g, b;
        for(int j = 0; j < contornos.size(); j ++){
            System.out.println("j: " + j);
            Imgproc.drawContours(contorno, contornos, j, new Scalar(180, 255, 255), -1);
            Imgproc.cvtColor(contorno, contorno, Imgproc.COLOR_BGR2GRAY);
            Core.bitwise_and(contorno, imagenCuadrados, intersec);
            contornos_intersec = detectarContornos(intersec);
            if(contornos_intersec.size() > 1){
                r = rand.nextInt(256);
                g = rand.nextInt(256);
                b = rand.nextInt(256);
                Scalar color = new Scalar(r,g,b);
                for(int z = 0; z < contornos_intersec.size(); z++){
                    Imgproc.drawContours(conectores, contornos_intersec, z, color, -1);
                }
                ArrayList<Point> extremos = getCentros(contornos_intersec);
                for(int k = 0; k < extremos.size(); k++){
                    System.out.println("algo hay" + extremos.get(k).x + " " + extremos.get(k).y);
                    Core.circle(conectores, extremos.get(k), 4, color, -1);
                }
                analizarExtremos(j, extremos);
                Core.rectangle(conectores, diagrama.getConector(j).getModuloDesde().getRectangulo().tl(), diagrama.getConector(j).getModuloDesde().getRectangulo().br(), color, 3);
                Core.rectangle(conectores, diagrama.getConector(j).getModuloHasta().getRectangulo().tl(), diagrama.getConector(j).getModuloHasta().getRectangulo().br(), color, 3);
            }
            contorno = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
        }
        
//        for(int i = 0; i < contornos.size(); i++){
//            Imgproc.drawContours(conectores, contornos, i, new Scalar(256, 256, 256));
//        }
        return conectores;
    }

    
    private ArrayList<Point> getCentros(ArrayList<MatOfPoint> contornos){
        ArrayList<Moments> mu = new ArrayList<>(contornos.size());
        for(int i = 0; i < contornos.size(); i++){
            mu.add(i, Imgproc.moments(contornos.get(i), false));
        }
        ArrayList<Point> mc = new ArrayList<>(contornos.size());
        for(int i = 0; i < contornos.size(); i++){
            mc.add(i, new Point(mu.get(i).get_m10()/mu.get(i).get_m00(), mu.get(i).get_m01()/mu.get(i).get_m00()));
        }
        return mc;
    }

    private void analizarExtremos(Integer id_conector, ArrayList<Point> extremos) {
        ArrayList<Modulo> modulos = diagrama.getModulos();
        Set<Modulo> modulos_conectados = new HashSet<>();
        for(int i = 0; i < extremos.size(); i++){
            for(int j = 0; j < modulos.size(); j++){
                Rect rectangulo = modulos.get(j).getRectangulo();
                System.out.println("rectangulo " + rectangulo.tl().x + ", "
                        + rectangulo.tl().y + ", "
                        + rectangulo.br().x + ", "
                        + rectangulo.br().y );
                if(extremos.get(i).inside(rectangulo)){
                    System.out.println("el pavo esta en el saco");
                    modulos_conectados.add(modulos.get(j));
                }
            }
        }
        int i = modulos_conectados.size();
        Modulo m1 = (Modulo) modulos_conectados.toArray()[0];
        Modulo m2 = (Modulo) modulos_conectados.toArray()[i-1];
        diagrama.addConector(id_conector, m1, m2, extremos.get(0), extremos.get(extremos.size()-1));   
    }
}
