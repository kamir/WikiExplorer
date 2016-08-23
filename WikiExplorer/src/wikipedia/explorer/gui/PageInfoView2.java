/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia.explorer.gui;

import wikipedia.corpus.extractor.iwl.ExtractIWLinkCorpus;
import wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import gui.WikiCorpusClusteringView;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import jstat.data.Document;
import org.wikipedia.Wiki;
import wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class PageInfoView2 extends javax.swing.JFrame {

    /**
     * Creates new form PageInfoView
     */
    public PageInfoView2() {

        initComponents();
    }
    
    public void initContent() {

        jLabel1.setText( centerNode.wiki + " : " + centerNode.page);

        try {
            
            // catMembers listen

            StringBuffer sbCM = new StringBuffer();
            int i = 0;
            System.out.println("****************************");
            for (WikiNode memb : tool.tempCOLcatMembA ) {
                sbCM.append("A (" + i + ") " + memb.toString() + "\n");
                i++;
            }
            for (WikiNode memb : tool.tempCOLcatMembB ) {
                sbCM.append("B (" + i + ") " + memb.toString() + "\n");
                i++;
            }
            jtaCM.setText(sbCM.toString());
            zCM.setText(i + "");



            int izLINKS = 0;
            StringBuffer sbL = new StringBuffer();
            i = 0;
            System.out.println("****************************");
            for (WikiNode l : tool.tempCOLlinksA) {
                sbL.append("A (" + i + ") " + l + "\n");
                i++;
            };
            for (WikiNode l : tool.tempCOLlinksB) {
                sbL.append("B (" + i + ") " + l + "\n");
                i++;
            };
            izLINKS = i;
            zLINKS.setText(izLINKS + "");
            
            try { 
                jtaLINKS.setText(sbL.toString() );
            }
            catch(  Exception ex ) { 
                ex.printStackTrace();
                
            }
            StringBuffer sbIWL = new StringBuffer();
            i = 0;
            System.out.println("****************************");
            for (WikiNode n : tool.tempCOLiwl ) {
                i++;
                sbIWL.append("(" + i + ") " + n + "\n");
            };
            jtaIWLINKS.setText(sbIWL.toString());
            zIWL.setText(i + "");


            


//            String textMemb = wiki1.getRenderedText(memb);
//            Document doc = new Document(getUrl(wiki, memb), textMemb);
//            corpus.addDocument(doc);

            System.out.println("****************************");


            // createNetwork( iwlinks, links, catMembs, jpNETWORK, a );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setVisible(true);

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
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtaIWLINKS = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaLINKS = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaCM = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jpNETWORK = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        zIWL = new javax.swing.JLabel();
        zLINKS = new javax.swing.JLabel();
        zCM = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jtaIWLINKS.setColumns(20);
        jtaIWLINKS.setRows(5);
        jScrollPane3.setViewportView(jtaIWLINKS);

        jPanel2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("IWL", jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jtaLINKS.setColumns(20);
        jtaLINKS.setRows(5);
        jScrollPane2.setViewportView(jtaLINKS);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Links", jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jtaCM.setColumns(20);
        jtaCM.setRows(5);
        jScrollPane1.setViewportView(jtaCM);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Cat Members", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 813, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("History", jPanel5);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jpNETWORK.setBackground(java.awt.Color.white);
        jpNETWORK.setLayout(new java.awt.BorderLayout());
        jPanel6.add(jpNETWORK, java.awt.BorderLayout.CENTER);

        jLabel2.setForeground(new java.awt.Color(249, 176, 34));
        jLabel2.setText("IWL");

        jLabel3.setForeground(new java.awt.Color(31, 135, 31));
        jLabel3.setText("Links");

        jLabel4.setForeground(java.awt.Color.blue);
        jLabel4.setText("Cat Members");

        zIWL.setText("...");

        zLINKS.setText("...");

        zCM.setText("...");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(zIWL, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(zLINKS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zCM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(0, 643, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(zIWL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(zLINKS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(zCM))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel8, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab("Network", jPanel6);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    ExtractIWLinkCorpus tool = null;
    WikiNode centerNode = null;

    /**
     * @param args the command line arguments
     */
    public void open( WikiNode _centerNode, ExtractIWLinkCorpus _tool) {
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
            java.util.logging.Logger.getLogger(PageInfoView2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PageInfoView2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PageInfoView2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PageInfoView2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        tool = _tool;
        centerNode = _centerNode;
         
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel jpNETWORK;
    private javax.swing.JTextArea jtaCM;
    private javax.swing.JTextArea jtaIWLINKS;
    private javax.swing.JTextArea jtaLINKS;
    private javax.swing.JLabel zCM;
    private javax.swing.JLabel zIWL;
    private javax.swing.JLabel zLINKS;
    // End of variables declaration//GEN-END:variables

    private void createNetwork(
            HashMap<String, String> iwlinks,
            String[] links,
            String[] catMembs,
            JPanel jpNETWORK,
            String[] b) {
        try {
            WikiCorpusClusteringView wv = new WikiCorpusClusteringView();
            wv.open(iwlinks, links, catMembs, jpNETWORK, b);

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(PageInfoView2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * For a given Wikinode all LINKS are collected.
     *
     * @param wn
     * @return
     * @throws IOException
     */
    private String[] getLinks(WikiNode wn) throws IOException {
        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");

        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        return wiki1.getLinksOnPage(wn.page);
    }

    private Vector<WikiNode> getLinksVector(WikiNode wn) throws IOException {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");

        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        String[] n = wiki1.getLinksOnPage(wn.page);

        for( String s : n ) { 
            WikiNode wn2 = new WikiNode( wn.wiki, s);
            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }
}