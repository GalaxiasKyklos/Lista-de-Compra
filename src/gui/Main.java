/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import database.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import tools.Impresora;

/**
 *
 * @author Saúl
 */
public class Main extends javax.swing.JFrame {  
    
    /**
     * Creates new form Main
     */
    
    JComboBox<String> mercados;
    JComboBox<String> categorias;
    Vector<Producto> productosTotales;
    Vector<Producto> listaActual;
    boolean guardado = true;
    double total;
    
    //Hecho para agregar los combobox a la tabla, listener, y windowadapter
    public void initComponents1() {
        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/gui/cart-8x.png")).getImage());
        
        listaActual = new Vector<>();
        jTextPane1.setEditable(false);
        jTextPane1.setText("Total: ");
        
        mercados = new JComboBox<>();
        mercados.addItem("Mercado 1");
        mercados.addItem("Mercado 2");
        mercados.addItem("Mercado 3");
        
        TableColumn mercadoColumn = jTable1.getColumnModel().getColumn(3);
        mercadoColumn.setCellEditor(new DefaultCellEditor(mercados));        
        
        categorias = new JComboBox<>(CargarArchivos.categorias());

        TableColumn catColumn = jTable1.getColumnModel().getColumn(0);
        catColumn.setCellEditor(new DefaultCellEditor(categorias));
        
        productosTotales = CargarArchivos.cargarProductos();
        
        DefaultTableModel mod = (DefaultTableModel) jTable1.getModel();
        
        
        mod.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                boolean ext = true;
                Object data = mod.getValueAt(row, col);
                Producto p = null;
                String nombre = (String) mod.getValueAt(row, 1);
                
                for (int i = 0; i < productosTotales.size(); i++) {
                    if (productosTotales.get(i).nombre.equalsIgnoreCase(nombre)) {
                        p = productosTotales.get(i);
                        break;
                    }
                }
                if (p == null)
                    ext = false;
                
                if (col == 0) {
                    if (!ext) return;
                    String cat = (String) data;
                    Producto ap = null;
                    if(listaActual.contains(p)) {
                        ap = listaActual.get(listaActual.indexOf(p));
                    }
                    else
                        return;
                    ap.categoria = cat;
                }
                
                if (col == 1) {
                    if (ext) {
                        if (!listaActual.contains(p)) {
                            if (p.nombre.equals("")) return;
                            mod.setValueAt(p.categoria, row, 0);
                            mod.setValueAt(p.cantidad, row, 2);
                            mod.setValueAt("Mercado 1", row, 3);
                            listaActual.add(p);
                            try {
                                mod.addRow(new Object[] {});
                            } catch (Exception ex) {}
                        }
                        else
                            mod.setValueAt("", row, 1);
                        
                    }
                    else {
                        p = new Producto();
                        p.nombre = nombre;
                        p.cantidad = 1;
                        p.categoria = "Abarrotes";
                        p.precio1 = 0.0;
                        p.precio2 = 0.0;
                        p.precio3 = 0.0;
                        if (p.nombre.equals("")) return;
                        productosTotales.add(p);
                        mod.setValueAt(p.nombre, row, 1);
                        try {
                            mod.addRow(new Object[] {});
                        } catch (Exception ex) {}
                    }
                    
                }
                if (col == 2) {
                    if (!ext) return;
                    int ctd = (int) data;
                    if (ctd < 1) {
                        ctd = 1;
                        mod.setValueAt(ctd, row, 2);
                    }
                    p.cantidad = ctd;
                }
                
                if (col == 3) {
                    if (!ext) return;
                    String cb = (String) data;
                    switch (cb.toLowerCase()) {
                        case "mercado 1":
                            mod.setValueAt(p.precio1, row, 4);
                            p.precioActivo = p.precio1;
                            break;
                        case "mercado 2":
                            mod.setValueAt(p.precio2, row, 4);
                            p.precioActivo = p.precio2;
                            break;
                        case "mercado 3":
                            mod.setValueAt(p.precio3, row, 4);
                            p.precioActivo = p.precio3;
                            break;
                    }
                }
                
                if (col == 4) {
                    if (!ext) return;
                    double precio = (double) data;
                    if (precio < 0.0) {
                        precio = 0.0;
                        mod.setValueAt(precio, row, 4);
                    }
                    String mercado = (String) mod.getValueAt(row, 3);
                    Producto ap = null;
                    if(listaActual.contains(p)) {
                        ap = listaActual.get(listaActual.indexOf(p));
                    }
                    else
                        return;
                    switch (mercado.toLowerCase()) {
                        case "mercado 1":
                            ap.precioActivo = ap.precio1 = precio;
                            break;
                        case "mercado 2":
                            ap.precioActivo = ap.precio2 = precio;
                            break;
                        case "mercado 3":
                            ap.precioActivo = ap.precio3 = precio;
                            break;
                    }
                }
                guardado = false;
                updateTotal();
                jTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
                jTable1.updateUI();
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (!guardado) {
                    int i = JOptionPane.showConfirmDialog(null, "¿Deseas continuar de todos modos?", "Lista no guardada", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (i != JOptionPane.YES_OPTION)
                        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    else
                        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                CargarArchivos.archivarProductos(productosTotales);
            }
        });
        
