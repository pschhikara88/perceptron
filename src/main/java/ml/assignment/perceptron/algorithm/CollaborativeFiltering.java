package ml.assignment.perceptron.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ml.assignment.perceptron.application.ApplicationData;
import ml.assignment.perceptron.domain.UserVote;
import ml.assignment.perceptron.utility.Utils;

public class CollaborativeFiltering {
	
	public static HashMap<String,Double> meanVoteByUser(HashMap<String,ArrayList<UserVote>> voteListByUserId)
	{
		HashMap<String,Double> userVoteMeanMap = new HashMap<String,Double>();
		int movieCount = 0;
		double countValue =0.0f;
		ArrayList<UserVote> voteList=null;
		for(String userId : voteListByUserId.keySet())
		{
			countValue =0.0f;
			voteList = voteListByUserId.get(userId);
			movieCount = voteList.size();
			for(UserVote userVote : voteList)
			{
				countValue = countValue + userVote.getVoteValue();
			}
			userVoteMeanMap.put(userId, countValue/movieCount);
		}
		return userVoteMeanMap;
	}
	
	public static HashMap<String,Double> meanVoteByUser2(HashMap<String,HashMap<String,Float>>  voteListByUserId)
	{
		HashMap<String,Double> userVoteMeanMap = new HashMap<String,Double>();
		int movieCount = 0;
		double countValue =0.0f;
		HashMap<String,Float> voteMap=null;
		for(String userId : voteListByUserId.keySet())
		{
			countValue =0.0f;
			voteMap = voteListByUserId.get(userId);
			movieCount = voteMap.size();
			for(String movieId : voteMap.keySet())
			{
				countValue = countValue + voteMap.get(movieId);
			}
			userVoteMeanMap.put(userId, countValue/movieCount);
		}
		return userVoteMeanMap;
	}
	
	
	public static HashMap<String,Double> createCorelationMap(HashMap<String,ArrayList<UserVote>> voteListByUserId, HashMap<String,Double> meanVoteByUser)
	{
		HashMap<String,Double> correlationMap = new HashMap<String,Double>();
		String correlationId = null;
		double correlation=0.0f;
		ArrayList<UserVote> userVoteList1=null;
		ArrayList<UserVote> userVoteList2=null;
		for(String userId1 : voteListByUserId.keySet())
		{
			userVoteList1 = voteListByUserId.get(userId1);
			for(String userId2 : voteListByUserId.keySet())
			{
				userVoteList2 = voteListByUserId.get(userId2);
				correlationId = Utils.createCorrelationId(userId1, userId2);
				if(!userId1.equals(userId2) ||  !correlationMap.containsKey(correlationId))
				{
					
					correlation = calculateCorrelation(userVoteList1,userVoteList2, meanVoteByUser);
					correlationMap.put(correlationId, correlation);
				}
			}
		}
		return correlationMap;
	}
	
	private static double calculateCorrelation(ArrayList<UserVote> userVoteList1, ArrayList<UserVote> userVoteList2,HashMap<String,Double> meanVoteByUser)
	{
		double diffSquare1 = 0.0;
		double diffSquare2= 0.0;
		double diff1=0.0f;
		double diff2=0.0f;
		double numerator=0.0f;
		boolean exitFlag = false;
		double denominator=0.0f;
		for(UserVote userVote1 : userVoteList1 )
		{
			exitFlag=false;
			for(UserVote userVote2 : userVoteList2)
			{
				if(userVote2.getMovieId().equals(userVote1.getMovieId()))
				{
					diff1 =  (userVote1.getVoteValue()-meanVoteByUser.get(userVote1.getUserId()));
					diff2 =  (userVote2.getVoteValue()-meanVoteByUser.get(userVote2.getUserId()));
					numerator = numerator + diff1*diff2;
					diffSquare1=diffSquare1 + Math.pow(diff1, 2);
					diffSquare2=diffSquare2 + Math.pow(diff2, 2);
					break;
				}
			}
	
		}
		denominator = diffSquare1*diffSquare2;
		if(numerator==0.0 || denominator==0.0)
			return 0.0f;
		return numerator/Math.sqrt(denominator);
	}
	
