package nutch.corpus.creator;

import wikipedia.corpus.extractor.*;
import com.thoughtworks.xstream.XStream;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.Vector;
import wikipedia.explorer.data.WebNode;
import wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class CrawlMetaData {

    public void createSeedFile() throws IOException {
        
        File f = new File( data_base_dir + "/" + this.name );
        if ( !f.exists() ) { 
            f.mkdirs();
        }
        File f2 = new File( data_base_dir + "/" + this.name + "/" + seed_text_dir + "/seed.txt");
        FileWriter fw = new FileWriter( f2 );
        for( WebNode wn : this.getWn() ) { 
            fw.write( wn.getUri().toString() );
        }
        fw.flush();
        fw.close();
        System.out.println( "> seed-file : " + f2.getAbsolutePath() );
    }

 

    String depth = "3";
    String topN = "10";
    String dir = "data";
    String seed_text_dir = "urls";
    
    String data_base_dir = "/home/kamir/ANALYSIS/Nutch";

    public String name = "?";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector<WebNode> getWn() {
        return wn;
    }

    public void setWn(Vector<WebNode> wn) {
        this.wn = wn;
    }
    
    public String description = "...";
    Vector<WebNode> wn = new Vector<WebNode>();
    
    void addNewNode( URI w ) { 
        wn.add( new WebNode( w ) );
    }
    
    public static void store( File f, CrawlMetaData data ) throws FileNotFoundException, IOException { 
        FileWriter os = new FileWriter( f );
        
        XStream xstream = new XStream();
        String s = xstream.toXML( data );
        os.write( s );
        os.flush();
        os.close(); 
        
        data.createSeedFile();
    }
    
    public static CrawlMetaData load( File f ) throws FileNotFoundException {  
        FileInputStream os = new FileInputStream( f );
        XStream xstream = new XStream();
        Object o = xstream.fromXML(os); 
        CrawlMetaData d = (CrawlMetaData)o;
        return d;
    }
    
    public void runCrawl() throws IOException {
        
        String NUTCH_HOME = "/home/kamir/bin/apache-nutch-1.6/bin/nutch";
        String CB = data_base_dir + "/" + this.name;
        
        System.out.println( "nutch crawl " + this.seed_text_dir + " " +
                            "-dir " + this.dir + " -depth " + this.depth + " " +
                            "-topN " + this.topN 
        );

        ProcessBuilder builder = new ProcessBuilder(
            NUTCH_HOME, "crawl", this.seed_text_dir,
            "-dir", this.dir,
            "-depth", this.depth,
            "-topN", this.topN
        );

        // PROJECT Folder !!!
        builder.directory(new File( CB ) );
        Process p = builder.start();
        
        System.out.println("> Nutch-Home : " + NUTCH_HOME );
        System.out.println("> Crawl-Base : " + NUTCH_HOME );
        
        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
        System.out.println(s.next());    
    
    }

 
}
