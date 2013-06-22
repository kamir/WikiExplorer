/*
 * We want to test the problem of wrong "first edit" events
 * retieved by the WIKI-API
 *
 * 
 * Punkte der Diskussion mit Holger Motzkau:
 - Änderung bei schwedischen Zugriffen mit Einrichten der englischen Seite:
 Stichproben (Fritiof_Nilsson_Piraten, Gustav_III) zeigen, dass die
 Edit-Zeitpunkte
 in der Liste nicht oder nur ungefähr stimmen; manche sind doppelt
 (Statistik)
 - Änderungen auch umgekehrt (schwedische Seite wird erzeugt) und für andere
 Sprachen
 zum Vergleich
 - An wichtigsten sind ihm die Qualitätsbewertungen der Artikel in
 Korrelation mit den
 Zugriffszahlen (schwedisch, zwei Cluster?)
 - Aufteilen der englischen Wikipedia-Zugriffe im Monat der hebräischen
 Extra-Zeit und
 davor -- Unterschiede von ein Prozent oder weniger?
 - Anomalie der japanischen Edit-Häufigkeit im Zusammenhang mit Bots möglich?
 - Aus den Edit-Minima kann man obere Grenzen für Edits außerhalb des Landes
 gewinnen.
 - Seite http://reportcard.wmflabs.org/ zu den Langzeittrends (nicht
 größenabhängig)
 - Research Newsletter:
 - Research Newsletter:

 http://meta.wikimedia.org/wiki/Research:Newsletter/2012/November#cite_note-12

 * 
 * MAIL vom 27.12.2012 von Jan an Mirko
 */
package wikipedia.explorer;

import util.WikiToolHelper;
import util.MathTools;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.security.auth.login.*;
import javax.swing.JTextArea;
import org.wikipedia.Wiki;
import org.wikipedia.Wiki.Revision;
import wikipedia.explorer.gui.PageRequestDialog;

/**
 *
 * @author root
 */
public class WikiHistoryExplorer {

    static String[] pages = {"Gustav_III_of_Sweden"}; //, "Fritiof_Nilsson_Piraten"};
    static String[] wikis = {"en"};

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        for (String pn : pages) {
            for (String w : wikis) {
                loadPageHistory(w, pn, null);
            }
        }
    }

    public static void loadPageHistory(String wikipedia, String pn, JTextArea a) throws IOException, Exception {
        if (a == null) {
            a = new JTextArea();
        }

        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        // System.out.println( wiki.getPageText( pn ) );

        String[] cat = wiki.getCategories(pn);
        System.out.println("> # category: " + cat.length);
        System.out.flush();
        a.append("> # category: " + cat.length);
        a.append("\n");

        int i = 0;
        for (String c : cat) {
            // System.out.println( i + " - " + c );
            a.append(i + " - " + c);
            a.append("\n");
            i++;
        }
        System.out.flush();

        HashMap<String, String> map = wiki.getInterwikiLinks(pn);
        System.out.println("> # of interwiki links: " + map.size());
        a.append("> # of interwiki links: " + map.size());
        a.append("\n");

        lookupRevisions(pn, wiki);

        int j = 0;
        for (String key : map.keySet()) {
            String pnIWL = (String) map.get(key);
            a.append(j + " : " + key + " ---> " + pnIWL);
            a.append("\n");

            Wiki wikiIWL = new Wiki(key + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
            Calendar late = new GregorianCalendar();
            late.set(2010, 11, 31, 23, 59);

            Calendar early = new GregorianCalendar();
            early.set(2000, 0, 1, 0, 0);
            System.out.println(late.getTime());
            System.out.println(early.getTime());



        }
        System.out.flush();


        String[] links = wiki.getLinksOnPage(pn);
        // System.out.println( "> # of links: " + links.length );
        a.append("> # of links: " + links.length);
        a.append("\n");
        int k = 0;
        for (String link : links) { // pages generated from (say) getCategoryMembers()
            System.out.println(k + " : " + link);
            // a.append( k + " : " + link  );
            a.append(link);

            a.append("\n");
            k++;
        }
        System.out.flush();
        a.append("=================================\n\n\n");

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
        Revision[] revs = wiki.getPageHistory(pn);
        int j = 0;
        if (revs != null) {
            Calendar calFIRST = null;
            int z = 0;
            for (Revision r : revs) {
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
        Revision r = wiki.getFirstRevision(pn);
        System.out.println( "*****" + r.getTimestamp().getTime() );
        
    }
}
