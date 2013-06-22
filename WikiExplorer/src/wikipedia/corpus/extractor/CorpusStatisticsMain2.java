/*
 
 */
package wikipedia.corpus.extractor;

import io.CNResultManager2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JFileChooser;
import jstat.data.Corpus;
import wikipedia.ts.extractor.JSTATAccess;

/**
 *
 * @author kamir
 */
public class CorpusStatisticsMain2 {

    /**
     * @param args the command line arguments
     */
    public static void createTextStatisticsResultFile(String studie, String[] wiki, String[] page, int mode, String[] ext, CNResultManager2 rm ) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
 
//        JFileChooser jfc = new JFileChooser();
//        jfc.setCurrentDirectory( new File("/home/kamir/bin/WikiExplorer/WikiExplorer") );
//        jfc.showOpenDialog(null);
        
//        String studie = "finance_DAX_2012";
//        String wiki = "de";
//        String page = "BMW";
        
        String p = "/home/kamir/bin/WikiExplorer/WikiExplorer/";
        
        String f = "result_" + studie + ".dat.csv";
        
        File file = new File( p + "/" + f );
        
        FileWriter fw = new FileWriter( file );
        JSTATAccess.initHeader(fw);

        for( int i = 0; i < wiki.length; i++ ) {
        
            System.out.println("> textanalysis : " + i );
            JSTATAccess.work(wiki[i], page[i], studie, fw, mode, ext[i], rm, i );
        
        }
        fw.close();
        
    }

}
