package langmod;

public class Unigram {
	public static void main(String[] args){
		assert(args.length==4);
		PlainUnigramModel plainUniModel=new PlainUnigramModel();
		//save training data to model
		plainUniModel.trainModel(args[0]);
		
		double alpha=1.0;
		System.out.println(plainUniModel.getLogModelProbFromFile(args[1], alpha));
		//optimize alpha
		alpha=plainUniModel.optimizeParameter(args[2]);
		System.out.println(plainUniModel.getLogModelProbFromFile(args[1], alpha));
		
		System.out.println(plainUniModel.classifyGoodBadCorrectRate(args[3], alpha));
		System.out.println(alpha);
	}
}
