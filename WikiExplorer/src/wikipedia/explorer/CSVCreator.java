/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia.explorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author kamir
 */
public class CSVCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File f = new File( "_liste_ALL4.dat");
        FileWriter fw = new FileWriter( "liste_ALL4.csv" );
        
        
        BufferedReader br = new BufferedReader( new FileReader( f ) );
        while( br.ready() ) {
            String line = br.readLine();
            String nl = "";
            
            StringTokenizer st = new StringTokenizer( line, "\t" );
            while( st.hasMoreTokens() ) { 
                String tok = st.nextToken(); 
                
                nl = nl.concat( tok );
                if ( st.hasMoreTokens() ) nl = nl.concat("\t");
            }
            fw.write(nl + "\n" );
            System.out.println( nl );
        }  
        fw.close();   
    }

    
}   