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
import wikipedia.explorer.gui.PageRequestDialog;

/**
 *
 * @author root
 */
public class MapPageIdsToInterWikiLinkPages {

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int count = 0;
        int countAll = 0;
        int starte = 0;
        int countERR = 0;

        String fn = "names.dat";

        FileWriter fwA1 = new FileWriter("no_IW_link_to_EN_sv.dat");
        FileWriter fwB1 = new FileWriter("has_IW_link_to_EN_sv.dat");

        FileWriter fwA2 = new FileWriter("no_IW_link_to_EN_en.dat");
        FileWriter fwB2 = new FileWriter("has_IW_link_to_EN_en.dat");

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
                Wiki wiki = new Wiki(ref + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

                System.out.println("\n>[" + countAll + "] PAGE: " + name + "\n");

                try {
                    HashMap<String, String> map = wiki.getInterwikiLinks(name);

                    if (map != null) {
                        System.out.println("> # of interwiki links: " + map.size());

                        boolean isInEN = false;
                        boolean isInSV = true;

                        String enName = "";

                        if (map.containsKey("en")) {
                            
                            isInEN = true;
                            
                            System.out.println("*** (en) -> " + map.get("en"));
                            enName = map.get("en");
                        }
                        System.out.flush();


                        // PREP) 

                        // A) !isInEN && isInSV
                        boolean A = !isInEN && isInSV;

                        // B) isInEN && isInSV
                        boolean B = isInEN && isInSV;


                        if (A) {
                            fwA1.write(name + "\n");
                            fwA2.write(enName + "\n");

                        }
                        if (B) {
                            fwB1.write(name + "\n");
                            fwB2.write(enName + "\n");
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

    }
}
