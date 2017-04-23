/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import javax.swing.*;
import java.awt.*;
import database.Producto;
import java.util.Vector;
/**
 *
 * @author Saúl
 */
public class Impresora {
    Font fuente = new Font("Dialog", Font.PLAIN, 10);
    PrintJob pj;
    Graphics pagina;
    
    public Impresora() {
        pj = Toolkit.getDefaultToolkit().getPrintJob(new Frame(), "SCAT", null);
    }
    
    //Manda imprimirn la lista de productos recibida, agrega el total al final
    public void imprimir(Vector<Producto> p, double total)	{
        try {
            pagina = pj.getGraphics();
            pagina.setFont(fuente);
            pagina.setColor(Color.black);
            
            pagina.drawString("Categoría", 80, 60);
            pagina.drawString("Nombre", 80+80, 60);
            pagina.drawString("Cantidad", 80+(80*2), 60);
            pagina.drawString("Precio", 80+(80*3), 60);
            pagina.drawString("Check", 80+(80*4), 60);
            int i;
            for (i = 0; i < p.size(); i++) {
                int j = 0;
                pagina.drawString(p.get(i).categoria, 80+(80*j++), 60+((i+3)*15));
                pagina.drawString(p.get(i).nombre, 80+(80*j++), 60+((i+3)*15));
                pagina.drawString(p.get(i).cantidad + "", 80+(90*j++), 60+((i+3)*15));
                pagina.drawString("$" + p.get(i).precioActivo, 80+(80*j++), 60+((i+3)*15));
                pagina.drawString("\u2610", 80+(80*j++), 60+((i+3)*15));
            }
            
            pagina.drawString("Total: $" + total, 80, 60+((i+6)*15));
            
            pagina.dispose();
            pj.end();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Impresión cancelada!", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
