package dataReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.Row;

/**
 * Abstract class for parsing input files into rows.
 */
public abstract class Reader {
	
	protected String name = "Unnamed";
	protected int inputs;
	protected int outputs;
	protected List<Row> data;
	protected List<String> possibleClasses;

	/**
	 * Abstract method to parse the file and create the list of data
	 * points. Needs to be implemented for each parser.
	 */
	public abstract void parseFile();

	/**
	 * Abstract method to build and find all the output classes. This
	 * information is stored in the possibleClasses list.
	 * 
	 * This information will eventually be used to determine the
	 * meaning of output neurons for non-numerical classification
	 * problems. The index in which a class exists in this possible
	 * classes list corresponds to which output neuron we hope get
	 * selected.
	 */
	public abstract void findClasses();

	/**
	 * @return	getter function for inputs (inputes).
	 */
	public int getInputs() {
		return inputs;
	}

	/**
	 * @return	getter function for outputs.
	 */
	public int getOutputs() {
		return outputs;
	}

	/**
	 * @return	getter function for the list of rows that was created.
	 */
	public List<Row> getData() {
		return data;
	}
	
	/**
	 * Used to get the output vector in the case of classification.
	 * 
	 * @param classname	The name of the class that is the target output.
	 * @return			A vector with 1.0 in the index of the specified class and 0.0 everywhere else.
	 */
	public List<Double> getOutputVector(String classname) {
		// find the index of the target class
		int classIndex = possibleClasses.indexOf(classname);
		List<Double> outputs = new ArrayList<>();
		// build a vector of target outputs
		for (int output = 0; output < possibleClasses.size(); output++) {
			if (output == classIndex)	// place a 1.0 in the index of the target class
				outputs.add(1.0);
			else						// place a 0.0 everywhere else
				outputs.add(0.0);
		}
		return outputs;
	}
	
	public void truncate(int remainingSize){
		
		if (data.size() < remainingSize) {
			return;
		}
		
		Random rand = new Random(11235);
		int toRemove = data.size() - remainingSize;
		
		for (int i = 0; i < toRemove; i++) {
			data.remove(rand.nextInt(data.size()));
		}
	
	}
	
	public String toString() {
		return name;
	}

}
