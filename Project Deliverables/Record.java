package airbnb;

public class Record {
	private final int size;
	private String[] cat;
	private Double[] num;
	private String classLabel;
	
	public Record(int size) {
		this.size = size;
		cat = new String[size];
		num = new Double[size];
	}
	
	public void addCat (int a, String val) {
		if (indexInBounds(a)){
			cat[a] = val;
		}
	}
	
	public void addNum (int a, Double val) {
		if (indexInBounds(a)){
			num[a] = val;
		}
	}
	
	public String getCatAttr(int a) {	
		if (indexInBounds(a) && cat[a] != null){
			return cat[a];
		}
		return null;
	}
	
	public void setCatAttr(int a, String val) {
		if (indexInBounds(a)){
			cat[a] = val;
		}
	}
	
	public Double getNumAttr(int a) {
		if (indexInBounds(a) && num[a] != null){
			return num[a];
		}
		return null;
	}
	
	public void setNumAttr(int a, Double val) {
		if (indexInBounds(a)){
			num[a] = val;
		}
	}
	
	public void setClassLabel(String classLabel){
		this.classLabel = classLabel;
	}
	
	public String getClassLabel(){
		return classLabel;
	}
	
	public int getSize() {
		return size;
	}
	
	public String toString() {
		String s = getClassLabel() + ",";
		
		if (getCatAttr(0) != null){
			s += getCatAttr(0);
		}else if (getNumAttr(0) != null){
			s += getNumAttr(0).toString();
		}
		
		for (int i=1; i<size; i++) {
			if (getCatAttr(i) != null){
				s += "," + getCatAttr(i);
			}else if (getNumAttr(i) != null){
				s += "," + getNumAttr(i).toString();
			}
		}
		
		return s;
	}
	
	private boolean indexInBounds(int a) {
		if (a >=0 && a < size){
			return true;
		}
		return false;
	}
}