	private static double calculateCorrelation2(HashMap<String,Float> userVoteMap1, HashMap<String,Float> userVoteMap2,HashMap<String,Double> meanVoteByUser,String userId,String userId1)
	{
		double diffSquare1 = 0.0;
		double diffSquare2= 0.0;
		double diff1=0.0f;
		double diff2=0.0f;
		double numerator=0.0f;
		boolean exitFlag = false;
		double denominator=0.0f;
		float vote1=0.0f;
		float vote2=0.0f;
		double meanVote1 =meanVoteByUser.get(userId) ;
		double meanVote2= meanVoteByUser.get(userId1);
		HashSet<String> userList=null;
		String movieId=null;
		for (Map.Entry<String,Float> entry : userVoteMap1.entrySet())
		{
			movieId = entry.getKey();
			//userList = ApplicationData.movieUserInfoMap.get(movieId);
			if(userVoteMap2.containsKey(movieId))
			{
				vote1 =  entry.getValue();
				vote2 = userVoteMap2.get(movieId);
				diff1 =  (vote1-meanVote1);
				diff2 =  (vote2-meanVote2);
				numerator = numerator + diff1*diff2;
				diffSquare1=diffSquare1 + Math.pow(diff1, 2);
				diffSquare2=diffSquare2 + Math.pow(diff2, 2);
			}
		}
		denominator = diffSquare1*diffSquare2;
		if(numerator==0.0 || denominator==0.0)
			return 0.0f;
		return numerator/Math.sqrt(denominator);
	}
	
