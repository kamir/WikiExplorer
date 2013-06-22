/*

 */
package wikipedia.explorer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.wikipedia.Wiki;
import wikipedia.explorer.data.WikipediaStats;

/**
 *
 * @author kamir
 */
public class StatsExtractor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        WikipediaStats.initProjectList();
        
//        WikipediaStats.initProjectListERRORS();
        
        StringBuffer sb = new StringBuffer();
        boolean isNew = false;
        
        sb.append( "#lang,articles,pages,files,edits,users,admins\n");
        
        String[] statkeys = {"articles", "pages", "files", "edits", "users", "admins"};
        
        int errors = 0;
        for( String l : WikipediaStats.langs ) { 
            Wiki wiki = new Wiki( l + ".wikipedia.org" ); // create a new wiki connection to en.wikipedia.org
            
            
            // if an error occurs while content is parsed, NULL comes back ...
            HashMap<String,Integer> stat = wiki.getSiteStatistics();
            
            
            if ( stat != null ) {
                System.out.println( ">>> WIKI : " + l );
                
                sb.append(l);
                for( String name : statkeys ) {
                    // System.out.println( "> " + name );

                    int v = stat.get(name);
                    sb.append("," + v );
                }   
                sb.append("\n");
                System.out.flush();
            }   
            else { 
                errors++;
                System.err.println( ">>> ERROR ("+errors+") : " + l );                
            }
        }
        
        FileWriter fw = new FileWriter( "StatsExtractor_NOW.dat");
        BufferedWriter bw = new BufferedWriter( fw );
        fw.write( sb.toString() );
        bw.flush();
    }
}
