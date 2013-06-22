/**
 * A list of all available WIKI Projects 
 * 
 * last version:2013
 *
 *  
 */
package wikipedia.explorer.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class WikipediaStats {
    
    static String fn = "./data/Wikipedia Language Codes - Tabellenblatt2.csv";
    
    static public Vector<String> langs = new Vector<String>();
    
    public static void initProjectList(){ 
        File f = new File( fn );
        System.out.println( f.exists() );
        
        try {
            // load column 0 and 1 from this table ... beginning in line 3
            BufferedReader br = new BufferedReader( new FileReader( f ) );
            
            int i = 0;
            while( br.ready() ) { 
                i++;

                String line = br.readLine();
                StringTokenizer st = new StringTokenizer( line , "," );
                int tok = st.countTokens();

                String lid = st.nextToken().replace('\"', ' ').trim();

                int id = Integer.parseInt(lid);
                String lang = st.nextToken();
                
                System.out.println( "[" + i + "] " + tok + "\t" + id + "\t" + lang );
                langs.add(lang);
            }                
        } 
        catch (Exception ex) {
            Logger.getLogger(WikipediaStats.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    };

    public static void initProjectListERRORS() {
    
        langs.add("fr");
        langs.add("ru");
        langs.add("ja");
        langs.add("tokipona");
        langs.add("nomcom");
        
    }
    
    
}
