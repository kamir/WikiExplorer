package wikipedia.corpus.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JTextArea;
import org.wikipedia.Wiki;
import research.ETH.ExtendedNodePairSFE;
import util.WikiToolHelper;
import static wikipedia.explorer.WikiHistoryExplorer.loadPageHistory;
import wikipedia.explorer.data.WikiNode;

/**
 * Wir laden hier alle Gruppen und ermittlen sowohl die tatsächliche als auch
 * die theoretische LINK-Anzahl um die LINK Dichte zu messen.
 *
 *
 * @author kamir
 */
public class NetworkDensiteyCalculator implements Runnable {

    private NetworkDensiteyCalculator() {
    }

    public NetworkDensiteyCalculator(WikiNode wn) {

        this.wn = wn;

    }
    static String[] pages = {"Daimler_AG"}; // {"Stollberg", "Sulingen", "Bad Harzburg"}; // , "Fritiof_Nilsson_Piraten"};
    static String[] wikis =  {"de"}; // {"de", "de", "de"};//
    int i = 0;
    Hashtable<Integer, WikiNode> CN = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> IWL = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> AL = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> BL = new Hashtable<Integer, WikiNode>();
    boolean useBacklinks = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        NetworkDensiteyCalculator nc = new NetworkDensiteyCalculator();
        