	public static void testAccuracyByCFOnNetFlix(HashMap<String,ArrayList<UserVote>> testUserVote,HashMap<String,ArrayList<UserVote>> trainingData,
			/*HashMap<String,double> corelationMap,*/HashMap<String,Double> meanVoteByUser,HashMap<String,Integer> indexMap,Double[] corelationMatrix)
	{
		double calculatedVote=0.0f;
		int totalUser = trainingData.size();
		//String userId=null;
		String movieId=null;
		double normalizationConstant = 0.001f;
		double summationTerm=0.0;
		ArrayList<UserVote> tempUserVoteList = null;
		UserVote tempUserVote=null;
		String tempCorrelationId=null;
		double absoluteMeanError = 0.0;
		double rootMeanSquare =0.0;
		double errorValue=0.0f;
		//ArrayList<double> normalizationContantList = new ArrayList<double>(); 
		double constantSumForNormalizationContant = 0.0f;
		
		double correlationValue=0.0f;
		/*for(UserVote userVote : testUserVote)
		{
			userId = userVote.getUserId();
			movieId = userVote.getMovieId();
			
			for(String currentUserId : trainingData.keySet())
			{
				tempUserVote=null;
				tempUserVoteList = trainingData.get(currentUserId);
				for(UserVote cuurentUserVote :  tempUserVoteList)
				{
					if(cuurentUserVote.getMovieId().equals(movieId))
					{
						tempUserVote = cuurentUserVote;
						break;
					}
				}
				if(tempUserVote!=null)
				{
					tempCorrelationId = Utils.createCorrelationId(userId, currentUserId);
					if(ApplicationData.correlationMap.containsKey(tempCorrelationId))
					{
						correlationValue = ApplicationData.correlationMap.get(tempCorrelationId);
					}
					else
					{
						correlationValue = calculateCorrelation(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser);
						//normalizationContantList.add(correlationValue);
						ApplicationData.correlationMap.put(tempCorrelationId,correlationValue);
					}
					constantSumForNormalizationContant = constantSumForNormalizationContant+correlationValue;
			//	summationTerm = summationTerm + corelationMap.get(tempCorrelationId)*(tempUserVote.getVoteValue() - meanVoteByUser.get(currentUserId));
					summationTerm = summationTerm + correlationValue*(tempUserVote.getVoteValue() - meanVoteByUser.get(currentUserId));
				}
			}*/
		int index1;
		int index2;
		int index;
		String currentUserId=null;
		for(String userId : testUserVote.keySet())
		{
			//userId = userVote.getUserId();
			constantSumForNormalizationContant =0;
			summationTerm =0;
			tempUserVoteList = testUserVote.get(userId);
			if(trainingData.get(userId)!=null)
			{
				for(UserVote userVote : tempUserVoteList)
				{
					movieId = userVote.getMovieId(); 
					
					
					for (Map.Entry<String,ArrayList<UserVote>> entry : trainingData.entrySet())
					//for(String currentUserId : trainingData.keySet())
					{
						currentUserId = entry.getKey();
						tempUserVoteList = entry.getValue();
						for(UserVote cuurentUserVote :  tempUserVoteList)
						{
							if(cuurentUserVote.getMovieId().equals(movieId))
							{
								tempUserVote = cuurentUserVote;
								break;
							}
						}
				
						if(tempUserVote!=null)
						{
							index1 = indexMap.get(userId);
							index2 = indexMap.get(currentUserId);
							if(index1>index2)
								{
								//j * N - (j - 1) * j / 2 + i - j;
								//index=(index1)*(index1+1)+index2;
								index = (index2*totalUser) - ((index2-1)*index2)/2 + index1-index2;
								}
							else
								{//i * N - (i - 1) * i / 2 + j - i;
								index=(index1*totalUser) - ((index1-1)*index1)/2 + index2-index1;
								}
						//	System.out.println(index1+"\t"+index2+"\t"+index+"\t"+totalUser+"\t"+corelationMatrix.length);
							if(corelationMatrix[index]==null)
							{
								correlationValue =  calculateCorrelation(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser);
								corelationMatrix[index] = correlationValue;
							}
							else
							{
								correlationValue = corelationMatrix[index];
							}
							//System.out.println(currentUserId + "     " +  correlationValue );
							constantSumForNormalizationContant = constantSumForNormalizationContant+Math.abs(correlationValue);
							
							summationTerm = summationTerm + correlationValue*(tempUserVote.getVoteValue() - meanVoteByUser.get(currentUserId));
						}
						
						
					}
					
					if(constantSumForNormalizationContant==0.0)
					{
						normalizationConstant = 0.0;
					}
					else
					{
						normalizationConstant = 1/constantSumForNormalizationContant;
					}
					
					//System.out.println("meanVote - "+meanVoteByUser.get(userId) + "   summ "+ summationTerm);
					calculatedVote = meanVoteByUser.get(userId) + normalizationConstant*summationTerm;
					//System.out.println(meanVoteByUser.get(userId) + "\t"+ constantSumForNormalizationContant );
					errorValue = Math.abs(calculatedVote-userVote.getVoteValue());
					//System.out.println("pvote - "+calculatedVote);
					absoluteMeanError = absoluteMeanError + Math.abs(errorValue);
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
		
					
						
				}
			}
			else
			{
				for(UserVote userVote : tempUserVoteList)
				{
					errorValue = userVote.getVoteValue();
					absoluteMeanError = absoluteMeanError + errorValue;
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
				}
			}
				
				
				/*if(tempUserVote!=null)
				{
					tempCorrelationId = Utils.createCorrelationId(userId, currentUserId);
					if(ApplicationData.correlationMap.containsKey(tempCorrelationId))
					{
						correlationValue = ApplicationData.correlationMap.get(tempCorrelationId);
					}
					else
					{
						correlationValue = calculateCorrelation(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser);
						//normalizationContantList.add(correlationValue);
						ApplicationData.correlationMap.put(tempCorrelationId,correlationValue);
					}
					constantSumForNormalizationContant = constantSumForNormalizationContant+correlationValue;
			//	summationTerm = summationTerm + corelationMap.get(tempCorrelationId)*(tempUserVote.getVoteValue() - meanVoteByUser.get(currentUserId));
					summationTerm = summationTerm + correlationValue*(tempUserVote.getVoteValue() - meanVoteByUser.get(currentUserId));
				}*/
			
			
		}
		System.out.println("Absolute Mean Error : " + absoluteMeanError/ApplicationData.totalUserList.get(1));
		System.out.println("Root Mean Square Error : " + Math.sqrt(rootMeanSquare/ApplicationData.totalUserList.get(1)));
	}
	
	
	public static void testAccuracyByCFOnNetFlix1(ArrayList<UserVote> testUserVote,HashMap<String,HashMap<String,Float>> trainingData,
			/*HashMap<String,double> corelationMap,*/HashMap<String,Double> meanVoteByUser)
	{
		double calculatedVote=0.0f;
		int totalUser = trainingData.size();
		String userId=null;
		String movieId=null;
		double normalizationConstant = 0.001f;
		double summationTerm=0.0;
		ArrayList<UserVote> tempUserVoteList = null;
		UserVote tempUserVote=null;
		String tempCorrelationId=null;
		double absoluteMeanError = 0.0;
		double rootMeanSquare =0.0;
		double errorValue=0.0f;
		double constantSumForNormalizationContant = 0.0f;
		
		double correlationValue=0.0f;
		int index1;
		int index2;
		int index;
		Float currentVote=null;
		String currentUserId=null;
		int count=0;
		int totalCount=0;
		double diffVote=0.0;
		for(UserVote userVote : testUserVote)
		{
			count++;
			userId = userVote.getUserId();
			movieId = userVote.getMovieId(); 

			if(trainingData.get(userId)!=null)
			{
				constantSumForNormalizationContant =0;
				summationTerm =0;

				for (Map.Entry<String,HashMap<String,Float>> entry : trainingData.entrySet())
					{
						currentUserId = entry.getKey();
						currentVote=entry.getValue().get(movieId);

						correlationValue=0.0;
						if(currentVote!=null)
						{
							
							diffVote = currentVote - meanVoteByUser.get(currentUserId);
							if(userId.equals(currentUserId))
							{
								correlationValue=1.0;
							}
							else if(diffVote!=0.0 )
							{
								correlationValue =  calculateCorrelation2(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser,userId,currentUserId);
							}
							constantSumForNormalizationContant = constantSumForNormalizationContant+Math.abs(correlationValue);
							
							summationTerm = summationTerm + correlationValue*(currentVote - meanVoteByUser.get(currentUserId));
						}
						
						
					}
					
					if(constantSumForNormalizationContant==0.0)
					{
						normalizationConstant = 0.0;
					}
					else
					{
						normalizationConstant = 1/constantSumForNormalizationContant;
					}
					calculatedVote = meanVoteByUser.get(userId) + normalizationConstant*summationTerm;
					errorValue = Math.abs(calculatedVote-userVote.getVoteValue());
					absoluteMeanError = absoluteMeanError + Math.abs(errorValue);
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
					System.out.println("count : "+count +"  userId : "+userId+ " movieId : "+movieId+ " absoluteMeanError - "+absoluteMeanError + "\t : " + rootMeanSquare);
	
			}
			else
			{
					errorValue = userVote.getVoteValue();
					absoluteMeanError = absoluteMeanError + errorValue;
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
					System.out.println("count : "+count +"userId : "+userId+ " movieId : "+movieId+ " absoluteMeanError - "+absoluteMeanError + " : " + rootMeanSquare);
			}
			
			
		}
		System.out.println("Absolute Mean Error : " + absoluteMeanError/ApplicationData.totalUserList.get(1));
		System.out.println("Root Mean Square Error : " + Math.sqrt(rootMeanSquare/ApplicationData.totalUserList.get(1)));
	}
	
