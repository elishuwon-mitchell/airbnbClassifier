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
	
	//------------------------ Methods --------------------------------
	
	public void addItem(String item){
		data.add(item);
	}
	
	public String getId(){
		return id;
	}
	
	public String getAttrValue(int index){
		return data.get(index);
	}
	
	public String getClassVal(){
		return data.get(data.size() - 1);
	}
}
