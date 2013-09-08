/**
 * 
 * The Core is defined as the group of Wikipedia pages, connected to the 
 * central node by a inter-wiki-link. 
 * 
 * The central node is the selected page the focus is on.
 * 
 * n is the number of pages with an interwikilink to the core page.
 * 
 *      int n = map.size();
 * 
 * z is the number of all possible directed links without self loops.
 * 
 *      double z = n * (n-1);
 * 
 * rho is the density of the core, which is the ratio of existing links and
 * all possible links.
 * 
 *      double rho = SUMIWL / z;
 * 
 **/
package research.measures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JTextArea;
import org.wikipedia.Wiki;
import util.WikiToolHelper;
import static wikipedia.explorer.WikiHistoryExplorer.loadPageHistory;
import wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class CoreDensiteyCalculator {

    static String[] pages3 = {"Amoklauf_von_Erfurt", "Illuminati_(Buch)" }; 
    static String[] wikis3 = {"de","de" };
    
    static String[] pages = {"Berlin","Heidelberg","Sulingen","Bad_Harzburg","Oxford", "Birmingham" }; 
    static String[] wikis = {"de","de","de","de","en","en"};

    static String[] pages2 = {"Deutschland","Vereinigte_Staaten","Angela_Merkel","Barack_Obama","Germany", "United_States", "Angela_Merkel", "Barack_Obama" }; 
    static String[] wikis2 = {"de","de","de","de","en","en","en","en"};

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        BufferedWriter br = new BufferedWriter( new FileWriter( new File( "./data/out/core_density_PLOSONE_paper_v09.dat" )));
        int i = 0;
        for (String pn : pages) {
            loadCoreDensity(wikis[i], pn, br );
            i=i+1;
        }
        
        i = 0;
        for (String pn : pages2) {
            loadCoreDensity(wikis2[i], pn, br );
            i=i+1;
        }
        
        i = 0;
        for (String pn : pages3) {
            loadCoreDensity(wikis3[i], pn, br );
            i=i+1;
        }
        br.flush();
        br.close();        
    }

    public static void loadCoreDensity(String wikipedia, String pn, BufferedWriter fw ) throws IOException, Exception {

        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        HashMap<String, String> map = wiki.getInterwikiLinks( pn );
        System.out.println("> # of interwiki links: " + map.size());
         
        int SUMIWL = 0;

        SUMIWL = map.size();

        int j = 0;
        for (String key : map.keySet()) {
            
            String pnIWL = (String) map.get(key);
            
            System.out.println( j + " : " + key + " ---> " + pnIWL );

            Wiki wikiIWL = new Wiki(key + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            j++;

            HashMap<String, String> map2 = wikiIWL.getInterwikiLinks( pnIWL );
            SUMIWL = SUMIWL + map2.size();
            
            System.out.println( j + " : " + key + " ---> " + pnIWL + "has inter-wiki-links( " + map2.size() + " )" );
        }
        
        DecimalFormat df = new DecimalFormat("0.000"); 
        
        int n = map.size();
        double z = n * (n-1);
        double rho = SUMIWL / z;
        
        System.out.println("---------------------------------\n\n\n");
        System.out.println("n        : " + n );
        System.out.println("z        : " + z );
        System.out.println("SUMIWL   : " + SUMIWL );
        System.out.println("rho      : " + rho );
        
        if( fw != null ) { 
            fw.write(wikipedia + "\t" + pn + "\t" + n + "\t" + z + "\t" + SUMIWL + "\t" + df.format(rho) );    
            fw.newLine();
            fw.flush();   
        }
        
        
        System.out.flush();
        System.out.println("=================================\n\n\n");

    }

    private static boolean isLinkInLangsAvailable(String link, String[] langs, JTextArea b) throws IOException {
        boolean bo = true;
        for (String l : langs) {
            Wiki wiki = new Wiki(l + ".wikipedia.org");

            link = WikiToolHelper.isCleanPagename(link);

            if (link == null) {
                return false;
            }

            HashMap<String, Object> map = wiki.getPageInfo(link);
            Integer i = (Integer) map.get("size");
            b.append(l + " : " + link + " => " + i + "\n");
            System.out.println(l + " : " + link + " => " + i + "\n");
            bo = bo && (i > 0);
        }
        System.out.flush();
        return bo;
    }

    private static void lookupRevisions(String pn, Wiki wiki) throws IOException {
        Wiki.Revision[] revs = wiki.getPageHistory(pn);
        int j = 0;
        if (revs != null) {
            Calendar calFIRST = null;
            int z = 0;
            for (Wiki.Revision r : revs) {
                z++;
                System.out.println("\t" + z + ")" + r.getTimestamp().getTime());
                Calendar cal = r.getTimestamp();
                if (calFIRST == null) {
                    calFIRST = cal;
                } else {
                    if (cal.before(calFIRST)) {
                        calFIRST = cal;
                    }
                }
            }
            j++;

      
        }
        
        Wiki.Revision r = wiki.getFirstRevision(pn);
        System.out.println( "*****" + r.getTimestamp().getTime() );
        
    }
}