	public static void testAccuracyByCFOnNetFlix2(HashMap<String,ArrayList<UserVote>> testUserVote,HashMap<String,HashMap<String,Float>> trainingData,
			/*HashMap<String,double> corelationMap,*/HashMap<String,Double> meanVoteByUser,HashMap<String,Integer> indexMap,int i)
	{
		Double[] correlationMatrix =null;
		double calculatedVote=0.0f;
		int totalUser = trainingData.size();
		String userId=null;
		String movieId=null;
		double normalizationConstant = 0.001f;
		double summationTerm=0.0;
		ArrayList<UserVote> tempUserVoteList = null;
		UserVote tempUserVote=null;
		String tempCorrelationId=null;
		double absoluteMeanError = 0.0;
		double rootMeanSquare =0.0;
		double errorValue=0.0f;
		double constantSumForNormalizationContant = 0.0f;
		
		Double correlationValue=null;
		int index1;
		int index2;
		int index;
		Float currentVote=null;
		String currentUserId=null;
		int count=0;
		int totalCount=0;
		double diffVote=0.0;
		for(Map.Entry<String,ArrayList<UserVote>> testEntry : testUserVote.entrySet())
		{
			correlationMatrix = new Double[i];
			userId = testEntry.getKey();
			//movieId = userVote.getMovieId(); 
			tempUserVoteList = testEntry.getValue();
			
			if(trainingData.get(userId)!=null)
			{
				//createCorrelationMatrix(userId, trainingData, meanVoteByUser, indexMap,correlationMatrix);
				for(UserVote userVote : tempUserVoteList)
				{
					movieId = userVote.getMovieId(); 
					constantSumForNormalizationContant =0;
					summationTerm =0;
					count++;
					for (Map.Entry<String,HashMap<String,Float>> entry : trainingData.entrySet())
					{
						currentUserId = entry.getKey();
						currentVote=entry.getValue().get(movieId);

						correlationValue=null;
						if(currentVote!=null)
						{
							
							//diffVote = currentVote - meanVoteByUser.get(currentUserId);
							/*if(userId.equals(currentUserId))
							{
								correlationValue=1.0;
							}
							else
							{*/
								//correlationValue =  calculateCorrelation2(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser,userId,currentUserId);
								correlationValue = correlationMatrix[indexMap.get(currentUserId)];
								if(correlationValue==null)
								{
									correlationValue =  calculateCorrelation2(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser,userId,currentUserId);
									correlationMatrix[indexMap.get(currentUserId)] = correlationValue;
								}
							//}
							constantSumForNormalizationContant = constantSumForNormalizationContant+Math.abs(correlationValue);
							
							summationTerm = summationTerm + correlationValue*(currentVote - meanVoteByUser.get(currentUserId));
						}
						
						
					}
					
					if(constantSumForNormalizationContant==0.0)
					{
						normalizationConstant = 0.0;
					}
					else
					{
						normalizationConstant = 1/constantSumForNormalizationContant;
					}
					calculatedVote = meanVoteByUser.get(userId) + normalizationConstant*summationTerm;
					errorValue = Math.abs(calculatedVote-userVote.getVoteValue());
					absoluteMeanError = absoluteMeanError + Math.abs(errorValue);
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
					System.out.println("count : "+count +"  userId : "+userId+ " movieId : "+movieId+ " absoluteMeanError - "+absoluteMeanError + "\t : " + rootMeanSquare);
	
				}
			}
			else
			{
				for(UserVote userVote: tempUserVoteList)
				{
					count++;
					errorValue = userVote.getVoteValue();
					absoluteMeanError = absoluteMeanError + errorValue;
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
					System.out.println("count : "+count +"userId : "+userId+ " movieId : "+movieId+ " absoluteMeanError - "+absoluteMeanError + " : " + rootMeanSquare);
				}
			}
			
			
		}
		System.out.println("Absolute Mean Error : " + absoluteMeanError/ApplicationData.totalUserList.get(1));
		System.out.println("Root Mean Square Error : " + Math.sqrt(rootMeanSquare/ApplicationData.totalUserList.get(1)));
	}
	
