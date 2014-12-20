/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav.filtros.instanciaciones;

import static java.lang.Math.abs;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import simeav.Conector;
import simeav.Diagrama;
import simeav.Modulo;
import simeav.Utils;
import simeav.filtros.DetectorConectores;

/**
 *
 * @author Nacha
 */
public class DetectorConectoresEstandar extends DetectorConectores{

    @Override
    public Mat detectarConectores(Mat original, Mat mascaraModulos, Diagrama diagrama) {
        Mat sinCuadrados = Utils.borrarMascara(original, mascaraModulos);
        // dilato los conectores para que se superpongan con los cuadrados
        sinCuadrados = Utils.dilate(sinCuadrados);
        sinCuadrados = Utils.dilate(sinCuadrados);
        sinCuadrados = Utils.dilate(sinCuadrados);
        //elimino puntos que pueden haber quedado de la eliminacion de cuadrados
        ArrayList<MatOfPoint> contornos = Utils.detectarContornos(sinCuadrados);
        for(int i = 0; i < contornos.size(); i++){
            double area = Imgproc.contourArea(contornos.get(i));
            if(area <= 50){
                Imgproc.drawContours(sinCuadrados, contornos, i, new Scalar(0, 0, 0), -1);
            }
        }
      
        this.extremos = original.clone();
        Mat mascara;
        String tipo_extremo1, tipo_extremo2;
        // Imagen en la que se va a dibuja el resultado
        Mat conectores = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
        Mat contorno;
        contornos = Utils.detectarContornos(sinCuadrados);
        Mat intersec = new Mat();

        ArrayList<MatOfPoint> contornos_intersec;
        int r, g, b;
        for(int j = contornos.size() - 1; j >= 0; j --){
            //Dibujo el contorno relleno, para despues sacar la interseccion con los cuadrados
            contorno = Mat.zeros(sinCuadrados.size(), CvType.CV_8UC3);
            Imgproc.drawContours(contorno, contornos, j, new Scalar(180, 255, 255), -1);
            Imgproc.cvtColor(contorno, contorno, Imgproc.COLOR_BGR2GRAY);
            //Calculo la interseccion con los cuadrados (se dibujan en intersec)
            Core.bitwise_and(contorno, mascaraModulos, intersec);
            //Saco los contornos de las intersecciones para saber donde estan
            contornos_intersec = Utils.detectarContornos(intersec);
            if(contornos_intersec.size() > 1){
                Scalar color = Utils.getColorRandom();
                for(int z = 0; z < contornos_intersec.size(); z++){
                    Imgproc.drawContours(conectores, contornos_intersec, z, color, -1);
                }
                ArrayList<Point> centros_extremos = Utils.getCentros(contornos_intersec);
                for (Point centros_extremo : centros_extremos) {
                    Core.circle(conectores, centros_extremo, 4, color, -1);
                }
                analizarExtremos(j, centros_extremos, diagrama);
                Conector c = diagrama.getConector(j);
                
                Core.rectangle(conectores, c.getModuloDesde().getRectangulo().tl(), c.getModuloDesde().getRectangulo().br(), color, 3);
                Core.rectangle(conectores, c.getModuloHasta().getRectangulo().tl(), c.getModuloHasta().getRectangulo().br(), color, 3);
                Point tl_desde = new Point(c.getDesde().x - 20, c.getDesde().y - 20);
                Point br_desde = new Point(c.getDesde().x + 20, c.getDesde().y + 20);
                Point tl_hasta = new Point(c.getHasta().x - 20, c.getHasta().y - 20);
                Point br_hasta = new Point(c.getHasta().x + 20, c.getHasta().y + 20);
                mascara = new Mat(sinCuadrados.size(), CvType.CV_8U, new Scalar(255, 255, 255));
                Core.rectangle(mascara, tl_desde, br_desde, new Scalar(0, 0, 0), -1);
                tipo_extremo1 = clasificarExtremo(Utils.borrarMascara(original, mascara));
                
                mascara = new Mat(sinCuadrados.size(), CvType.CV_8U, new Scalar(255, 255, 255));
                Core.rectangle(mascara, tl_hasta, br_hasta, new Scalar(0, 0, 0), -1);
                tipo_extremo2 = clasificarExtremo(Utils.borrarMascara(original, mascara));
                if (!tipo_extremo1.equals(tipo_extremo2)){
                    if (tipo_extremo1.equals("Normal"))
                        c.setTipo(tipo_extremo2);
                    else if (tipo_extremo2.equals("Normal")){
                        Modulo aux = c.getModuloDesde();
                        c.setDesde(c.getModuloHasta());
                        c.setHacia(aux);
                        Point p_aux = c.getDesde();
                        c.setDesde(c.getHasta());
                        c.setHasta(p_aux);
                        c.setTipo(tipo_extremo1);
                    }
                    else {
                        c.setTipo("Indeterminado");
                    }
                } else {
                    c.setTipo("Indeterminado");
                }
            }
        }
        return conectores;
    }
    
    
    
    private void analizarExtremos(Integer id_conector, ArrayList<Point> extremos, Diagrama diagrama) {
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

    private String clasificarExtremo(Mat extremo) {
        String tipo_extremo = "Normal";
        extremo = Utils.dilate(extremo);
        extremo = Utils.dilate(extremo);
        extremo = Utils.dilate(extremo);
        extremo = Utils.dilate(extremo);
        ArrayList<MatOfPoint> contornos = Utils.detectarContornos(extremo);
        double area = 0;
        for(int i = 0; i < contornos.size(); i++){
            area += Imgproc.contourArea(contornos.get(i));
            
        }
        if(area <= 900){
            Imgproc.drawContours(extremos, contornos, 0, new Scalar(0, 255, 0), 2);
        }
        else if (area <= 1400){
            tipo_extremo = "Usa";
            Imgproc.drawContours(extremos, contornos, 0, new Scalar(0, 0, 255), 3);
        }
        else if (area <= 1600){
            tipo_extremo = "Extension";
            Imgproc.drawContours(extremos, contornos, 0, new Scalar(255, 0, 0), 3);
        }
        else {
            tipo_extremo = "Agregacion";
            Imgproc.drawContours(extremos, contornos, 0, new Scalar(255, 255, 0), 3);
        }
            
        return tipo_extremo;
    }
    
}
