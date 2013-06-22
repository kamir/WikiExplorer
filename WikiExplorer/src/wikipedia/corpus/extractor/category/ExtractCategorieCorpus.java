package wikipedia.corpus.extractor.category;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.*;
import jstat.data.Corpus;
import jstat.data.Document;
import org.wikipedia.Wiki; 
import org.apache.hadoop.io.Writable;


/**
 * Extract data for a certain page categorie.
 * 
 * 
 * 
 * NOT TESTED !!!!!
 * 
 * 
 * 
 * @author root
 */
public class ExtractCategorieCorpus {

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
 
        System.out.println(" *** NOT TESTED *** ");
        System.exit(0);
        
        String wiki = "de";
        String page = "London";
        
        extractCorpus(wiki, page);
        
    }
    
    public static void extractCorpus( String wiki, String page) throws IOException {
        
        Corpus corpus = new Corpus();
        
        int count = 0;
        int countERR = 0;
        
        
        String fname =  "cat_corpus_" + wiki + "_" + page;
        String fn =  fname + ".dat" ;
        
        Vector<String> v = new Vector<String>();

        int wrong = 0;
        int sum = 0;
         
        // for each id load the interlanguage links
        String url = getUrl(wiki,page);
        Wiki.debug = true;
        
        Wiki wiki1 = new Wiki(wiki + ".wikipedia.org"); 
        wiki1.getSiteStatistics();
        
        System.out.println("\n>[PAGE] : " + page + "\n");

        try {
            System.out.println( wiki1.getFirstRevision(page) );
            System.out.println( wiki1.getLinksOnPage(page).length );
            System.out.println( wiki1.getInterwikiLinks(page).size() );
            String[] catMembs = wiki1.getCategoryMembers(page);
                        
            Thread.currentThread().sleep(2000);
            System.err.flush();
            
            System.out.println( "****************************" );
            for( String memb : catMembs ) { 
                System.out.println( memb );
                String textMemb = wiki1.getRenderedText(memb);
                Document doc = new Document(getUrl(wiki, memb), textMemb );
                corpus.addDocument(doc);
            }
            
            System.out.println( "****************************" );
            
        } 
        catch (Exception ex) {
             System.err.println(ex.getCause());
        }
        try {
            Corpus.storeCorpus( corpus, fn, Corpus.mode_SEQ );
        } catch (URISyntaxException ex) {
            Logger.getLogger(ExtractCategorieCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getUrl(String wiki, String page) {
        return "http://" + wiki + ".wikipedia.org/wiki/" + page;
    }
    
}
