package ml.assignment.perceptron.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ml.assignment.perceptron.algorithm.CollaborativeFiltering;
import ml.assignment.perceptron.domain.UserVote;
import ml.assignment.perceptron.technical.DataReader;

public class CFMain {
	
	public static void main(String args[]) throws IOException
	{
		// Netflix filtering 
		//String movieFileName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\Netflix\\movie_titles.txt";
		String folderName = args[0];
		String trainVoteFileName = "Data"+File.separator+"CF"+File.separator+folderName+File.separator+"TrainingRatings.txt";
		//String trainVoteFileName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\Netflix\\TrainingRatings.txt";
		String testVoteFileName = "Data"+File.separator+"CF"+File.separator+folderName+File.separator+"TestingRatings.txt";
		//String testVoteFileName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\Netflix\\TestingRatings.txt";
		//String testVoteFileName = "C:\\Users\\SRPOP\\Desktop\\Gogate\\HW3\\Netflix\\sample.txt";
		//ArrayList<Movie> movieList = DataReader.readNetflixMovieInfo(movieFileName);
		
		//ArrayList<UserVote> trainUserVoteList = DataReader.readNetflixVoteInfo(trainVoteFileName);
	
		
		//float[][] corelationMatrix = new float[][];
		
		//HashMap<String,ArrayList<UserVote>> userVoteMapByUserId = DataConvertor.getUserVoteByUserId(trainUserVoteList);
		//HashMap<String,ArrayList<UserVote>> userVoteMapByUserIdTest = DataConvertor.getUserVoteByUserId(testUserVoteList);
		
		HashMap<String,HashMap<String,Float>> userVoteMapByUserId = DataReader.readNetflixVoteInfoInMapWithMap(trainVoteFileName);
		//HashMap<String,ArrayList<UserVote>> userVoteMapByUserId = DataReader.readNetflixVoteInfoInMap(trainVoteFileName);
		HashMap<String,ArrayList<UserVote>> userVoteMapByUserIdTest = DataReader.readNetflixVoteInfoInMap(testVoteFileName);
		//ArrayList<UserVote> testUserVoteList = DataReader.readNetflixVoteInfo(testVoteFileName);
		
		//System.out.println(testUserVoteList);
	
		HashMap<String,Integer> indexMap = new HashMap<String,Integer>();
		int i=0;
		for(String userId : userVoteMapByUserId.keySet())
		{
			indexMap.put(userId, i);
			i++;
		}
		//i--;
		/*for(String userId : userVoteMapByUserIdTest.keySet())
		{
				indexMap.put(userId, i);
				i++;
			}
		}*/
		//Double j = (;
		//i=(i*(i+1))/2;
		//System.out.println();
		//System.out.println(i);
		//Float[][] corelationMatrix = new Float[i][i];
		//Double[] corelationMatrix = new Double[i];
		//System.out.println(corelationMatrix.length);
		
		/*int count =0;
		HashSet<String> testUserList = new HashSet<String>();
		for(String userId : userVoteMapByUserIdTest.keySet())
		{
			for(UserVote userVote : userVoteMapByUserIdTest.get(userId))
			{
			if(userVoteMapByUserId.get(userId)!=null)
			{
				count++;
				//testUserList.add(userVote.getUserId());
			}
			}
		}
		System.out.println(testUserVoteList.size());
		System.out.println(count);
		*/
		HashMap<String,Double> meanVoteByUser = CollaborativeFiltering.meanVoteByUser2(userVoteMapByUserId);
		
		//HashMap<String,Double> meanVoteByMovie = CollaborativeFiltering.meanVoteByUser2(ApplicationData.movieUserInfoMap);
		
		//System.out.println(CollaborativeFiltering.createCorrelationMatrix(testUserList, userVoteMapByUserId, meanVoteByUser, indexMap, i));
		
		//HashMap<String,Double> meanVoteByUser = CollaborativeFiltering.meanVoteByUser(userVoteMapByUserId);
		
		
		//CollaborativeFiltering.testAccuracyByCFOnNetFlix(userVoteMapByUserIdTest, userVoteMapByUserId, meanVoteByUser,indexMap,corelationMatrix);
		//CollaborativeFiltering.testAccuracyByCFOnNetFlix1(testUserVoteList, userVoteMapByUserId, meanVoteByUser,indexMap,corelationMatrix);
		//CollaborativeFiltering.testAccuracyByCFOnNetFlix1(testUserVoteList, userVoteMapByUserId, meanVoteByUser);
		CollaborativeFiltering.testAccuracyByCFOnNetFlix2(userVoteMapByUserIdTest, userVoteMapByUserId, meanVoteByUser,indexMap,i);
		
		//CollaborativeFiltering.testAccuracyByCFOnNetFlix3(userVoteMapByUserIdTest, userVoteMapByUserId, meanVoteByUser,indexMap,i,meanVoteByMovie);
	}

}
