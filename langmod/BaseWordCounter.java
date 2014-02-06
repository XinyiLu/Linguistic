package langmod;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


public abstract class BaseWordCounter {

	protected HashMap map;
	protected int totalWordCount;
	protected double resphi;
	
	public HashMap getHashMap(){
		return map;
	}
	
	public int getTotalCount(){
		return totalWordCount;
	}
	

	protected BaseWordCounter(){
		resphi=2-(1+Math.sqrt(5))/2.0;
		totalWordCount=0;
		map=generateHashMap();
	}
	
	protected void trainModel(String fileName){
		parseFileToMap(fileName,map);
		//set totalWordCount
		setTotalCount();
	}
	
	abstract void setTotalCount();
	abstract HashMap generateHashMap();
	//function to count words in a line
	abstract void splitLineToMap(HashMap hashMap,String line);
	
	//function to read input file and save the counts to the map
	protected void parseFileToMap(String fileName,HashMap hashMap){
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"ISO-8859-1"));
			String line=null;
			//each time we read a line, count its words
			while((line=reader.readLine())!=null){
				splitLineToMap(hashMap,line);
			}
			//close the buffered reader
			reader.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	abstract int getTypeCount(HashMap hashMap);
	
	protected double goldenSectionSearch(HashMap heldOutMap,double a,double b,double c,double tau,int typeCount){
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
		double prob=getLogModelProbFromMap(heldOutMap,d,typeCount);
		double prevProb=getLogModelProbFromMap(heldOutMap,b,typeCount);
		assert(prob!=prevProb);
		if(prob>prevProb){
			if(c-b>b-a)
				return goldenSectionSearch(heldOutMap,b,d,c,tau,typeCount);
			else
				return goldenSectionSearch(heldOutMap,a,d,b,tau,typeCount);
		}else{
			if(c-b>b-a)
				return goldenSectionSearch(heldOutMap,a,b,d,tau,typeCount);
			else
				return goldenSectionSearch(heldOutMap,d,b,c,tau,typeCount);
			
		}
		
	} 
	
	abstract double getLogModelProbFromMap(HashMap hashMap,double para,int typeCount);
	
	protected double getLogModelProbFromFile(String fileName,double para){
		HashMap hashMap=generateHashMap();
		parseFileToMap(fileName,hashMap);
		return getLogModelProbFromMap(hashMap,para,getTypeCount(hashMap));
	}
	
	public double optimizeParameter(String heldOutFile){
		HashMap heldOutMap=generateHashMap();
		parseFileToMap(heldOutFile,heldOutMap);
		double a=1.0,c=totalWordCount;
		double b=a+resphi*(c-a);
		return goldenSectionSearch(heldOutMap,a,b,c,0.001/totalWordCount,getTypeCount(heldOutMap));
	}
	
	protected boolean classifyGoodBadPair(String[] pair,double para){
		assert(pair.length==2);
		double[] probs=new double[2];
		for(int i=0;i<pair.length;i++){
			String line=pair[i];
			//This should be grouped into one function
			HashMap lineMap=generateHashMap();
			splitLineToMap(lineMap,line);
			probs[i]=getLogModelProbFromMap(lineMap,para,getTypeCount(lineMap));
		}
		
		return probs[0]>probs[1];
	}
	
	public double classifyGoodBadCorrectRate(String testFile,double para){
		BufferedReader reader;
		double rate=0.0;
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile),"ISO-8859-1"));
			String[] pair=new String[2];
			int pairCount=0,rightCount=0;
			while((pair[0]=reader.readLine())!=null){
				pair[1]=reader.readLine();
				pairCount++;
				if(classifyGoodBadPair(pair,para)){
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
