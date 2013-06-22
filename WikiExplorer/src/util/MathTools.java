/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author root
 */
public class MathTools {

    public static double[] normalizeMax(double[] weights) {
        double max = 0.0;
        for( double v : weights ) { 
            if ( max < v ) max = v;
        }
        for( int i = 0; i < weights.length; i++ ) {
            weights[i] = weights[i] / max;
        }
        return weights;
    }

    public static double[] normalize(double[] weights) {
        double sum = 0.0;
        for( double v : weights ) { 
            sum = sum + v;
        }
        for( int i = 0; i < weights.length; i++ ) {
            weights[i] = weights[i] / sum;
        }
        return weights;
    }

    
    public static double[] logNormalizeMax(double[] weights) {
        double max = 0.0;
        for( double v : weights ) { 
            if ( max < Math.log(v) ) max = v;
        }
        for( int i = 0; i < weights.length; i++ ) {
            weights[i] = Math.log( weights[i] ) / max;
        }
        return weights;
    }
    
}
