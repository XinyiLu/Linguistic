package langmod;

public class Bigram {
	public static void main(String[] args){
		assert(args.length==4);
		PaddedUnigramModel uniModel=new PaddedUnigramModel();
		uniModel.trainModel(args[0]);
		double alpha=uniModel.optimizeParameter(args[2]);
		BigramModel biModel=new BigramModel(uniModel,alpha);
		biModel.trainModel(args[0]);
		double beta=1.0;
		System.out.println(biModel.getLogModelProbFromFile(args[1], beta));
		beta=biModel.optimizeParameter(args[2]);
		System.out.println(biModel.getLogModelProbFromFile(args[1], beta));
		System.out.println(biModel.classifyGoodBadCorrectRate(args[3], beta));
		System.out.println(alpha);
		System.out.println(beta);
	}
}
