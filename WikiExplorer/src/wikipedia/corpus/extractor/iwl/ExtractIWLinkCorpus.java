package wikipedia.corpus.extractor.iwl;

import io.CNResultManager2;
import java.io.FileWriter;
import wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.*;
import jstat.data.Corpus;
import jstat.data.Document;
import org.wikipedia.Wiki;
import org.apache.hadoop.io.Writable;
import tool.SequenceFileExplorer;
import wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import wikipedia.explorer.gui.PageInfoView2;
import wikipedia.explorer.data.WikiNode;
import wikipedia.ts.extractor.JSTATAccess;
import wikipedia.corpus.extractor.*;

/**
 * Extract a Wikipedia-Corpus for Interwiki-Linked data for a certain central
 * page CN and all linked pages with link depth ld=1 (just nearest neighbours).
 *
 * @author root
 */
public class ExtractIWLinkCorpus implements Runnable {

    public static String studie = null;
    public static String[] wiki = null;
    public static String[] page = null;

    public static void runFromGUITool(String _studie, String[] _wiki, String[] _page,
            boolean _withText, boolean runCluster,
            FileWriter fw, int _fm) throws IOException {

        studie = _studie;
        wiki = _wiki;
        page = _page;

        fwResults = fw;

        withText = _withText;
        /**
         *
         * all Einträge der Liste abarbeiten ... für jede CN wird ein eigener
         * Corpus geladen.
         *
         *
         */
        ExtractIWLinkCorpus tool = null;
        for (int i = 0; i < wiki.length; i++) {

            String w = wiki[i];
            String p = page[i];

            // define a center page to start data retrieval procedure ...
            WikiNode cp = new WikiNode(w, p);

            // create the extraction tool ...
            tool = new ExtractIWLinkCorpus();
            tool.centerPage = cp;
            tool.runOnCluster = runCluster;
            tool.fileMode = _fm;

//            tool.crawlMode = true;

            tool._mergedMode = true;
            tool.run();

            System.out.println(" ***** ");
            System.out.println(" {" + cp.toString() + "} is done.");
            System.out.println(" ***** ");

        }

        tool.mergedClusterRun("merged_listfile_" + studie + ".lst", _studie);

        if (mns != null) {
            mns.close();
            mns.createWikiIDListe();
            mns.createNodeIDListe();
        };



    }

    /**
     *
     * Crawl und extract TS trennen ...
     *
     * @param studie
     * @throws IOException
     */
    public static void submit(String studie) throws IOException {

        /**
         *
         * Liste abarbeiten ...
         *
         */
        ExtractIWLinkCorpus tool = null;

        // create the extraction tool ...
        tool = new ExtractIWLinkCorpus();

        tool.runOnCluster = true;
        tool.fileMode = Corpus.mode_XML;
        tool._mergedMode = true;

        tool.mergedClusterRun("merged_listfile_" + studie + ".lst", studie);

    }
    static FileWriter fwResults = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        initStudie_GERMAN_Cities();



