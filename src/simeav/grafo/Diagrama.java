/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav.grafo;

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
    private ArrayList<Conector> conectores;
    
    public Diagrama(){
        modulos = new ArrayList<>();
        conectores = new ArrayList<>();
    }
    
    public void addModulo(Integer id, Rect rectangulo){
        Modulo m = new Modulo(id, rectangulo);
        m.setNombre("Modulo " + id);
        modulos.add(m);
    }
    
    public Modulo getModulo(Integer id){
        Modulo ret = null;
        for(int i = 0; i < modulos.size(); i++){
            ret = modulos.get(i);
            if(ret.getId() == id){
                return ret;
            }
        }
        return ret;
    }
    
    public Conector getConector(Integer id){
        Conector ret = null;
        for(int i = 0; i < conectores.size(); i++){
            ret = conectores.get(i);
            if(ret.getId() == id){
                return ret;
            }
        }
        return ret;
    }
    
    public ArrayList<Modulo> getModulos(){
        return modulos;
    }
    
    public ArrayList<Conector> getConectores(){
        return conectores;
    }
    
    public void addConector(Integer id, Modulo desde, Modulo hacia, Point p1, Point p2){
        conectores.add(new Conector(id, desde, hacia, p1, p2, "tipo"));
        desde.addAdyacente(id, hacia);
        hacia.addAdyacente(id, desde);
    }
}
