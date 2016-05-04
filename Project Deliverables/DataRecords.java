/*THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING

A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Colvin Zhu*/

package airbnb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Data model to access dataset file
 * 		Attributes: labeled as id's 0 to (number of attributes-1)
 * 			first column is skipped because it is the class label
 * 		Records: labeled as id's 0 to (number of records-1)
 */
public class DataRecords {
	private String filePath;
	private int numAttr;
	private String delimiter;
	private char[] schema;
	private int numRecords;
	private Set<String> nullChars;
	private Map<Integer, ArrayList<Integer>> incRecords;
	private boolean load;
	private Record[] data;
	
	public DataRecords (String filePath, String delimiter, boolean load) {
		setFilePath(filePath);
		setDelimiter(delimiter);
		setSchema();
		setNumRecords();
		setNullChars(new HashSet<String>(), "", "-unknown-");
		if (load) {
			data = new Record[numRecords];
			loadData();
		}
		this.load = load;
		setIncRecords();
	}
	
	private void loadData() {
		String s;
		String[] attributes;
		Record record;
		
		//read line as string
		try{
			BufferedReader bReader = new BufferedReader(new FileReader(filePath));
			s = bReader.readLine();	//skip first line
			for (int id=0; id<=numRecords; id++){
				s = bReader.readLine();
				if (s == null) continue;
				
				//convert line to Record
				attributes = s.split(delimiter);
				record = new Record(numAttr);
				for (int i=0; i<numAttr; i++){
					if (schema[i] == 'c'){
						if (nullChars.contains(attributes[i])){
							record.addCat(i, null);
						}else{
							record.addCat(i, attributes[i]);
						}
					}else if (schema[i] == 'n'){
						if (nullChars.contains(attributes[i])){
							record.addNum(i, null);
						}else if( i == 3 && (Double.parseDouble(attributes[i]) <18 || Double.parseDouble(attributes[i]) > 85)){
							record.addNum(i, null);
						}
						else{
							record.addNum(i, Double.parseDouble(attributes[i]));
						}
					}else if (schema[i] == 'H'){
						record.setClassLabel(attributes[i]);
					}
				}
				data[id] = record;
			}
			bReader.close();
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	/*Schema: first line of file that specifies attribute types
	 *	'c': categorical
	 *	'n': numeric
	 *	'H': class label
	 **/
	public void setSchema (){
		String s = "";
		String[] line;
		char[] schema;
		
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(filePath));
			s = bReader.readLine();
			bReader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		line = s.split(delimiter);
		setNumAttr(line.length);
		schema = new char[numAttr];
		for (int i=0; i<line.length; i++){
			schema[i] = line[i].charAt(0);
		}
		
		this.schema = schema;
	}
	
	public char[] getSchema () {
		return schema;
	}
	
	/*
	 * get pre-loaded record or load record from file as a Record object
	 */
	public Record getRecord (int id) {
		if (load && id>=0 && id<data.length){
			return (data[id]);
		}else{
			String s = null;
			String[] attributes;
			Record record;
			
			//read line as string
			try{
				BufferedReader bReader = new BufferedReader(new FileReader(filePath));
				for (int i=-1; i<=id; i++){
					s = bReader.readLine();
				}
				bReader.close();
			}catch (IOException e){
				System.out.println(e.getMessage());
				return null;
			}
			if (s == null) return null;
			
			//convert line to Record
			attributes = s.split(delimiter);
			record = new Record(numAttr);
			for (int i=0; i<numAttr; i++){
				if (schema[i] == 'c'){
					if (nullChars.contains(attributes[i])){
						record.addCat(i, null);
					}else{
						record.addCat(i, attributes[i]);
					}
				}else if (schema[i] == 'n'){
					if (nullChars.contains(attributes[i])){
						record.addNum(i, null);
					}else{
						record.addNum(i, Double.parseDouble(attributes[i]));
					}
				}else if (schema[i] == 'H'){
					record.setClassLabel(attributes[i]);
				}
			}
			
			return record;
		}
	}
	
	public void setIncRecords() {
		Record record;
		Map<Integer, ArrayList<Integer>> incRecords = new HashMap<Integer, ArrayList<Integer>>();
		
		for (int id=0; id<numRecords; id++){
			record = getRecord(id);
			for (int a=0; a<numAttr; a++){
				if (record == null){
					System.out.println(id);
					System.out.println(data[id]);
				}
				if (schema[a] == 'c' && record.getCatAttr(a) == null || schema[a] == 'n' && record.getNumAttr(a) == null){
					if (incRecords.containsKey(id)){
						incRecords.get(id).add(a);
					}else{
						incRecords.put(id, new ArrayList<Integer>());
					}
				}
			}
		}
		
		this.incRecords = incRecords;
	}
	
	public Map<Integer, ArrayList<Integer>> getIncRecords(){
		return incRecords;
	}
	
	public void setNumRecords() {	
		int count = 0;
		try{
			BufferedReader bReader = new BufferedReader(new FileReader(filePath));
			bReader.readLine(); //skip schema
			String s = bReader.readLine();
			while(s!=null && !"".equals(s)){
				count++;
				s = bReader.readLine();
			}
			bReader.close();
			numRecords = count;
		}catch (IOException e){
			System.out.println(e.getMessage());
			numRecords = -1;
		}
	}
	
	public int getNumRecords() {
		return numRecords;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getNumAttr() {
		return numAttr;
	}

	public void setNumAttr(int numAttr) {
		this.numAttr = numAttr;
	}
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Set<String> getNullChars() {
		return nullChars;
	}

	public void setNullChars(Set<String> nullChars, String... s) {
		String[] strings = s;
		for (String string : strings){
			nullChars.add(string);
		}
		this.nullChars = nullChars;
	}
	
	public Record[] getAllRecords(){
		return data;
	}
}