package unigram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Bigram extends CountWords{

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

	public double getBigramHeldOutWordLogProb(HashMap<String,HashMap<String,Integer>> heldOutMap,double beta){
		return getBigramLogModelProbHelper(heldOutMap,beta);
	}
	public double bigramGoldenSectionSearch(HashMap<String,HashMap<String,Integer>> heldOutBigramMap,double a,double b,double c,double tau,int typeCount){
		assert(c>b&&b>a);
		double d=0;
		
		if(c-b>b-a){
			d=b+resphi*(c-b);
		}else{
			d=b-resphi*(b-a);
		}
		if(Math.abs(c-a)<tau*(Math.abs(b)+Math.abs(d))){
			return (c+a)/2.0;
		}
		double prob=getBigramHeldOutWordLogProb(heldOutBigramMap,d);
		double prevProb=getBigramHeldOutWordLogProb(heldOutBigramMap,b);
		assert(prob!=prevProb);
		if(prob>prevProb){
			if(c-b>b-a)
				return bigramGoldenSectionSearch(heldOutBigramMap,b,d,c,tau,typeCount);
			else
				return bigramGoldenSectionSearch(heldOutBigramMap,a,d,b,tau,typeCount);
		}else{
			if(c-b>b-a)
				return bigramGoldenSectionSearch(heldOutBigramMap,a,b,d,tau,typeCount);
			else
				return bigramGoldenSectionSearch(heldOutBigramMap,d,b,c,tau,typeCount);
			
		}
		
	} 

	public double optimizeBeta(String heldOutFile){
		Bigram heldOutBigram=new Bigram();
		heldOutBigram.readTrainingFileToBigram(heldOutFile);
		HashMap<String,HashMap<String,Integer>> heldOutMap=heldOutBigram.getBigramMap();
		
		double a=0,c=heldOutMap.size();
		double b=a+resphi*(c-a);
		return bigramGoldenSectionSearch(heldOutMap,a,b,c,0.0001/totalCount,getBigramTypeCount(heldOutMap));
	}
	
	public boolean guessGoodBadBigram(String[] pair,double beta){
		assert(pair.length==2);
		double[] probs=new double[2];
		for(int i=0;i<pair.length;i++){
			String line=pair[i];			
			HashMap<String,HashMap<String,Integer>> lineMap=new HashMap<String,HashMap<String,Integer>>();
			parseBigramPairToHashMap(lineMap,line);
			probs[i]=getBigramLogModelProbHelper(lineMap,beta);
		}
		
		return probs[0]>probs[1];
	}
	
	public double guessGoodBadCorrectRateBigram(String testFile,double beta){
		BufferedReader reader;
		double rate=0.0;
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile),"ISO-8859-1"));
			String[] pair=new String[2];
			int pairCount=0,rightCount=0;
			while((pair[0]=reader.readLine())!=null){
				pair[1]=reader.readLine();
				pairCount++;
				if(guessGoodBadBigram(pair,beta)){
					rightCount++;
				}
			}
			reader.close();
			rate=rightCount*1.0/pairCount;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rate;
	} 

}
