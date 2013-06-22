/*
 * 
 * http://www.javapractices.com/topic/TopicAction.do?Id=207
 * 
 */
package terms; 
import data.series.Messreihe;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author root
 */
public class TermCollectionTools {
    
    static Map<String,Double> globalMap = null; 
    
    static public void initGlobalOrder( Messreihe mr ) { 
        System.out.println( ">> order by : " + mr.label );
        globalMap = TermComparator.getMapSortedByValue( mr );
        System.out.println( globalMap );
    };
    
    public static Messreihe getTermVector( Messreihe mr ) { 
        Messreihe mr2 = new Messreihe();
        
        mr2.setLabel( mr.getLabel() + "_TV");
        
        Iterator it = globalMap.keySet().iterator();
        while( it.hasNext() ) { 
            String k = (String)it.next();
            Double v = (Double)mr.hashedValues.get(k);
            if ( v == null ) v = new Double(0);
            mr2.addValue(v);
            // System.out.println( mr2.getLabel() + " : " + k + " " + v );
        }
        
        return mr2;
    }

    public static void initGlobalOrder(Vector<Messreihe> mrsTermDist, String referenz) {
        Messreihe ref = null;
        for( Messreihe mr : mrsTermDist ) { 
            if ( mr.getLabel().startsWith( referenz ) && ref == null ) { 
                ref = mr;
            };
        }
        initGlobalOrder(ref);
    }
    
}
