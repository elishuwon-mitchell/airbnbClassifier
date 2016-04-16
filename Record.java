/**
 * 
 */
package airbnbClassifier;

import java.util.ArrayList;

/**
 * @author Elishuwon Mitchell, Robert Lantry, Colvin Zhu
 *
 */
public class Record {
	
	private String id;
	private ArrayList<String> data;
	
	
	//----------------------- Constructor ----------------------

	public Record(String id){
		this.id = id;
		data = new ArrayList<String>();
	}
	
	//------------------------ Set Methods --------------------------------
	
	public void addAttr(String item){
		data.add(item);
	}
	
	public void updateAttrVal(int index, String val){
		data.set(index, val);
	}
	
	//------------------------ Get Methods --------------------------------

	public String getId(){
		return id;
	}
	
	public String getAttrVal(int index){
		return data.get(index);
	}
	
	public String getClassLabel(){
		return data.get(data.size() - 1);
	}
	
	public ArrayList<String>  getData(){
		return data;
	}
}
