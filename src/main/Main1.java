package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JComboBox;

import clustering.AntColonyClustering;
import clustering.ClusteringModel;
import dataReader.BreastCancerWisconsinReader;
import dataReader.DermatologyReader;
import dataReader.IrisReader;
import dataReader.Reader;
import evalution.ClusteringEvaluation;
import evalution.DunnIndexEvaluation;
import graph.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Color;
import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.UIManager;

public class Main1 {

    JFrame frame;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    private JTable table;

    JButton btnNewButton_1;
    public static JComboBox comboBox;

    private ClusteringModel cluster;
    private ClusteringEvaluation evaluation;
    int maxDataSetSize = 700;
    Reader reader;
    List<Row> data;

    /**
     * Launch the application.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        /*
		try {
			PrintStream myConsole= new PrintStream (new File("C:\\Users\\Rania\\Documents\\test\\filename.txt"));
			System.setOut(myConsole);
		}catch (IOException e1) {
			System.out.println(e1);
		}
		
		try {
        	
            FileWriter myWriter = new FileWriter("C:\\Users\\Rania\\Documents\\test\\filename.txt");
            myWriter.write("good");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e1) {
            System.out.println("An error occurred.");
            e1.printStackTrace();
          }
         */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main1 window = new Main1();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Create the application.
     */
    public Main1() {
        initialize();

        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String info = (String) comboBox.getItemAt(comboBox.getSelectedIndex());
                evaluation = new DunnIndexEvaluation();
                // get input data
                // reader = new DermatologyReader();
                if (info == "Iris") {
                    reader = new IrisReader();
                } else if (info == "Breast Cancer Wisconsin") {
                    reader = new BreastCancerWisconsinReader();
                } else if (info == "Dermatology") {
                    reader = new DermatologyReader();
                }
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
                new Thread(() -> {
                    runClustering(data);
                    System.out.print("-----------------------------------" + "\n");
                    printClusters();
                }).start();

            }

        });
    }

    public List<Row> reduceDimenseions() {
        PCA pca = new PCA(2);
        data = pca.runPCA(data);
        System.out.println("After dimension reducing");
        return data;
    }

    public void runClustering(List<Row> data) {

        cluster = new AntColonyClustering(data, evaluation);
        cluster.setVisualize(true);
        // cluster.setVisualizeDirectory("output");
        cluster.run();
        //cluster.setStartVisualize(maxDataSetSize);
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

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("CACO");
        frame.getContentPane().setForeground(new Color(0, 0, 205));
        frame.getContentPane().setBackground(new Color(220, 220, 220));
        frame.setBounds(100, 100, 631, 301);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        String[] data = {"Iris", "Breast Cancer Wisconsin", "Dermatology"};

        JPanel panel = new JPanel();
        panel.setBackground(UIManager.getColor("CheckBox.background"));
        panel.setBorder(new LineBorder(new Color(204, 0, 153), 1, true));
        panel.setBounds(10, 11, 286, 59);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        comboBox = new JComboBox(data);
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String info = (String) comboBox.getItemAt(comboBox.getSelectedIndex());
                if (info == "Iris") {
                    textField.setText("150");
                    textField_1.setText("4");
                    textField_2.setText("3");
                } else if (info == "Breast Cancer Wisconsin") {
                    textField.setText("699");
                    textField_1.setText("10");
                    textField_2.setText("2");
                } else if (info == "Dermatology") {
                    textField.setText("366");
                    textField_1.setText("33");
                    textField_2.setText("6");
                }

            }
        });
        comboBox.setFont(new Font("Times New Roman", Font.BOLD, 12));
        comboBox.setBackground(new Color(216, 191, 216));
        comboBox.setForeground(new Color(0, 0, 0));
        comboBox.setBounds(122, 31, 154, 20);
        panel.add(comboBox);

        JLabel lblNewLabel = new JLabel("Select the dataset:");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(10, 34, 107, 14);
        panel.add(lblNewLabel);

        JLabel lblNewLabel_4 = new JLabel("Dataset selection:");
        lblNewLabel_4.setBackground(new Color(0, 0, 205));
        lblNewLabel_4.setForeground(new Color(186, 85, 211));
        lblNewLabel_4.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblNewLabel_4.setBounds(10, 11, 117, 14);
        panel.add(lblNewLabel_4);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(UIManager.getColor("CheckBox.background"));
        panel_1.setLayout(null);
        panel_1.setBorder(new LineBorder(new Color(204, 0, 153), 1, true));
        panel_1.setBounds(10, 91, 286, 125);
        frame.getContentPane().add(panel_1);

        JLabel lblInstancesNumber = new JLabel("Instances number:");
        lblInstancesNumber.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblInstancesNumber.setBounds(10, 34, 105, 14);
        panel_1.add(lblInstancesNumber);

        JLabel lblNewLabel_4_1 = new JLabel("Dataset Parameters:");
        lblNewLabel_4_1.setBackground(new Color(0, 0, 139));
        lblNewLabel_4_1.setForeground(new Color(186, 85, 211));
        lblNewLabel_4_1.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblNewLabel_4_1.setBounds(10, 11, 141, 14);
        panel_1.add(lblNewLabel_4_1);

        textField_2 = new JTextField();
        textField_2.setFont(new Font("Times New Roman", Font.BOLD, 12));
        textField_2.setBounds(149, 94, 86, 20);
        panel_1.add(textField_2);
        textField_2.setColumns(10);

        JLabel lblNewLabel_3 = new JLabel("Features number:");
        lblNewLabel_3.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel_3.setBounds(10, 65, 105, 14);
        panel_1.add(lblNewLabel_3);

        textField_1 = new JTextField();
        textField_1.setFont(new Font("Times New Roman", Font.BOLD, 12));
        textField_1.setBounds(149, 59, 86, 20);
        panel_1.add(textField_1);
        textField_1.setColumns(10);

        JLabel lblNewLabel_3_1 = new JLabel("Clusters number:");
        lblNewLabel_3_1.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel_3_1.setBounds(10, 97, 105, 14);
        panel_1.add(lblNewLabel_3_1);

        textField = new JTextField();
        textField.setFont(new Font("Times New Roman", Font.BOLD, 12));
        textField.setBounds(149, 28, 86, 20);
        panel_1.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Reset");
        btnNewButton.setBackground(new Color(216, 191, 216));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        btnNewButton.setForeground(new Color(0, 0, 0));
        btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnNewButton.setBounds(312, 227, 89, 23);
        frame.getContentPane().add(btnNewButton);

        JPanel panel_3 = new JPanel();
        panel_3.setLayout(null);
        panel_3.setBorder(new LineBorder(new Color(204, 0, 102), 1, true));
        panel_3.setBackground(UIManager.getColor("CheckBox.background"));
        panel_3.setBounds(320, 148, 286, 68);
        frame.getContentPane().add(panel_3);

        JLabel lblNewLabel_4_3 = new JLabel("Download data Clustering result:");
        lblNewLabel_4_3.setForeground(new Color(186, 85, 211));
        lblNewLabel_4_3.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblNewLabel_4_3.setBackground(UIManager.getColor("CheckBox.background"));
        lblNewLabel_4_3.setBounds(10, 11, 211, 23);
        panel_3.add(lblNewLabel_4_3);

        JButton btnDownload = new JButton("Download");
        btnDownload.setBackground(new Color(216, 191, 216));
        btnDownload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        btnDownload.setForeground(new Color(0, 0, 0));
        btnDownload.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnDownload.setBounds(172, 34, 89, 23);
        panel_3.add(btnDownload);

        JLabel lblNewLabel_2 = new JLabel("Data clustering:");
        lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel_2.setBounds(10, 38, 116, 14);
        panel_3.add(lblNewLabel_2);

        JPanel panel_2 = new JPanel();
        panel_2.setBounds(320, 11, 286, 131);
        frame.getContentPane().add(panel_2);
        panel_2.setBackground(UIManager.getColor("CheckBox.background"));
        panel_2.setLayout(null);
        panel_2.setBorder(new LineBorder(new Color(204, 0, 102), 1, true));

        JLabel lblNewLabel_1 = new JLabel("Evaluation measures results:");
        lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel_1.setBounds(10, 34, 160, 14);
        panel_2.add(lblNewLabel_1);

        JLabel lblNewLabel_4_2 = new JLabel("Execution results:");
        lblNewLabel_4_2.setForeground(new Color(186, 85, 211));
        lblNewLabel_4_2.setFont(new Font("Times New Roman", Font.BOLD, 14));
        lblNewLabel_4_2.setBounds(10, 11, 117, 14);
        panel_2.add(lblNewLabel_4_2);

        String mdr[][] = {{"", "Fmeausre", "Dunn Index"},
        {"Exec1", "", ""},
        {"Exec2", "", ""},
        {"Exec3", "", ""}};
        String lol[] = {"", "Dunn index", "FMeasure"};
        JTable table = new JTable(mdr, lol);
        table.setModel(new DefaultTableModel(
                new Object[][]{
                    {"", "Fmeausre", "Dunn Index"},
                    {"Exec1", "", ""},
                    {"Exec2", "", ""},
                    {"Exec3", "", ""},},
                new String[]{
                    "", "Dunn index", "FMeasure"
                }
        ));
        table.setBorder(new LineBorder(new Color(186, 85, 211)));
        table.setFont(new Font("Times New Roman", Font.BOLD, 12));
        table.setBounds(38, 59, 200, 48);

        panel_2.add(table);

        btnNewButton_1 = new JButton("Execute");
        btnNewButton_1.setBackground(new Color(216, 191, 216));

        btnNewButton_1.setForeground(new Color(0, 0, 0));
        btnNewButton_1.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btnNewButton_1.setBounds(215, 227, 89, 23);
        frame.getContentPane().add(btnNewButton_1);

    }
}
