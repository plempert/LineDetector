import org.w3c.dom.ranges.RangeException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Patrick on 12/13/2015.
 */
public class HoughTransform {

    static class SetIndexBoundArray{
        int lower_bound_first;
        int upper_bound_first;
        int size_first;
        int lower_bound_second;
        int upper_bound_second;
        int size_second;
        int [][] arr;
        SetIndexBoundArray(int l1, int u1, int l2, int u2){
            if(!(l1<u1 && l2<u2)){
                throw new RangeException(RangeException.BAD_BOUNDARYPOINTS_ERR, "Bad Boundaries in 2D array!");
            }
            lower_bound_first = l1;
            upper_bound_first = u1;
            lower_bound_second = l2;
            upper_bound_second = u2;
            size_first = u1-l1;
            size_second = u2-l2;
            arr = new int [size_first][size_second];
            for (int i = 0; i < size_first; i++) {
                for (int j = 0; j < size_second; j++) {
                    arr[i][j] = 0;
                }
            }
        }
        private void checkOutOfBounds(int x, int y){
            if(!(x>=lower_bound_first && x<=upper_bound_first)) throw new ArrayIndexOutOfBoundsException(x);
            if(!(y>=lower_bound_second && y<=upper_bound_second)) throw new ArrayIndexOutOfBoundsException(y);
        }
        public int get(int x, int y){
            checkOutOfBounds(x,y);
            return arr[x-lower_bound_first][y-lower_bound_second];
        }
        public void incr(int x, int y){
            checkOutOfBounds(x,y);
            arr[x-lower_bound_first][y-lower_bound_second]++;
        }
        boolean isLocalMaximum(int r, int t){
            int bound = 10;
            int valToCheck = this.get(r,t);
            int max = 0;
            for(int i = r-bound; i < r+bound; i++){
                for(int j = t-bound; j < t+bound; j++){
                    if(i>=lower_bound_first && i<upper_bound_first && j>=lower_bound_second && j<upper_bound_second){
                        if(max < this.get(i,j)) max = this.get(i,j);
                    }
                }
            }
            return max == valToCheck;
        }
    }


    static int r_max;
    static int t_max = 180;
    static int [][] accumulator;
    static SetIndexBoundArray accum;
    static int threshold = 60;
    static ArrayList<Line> lines;


    static void fillAccumulator(PLImage img){
        r_max = img.getWidth()+img.getHeight();
        accum = new SetIndexBoundArray(-r_max,r_max,-t_max,t_max);
        int r;
        double t;

        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {
                if(img.getRed(i,j) == 255){
                    for(int theta = -t_max; theta < t_max; theta++){
                        t = theta*Math.PI/180;
                        r = (int)(i*Math.cos(t) + j*Math.sin(t));
                        accum.incr(r,theta);
                    }
                }
            }
        }


    }



    public static void findLines(BufferedImage bufferedImage){
        lines = new ArrayList<Line>();
        PLImage img = new PLImage(bufferedImage);

        fillAccumulator(img);

        // Draw accumulator
        PLImage accumulatorImg = new PLImage(2*t_max,2*r_max);
        int c;
        for(int r = -r_max; r < r_max; r++) {
            for (int theta = -t_max; theta < t_max; theta++) {
                c = accum.get(r,theta);
                accumulatorImg.setPixel(theta+t_max,r+r_max,c,c,c);
            }
        }

        for(int r = -r_max; r < r_max; r++){
            for(int t = -t_max; t < t_max; t++) {
                if(accum.get(r,t) >= threshold){
                    if(accum.isLocalMaximum(r,t)){
                        lines.add(new Line(r,t));
                        accumulatorImg.setPixel(t+t_max,r+r_max,255,255,255);
                    }
                }
            }
        }

        for(Line i:lines){
            System.out.println(i.r+" "+i.t);
        }

        Main.writeImage(accumulatorImg.getBufferedImage(),"accumulator.jpg");

    }
    
    static void drawLine(PLImage img, Line l){
        double t; // t is theta in radians
        int x,y;

        // Draw vertical lines differently from non-vertical lines
        if(l.t > -15 && l.t < 15){
            for(y=0; y<img.getHeight(); y++){
                t = l.t*Math.PI/180;
                x = (int)((l.r-y*Math.sin(t))/Math.cos(t));
                System.out.println(l.r+" "+l.t+" "+x+" "+y);
                if(x>=0 && x<img.getWidth()){
                    img.setPixel(x,y,255,0,0);
                }
            }
        } else {
            for(x=0; x<img.getWidth(); x++){
                t = l.t*Math.PI/180; // t is theta in radians
                y = (int)((l.r-x*Math.cos(t))/Math.sin(t));
                System.out.println(l.r+" "+l.t+" "+x+" "+y);
                if(y>=0 && y<img.getHeight()){
                    img.setPixel(x,y,255,0,0);
                }
            }
        }




    }

    public static BufferedImage drawLines(BufferedImage img){
        PLImage plimage = new PLImage(img);
        for(Line i: lines){
            drawLine(plimage,i);
        }
        return plimage.getBufferedImage();
    }

}
