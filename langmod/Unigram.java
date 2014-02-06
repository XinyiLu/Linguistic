package langmod;

public class Unigram {
	public static void main(String[] args){
		assert(args.length==4);
		PlainUnigramModel plainUniModel=new PlainUnigramModel();
		plainUniModel.trainModel(args[0]);
		
		double alpha=1.0;
		System.out.println(plainUniModel.getLogModelProbFromFile(args[1], alpha));
		alpha=plainUniModel.optimizeParameter(args[2]);
		System.out.println(plainUniModel.getLogModelProbFromFile(args[1], alpha));
		
		System.out.println(plainUniModel.classifyGoodBadCorrectRate(args[3], alpha));
		System.out.println(alpha);
	}
}
