package wikipedia.explorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class AnalyseDataFlow {

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int count = 0;
        int countAll = 0;
        int starte = 0;
        int countERR = 0;

        String fname = "has_IW_link_to_EN_sv";
        
        String fn =  fname + ".dat" ;

        FileWriter fw = new FileWriter( "data/" + fname + "_firstEdit" + ".dat" ); 

        Vector<String> v = new Vector<String>();

        int wrong = 0;
        
        System.out.println( (new File(fn)).getAbsolutePath() );
        // load a PageName-List
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);
        
        Hashtable<String,String> deduplicator = new Hashtable<String,String>();
        
        int sum = 0;
        
        while (br.ready()) {
            String line = br.readLine();
            
            StringTokenizer st = new StringTokenizer( line );
            String tok1 = st.nextToken(); 
            v.add( tok1 );
            
        }

        System.out.println("Wrong lines: " + wrong);
        System.out.println("Used pages: " + sum);
        
        String ref1 = "sv";
        String ref2 = "en";
        
        sum = 0;

        // loop over all given pagenames
        for (String name : v) {
            countAll++;

            if (countAll > starte) {
                
                // for each id load the interlanguage links
                Wiki wiki1 = new Wiki(ref1 + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                Wiki wiki2 = new Wiki(ref2 + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                
                System.out.println("\n>[" + countAll + "] PAGE: " + name + "\n");

                try {
                    HashMap<String, String> mapIWL = wiki1.getInterwikiLinks(name);
                    
                    Calendar calA = new GregorianCalendar();
                    Calendar calB = new GregorianCalendar();
                    calA.set(2009, 4, 1);
                    calB.set(2009, 5, 31);
                    
                    Calendar cal1 = wiki1.getFirstRevision(name).getTimestamp();
                    Calendar cal2 = wiki2.getFirstRevision(name).getTimestamp();
                    
                    String dateOfFirstEdit1 = cal1.getTime().toString();
                    String dateOfFirstEdit2 = cal2.getTime().toString();

                    fw.write( name + "\tsv\t" + dateOfFirstEdit1 + "\ten\t" + dateOfFirstEdit2 + "\n" );
                    
//                    if( cal2.after( calA ) && cal2.before( calB )  ) {
//                        fw.write( name + "\tsv\t" + dateOfFirstEdit1 + "\ten\t" + dateOfFirstEdit2 + "\n" );
//                        fw.flush();
//                    }
//                    if (mapIWL != null) {
//                        System.out.println("> # of interwiki links: " + mapIWL.size());
//
//                        for (String k : mapIWL.keySet() ) {
//                            
//                            System.out.println("*** ("+k+") -> " + mapIWL.get(k));
//                            String enName = mapIWL.get( k );
//                                             
//                            
//                            if ( deduplicator.contains( enName ) ) {
//                              
//                            }
//                            else {
//                                deduplicator.put( enName, enName );
//                                fw.write( enName + "\n" );
//                                sum = sum + 1;
//                            };    
//                        }
//                        System.out.flush();

                        count++;
//                    }
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
