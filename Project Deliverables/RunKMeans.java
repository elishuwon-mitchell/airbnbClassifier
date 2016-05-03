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
	private static String dataFile;
	private static String outFile;
	private static String delimiter;
	private static int K;
	private static double threshold;
	private static DataRecords data; 
	
	public static double WSS(List<List<Integer>> clusters, Record[] centroids) {
		double wss = 0;
		Record mi;
		Record x;
		
		for (int i=0; i<clusters.size(); i++){
			mi = centroids[i];
			for (int id=0; id<clusters.get(i).size(); id++){
				x = data.getRecord(clusters.get(i).get(id));
				wss += Kmeans.distance(x, mi);
			}
		}
		
		return wss;
	}
	
	public static double BSS(Record[] centroids) {
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
	
	public static double TSS(List<List<Integer>> clusters, Record[] centroids){
		return WSS(clusters, centroids) + BSS(centroids);
	}
	
	public static void main(String[] args) throws IOException {
			/*
			 * Command Line arguments:
			 * 0 - data file path (String)
			 * 1 - load entire dataset (boolean)
			 * 2 - output file path (String)
			 * 3 - delimiter (String)
			 * 4 - K (int)
			 * 5 - centroid change threshold (double)
			 */
		
/*			if (args.length == 6){
				dataFile = args[0];
				outFile = args[2];
				delimiter = args[3];
				K = Integer.parseInt(args[4]);
				threshold = Double.parseDouble(args[5]);
			}
			else{
				System.err.println("Usage:\nDataFilePath: /Users/username/documents/iris.csv"
					+ "true"
					+ "\nOutFilePath: /Users/username/documents/iris.out"
					+ "\nK: 2"
					+ "\ndelimiter: \",\""
					+ "\nthreshold: .001");
				System.exit(0);
			}
*/

		dataFile = "C:\\Users\\Elishuwon\\Desktop\\AirBnB Data\\trainingData.csv";
		outFile = "C:\\Users\\Elishuwon\\Desktop\\clusteredTrainingData.txt";
		delimiter = ",";
		K = 7;
		threshold = 0.0001;
		
		data = new DataRecords (dataFile, delimiter, true);
		Kmeans kmeans = new Kmeans(data, K, threshold);
		List<List<Integer>> clusters = kmeans.cluster();
		
		MissingValueWriter mvw = new MissingValueWriter(data, clusters);
		mvw.writeFile();
		
		double wss = WSS(clusters, kmeans.getCentroids());
		double bss = BSS(kmeans.getCentroids());
		double tss = bss + wss;
		
		FileOutputStream fstream = new FileOutputStream(outFile);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fstream));

		for (int c=0; c<clusters.size(); c++) {
			writer.write("*****CLUSTER " + (c+1) + "*****\n");
			writer.newLine();
			for (Integer recordID : clusters.get(c)) {
				writer.write(data.getRecord(recordID).toString());
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
}