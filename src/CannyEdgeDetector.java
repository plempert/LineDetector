import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Patrick on 12/13/2015.
 */
public class CannyEdgeDetector {
    static double[][] theta;

    public static BufferedImage convertToEdgeView(BufferedImage bufferedImage) {
        PLImage processedImage = new PLImage(bufferedImage);

        convertToGrayScale(processedImage);
        applyGaussianFilter(processedImage);
        findIntensityGradient(processedImage);
        applyNonMaximumSuppression(processedImage);
        applyDoubleThreshold(processedImage);

        return processedImage.getBufferedImage();
    }

    static void convertToGrayScale(PLImage img){
        System.out.println("convertToGrayScale() in progress...");
        int r, g, b;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                double red_grayify = 0.299;
                double green_grayify = 0.587;
                double blue_grayify = 0.114;
                r = img.getRed(i,j);
                g = img.getGreen(i,j);
                b = img.getBlue(i,j);
                int grayVal = (int)(red_grayify*r + green_grayify*g + blue_grayify*b);
                r = grayVal;
                g = grayVal;
                b = grayVal;
                img.setPixel(i,j,r,g,b);
            }
        }
        System.out.println("convertToGrayScale() succeeded.");
    }

    public static void applyGaussianFilter(PLImage img){
        System.out.println("applyGaussianFilter() in progress...");
        int k = 2, r, g, b;
        double sigma = 1.0;
        double[][] gaussianMatrix = new double[5][5];
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                gaussianMatrix[i][j] = (1/(2*3.1415*Math.pow(sigma, 2)))*
                        Math.exp(-1 * (Math.pow(-1 * k + j, 2) + Math.pow(-1 * k + i, 2)) / (2 * Math.pow(sigma, 2)));
            }
        }
        double[][] blur_val = new double [img.getWidth()][img.getHeight()];
        double blur_sum;
        for(int i=2 ; i < img.getWidth()-2 ; i++){
            for( int j=2 ; j < img.getHeight()-2 ; j++){
                blur_sum = 0;
                for(int m=0; m<5; m++){
                    for(int n=0; n<5; n++){
                        blur_sum += gaussianMatrix[m][n] *
                                img.getRed(i-2+m,j-2+n);
                    }
                }
                blur_val[i][j]=blur_sum;
            }
        }
        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {
                r = (int) blur_val[i][j];
                g = (int) blur_val[i][j];
                b = (int) blur_val[i][j];
                img.setPixel(i,j,r,g,b);
                //img.setPixel(i, j, Color.argb(Color.alpha(img.getPixel(i, j)), r, g, b));
            }
        }
        System.out.println("applyGaussianFilter() succeeded.");
    }

    public static void findIntensityGradient(PLImage img){
        System.out.println("findIntensityGradient() in progress...");
        int r,g,b;
        int[][] Gx = new int[3][3];
        Gx[0][0]=-1;    Gx[0][1]=0;     Gx[0][2]=1;
        Gx[1][0]=-2;    Gx[1][1]=0;     Gx[1][2]=2;
        Gx[2][0]=-1;    Gx[2][1]=0;     Gx[2][2]=1;

        int[][] Gy = new int[3][3];
        Gy[0][0]=1;     Gy[0][1]=2;     Gy[0][2]=1;
        Gy[1][0]=0;     Gy[1][1]=0;     Gy[1][2]=0;
        Gy[2][0]=-1;    Gy[2][1]=-2;    Gy[2][2]=-1;

        //results of convolution
        double[][] Gxc = new double [img.getWidth()][img.getHeight()];
        double[][] Gyc = new double [img.getWidth()][img.getHeight()];
        double[][] Gc = new double [img.getWidth()][img.getHeight()];
        theta = new double [img.getWidth()][img.getHeight()];

        double convolution_value;

        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {

                convolution_value = 0;
                for(int m=0; m<3; m++){
                    for(int n=0; n<3; n++){
                        convolution_value += Gx[m][n] *
                                img.getRed(i-2+m,j-2+n);
                    }
                }
                Gxc[i][j] = Math.abs(convolution_value);

                convolution_value = 0;
                for(int m=0; m<3; m++){
                    for(int n=0; n<3; n++){
                        convolution_value += Gy[m][n] *
                                img.getRed(i-2+m,j-2+n);
                    }
                }
                Gyc[i][j] = Math.abs(convolution_value);

                Gc[i][j] = Math.sqrt(Math.pow(Gxc[i][j],2)+Math.pow(Gyc[i][j],2));
                theta[i][j] = Math.atan(Gyc[i][j] / Gxc[i][j]);
            }
        }

        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {
                r = (int) Gc[i][j];
                g = (int) Gc[i][j];
                b = (int) Gc[i][j];
                img.setPixel(i,j,r,g,b);
            }
        }
        System.out.println("findIntensityGradient() succeeded.");
    }

    static void applyNonMaximumSuppression(PLImage img){
        System.out.println("nonMaximumSuppression() in progress...");

        int r,g,b;
        double[][] thin = new double [img.getWidth()][img.getHeight()];
        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {
                theta[i][j] = theta[i][j] * 180.0 / (2*3.1415);
                if(theta[i][j]>=157.5 || theta[i][j]<22.5){ //edge runs W-E
                    if(img.getRed(i,j)>img.getRed(i,j-1) &&
                            img.getRed(i,j)>img.getRed(i,j+1))
                        thin[i][j] = img.getRed(i,j);
                    else thin[i][j] = 0;

                }
                else if(theta[i][j]>=22.5 && theta[i][j]<67.5){ //edge runs SW-NE
                    if(img.getRed(i,j)>img.getRed(i-1,j-1) &&
                            img.getRed(i,j)>img.getRed(i+1,j+1))
                        thin[i][j] = img.getRed(i,j);
                    else thin[i][j] = 0;
                }
                else if(theta[i][j]>=67.5 && theta[i][j]<112.5){ //edge runs N-S
                    if(img.getRed(i,j)>img.getRed(i-1,j) &&
                            img.getRed(i,j)>img.getRed(i+1,j))
                        thin[i][j] = img.getRed(i,j);
                    else thin[i][j] = 0;
                }
                else if(theta[i][j]>=112.5 && theta[i][j]<157.5){ //edge runs NW-SE
                    if(img.getRed(i,j)>img.getRed(i+1,j-1) &&
                            img.getRed(i,j)>img.getRed(i-1,j+1))
                        thin[i][j] = img.getRed(i,j);
                    else thin[i][j] = 0;
                }
            }
        }
        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {
                r = (int) thin[i][j];
                g = (int) thin[i][j];
                b = (int) thin[i][j];
                img.setPixel(i,j,r,g,b);
            }
        }
        System.out.println("nonMaximumSuppression() succeeded.");
    }

    static void applyDoubleThreshold(PLImage img){
        System.out.println("applyDoubleThreshold() in progress...");
        int thresh = 30;
        for(int i=2 ; i < img.getWidth()-2 ; i++) {
            for (int j = 2; j < img.getHeight() - 2; j++) {
                int high = thresh;
                int low = thresh;
                if(img.getRed(i,j)>high){
                    img.setPixel(i,j,255,255,255);
                    //img.setPixel(i, j, Color.argb(Color.alpha(img.getPixel(i, j)), 255, 255, 255));
                }
                if(img.getRed(i,j)<low){
                    img.setPixel(i,j,0,0,0);
                    //img.setPixel(i, j, Color.argb(Color.alpha(img.getPixel(i, j)), 0, 0, 0));
                }
            }
        }
        System.out.println("applyDoubleThreshold() succeeded.");
    }

}
