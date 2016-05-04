README:

	The java files for this project are for preprocessing the data. We used Weka to run the algorithms for the data mining phase. Included in the zip file are
all the java program neccessary to preprocess that data, the training data file, the output of the data after we clustered it, and the data after all preprocessing 
has been complete. 

	Open Preprocess.java and look for the lines below:
		
		String originalTrainingFile = "C:\\Users\\Elishuwon\\Desktop\\AirBnB Data\\trainingData.csv";
		String clusteredTrainingFileOutput = "C:\\Users\\Elishuwon\\Desktop\\clusteredTrainingData.txt";
		String preprocessedTrainingDataOutput = "C:\\Users\\Elishuwon\\Desktop\\preprocessedTrainingData.csv";

	Change the variable originalTrainingFile to the path of the training data file on our machine. This file in included with the name 'traininData.csv'
		in the project deliverables.
	Change the variale clusteredTrainingFileOutput to the path where you want the output of the clustering to be written to. This file will just show each
		of the clusters and with their corressponding data points as well as the performace for the clustering.
	Change the variable preprocessedTrainingDataOutput to the path where you want the finished clean or preprocessed data to be written to. This is 
		the file that you will upload to Weka to run the classification algorithms on. NOTE: this file must be a .csv file in order to uplaod to Weka.