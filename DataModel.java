
package airbnbClassifier;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @authors Elishuwon Mitchell, Robert Lantry, Colvin Zhu
 *
 */
public class DataModel {
	 
	public static HashMap<String, Record> dataMap; 
	public static HashMap<String, Record> badData;
	
	//----------------------------------- Constructor --------------------------------------------

	public PreProcess(String trainingData, String delimitor, boolean containsHeader) {		
		
		dataMap = new HashMap<String, Record>();
		badData = new HashMap<String, Record>();
		
		try {
			readData(trainingData, delimitor, containsHeader);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
	}
	

	//----------------------------------- Read Data --------------------------------------------

	public static void readData(String trainingData, String delimitor, boolean containsHeader) throws FileNotFoundException {
	
		File file = new File(trainingData);
		Scanner in = new Scanner(file);

		//if the data contains a header in the first line
		if(containsHeader){
			in.nextLine();
		}
		
		
		while(in.hasNextLine()){
			
			String[] line = in.nextLine().split(delimitor);
			
			Record r = new Record(line[0]);
			
			for(int i = 1; i < line.length; i++){
				String item = line[i];
				
				//date_account_created attribute
				if(i == 1){
					item = item.charAt(0) + "";
				}
				
				//Age attribute 
				if(i == 5){
					
					//if missing value
					if(item.isEmpty()){ 
						badData.put(r.getId(), r);
					}
					else{
						int num = Integer.parseInt(item);
						
						if( num > 1900){
							num  = 2016 - num;
						}
						if(num < 18 ){
							num = 18;
						}
						else if(num > 85){
							num = 85;
						}
						
						item = num + "";
					}
			
				}
				
				//first_affilitate_tracked attribute
				if( i == 11){
					if( line[i].isEmpty() ){
						badData.put(r.getId(), r);
					}
				}
				
				r.addItem(item);

			}
			dataMap.put(r.getId(), r);
		}
		in.close();

		
	}
	


}
