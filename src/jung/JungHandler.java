package jung;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import graph.Edge;
import graph.Graph;
import graph.Node;


/**
 *
 * @author Pc
 */
public class JungHandler {

    private Layout<Node, Edge> layout;
    private BasicVisualizationServer<Node, Edge> viewer;
    private BufferedImage image;
    private JFrame frame = new JFrame("Clustering Visualization");
    private int width = 800;
    private int height = 600;
    private int padding = 25;
    private boolean saveImages = false;
    private String imageFolder;
    private int imageCount = 1;
    private int startVisualize = 0;

    /**
     * Handle the visualization component of these clusters.
     *
     * The JUNG library (Java Universal Networking and Graphing) is used.
     */
    public JungHandler() {

    }

    /**
     * Update the visualizer with a new graph object. This re-renders the
     * visualization to show a new graph
     *
     * @param graph
     */
    public void setGraph(Graph graph) {
        layout = new TreeLayout<>(graph);
        viewer = new BasicVisualizationServer<>(layout);

        // Map nodes to their internally stored colors
        Transformer<Node, Paint> vertexColor = new Transformer<Node, Paint>() {
            public Paint transform(Node node) {
                return node.getColor();
            }
        };
        viewer.getRenderContext().setVertexFillPaintTransformer(vertexColor);

        //put each node in a 2-D spot on the graph
        for (Node n : graph.getVertices()) {
            layout.setLocation(n, n.getLocation());
        }

        frame.add(viewer, BorderLayout.CENTER);
    }

    /**
     * Renders the graph. This can be rendered to (1) the JUNG window or (2) a
     * large list of images.
     */
    public void draw() {
        if (imageCount > startVisualize) {

            //set the frame to the correct size
            frame.setSize(width, height);
            if (saveImages) {
                try {
                    //save images in a directory
                    frame.pack();
                    image = new BufferedImage(viewer.getWidth(), viewer.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics2D = image.createGraphics();
                    viewer.print(graphics2D);
                    graphics2D.dispose();
                    ImageIO.write(image, "jpeg", new File(imageFolder + "/" + imageCount + ".jpg"));
                } catch (Exception exception) {
                    System.out.println("Could not save image");
                }
            } else {

                //display graph to window
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        }
        imageCount++;
    }

    /**
     * Sets the dimension of the visualization viewer
     *
     * @param width	width of frame
     * @param height	height of frame
     */
    public void setDimensions(int width, int height) {
        this.width = width + padding * 2;
        this.height = height + padding * 2;
    }

    /**
     * method to save images to a certain file.
     *
     * @param folder directory to save images to
     */
    public void saveImagesTo(String folder) {
        File directory = new File(folder);
        if (directory.isDirectory()) {
            this.imageFolder = directory.getAbsolutePath();
            this.saveImages = true;
            frame.setUndecorated(true);
        } else {
            throw new IllegalArgumentException("Invalid Image Folder.");
        }
    }

    /**
     * boolean to return if we are saving images or not
     *
     * @return
     */
    public boolean isSavingImages() {
        return saveImages;
    }

    /**
     * set the visualization frame to the correct size
     *
     * @param frame
     */
    public void setStartVisualize(int frame) {
        this.startVisualize = frame;
    }

}
