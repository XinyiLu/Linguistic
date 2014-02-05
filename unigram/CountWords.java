package unigram;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CountWords {
	protected HashMap<String,Integer> map;
	protected int totalCount;
	protected double resphi;
	
	public HashMap<String,Integer> getHashMap(){
		return map;
	}
	
	protected CountWords(){
		map=new HashMap<String,Integer>();
		resphi=2-(1+Math.sqrt(5))/2.0;
	}

	//function to count words in a line
	protected void countWords(String line){
		//split words with whitespace
		String[] words=line.split(" ");
		//count the number of each word
		for(String word:words){
			//if the word is "",ignore it
			if(word.isEmpty())
				continue;
			totalCount++;
			//if the word hasn't been counted, add it to the map
			if(!map.containsKey(word)){
				map.put(word,0);
			}
			//add the count of this word by one
			map.put(word,map.get(word)+1);
		}
	}
	
	//function to read input file and save the counts to the map
	protected void readInputFile(String fileName){
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"ISO-8859-1"));
			String line=null;
			//each time we read a line, count its words
			while((line=reader.readLine())!=null){
				countWords(line);
			}
			//close the buffered reader
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//function to write map to output file
	protected void writeOutputFile(String fileName){
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(fileName));
			//for each word in keySet of the map, save the key and value and write a new line
			for(String word:map.keySet()){
				writer.write(word+" "+map.get(word).toString());
				writer.newLine();
			}
			//close the buffered writer
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
	
//	public static void main(String[] args)
//	{
//		if(args.length!=2){
//			System.out.println("Usage is: java <myprogram> $input $output");
//		}
//		CountWords countClass=new CountWords();
//		countClass.readInputFile(args[0]);
//		countClass.writeOutputFile(args[1]);		
//	}
	
}