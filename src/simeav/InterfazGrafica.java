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
import java.util.ArrayList;
import java.util.Observable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

/**
 *
 * @author Nacha
 */
public class InterfazGrafica extends javax.swing.JFrame implements java.util.Observer, TreeSelectionListener{

    /**
     * Creates new form InterfazGrafica
     */
    public InterfazGrafica() {
    }
    
    public void inicializar(Simeav modelo){
        this.modelo = modelo;
        this.modelo.addObserver(this);
        initComponents();
        arbolResultados.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbolResultados.addTreeSelectionListener(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        ArrayList<InfoImagen> imagenes = modelo.getImagenes();
        for(int i = 0; i < imagenes.size(); i++){
            InfoImagen imagen = imagenes.get(i);
            if(rootArbolResultados.getChildCount() <= i){
                DefaultMutableTreeNode nodo;
                nodo = new DefaultMutableTreeNode(imagen, false);
                rootArbolResultados.add(nodo);
            }
            else {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootArbolResultados.getChildAt(i);
                node.setUserObject(imagen);
            }
        }
        mostrar(((InfoImagen)((DefaultMutableTreeNode)rootArbolResultados.getChildAt(0)).getUserObject()).getImagen());
    }    
    
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           arbolResultados.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            InfoImagen imagen = (InfoImagen)node.getUserObject();
            mostrar(imagen.getImagen());
        }
    }

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

        jPanel2.setMinimumSize(new java.awt.Dimension(130, 0));

        jScrollPane2.setViewportView(arbolResultados);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
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
           modelo.setImagenOriginal(selectorArchivos.getSelectedFile());
//           im = modelo.getImagenOriginal();
//           mostrar(im);
        }
    }//GEN-LAST:event_menuItemAbrirActionPerformed

    private void menuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGuardarActionPerformed
        int returnVal = selectorArchivos.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           modelo.guardar(selectorArchivos.getSelectedFile());
        }
    }//GEN-LAST:event_menuItemGuardarActionPerformed

 
    
    
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DefaultMutableTreeNode rootArbolResultados;
    private javax.swing.JTree arbolResultados;
    private javax.swing.JLabel imagen;
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
    // End of variables declaration//GEN-END:variables
    // Elementos del arbolResultados
    private DefaultMutableTreeNode arResultado;
    private JFileChooser selectorArchivos = new JFileChooser();
    private Simeav modelo = new Simeav();


}
