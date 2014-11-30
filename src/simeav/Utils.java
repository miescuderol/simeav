/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Nacha
 */
public final class Utils {
    
    private Utils(){
        
    }
    
    public static ArrayList<MatOfPoint> detectarContornos(Mat original) {
        Mat jerarquia = new Mat();
        ArrayList<MatOfPoint> contornos = new ArrayList<>();
        Imgproc.findContours(original.clone(), contornos, jerarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return contornos;
    }
    
    
    public static Scalar getColorRandom() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return new Scalar(r,g,b);
    }
    
    public static Mat borrarMascara(Mat imagenOriginal, Mat mascara) {
        Mat imGrises = new Mat();
        Imgproc.cvtColor(imagenOriginal, imGrises, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imGrises, imGrises, 200, 250, Imgproc.THRESH_BINARY_INV);
        return imGrises.setTo(new Scalar(0, 0, 0), mascara);
    }
    
    public static boolean contiene(Rect rectangulo1, Rect rectangulo2){
        return rectangulo1.contains(rectangulo2.br()) && rectangulo1.contains(rectangulo2.tl());
    }
    
    public static boolean conecta(Rect modulo, Point c){
        Point tl = modulo.tl();
        Point br = modulo.br();
        return ((abs(c.y - tl.y) < 10 || abs(c.y - br.y) < 10) && c.x >= tl.x && c.x <= br.x)||
                ((abs(c.x - tl.x) < 10 || abs(c.x - br.x) < 10) && c.y >= tl.y && c.y <= br.y);
    }
    
}
