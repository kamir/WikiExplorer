
package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author kamir
 */
public class FileSplitter {

    
    
    public static void main( String[] args ) throws IOException { 
        
        String n = "60_part_";
        String inF = "/home/userb/data/home_kamir_60_pageIds.dat";
        FileWriter[] fw = new FileWriter[6];
        
        for( int i = 0; i < 6; i++ ) { 
            fw[i] = new FileWriter( new File( n + i + ".dat")  );
        }
        
        int i = 0;
        BufferedReader br = new BufferedReader( new FileReader( inF ) );
        while( br.ready() ) {
            String line = br.readLine();
            fw[i].write( line + "\n" );
            i = i+1;
            if ( i == 6 ) i = 0;
        };
        
        for( i = 0; i < 6; i++ ) { 
            fw[i].flush();
            fw[i].close();            
        }
        
    }
}
