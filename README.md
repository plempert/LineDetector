# LineDetector
_This app detects lines and superimposes them unto an image_

## Some examples

### Box

Original: 
![Box Original](https://github.com/plempert/LineDetector/blob/master/imgs/box.jpg?raw=true)

Lines: 
![Box Processed](https://github.com/plempert/LineDetector/blob/master/imgs/box_lines.jpg?raw=true)

### Hallway

Original:
![Hallway Original](https://github.com/plempert/LineDetector/blob/master/imgs/hallway.jpg?raw=true)

Edges:
![Hallway Edges](https://github.com/plempert/LineDetector/blob/master/imgs/hallway_edges.jpg?raw=true)

Edges+Lines:
![Hallway Edges and Lines](https://github.com/plempert/LineDetector/blob/master/imgs/hallway_edges_lines.jpg?raw=true)

Lines:
![Hallway Lines](https://github.com/plempert/LineDetector/blob/master/imgs/hallway_lines.jpg)

## Explanation

The app utilizes the Canny edge detection algorithm and the Hough transform to find edges and lines in a photo. The Hough transform takes a photo that has been run through the edge detector and converts it to an accumulator matrix, which looks like the following:

![Accumulator](https://github.com/plempert/LineDetector/blob/master/imgs/accumulator_cropped.png)

These are a collection of sine waves: each sine wave represents all the lines that pass through a single point. The points where the sine waves intersect represent lines in the original image.


