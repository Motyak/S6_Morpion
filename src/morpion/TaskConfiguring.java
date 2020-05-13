package morpion;

import java.io.File;

import javafx.concurrent.Task;

/**
 * Is responsible for detecting whenever the configuration file has changed and informing the learning thread
 * @author Tommy 'Motyak'
 *
 * @param <Void> none
 */
@SuppressWarnings("hiding")
public class TaskConfiguring<Void> extends Task<Void> {

	private Controller ctrl;
	
	public TaskConfiguring(Controller ctrl) {
		this.ctrl = ctrl;
	}
	
	@Override
	protected Void call() throws Exception {
		System.out.println("Config thread launched");
		
		final File CONFIG_FILE = new File(Ai.DATA_DIRPATH + Ai.CONF_FILENAME);
		long lastModifiedOld = CONFIG_FILE.lastModified();
		long lastModifiedNew;
		
		while(true)
		{
			lastModifiedNew = CONFIG_FILE.lastModified();
			if(lastModifiedNew != lastModifiedOld)
			{
				System.out.println("Reloading the ai model..");
				this.ctrl.launchLearning();
				lastModifiedOld = lastModifiedNew; 
			}
			
			
			if(Main.configThread.isInterrupted()) {
				System.out.println("Config thread interrupted");
				if(Main.learningThread != null)
					Main.learningThread.interrupt();
				return null;
			}
		}
	}

}
