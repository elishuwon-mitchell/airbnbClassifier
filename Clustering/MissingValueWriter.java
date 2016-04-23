package Clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingValueWriter {
	DataRecords dataModel;
	List<List<Integer>> clusters;
	char[] schema;
	
	public MissingValueWriter(DataRecords data, List<List<Integer>> clusters){
		dataModel = data;
		this.clusters = clusters;
		schema = dataModel.getSchema();
	}
	
	public void writeFile() throws IOException{
		Record record;
		List<Integer> cluster;
		
		Map<Integer, Double> averages = new HashMap<Integer, Double>();
		Map<Integer, String> modes = new HashMap<Integer, String>();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/Colvin/Documents/cleaned.data"));
		
		for (int C=0; C<clusters.size(); C++){
			cluster = clusters.get(C);
			averages.clear();
			modes.clear();
			
			for (int c=0; c<cluster.size(); c++){
				record = dataModel.getRecord(cluster.get(c));
				//System.out.println(cluster.get(c));
				//fill in missing attribute values
				if (dataModel.getIncRecords().containsKey(cluster.get(c))){
					//System.out.println("id: " + cluster.get(c) + "Record: " + record.toString());
					for (int a=0; a<dataModel.getNumAttr(); a++){
						if (schema[a] == 'n' && record.getNumAttr(a) == null){
							if (!averages.containsKey(a)){
								averages.put(a, getAvgAttrVal(cluster, a));
							}
							record.setNumAttr(a, averages.get(a));
						}else if (schema[a] == 'c' && record.getCatAttr(a) == null){
							if (!modes.containsKey(a)){
								modes.put(a, getMode(cluster, a));
							}
							record.setCatAttr(a, modes.get(a));
						}
					}
				}
				
				//write record to file
				writer.write(record.toString() + "\n");
			}
		}
		
		writer.flush();
		writer.close();
	}
	
	
	public double getAvgAttrVal(List<Integer> cluster, int a){
		Record record;
		int count = 0;
		double sum = 0.0;
		
		if (schema[a] != 'n'){
			return Double.NaN;
		}
		
		for (int r=0; r<cluster.size(); r++){
			if (dataModel.getIncRecords().containsKey(cluster.get(r))){
				continue;
			}
			record = dataModel.getRecord(cluster.get(r));
			sum+=record.getNumAttr(a);
			count++;
		}
		
		return sum / count;
	}
	
	public String getMode(List<Integer> cluster, int a){
		Record record;
		Map<String, Integer> frequency = new HashMap<String, Integer>();
		int maxCount = 0;
		String maxString = "";
		
		for (int r=0; r<cluster.size(); r++){
			if (dataModel.getIncRecords().containsKey(cluster.get(r))){
				continue;
			}
			record = dataModel.getRecord(cluster.get(r));
			if(frequency.containsKey(record.getCatAttr(a))){
				frequency.put(record.getCatAttr(a), frequency.get(record.getCatAttr(a)) + 1);
			}else{
				frequency.put(record.getCatAttr(a), 1);
			}
		}
		
		for(Map.Entry<String, Integer> e : frequency.entrySet()){
			if (e.getValue() > maxCount){
				maxCount = e.getValue();
				maxString = e.getKey();
			}
		}
		
		return maxString;
	}
}