        nc.doit(true,true);
        nc.doit(true,false);
    
    }
    int[] A = null;
    int[] B = null;
    int[] I = null;
    String n = "\n";

    public void doit(WikiNode wn) {
        pages[0] = wn.page;
        wikis[0] = wn.wiki;
        try {
            doit(false,false); // no threads ... no backlinks
        } catch (IOException ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FailedLoginException ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doit(boolean useThreads, boolean useBacklinksss ) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int i = 0;

        for (int ip = 0; ip < pages.length; ip++) {
            String pn = pages[ip];
//            for (String w : wikis) {
            String w = wikis[ip];
            i++;

            WikiNode wn = new WikiNode(w, pn);

            NetworkDensiteyCalculator ndc2 = new NetworkDensiteyCalculator(wn);
            ndc2.useBacklinks = useBacklinksss;
            if (useThreads) {
                Thread tr = new Thread(ndc2);
                tr.start();
            } else {
                ndc2.grabData(wn);
            }
//            }
        }
    }
    double rhoCORE = 0.0;
    double rhoAL = 0.0;
    double rhoBL = 0.0;

    public void loadCore(String wikipedia, String pn, BufferedWriter fw) throws IOException, Exception {

        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        HashMap<String, String> map = wiki.getInterwikiLinks(pn);
        System.out.println("> # of interwiki links: " + map.size());

        int SUMIWL = 0;

        SUMIWL = map.size();

        int j = 0;
        for (String key : map.keySet()) {

            String pnIWL = (String) map.get(key);

            System.out.println(j + " : " + key + " ---> " + pnIWL);

            Wiki wikiIWL = new Wiki(key + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            i++;
            IWL.put(i, new WikiNode(key, pnIWL));

            j++;

            HashMap<String, String> map2 = wikiIWL.getInterwikiLinks(pnIWL);
            SUMIWL = SUMIWL + map2.size();

        }

        DecimalFormat df = new DecimalFormat("0.000");

        int n = map.size();
        double z = n * (n - 1);
        double rho = SUMIWL / z;

        rhoCORE = rho;

        System.out.println("---------------------------------\n\n\n");
        System.out.println("n        : " + n);
        System.out.println("z        : " + z);
        System.out.println("SUMIWL   : " + SUMIWL);
        System.out.println("rho      : " + rho);

        if (fw != null) {
            fw.write(wikipedia + "\t" + pn + "\t" + n + "\t" + z + "\t" + SUMIWL + "\t" + df.format(rho));
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
        System.out.println("*****" + r.getTimestamp().getTime());

    }

    private void loadLocalNeighbors() throws IOException {
        for (WikiNode n : CN.values()) {
            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(n.page);

            for (String key : map) {
                i++;
                AL.put(i, new WikiNode(n.wiki, key));
            }

            
            if (useBacklinks) {
                String[] map2 = wiki.whatLinksHere(n.page);

                for (String key : map2) {
                    i++;
                    AL.put(i, new WikiNode(n.wiki, key));
                }
            }
        }
    }

    private void loadGlobalNeighbors() throws IOException {
        for (WikiNode n : IWL.values()) {
            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(n.page);

            for (String key : map) {
                i++;
                BL.put(i, new WikiNode(n.wiki, key));
            }

            if (useBacklinks) {
            String[] map2 = wiki.whatLinksHere(n.page);
                for (String key : map2) {
                    i++;
                    BL.put(i, new WikiNode(n.wiki, key));
                }
            }
        }
    }

    private int[] getIntraGroupDENS(Hashtable<Integer, WikiNode> AL) throws IOException {

        int SUMINT = 0;
        int SUMEXT = 0;

        HashSet hash = new HashSet();
        for (WikiNode wn : AL.values()) {
            hash.add(wn.getKey());
        }

        int n = hash.size();


        for (WikiNode wn : AL.values()) {

            Wiki wiki = new Wiki(wn.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(wn.page);

            for (String s : map) {

                WikiNode w = new WikiNode(wn.wiki, s);

                if (hash.contains(w.getKey())) {
                    SUMINT++;
                } else {
                    SUMEXT++;
                }
            }
        }

        int[] sums = new int[3];

        sums[0] = SUMINT;

        sums[1] = n;

        sums[2] = SUMEXT;

        return sums;
    }

    private void calcTheorieValues(BufferedWriter bwNL) {
        StringBuffer sb = new StringBuffer();
        String n = "\n";
        sb.append(" #nodes\t#links" + n);
        sb.append("CN =" + CN.size() + "\t" + getN_Links(CN) + n);
        sb.append("IWL=" + IWL.size() + "\t" + getN_Links(CN) + n);
        sb.append("AL =" + AL.size() + "\t" + getN_Links(CN) + n);
        sb.append("BL =" + BL.size() + "\t" + getN_Links(CN) + n);
        sb.append("O  =" + getOmmitted() + n);
        sb.append("LI =" + getLocalInteraction() + n);
        sb.append("GI =" + getGlobalInteraction() + n);



        System.out.println(sb.toString());
    }

    private int getN_Links(Hashtable<Integer, WikiNode> CN) {
        return CN.size() * (CN.size() - 1);
    }
    int CNs = 0;
    int IWLs = 0;
    int ALs = 0;
    int BLs = 0;
    int OM = 0;
    int LI = 0;
    int GI = 0;

    private int getOmmitted() {
        int n = 0;
        CNs = CN.size();
        IWLs = IWL.size();
        ALs = AL.size();
        BLs = BL.size();

        OM = (CNs * IWLs + IWLs * ALs + CNs * BLs + ALs * BLs) * 2;

        return n;
    }

    private int getLocalInteraction() {
        LI = 0;
        CNs = CN.size();
        IWLs = IWL.size();
        ALs = AL.size();
        BLs = BL.size();

        LI = (CNs * ALs) * 2;

        return LI;
    }

    private int getGlobalInteraction() {
        GI = 0;
        CNs = CN.size();
        IWLs = IWL.size();
        ALs = AL.size();
        BLs = BL.size();

        GI = (IWLs * BLs) * 2;

        return GI;
    }
    double R1 = 0;
    double R2 = 0;
    double R3 = 0;
    double R4 = 0;
    double RIWL = 0;
    double RAL = 0;
    double RBL = 0;
    double RLI = 0;
    double RGI = 0;
    double ROM = 0;
    double S1 = 0;
    double S2 = 0;
    double S3 = 0;
    double S4 = 0;
    int sum = 0;
    int S = 0;
    int LIWL;
    int LAL;
    int LBL;

    private void calcRatios() {

        getOmmitted();
        getLocalInteraction();
        getGlobalInteraction();

        LIWL = IWLs * (IWLs - 1);
        LAL = ALs * (ALs - 1);
        LBL = BLs * (BLs - 1);

        S1 = LIWL + LAL + LI;
        S2 = LIWL + LAL + LI + GI;
        S3 = LIWL + LAL + LI + GI + LBL;

        sum = CNs + IWLs + BLs + ALs;

        S = sum * (sum - 1);

        R1 = S1 / S;
        R2 = S2 / S;
        R3 = S3 / S;

        ROM = OM / S;

        RLI = LI / S;
        RGI = GI / S;

        RIWL = IWLs / S;
        RAL = ALs / S;
        RBL = BLs / S;

    }
    int sumLINKS_AL[] = null;
    int sumLINKS_BL[] = null;
    int sumLINKS_IWL[] = null;

    private double[][] calcAverageDegree() throws IOException {

        sumLINKS_AL = getIntraGroupDENS(AL);
        sumLINKS_BL = getIntraGroupDENS(BL);
        sumLINKS_IWL = getIntraGroupDENS(IWL);

        double[][] k = new double[3][3];

        k[0][0] = (double) sumLINKS_AL[0] / (double) AL.size();
        k[1][0] = (double) sumLINKS_BL[0] / (double) BL.size();
        k[2][0] = (double) sumLINKS_AL[0] / (double) IWL.size();

        k[0][1] = (double) sumLINKS_AL[1] / (double) AL.size();
        k[1][1] = (double) sumLINKS_BL[1] / (double) BL.size();
        k[2][1] = (double) sumLINKS_AL[1] / (double) IWL.size();

        k[0][2] = (double) sumLINKS_AL[2] / (double) AL.size();
        k[1][2] = (double) sumLINKS_BL[2] / (double) BL.size();
        k[2][2] = (double) sumLINKS_AL[2] / (double) IWL.size();

        return k;
    }

    private int getSumLinks(Hashtable<Integer, WikiNode> AL) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    BufferedWriter bwNL;

    public void grabData(WikiNode wn) throws Exception {

        CN = new Hashtable<Integer, WikiNode>();
        IWL = new Hashtable<Integer, WikiNode>();
        AL = new Hashtable<Integer, WikiNode>();
        BL = new Hashtable<Integer, WikiNode>();


        try {
            bwNL = new BufferedWriter(new FileWriter(new File("/home/kamir/ANALYSIS/" + wn.getKey() +"." + useBacklinks + ".NET.log.csv")));


            CN.put(i, wn);

//                IWL.put( i , wn );
//                AL.put( i , wn );
//                BL.put( i , wn );

            loadCore(wn.wiki, wn.page, null);

            loadLocalNeighbors();

            loadGlobalNeighbors();

            calcTheorieValues(bwNL);

            calcRatios();

            storeStaticNetAsCSV(new BufferedWriter(new FileWriter(new File("/home/kamir/ANALYSIS/" + wn.getKey() + ".stat.net.csv"))));

            double k[][] = calcAverageDegree();


            A = getIntraGroupDENS(AL);
            B = getIntraGroupDENS(BL);

            rhoAL = (double) A[0] / (double) A[1] * (A[1] - 1);
            rhoBL = (double) B[0] / (double) B[1] * (B[1] - 1);

            StringBuffer sb = new StringBuffer();

            sb.append("\n\n" + CN.elements().nextElement().getKey());
            sb.append("\n");
            sb.append("CN  " + CN.size());
            sb.append("\n");
            sb.append("IWL " + IWL.size() + "\t: " + rhoCORE);
            sb.append("\n");
            sb.append("AL  " + AL.size() + "\t: " + rhoAL + "\t" + A[0] + "\t" + A[1] + "\t" + A[2]);
            sb.append("\n");
            sb.append("BL  " + BL.size() + "\t: " + rhoBL + "\t" + B[0] + "\t" + B[1] + "\t" + B[2]);
            sb.append("\n");

            DecimalFormat df = new DecimalFormat("0.0000");

            sb.append("\n\n" + CN.elements().nextElement().getKey() + n);
            sb.append("CN  " + CN.size() + n);
            sb.append("IWL " + IWL.size() + "\t: rhoCORE=" + rhoCORE + n);
            sb.append("AL  " + AL.size() + "\t: rhoAL=" + rhoAL + "\t#l_int=" + A[0] + "\t#l_total=" + A[1] + "\t#l_ext=" + A[2] + n);
            sb.append("BL  " + BL.size() + "\t: rhoBL=" + rhoBL + "\t#l_int=" + B[0] + "\t#l_total" + B[1] + "\t#l_ext=" + B[2] + n);

            sb.append("\nOM  " + OM + "\t: " + df.format(ROM) + n);
            sb.append("LI  " + LI + "\t: " + df.format(RLI) + n);
            sb.append("GI  " + GI + "\t: " + df.format(RGI) + n);

            sb.append("\nS1  " + S1 + "\t R1 : " + df.format(R1) + n);
            sb.append("S2  " + S2 + "\t R2 : " + df.format(R2) + n);
            sb.append("S3  " + S3 + "\t R3 : " + df.format(R3) + n);
            sb.append("Sum " + sum + "\t SUM²" + (sum * sum) + "\t : " + S + n);

            sb.append("\n<k>_internal_IWL " + k[0][0] + n);
            sb.append("<k>_internal_AL  " + k[1][0] + n);
            sb.append("<k>_internal_BL  " + k[2][0] + n);

            sb.append("\n<k>_total_IWL " + k[0][1] + n);
            sb.append("<k>_total_AL  " + k[1][1] + n);
            sb.append("<k>_total_BL  " + k[2][1] + n);

            sb.append("\n<k>_external_IWL " + k[0][2] + n);
            sb.append("<k>_external_AL  " + k[1][2] + n);
            sb.append("<k>_external_BL  " + k[2][2] + n);

            bwNL.write(sb.toString());

            bwNL.flush();
            bwNL.close();

        } catch (IOException ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    WikiNode wn = null;

    public void run() {

        if (wn == null) {
            return;
        }

        javax.swing.JOptionPane.showMessageDialog(null, wn.getKey() + " ... started!");
        try {

            grabData(wn);

        } catch (Exception ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logToAnalysisFile(String data, String label) throws IOException {
        BufferedWriter bwNL2 = new BufferedWriter(new FileWriter(new File("/home/kamir/ANALYSIS/" + wn.getKey() + "." + label + ".csv")));

        bwNL2.write(data);
        bwNL2.close();
    }
    Hashtable<String, Vector> links = new Hashtable<String, Vector>();

    public void collectLink(String groupKEY, ExtendedNodePairSFE np) {
        Vector v = links.get(groupKEY);
        if (v == null) {
            v = new Vector();
            links.put(groupKEY, v);
        }
        v.add(np);
    }

    public void flushNetworks() {
        BufferedWriter bwNL2;
        try {
            bwNL2 = new BufferedWriter(new FileWriter(new File("/home/kamir/ANALYSIS/" + wn.getKey() + ".use_back_links=" + useBacklinks + ".LINKNET.csv")));

            bwNL2.write("Source\tTarget\tWeight\ta\tb\tc\n");
            for (String key : links.keySet()) {



                Vector v = links.get(key);
                for (Object o : v) {
                    ExtendedNodePairSFE l = (ExtendedNodePairSFE) o;
                    bwNL2.write(l.toString2() + "\t" + key + "\n");
                };

            }
            bwNL2.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkDensiteyCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void storeStaticNetAsCSV(BufferedWriter bw) throws IOException {
        writeLinksToStream(CN, bw, "CN");
        writeLinksToStream(IWL, bw, "IWL");
        writeLinksToStream(AL, bw, "AL");
        writeLinksToStream(BL, bw, "BL");

    }

    private void writeLinksToStream(Hashtable<Integer, WikiNode> CN, BufferedWriter bw, String key) throws IOException {
        for (Integer id : CN.keySet() ) {

            // Source ...
            WikiNode wn = CN.get(id);
            bw.write( wn.getKey() );
            
            // Abruf aktueller Links
            Wiki wiki = new Wiki(wn.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
            String[] map = wiki.getLinksOnPage(wn.page);
            
            for (String pn : map) {
                WikiNode wl = new WikiNode(wn.wiki, pn);
                bw.write( ";" + wl.getKey() );
                
            }
            bw.write("\n");
            bw.flush();

            if (useBacklinks) {
                String[] map2 = wiki.whatLinksHere(wn.page);
                for ( int i=0; i < map2.length; i++ ) {
                     WikiNode wl = new WikiNode( map2[0], map2[1] );
                     bw.write( wl.getKey() +";"+wn.getKey() +"\n" );
                }
                bw.flush();
            }
        }
    }
}
