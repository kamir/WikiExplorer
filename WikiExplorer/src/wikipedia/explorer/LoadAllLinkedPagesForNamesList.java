package wikipedia.explorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.security.auth.login.*;
import javax.swing.JTextArea;
import org.wikipedia.Wiki;
import wikipedia.explorer.gui.PageRequestDialog;

/**
 *
 * @author root
 */
public class LoadAllLinkedPagesForNamesList {

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int count = 0;
        int countAll = 0;
        int starte = 0;
        int countERR = 0;

//        String fname = "list_DAX_stockcode_PageID_mapping.tab"; // + dat nicht vergessen
        String fname = "sup500_mapper";
        
        String fn = "/media/sda/BUFFER/DATA/stockmarket/" + fname + ".dat" ;

        FileWriter fw = new FileWriter( "linked_to_"+ fname + ".dat" ); 

        Vector<String> v = new Vector<String>();

        int wrong = 0;
        // load a id-List
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);
        
        Hashtable<String,String> deduplicator = new Hashtable<String,String>();
        
        int sum = 0;
        
        while (br.ready()) {
            String line = br.readLine();
            
            StringTokenizer st = new StringTokenizer( line );
            String tok1 = st.nextToken();
            String tok2 = st.nextToken();
            
            if ( deduplicator.contains( tok2 ) ) {
                
                
            }
            else {
                deduplicator.put( tok2 , tok2 );
                line = WikiToolHelper.isCleanPagename(tok2);
                if (line == null) {
                    wrong++;
                } 
                else {
                    
                    v.add(line);
                    
                    // fw.write( tok2 + "\n" );
                    sum = sum + 1;
                }
            }    
        }

        System.out.println("Wrong lines: " + wrong);
        System.out.println("Used pages: " + sum);
        
        String ref = "de";
        
        sum = 0;

        // loop over all pagenames
        for (String name : v) {
            countAll++;

            if (countAll > starte) {
                // for each id load the interlanguage links
                Wiki wiki = new Wiki(ref + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                
                System.out.println("\n>[" + countAll + "] PAGE: " + name + "\n");

                try {
                    HashMap<String, String> mapIWL = wiki.getInterwikiLinks(name);
                                    
                    if (mapIWL != null) {
                        System.out.println("> # of interwiki links: " + mapIWL.size());

                        for (String k : mapIWL.keySet() ) {
                            
                            System.out.println("*** ("+k+") -> " + mapIWL.get(k));
                            String enName = mapIWL.get( k );
                                             
                            
                            if ( deduplicator.contains( enName ) ) {
                              
                            }
                            else {
                                deduplicator.put( enName, enName );
                                fw.write( enName + "\n" );
                                sum = sum + 1;
                            };    
                        }
                        System.out.flush();

                        count++;
                    }
                } catch (Exception ex) {
                    // System.err.println(ex.getCause());
                    countERR++;
                }

            }

        }
        
        System.out.println( sum + " pages extracted.");
        
        for( String k : deduplicator.keySet() ) { 
            fw.write( deduplicator.get(k) + "\n" );        
        }

        fw.close(); 


    }
    
}
