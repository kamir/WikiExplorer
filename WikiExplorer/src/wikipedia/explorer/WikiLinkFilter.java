/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class WikiLinkFilter {

    public static PageRequestDialog dlg = null;
    public static String lang = "sv";
    public static String fn = "/home/kamir/liste.dat";

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int countAll = 0;
        int starte = 24950;
        int count = 0;

        FileWriter fwA = new FileWriter("A");
        FileWriter fwB = new FileWriter("B");
        FileWriter fwC = new FileWriter("C");

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


        String[] ref = {"en", "ja", "nl", "he", "fi", "nl"};
        boolean[] refB = new boolean[ref.length];

        for (int i = 0; i < ref.length; i++) {
            refB[i] = false;
        }


        // loop over all names
        for (String name : v) {
            countAll++;

            if (countAll > starte) {
                // for each id load the interlanguage links
                Wiki wiki = new Wiki(lang + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

                System.out.println("\n>[" + countAll + "] PAGE: " + name + "\n");
                HashMap<String, String> map = wiki.getInterwikiLinks(name);
                System.out.println("> # of interwiki links: " + map.size());

                boolean isInEN = false;
                boolean isInSV = true;

                int j = 0;
                for (String key : map.keySet()) {
                    
                    System.out.println(key + " -> " + map.get(key));
                    j++;

                    if (key.equals("en")) {
                        isInEN = true;
                    }

                    for (int i = 0; i < ref.length; i++) {
                        
                        refB[i] = ref[i].equals(key);
                        System.out.println(ref[i] + " " + key + " : " + refB[i]);

                    }

                }
                System.out.flush();


                // PREP) 

                // A) !isInEN && isInSV
                boolean A = !isInEN && isInSV;

                // B) isInEN && isInSV
                boolean B = isInEN && isInSV;

                // C) is In List( list )
                String s = new String();
                boolean C = true;
                for (int i = 0; i < ref.length; i++) {
                    C = C && refB[i];
                    if (refB[i]) {
                        s = s + "1";
                    } else {
                        s = s + "0";
                    }
                }

                if (A) {
                    fwA.write(name + "\n");
                }
                if (B) {
                    fwB.write(name + "\n");
                }

                fwC.write(s + "\t" + name + "\t" + countAll + "\n");


                count++;


                if (count == 50) {
                    fwA.flush();
                    fwB.flush();
                    fwC.flush();
                    count = 0;
                }

                A = true;
                B = true;
                C = true;


                for (int i = 0; i < ref.length; i++) {
                    refB[i] = false;
                }



            }

        }
        fwA.close();
        fwB.close();
        fwC.close();

    }

    public static void processRequest(String wikipedia, String pn, JTextArea a) throws IOException, Exception {
        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        // System.out.println( wiki.getPageText( pn ) );

        String[] cat = wiki.getCategories(pn);
        System.out.println("> # category: " + cat.length);
        System.out.flush();
        a.append("> # category: " + cat.length);
        a.append("\n");

        int i = 0;
        for (String c : cat) { // pages generated from (say) getCategoryMembers()
            System.out.println(i + " - " + c);
            a.append(i + " - " + c);
            a.append("\n");
            i++;
        }
        System.out.flush();

        HashMap<String, String> map = wiki.getInterwikiLinks(pn);
        System.out.println("> # of interwiki links: " + map.size());
        a.append("> # of interwiki links: " + map.size());
        a.append("\n");


        int j = 0;
        for (String key : map.keySet()) { // pages generated from (say) getCategoryMembers()
            System.out.println(j + " : " + key + " -> " + map.get(key));
            a.append(j + " : " + key + " -> " + map.get(key));
            a.append("\n");
            j++;
        }
        System.out.flush();

        String[] links = wiki.getLinksOnPage(pn);
        System.out.println("> # of links: " + links.length);
        a.append("> # of links: " + links.length);
        a.append("\n");
        int k = 0;
        for (String link : links) { // pages generated from (say) getCategoryMembers()
            System.out.println(k + " : " + link);
            a.append(k + " : " + link);
            a.append("\n");
            k++;
        }
        System.out.flush();
        a.append("=================================\n\n\n");

        dlg.setNrOfLinks(map.size(), links.length);

    }

    public static void processRequest(String wikipedia, String pn, JTextArea a,
            String[] langs, JTextArea b, JTextArea e, JTextArea d) throws IOException, Exception {
        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        System.out.println(">>> [ " + pn + " ]");
        String[] cat = wiki.getCategories(pn);
        System.out.println("> # category: " + cat.length);
        System.out.flush();
        a.append("> # category: " + cat.length);
        a.append("\n");

        int i = 0;
        for (String c : cat) { // pages generated from (say) getCategoryMembers()
            System.out.println(i + " - " + c);
            a.append(i + " - " + c);
            a.append("\n");
            i++;
        }
        System.out.flush();

        HashMap<String, String> map = wiki.getInterwikiLinks(pn);
        System.out.println("> # of interwiki links: " + map.size());
        a.append("> # of interwiki links: " + map.size());
        a.append("\n");

        int j = 0;
        for (String key : map.keySet()) {
            System.out.println(j + " : " + key + " -> " + map.get(key));
            a.append(j + " : " + key + " -> " + map.get(key));
            a.append("\n");
            j++;
        }
        System.out.flush();

        Vector<String> nodes = new Vector<String>();
        nodes.add("***" + pn + "***");

        String[] links = wiki.getLinksOnPage(pn);
        System.out.println("> # of links: " + links.length);
        a.append("> # of links: " + links.length);
        a.append("\n");
        int k = 0;
        for (String link : links) { // pages generated from (say) getCategoryMembers()
            System.out.println(k + " : " + link);
            a.append(k + " : " + link);

            if (isLinkInLangsAvailable(link, langs, b)) {
                nodes.add(link);
            };

            a.append("\n");
            k++;
        }
        System.out.flush();
        System.out.println(" go on ... ");

        a.append("=================================\n\n\n");

        System.out.println("In all langs: " + nodes.size());
        d.append("In all langs: " + nodes.size() + "\n");
        for (String s : nodes) {
            System.out.println(s);
            d.append(s + "\n");
        }
        System.out.flush();

        System.out.println(">>>calculateWeights() ... ");

        map.put(wikipedia, pn);
        double[] w = calcLanguageWeights(map);


        double sum = 0;
        DecimalFormat df = new DecimalFormat("0.000 %");
        Iterator it = map.keySet().iterator();
        System.out.println("Language weights: ");
        System.out.println("=================================");
        e.append("Language weights: \n");
        e.append("=================================\n");
        for (int m = 0; m < w.length; m++) {
            String lang = (String) it.next();
            System.out.println(m + " " + lang + " " + df.format(w[m]));
            e.append(m + " " + lang + " " + df.format(w[m]) + "\n");
//            System.out.println( m + " " + lang + " " + w[m] + "%" );
            sum = sum + w[m];
        }
        System.out.println("Summe=" + sum);
        System.out.flush();


    }

    private static boolean isLinkInLangsAvailable(String link, String[] langs, JTextArea b) throws IOException {
        boolean bo = true;
        for (String l : langs) {
            Wiki wiki = new Wiki(l + ".wikipedia.org");

            link = clean(link);

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

    private static double[] calcLanguageWeights(HashMap<String, String> map) throws IOException {
        double[] weights = new double[map.size()];
        int j = 0;
        for (String key : map.keySet()) { // pages generated from (say) getCategoryMembers()
            Wiki wiki = new Wiki(key + ".wikipedia.org");
            HashMap<String, Object> props = wiki.getPageInfo((String) map.get(key));
            Integer i = (Integer) props.get("size");
            if (i < 0) {
                i = 0;
            }
            weights[j] = i;
            System.out.println("w=" + weights[j] + " : " + j + " : " + key + " -> " + map.get(key));
            j++;
            System.out.println(j);
        }
        // return MathTools.logNormalize( weights );
        return MathTools.normalize(weights);
    }

    private static String clean(String link) {
        if (link.contains("\"") || link.contains(">") || link.contains("<")) {
            return null;
        } else {
            return link;
        }
    }
}
