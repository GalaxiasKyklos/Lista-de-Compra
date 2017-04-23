/*
 * Open Sourse, you MUST atribute the use of this software to de original author
 * you are free to change or modify the software as you wish
 * originaly created by Saul Ponce
 */
package database;

import gui.Main;
import java.io.File;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class CargarArchivos {
    
    
    //Carga las categorias, si no las encuentra, manda llamar crearArchivosDef() y carga la lista default
    public static Vector<String> categorias() {

        File dir = new File("archivos");
        if (dir.mkdir())
            crearArchivosDef();

        File f1 = new File(dir.getName() + "/" + "categorias.cat");
        
        Vector<String> categorias = new Vector<>();
        
        try {
            FileInputStream fi = new FileInputStream(f1);
            ObjectInputStream ois = new ObjectInputStream(fi);
            String o;
            o = (String) ois.readObject();
            while (o != null) {
               categorias.add((String) o);
               o = (String) ois.readObject();
            }
            
            ois.close();
            fi.close();            
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        return categorias;
    }
    
    
    //Crea la lista default de categorias y cinco productos default
    private static void crearArchivosDef() {
        File dir = new File("archivos");
        File f1 = new File(dir.getName() + "/" + "categorias.cat");
        
        String[] categoriasDefault = {"Abarrotes", "Bebidas", "Botanas", "Frutas y ver", "Ropa", "Regalos", "Juguetes", "Eletrónicos", 
        "Hogar", "Limpieza"};
        
        try {
            FileOutputStream fo = new FileOutputStream(f1, false);
            ObjectOutputStream oos = new ObjectOutputStream(fo);
            
            for (String s : categoriasDefault) {
                oos.writeObject(s);
            }
            
            oos.close();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Producto p[] = new Producto[5];
		
        for (int i = 0; i < p.length; i++) {
                p[i] = new Producto();
        }

        p[0].nombre = "Ariel";
        p[0].categoria = "Limpieza";
        p[0].cantidad = 1;
        p[0].precio1 = 50.0;
        p[0].precio2 = 55.0;
        p[0].precio3 = 45.0;

        p[1].nombre = "Pan blanco";
        p[1].categoria = "Abarrotes";
        p[1].cantidad = 2;
        p[1].precio1 = 15.0;
        p[1].precio2 = 20.0;
        p[1].precio3 = 18.0;

        p[2].nombre = "Atún";
        p[2].categoria = "Abarrotes";
        p[2].cantidad = 3;
        p[2].precio1 = 6.0;
        p[2].precio2 = 7.5;
        p[2].precio3 = 5.90;

        p[3].nombre = "Manzanas";
        p[3].categoria = "Frutas y ver";
        p[3].cantidad = 1;
        p[3].precio1 = 15.50;
        p[3].precio2 = 18.30;
        p[3].precio3 = 13.20;

        p[4].nombre = "Sabritas";
        p[4].categoria = "Botanas";
        p[4].cantidad = 1;
        p[4].precio1 = 10.0;
        p[4].precio2 = 11.0;
        p[4].precio3 = 10.50;

        try {

            File f = new File(dir.getName() + "/" + "productos.pct");


            FileOutputStream fo = new FileOutputStream(f, false);
            ObjectOutputStream oos = new ObjectOutputStream(fo);

            for (Producto pr : p) {
                    oos.writeObject(pr);
            }

            oos.close();
            fo.close();			
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    //Carga los productos para la base de datos principal
    public static Vector<Producto> cargarProductos() {
        Vector<Producto> p = new Vector<>();
        
        File dir = new File("archivos");
        if (dir.mkdir())
            crearArchivosDef();

        File f1 = new File(dir.getName() + "/" + "productos.pct");
        
        try {
            FileInputStream fi = new FileInputStream(f1);
            ObjectInputStream ois = new ObjectInputStream(fi);
            Producto o;
            o = (Producto) ois.readObject();
            while (o != null) {
               p.add(o);
               o = (Producto) ois.readObject();
            }
            
            ois.close();
            fi.close();            
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        return p;
    }
    
    
    //Guarda el vector sobreescribiendo la base prinicipal
    public static void archivarProductos(Vector<Producto> p) {
        
        try {
			
            File dir = new File("archivos");
            File f = new File(dir.getName() + "/" + "productos.pct");


            FileOutputStream fo = new FileOutputStream(f, false);
            ObjectOutputStream oos = new ObjectOutputStream(fo);

            for (Producto pr : p) {
                    oos.writeObject(pr);
            }

            oos.close();
            fo.close();			
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    //guarda la categorias sobreescribiendo el archivo
    public static void archivarCategorias(Vector<String> cat) {
        
        File dir = new File("archivos");
        File f1 = new File(dir.getName() + "/" + "categorias.cat");
        
        try {
            FileOutputStream fo = new FileOutputStream(f1, false);
            ObjectOutputStream oos = new ObjectOutputStream(fo);
            
            for (String s : cat) {
                oos.writeObject(s);
            }
            
            oos.close();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    //Guarda la lista recibida con ayuda de JFileChooser
    public static void guardarLista(Vector<Producto> lista, Main m) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de lista LST", "lst");
        chooser.setFileFilter(filter);
        int c = chooser.showSaveDialog(m);
        
        if (c == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile(), f1;
            if (!f.getName().endsWith(".lst"))
                f1 = new File(f.getAbsolutePath() + ".lst");
            else
                f1 = f;
            
            try {
                FileOutputStream fo = new FileOutputStream(f1, false);
                ObjectOutputStream oos = new ObjectOutputStream(fo);

                for (Producto p : lista) {
                    oos.writeObject(p);
                }

                oos.close();
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    //Carga una lista con ayuda de JFileChooser
    public static Vector<Producto> cargarLista(Main m) {
        Vector<Producto> lista = new Vector<>();
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de lista LST", "lst");
        chooser.setFileFilter(filter);
        int c =  chooser.showOpenDialog(m);
        
        if (c == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            
            if (!f.getName().endsWith(".lst"))
                return null;
            
            try {
                FileInputStream fi = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fi);
                Producto o;
                o = (Producto) ois.readObject();
                while (o != null) {
                   lista.add(o);
                   o = (Producto) ois.readObject();
                }

                ois.close();
                fi.close();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        
        return lista;
    }
}
