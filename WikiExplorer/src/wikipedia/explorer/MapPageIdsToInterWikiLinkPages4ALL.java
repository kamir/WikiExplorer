package wikipedia.explorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.security.auth.login.*;
import javax.swing.JTextArea;
import org.wikipedia.Wiki;
import org.wikipedia.Wiki2;
import wikipedia.explorer.gui.PageRequestDialog;

/**
 *
 * @author root
 */
public class MapPageIdsToInterWikiLinkPages4ALL {

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int count = 0;
        int countAll = 0;
        int starte = 0;
        int countERR = 0;

        String fn = "names.dat";
        
        String ZIEL = "ALL4";
        String ref = "sv";
        String[] ziel = { "fi", "ja", "he", "ko", "nl", "en" };

//        String ref = args[0];
//        String ziel = args[1];
//        String ZIEL = args[2];

        FileWriter fwA1 = new FileWriter("no_IW_link_to_"+ZIEL+"_"+ref+".dat");
        FileWriter fwB1 = new FileWriter("has_IW_link_to_"+ZIEL+"_"+ref+".dat");

        FileWriter fwC = new FileWriter("_liste_"+ZIEL+".dat");
        
        Vector<String> v = new Vector<String>();

        int wrong = 0;
        // load a id-List
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()) {
            String line = br.readLine();
            line = WikiToolHelper.isCleanPagename(line);
            if (line == null) {
                wrong++;
            } else {
                v.add(line);
            }
        }

        System.out.println("Wrong lines: " + wrong);

        int cc = 0;      // loop over all names
        for (String name : v) {
            countAll++;
            
       
            if (countAll > starte) {
                
               
                
                // for each id load the interlanguage links
                Wiki2 wiki = new Wiki2(ref + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

                HashMap<String, Object> info = wiki.getPageInfo(name);
                         
                String lname = "_?_";
                if ( info != null) {
                    Object opi = (Integer)info.get("pageid");
                    Object ons = (Integer)info.get("ns");
                    
                    if ( opi != null && ons != null ) {
                        int PageId = (Integer)opi; 
                        int Ns = (Integer)ons;
                        lname = name + "\t" + PageId + "\t" + Ns;
                    }    
                   
                }                
                System.out.println("\n>[" + countAll + "," + cc  + "] PAGE: " + name + "\n");

                try {
                    HashMap<String, String> map = wiki.getInterwikiLinks(name);

                    
                    if (map != null) {
                        System.out.println("> # of interwiki links: " + map.size());

                        boolean isInALL = true;

                        String enName = "";

                        for( String s : ziel ) {
                            boolean ist = map.containsKey( s );
                            System.out.print(ist + " " );
                            isInALL = isInALL && ist;
 
                            if ( ist ) {
                                String pn = map.get(s);
                                
                                // get the PageID
                                 Wiki2 wikiEN = new Wiki2( s + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                     
                                 info = wikiEN.getPageInfo(pn);
                                 
                                 int svPageId = (Integer)info.get("pageid");
                                 int svNs = (Integer)info.get("ns");

                                 enName = enName.concat("\t" + pn + "\t" +  svPageId + "\t" + svNs );                                
                                
                            }
                            System.out.println("=" + isInALL + "" );
                        }
                        
                        System.out.flush();
                        if (isInALL)  {
                            System.out.println("************** > " + enName );
                            cc = cc + 1;
                        }
                        
                        if (!isInALL) {
                            fwA1.write(lname + "\n");
                            fwA1.flush();

                        }
                        if (isInALL) {
                            fwB1.write(lname + "\n");
                            fwB1.flush();
                            fwC.write(lname + enName + "\n");
                            fwC.flush();
                        }

                        count++;


                        if (count == 5) {
                            fwA1.flush();
                            fwB1.flush();
                            count = 0;
                        }

                        
                    }
                } catch (Exception ex) {
                    // System.err.println(ex.getCause());
                    countERR++;
                }

            }

        }
        fwA1.close();
        fwB1.close();
        fwC.close();
    }
}
