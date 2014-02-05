package unigram;

import java.util.HashMap;

public class UnigramBase extends CountWords{
	
	//private HashMap<String,Integer> heldOutMap;
	
	public double getWordSmoothedProb(String word,double alpha,int typeCount){
		int wCount=0;
		if(map.containsKey(word)){
			wCount=map.get(word);
		}
		double theta=(wCount+alpha)/(totalCount+alpha*typeCount);
		return theta;
	}
	
	public double getLogModelProbHelper(HashMap<String,Integer> hashMap,double alpha,int typeCount){
		double prob=0.0;
		for(String word:hashMap.keySet()){
			double theta=getWordSmoothedProb(word,alpha,typeCount);
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
	
	public double getLogModelProb(String testFile,double alpha){
		//read test data into main memory
		UnigramBase testModel=new UnigramBase();
		testModel.readInputFile(testFile);
		HashMap<String,Integer> testMap=testModel.getHashMap();
		return getLogModelProbHelper(testMap,alpha,getTypeCount(testMap));
	}
	
	public double getHeldOutWordLogProb(HashMap<String,Integer> heldOutMap,double alpha,int typeCount){
//		double prob=0.0;
//		for(String word:heldOutMap.keySet()){
//			double theta=getWordSmoothedProb(word,alpha,typeCount);
//			prob+=heldOutMap.get(word)*Math.log(theta);
//		}
//		
//		return prob;
		return getLogModelProbHelper(heldOutMap,alpha,typeCount);
	}
	
	//use golden section search to get the optimized alpha
	public double goldenSectionSearch(HashMap<String,Integer> heldOutMap,double a,double b,double c,double tau,int typeCount){
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
		double prob=getHeldOutWordLogProb(heldOutMap,d,typeCount);
		double prevProb=getHeldOutWordLogProb(heldOutMap,b,typeCount);
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
	
	public double optimizeAlpha(String heldOutFile){
		CountWords countClass=new CountWords();
		countClass.readInputFile(heldOutFile);
		HashMap<String,Integer> heldOutMap=countClass.getHashMap();
		double a=0,c=heldOutMap.size();
		double b=a+resphi*(c-a);
		return goldenSectionSearch(heldOutMap,a,b,c,0.0001/totalCount,getTypeCount(heldOutMap));
	}
	
//	public boolean guessRight(String[] pair){
//		assert(pair.length==2);
//		double[] probs=new double[2];
//		for(String line:pair){
//			HashMap<String,Integer> lineMap=new HashMap<String,Integer>();
//			String[] words=line.split(" ");
//			for(String word:words){
//				if(word.isEmpty())
//					continue;
//				if(!lineMap.containsKey(word))
//					lineMap.put(word,0);
//				
//				lineMap.put(word,lineMap.get(word)+1);
//			}
//			
//			
//		}
//	}
	
	public static void main(String[] args){
		assert(args.length!=0);
		UnigramBase uniModel=new UnigramBase();
		uniModel.readInputFile(args[0]);
		System.out.println("(1) "+uniModel.getLogModelProb(args[1], 1));
		double alpha=uniModel.optimizeAlpha(args[2]);
		System.out.println("(2) alpha = "+alpha+"\t prob = "+uniModel.getLogModelProb(args[1], alpha));
		
	}
	
	
	
	
	
	
	
	
	
	
}
