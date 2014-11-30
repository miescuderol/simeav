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
import simeav.filtros.Preprocesador;
import simeav.filtros.SeparadorTexto;
import simeav.filtros.instanciaciones.PreprocesadorEstandar;
import simeav.filtros.instanciaciones.SeparadorTextoEstandar;


/**
 *
 * @author Nacha
 */
public class Simeav extends Observable{
 
    private Diagrama diagrama;
    private Map<Integer, Rect> rectangulos;
    private HashMap<Etapa, Mat> imagenes;
    private boolean inicializado;
    private SeparadorTexto sepTexto = new SeparadorTextoEstandar();
    private Preprocesador preprocesador = new PreprocesadorEstandar();
    private Mat extremos;

    
    Simeav(){
        diagrama = new Diagrama();
        inicializado = false;
        
    }
    
    void setImagenOriginal(File selectedFile) {
        diagrama = new Diagrama();
        imagenes = new HashMap<>();
        imagenes.put(Etapa.ORIGINAL, Highgui.imread(selectedFile.getAbsolutePath()));
//        imagenes.put(Etapa.PREPROCESADA, new InfoImagen("Preprocesada",preprocesador.preprocesar(imagenes.get(Etapa.ORIGINAL).getImagen())));
        inicializado = true;
    }
    
    
    void procesarModulos(){
//        imagenes.put(Etapa.TEXTO, borrarMascara(imagenes.get(Etapa.PREPROCESADA), sepTexto.getImagenTexto()));
//        imagenes.put(Etapa.SIN_TEXTO, sepTexto.getImagenSinTexto());
        imagenes.put(Etapa.BINARIZADA, calcularBinaria(imagenes.get(Etapa.SIN_TEXTO)));
        imagenes.put(Etapa.MODULOS, detectarCuadrados(imagenes.get(Etapa.BINARIZADA)));
        imagenes.put(Etapa.CONECTORES, detectarConectores(imagenes.get(Etapa.SIN_TEXTO), imagenes.get(Etapa.MODULOS)));
        imagenes.put(Etapa.EXTREMOS, Utils.borrarMascara(imagenes.get(Etapa.SIN_TEXTO), this.extremos));
        imagenes.put(Etapa.GRAFO, dibujarGrafo());
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

    boolean inicializado(){
        return inicializado;
    }
    
    HashMap<Etapa, Mat> getImagenes(){
        return imagenes;
    }
    
    Mat preprocesar(int umbral){
        imagenes.put(Etapa.PREPROCESADA, preprocesador.preprocesar(imagenes.get(Etapa.ORIGINAL), umbral));
        return imagenes.get(Etapa.PREPROCESADA);
    }
    
    
    Mat dibujarGrafo(){
        Mat grafo = Mat.zeros(imagenes.get(Etapa.PREPROCESADA).size(), CvType.CV_8UC3);
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

    public Mat separarTexto(int lados){
        sepTexto.separarTexto(imagenes.get(Etapa.PREPROCESADA), lados);
        imagenes.put(Etapa.TEXTO, Utils.borrarMascara(imagenes.get(Etapa.PREPROCESADA), sepTexto.getImagenTexto()));
        imagenes.put(Etapa.SIN_TEXTO, sepTexto.getImagenSinTexto());
        return imagenes.get(Etapa.SIN_TEXTO);
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
    
    private Mat calcularBinaria2(Mat original){
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(original, imGrises, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Canny(imGrises, bw, 100, 150, 5, true);
        Imgproc.GaussianBlur(imGrises, bw, new Size(1,1), 0);
        Imgproc.threshold(bw, bw, 100, 250, Imgproc.THRESH_BINARY);
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



    private Mat detectarConectores(Mat original, Mat imagenCuadrados) {
        Mat sinCuadrados = Utils.borrarMascara(original, imagenCuadrados);
        // dilato los conectores para que se superpongan con los cuadrados
        sinCuadrados = dilate(sinCuadrados);
        sinCuadrados = dilate(sinCuadrados);
        sinCuadrados = dilate(sinCuadrados);
        //elimino puntos que pueden haber quedado de la eliminacion de cuadrados
        ArrayList<MatOfPoint> contornos = Utils.detectarContornos(sinCuadrados);
        for(int i = 0; i < contornos.size(); i++){
            double area = Imgproc.contourArea(contornos.get(i));
            if(area <= 50){
                Imgproc.drawContours(sinCuadrados, contornos, i, new Scalar(0, 0, 0), -1);
            }
        }
      
        this.extremos = Mat.zeros(sinCuadrados.size(), CvType.CV_8U);
        // Imagen en la que se va a dibuja el resultado
        Mat conectores = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
        Mat contorno;
        contornos = Utils.detectarContornos(sinCuadrados);
        Mat intersec = new Mat();

        ArrayList<MatOfPoint> contornos_intersec = new ArrayList<>();
        int r, g, b;
        for(int j = contornos.size() - 1; j >= 0; j --){
            //Dibujo el contorno relleno, para despues sacar la interseccion con los cuadrados
            contorno = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
            Imgproc.drawContours(contorno, contornos, j, new Scalar(180, 255, 255), -1);
            Imgproc.cvtColor(contorno, contorno, Imgproc.COLOR_BGR2GRAY);
            //Calculo la interseccion con los cuadrados (se dibujan en intersec)
            Core.bitwise_and(contorno, imagenCuadrados, intersec);
            //Saco los contornos de las intersecciones para saber donde estan
            contornos_intersec = Utils.detectarContornos(intersec);
            if(contornos_intersec.size() > 1){
                Scalar color = Utils.getColorRandom();
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
                Point tl_desde = new Point(c.getDesde().x - 20, c.getDesde().y - 20);
                Point br_desde = new Point(c.getDesde().x + 20, c.getDesde().y + 20);
                Point tl_hasta = new Point(c.getHasta().x - 20, c.getHasta().y - 20);
                Point br_hasta = new Point(c.getHasta().x + 20, c.getHasta().y + 20);
                Core.rectangle(this.extremos, tl_desde, br_desde, new Scalar(255, 255, 255), -1);
                Core.rectangle(this.extremos, tl_hasta, br_hasta, new Scalar(255, 255, 255), -1);
            }
        }
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
                if(Utils.conecta(rectangulo, extremos.get(i))){
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
        i = modulos_conectados.size();
        if(i >= 2){
            Modulo m1 = (Modulo) modulos_conectados.get(0);
            Modulo m2 = (Modulo) modulos_conectados.get(1);
            diagrama.addConector(id_conector, m1, m2, extremos.get(0), extremos.get(1)); 
        }
    }

}
