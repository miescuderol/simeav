/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simeav;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

/**
 *
 * @author Nacha
 */
public class InterfazGrafica extends javax.swing.JFrame {

    /**
     * Creates new form InterfazGrafica
     */
    public InterfazGrafica() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rootArbolResultados = new DefaultMutableTreeNode("Root");
        arbolResultados = new javax.swing.JTree(rootArbolResultados);
        botonBinaria = new javax.swing.JButton();
        botonCuadrados = new javax.swing.JButton();
        botonLineas = new javax.swing.JButton();
        textoTH = new javax.swing.JTextField();
        botonVertices = new javax.swing.JButton();
        textoBS = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textoAS = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        textoK = new javax.swing.JTextField();
        botonErode = new javax.swing.JButton();
        botonDilate = new javax.swing.JButton();
        botonContornos = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelVisualizador = new javax.swing.JPanel();
        imagen = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItemAbrir = new javax.swing.JMenuItem();
        menuItemGuardar = new javax.swing.JMenuItem();
        menuItemSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane2.setViewportView(arbolResultados);

        botonBinaria.setText("Binaria");
        botonBinaria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonBinariaMouseClicked(evt);
            }
        });

        botonCuadrados.setText("Cuadrados");
        botonCuadrados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonCuadradosMouseClicked(evt);
            }
        });

        botonLineas.setText("Lineas");
        botonLineas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonLineasMouseClicked(evt);
            }
        });

        textoTH.setText("0");

        botonVertices.setText("Vertices");
        botonVertices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonVerticesMouseClicked(evt);
            }
        });

        textoBS.setText("2");

        jLabel1.setText("Block size");

        jLabel2.setText("Aperture size");

        textoAS.setText("0");

        jLabel3.setText("k");

        textoK.setText("0.04");

        botonErode.setText("E");
        botonErode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonErodeMouseClicked(evt);
            }
        });

        botonDilate.setText("D");
        botonDilate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonDilateMouseClicked(evt);
            }
        });

        botonContornos.setText("Contornos");
        botonContornos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                botonContornosMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonBinaria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonCuadrados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonLineas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonVertices, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textoTH)
                    .addComponent(textoBS)
                    .addComponent(textoAS)
                    .addComponent(textoK)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(botonErode, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonDilate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(botonContornos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonBinaria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonCuadrados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonLineas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonVertices)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonContornos)
                .addGap(2, 2, 2)
                .addComponent(textoTH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textoBS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textoAS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textoK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonErode)
                    .addComponent(botonDilate))
                .addGap(0, 42, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel2);

        imagen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        imagen.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        imagen.setDoubleBuffered(true);

        javax.swing.GroupLayout panelVisualizadorLayout = new javax.swing.GroupLayout(panelVisualizador);
        panelVisualizador.setLayout(panelVisualizadorLayout);
        panelVisualizadorLayout.setHorizontalGroup(
            panelVisualizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVisualizadorLayout.createSequentialGroup()
                .addComponent(imagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 759, Short.MAX_VALUE))
        );
        panelVisualizadorLayout.setVerticalGroup(
            panelVisualizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelVisualizador);

        jSplitPane2.setRightComponent(jScrollPane1);

        jMenu1.setText("Archivo");

        menuItemAbrir.setText("Abrir");
        menuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAbrirActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemAbrir);

        menuItemGuardar.setText("Guardar");
        menuItemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemGuardarActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemGuardar);

        menuItemSalir.setText("Salir");
        jMenu1.add(menuItemSalir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAbrirActionPerformed
        int returnVal = selectorArchivos.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           Mat im;
           modelo.setImagenOriginal(selectorArchivos.getSelectedFile(), Integer.parseInt(textoTH.getText()));
           im = modelo.getImagenCuadrados();
           mostrar(im);
           rootArbolResultados.add(arImagenOriginal);
        }
    }//GEN-LAST:event_menuItemAbrirActionPerformed

    private void menuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGuardarActionPerformed
        int returnVal = selectorArchivos.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           modelo.guardar(selectorArchivos.getSelectedFile());
        }
    }//GEN-LAST:event_menuItemGuardarActionPerformed

    private void botonBinariaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonBinariaMouseClicked
        mostrar(modelo.getImagenBinaria());
    }//GEN-LAST:event_botonBinariaMouseClicked

    private void botonCuadradosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonCuadradosMouseClicked
        mostrar(modelo.getImagenCuadrados());
    }//GEN-LAST:event_botonCuadradosMouseClicked

    private void botonLineasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonLineasMouseClicked
        mostrar(modelo.getImagenLineas(Integer.parseInt(textoTH.getText())));
    }//GEN-LAST:event_botonLineasMouseClicked

    private void botonVerticesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonVerticesMouseClicked
        int bs = Integer.parseInt(textoBS.getText());
        int as = Integer.parseInt(textoAS.getText());
        double k = Double.parseDouble(textoK.getText());
        mostrar(modelo.getImagenVertices(bs, as, k));
    }//GEN-LAST:event_botonVerticesMouseClicked

    private void botonErodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonErodeMouseClicked
        mostrar(modelo.getErode());
    }//GEN-LAST:event_botonErodeMouseClicked

    private void botonDilateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonDilateMouseClicked
        mostrar(modelo.getDilate());
    }//GEN-LAST:event_botonDilateMouseClicked

    private void botonContornosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botonContornosMouseClicked
        mostrar(modelo.getContornos());
    }//GEN-LAST:event_botonContornosMouseClicked

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazGrafica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new InterfazGrafica().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DefaultMutableTreeNode rootArbolResultados;
    private javax.swing.JTree arbolResultados;
    private javax.swing.JButton botonBinaria;
    private javax.swing.JButton botonContornos;
    private javax.swing.JButton botonCuadrados;
    private javax.swing.JButton botonDilate;
    private javax.swing.JButton botonErode;
    private javax.swing.JButton botonLineas;
    private javax.swing.JButton botonVertices;
    private javax.swing.JLabel imagen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JMenuItem menuItemAbrir;
    private javax.swing.JMenuItem menuItemGuardar;
    private javax.swing.JMenuItem menuItemSalir;
    private javax.swing.JPanel panelVisualizador;
    private javax.swing.JTextField textoAS;
    private javax.swing.JTextField textoBS;
    private javax.swing.JTextField textoK;
    private javax.swing.JTextField textoTH;
    // End of variables declaration//GEN-END:variables
    // Elementos del arbolResultados
    private DefaultMutableTreeNode arImagenOriginal = new DefaultMutableTreeNode("Imagen original", false);
    private DefaultMutableTreeNode arResultado;
    private JFileChooser selectorArchivos = new JFileChooser();
    private Simeav modelo = new Simeav();

    private void mostrar(Mat im) {
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", im, matOfByte); 
        byte[] byteArray = matOfByte.toArray();
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage bufImage = ImageIO.read(in);
            ImageIcon image = new ImageIcon(bufImage);
            imagen.setIcon(image);
        } catch (IOException e) {
        }
    }
}