	public static void testAccuracyByCFOnNetFlix3(HashMap<String,ArrayList<UserVote>> testUserVote,HashMap<String,HashMap<String,Float>> trainingData,
			/*HashMap<String,double> corelationMap,*/HashMap<String,Double> meanVoteByUser,HashMap<String,Integer> indexMap,int i,HashMap<String,Double> meanVoteByMovie)
	{
		Double[] correlationMatrix =null;
		double calculatedVote=0.0f;
		int totalUser = trainingData.size();
		String userId=null;
		String movieId=null;
		double normalizationConstant = 0.001f;
		double summationTerm=0.0;
		ArrayList<UserVote> tempUserVoteList = null;
		UserVote tempUserVote=null;
		String tempCorrelationId=null;
		double absoluteMeanError = 0.0;
		double rootMeanSquare =0.0;
		double errorValue=0.0f;
		double constantSumForNormalizationContant = 0.0f;
		
		Double correlationValue=null;
		int index1;
		int index2;
		int index;
		Float currentVote=null;
		String currentUserId=null;
		int count=0;
		int totalCount=0;
		double diffVote=0.0;
		for(Map.Entry<String,ArrayList<UserVote>> testEntry : testUserVote.entrySet())
		{
			correlationMatrix = new Double[i];
			userId = testEntry.getKey();
			//movieId = userVote.getMovieId(); 
			tempUserVoteList = testEntry.getValue();
			
			if(trainingData.get(userId)!=null)
			{
				//createCorrelationMatrix(userId, trainingData, meanVoteByUser, indexMap,correlationMatrix);
				for(UserVote userVote : tempUserVoteList)
				{
					movieId = userVote.getMovieId(); 
					constantSumForNormalizationContant =0;
					summationTerm =0;
					count++;
					for (Map.Entry<String,HashMap<String,Float>> entry : trainingData.entrySet())
					{
						currentUserId = entry.getKey();
						currentVote=entry.getValue().get(movieId);

						correlationValue=null;
						if(currentVote!=null)
						{
							
							//diffVote = currentVote - meanVoteByUser.get(currentUserId);
							/*if(userId.equals(currentUserId))
							{
								correlationValue=1.0;
							}
							else
							{*/
								//correlationValue =  calculateCorrelation2(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser,userId,currentUserId);
								correlationValue = correlationMatrix[indexMap.get(currentUserId)];
								if(correlationValue==null)
								{
									correlationValue =  calculateCorrelation2(trainingData.get(userId),trainingData.get(currentUserId),meanVoteByUser,userId,currentUserId);
									correlationMatrix[indexMap.get(currentUserId)] = correlationValue;
								}
							//}
							constantSumForNormalizationContant = constantSumForNormalizationContant+Math.abs(correlationValue);
							
							summationTerm = summationTerm + correlationValue*(currentVote - meanVoteByUser.get(currentUserId));
						}
						
						
					}
					
					if(constantSumForNormalizationContant==0.0)
					{
						normalizationConstant = 0.0;
					}
					else
					{
						normalizationConstant = 1/constantSumForNormalizationContant;
					}
					calculatedVote = meanVoteByUser.get(userId) + normalizationConstant*summationTerm;
					errorValue = Math.abs(calculatedVote-userVote.getVoteValue());
					absoluteMeanError = absoluteMeanError + Math.abs(errorValue);
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
					System.out.println("count : "+count +"  userId : "+userId+ " movieId : "+movieId+ " absoluteMeanError - "+absoluteMeanError + "\t : " + rootMeanSquare);
	
				}
			}
			else
			{
				for(UserVote userVote: tempUserVoteList)
				{
					count++;
				    movieId = userVote.getMovieId();
					if(meanVoteByMovie.get(movieId)!=null)
					{
						errorValue =Math.abs(meanVoteByMovie.get(movieId)- userVote.getVoteValue());
					}
					else
					{
						errorValue = userVote.getVoteValue();
					}
					
					absoluteMeanError = absoluteMeanError + errorValue;
					rootMeanSquare = rootMeanSquare + Math.pow(errorValue, 2);
					System.out.println("count : "+count +"userId : "+userId+ " movieId : "+movieId+ " absoluteMeanError - "+absoluteMeanError + " : " + rootMeanSquare);
				}
			}
			
			
		}
		System.out.println("Absolute Mean Error : " + absoluteMeanError/ApplicationData.totalUserList.get(1));
		System.out.println("Root Mean Square Error : " + Math.sqrt(rootMeanSquare/ApplicationData.totalUserList.get(1)));
	}
	
