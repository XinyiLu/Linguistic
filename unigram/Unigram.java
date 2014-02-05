package unigram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Unigram extends CountWords{
	
	public double getUnigramWordSmoothedProb(String word,double alpha,int typeCount){
		int wCount=0;
		if(map.containsKey(word)){
			wCount=map.get(word);
		}
		double theta=(wCount+alpha)/(totalCount+alpha*typeCount);
		return theta;
	}
	
	
	public double getUnigramLogModelProbHelper(HashMap<String,Integer> hashMap,double alpha,int typeCount){
		double prob=0.0;
		for(String word:hashMap.keySet()){
			double theta=getUnigramWordSmoothedProb(word,alpha,typeCount);
			prob+=hashMap.get(word)*Math.log(theta);
		}
		return prob;
	}
	
	public int getTypeCount(HashMap<String,Integer> hashMap){
		int typeCount=map.size();
		for(String word:hashMap.keySet()){
			if(!map.containsKey(word)){
				typeCount++;
				break;
			}
		}
		return typeCount;
	}
	
	public double getUnigramLogModelProb(String testFile,double alpha){
		//read test data into main memory
		Unigram testModel=new Unigram();
		testModel.readInputFile(testFile);
		HashMap<String,Integer> testMap=testModel.getHashMap();
		return getUnigramLogModelProbHelper(testMap,alpha,getTypeCount(testMap));
	}
	
	public double getUnigramHeldOutWordLogProb(HashMap<String,Integer> heldOutMap,double alpha,int typeCount){
		return getUnigramLogModelProbHelper(heldOutMap,alpha,typeCount);
	}
	
	//use golden section search to get the optimized alpha
	public double unigramGoldenSectionSearch(HashMap<String,Integer> heldOutMap,double a,double b,double c,double tau,int typeCount){
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
		double prob=getUnigramHeldOutWordLogProb(heldOutMap,d,typeCount);
		double prevProb=getUnigramHeldOutWordLogProb(heldOutMap,b,typeCount);
		assert(prob!=prevProb);
		if(prob>prevProb){
			if(c-b>b-a)
				return unigramGoldenSectionSearch(heldOutMap,b,d,c,tau,typeCount);
			else
				return unigramGoldenSectionSearch(heldOutMap,a,d,b,tau,typeCount);
		}else{
			if(c-b>b-a)
				return unigramGoldenSectionSearch(heldOutMap,a,b,d,tau,typeCount);
			else
				return unigramGoldenSectionSearch(heldOutMap,d,b,c,tau,typeCount);
			
		}
		
	} 
	
	public double optimizeAlpha(String heldOutFile){
		CountWords countClass=new CountWords();
		countClass.readInputFile(heldOutFile);
		HashMap<String,Integer> heldOutMap=countClass.getHashMap();
		double a=0,c=heldOutMap.size();
		double b=a+resphi*(c-a);
		return unigramGoldenSectionSearch(heldOutMap,a,b,c,0.0001/totalCount,getTypeCount(heldOutMap));
	}
	
	public boolean guessGoodBadUnigram(String[] pair,double alpha){
		assert(pair.length==2);
		double[] probs=new double[2];
		for(int i=0;i<pair.length;i++){
			String line=pair[i];
			//This should be grouped into one function
			HashMap<String,Integer> lineMap=new HashMap<String,Integer>();
			countWords(lineMap,line);
			probs[i]=getUnigramLogModelProbHelper(lineMap,alpha,getTypeCount(lineMap));
		}
		
		return probs[0]>probs[1];
	}
	
	public double guessGoodBadCorrectRateUnigram(String testFile,double alpha){
		BufferedReader reader;
		double rate=0.0;
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile),"ISO-8859-1"));
			String[] pair=new String[2];
			int pairCount=0,rightCount=0;
			while((pair[0]=reader.readLine())!=null){
				pair[1]=reader.readLine();
				pairCount++;
				if(guessGoodBadUnigram(pair,alpha)){
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
	
	
	public static void main(String[] args){
		assert(args.length!=0);
		Unigram uniModel=new Unigram();
		uniModel.readInputFile(args[0]);
		System.out.println("(1) "+uniModel.getUnigramLogModelProb(args[1], 1));
		double alpha=uniModel.optimizeAlpha(args[2]);
		System.out.println("(2) alpha = "+alpha+"\t prob = "+uniModel.getUnigramLogModelProb(args[1], alpha));
		
		System.out.println("(3) "+uniModel.guessGoodBadCorrectRateUnigram(args[3], alpha));
		Bigram bigramModel=new Bigram(args[0],uniModel,alpha);
		System.out.println("(4) Bigram Prob of testing file: "+bigramModel.getBigramLogModelProb(args[1], 1.0));
		
		double beta=bigramModel.optimizeBeta(args[2]);
		System.out.println("(5) beta = "+beta+"\t prob = "+bigramModel.getBigramLogModelProb(args[1], beta));
		
	}
	
	
	
	
	
	
	
	
	
	
}
