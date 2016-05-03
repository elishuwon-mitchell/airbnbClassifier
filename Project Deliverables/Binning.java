/**
 * 
 */
package airbnb;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.Scanner;

/**
 * @author Elishuwon Mitchell
 *
 */
public class Binning {
	
	/*--------------------------------Read In Data-----------------------------------*/
	/**
	 * 
	 * @param file The file containing the data to be read in.
	 * @return list of list of strings of the data.
	 * @throws FileNotFoundException
	 */
	
	public static ArrayList<ArrayList<String>> readData(String file, String delimitor) throws FileNotFoundException {
		
		ArrayList<ArrayList<String>>  data = new ArrayList<ArrayList<String>>();	
		
		File myFile = new File(file);
		Scanner lineReader = new Scanner(myFile);
		int i = 0;
		while(lineReader.hasNextLine()){
			i++;
			ArrayList<String> tempList = new ArrayList<String>();
			String[] line = lineReader.nextLine().split(delimitor);
			
			for( String s: line){
				tempList.add(s);
			}
			if(tempList.size() != 12){
				System.out.println(tempList.toString() + " " + i);
				continue;
			}
			data.add(tempList);

		}
		
		lineReader.close();
		
		return data;
	}

	/*--------------------------------Bin Attribute-----------------------------------*/
	/**
	 * 
	 * @param data The data
	 * @param indexOfAttr Index of the attribute to be binned 
	 * @param min The minimum value of that attribute
	 * @param binSize The size of the bin or the amount of values desired to be placed in each bin
	 */
	public static void binAttr(ArrayList<ArrayList<String>> data, int indexOfAttr, int min, int binSize){
		
		for(ArrayList<String> list: data){
			int prev = (int)Double.parseDouble(list.get(indexOfAttr));
			int cat = (prev - min) / binSize;
			list.set(indexOfAttr, cat+"");
		}
		
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException{
		
		String inputFile = "C:\\Users\\Elishuwon\\Desktop\\trainingDataNoMissingValues.csv";
		String outputFile =  "C:\\Users\\Elishuwon\\Desktop\\preprocessedTrainingData.csv";
		String delimitor = ",";
		
		PrintWriter writer = new PrintWriter(outputFile) ;
		
		ArrayList<ArrayList<String>> data = readData(inputFile, delimitor);
		
		binAttr(data, 4, 18, 5); //bin age attribute
		binAttr(data, 6, 0, 5); //bin signup_flow attribute


		for(ArrayList<String> a: data){
			writer.print(a.get(0) + ",");
			for(int i = 1; i < a.size()-1; i++){
				writer.print(a.get(i)+",");
			}
			writer.println(a.get(a.size()-1));
		}

		writer.close();
	}
	
	
}
