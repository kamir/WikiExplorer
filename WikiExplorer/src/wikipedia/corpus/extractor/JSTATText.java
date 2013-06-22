/*
 * 
 * A collection of Text Analysis Tools.
 * 
 */
package wikipedia.corpus.extractor;

import chart.simple.MultiBarChart;
import data.series.Messreihe;
import io.CorpusFile2;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jstat.analytics.CorpusAnalyser;
import jstat.data.Corpus;
import terms.TermCollectionTools;


/**
 *
 * @author root
 */
public class JSTATText {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {

        String[] pages = { "Berlin" };

        String referenz = pages[0];
        
        String wiki = "de";
        
        Vector<Messreihe> mrs1 = new Vector<Messreihe>();
        Vector<Messreihe> mrsTermDist = new Vector<Messreihe>();
        
        for( String page : pages ) {
            // wikipedia.explorer.ExtractCategorieCorpus.extractCorpus(wiki, page);

            String file = "cat_corpus_de_" + page + ".dat.corpus.seq";
            Corpus c = CorpusFile2.loadFromLocalFS("/home/kamir/bin/WikiExplorer/WikiExplorer/" + file );

//            Messreihe mr1 = CorpusAnalyser.analyseCharacterDistribution(c, page);
//            mrs1.add(mr1);

            Messreihe mr2 = null;
            try {
                mr2 = CorpusAnalyser.analyseTermDistribution(c, page);
            } catch (Exception ex) {
                ex.printStackTrace();
//                Logger.getLogger(JSTAT.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if ( mr2 != null ) mrsTermDist.add(mr2);
        }   
        
        TermCollectionTools.initGlobalOrder( mrsTermDist , referenz );
        Vector<Messreihe> mrsTermDistTV = createGlobalOrder( mrsTermDist );
        
//        MultiBarChart.open(mrs1, "Character Count" , "symbol" , "log( anz )", true );
        MultiBarChart.open(mrsTermDistTV, "Term Count" , "symbol" , "log( anz )", true );
        
        
    }

    private static Vector<Messreihe> createGlobalOrder(Vector<Messreihe> mrsTermDist) {

        Vector<Messreihe> mrsTermDistT = new Vector<Messreihe>();
        // determine all terms of all rows
        HashSet<String> terms = new HashSet<String>();
        for( Messreihe mr : mrsTermDist ) { 
            for( String a : mr.xLabels2 ) {
                if ( !terms.contains(a) ) {
                    terms.add( a );
                }    
            };
            System.out.println( "Nr of terms : [" + mr.getLabel() + "] " + terms.size() );
        }
        
        for( Messreihe mr : mrsTermDist ) { 
            int sVor = mr.getXValues().size();
            for( String term : terms ) {
                if ( !mr.xLabels2.contains(term) ) {
                    mr.addValue(0, term);
                }    
            };
            System.out.println( "expandet : " + mr.getLabel() + " from: " + sVor + " => " + mr.xValues.size() );
            Messreihe r = TermCollectionTools.getTermVector(mr);
            mrsTermDistT.add(r.getYLogData());
        }
        
        return mrsTermDistT;
    }
}
