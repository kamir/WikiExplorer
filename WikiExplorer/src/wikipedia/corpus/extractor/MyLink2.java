/*
 * Create a network for WikiPages with respect to
 * interwikilinks and layers within different 
 * languages.
 * 
 * LAYERS show also the representation of different
 * concepts in different languages.
 * 
 */
package wikipedia.corpus.extractor;

/**
 *
 * @author kamir
 */
public class MyLink2 {
    
    int uniqueIdSRC = 0;
    int uniqueIdDEST = 0;
    
    public String wikiSRC = null;
    public String wikiDEST = null;
        
    public String source = null;
    public String dest = null;

    int uniqueIdWikiSRC;
    int uniqueIdWikiDEST;

    public int iwl = 0;
    public int direct = 0;
    
    int cnID;
    
    public MyLink2 clone() { 
        MyLink2 l = new MyLink2();
        l.dest = dest;
        l.wikiDEST = wikiDEST;
        l.wikiSRC = wikiSRC;
        l.source = source;
        l.iwl = iwl;
        l.direct = direct;
        l.uniqueIdWikiSRC = uniqueIdWikiSRC;
        l.uniqueIdWikiDEST = uniqueIdWikiDEST;
        l.uniqueIdSRC = uniqueIdSRC;
        l.uniqueIdDEST = uniqueIdDEST;
        
        return l;
    }

    @Override
    public String toString() {
        String t = "\t";
        return uniqueIdSRC + t + uniqueIdDEST + t + source.trim() + t + dest.trim() + t + uniqueIdWikiSRC + t + uniqueIdWikiDEST + t + wikiSRC + t + wikiDEST + t + iwl + t + direct + t + cnID;
    }
    
    public static String getHeadline() {
        String t = "\t";
        return  "Source" + t + "Target" + t + "lSource" + t + "lTarget" + t + "uniqueIdWikiDEST" + t + "uniqueIdWikiDEST" + t + "wikiSRC" + t + "wikiTarget" + t + "iwl" + t + "direct" + t + "cnID";
    }

    String getKeySRC() {
        return wikiSRC + "." + source.trim();
    }
    String getKeyDEST() {
        return wikiDEST + "." + dest.trim();
    }
    
}
