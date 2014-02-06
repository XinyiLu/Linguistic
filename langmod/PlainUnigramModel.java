package langmod;

import java.util.HashMap;

public class PlainUnigramModel extends PaddedUnigramModel {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void splitLineToMap(HashMap hashMap, String line) {
		HashMap<String,Integer> unigramMap=(HashMap<String,Integer>)hashMap;
		String[] words=line.split(" ");

		//count the number of each word
		for(String word:words){
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
	
}
