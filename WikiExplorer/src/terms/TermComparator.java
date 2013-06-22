/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package terms;

import data.series.Messreihe;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;

/**
 *
 * @author root
 */
public class TermComparator {
    

    public static void main(String[] args) {

        HashMap<String,Double> map = new HashMap<String,Double>();
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);

        map.put("A",99.5);
        map.put("B",67.4);
        map.put("C",67.4);
        map.put("D",67.3);

        System.out.println("unsorted map: "+map);

        sorted_map.putAll(map);

        System.out.println("results: "+sorted_map);
    }
    
    public static Map<String,Double> getMapSortedByValue( Messreihe mr ) { 
        
        HashMap<String,Double> map = new HashMap<String,Double>();
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);

        Iterator it = mr.hashedValues.keySet().iterator();
        while( it.hasNext() ) {
            String k = (String)it.next();
            Double v = (Double)mr.hashedValues.get(k);
            map.put( k , v );
            // System.out.println( k + " " + v );
        }
        sorted_map.putAll(map);

        return sorted_map;
    };
    
}

class ValueComparator implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
