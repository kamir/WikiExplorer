package io;

import data.series.Messreihe;
import java.io.EOFException;
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
import wikipedia.explorer.data.WikiNode;

public class WikiNodeCacheEntry {

    public String key;
    public Messreihe mr;

    public void _store(ObjectOutputStream store) throws IOException {
        store.writeUTF(key);
        store.writeUTF(mr.getLabel());
        store.writeUTF(mr.getIdentifier());
        store.writeObject(mr.getLabel_X());
        store.writeObject(mr.getLabel_Y());
        store.writeObject(mr.xValues);
        store.writeObject(mr.yValues);
    }

    public int load(ObjectInputStream store, Hashtable<String, Messreihe> c, Calendar von, Calendar bis) throws IOException, ClassNotFoundException {

//        System.out.println("***** LOAD ???");

        int i = 0;

        boolean goOn = true;
        while ( goOn ) {

            goOn = loadMore(store, c, von, bis);

            i = i + 1;
        }

        return i;
    }
    
    public boolean loadMore(ObjectInputStream store, Hashtable<String, Messreihe> c, Calendar von, Calendar bis) throws IOException, ClassNotFoundException {
        boolean v;
        try {
        
            String key = (String) store.readUTF();
            String label = (String) store.readUTF();
            String id = (String) store.readUTF();
            String labelx = (String) store.readObject();
            String labely = (String) store.readObject();
            Object x = store.readObject();
            Object y = store.readObject();

            Messreihe mr = new Messreihe();
            mr.setLabel(label);
            mr.setIdentifier(id);
            mr.setLabel_X(labelx);
            mr.setLabel_Y(labely);
            mr.xValues = (Vector)x;
            mr.yValues = (Vector)y;

        
     
            String k = tscache.TSCache.getKey(mr, von , bis );
            c.put(k, mr);
            
            v = true;
            
        } catch (EOFException ex) {
            
            v = false;
            System.out.println( "EOF" );            

        }
        catch (IOException ex) {
            Logger.getLogger(WikiNodeCacheEntry.class.getName()).log(Level.SEVERE, null, ex);
            v = false;
            

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WikiNodeCacheEntry.class.getName()).log(Level.SEVERE, null, ex);
            v = false; 

        }

        return v;
    }


}