        jTable1.updateUI();
    }
    
    //Actualiza el total en la tabla y atributo
    private void updateTotal() {
        total = 0.0;
        if (!listaActual.isEmpty()) {
            for (int i = 0; i < listaActual.size(); i++)
                total += (listaActual.get(i).precioActivo * listaActual.get(i).cantidad);
            jTextPane1.setText("Total:" + "\n\n" + "$" + total);
        }
    }
    
    //Actualiza las categorias
    public void updateCatgs() {
        categorias.removeAllItems();
        categorias.setModel(new DefaultComboBoxModel<String>(CargarArchivos.categorias()));
        categorias.updateUI();
        TableColumn catColumn = jTable1.getColumnModel().getColumn(0);
        catColumn.setCellEditor(new DefaultCellEditor(categorias));
        jTable1.updateUI();
    }
    
    //Actualiza productosTotales
    public void updatePdcts() {
        productosTotales = CargarArchivos.cargarProductos();
        jTable1.updateUI();
    }
    
    public Main() {    
        initComponents();
        initComponents1();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lista de Compra PRO");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(600, 550));
        setResizable(false);
        setSize(new java.awt.Dimension(600, 650));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/Default.png"))); // NOI18N
        jButton1.setToolTipText("Nueva Lista");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/Printer.png"))); // NOI18N
        jButton2.setToolTipText("Imprimir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/Floppy.png"))); // NOI18N
        jButton3.setToolTipText("Guardar Lista");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/GIF.png"))); // NOI18N
        jButton4.setToolTipText("Administar Productos");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/Default_Document.png"))); // NOI18N
        jButton5.setToolTipText("Abrir Lista");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/Archive.png"))); // NOI18N
        jButton6.setToolTipText("Administrar Categorías");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jButton4.getAccessibleContext().setAccessibleDescription("Administrar Productos");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Categoría", "Nombre", "Cantidad (pz, kg, ...)", "Mercado", "Precio ($)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setRowHeight(25);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jTextPane1.setToolTipText("Total de la Lista");
        jScrollPane2.setViewportView(jTextPane1);

        jButton7.setText("Acerca de");
        jButton7.setToolTipText("Acerca de");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Eliminar");
        jButton8.setToolTipText("Eliminar seleccionado");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("Lista de Compra");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //Restablece la tabla y pregunta si se quiere guardar cuando sea necesario
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (!guardado) {
            int i = JOptionPane.showConfirmDialog(null, "¿Deseas continuar de todos modos?", "Lista no guardada", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (i != JOptionPane.YES_OPTION) return;
        }
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Categoría", "Nombre", "Cantidad (pz, kg, ...)", "Mercado", "Precio ($)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setRowHeight(25);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        guardado = true;
        initComponents1();
    }//GEN-LAST:event_jButton1ActionPerformed

    
    //Manda la lista actual a imprimir
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:    
        if (!listaActual.isEmpty()) {
            Impresora imp = new Impresora();
            imp.imprimir(listaActual, total);
        }
        else JOptionPane.showMessageDialog(null, "La lista está vacía!", "Error al imprimir", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_jButton2ActionPerformed

    
    //crea una ventana para administrar categorias
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        new AdmCat(this);
    }//GEN-LAST:event_jButton6ActionPerformed

    
    //Muestra el cuadro acerca de
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String s = "Software desarrollado por Saúl Ponce\n";
        s += "Iconos de Asher, bajo lincencia GNU";
        JOptionPane.showMessageDialog(this, s, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton7ActionPerformed

    
    //Guarda la lista actual si no esta vacía
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if (!listaActual.isEmpty()) {
            CargarArchivos.guardarLista(listaActual, this);
            guardado = true;
        }
        else
            JOptionPane.showMessageDialog(null, "La lista actual está vacía!", "Lista vacía", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed

    
    //Carga una lista, la agrega a la lista actual y actualiza la tabla.
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if (!guardado) {
            int i = JOptionPane.showConfirmDialog(null, "¿Deseas continuar de todos modos?", "Lista no guardada", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (i != JOptionPane.YES_OPTION)
                return;
            else
                jButton1ActionPerformed(evt);
        }
        Vector<Producto> tmp = CargarArchivos.cargarLista(this);
        if (tmp == null) {
            JOptionPane.showMessageDialog(null, "Tipo de archivo no permitido", "Error al cargar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Vector<String> cat = CargarArchivos.categorias();
        DefaultTableModel mod = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < tmp.size(); i++) {
            try {
                Producto p = tmp.get(i);
                
                if (!cat.contains(p.categoria)) {
                    cat.add(p.categoria);
                }
                
                if (productosTotales.contains(p)) {
                    mod.setValueAt(p.nombre, i, 1);
                    if (p.precioActivo == p.precio1) {
                        mod.setValueAt("Mercado 1", i, 3);
                    }
                    else if (p.precioActivo == p.precio2) {
                        mod.setValueAt("Mercado 2", i, 3);
                    }
                    else if (p.precioActivo == p.precio3) {
                        mod.setValueAt("Mercado 3", i, 3);
                    }
                }
                else {
                    productosTotales.add(p);
                    mod.setValueAt(p.nombre, i, 1);
                }
                
            } catch (Exception e) {}
        }
        CargarArchivos.archivarCategorias(cat);
        updateCatgs();
        guardado = true;
        jTable1.updateUI();
    }//GEN-LAST:event_jButton5ActionPerformed

    
    //Crea una ventana de administrar productos
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        CargarArchivos.archivarProductos(productosTotales);
        if (!listaActual.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No es posible administrar los productos con una lista activa", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new AdmProd(this);
    }//GEN-LAST:event_jButton4ActionPerformed

    
    //Elimina la fila seleccionada en la tabla
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        if (listaActual.isEmpty()) {
            JOptionPane.showMessageDialog(null, "La lista está vacía!", "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            DefaultTableModel mod = (DefaultTableModel) jTable1.getModel();
            int row = jTable1.getSelectedRow();
            Producto p = new Producto();
            p.nombre = (String) mod.getValueAt(row, 1);
            listaActual.remove(p);
            mod.removeRow(row);
        } catch (Exception e) {}
        updateTotal();
        jTable1.updateUI();
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
