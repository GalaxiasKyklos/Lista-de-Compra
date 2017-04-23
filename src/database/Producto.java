/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;

public class Producto implements Serializable {
    public String nombre = "";
    public String categoria = "";
    public Integer cantidad = 0;
    public Double precio1 = 0.0;
    public Double precio2 = 0.0;
    public Double precio3 = 0.0;
    public Double precioActivo = null;
    
    
    public boolean equals(Object o) {
        if (!(o instanceof Producto))
            return false;
        Producto p = (Producto) o;
    	return this.nombre.equalsIgnoreCase(p.nombre);
    }
    
    public String toString() {
    	String s = "[" + this.categoria + "] " + this.nombre;
    	return s;
    }
}
