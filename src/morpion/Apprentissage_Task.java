package morpion;

import Mk.Pair;
import javafx.concurrent.Task;

public class Apprentissage_Task<Void> extends Task<Void> {
	
	private Ai ai;
	
	public Apprentissage_Task(Ai ai) {
		this.ai = ai;
	}

	@Override
	protected Void call() throws Exception {
		System.out.println("Bonjour thread JavaFX");
//		final double PERCENTAGE_STAGE = 10.0;
//		
//		int i = this.ai.getConsideredMoves();
//		String diff = this.ai.getDiff().getValue();
//		Pair<Integer,Double> params = this.ai.getModelParams();
//		double optNb = (double)this.ai.calcOptNb(params.first, params.second);
//		long stage = Math.round(optNb / PERCENTAGE_STAGE);
//		boolean reached = (i < optNb);
//		
//		while(!reached)
//		{
//			this.ai.learn();
//			++i;
//			int percentage = (int)(i / optNb);
//			if(i % stage == 0)
//				System.out.println("Modèle " + diff + " (" + params.first + 
//						", " + params.second + ") : " + percentage + "%");
//			
//			if (Thread.currentThread().isInterrupted())	return null;
//		}
//		System.out.println("Modèle " + diff + " (" + params.first + 
//				", " + params.second + ") : " + "Apprentissage terminée !");

		return null;
	}

}
