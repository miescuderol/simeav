/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 *
 * @author Nacha
 */
class Modulo {
    private Integer id;
    private String nombre;
    private Rect rectangulo;
    private HashMap<Integer, Modulo> adyacentes;
    
    public Modulo(Integer id, Rect rectangulo){
        this.id = id;
        this.nombre = "";
        this.rectangulo = rectangulo;
        adyacentes = new HashMap<>();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Modulo other = (Modulo) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
    
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setRectangulo(Rect rectangulo){
        this.rectangulo = rectangulo;
    }
    
    public void addAdyacente(Integer conector, Modulo modulo){
        adyacentes.put(conector, modulo);
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    
    public Rect getRectangulo(){
        return rectangulo;
    }
    
    public HashMap<Integer, Modulo> getAdyacentes(){
        return adyacentes;
    }
    
    public ArrayList<Modulo> getModulosAdyacentes(){
        return new ArrayList<>(adyacentes.values());
    }
    
    public Set<Integer> getConectoresAdyacentes(){
        return adyacentes.keySet();
    }
            
}