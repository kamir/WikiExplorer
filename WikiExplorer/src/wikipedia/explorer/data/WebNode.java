/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia.explorer.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class WebNode {

    public WebNode( URI url ) { 
        uri = url;
    }
    
    public WebNode() {
        try {
            uri = new URI( "http://127.0.0.1:80/index.html" );
        } 
        catch (URISyntaxException ex1) {
            Logger.getLogger(WebNode.class.getName()).log(Level.SEVERE, null, ex1);
        }
    } 
    
    public WebNode( String url ) { 
        try {
            uri = new URI( url );
        } 
        catch (URISyntaxException ex) {
            Logger.getLogger(WebNode.class.getName()).log(Level.SEVERE, null, ex);
            try {
                uri = new URI( "http://127.0.0.1:80/index.html" );
            } 
            catch (URISyntaxException ex1) {
                Logger.getLogger(WebNode.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
    
    URI uri = null;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
    
    
    
}
