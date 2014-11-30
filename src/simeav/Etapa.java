/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simeav;

/**
 *
 * @author Nacha
 */
public enum Etapa {
    ORIGINAL("Original"), 
    PREPROCESADA("Escalada y umbralada"), 
    BINARIZADA("Binaria"),
    TEXTO("Texto"),
    SIN_TEXTO("Imagen sin texto"), 
    MODULOS("Modulos detectados"),
    CONECTORES("Conectores detectados"),
    EXTREMOS("Extremos de los conectores"),
    GRAFO("Grafo resultante");
    
    private String nombre;
    
    private Etapa(String nombre){
        this.nombre = nombre;
    }
    
    public String toString(){
        return nombre;
    }
}
