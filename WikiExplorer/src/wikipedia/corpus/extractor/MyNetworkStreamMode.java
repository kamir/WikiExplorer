
package wikipedia.corpus.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author kamir
 */
public class MyNetworkStreamMode {
    
    private MyNetworkStreamMode() { 
         wikiIDs = new Hashtable<String,Integer>();
         nodeIDs = new Hashtable<String,Integer>();
    };
    
    static MyNetworkStreamMode stream = null;
    
    public static MyNetworkStreamMode getMyNetworkStreamMode( String studie ) { 
        if ( stream == null )  {
            stream = new MyNetworkStreamMode();
            stream.setStudienName(studie);
        }
        return stream;
    }     
    
    int cnID = 0;
    public void nextCN() { 
        cnID = cnID + 1;
    };
    
    public void addLink( MyLink2 link ) throws IOException { 
        calcUniqueNodesIDs( link );
        bw.write( link.toString() + "\n");
        bw.flush();
        i++;
    }
    
    String studie = null;
    String fn = null;
    BufferedWriter bw = null;
    int i = 0;
    
    public void init(String netn) throws IOException {
        File f = new File( netn );
        fn = netn; 
        int i = 0;
        System.out.println( ">>> create stream-mode network file: " + netn + " " + f.canWrite() );
        System.out.println( ">>> " + f.getAbsolutePath() );
       
        bw = new BufferedWriter( new FileWriter( f ) );
        bw.write( MyLink2.getHeadline() + "\n");
        bw.flush();
    }
    
    public void close() throws IOException { 
        bw.flush();
        bw.close();        
        System.out.println( ">>> Done => (" + i + ") links." );
    }
    
    public void createNodeIDListe() throws IOException {
        
        System.out.println( ">>> Create Node-ID-Lists ... [size="+MyNetworkStreamMode.getMyNetworkStreamMode(studie).nodeIDs.size()+"]" );
        
        Enumeration en = MyNetworkStreamMode.getMyNetworkStreamMode(studie).nodeIDs.keys();
        File f = new File( studie + ".ids.dict" );
        int i = 0;
        System.out.println( ">>> create stream-mode dictionary file: " + f.getName() + " " + f.canWrite() );
        System.out.println( ">>> " + f.getAbsolutePath() );
       
        
        BufferedWriter bw = new BufferedWriter( new FileWriter( f ) );
        while( en.hasMoreElements() ) { 
            String key = (String)en.nextElement();
            String v = "" + nodeIDs.get(key);
            bw.write( key + "\t" + v + "\n" );
        }
        bw.flush();
        bw.close();
        
        System.out.println( ">>> Done." );
        
    }
    
    public void createWikiIDListe() throws IOException {
        
        System.out.println( ">>> Create Language-ID-List ... [size="+ MyNetworkStreamMode.getMyNetworkStreamMode(studie).wikiIDs.size() +"]");
        
        Enumeration en2 = MyNetworkStreamMode.getMyNetworkStreamMode(studie).wikiIDs.keys();
        File f2 = new File( studie + ".wikis.dict" );
        int i2 = 0;
        System.out.println( ">>> create stream-mode dictionary file: " + f2.getName() + " " + f2.canWrite() );
        System.out.println( ">>> " + f2.getAbsolutePath() );
       
        BufferedWriter bw = new BufferedWriter( new FileWriter( f2 ) );
        while( en2.hasMoreElements() ) { 
            String key = (String)en2.nextElement();
            String v = "" + wikiIDs.get(key);
            bw.write( key + "\t" + v + "\n" );
        }
        bw.flush();
        bw.close();
        
        System.out.println( ">>> Done." );
        
        
    };

    private int maxWikiId = 0;
    private Hashtable<String,Integer> wikiIDs = null;
    
    private static int maxNodeId = 0;
    private Hashtable<String,Integer> nodeIDs = null;
    
    private void calcUniqueNodesIDs(MyLink2 link) {
        
        /**
         * Wikipages in eine Unique-ID wandeln.
         */
        String key1 = link.getKeySRC(); 
        String key2 = link.getKeyDEST(); 
                
        int id1 = 0;
        int id2 = 0;
        
        if ( nodeIDs.containsKey(key1) ) {
            id1 = (Integer)nodeIDs.get(key1);
        }    
        else {
            id1 = maxNodeId + 1;
            maxNodeId = id1;
            nodeIDs.put(key1, id1);
        }; 
        

        if ( nodeIDs.containsKey(key2) ) {
            id2 = (Integer)nodeIDs.get(key2);
        }    
        else {
            id2 = maxNodeId + 1;
            maxNodeId = id2;
            nodeIDs.put(key2, id2);
        }; 

        System.out.println("*** KEY1:" + key1 + " ID:" + id1 );
        System.out.println("*** KEY2:" + key2 + " ID:" + id2);
        
        
        link.uniqueIdSRC = id1;
        link.uniqueIdDEST = id2;
        
        // erfassen der aktuellen Nummer der CN aus der
        // Studienliste ...
        link.cnID = cnID;
        
        String wikikey1 = link.wikiSRC; 
        String wikikey2 = link.wikiDEST; 
        
        int wid1 = 0;
        int wid2 = 0;
        
        if ( wikiIDs.containsKey(wikikey1) ) {
            wid1 = (Integer)wikiIDs.get(wikikey1);
        }    
        else {
            wid1 = maxWikiId + 1;
            maxWikiId = wid1;
            wikiIDs.put(wikikey1, wid1);
        }; 

        if ( wikiIDs.containsKey(wikikey2) ) {
            wid2 = (Integer)wikiIDs.get(wikikey2);
        }    
        else {
            wid2 = maxWikiId + 1;
            maxWikiId = wid2;
            wikiIDs.put(wikikey2, wid2);
        }; 

        link.uniqueIdWikiSRC = wid1;
        link.uniqueIdWikiDEST = wid2;
        
    }

    public void setStudienName(String sn) {
        studie = sn;
    }
    
}
