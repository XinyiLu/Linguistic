package langmod;


public class Langmod {
	public static void main(String[] args){
		UnigramModel uniModel=new UnigramModel();
		uniModel.trainModel(args[0]);
		double alpha=1.0;
		System.out.println("(1) alpha = "+alpha+"\tprob = "+uniModel.getLogModelProbFromFile(args[1], alpha));
		alpha=uniModel.optimizeParameter(args[2]);
		System.out.println("(2) alpha = "+alpha+"\tprob = "+uniModel.getLogModelProbFromFile(args[1], alpha));
		
		System.out.println("(3) classification correct rate : "+uniModel.classifyGoodBadCorrectRate(args[3], alpha));
		BigramModel biModel=new BigramModel(uniModel,alpha);
		biModel.trainModel(args[0]);
		double beta=1.0;
		System.out.println("(4) beta = "+beta+"\tprob = "+biModel.getLogModelProbFromFile(args[1], beta));
		beta=biModel.optimizeParameter(args[2]);
		System.out.println("(5) beta = "+beta+"\tprob = "+biModel.getLogModelProbFromFile(args[1], beta));
		System.out.println("(6) classification correct rate : "+biModel.classifyGoodBadCorrectRate(args[3], beta));
	}
}