        /**
         *
         * Kombinatorik ...
         *
         */
        for (int i = 0; i < wiki.length; i++) {
            for (int j = 0; j < page.length; j++) {
                String w = wiki[i];
                String p = page[j];

                // define a center page to start data retrieval procedure ...
                WikiNode cp = new WikiNode(w, p);

                // create the extraction tool ...
                ExtractIWLinkCorpus tool = new ExtractIWLinkCorpus();

                tool.crawlMode = true;
                tool.runOnCluster = true;
                tool.showAnalyseFrame = true;

                tool.centerPage = cp;

                Thread t = new Thread(tool);
                t.start();
            }
        }
    }

    public static void initStudie_core() {
        studie = "core";
        String[] _wiki = {"de"};
        String[] _page = {"Barack Obama", "Angela Merkel"};
        wiki = _wiki;
        page = _page;
    }

    public static void initStudie_finance() {
        studie = "finance_DAX";
        String[] _wiki = {"de"};
        String[] _page = {"Bayerische_Hypotheken-_und_Wechsel-Bank",
            "Feldmühle_Nobel", "Babcock_Borsig", "Continental", "Bayerische_Vereinsbank",
            "MAN", "Mannesmann", "Nixdorf", "Schering", "Veba", "Viag", "Daimler-Benz", "Deutsche Bank",
            "Deutsche Lufthansa", "Dresdner Bank", "Henkel", "Hoechst", "Karstadt", "Kaufhof", "Linde", "RWE",
            "Siemens", "Degussa", "Commerzbank", "Bayer", "Volkswagen", "RWE", "BMW", "BASF", "Thyssen", "Allianz"};
        wiki = _wiki;
        page = _page;
    }

    public static void initStudie_GERMAN_Cities() {
        studie = "de.cities";
        String[] _wiki = {"de"};
        String[] _page = {"Sulingen", "Meiningen"};
        wiki = _wiki;
        page = _page;
    }

    public static void initStudie_finance2() {
        studie = "dax.finance";
        String[] _wiki = {"de"};
        String[] _page = {"Volkswagen"};
        wiki = _wiki;
        page = _page;
    }
    private boolean runOnCluster;
    private int fileMode;
    String pattern = "2007/2007-12/page*";
    private boolean _mergedMode;
    public static boolean crawlMode;
    private boolean showAnalyseFrame;

    public ExtractIWLinkCorpus() {
        tempCOLlinksA = new Vector<WikiNode>();
        tempCOLiwl = new Vector<WikiNode>();
        tempCOLlinksB = new Vector<WikiNode>();
        tempCOLcatMembA = new Vector<WikiNode>();
        tempCOLcatMembB = new Vector<WikiNode>();
    }
    public Vector<WikiNode> tempCOLlinksA = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLiwl = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLlinksB = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLcatMembA = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLcatMembB = new Vector<WikiNode>();

    public void run() {
        try {

            // load data from WIKIPEDIA-API ... 
            System.out.println(">>> Crawlmode=" + crawlMode);
            if (crawlMode) {
                extractCorpusInfos(centerPage);
            }

            // load corpus-file and show simple group statistics ...
            String listFile = JSTATAccess.work(centerPage.wiki, centerPage.page, studie, fwResults, fileMode, "", new CNResultManager2(), 1);
            System.out.println(">>> LISTFILE : " + listFile);

            // extract TS on the cluster
            if (runOnCluster) {
                System.out.println("1.) Copy listfile to cluster ...");
                JSTATAccess.uploadFile(Corpus.listfile_pfad, listFile, "/");

                System.out.println("2.) Run Extract-JOB on the cluster ...");
                String out = JSTATAccess.runExtraction(listFile, pattern, centerPage.wiki, centerPage.page, studie);

                System.out.println("3.) Copy resultfolder from cluster ...");
                JSTATAccess.downloadFile("/user/kamir/wikipedia/corpus/" + out, "/user/kamir/wikipedia/corpus/");

            }

            // 
            if (showAnalyseFrame) {
                System.out.println("4.) Start local TS-Analysis-Tool ...");

                String[] args2 = null;
                SequenceFileExplorer.main(args2);
            }



        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mergedClusterRun(String fn, String _studie) {
        if (runOnCluster && _mergedMode) {
            try {
                String the_corpus_listfile_pfad = "/home/kamir/bin/WikiExplorer/WikiExplorer/";

                System.out.println("1.) Copy listfile to cluster ...");
                System.out.println("    listfile in path : " + the_corpus_listfile_pfad);


                JSTATAccess.uploadFile(the_corpus_listfile_pfad, fn, "/");

                pattern = javax.swing.JOptionPane.showInputDialog("pattern=", pattern);
                String ext = javax.swing.JOptionPane.showInputDialog("ext=", "a");

                System.out.println("2.) Run Extract-JOB on the cluster ...");
                String out = JSTATAccess.runExtraction2(fn, pattern, _studie, ext);

                System.out.println("3.) Copy resultfolder from cluster ...");
                JSTATAccess.downloadFile("/user/kamir/wikipedia/corpus/" + out, "/user/kamir/wikipedia/corpus/");

                System.out.println("4.) Start local TS-Analysis-Tool ...");

                /**
                 * CMD : /usr/bin/hadoop jar store/EXTS4Corpus.jar INPUT :
                 * /user/kamir/wikipedia/raw/2007/2007-12/page* PUTPUT :
                 * /user/kamir/wikipedia/corpus/CCAA_a_merged LIST :
                 * merged_listfile_CCAA.lst
                 */
                String[] args2 = new String[5];
                args2[0] = _studie;
                args2[1] = Corpus.listfile_pfad + "/" + fn;
                args2[2] = out;
                args2[3] = ext;
                args2[4] = pattern;

                CNInputFrame2.setArgs(args2);

                // SequenceFileExplorer.main(args2);

            } catch (IOException ex) {
                Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    WikiNode centerPage = null;
    static boolean withText = false;
    /**
     * Load Corpus Data from WEB ...
     *
     * @param wn
     * @throws IOException
     *
     */
    static MyNetworkStreamMode mns = null;

    public void extractCorpusInfos(WikiNode wn) throws IOException {

        mns = MyNetworkStreamMode.getMyNetworkStreamMode(studie);

        Corpus corpus = new Corpus(withText);

        int count = 0;
        int countERR = 0;
        
        String netn_ = wn.page;
        
        if ( netn_.contains( "/" ) ) {
           netn_ = netn_.replaceAll("/", "_");
        }    

        // Datei zum Speichern des CORPUS
        String fname = "iwl_corpus_" + studie + "_" + wn.wiki + "_" + netn_;
        String fn = fname + ".dat";


        String netn = "net." + fname + ".tab.csv";


        Vector<String> v = new Vector<String>();

        // GLOBAL statefull network stream data handler ...

        mns.nextCN(); // count on ...
        mns.init(netn);

        int wrong = 0;
        int sum = 0;

        System.out.println("\n>[PAGE] CN: " + wn.page + "\n");

        try {

            // 
            MyLink2 linkCN = new MyLink2();
            linkCN.source = wn.page;
            linkCN.wikiSRC = wn.wiki;

            String a[] = new String[2];
            a[0] = wn.wiki;
            a[1] = wn.page;

            // lade direkte Links
            tempCOLlinksA = getLinksVector(wn);
            for (WikiNode wnnA : tempCOLlinksA) {
                MyLink2 Al = linkCN.clone();
                Al.wikiDEST = wnnA.wiki;
                Al.dest = wnnA.page;
                Al.iwl = 0;
                Al.direct = 1;
                mns.addLink(Al);
            }

            // lade InterwikiLinks 
            tempCOLiwl = getInterWikiLinksVector(wn);
            tempCOLcatMembA = getCatMembers(wn);

            for (WikiNode wnn : tempCOLiwl) {
                MyLink2 iwl = linkCN.clone();
                iwl.wikiDEST = wnn.wiki;
                iwl.dest = wnn.page;
                iwl.iwl = 1;
                iwl.direct = 0;
                mns.addLink(iwl);
            }

            // lade Links zu den Interwikilinks gelinkten
            for (WikiNode iwlCN : tempCOLiwl) {
                MyLink2 linkCN2 = new MyLink2();
                linkCN2.source = iwlCN.page;
                linkCN2.wikiSRC = iwlCN.wiki;
                final Vector<WikiNode> linksVector = getLinksVector(iwlCN);
                for (WikiNode wnn : linksVector) {
                    MyLink2 Bl = linkCN2.clone();
                    Bl.wikiDEST = wnn.wiki;
                    Bl.dest = wnn.page;
                    Bl.iwl = 0;
                    Bl.direct = 1;
                    mns.addLink(Bl);
                }

                tempCOLlinksB.addAll(linksVector);
                tempCOLcatMembB.addAll(getCatMembers(iwlCN));
            }

            PageInfoView2 piv = new PageInfoView2();
            piv.open(wn, this);
            piv.initContent();


        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        corpus.addWikiNodes( tempCOLcatMembA, "group", "A.CM" );
//        corpus.addWikiNodes( tempCOLcatMembB, "group", "B.CM" );
        corpus.addWikiNodes(tempCOLlinksA, "A.L");
        corpus.addWikiNodes(tempCOLlinksB, "B.L");

        Wiki wikiCN = new Wiki(wn.wiki + ".wikipedia.org");
        int vol = getPageSize(wikiCN, wn.page);
        wn.pageVolume = vol;

        corpus.addWikiNode(wn, "CN");
        corpus.addWikiNodes(tempCOLiwl, "IWL");

        System.out.println("****************************");
        System.out.println(" Loading pages now ...");
        System.out.println("****************************");

        try {

            if (netn.contains("/")) {
                netn = netn.replaceAll("/", "_");
            }



            Corpus.storeCorpus(corpus, fn, Corpus.mode_XML);
        } catch (Exception ex) {
            System.out.println("###  " + ex.getCause());
        }
        System.out.println("*** DONE ***");

    }

    public static String getUrl(String wiki, String page) {
        return "http://" + wiki + ".wikipedia.org/wiki/" + page;
    }

    public static String getHTML(WikiNode wn) throws IOException {
        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");
        String n = wiki1.getPageText(wn.page);
        return n;
    }

    private Vector<WikiNode> getLinksVector(WikiNode wn) throws IOException {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");

        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        String[] n = wiki1.getLinksOnPage(wn.page);

        if (Wiki.debug) {
            for (String l : n) {
                System.out.println("###" + l + "###");
            }
            // System.exit(0);
        };

        for (String s : n) {
            WikiNode wn2 = new WikiNode(wn.wiki, s);

            int vol = getPageSize(wiki1, s);
            wn2.pageVolume = vol;

            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private Vector<WikiNode> getInterWikiLinksVector(WikiNode wn) throws IOException, Exception {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        Wiki.debug = true;
        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");

        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        HashMap<String, String> map = wiki1.getInterwikiLinks(wn.page);

        for (String k : map.keySet()) {
            WikiNode wn2 = new WikiNode(k, map.get(k));
            // VOL
            int vol = getPageSize(wiki1, wn2.page);
            wn2.pageVolume = vol;
            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private Vector<WikiNode> getCatMembers(WikiNode wn) throws IOException {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");


        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        String[] n = wiki1.getCategories(wn.page);

        for (String s : n) {
            WikiNode wn2 = new WikiNode(wn.wiki, s);

            int vol = getPageSize(wiki1, wn2.page);
            wn2.pageVolume = vol;

            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private int getPageSize(Wiki wiki, String p) {
        HashMap<String, Object> t;
        Integer vol = 0;
        try {
            if (!p.contains(">")) {
                t = wiki.getPageInfo(p);

                System.out.println("<..." + p + " ...> ");

                if (t != null) {
                    System.out.println("<<  " + p + " ...> ");
                    vol = (Integer) t.get("size");
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage() + " : " + ex.getCause());
            vol = 0;
        }
        return vol;
    }
}
