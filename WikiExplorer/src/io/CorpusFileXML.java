/**
 * 
 * A corpus file contains documents to a special analysis context.
 *
 * 
 */
package io;


import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import jstat.data.Corpus;
import jstat.data.Document;

/**
 *
 * @author root
 */
public class CorpusFileXML {
    
    static int LIMIT = Integer.MAX_VALUE;
    
    /**
     * Im sourceFolder wird eine komplette Gruppe gewählt
     * und in einen TS Bucket überführt.
     * 
     * ==> ist nur eine SAVE Funktion ... 
     *
     * @param groupFolder
     */
    public static void createCorpusFile(String outPath, String name, Corpus corpus) throws IOException, URISyntaxException {
        
        File path = new File( outPath + "/" + name + ".corpus.xml" );

        System.out.println("--> create corpus : " + path.toString() );
    
        // write a XML String of the Corpus
        FileWriter os = new FileWriter( path );
        
        XStream xstream = new XStream();
        String s = xstream.toXML( corpus );
        os.write( s );
        os.flush();
        os.close(); 
    }

    public static Corpus loadFromLocalFS( String fn ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {

        HashSet groups = new HashSet();

        FileInputStream os = new FileInputStream( fn );
        
        System.out.println("--> load XML-file with Corpus data ...   : " + fn );
        
        XStream xstream = new XStream();
        Object o = xstream.fromXML(os); 
        Corpus c = (Corpus)o;

        System.out.println("*** DONE *** " );

        int i = 0;
        for( Document doc : c.docs ) {
            groups.add( doc.group );
            i++;
        }
        
        System.out.println("--> nr of records     : " + i );
        System.out.println("--> nr of groups      : " + groups.size() );
        System.out.println("--> " + groups.toString() );

        return c;
    }

 
    
}
