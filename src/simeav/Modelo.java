/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import simeav.grafo.Diagrama;
import simeav.grafo.Modulo;
import simeav.grafo.Conector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import simeav.filtros.DetectorConectores;
import simeav.filtros.DetectorModulos;
import simeav.filtros.Preprocesador;
import simeav.filtros.SeparadorTexto;
import simeav.filtros.instanciaciones.DetectorConectoresEstandar;
import simeav.filtros.instanciaciones.DetectorModulosEstandar;
import simeav.filtros.instanciaciones.PreprocesadorEstandar;
import simeav.filtros.instanciaciones.SeparadorTextoEstandar;

/**
 *
 * @author Nacha
 */
public class Modelo extends Observable{

    private Diagrama diagrama;
    private HashMap<Etapa, Mat> imagenes;
    private boolean inicializado;
    private SeparadorTexto sepTexto = new SeparadorTextoEstandar();
    private Preprocesador preprocesador = new PreprocesadorEstandar()
    private DetectorModulos detectorModulos = new DetectorModulosEstandar();
    private DetectorConectores detectorConectores = new DetectorConectoresEstandar();


    Modelo(){
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

    Mat preprocesar(int umbral){
        imagenes.put(Etapa.PREPROCESADA, preprocesador.preprocesar(imagenes.get(Etapa.ORIGINAL), umbral));
        return imagenes.get(Etapa.PREPROCESADA);
    }

    public Mat separarTexto(int lados){
        sepTexto.separarTexto(imagenes.get(Etapa.PREPROCESADA), lados);
        imagenes.put(Etapa.TEXTO, Utils.borrarMascara(imagenes.get(Etapa.PREPROCESADA), sepTexto.getImagenTexto()));
        imagenes.put(Etapa.SIN_TEXTO, sepTexto.getImagenSinTexto());
        return imagenes.get(Etapa.SIN_TEXTO);
    }

    void procesar(){
//        imagenes.put(Etapa.TEXTO, borrarMascara(imagenes.get(Etapa.PREPROCESADA), sepTexto.getImagenTexto()));
//        imagenes.put(Etapa.SIN_TEXTO, sepTexto.getImagenSinTexto());
        imagenes.put(Etapa.BINARIZADA, calcularBinaria(imagenes.get(Etapa.SIN_TEXTO)));
        imagenes.put(Etapa.MODULOS, detectorModulos.detectarModulos(imagenes.get(Etapa.BINARIZADA), diagrama));
        imagenes.put(Etapa.CONECTORES, detectorConectores.detectarConectores(imagenes.get(Etapa.SIN_TEXTO), imagenes.get(Etapa.MODULOS), diagrama));
        imagenes.put(Etapa.EXTREMOS, detectorConectores.getExtremos());
        imagenes.put(Etapa.GRAFO, dibujarGrafo());
        setChanged();
        notifyObservers();
    }


    private Mat dibujarGrafo(){
        Mat grafo = new Mat(imagenes.get(Etapa.PREPROCESADA).size(), CvType.CV_8UC3, new Scalar(255,255,255));
        ArrayList<Modulo> modulos = diagrama.getModulos();
        for (Modulo modulo : modulos) {
            Rect rect = modulo.getRectangulo();
            Core.rectangle(grafo, rect.tl(), rect.br(), new Scalar(182,170,5), 3);
            Core.putText(grafo, modulo.getNombre(), new Point(rect.tl().x + 20, rect.tl().y + 20), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(175, 180, 5), 2);
        }
        ArrayList<Conector> conectores = diagrama.getConectores();
        for (Conector c : conectores) {
            Core.line(grafo, c.getDesde(), c.getHasta(), new Scalar(180,170, 5), 2);
            String tipo = c.getTipo();
            switch (tipo) {
                case "Usa":
                    Core.circle(grafo, c.getHasta(), 6, new Scalar(0, 0, 255), -1);
                    break;
                case "Agregacion":
                    Core.circle(grafo, c.getHasta(), 6, new Scalar(255, 255, 0), -1);
                    break;
                case "Extension":
                    Core.circle(grafo, c.getHasta(), 6, new Scalar(255, 0, 0), -1);
            }
        }
        return grafo;
    }

    void guardar(File selectedFile) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(selectedFile.getAbsolutePath(), "UTF-8");
            ArrayList<Modulo> modulos = diagrama.getModulos();
            writer.println("Módulos:");
            for (Modulo modulo : modulos) {
                writer.println("    " + modulo.getNombre());
            }
            writer.println("Conectores:");
            ArrayList<Conector> conectores = diagrama.getConectores();
            for (Conector c : conectores) {
                writer.println("    Conector " + c.getId() + ":");
                writer.println("      Desde: " + c.getModuloDesde().getNombre());
                writer.println("      Hasta: " + c.getModuloHasta().getNombre());
                writer.println("      Tipo: " + c.getTipo());
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean inicializado(){
        return inicializado;
    }

    HashMap<Etapa, Mat> getImagenes(){
        return imagenes;
    }


    private Mat calcularBinaria(Mat original){
        Mat imGrises = new Mat();
        Mat bw = new Mat();
        Imgproc.cvtColor(original, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imGrises, bw, new Size(5,5), 0);
        Imgproc.threshold(bw, bw, 200, 250, Imgproc.THRESH_BINARY_INV);
        return bw;
    }


}
