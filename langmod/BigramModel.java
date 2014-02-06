package langmod;

import java.util.ArrayList;
import java.util.HashMap;


public class BigramModel extends BaseWordCounter {
	private UnigramModel unigram;
	private double alpha;
	
	public BigramModel(){
		super();
	}
	
	public BigramModel(UnigramModel uni,double al){
		super();
		unigram=uni;
		alpha=al;
	}

	void setTotalCount() {
		totalWordCount=unigram.getTotalCount();
	}

	
	HashMap<String,HashMap<String,Integer>> generateHashMap() {
		return new HashMap<String,HashMap<String,Integer>>();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void splitLineToMap(HashMap hashMap, String line) {
		String[] words=line.split(" ");
		HashMap<String,HashMap<String,Integer>> bigramMap=(HashMap<String,HashMap<String,Integer>>)hashMap;
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
			if(!bigramMap.containsKey(word)){
				bigramMap.put(word,new HashMap<String,Integer>());
			}
			String followWord=((i==wordList.size()-1)?"":wordList.get(i+1));
			HashMap<String,Integer> subMap=bigramMap.get(word);
			if(!subMap.containsKey(followWord)){
				subMap.put(followWord,0);
			}
			//add the count of this word by one
			subMap.put(followWord,subMap.get(followWord)+1);
		}	
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	int getTypeCount(HashMap hashMap) {
		HashMap<String,HashMap<String,Integer>> bigramMap=(HashMap<String,HashMap<String,Integer>>)hashMap;
		HashMap<String,Integer> unigramMap=(HashMap<String,Integer>)unigram.getHashMap();
		for(String word1:bigramMap.keySet()){
			if(!unigramMap.containsKey(word1)){
				return unigramMap.size()+1;
			}
		}
		return unigramMap.size();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	double getLogModelProbFromMap(HashMap hashMap, double beta, int typeCount) {
		HashMap<String,HashMap<String,Integer>> testMap=(HashMap<String,HashMap<String,Integer>>)hashMap;
		double prob=0.0;
		for(String word1:testMap.keySet()){
			HashMap<String,Integer> subMap=testMap.get(word1);
			for(String word2:subMap.keySet()){
				double bigTheta=getBigramWordSmoothedProb(word1,word2,beta,typeCount);
				prob+=subMap.get(word2)*Math.log(bigTheta);
			}
		}
		return prob;
	}
	
	@SuppressWarnings({ "unchecked" })
	public double getBigramWordSmoothedProb(String word1,String word2,double beta,int typeCount){
		double theta=unigram.getUnigramWordSmoothedProb(word2, alpha,typeCount);
		HashMap<String,HashMap<String,Integer>> bigramMap=(HashMap<String,HashMap<String,Integer>>)map;
		HashMap<String,Integer> unigramMap=(HashMap<String,Integer>)unigram.getHashMap();
		if(!unigramMap.containsKey(word1)){
			return theta;
		}
		HashMap<String,Integer> subMap=bigramMap.get(word1);
		int count=0;
		if(subMap.containsKey(word2)){
			count=subMap.get(word2);
		}
		
		return (count+beta*theta)*1.0/(unigramMap.get(word1)+beta);
	}

}