	public static void createCorrelationMatrix(String user,HashMap<String,HashMap<String,Float>> trainingData, HashMap<String,Double> meanVoteByUser,HashMap<String,Integer> indexMap,Double[] corelationMatrix  )
	{
		//Double[] corelationMatrix = new Double[i];
		int index1;
		int index2;
		int index;
		String currentUserId=null;
		int totalUser=trainingData.size();
		double correlationValue=0.0;
		int count=0;
			for (Map.Entry<String,HashMap<String,Float>> entry : trainingData.entrySet())
				//for(String currentUserId : trainingData.keySet())
				{
					currentUserId = entry.getKey();
					//index1 = indexMap.get(user);
					index2 = indexMap.get(currentUserId);
					/*if(index1>index2)
					{
					//j * N - (j - 1) * j / 2 + i - j;
					//index=(index1)*(index1+1)+index2;
						index = (index2*totalUser) - ((index2-1)*index2)/2 + index1-index2;
					}
					else
					{//i * N - (i - 1) * i / 2 + j - i;
						index=(index1*totalUser) - ((index1-1)*index1)/2 + index2-index1;
					}*/
			//	System.out.println(index1+"\t"+index2+"\t"+index+"\t"+totalUser+"\t"+corelationMatrix.length);
					/*if(corelationMatrix[index2]==null)
					{*/
						correlationValue =  calculateCorrelation2(trainingData.get(user),trainingData.get(currentUserId),meanVoteByUser,user,currentUserId);
						corelationMatrix[index2] = correlationValue;
					/*}
					else
					{
						correlationValue = corelationMatrix[index2];
					}*/
			//	System.out.println(correlationValue);
				}
			//count++;
			//System.out.println(count);
	}
	public static void main(String args[]) throws IOException
	{
		/*Map<String,Map<String,Long>> testData = getDataBasedOnClassification("C:\\Users\\SRPOP\\Desktop\\Gogate\\testFolder\\train");
		Map<String,HashMap<String,Map<String,Long>>> dataBasedOnClassification= getTestDataBasedOnClassification("C:\\Users\\SRPOP\\Desktop\\Gogate\\testFolder\\train");
		System.out.println("ApplicationData.docCountByClassification : " + ApplicationData.docCountByClassification);
		System.out.println("ApplicationData.vocabCountByClassification : " + ApplicationData.vocabCountByClassification);
		System.out.println(dataBasedOnClassification);
		HashSet<String> stopWords = getStopWordsSet("C:\\Users\\SRPOP\\Desktop\\Gogate\\HW2\\stopword.csv");
			System.out.println(stopWords.size());*/
		//ArrayList<UserVote> userList = DataReader.readNetflixVoteInfo("C:/Users/SRPOP/Desktop/Gogate/HW3/Netflix/TrainingRatings.txt");
		
	//	System.out.println(readNetflixVoteInfo("C:/Users/SRPOP/Desktop/Gogate/HW3/Netflix/TrainingRatings.txt").size());
		double test1 = 5;
		System.out.println(1/test1);
		/*String reg = "^[a-zA-Z]*$";
		System.out.println("acs".matches(reg));*/
		
	}
	
/*	public static HashMap<String,HashMap<String,double>> predictedVote(HashMap<String,double> correlationMap,HashMap<String,double> meanVoteByUser)
	{
		
	}*/

}
