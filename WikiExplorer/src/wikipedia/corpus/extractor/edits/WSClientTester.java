/*
 * Prüft nur, ob Daten in HBase gespeichert und abgerufen werden können. 
 */
package wikipedia.corpus.extractor.edits;

import ws.cache.*;

/**
 *
 * @author kamir
 */
public class WSClientTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HBaseCacheService service = new HBaseCacheService ();
        TSCacheV2 proxy = service.getTSCacheV2Port();

        // add
        int i = proxy.add(656, 8);
        System.out.println( i );
        javax.swing.JOptionPane.showMessageDialog(null, i );

        // init
        proxy.init();
        
        // put
        proxy.put( "Mirko", "Kämpf" );
        
        //get
        String s = proxy.get("Mirko");
        System.out.println(  s );
        javax.swing.JOptionPane.showMessageDialog(null, new String(s) );     
    }
}
