package tscache;

import com.thoughtworks.xstream.XStream;
import data.series.MRT;
import data.series.Messreihe;
import io.WikiNodeCacheEntry;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import wikipedia.corpus.extractor.edits.WikiHistoryExtraction2;
import wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE;
import wikipedia.explorer.data.WNT;
import wikipedia.explorer.data.WikiNode;
import ws.cache2.*;

public class TSCache {

    // lädt lokal oder aus HBase ...
    static public boolean RAM = false;
    // lädt aus dem Netz und legt im HBase ab ...
    static public boolean WebHBase = true;
//    public static void setModeFROMCache() { 
//        RAM = true;
//        WebHBase = false;
//    }
    static TSCache tsc = null;

    public static TSCache getTSCache() {
        if (tsc == null) {
            tsc = new TSCache();
            tscConnects();
        }
        return tsc;
    }
    static TSCacheV2 proxy = null;

    private static void tscConnects() {

        if (WebHBase) {
            System.out.println(">>> Connect to TSCache DB ... ");
            try {
                if (proxy == null) {
                    HBaseCacheService service = new HBaseCacheService();
                    proxy = service.getTSCacheV2Port();
                    System.out.println(">>> " + proxy.init());
                }
            } catch (Exception ex) {
                Logger.getLogger(TSCache.class.getName()).log(Level.SEVERE, null, ex);
            }

        }


        if (RAM) {
            System.out.println(">>> chache in RAM ... ");
            if (c == null) {
                c = new Hashtable<String, Messreihe>();
            }
        }

    }
    
    static public boolean debug = false;
    
    private static Hashtable<String, Messreihe> c = new Hashtable<String, Messreihe>();

    public Messreihe getMrFromCache(WikiNode wikiNode) throws IOException {

        String key = getKey(wikiNode, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());

//        System.out.println("\n[KEY] " + key );
//        System.out.println("      RAM   :" + RAM  );
//        System.out.println("      HBASE :" + WebHBase  );


        if (RAM) {
            Messreihe mr = c.get(key);
            if (mr != null) {
                // System.out.println("[RAM]");
                return mr;
            }
        }

        if (proxy == null) {
            
            TSCache.tscConnects();
        }

        if (WebHBase) {

            if (debug ) System.out.println( ">> WS-Proxy: " + proxy + " KEY:"+key );

            String value = proxy.get(key);
            Messreihe mr = null;

            if (value != null) {
                if (!value.equals("NULL")) {
                    mr = MRT._fromString(value);
                }
            }
            if (mr != null) {
                c.put(key, mr);
            }
            else { 
            }

            if (debug ) System.out.println("[HBASE]");

            return mr;
        } else {
            return null;
        }
    }
    
 
    HBaseCacheService service = null;

    public void putIntoCache(Messreihe exp1) throws IOException {
        String key = getKey(exp1, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());
        if (RAM) {
            c.put(key, exp1);
        }

        if (WebHBase) {

            if ( service == null ) service = new HBaseCacheService();
            TSCacheV2 proxy = service.getTSCacheV2Port();

            String value = MRT.getAsString(exp1);

            proxy.put(key, value);
        };
    }

    public static String getKey(Messreihe exp1, Calendar von, Calendar bis) {
        if ( von == null ) von = WikiHistoryExtractionBASE.getVon();
        if ( bis == null ) von = WikiHistoryExtractionBASE.getBis();
        
        String v = cutMillis(von);
        String b = cutMillis(bis);

//         System.out.println("(MR-Key: von=" + v + ")");

        String key = v + "___" + b + "___" + exp1.getIdentifier();
//        System.out.println(key);

        return key;
    }

    public static String getKey(WikiNode wn, Calendar von, Calendar bis) {
        
        if ( von == null ) von = WikiHistoryExtractionBASE.getVon();
        if ( bis == null ) bis = WikiHistoryExtractionBASE.getBis();
        
        
        String v = cutMillis(von);
        String b = cutMillis(bis);

        // System.out.println("(WN-Key: von=" + v + ")");

        String key = v + "___" + b + "___" + wn.getKey();
        // System.out.println( key );
        return key;
    }

    static String cutMillis(Calendar o) {
        String v = "" + o.getTimeInMillis();
//        v = v.substring( 0 , v.length() -4 ) + "0000";
        return "" + v;
    }

    public static Hashtable<String, Messreihe> getC() {
        return tsc.c;
    }

    public static void setC(Hashtable<String, Messreihe> c) {
        tsc.c = c;
        for (String s : tsc.c.keySet()) {
            System.out.println("k=> " + s);
        }
    }

//    public Hashtable<String, Messreihe> _getC(Vector<WikiNode> n) {
//        Hashtable<String, Messreihe> cSUB = new Hashtable<String, Messreihe>();
//
//        for (WikiNode node : n) {
//            String key = getKey(node, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());
//            Messreihe mr = c.get(key);
//            cSUB.put(key, mr);
//        }
//
//        return cSUB;
//    }
    public void storeC(Vector<WikiNode> n, ObjectOutputStream store) throws IOException {

        WikiNodeCacheEntry entry = new WikiNodeCacheEntry();

        for (WikiNode node : n) {

            entry.key = getKey(node, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());
            entry.mr = c.get(entry.key);

            entry._store(store);
            

        }
        store.close();
    }

    public static Calendar von;
    public static Calendar bis;
    
    public int _loadC(ObjectInputStream store) throws IOException, ClassNotFoundException {
        
        int i = 0;
        
        WikiNodeCacheEntry entry = new WikiNodeCacheEntry();
        
        i = i + entry.load( store , c , von, bis );

        System.out.println( ">>> gelesen : i=" +i );
        return i;
    }
}

