package wikipedia.explorer.MLU.finance;

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
 * Selects the first EDIT time for pages with an Inter-Wiki-Link
 * between EN and SV pages.
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

        FileWriter fw = new FileWriter( "data/" + fname + "_firstEdit_a_v3" + ".dat" ); 
        FileWriter fw2 = new FileWriter( "data/" + fname + "_firstEdit_b_v3" + ".dat" ); 

        Vector<String> sv = new Vector<String>();

        int wrong = 0;
        
        System.out.println( (new File(fn)).getAbsolutePath() );
        // load a PageName-List
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);
        
        Hashtable<String,String> deduplicator = new Hashtable<String,String>();
        
        int sum = 0;
        
        while (br.ready()) {
            String line = br.readLine();
            System.out.println( line );
            
            StringTokenizer st = new StringTokenizer( line );
            String tok1 = st.nextToken(); 
            sv.add( tok1 );
            
        }

        System.out.println("Wrong lines: " + wrong);
        System.out.println("Used pages: " + sum);
        
        String ref1 = "sv";
        String ref2 = "en";
        
        sum = 0;

        // loop over all given pagenames
        for (String sv_name : sv) {
            countAll++;

            
            if (countAll > starte) {
                
                // for each id load the interlanguage links
                Wiki wiki1 = new Wiki(ref1 + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                Wiki wiki2 = new Wiki(ref2 + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                
                System.out.println("\n>[" + countAll + "] PAGE: " + sv_name + "\n");

                try {
                    HashMap<String, String> mapIWL = wiki1.getInterwikiLinks(sv_name);
                    
                    Calendar calA = new GregorianCalendar();
                    Calendar calB = new GregorianCalendar();
                    calA.set(2009, 2, 1); // MÄRZ 
                    calB.set(2009, 6, 30); //JULI 
                    
                    String en_name = mapIWL.get(ref2);
                    
                    Calendar cal1 = wiki1.getFirstRevision(sv_name).getTimestamp();
                    Calendar cal2 = wiki2.getFirstRevision(en_name).getTimestamp();
                    
                    String dateOfFirstEdit1 = cal1.getTime().toString();
                    String dateOfFirstEdit2 = cal2.getTime().toString();

                    String lineR = sv_name + "\t" + en_name + "\t" + dateOfFirstEdit1 + "\t" + dateOfFirstEdit2 + "\n"; 
                    fw.write( lineR );
                    fw.flush();
                    boolean inRANGE = cal2.after( calA ) && cal2.before( calB );
                    if( cal1.before( cal2 ) && inRANGE ) {
                        fw2.write( lineR );
                        fw2.flush();
                    }
                    count++;
                } 
                catch (Exception ex) {
                    // System.err.println(ex.getCause());
                    countERR++;
                    fw.write( "***ERROR***\t" + sv_name + "\t" + ex.getCause() + "\n");
                    fw.flush();
                }

            }

        }
        
        System.out.println( sum + " pages extracted.");
        
        for( String k : deduplicator.keySet() ) { 
            fw.write( deduplicator.get(k) + "\n" );        
        }

        fw.close(); 
        fw2.close();


    }
    
}
