package langmod;

public class Bigram {
	public static void main(String[] args){
		assert(args.length==4);
		//first need to initiate a padded unigram model and save the training data to its map
		PaddedUnigramModel uniModel=new PaddedUnigramModel();
		uniModel.trainModel(args[0]);
		//optimize alpha
		double alpha=uniModel.optimizeParameter(args[2]);
		//initialize bigram model and save training data to its map
		BigramModel biModel=new BigramModel(uniModel,alpha);
		biModel.trainModel(args[0]);
		double beta=1.0;
		System.out.println(biModel.getLogModelProbFromFile(args[1], beta));
		//optimize beta
		beta=biModel.optimizeParameter(args[2]);
		System.out.println(biModel.getLogModelProbFromFile(args[1], beta));
		System.out.println(biModel.classifyGoodBadCorrectRate(args[3], beta));
		System.out.println(alpha);
		System.out.println(beta);
	}
}
