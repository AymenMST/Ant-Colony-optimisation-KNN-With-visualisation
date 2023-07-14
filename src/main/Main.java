package main;

import dataReader.IrisReader;
import java.util.ArrayList;
import java.util.List;

import clustering.*;
import dataReader.BreastCancerWisconsinReader;
import dataReader.DermatologyReader;
import dataReader.Reader;

import evalution.DunnIndexEvaluation;
import evalution.ClusteringEvaluation;
import graph.Node;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Pc
 */
public class Main {

    private ClusteringModel cluster;
    private ClusteringEvaluation evaluation;
    int maxDataSetSize = 700;
    Reader reader;
    List<Row> data;

    public Main() {

        evaluation = new DunnIndexEvaluation();
        // get input data
      //  reader = new IrisReader();
        reader = new DermatologyReader();
       // reader = new BreastCancerWisconsinReader();
        // parse data
        reader.parseFile();
        reader.truncate(maxDataSetSize);
        data = reader.getData();
        System.out.print("-----------------------------------------" + "\n" + "\n");
        System.out.print(reader + "\n" + "\n");
        System.out.print("-----------------------------------------" + "\n");
        System.out.println("Before dimension reducing");
        System.out.print("-----------------------------------------" + "\n");
        printDataset(data);
        System.out.print("-----------------------------------" + "\n");
       // data = reduceDimenseions();
       // System.out.print("-----------------------------------" + "\n");
      //  printDataset(data);
        System.out.print("-----------------------------------" + "\n");
        runClustering(data);
        System.out.print("-----------------------------------" + "\n");
        printClusters();
    }

    public List<Row> reduceDimenseions() {
        PCA pca = new PCA(2);
        data = pca.runPCA(data);
        System.out.println("After dimension reducing");
        return data;
    }

    public void runClustering(List<Row> data) {

        cluster = new AntColonyClustering(data, evaluation);

        //cluster.setVisualize(true);
        //cluster.setVisualizeDirectory("output");

            cluster.run(); 





        // cluster.setStartVisualize(maxDataSetSize);
    }
    // Print each cluster elements;

    public void printClusters() {
        List<List<Node>> clusters = cluster.getClusters();
        for (int i = 0; i < clusters.size(); i++) {
            //System.out.println(clusters);
            int counter = i;
            System.out.print("Cluster :" + String.valueOf(counter + 1) + "\n");
            System.out.print("-----------------------------------" + "\n");
            List<Node> p = clusters.get(i);
            for (int j = 0; j < p.size(); j++) {
                System.out.print("[");
                Node node = p.get(j);
                Row dP = node.getDataPoint();
                dP.printInputs();
                System.out.print("]--->[");
                dP.printOutputs();
                System.out.println("]");
            }
            System.out.print("-----------------------------------" + "\n");
        }
    }

    public void printDataset(List<Row> data) {
        for (int i = 0; i < data.size(); i++) {
            System.out.println(i + 1 + " : " + data.get(i).getInputes() + "--->" + data.get(i).getOutputs());

        }
    }

    public static void main(String[] args) {
        new Main();
    }

}
