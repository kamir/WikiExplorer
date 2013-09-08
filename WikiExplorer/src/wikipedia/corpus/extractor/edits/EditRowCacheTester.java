/*
 * Prüft nur, ob Daten in HBase gespeichert und abgerufen werden können. 
 */
package wikipedia.corpus.extractor.edits;

import chart.simple.MultiBarChart;
import data.series.MRT;
import data.series.Messreihe;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.security.auth.login.FailedLoginException;
import org.wikipedia.Wiki2;
import wikipedia.corpus.extractor.WikiStudieMetaData;
import static wikipedia.corpus.extractor.edits.WikiHistoryExtraction2.processStudie;
import static wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.getMrFromCache;
import static wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.pages;
import static wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.run;
import wikipedia.explorer.data.WikiNode;
import ws.cache2.*;

/**
 *
 * @author kamir
 */
public class EditRowCacheTester extends WikiHistoryExtractionBASE {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        tscache.TSCache.RAM = false;
        String path1 = ".";
        String path2 = ".";


        WikiStudieMetaData wd = new WikiStudieMetaData();
        //File ff = wd.selectFile( "/home/kamir/ANALYSIS/Wikipedia/WikiPaper_und_BA_2012");
        File ff = new File("/home/kamir/ANALYSIS/Wikipedia/WikiPaper_und_BA_2012/WikiPaper_und_BA_2012.xml");
        wd = wd.load(ff);

        // NAME extrahieren und Liste LADEN
        File f = wd.getRealPathToProjectFile(ff);

        HBaseCacheService service = new HBaseCacheService();
        TSCacheV2 proxy = service.getTSCacheV2Port();

        von = new GregorianCalendar();
        von.clear();
        von.set(2009, 0, 1, 0, 0);


        java.util.GregorianCalendar bis = new java.util.GregorianCalendar();
        bis.clear();
        bis.set(2009, 3, 1, 0, 0);

        processStudie(wd, von, bis, f);

        logStats();

