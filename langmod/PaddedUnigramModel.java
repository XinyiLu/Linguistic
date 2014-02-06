package langmod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;



public class PaddedUnigramModel extends BaseWordCounter{

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void splitLineToMap(HashMap hashMap, String line) {
		HashMap<String,Integer> unigramMap=(HashMap<String,Integer>)hashMap;
		String[] words=line.split(" ");
		ArrayList<String> list=new ArrayList<String> (Arrays.asList(words));
		list.add("");
		//count the number of each word
		for(String word:list){
			//if the word is "",ignore it
			if(word.isEmpty())
				continue;
			//totalCount++;
			//if the word hasn't been counted, add it to the map
			if(!hashMap.containsKey(word)){
				unigramMap.put(word,0);
			}
			//add the count of this word by one
			unigramMap.put(word,unigramMap.get(word)+1);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	int getTypeCount(HashMap hashMap) {
		int typeCount=map.size();
		HashMap<String,Integer> unigramMap=(HashMap<String,Integer>)hashMap;
		for(String word:unigramMap.keySet()){
			if(!map.containsKey(word)){
				typeCount++;
				break;
			}
		}
		return typeCount;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	double getLogModelProbFromMap(HashMap hashMap, double para, int typeCount) {
		double prob=0.0;
		HashMap<String,Integer> unigramMap=(HashMap<String,Integer>)hashMap;
		for(String word:unigramMap.keySet()){
			double theta=getUnigramWordSmoothedProb(word,para,typeCount);
			prob+=unigramMap.get(word)*Math.log(theta);
		}
		return prob;
	}
	
	@SuppressWarnings({ "unchecked" })
	public double getUnigramWordSmoothedProb(String word,double alpha,int typeCount){
		HashMap<String,Integer> hashMap=(HashMap<String,Integer>)map;
		int wCount=0;
		if(hashMap.containsKey(word)){
			wCount=hashMap.get(word);
		}
		double theta=(wCount+alpha)/(totalWordCount+alpha*typeCount);
		return theta;
	}

	
	HashMap<String,Integer> generateHashMap() {
		return new HashMap<String,Integer>();
	}

	@SuppressWarnings({ "unchecked" })
	void setTotalCount() {
		HashMap<String,Integer> hashMap=(HashMap<String,Integer>)map;
		totalWordCount=0;
		for(String word:hashMap.keySet()){
			totalWordCount+=hashMap.get(word);
		}
		
	}
	
	
}
