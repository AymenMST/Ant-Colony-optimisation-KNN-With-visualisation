package main;

import java.util.ArrayList;
import java.util.List;

import cern.colt.matrix.*;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 *
 * @author Pc
 */
// Principal component analysis to reduce the dimension of dataset
public class PCA {

    private int numPrincipleComponents;
    private List<Row> data;
    private List<Row> transformedData;
    private List<Double> featureMeans;
    private DoubleMatrix2D covarianceMatrix;
    private EigenvalueDecomposition eigenValueDecomp;
    private DoubleMatrix2D principleMatrix;
    private DoubleMatrix2D transformationMatrix;

    //constructor to perform PCA on the dataset
    public PCA(int numPrincipleComponents) {
        this.numPrincipleComponents = numPrincipleComponents;
    }

    /**
     * Driver of the PCA class. Performs all operations necessary to PCA in
     * order
     *
     * @param data list of data to perform PCA on
     * @return list of data with dimensions reduced.
     */
    public List<Row> runPCA(List<Row> data) {
        this.data = data;

        // center all original data points at (expected-actual)
        centerAtZero();

        // build covariance matrix
        calculateCovarianceMatrix();

        // find the eigenvectors of the covariance matrix
        calculateEigenVectors();

        // find the n-largest eigenvalues and keep the eigenvectors of
        // those eigenvalues
        findPrincipleComponents();

        // transform matrix of eigenvectors to make it compatible with
        // multiplication of original data
        constructTransformationMatrix();

        // transform original data by multiplying it with the
        // eigenvectors.
        transformData();
        return transformedData;
    }

    /**
     * Transform original data by multiplying it with the eigenvectors
     */
    private void transformData() {
        transformedData = new ArrayList<Row>(data.size());

        // transform all rows by removing unimportant inputes
        for (Row row : data) {
            transformedData.add(transformDataPoint(row));
        }
    }

    /**
     * Individually remove unimportant inputes from original rows
     *
     * @param row data point with full set of inputes
     * @return new data point with unimportant inputes removed
     */
    private Row transformDataPoint(Row row) {

        List<Double> inputes = row.getInputes();
        double[][] difference = new double[1][inputes.size()];

        // build matrix consisting of original inputes
        for (int i = 0; i < inputes.size(); i++) {
            difference[0][i] = inputes.get(i);
        }
        DoubleMatrix2D diff = new DenseDoubleMatrix2D(difference);
        DoubleMatrix2D newVector = null;

        // multiply the original feature set by the eigenvector matrix
        newVector = diff.zMult(transformationMatrix, newVector);

        // arrange the result from the multiplication in correct matrix form
        List<Double> newInputes = new ArrayList<Double>(newVector.columns());
        for (int i = 0; i < newVector.columns(); i++) {
            newInputes.add(newVector.get(0, i));
        }

        // return a new row of the result. This row has
        // unimportant inputes removed.
        return new Row(newInputes, row.getOutputs());
    }

    /**
     * Build the matrix used to transform the original data
     */
    private void constructTransformationMatrix() {
        double[][] transformationMat = new double[principleMatrix.rows()][numPrincipleComponents];
        int rows = transformationMat.length;
        int columns = transformationMat[0].length;

        //build the transformation matrix by only keeping the number of principle components columns.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transformationMat[i][j] = principleMatrix.get(i, j + (columns - numPrincipleComponents));
            }
        }
        transformationMatrix = new DenseDoubleMatrix2D(transformationMat);
    }

    /**
     * Center all data points at zero. This is done through (actual - expected),
     * where expected is the mean of that feature.
     */
    private void centerAtZero() {
        featureMeans = new ArrayList<>();

        // initiate list of feature means
        for (int i = 0; i < data.get(0).getInputes().size(); i++) {
            featureMeans.add(new Double(0.0));
        }

        // append array of feature means
        for (Row row : data) {
            for (int i = 0; i < row.getInputes().size(); i++) {
                double currentSum = featureMeans.get(i);
                featureMeans.set(i, currentSum + row.getInputes().get(i));
            }
        }

        // find means of data points by dividing sum by size
        for (int i = 0; i < featureMeans.size(); i++) {
            featureMeans.set(i, featureMeans.get(i) / data.size());
        }

        // shift original inputes by subtracting them from the mean
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).getInputes().size(); j++) {
                Double oldValue = data.get(i).getInputes().get(j);
                data.get(i).getInputes().set(j, oldValue - featureMeans.get(j));
            }
        }
    }

    /**
     * Build the covariance matrix by finding the variance of every feature to
     * every other feature.
     */
    private void calculateCovarianceMatrix() {
        int numDimensions = featureMeans.size();
        double[][] covarianceMatrix = new double[numDimensions][numDimensions];

        // construct the covariance matrix by calling the method
        // calculatecovariance.
        // This is done for every combination of data points.
        for (int i = 0; i < covarianceMatrix.length; i++) {
            for (int j = 0; j < covarianceMatrix[0].length; j++) {
                covarianceMatrix[i][j] = calculateCovariance(i, j);
            }
        }
        this.covarianceMatrix = new DenseDoubleMatrix2D(covarianceMatrix);

    }

    /**
     * find the variance of any two feature dimensions.
     *
     * @param dimensionOneIndex index of first feature
     * @param dimensionTwoIndex index of second feature
     * @return variance of two inputes.
     */
    private Double calculateCovariance(int dimensionOneIndex, int dimensionTwoIndex) {

        Double covariance = 0.0;
        Double dimensionOneMean = featureMeans.get(dimensionOneIndex);
        Double dimensionTwoMean = featureMeans.get(dimensionTwoIndex);

        // for each feature index in each data point, find the
        // variance with the second feature index
        for (Row row : data) {
            Double dimensionOneValue = row.getInputes().get(dimensionOneIndex);
            Double dimensionTwoValue = row.getInputes().get(dimensionTwoIndex);
            covariance += ((dimensionOneValue - dimensionOneMean) * (dimensionTwoValue - dimensionTwoMean));
        }

        // average covariance
        covariance /= (featureMeans.size() - 1);
        return covariance;
    }

    /**
     * Use the cern.colt.matrix.* library call to get a matrix of eigenvectors
     * from the covariance matrix
     */
    private void calculateEigenVectors() {
        eigenValueDecomp = new EigenvalueDecomposition(covarianceMatrix);
    }

    /**
     * find the n-largest eigenvalues and the eigenvectors corresponding to them
     */
    private void findPrincipleComponents() {
        DoubleMatrix2D eigenVectors = eigenValueDecomp.getV();
        DoubleMatrix1D eigenValues = eigenValueDecomp.getRealEigenvalues();

        // sort the eigenvalues and re-arrange the eigenvector matrix at the same time
        for (int i = 0; i < eigenValues.size(); i++) {
            for (int j = 0; j < eigenValues.size(); j++) {
                if (eigenValues.get(j) < eigenValues.get(i)) {
                    double eigenValueTemp = eigenValues.get(i);
                    eigenValues.set(i, eigenValues.get(j));
                    eigenValues.set(j, eigenValueTemp);

                    // swap values in the eigenvector matrix if j eigenvalues are smaller than i
                    for (int k = 0; k < eigenVectors.columns(); k++) {
                        double eigenVectorTemp = eigenVectors.get(i, k);
                        eigenVectors.set(i, k, eigenVectors.get(j, k));
                        eigenVectors.set(j, k, eigenVectorTemp);
                    }
                }
            }
        }

        principleMatrix = eigenVectors;
    }

    public List<Row> getData() {
        return data;
    }

    public void setData(List<Row> data) {
        this.data = data;
    }

}
