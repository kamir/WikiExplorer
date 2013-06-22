/**
 * The core data container for textanalysis.
 */
package jstat.data;

import io.CorpusFile2;
import io.CorpusFileXML;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.wikipedia.Wiki;
import wikipedia.corpus.extractor.iwl.ExtractIWLinkCorpus;
import wikipedia.explorer.data.WikiNode;

/**
 *
 * @author root
 */
public class Corpus {

    public static final int mode_XML = 0;
    public static final int mode_SEQ = 1;

    public static Corpus loadCorpus(String name, int mode) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (mode == mode_SEQ) {
            return CorpusFile2.loadFromLocalFS(name);
        } else if (mode == mode_XML) {
            return CorpusFileXML.loadFromLocalFS(name);
        } else {
            return null;
        }
    }

    public static void storeCorpus(Corpus corpus, String name, int mode) throws IOException, URISyntaxException {
        if (mode == mode_SEQ) {
            CorpusFile2.createCorpusFile(".", name, corpus);
        } else if (mode == mode_XML) {
            CorpusFileXML.createCorpusFile(".", name, corpus);
        }
    }
    boolean loadPageContent = true;

    public Corpus(boolean loadPC) {
        this();
        this.loadPageContent = loadPC;
    }

    public Corpus() {
        this.docs = new Vector<Document>();
    }

    public void addDocument(Document doc) {
        docs.add(doc);
    }
    public Vector<Document> docs = null;

    public void addWikiNodes(Vector<WikiNode> t, String acM) {

        System.out.println(t.size() + " " + acM);

        for (WikiNode wn : t) {
            String html = "";
            try {
                if (loadPageContent) {
                    html = ExtractIWLinkCorpus.getHTML(wn);
                }
                Document doc = new Document(ExtractIWLinkCorpus.getUrl(wn.wiki, wn.page), html);
                doc.group = acM;
                doc.wn = wn;
                addDocument(doc);
            } catch (IOException ex) {
                Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addWikiNode(WikiNode wn, String b) {
        String html;
        try {
            html = ExtractIWLinkCorpus.getHTML(wn);
            Document doc = new Document(ExtractIWLinkCorpus.getUrl(wn.wiki, wn.page), html);
            doc.group = b;
            doc.wn = wn;
            addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Vector<WikiNode> getWikiNodes(String cN) {
        Vector<WikiNode> d = new Vector<WikiNode>();
        for (Document doc : docs) {
            if (doc.group.equals(cN)) {
                d.add(doc.wn);
            }
        }
        return d;
    }
    // public static String listfile_pfad = "/home/kamir/bin/ExtractWikipediaTS/";
    public static String listfile_pfad = "./";

    public void writeWikiNodeKeyFile(WikiNode wn, String sdm_name) {
        
        String listFILE = "listfile_" + sdm_name + "_" + wn.wiki + "_" + wn.page + ".lst";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(listfile_pfad + listFILE));
            int i = 0;
            for (Document doc : docs) {
                WikiDocumentKey k = new WikiDocumentKey(doc);
                bw.write(k + "\n");
                i++;
            }
            System.out.println(i + " Docs gespeichert.");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String ENCODE_FN(String wiki, String pn) {
        Wiki wiki1 = new Wiki(wiki + ".wikipedia.org");
        String l = pn;
        try {
            l = URLEncoder.encode( wiki1.normalize(pn), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }
    
    public String getTextStatistikLine() { 
        String hl = "STUDIE	LANG	Page	z.CN	z.IWL	z.AL	z.BL	vol.CN	vol.IWL	vol.A.L	vol.B.L	volCN / volAL	volIWL / volBL	 ( volCN + volIWL) / (volAL + volBL)";
        return hl;
    }
    
    public void exportCorpusToSequenceFile( SequenceFile.Writer writer ) throws IOException, URISyntaxException {

        int c = 0;
        for ( Document doc : docs ) {
            c++;
            Text key = new Text( new WikiDocumentKey( doc ).toString() );
            Text val = new Text( doc.html );
            writer.append( key, val );
        }
    }
}
