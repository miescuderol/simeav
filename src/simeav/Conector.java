/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import org.opencv.core.Point;

/**
 *
 * @author Nacha
 */
public class Conector {
    Integer id;
    Modulo modulo_desde;
    Modulo modulo_hasta;
    Point desde;
    Point hasta;
    String tipo;
    
    Conector(Integer id, Modulo modulo_desde, Modulo modulo_hasta, Point desde, Point hasta, String tipo){
        this.id = id;
        this.modulo_desde = modulo_desde;
        this.modulo_hasta = modulo_hasta;
        this.desde = desde;
        this.hasta = hasta;
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }

    public Modulo getModuloDesde() {
        return modulo_desde;
    }

    public Modulo getModuloHasta() {
        return modulo_hasta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDesde(Modulo desde) {
        this.modulo_desde = desde;
    }

    public Point getDesde() {
        return desde;
    }

    public void setDesde(Point desde) {
        this.desde = desde;
    }

    public void setHasta(Point hasta) {
        this.hasta = hasta;
    }

    public Point getHasta() {
        return hasta;
    }

    public void setHacia(Modulo hacia) {
        this.modulo_hasta = hacia;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
