package unigram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Bigram{
	
//	class BigramPair{
//		String word1;
//		String word2;
//		
//		public BigramPair(String first,String second){
//			word1=first;
//			word2=second;
//		}
//	}
	
	
	private Unigram unigram;
	private double alpha;
	private HashMap<String,HashMap<String,Integer>> bigramMap;
	
	public HashMap<String,HashMap<String,Integer>> getBigramMap(){
		return bigramMap;
	}
	
	public Bigram(){
		bigramMap=new HashMap<String,HashMap<String,Integer>>();
	}
	
	public Bigram(String fileName,Unigram uni,double al){
		unigram=uni;
		alpha=al;
		bigramMap=new HashMap<String,HashMap<String,Integer>>();
		readTrainingFileToBigram(fileName);
	}
	
	public void parseBigramPairToHashMap(HashMap<String,HashMap<String,Integer>> hashMap,String line){
		String[] words=line.split(" ");
		ArrayList<String> wordList=new ArrayList<String>(words.length);
		for(String word:words){
			if(word.isEmpty())
				continue;
			wordList.add(word);
		}
		//count the number of each word
		for(int i=0;i<wordList.size();i++){
			//if the word is "",ignore it
			String word=wordList.get(i);
			//totalCount++;
			//if the word hasn't been counted, add it to the map
			if(!hashMap.containsKey(word)){
				hashMap.put(word,new HashMap<String,Integer>());
			}
			String followWord=((i==wordList.size()-1)?"":wordList.get(i+1));
			HashMap<String,Integer> subMap=hashMap.get(word);
			if(!subMap.containsKey(followWord)){
				subMap.put(followWord,0);
			}
			//add the count of this word by one
			subMap.put(followWord,subMap.get(followWord)+1);
		}	
	}
	
	public void readTrainingFileToBigram(String fileName){
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"ISO-8859-1"));
			String line=null;
			//each time we read a line, count its words
			while((line=reader.readLine())!=null){
				parseBigramPairToHashMap(bigramMap,line);
			}
			//close the buffered reader
			reader.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public double getBigramWordSmoothedProb(String word1,String word2,double beta,int typeCount){
		double theta=unigram.getUnigramWordSmoothedProb(word2, alpha,typeCount);
		if(!unigram.getHashMap().containsKey(word1)){
			return theta;
		}
		HashMap<String,Integer> subMap=bigramMap.get(word1);
		int count=0;
		if(subMap.containsKey(word2)){
			count=subMap.get(word2);
		}
		
		return (count+beta*theta)/(unigram.getHashMap().get(word1)+beta);
	}
	
	public int getBigramTypeCount(HashMap<String,HashMap<String,Integer>> testBigramMap){
		for(String word1:testBigramMap.keySet()){
			if(!unigram.getHashMap().containsKey(word1)){
				return unigram.getHashMap().size()+1;
			}
		}
		return unigram.getHashMap().size();
	}
	
	public double getBigramLogModelProbHelper(HashMap<String,HashMap<String,Integer>> testBigramMap,double beta){
		double prob=0.0;
		int typeCount=getBigramTypeCount(testBigramMap);
		for(String word1:testBigramMap.keySet()){
			HashMap<String,Integer> subMap=testBigramMap.get(word1);
			for(String word2:subMap.keySet()){
				double bigTheta=getBigramWordSmoothedProb(word1,word2,beta,typeCount);
				prob+=subMap.get(word2)*Math.log(bigTheta);
			}
		}
		return prob;
	}
	
	public double getBigramLogModelProb(String fileName,double beta){
		Bigram bigramModel=new Bigram();
		bigramModel.readTrainingFileToBigram(fileName);
		return getBigramLogModelProbHelper(bigramModel.getBigramMap(),beta);
	}
	
}
