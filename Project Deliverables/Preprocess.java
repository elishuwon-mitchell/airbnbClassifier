import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author Elishuwon Mitchell
 *
 */
public class Preprocess {
	
	public static void main(String[] args) throws IOException{
		
		String originalTrainingFile = "C:\\Users\\Elishuwon\\Desktop\\AirBnB Data\\trainingData.csv";
		String clusteredTrainingFileOutput = "C:\\Users\\Elishuwon\\Desktop\\clusteredTrainingData.txt";
		String preprocessedTrainingDataOutput = "C:\\Users\\Elishuwon\\Desktop\\preprocessedTrainingData.csv";
		
		
		
		String delimiter = ",";
		int K = 7;
		double threshold = 0.0001;
		
		System.out.println("Running Clustering....");
		RunKMeans cluster = new RunKMeans(originalTrainingFile, clusteredTrainingFileOutput, delimiter,  K, threshold);
		cluster.run();
		System.out.println("Done.\n");
		
		System.out.println("Filling in missing values....");
		MissingValueWriter mvw = new MissingValueWriter(cluster.getDataRecords(), cluster.getClusters());
		ArrayList<ArrayList<String>> data = mvw.fillMissingValues();
		System.out.println("Done.\n");
		
		System.out.println("Binning Attributes....");
		Binning.binAttr(data, 4, 18, 5); //bin age attribute
		Binning.binAttr(data, 6, 0, 5); //bin signup_flow attribute
		System.out.println("Done.\n");

		System.out.println("Printing Preprocessed Data");
		PrintWriter writer = new PrintWriter(preprocessedTrainingDataOutput);
		

		for(ArrayList<String> a: data){

			writer.print(a.get(0) + ",");
			for(int i = 1; i < a.size()-1; i++){
				writer.print(a.get(i)+",");
			}
			writer.println(a.get(a.size()-1));
		}
		writer.close();
		System.out.println("Done.\n");
		
		System.out.println("Preprocessing Complete.");
	}
	
}
