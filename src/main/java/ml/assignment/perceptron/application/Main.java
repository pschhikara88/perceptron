package ml.assignment.perceptron.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ml.assignment.perceptron.algorithm.CollaborativeFiltering;
import ml.assignment.perceptron.algorithm.Perceptron;
import ml.assignment.perceptron.domain.Movie;
import ml.assignment.perceptron.domain.UserVote;
import ml.assignment.perceptron.technical.DataReader;
import ml.assignment.perceptron.utility.DataConvertor;
import ml.assignment.perceptron.utility.RemoveUtils;
import ml.assignment.perceptron.utility.WeightUtils;

public class Main {
	
	public static void main(String args[]) throws IOException
	{
		//int totalDoc=0;
		//String folderName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\chineseData";
		String folderArgument= args[0];
		String folderName = "Data"+File.separator+"Perceptron"+File.separator+folderArgument;
		//String folderName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\Perceptron\\data-set1";
		//String folderName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\Perceptron\\enron1";
		int noOfIteration=Integer.valueOf(args[1]);
		float learningRate=Float.valueOf(args[2]);
		
		
		Map<String,ArrayList<Map<String,Long>>> trainingData = DataReader.getDataBasedOnClassification(folderName+File.separator+"train");
		
		Map<String,ArrayList<Map<String,Long>>> testData = DataReader.getDataBasedOnClassification(folderName+File.separator+"test");
		
		/*for(String className : trainingData.keySet())
		{
			totalDoc =totalDoc + trainingData.get(className).size();
		}*/
		
	
		
		HashMap<String,Double> weightVector = WeightUtils.initializeWeightVector(ApplicationData.totalVocabData); 
		
		//HashSet<String> stopWords = DataReader.getStopWordsSet("C:\\Users\\SRPOP\\Desktop\\Gogate\\HW2\\stopword.csv");
		HashSet<String> stopWords = DataReader.getStopWordsSet("Data"+File.separator+"stopword.csv");
		HashMap<String,Double> weightVectorWithoutStopWords = RemoveUtils.removeStopWordsFromWeightVectorReturnNewMap(weightVector, stopWords);
		
		ArrayList<Map<String,Long>> docList= null;
		int o=0;
		int t=0;
		boolean breakFlag=false;
		/*for(int i=0;i<noOfIteration;i++)
		{
			for(String className :trainingData.keySet() )
			{
				t=className.equalsIgnoreCase("ham")?1:-1;
				docList = trainingData.get(className);
				for(Map<String,Long> docData : docList)
				{
				  o = Perceptron.sigmoidUnitExecution(docData, weightVector);
				  Perceptron.updateWeightVector(docData, weightVector, learningRate, t, o);
				  breakFlag = checkAccuracy(trainingData,weightVector);
				  if(breakFlag)
					  break;
				}
				if(breakFlag)
					  break;
			}
			if(breakFlag)
				  break;
		}*/
		
		for(int i=0;i<noOfIteration;i++)
		{
			for(String className :trainingData.keySet() )
			{
				t=className.equalsIgnoreCase("ham")?1:-1;
				docList = trainingData.get(className);
				for(Map<String,Long> docData : docList)
				{
				  o = Perceptron.sigmoidUnitExecution(docData, weightVector);
				  if(t!=o)
					 Perceptron.updateWeightVector(docData, weightVector, learningRate, t, o);
				//  breakFlag = checkAccuracy(trainingData,weightVector);
				}
			}
		}
		
		float testAccuracy = checkAccuracyPercentage(testData,weightVector);
		
		
		
		Map<String,Long> totalVoacbWithoutStopWord = RemoveUtils.removeStopWordsFromTotalVocabReturnNewMap(ApplicationData.totalVocabData,stopWords);
		
		weightVector = WeightUtils.initializeWeightVector(totalVoacbWithoutStopWord); 
		
		for(int i=0;i<noOfIteration;i++)
		{
			for(String className :trainingData.keySet() )
			{
				t=className.equalsIgnoreCase("ham")?1:-1;
				docList = trainingData.get(className);
				for(Map<String,Long> docData : docList)
				{
				  o = Perceptron.sigmoidUnitExecution(docData, weightVectorWithoutStopWords);
				  if(t!=o)
					 Perceptron.updateWeightVector(docData, weightVectorWithoutStopWords, learningRate, t, o);
				//  breakFlag = checkAccuracy(trainingData,weightVector);
				}
			}
		}
		
		float testAccuracy1 = checkAccuracyPercentage(testData,weightVectorWithoutStopWords);
		System.out.println("Number of iteration : "+ noOfIteration);
		System.out.println("Learning Rate : "+ learningRate);
		System.out.println("Accuracy on test data without removing stopwords : " + testAccuracy);
		System.out.println("Accuracy on test data with removing stopwords : " + testAccuracy1);
		///System.out.print(noOfIteration+","+ testAccuracy+","+testAccuracy1);
		
		
		
		
		
		
	}
	
	private static boolean checkAccuracy(Map<String,ArrayList<Map<String,Long>>> testData,HashMap<String,Double> weightVector)
	{
		ArrayList<Map<String,Long>> docList= null;
		int o=0;
		int t=0;
		for(String className :testData.keySet() )
		{
			t=className.equalsIgnoreCase("ham")?1:-1;
			docList = testData.get(className);
			for(Map<String,Long> docData : docList)
			{
				o = Perceptron.sigmoidUnitExecution(docData, weightVector);
				if(t!=o)
					return false;	
			}
		}
		return true;
		
	}
	
	private static float checkAccuracyPercentage(Map<String,ArrayList<Map<String,Long>>> testData,HashMap<String,Double> weightVector)
	{
		ArrayList<Map<String,Long>> docList= null;
		int o=0;
		int t=0;
		int successCount=0;
		int totalCount=0;
		for(String className :testData.keySet() )
		{
			t=className.equalsIgnoreCase("ham")?1:-1;
			docList = testData.get(className);
			for(Map<String,Long> docData : docList)
			{
				o = Perceptron.sigmoidUnitExecution(docData, weightVector);
				if(t==o)
					successCount = successCount+1;
				totalCount = totalCount + 1;
			}
		}
		return 100 * ((float)successCount)/totalCount;
		
	}

}
