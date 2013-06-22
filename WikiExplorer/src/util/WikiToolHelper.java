package util;

import org.wikipedia.Wiki;

/**
 *
 * @author kamir
 */
public class WikiToolHelper {
    
    /**
     * isCleanPagename
     * 
     * just look for ",< and >
     * give back null if strange symbols are in.
     * 
     * @param link
     * @return 
     */
    public static String isCleanPagename(String link) {

        if ( link.contains( "\"" ) || link.contains(">") || link.contains( "<") ) {
            return null;
        }
        else return link;
    }
    
//    public static String codeBack( String s ) { 
//        Wiki w = new Wiki();
//        
//        return s + "+~";
//    }
    
}
