/**
 * 
 * A corpus file contains documents to a special analysis context.
 *
 * 
 */
package io;


import hadoopts.topics.wikipedia.AccessFileFilter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import jstat.data.Corpus;
import jstat.data.Document;
import jstat.data.WikiDocumentKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

/**
 *
 * @author root
 */
public class CorpusFile2 {
    
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

        Configuration config = new Configuration();
        LocalFileSystem fs = (LocalFileSystem) FileSystem.get( config );
        
        Path path = new Path( outPath + "/" + name + ".corpus.seq" );

        System.out.println("--> create corpus : " + path.toString() );
    
        // write a SequenceFile form a Vector
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, config, path, WikiDocumentKey.class, Document.class);

        int c = 0;
        for ( Document doc : corpus.docs ) {
            c++;
            if ( c < LIMIT ) {
                //System.out.println("("+c+")" + doc.url );
                writer.append( new WikiDocumentKey( doc ), doc );
            }
        }
        writer.close();
        
    }

    public static Corpus loadFromLocalFS( String fn ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {

        HashSet groups = new HashSet();
        Corpus c = new Corpus();
        
        Configuration config = new Configuration();
        LocalFileSystem fs = (LocalFileSystem) FileSystem.get(new URI("file:///"), config );
        

        Path path = new Path( fn );

        // write a SequenceFile form a Vector
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, config);

        System.out.println("--> process corpus    : " + fn );
        System.out.println("--> compression-codes : " + reader.getCompressionCodec() );
        System.out.println("--> key-classename    : " + reader.getKeyClassName() );
        System.out.println("--> value-classname   : " + reader.getValueClassName() );

        boolean goOn = true; 
        int i = 1;
        while( goOn && i <= LIMIT ) {
            
            
            Class ckey = reader.getKeyClass();
            Class cval = reader.getValueClass();
            
            Document val = new Document();
            WikiDocumentKey key = new WikiDocumentKey();
                    
            goOn = reader.next( (Writable)key );

            if( goOn ) {
                reader.getCurrentValue( val );
            
                Document doc = (Document)val;
                c.addDocument(doc);
                
                groups.add( doc.group );
            }
            i++;
        }
        System.out.println("--> nr of records     : " + i );
        System.out.println("--> nr of groups      : " + groups.size() );
        System.out.println("--> " + groups.toString() );

        return c;
        
        
    }

 
    
}
