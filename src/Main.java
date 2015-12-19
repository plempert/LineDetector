import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Patrick on 12/13/2015.
 */
public class Main {

    public static void main(String [] args){
        BufferedImage original;
        original = readImage("playing_card.jpg");

        BufferedImage edges = PLImage.deepCopy(original);
        edges = CannyEdgeDetector.convertToEdgeView(edges);
        writeImage(edges, "playing_card_edges.jpg");


        BufferedImage edges_lines = PLImage.deepCopy(edges);
        HoughTransform.findLines(edges_lines);

        edges_lines = HoughTransform.drawLines(edges_lines);
        writeImage(edges_lines,"playing_card_edges_lines.jpg");

        BufferedImage lines = PLImage.deepCopy(original);
        lines = HoughTransform.drawLines(lines);
        writeImage(lines, "playing_card_lines.jpg");
    }

    public static BufferedImage readImage(String str){
        BufferedImage img;
        try {
            img = ImageIO.read(new File(str));
            return img;
        } catch (IOException e) {
            // ...
        }
        return null;
    }

    public static void writeImage(BufferedImage img, String str){
        try {
            // retrieve image
            File outputfile = new File(str);
            ImageIO.write(img, "jpg", outputfile);
        } catch (IOException e) {
            // ...
        }
    }

}
