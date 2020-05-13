package morpion;

import java.io.File;
import Mk.Pair;
import javafx.concurrent.Task;

@SuppressWarnings("hiding")
public class TaskLearning<Void> extends Task<Void> {
	
	private Ai ai;
	
	public TaskLearning(Ai ai) {
		this.ai = ai;
	}

	@Override
	protected Void call() throws Exception {
		System.out.println("Learning thread launched");
		
		final File MOVES_FILE = new File(Ai.DATA_DIRPATH + Ai.MOVES_FILENAME);
		final double STAGE_PERCENTAGE = 10.0;
		final Pair<Integer,Double> params = this.ai.getModelParams();
		final double optNb = (double)this.ai.calcOptNb(params.first, params.second);
		final long stage = Math.round(optNb / STAGE_PERCENTAGE);
		final String DIFFICULTY = this.ai.getDiff().getValue();
		
		int nbLearntMoves;
		double percentage;
		int i = this.ai.model.getConsideredMoves();
		int stageIteration = 1;
		
		boolean reached = (i >= optNb);
		boolean needUpdate = (this.ai.model.getFileLastModified() != MOVES_FILE.lastModified());
		boolean learningFinished = false;

		while(true)
		{
			while(!reached || needUpdate)
			{
				learningFinished = false;

				if(needUpdate)
				{
					this.ai.reset();
					i = this.ai.model.getConsideredMoves();
					needUpdate = false;
				}

				nbLearntMoves = this.ai.learn();
				i += nbLearntMoves;
				this.ai.model.setConsideredMoves(i);
				this.ai.model.setFileLastModified(MOVES_FILE.lastModified());
				
				reached = (i >= optNb);
			
				if(i >= stageIteration * stage)
				{
					this.ai.save();
					percentage = stageIteration * STAGE_PERCENTAGE;
					System.out.println(DIFFICULTY + " model (" + params.first + 
							", " + params.second + ") : " + percentage + "% of " + (int)optNb);
					++stageIteration;
				}
					
				if (Main.learningThread.isInterrupted()) {
					System.out.println("Learning thread interrupted");
					return null;
				}
			}
			stageIteration = 1;
			needUpdate = (this.ai.model.getFileLastModified() != MOVES_FILE.lastModified());
			
			
			if(!learningFinished)
			{
				System.out.println(DIFFICULTY + " model (" + params.first + ", " + 
						params.second + ") : " + "Learning finished !");
				learningFinished = true;
			}
			
			if (Main.learningThread.isInterrupted()) {
				System.out.println("Learning thread interrupted");
				return null;
			}
		}
	}

}
