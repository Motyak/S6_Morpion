package morpion;

import java.io.File;

import javafx.concurrent.Task;

@SuppressWarnings("hiding")
public class Config_Task<Void> extends Task<Void> {

	private Ai ai;
	
	public Config_Task(Ai ai) {
		this.ai = ai;
	}
	
	@Override
	protected Void call() throws Exception {
		System.out.println("Thread config lancé");
		
		final File FILE_CONFIG = new File(Ai.DATA_DIRPATH + Ai.CONF_FILENAME);
		long lastModifiedOld = FILE_CONFIG.lastModified();
		long lastModifierNew;
		
		while(true)
		{
			lastModifierNew = FILE_CONFIG.lastModified();
			if(lastModifierNew != lastModifiedOld)
			{
				System.out.println("Rechargement du modèle..");
				Controller.lancerApprentissage(this.ai);
				lastModifiedOld = lastModifierNew; 
			}
			
			
			if(Main.configThread.isInterrupted()) {
				System.out.println("Thread config interrompu");
				if(Main.learningThread != null)
					Main.learningThread.interrupt();
				return null;
			}
		}
	}

}