        System.out.println("Done.");
        System.exit(0);
    }

    public static void processStudie(WikiStudieMetaData wd, Calendar von, Calendar bis, File f) throws FileNotFoundException, ClassNotFoundException, IOException, FailedLoginException, Exception {

        long t0 = System.currentTimeMillis();

        boolean operate_LOCALY = false;
        boolean operate_DEV = false;

        if (von == null) {
            System.exit(-1);
        }
        if (bis == null) {
            System.exit(-1);
        }

        showChart = false;

        loadFromLocalCACHE = false;
        loadSPLITS = false;

        storeToLocalCACHE = false;
        storeToLocal_SPLIT_PER_CN = false;

        if (f.exists()) {

            System.out.println(">>> Loading CN   : " + f.getAbsolutePath());

            if (loadFromLocalCACHE) {
                if (loadSPLITS) {
                    _loadLocalCacheDumpSPLITS(wd.path2, wd);
                } else {
                    _loadLocalCacheDump(wd.path2, wd);
                }
            }

            System.out.println(">>> Info: \n" + wd.description);

            final Vector<WikiNode> wnCN = wd.getWn();
            System.out.println(">>> Nr of CN-nodes : " + wnCN.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } else {
                run(wnCN, von, bis, true);
                logStats();
            }


            final Vector<WikiNode> wnIWL = wd.getIWL();
            System.out.println(">>> Nr of IWL-nodes : " + wnIWL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } else {
                run(wnIWL, von, bis, true);
                logStats();
            }


//            final Vector<WikiNode> wnAL = wd.getAL();
//            System.out.println(">>> Nr of AL-nodes : " + wnAL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnAL, von, bis, true);
//                logStats();
//            }

            final Vector<WikiNode> wnBL = wd.getBL();
            System.out.println(">>> Nr of BL-nodes : " + wnBL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } else {
                run(wnBL, von, bis, true);
                logStats();
            }

        } else {
            System.out.println(">>> " + f.getAbsolutePath() + " not available.");
        }

        if (showChart) {
            MultiBarChart.open(rows1, "1 Event-Zeitreihen", "t", "nr", false);
            MultiBarChart.open(rowsBINARY1, "1 Event-Reihen", "t", "nr", false);
        }

        /**
         *
         */
        if (storeToLocalCACHE) {
            if (storeToLocal_SPLIT_PER_CN) {
                int max = wd.getWn().size();
                // für jede CN eine eigene Hashtable anlegen
                for (int i = 0; i < max; i++) {
                    int CN = i + 1;
                    _storeLocalCacheDump_SPLIT_PER_CN(wd.path2, wd.selectedName, CN, wd);
                }
            } else {
                storeLocalCacheDump(wd.path2, wd.selectedName);
            }
        }

        wd.logExtraction(von, bis);

        Vector<Messreihe> mrCN = null;
        Vector<Messreihe> mrAL = null;
        Vector<Messreihe> mrBL = null;
        Vector<Messreihe> mrIWL = null;


        System.out.println(">>> " + wd.name);
        System.out.println(">>> INFO : \n" + wd.getDescription());

        System.out.println(">>> von : " + von.getTime());
        System.out.println(">>> bis : " + bis.getTime());

        System.out.println(">>> Done.");

        long t1 = System.currentTimeMillis();

        System.out.println(">>> " + new Date(t1 - t0));


    }

    /**
     *
     * Aufruf als Tool ....
     *
     * @param nodes
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FailedLoginException
     * @throws Exception
     */
    public static void run(Vector<WikiNode> n, Calendar v, Calendar b, boolean showCh) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        WikiNode[] nodes = new WikiNode[n.size()];
        int i = 0;
        for (WikiNode nnn : n) {
            nodes[i] = nnn;
            i++;
            System.out.println("(" + i + "): \t " + nnn);
        }
        run(nodes, v, b, showCh);
    }

    public static void run(WikiNode[] cnNodes, Calendar v, Calendar b, boolean showCh) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        ArrayList<Integer> list = new ArrayList<Integer>();

        // work with all
        for (int i = 0; i < cnNodes.length; i++) {
            list.add(i);
        }

        Collections.shuffle(list);

        pages = new String[cnNodes.length];
        wikis = new String[cnNodes.length];

        von = v;
        bis = b;
        int i = 0;
        for (WikiNode wn : cnNodes) {
            int id = list.get(i);
            pages[id] = wn.page;
            wikis[id] = wn.wiki;
            i++;
        }


        work(null);
    }
    static boolean doLOAD = true;

    public static void work(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        System.out.println("WORK " + pages.length);
        int i = 0;

        for (String pn : pages) {
            String w = wikis[i];
            testPageHistory(w, pn);
            i++;
            if (i % 1000 == 0) {
                logStats();
            }
        }
    }
    public static int MR_AVAILABLE = 0;
    public static int MR_MISSING = 0;
    public static int total = 0;

    public static Messreihe testPageHistory(String wikipedia, String pn) throws IOException, Exception {

        WikiNode wn = new WikiNode(wikipedia, pn);
        Messreihe m = getMrFromCache(wn);
        if (m != null) {
            MR_AVAILABLE++;
        } else {
            MR_MISSING++;
            if (doLOAD) {
                loadPageHistory(wn);
            }
        }

        total++;
        return preprocess(null);
    }

    public static void logStats() {
        Date d = new Date(System.currentTimeMillis());

        System.out.println("***TIME               : " + d);

        System.out.println("   #available         : " + MR_AVAILABLE);
        System.out.println("   #missing           : " + MR_MISSING);

        System.out.println("   #ratio: available :: " + (double) (MR_AVAILABLE / total));
        System.out.println("   #ratio: missing   :: " + (double) (MR_MISSING / total));
    }
}
