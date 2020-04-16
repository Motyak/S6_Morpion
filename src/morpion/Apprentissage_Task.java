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
		final double PERCENTAGE_STAGE = 10.0;
		String diff = this.ai.getDiff().getValue();
		
		int i = this.ai.model.getConsideredMoves();
		Pair<Integer,Double> params = this.ai.getModelParams();
		double optNb = (double)this.ai.calcOptNb(params.first, params.second);
		long stage = Math.round(optNb / PERCENTAGE_STAGE);
		boolean reached = (i >= optNb);
		
		while(!reached)
		{
			this.ai.learn();
			++i;
			this.ai.model.incConsideredMoves();
			int percentage = (int)(i / optNb * 100.0);
			if(i % stage == 0)
			{
				this.ai.save();
				System.out.println("Modèle " + diff + " (" + params.first + 
						", " + params.second + ") : " + percentage + "%");
			}
				
			
			reached = (i >= optNb);
			
			if (Main.learningThread.isInterrupted())	return null;
		}
		System.out.println("Modèle " + diff + " (" + params.first + 
				", " + params.second + ") : " + "Apprentissage terminée !");

		return null;
	}

}
