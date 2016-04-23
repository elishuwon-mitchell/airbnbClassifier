/*THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING

A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Colvin Zhu

Based on AbstractKMeans.java by Jinho D. Choi: 
https://github.com/emory-courses/cs325/blob/master/src/main/java/edu/emory/mathcs/cs325/document/AbstractKmeans.java
*/

package Clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Kmeans {
	DataRecords dataModel;
	int K;
	double threshold;
	Record[] centroids;
	
	public Kmeans (DataRecords data, int k, double threshold){
		setDataModel(data);
		setK(k);
		setThreshold(threshold);
		setCentroids(initCentroids());
	}

	public List<List<Integer>> cluster() {
		Record[] prevCentroids;
		List<List<Integer>> clusters;
		
		do{
			clusters = maximize(centroids);
			prevCentroids = centroids;
			centroids = expect(clusters);
		}while(centroidDistance(prevCentroids, centroids) > threshold);
		
		return clusters;
	}
	
	/*
	 * Initialize centroids; assumes normalized values
	 */
	public Record[] initCentroids() {
		//<attribute, <category, count>>
		Map<Integer, Map<String, Integer>> fq_cat = new HashMap<Integer, Map<String, Integer>>();
		//<attribute, <sorted categories>>
		Map<Integer, ArrayList<String>> cMatrix = new HashMap<Integer, ArrayList<String>>();
		char[] schema = dataModel.getSchema();
		Record record;
		String cAttr;
		Map<String, Integer> count;
		ArrayList<CategoricalCount> cCount = new ArrayList<CategoricalCount>();
		Record[] centroids = new Record[K];
		ArrayList<String> cSorted;
		ArrayList<int[]> priority = new ArrayList<int[]>();
		int numAttr = dataModel.getNumAttr();
		int numRecords = dataModel.getNumRecords();
		Random rand;
		
		//count category instances
		for (int id=0; id<numRecords; id++){
			record = dataModel.getRecord(id);
			for (int a=0; a<numAttr; a++){
				cAttr = record.getCatAttr(a);
				//attribute is categorical
				if (cAttr != null){
					//fq_cat has attribute
					if (!fq_cat.containsKey(a)){
						fq_cat.put(a, new HashMap<String, Integer>());
					}
					
					if (!fq_cat.get(a).containsKey(cAttr)){
						fq_cat.get(a).put(cAttr, 1);
					}else{
						fq_cat.get(a).put(cAttr, fq_cat.get(a).get(cAttr)+1);
					}
				}
			}
		}
		
		//construct category matrix sorted by frequency
		for (int a=0; a<numAttr; a++){
			if (schema[a] == 'c'){
				count = fq_cat.get(a);
				cCount.clear();
				for (Map.Entry<String, Integer> e : count.entrySet()){
					cCount.add(new CategoricalCount(e.getKey(), e.getValue()));
				}
				
				Collections.sort(cCount);
				cMatrix.put(a, new ArrayList<String>());
				for (int c=0; c<cCount.size(); c++){
					cMatrix.get(a).add(cCount.get(c).getCategory());
				}
			}
		}
		
		rand = new Random();
		//initialize priority and centroids
		for (int c=0; c<centroids.length; c++){
			centroids[c] = new Record(numAttr);
			priority.add(new int[2]);
			priority.get(c)[0] = c;
			priority.get(c)[1] = c;
		}
		
		//assign categories to centroids based on frequency
		for (int a=0; a<numAttr; a++){
			if (schema[a] == 'c'){
				cSorted = cMatrix.get(a);
				for (int c=0; c<centroids.length; c++){
					if (priority.get(c)[0] < cSorted.size()){
						centroids[c].setCatAttr(a, cSorted.get(priority.get(c)[0]));	
					}else{
						centroids[c].setCatAttr(a, cSorted.get(cSorted.size()-1));
						priority.get(c)[0] += priority.get(c)[0] - cSorted.size()-1;
					}
					priority.get(c)[0]++;
					priority.get(c)[1]--;
					if (priority.get(c)[1] == 0){
						priority.get(c)[0] = 0;
					}
				}
			}else if (schema[a] == 'n'){
				for (int c=0; c<centroids.length; c++){
					centroids[c].setNumAttr(a, dataModel.getRecord(rand.nextInt(numRecords)).getNumAttr(a));
				}
			}
		}
		
		return centroids;
	}
	
	/*
	 * Adjust centroids
	 */
	public Record[] expect (List<List<Integer>> clusters) {
		int numAttr = dataModel.getNumAttr();
		//Record[] centroids = new Record[clusters.size()];
		Record record;
		
		//key: index	value: sum
		Map<Integer, Double> sums = new HashMap<Integer, Double>(numAttr);
		//key: index	value:attribute	- key: category, value:count
		Map<Integer, HashMap<String, Integer>> frequencies = new HashMap<Integer, HashMap<String, Integer>>(numAttr);
		
		int recordCount = 0 ;
		
		/*
		 * Get sums (numeric) and counts (categorical)
		 */
		//for each cluster
		for (int c=0; c<clusters.size(); c++){
			sums.clear();
			frequencies.clear();
			for (int r=0; r<clusters.get(c).size(); r++){
				record = dataModel.getRecord(clusters.get(c).get(r)); 
				//for each attribute
				for (int a=0; a<numAttr; a++){
					
					//numeric data type; update sum
					if (record.getNumAttr(a) != null){
						if (sums.containsKey(a)){
							sums.put(a, sums.get(a) + record.getNumAttr(a));
						}else{
							sums.put(a, record.getNumAttr(a));
						}
					}
					
					//categorical data type; update frequency
					else if (record.getCatAttr(a) != null){
						if (frequencies.containsKey(a)){
							Map<String, Integer> attribute = frequencies.get(a);
							if (attribute.containsKey(record.getCatAttr(a))){
								attribute.put(record.getCatAttr(a), attribute.get(record.getCatAttr(a)) + 1);
							}else {
								attribute.put(record.getCatAttr(a), 1);
							}
						}else {
							frequencies.put(a, new HashMap<String, Integer>());
						}
					}
				}
				if (!dataModel.getIncRecords().containsKey(clusters.get(c).get(r))){
					recordCount++;
				}
			}
			
			/*
			 * Get mean (numeric) and mode (categorical)
			 */
			for (Map.Entry<Integer, Double> i : sums.entrySet()){
				centroids[c].addNum(i.getKey(), i.getValue() / recordCount);
			}
			
			for (Map.Entry<Integer, HashMap<String, Integer>> i : frequencies.entrySet()){
				String maxKey = "";
				int maxCount = 0;
				
				//find most frequent category in each attribute a
				for (Map.Entry<String, Integer> a : i.getValue().entrySet()){
					if (a.getValue() > maxCount){
						maxCount = a.getValue();
						maxKey = a.getKey();
					}
				}
				
				centroids[c].addCat(i.getKey(), maxKey);
			}
		}
		
		return centroids;
	}
	
	/*
	 * Distance between previous centroids and new centroids
	 */
	public double centroidDistance (Record[] centroidsA, Record[] centroidsB) {
		double maxDist = 0;
		double dist = 0;
		
		for (int c=0; c<centroidsA.length; c++){
			dist = distance(centroidsB[c], centroidsA[c]);
			
			if (dist > maxDist){
				maxDist = dist;
			}
		}
		
		return maxDist;
	}
	
	//assign records to centroids
	public List<List<Integer>> maximize (Record[] centroids) {
		List<List<Integer>> clusters = new ArrayList<List<Integer>>();
		for (int c=0; c<K; c++){
			clusters.add(new ArrayList<Integer>());
		}
		
		int numRecords = dataModel.getNumRecords();
		Record record;
		double min, dist;
		int clusID = 0;
		
		for (int i=0; i<numRecords; i++){
			record = dataModel.getRecord(i);
			min = distance(centroids[0], record);
			clusID = 0;
			
			for (int j=1; j<K; j++){
				dist = distance(centroids[j], record);
				if (dist < min){
					min = dist;
					clusID = j;
				}
			}
			
			clusters.get(clusID).add(i); 
		}
		
		return clusters;
	}
	
	/*
	 * hybridized euclidean (for numeric) and weighted jaccard (for categorical) distance measures
	 */
	public static double distance (Record centroid, Record record) {
		int intersection = 0;
		int union = 0;
		double sum = 0;
		double weight;
		
		for (int i=0; i<record.getSize(); i++){
			//euclidean
			if (record.getNumAttr(i) != null && centroid.getNumAttr(i) != null){
				sum += Math.pow(record.getNumAttr(i) - centroid.getNumAttr(i), 2);
			}
			//jaccard
			else if (record.getCatAttr(i) != null && centroid.getCatAttr(i) != null){
				union++;
				if (centroid.getCatAttr(i).equals(record.getCatAttr(i))){
					intersection++;
				}
			}
		}
		
		double euclideanDist = Math.sqrt(sum);
		double jaccardDist;
		if (union == 0){
			jaccardDist = 0;
		}else{
			jaccardDist = 1 - (double) intersection / union;
		}
		
		weight = (double) union / record.getSize();
		weight /= (1-weight);
		
		return euclideanDist + weight*jaccardDist;
	}

	public DataRecords getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataRecords dataModel) {
		this.dataModel = dataModel;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	
	public Record[] getCentroids() {
		return centroids;
	}

	public void setCentroids(Record[] centroids) {
		this.centroids = centroids;
	}
	
	
	
	private class CategoricalCount implements Comparable<CategoricalCount>{
		String category;
		int count;
		
		private CategoricalCount(String category, int count){
			setCategory(category);
			setCount(count);
		}
		
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}

		@Override
		public int compareTo(CategoricalCount c) {
			if (c.getCount() > this.count){
				return 1;
			}
			else if (c.getCount() == this.count){
				return 0;
			}
			else {
				return -1;
			}
		}
	}
}
