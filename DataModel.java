
package airbnbClassifier;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @authors Elishuwon Mitchell, Robert Lantry, Colvin Zhu
 *
 */
public class DataModel {
	 
	private static HashMap<String, Record> dataMap; //map containing all the data
	private static ArrayList<String> badData; //list containing IDs of tuples with missing values 
	
	//----------------------------------- Constructor --------------------------------------------

	public DataModel(String trainingData, String delimitor, boolean containsHeader) {		
		
		dataMap = new HashMap<String, Record>();
		badData = new ArrayList<String>();
		
		try {
			readData(trainingData, delimitor, containsHeader);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
	}

	//----------------------------------- Get Methods --------------------------------------------
	public HashMap<String, Record> getData(){
		return dataMap;
	}
	
	public ArrayList<String> getBadData(){
		return badData;
	}

	//----------------------------------- Read Data --------------------------------------------

	public static void readData(String trainingData, String delimitor, boolean containsHeader) throws FileNotFoundException {
	
		//used to store min and max values and index 0 and 1 respectively
		//used for normailization.
		double[] timeStamp = new double[2]; 
		timeStamp[0] = Double.MAX_VALUE; 
		timeStamp[1] = Double.MIN_VALUE;
		
		double[] age = new double[2];
		age[0] = Double.MAX_VALUE; 
		age[1] = Double.MIN_VALUE;

		
		File file = new File(trainingData);
		Scanner in = new Scanner(file);

		//if the data contains a header in the first line
		if(containsHeader){
			in.nextLine();
		}
		
		
		while(in.hasNextLine()){
			
			String[] line = in.nextLine().split(delimitor);
			
			Record r = new Record(line[0]); //storing id
			
			for(int i = 1; i < line.length; i++){ //start at next attribute
				String item = line[i];
				
				//date_account_created attribute
				if(i == 1){
					item = item.charAt(0) + "";
				}
				
				//timestamp_first_active attribute
				else if( i == 2){
					
					//min max determination
					double temp = Double.parseDouble(new BigDecimal(Double.valueOf(item)).toString());
					if( temp < timeStamp[0])
						timeStamp[0] = temp;
					if( temp > timeStamp[1])
						timeStamp[1] = temp;
				}
				
				//Age attribute				
				else if( i == 5){
					
					if(item.isEmpty()){ 
						badData.add(r.getId());
					}
					else{
						
						boolean ignore = false;
						int num = Integer.parseInt(item);
						
						//blanks out the outliers 
						if(num < 18 || num > 85){ 
							ignore = true;
							item = "";
							badData.add(r.getId());
						}
						
						if(!ignore){
							//min max determination
							if( num < age[0])
								age[0] = num;
							if( num > age[1])
								age[1] = num;
						}
					}

				}
				
				//first_affilitate_tracked attribute
				else if( i == 11){
					if( line[i].isEmpty() ){
						badData.add(r.getId());
					}
				}
				
				r.addAttr(item);

			}
			dataMap.put(r.getId(), r);
		}
		in.close();
		
		/* Note that since the id of the tuple is stored as the id
		 * for the Record object, the data for the record object starts
		 * with the date_account_created attribute at index 0. This prevents
		 * the storing of the id value twice.
		*/
		
		//Normalize timeStamp attr
		normalizeAttr(1, timeStamp[0], timeStamp[1], 0, 1);
		
		//Normalize age attr
		normalizeAttr(4, age[0], age[1], 0, 1);


	}
	
	//----------------------------------- Normalize Attribute --------------------------------------------
	/**
	 * 
	 * Implements min-max normailization and updates the appropriate
	 * values in the data to their normalized form.
	 * 
	 * @param indexOfAttr Index of attribute to normalize in data
	 * @param oldMin Value of old minimum
	 * @param oldMax Value of old maximum
	 * @param newMin Value of new minimum
	 * @param newMax Value of new maximum
	 */
	public static void normalizeAttr(int indexOfAttr, double oldMin, double oldMax, double newMin, double newMax){
				
		for( Record r : dataMap.values()){
			String temp = r.getAttrVal(indexOfAttr);
			
			if(!temp.isEmpty()){

				double val = Double.parseDouble(new BigDecimal(Double.valueOf(temp)).toString());
				double normVal = ((val - oldMin)/(oldMax - oldMin)) * (newMax - newMin) + newMin;
				r.updateAttrVal(indexOfAttr, normVal + "" );
	

			}
		}
		
		
	}
	
	


}
