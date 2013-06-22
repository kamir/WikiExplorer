package wikipedia.explorer.MLU.finance;

import util.WikiToolHelper;
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
public class MapPageIdsToInterWikiLinkPages {

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        
        int countNoMap = 0;
        int countG1 = 0;
        int countG2 = 0;
        
        int count = 0;
        int countAll = 0;
        int starte = 0;
        int countERR = 0;

        String fn = "names.dat";

        FileWriter fwA1 = new FileWriter("_no_IW_link_to_EN_sv.dat");
        FileWriter fwB1 = new FileWriter("_has_IW_link_to_EN_sv.dat");

        FileWriter fwA2 = new FileWriter("_no_IW_link_to_EN_en.dat");
        FileWriter fwB2 = new FileWriter("_has_IW_link_to_EN_en.dat");

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

        String ref = "sv";
        


        // loop over all names
        for (String name : v) {
            countAll++;

            if (countAll > starte) {

                // for each id load the interlanguage links
                Wiki2 wiki = new Wiki2(ref + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
                Wiki2 wikiEN = new Wiki2("en" + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

                System.out.println("\n>[" + countAll + "] PAGE: " + name + "\n");

                try {
                    
                    HashMap<String, String> map = wiki.getInterwikiLinks(name);

                    HashMap<String, Object> info = wiki.getPageInfo(name);
                    
                    int svPageId = (Integer)info.get("pageid");
                    int svNs = (Integer)info.get("ns");
                    
                    int enPageId = 0;
                    int enNs = -5;
                    
                    if (map != null) {
                        
                        System.out.println("> # of interwiki links: " + map.size());

                        boolean isInEN = false;
                        boolean isInSV = true;

                        String enName = "";

                        if (map.containsKey("en")) {
                            
                            isInEN = true;
                            
                            System.out.println("*** (en) -> " + map.get("en"));
                            enName = map.get("en");
                            
                            HashMap<String, Object> infoEN = wiki.getPageInfo(name);
                    
                            // lookup English PageID
                            enPageId = (Integer)infoEN.get("pageid");
                            
                            // lookup English NameSpace
                            enNs = (Integer)infoEN.get("ns");
                            
                            
                        }
                        System.out.flush();


                        // PREP) 

                        // A) !isInEN && isInSV
                        boolean A = !isInEN;

                        // B) isInEN && isInSV
                        boolean B = isInEN;


                        if (A) {
                            fwA1.write(name + "\t" + svPageId + "\t" + svNs + "\n");
                            fwA2.write(enName + "\t" + enPageId + "\t" + enNs + "\n");
                            countG1++;

                        }
                        if (B) {
                            fwB1.write(name + "\t" + svPageId + "\t" + svNs + "\n");
                            fwB2.write(enName + "\t" + enPageId + "\t" + enNs + "\n");
                            countG2++;
                        }

                        count++;


                        if (count == 5) {
                            fwA1.flush();
                            fwB1.flush();
                            fwA2.flush();
                            fwB2.flush();
                            count = 0;
                        }

                        A = false;
                        B = false;
                    }
                    else { 
                        countNoMap = countNoMap + 1;
                    }
                } catch (Exception ex) {
                    // System.err.println(ex.getCause());
                    countERR++;
                }

            }

        }
        fwA1.close();
        fwB1.close();
        fwA2.close();
        fwB2.close();
        
        int all = countG1 + countG2 + countNoMap;
        System.out.println(  "G1    : " + countG1 );
        System.out.println(  "G2    : " + countG2 );
        System.out.println(  "noMap : " + countNoMap );
        System.out.println(  "sum   : " + all );
 

    }
}
