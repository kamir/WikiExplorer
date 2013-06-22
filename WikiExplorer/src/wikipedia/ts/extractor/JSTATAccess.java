/**
 *
 * Dieses Programm lädt eine Corpus-Datei und ermittelt dazu die 
 * neuen Mase für die Relevanzanalyse.
 * 
 * Ausserdem sind hier die Methoden des Cluster-Connectors enthalten.
 *
 * @author root
 */
package wikipedia.ts.extractor;

import chart.simple.MultiBarChart;
import data.series.Messreihe;
import extract.ts.mr.list_based_selection.ExtractAcTS4Corpus;
import io.CNResultManager2;
import io.CorpusFile2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jstat.analytics.CorpusAnalyser;
import jstat.data.Corpus;
import org.wikipedia.Wiki;
import terms.TermCollectionTools;
import wikipedia.corpus.extractor.iwl.ExtractIWLinkCorpus;
import wikipedia.explorer.data.WikiNode;

public class JSTATAccess {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        
        Wiki.debug = true;
        
        // do just a single ANALYSIS for one extracted corpus to get the measures for one single page
        String page = "RWE";
        String wiki = "de";
        String studie = "finance_DAX";
        
        CNResultManager2 cnrm = new CNResultManager2();
        
        work(wiki, page, studie, null, Corpus.mode_XML, "", cnrm , 3 );
        
        
        cnrm.printResults();
    }
    
    public static boolean export = true;

    /**
     * Gives back the listfile-name (full path).
     *
     * @param w
     * @param page
     * @param studie
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static String work(String w, String page, String studie, FileWriter fw, int FILEMODE, String ext, CNResultManager2 rm, int i ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        
        // ext war hier der erste Edit der Datei
        
        
        Vector<WikiNode> ACN = new Vector<WikiNode>();
        Vector<WikiNode> AL = new Vector<WikiNode>();

        Vector<WikiNode> BIWL = new Vector<WikiNode>();
        Vector<WikiNode> BL = new Vector<WikiNode>();

        // wikipedia.explorer.ExtractCategorieCorpus.extractCorpus(wiki, page);

        String file = "iwl_corpus_" + studie + "_" + w + "_" + page + ".dat.corpus.seq";

        String file2 = "iwl_corpus_" + studie + "_" + w + "_" + page + ".dat.corpus.seq.ts";

        String listFILE = "listfile_" + studie + "_" + w + "_" + page + ".lst";

        // Corpus2 c = CorpusFile2.loadFromLocalFS("/home/kamir/bin/WikiExplorer/WikiExplorer/" + file);
        Corpus c = null;
        try {

            if (FILEMODE == Corpus.mode_XML) {
                file = "iwl_corpus_" + studie + "_" + w + "_" + page + ".dat.corpus.xml";
            }

            c = Corpus.loadCorpus("./" + file, FILEMODE);

//            if ( export ) { 
//                c.exportCorpusToSequenceFile();
//            }
            
            ACN = c.getWikiNodes("CN");
            AL = c.getWikiNodes("A.L");
            
            BIWL = c.getWikiNodes("IWL");
            BL = c.getWikiNodes("B.L");
            
            double volCN = getSummeVolume(ACN);
            double volIWL = getSummeVolume(BIWL);
            double volBL = getSummeVolume(BL);
            double volAL = getSummeVolume(AL);

//            rm.setResult( i +".txt.vol.CN" , volCN );
//            rm.setResult( i +".txt.vol.IWL" , volIWL );
//            rm.setResult( i +".txt.vol.B.L" , volBL );
//            rm.setResult( i +".txt.vol.A.L" , volAL );
//            
//            rm.setResult( i +".z.CN" , ACN.size() );
//            rm.setResult( i +".z.IWL" ,BIWL.size() );
//            rm.setResult( i +".z.B.L" , BL.size() );
//            rm.setResult( i +".z.A.L" , AL.size() );
            
            c.writeWikiNodeKeyFile( ACN.elementAt(0) , studie );

            System.out.println("CN   : " + ACN.size());
            System.out.println("IWL  : " + BIWL.size());
            
            System.out.println("A.L  : " + AL.size());
            System.out.println("B.L  : " + BL.size());

            double r1 = 100 * volCN / volAL;
            double r2 = 100 * volIWL / volBL;
            double r3 = 100 * (volCN + volIWL) / (volAL + volBL);
            
//            rm.setResult( i +".txt.r1" , r1 );
//            rm.setResult( i +".txt.r2" , r2 );
//            rm.setResult( i +".txt.r3" , r3 );
            

            System.out.println("A local ratio : " + r1);

            System.out.println("B global ratio 1 : " + r2);
            System.out.println("B global ratio 2 : " + r3);

            
            DecimalFormat df = new DecimalFormat("0.00000");
            
            if (fw != null) {

                StringBuffer line = new StringBuffer();
                
                line.append( studie + "\t" + ACN.elementAt(0).wiki + "\t" + ACN.elementAt(0).page + "\t");
                line.append( ACN.size() + "\t");
                line.append( BIWL.size() + "\t");
                line.append( AL.size() + "\t");
                line.append( BL.size() + "\t");
                line.append( df.format( volCN ) + "\t");
                line.append( df.format( volIWL) + "\t");
                line.append( df.format( volAL ) + "\t");
                line.append( df.format( volBL ) + "\t");
                line.append( df.format(  r1 ) + "\t");
                line.append( df.format(  r2 ) + "\t");
                line.append( df.format(  r3 ) + "\t");
                line.append( ext + "\t");
                
                String l = line.toString().replace('.', ',' ); 
                fw.write( l );
                fw.write("\n");
                fw.flush();
            }

            String args2[] = new String[3];
            args2[0] = "/user/kamir/wikipedia/raw/2007/2007-12/page*";
            args2[1] = "/user/kamir/wikipedia/corpus/" + page;
            args2[2] = listFILE;

        } 
        catch (URISyntaxException ex) {
            Logger.getLogger(JSTATAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Corpus.listfile_pfad + listFILE;

    }
    
    public static void initHeader( FileWriter fw ) throws IOException {
                fw.write("#\n#studie\tACN.elementAt(0).wiki\tACN.elementAt(0).page\t");
                fw.write("ACN.size()\t");
                fw.write("BIWL.size()\t");
                fw.write("AL.size()\t");
                fw.write("BL.size()\t");
                fw.write("volCN\t");
                fw.write("volIWL\t");
                fw.write("volAL\t");
                fw.write("volBL\t");
                fw.write("r1\t");
                fw.write("r2\t");
                fw.write("r3\t");
                fw.write("ext\t");
                fw.write("\n#\n");
                fw.flush();
    }

    private static Vector<Messreihe> createGlobalOrder(Vector<Messreihe> mrsTermDist) {

        Vector<Messreihe> mrsTermDistT = new Vector<Messreihe>();
        // determine all terms of all rows
        HashSet<String> terms = new HashSet<String>();
        for (Messreihe mr : mrsTermDist) {
            for (String a : mr.xLabels2) {
                if (!terms.contains(a)) {
                    terms.add(a);
                }
            };
            System.out.println("Nr of terms : [" + mr.getLabel() + "] " + terms.size());
        }

        for (Messreihe mr : mrsTermDist) {
            int sVor = mr.getXValues().size();
            for (String term : terms) {
                if (!mr.xLabels2.contains(term)) {
                    mr.addValue(0, term);
                }
            };
            System.out.println("expandet : " + mr.getLabel() + " from: " + sVor + " => " + mr.xValues.size());
            Messreihe r = TermCollectionTools.getTermVector(mr);
            mrsTermDistT.add(r.getYLogData());
        }

        return mrsTermDistT;
    }

    public static void uploadFile(String base, String listFile, String dest) throws IOException {
        
        System.out.println( ">>> DELETE : " + dest + listFile );
        System.out.println("/usr/bin/hadoop fs -rm " + listFile );
        
        int go = javax.swing.JOptionPane.showConfirmDialog(null, "Go on?");
        
        System.out.println( go );
        
        if ( go == 1 ) System.exit(0);
        
        ProcessBuilder builder = new ProcessBuilder("/usr/bin/hadoop", "fs", "-rm", dest);
        builder.directory(new File(base));
        try {
            //        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
            //        
            Process p = builder.start();
            int i = p.waitFor();
            System.out.println(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(JSTATAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("/usr/bin/hadoop fs -copyFromLocal " + listFile + " " + dest);
        
        builder = new ProcessBuilder("/usr/bin/hadoop", "fs", "-copyFromLocal", listFile, dest);
        builder.directory(new File(base));
        try {
            //        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
            //        
            Process p = builder.start();
            int i = p.waitFor();
            System.out.println(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(JSTATAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 
     * @param listFile
     * @param pattern
     * @param wiki
     * @param page
     * @param studie
     * @return
     * @throws IOException 
     */
    public static String runExtraction(
            String listFile, String pattern, String wiki, String page, String studie) throws IOException {
        
        File f = checkFile(listFile);
        
        String lf = f.getName();
        String out = studie + "_" + wiki + "_" + page;

        checkFile( "/usr/bin/hadoop" );
        checkFile( "store/EXTS4Corpus.jar" );
        checkFile( "/home/kamir/bin/ExtractWikipediaTS/" );
        
        System.out.println("/usr/bin/hadoop jar store/EXTS4Corpus.jar "
                + "/user/kamir/wikipedia/raw/" + pattern
                + " /user/kamir/wikipedia/corpus/" + out
                + " " + lf);

        ProcessBuilder builder = new ProcessBuilder(
                "/usr/bin/hadoop", "jar", "store/EXTS4Corpus.jar",
                "/user/kamir/wikipedia/raw/" + pattern,
                "/user/kamir/wikipedia/corpus/" + out,
                lf);

        builder.directory(new File("/home/kamir/bin/ExtractWikipediaTS/"));
        Process p = builder.start();

        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
        System.out.println(s.next());

        return out;
    }

    public static String runExtraction2(
            String listFile, String pattern, String studie, String ext) throws IOException {
        File f = new File(listFile);
        String lf = f.getName();
        String out = studie + "_" + ext + "_merged";

        System.out.println(">>> BASE-FOLDER : /home/kamir/bin/ExtractWikipediaTS/" );
        
        System.out.println("/usr/bin/hadoop jar store/EXTS4Corpus.jar "
                + "/user/kamir/wikipedia/raw/" + pattern
                + " /user/kamir/wikipedia/corpus/" + out
                + " " + lf);

        ProcessBuilder builder = new ProcessBuilder(
                "/usr/bin/hadoop", "jar", "store/EXTS4Corpus.jar",
                "/user/kamir/wikipedia/raw/" + pattern,
                "/user/kamir/wikipedia/corpus/" + out,
                lf);

        builder.directory(new File("/home/kamir/bin/ExtractWikipediaTS/"));
        Process p = builder.start();

        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
        System.out.println(s.next());

        return out;
    }

    public static void downloadFile(String src, String dest) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "/usr/bin/hadoop", "fs", "-copyToLocal", src, dest);
        builder.directory(new File("/home/kamir"));
        try {
            //        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
            //        
            Process p = builder.start();
            int i = p.waitFor();
            System.out.println(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(JSTATAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static double getSummeVolume(Vector<WikiNode> ACN) {
        int s = 0;
        for (WikiNode wn : ACN) {
            if (wn.pageVolume != -1) {
                s = s + wn.pageVolume;
            }

        }
        System.out.println(ACN.size() + "  => " + s);
        return (double) s;
    }

    public static File checkFile(String listFile) {
        File f = new File(listFile);
        System.out.println(">>> listfile.exists() = " + f.exists() );
        return f;
    }
}
