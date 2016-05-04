/*THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING

A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Colvin Zhu
*/

package airbnb;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;


public class RunKMeans {
	
	private String dataFile;
	private String outFile;
	private String delimiter;
	private int K;
	private double threshold;
	private DataRecords data; 
	private List<List<Integer>> clusters;
	
	public RunKMeans(String dataFile, String outFile, String delimiter, int k, double threshold){
		this.dataFile = dataFile;
		this.outFile = outFile;
		this.delimiter = delimiter;
		this.K = k;
		this.threshold = threshold;
	}
	
	public double WSS(List<List<Integer>> clusters, Record[] centroids) {
		double wss = 0;
		Record mi;
		Record x;
		
		for (int i=0; i<clusters.size(); i++){
			mi = centroids[i];
			for (int id=0; id<clusters.get(i).size(); id++){
				x = getDataRecords().getRecord(clusters.get(i).get(id));
				wss += Kmeans.distance(x, mi);
			}
		}
		
		return wss;
	}
	
	public double BSS(Record[] centroids) {
		double bss = 0;
		
		for (int i=0; i<centroids.length; i++){
			for (int j=0; j<centroids.length; j++){
				if (i == j){
					continue;
				}
				bss += Kmeans.distance(centroids[i], centroids[j]);
			}
		}
		
		return bss;
	}
	
	public double TSS(List<List<Integer>> clusters, Record[] centroids){
		return WSS(clusters, centroids) + BSS(centroids);
	}
	
	public void run() throws IOException {
	
		
		data = new DataRecords (dataFile, delimiter, true);
		Kmeans kmeans = new Kmeans(data, K, threshold);
		clusters = kmeans.cluster();
		
		
		
		double wss = WSS(clusters, kmeans.getCentroids());
		double bss = BSS(kmeans.getCentroids());
		double tss = bss + wss;
		
		FileOutputStream fstream = new FileOutputStream(outFile);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fstream));

		for (int c=0; c<clusters.size(); c++) {
			writer.write("*****CLUSTER " + (c+1) + "*****\n");
			writer.newLine();
			for (Integer recordID : clusters.get(c)) {
				writer.write(getDataRecords().getRecord(recordID).toString());
				writer.newLine();
			}
			writer.write("\n");
		}
		
		writer.write("*****PERFORMANCE METRICS*****\n");
		writer.newLine();
		writer.write("K: " + K + "\n");
		writer.newLine();
		writer.write("WSS (Within Cluster Sum of Squared Errors): " + wss + "\n");
		writer.newLine();
		writer.write("BSS (Between Cluster Sum of Squared Errors): " +  bss + "\n");
		writer.newLine();
		writer.write("TSS (Total Sum of Squared Errors): " + tss + "\n");
		
		writer.close();
		fstream.close();
		
	}
	
	public DataRecords getDataRecords(){
		return data;
	}
	
	public List<List<Integer>> getClusters(){
		return clusters;
	}
	
	 
	
}