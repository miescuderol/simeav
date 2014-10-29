/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import java.util.ArrayList;
import java.util.HashMap;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 *
 * @author Nacha
 */
public class Diagrama {
    private ArrayList<Modulo> modulos;
    private HashMap<Integer, Conector> conectores;
    
    public Diagrama(){
        modulos = new ArrayList<>();
        conectores = new HashMap<>();
    }
    
    public void addModulo(Integer id, Rect rectangulo){
        modulos.add(new Modulo(id, rectangulo));
    }
    
    public Modulo getModulo(Integer id){
        return modulos.get(id);
    }
    
    public Conector getConector(Integer id){
        return conectores.get(id);
    }
    
    public ArrayList<Modulo> getModulos(){
        return modulos;
    }
    
    public int getCantConectores(){
        return conectores.size();
    }
    
    public void addConector(Integer id, Modulo desde, Modulo hacia, Point p1, Point p2){
        conectores.put(id, new Conector(id, desde, hacia, p1, p2, "tipo"));
        desde.addAdyacente(id, hacia);
        hacia.addAdyacente(id, desde);
    }
}
