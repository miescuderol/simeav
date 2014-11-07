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
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
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
public class Simeav extends Observable{
 
    private Diagrama diagrama;
    private Map<Integer, Rect> rectangulos;
    private ArrayList<InfoImagen> imagenes;
    
    Simeav(){
        diagrama = new Diagrama();
    }
    
    void setImagenOriginal(File selectedFile) {
        diagrama = new Diagrama();
        imagenes = new ArrayList<>();
        System.out.println("archio " + selectedFile.getName());
        imagenes.add(new InfoImagen("Original", Highgui.imread(selectedFile.getAbsolutePath())));
        imagenes.add(new InfoImagen("Binaria", calcularBinaria(imagenes.get(imagenes.size()-1).getImagen())));
        imagenes.add(new InfoImagen("Cuadrados", detectarCuadrados(imagenes.get(imagenes.size()-1).getImagen())));
        imagenes.add(new InfoImagen("Conectores", detectarConectores(imagenes.get(0).getImagen(), imagenes.get(imagenes.size()-1).getImagen())));
        imagenes.add(new InfoImagen("Grafo", dibujarGrafo()));
        setChanged();
        notifyObservers();
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


    
    ArrayList<InfoImagen> getImagenes(){
        return imagenes;
    }
    
    Mat dibujarGrafo(){
        Mat grafo = Mat.zeros(imagenes.get(0).getImagen().size(), CvType.CV_8UC3);
        ArrayList<Modulo> modulos = diagrama.getModulos();
        for(int i = 0; i < modulos.size(); i++){
            Rect rect = modulos.get(i).getRectangulo();
            Core.rectangle(grafo, rect.tl(), rect.br(), new Scalar(182,170,5), 3);
        }
        ArrayList<Conector> conectores = diagrama.getConectores();
        for(int i = 0; i < conectores.size(); i++){
            Conector c = conectores.get(i);
            Core.line(grafo, c.getDesde(), c.getHasta(), new Scalar(180,170, 5), 2);
        }
        return grafo;
    }
    
    private ArrayList<MatOfPoint> detectarContornos(Mat original) {
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return contornos;
    }

    private Mat detectarCuadrados(Mat original) {
        Imgproc.blur(original, original, new Size(15,15));
        original = dilate(original);
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        ArrayList<MatOfPoint> cp = new ArrayList<>(contornos.size());
        rectangulos = new HashMap<>();
        Integer id_cuadrado = 0;
        for (int i = 0; i < contornos.size(); i++) {
            if(jerarquia.get(0, i)[3] > -1){
                MatOfPoint2f contorno2f = new MatOfPoint2f();
                contorno2f.fromList(contornos.get(i).toList());
                MatOfPoint2f c = new MatOfPoint2f();
                Imgproc.approxPolyDP(contorno2f, c, 3, true);
                cp.add(new MatOfPoint(c.toArray()));
                int lados = cp.get(cp.size()-1).height();
                if((4 <= lados) && lados < 12){
                    rectangulos.put(id_cuadrado, Imgproc.boundingRect(new MatOfPoint(c.toArray()))); 
                    id_cuadrado++;
                } 
            }
        }
        Mat resultado = Mat.zeros(original.size(), CvType.CV_8U);
        for(int i = 0; i < rectangulos.size(); i++){
            Scalar color = new Scalar(180, 170, 5);
            Scalar blanco = new Scalar(255,255,255);
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
        return mc;
    }
    
    
    private Mat calcularBinaria(Mat original){
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(original, imGrises, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Canny(imGrises, bw, 100, 150, 5, true);
        Imgproc.GaussianBlur(imGrises, bw, new Size(5,5), 0);
        Imgproc.threshold(bw, bw, 200, 250, Imgproc.THRESH_BINARY_INV);
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

    private Mat borrarCuadrados(Mat imagenOriginal, Mat imagenCuadrados) {
        Mat imGrises = new Mat();
        Imgproc.cvtColor(imagenOriginal, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imGrises, imGrises, 200, 250, Imgproc.THRESH_BINARY_INV);
        return imGrises.setTo(new Scalar(0, 0, 0), imagenCuadrados);
    }

    private Mat detectarConectores(Mat original, Mat imagenCuadrados) {
        Mat sinCuadrados = borrarCuadrados(original, imagenCuadrados);
        //elimino puntos que pueden haber quedado de la eliminacion de cuadrados
        ArrayList<MatOfPoint> contornos = detectarContornos(sinCuadrados);
        for(int i = 0; i < contornos.size(); i++){
            double area = Imgproc.contourArea(contornos.get(i));
            if(area <= 50){
                Imgproc.drawContours(sinCuadrados, contornos, i, new Scalar(0, 0, 0), -1);
            }
        }
        // dilato los conectores para que se superpongan con los cuadrados
        sinCuadrados = dilate(sinCuadrados);
        sinCuadrados = dilate(sinCuadrados);
        sinCuadrados = dilate(sinCuadrados);
      
        // Imagen en la que se va a dibuja el resultado
        Mat conectores = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
        Mat contorno;
        contornos = detectarContornos(sinCuadrados);
        Random rand = new Random();
        Mat intersec = new Mat();
        ArrayList<MatOfPoint> contornos_intersec = new ArrayList<>();
        int r, g, b;
        for(int j = 0; j < contornos.size(); j ++){
            //Dibujo el contorno relleno, para despues sacar la interseccion con los cuadrados
            contorno = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
            Imgproc.drawContours(contorno, contornos, j, new Scalar(180, 255, 255), -1);
            Imgproc.cvtColor(contorno, contorno, Imgproc.COLOR_BGR2GRAY);
            //Calculo la interseccion con los cuadrados (se dibujan en intersec)
            Core.bitwise_and(contorno, imagenCuadrados, intersec);
            //Saco los contornos de las intersecciones para saber donde estan
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
                    Core.circle(conectores, extremos.get(k), 4, color, -1);
                }
                analizarExtremos(j, extremos);
                Conector c = diagrama.getConector(j);
                Core.rectangle(conectores, c.getModuloDesde().getRectangulo().tl(), c.getModuloDesde().getRectangulo().br(), color, 3);
                Core.rectangle(conectores, c.getModuloHasta().getRectangulo().tl(), c.getModuloHasta().getRectangulo().br(), color, 3);
            }
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
        ArrayList<Modulo> modulos_conectados = new ArrayList<>();
        for(int i = 0; i < extremos.size(); i++){
            for(int j = 0; j < modulos.size(); j++){
                Rect rectangulo = modulos.get(j).getRectangulo();
                if(extremos.get(i).inside(rectangulo)){
                    modulos_conectados.add(modulos.get(j));
                }
            }
        }
        int i = modulos_conectados.size();
        if(i > 2){
            int j = 0;
            int k = 1;
            Modulo m1, m2;
            Point p1, p2;
            while(j < extremos.size()){
                m1 = modulos_conectados.get(j);
                m2 = modulos_conectados.get(k);
                p1 = extremos.get(j);
                p2 = extremos.get(k);
                if((m1.equals(m2)) && ((abs(p1.x - p2.x) < 0.1) || (abs(p1.y - p2.y) < 0.1))){
                    Point centro = new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2);
                    extremos.remove(p1);
                    extremos.remove(p2);
                    extremos.add(centro);
                    modulos_conectados.remove(m1);
                    j = extremos.size();
                    k = j + 1;
                } else if(k == extremos.size() - 1){
                    j = k;
                    k = 0;
                } else {
                    j++;
                    k++;
                }
            }
        }
        Modulo m1 = (Modulo) modulos_conectados.get(0);
        Modulo m2 = (Modulo) modulos_conectados.get(1);
        diagrama.addConector(id_conector, m1, m2, extremos.get(0), extremos.get(1));   
    }
}
