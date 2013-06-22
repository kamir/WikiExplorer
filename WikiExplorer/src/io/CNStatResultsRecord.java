/*
 *  Für jede CN werden hier die Ergebnisse als KeyValue Paare abgelegt.
 * 
 *  Man erhält somit eine Tabelle mit "Node" Properties die aber z.B. auch
 *  auf Eigenschaften der Umgebung oder des Umgebenden Netzwerkes darstellen 
 *  können.
 * 
 *  Wir nutzen in einer Studie eine Datei mit dem Namen 
 * 
 */
package io;

import chart.simple.MultiChart;
import data.series.Messreihe;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.JComponent;
import wikipedia.corpus.extractor.WikiStudieMetaData;
import wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class CNStatResultsRecord {

    
    int i = 0;
    String studie = null;
    WikiStudieMetaData w = null;
    
    public CNStatResultsRecord(int _i, WikiStudieMetaData wd ) { 
        i = _i; 
        w = wd;
        studie = wd.getName();
    }
 
    Hashtable<String, Double> data = new Hashtable<String,Double>(); 
    void setResult(String t, double d) {
        data.put(t, d);
    }

    DecimalFormat df = new DecimalFormat( "0.0000" );
    void print() {
        System.out.print( i +"\t" + studie + "\t" + w.getWn().elementAt(i) + "\t");
        
        for( String k : data.keySet() ) { 
            System.out.print( df.format( data.get(k) ) + "\t" );
        }
        System.out.println();
    }
}
