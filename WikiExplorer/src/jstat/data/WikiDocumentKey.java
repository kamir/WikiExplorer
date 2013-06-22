/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jstat.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.http.client.utils.URLEncodedUtils;
import wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class WikiDocumentKey implements Writable, WritableComparable {
    
    WikiNode wn = null;
    String group = null;
    
    public WikiDocumentKey( Document doc ) { 
        this.wn = doc.wn;
        this.group = doc.group;
    }

    public WikiDocumentKey() {
        
    }

    public void write(DataOutput dout) throws IOException {
        dout.writeInt( wn.page.getBytes().length );
        dout.writeBytes( wn.wiki );
        dout.writeInt( wn.wiki.getBytes().length );
        dout.writeBytes( wn.page );
        dout.writeInt( group.getBytes().length );
        dout.writeBytes( group );
    }    

    public void readFields(DataInput din) throws IOException {
        int l = din.readInt();
        byte[] b = new byte[l];
        din.readFully(b);
        String w = new String(b);
        
        l = din.readInt();
        b = new byte[l];
        din.readFully(b);
        String p = new String( b );
        
        l = din.readInt();
        b = new byte[l];
        din.readFully(b);
        group = new String( b );
        wn = new WikiNode( w, p );
    }

    public int compareTo(Object o2) {
        String so1 = this.toString();
        String so2 = o2.toString();
        return so1.compareTo(so2);
    }
    
    public String toString() { 
        String c = wn.page;
        return group +"\t" + wn.wiki + "\t" + c ;
    }    
}
