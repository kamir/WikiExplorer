/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jstat.analytics;

import util.Counter;
import chart.simple.MultiBarChart;
import chart.simple.MultiChart;
import data.series.Messreihe;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import jstat.data.Corpus;
import jstat.data.Document;
import org.apache.lucene.analysis.CharReader;
import org.apache.lucene.analysis.CharStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;

/**
 *
 * @author root
 */
public class CorpusAnalyser {

    public static Messreihe analyseCharacterDistribution(Corpus c, String page) {

        HashMap<Character, Counter> map = new HashMap<Character, Counter>();
        for (Document doc : c.docs) {
            StringBuffer sb = new StringBuffer(doc.html.toString());
            CharSequence cs = sb.subSequence(0, sb.length());
            for (int i = 0; i < cs.length(); i++) {
                char c1 = cs.charAt(i);
                if (!map.containsKey(c1)) {
                    Counter co = new Counter();
                    co.inc();
                    map.put(c1, co);
                } else {
                    Counter co = map.get(c1);
                    co.inc();
                }
            }
        }

        Messreihe mr = new Messreihe();
        mr.setLabel(page);
        for (Object o : map.keySet()) {
            Character ccc = (Character) o;
            double x = ccc.hashCode() * 1.0;
            double y = (double) map.get(o).val;
            mr.addValuePair(x, Math.log(y));
            System.out.println(x + "\t" + y);
        }

        return mr;
    }

    ;
    
    public static Messreihe analyseTermDistribution(Corpus c, String page) throws Exception {

        HashMap<String, Counter> map = new HashMap<String, Counter>();
        for (Document doc : c.docs) {

            StringTokenizer st = new StringTokenizer(stripHTML(doc.html.toString()));

            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (!map.containsKey(s)) {
                    Counter co = new Counter();
                    co.inc();
                    map.put(s, co);
                } else {
                    Counter co = map.get(s);
                    co.inc();
                }
            }
        }

        List l = new ArrayList<String>();
        l.addAll(map.keySet());
        Collections.sort(l);

        Messreihe mr = new Messreihe();
        mr.setLabel(page);
        int i = 0;
        for (Object o : l) {
            i++;
            double x = i * 1.0;
            double y = (double) map.get(o).val;
//            mr.addValuePair( x , Math.log( y ) );
            mr.addValue(y, (String) o);
//            System.out.println( x + "\t" + y);
        }

        return mr;
    }

    ;

    
    private static String stripHTML(String value) throws Exception{
        StringBuilder out = new StringBuilder();
        StringReader strReader = new StringReader(value);
        try {
            HTMLStripCharFilter html = new HTMLStripCharFilter(CharReader.get(strReader.markSupported() ? strReader : new BufferedReader(strReader)));
            char[] cbuf = new char[1024 * 10];
            while (true) {
                int count = html.read(cbuf);
                if (count == -1) {
                    break; // end of stream mark is -1
                }
                if (count > 0) {
                    out.append(cbuf, 0, count);
                }
            }
            html.close();
        } catch (IOException e) {
            throw new Exception( "Failed stripping HTML ... ");
        }
        return out.toString();
    }
}
