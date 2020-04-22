package morpion;

import java.io.File;
import Mk.Pair;
import javafx.concurrent.Task;

@SuppressWarnings("hiding")
public class Apprentissage_Task<Void> extends Task<Void> {
	
	private Ai ai;
	
	public Apprentissage_Task(Ai ai) {
		this.ai = ai;
	}

	@Override
	protected Void call() throws Exception {
		
		final File FILE_COUPS = new File(Ai.DATA_DIRPATH + Ai.COUPS_FILENAME);
		final double PERCENTAGE_STAGE = 10.0;
		final Pair<Integer,Double> params = this.ai.getModelParams();
		final double optNb = (double)this.ai.calcOptNb(params.first, params.second);
		final long stage = Math.round(optNb / PERCENTAGE_STAGE);
		final String DIFFICULTY = this.ai.getDiff().getValue();
		
		int nbCoupsAppris;
		double percentage;
		int i = this.ai.model.getConsideredMoves();
		int stageIteration = 1;
		
		boolean reached = (i >= optNb);
		boolean needUpdate = (this.ai.model.getFileLastModified() != FILE_COUPS.lastModified());
		boolean apprentissageTerminee = false;

		while(true)
		{
			while(!reached || needUpdate)
			{
				apprentissageTerminee = false;

				if(needUpdate)
				{
					this.ai.reset();
					i = this.ai.model.getConsideredMoves();
					needUpdate = false;
				}

				nbCoupsAppris = this.ai.learn();
				i += nbCoupsAppris;
				this.ai.model.setConsideredMoves(i);
				this.ai.model.setFileLastModified(FILE_COUPS.lastModified());
				
				reached = (i >= optNb);
			
				if(i >= stageIteration * stage)
				{
					this.ai.save();
					percentage = stageIteration * PERCENTAGE_STAGE;
					System.out.println("Modèle " + DIFFICULTY + " (" + params.first + 
							", " + params.second + ") : " + percentage + "% of " + (int)optNb);
					++stageIteration;
				}
					
				if (Main.learningThread.isInterrupted()) {
					System.out.println("Thread interrompu");
					return null;
				}
			}
			stageIteration = 1;
			needUpdate = (this.ai.model.getFileLastModified() != FILE_COUPS.lastModified());
			
			
			if(!apprentissageTerminee)
			{
				System.out.println("Modèle " + DIFFICULTY + " (" + params.first + ", " + 
						params.second + ") : " + "Apprentissage terminée !");
				apprentissageTerminee = true;
			}
			
			if (Main.learningThread.isInterrupted()) {
				System.out.println("Thread interrompu");
				return null;
			}
		}
	}

}
