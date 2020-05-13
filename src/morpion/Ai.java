package morpion;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Mk.Pair;
import Mk.TextFile;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;
import ai.TransferFunction;

/**
 * Represent the ai model manager and game data 
 * @author Tommy 'Motyak'
 *
 */
public class Ai {
	
	public Model model;
	public Data data;
	
	private Difficulty diff;
	
	public final static String DATA_DIRPATH = 
			System.getProperty("user.dir") + File.separator + "data" + File.separator;
	
	public final static String CONF_FILE_DEFCONTENT = 
			"Easy=3,0.1\nNormal=6,0.75\nHard=9,0.9\n\n";
	
	public final static String CONF_FILENAME = "config.txt";
	public final static String MOVES_FILENAME = "moves.txt";
	
	/**
	 * @param diff the difficulty to apply
	 * @throws IOException in case there is a problem writing files in the user directory or writing/reading the ai model file
	 * @throws ClassNotFoundException in case the serialized object doesn't match the ai model class
	 */
	public Ai(Difficulty diff) throws IOException, ClassNotFoundException {
		this.diff = diff;
		this.data = new Data();
		this.initDataFiles();
		this.loadAiModel();
	}
	
	/**
	 * Make the ai model learn from the moves file
	 * @return the number of learnt moves
	 * @throws IOException in case the file storing the moves cannot be found
	 */
	public int learn() throws IOException {
		double[] input, output;
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(Ai.DATA_DIRPATH + Ai.MOVES_FILENAME));
		String line = reader.readLine();
		int nbOfMoves = 0;
		while(line != null)
		{
			input = new double[Ent.GRID_SIZE];
			output = new double[Ent.GRID_SIZE];
			Matcher mat = Pattern.compile("([^;]+);([^;]+)").matcher(line);
			mat.find();
			List<String> grilleInput = Arrays.asList(mat.group(1).split("\\s*,\\s*"));
			List<String> grilleOutput = Arrays.asList(mat.group(2).split("\\s*,\\s*"));
			for(int i = 0 ; i < Ent.GRID_SIZE ; ++i)
			{
				input[i] = Double.parseDouble(grilleInput.get(i));
				output[i] = Double.parseDouble(grilleOutput.get(i));
			}
			this.model.propagateBackward(input, output);
			++nbOfMoves;
			line = reader.readLine();
		}
		reader.close();
		return nbOfMoves;
	}
	
	/**
	 * Serialize the ai model to its related file
	 * @throws IOException in case the ai model file cannot be written/overwritten
	 */
	public void save() throws IOException {
		Pair<Integer,Double> params = this.getModelParams();
		String filename = params.first + "_" + params.second + ".srl";
		this.model.save(Ai.DATA_DIRPATH + filename);
		System.out.println("Model saved : " + filename);
	}
	
	/**
	 * Change the ai difficulty
	 * @param diff the difficulty to apply
	 * @throws IOException in case the file storing the configuration cannot be found
	 * @throws ClassNotFoundException in case the serialized object doesn't match the ai model class
	 */
	public void changeDiff(Difficulty diff) throws IOException, ClassNotFoundException {
		this.diff = diff;
		this.data.reset();
		this.initDataFiles();
		this.loadAiModel();
	}
	
	/**
	 * Generate ai output, sorted from best to worst based on the greatest decimal values
	 * @param input the ai input in the form of decimal values
	 * @return the indexes of the ai output sorted based on the greatest decimal values
	 */
	public int[] genOutput(double[] input) {
		return this.sortOutput(this.model.propagateForward(input));
	}
	
	/**
	 * Open the conf file with system's default text editor
	 * @throws IOException in case the configuration file cannot be found
	 */
	public void editConfigFile() throws IOException {
		Desktop.getDesktop().open(new File(Ai.DATA_DIRPATH + Ai.CONF_FILENAME));
	}
	
	/**
	 * Get the model parameters based on the configuration file
	 * @return the model parameters : the abstraction level and the learning rate.
	 * @throws IOException in case the configuration file cannot be found
	 */
	public Pair<Integer,Double> getModelParams() throws IOException {
		String conf = TextFile.fileToString(Ai.DATA_DIRPATH + Ai.CONF_FILENAME);
		Matcher mat = Pattern.compile(this.diff.getValue() + "=(.*?)\n").matcher(conf);
		mat.find();
		Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
		config.find();
		int abstractionLevel = Integer.parseInt(config.group(1));
		double learningRate = Double.parseDouble(config.group(2));
		return new Pair<>(abstractionLevel, learningRate);
	}
	
	/**
	 * Overwrite the ai model with default values
	 * @throws IOException in case the ai model file cannot be found
	 */
	public void reset() throws IOException {
		Pair<Integer,Double> params = this.getModelParams();
		int[] layers = {Ent.GRID_SIZE, params.second.intValue(), Ent.GRID_SIZE };
		this.model = new Model(layers, params.second, new SigmoidalTransferFunction());
		this.save();
	}
	
	/**
	 * Calculate the number of needed elements for the ai model to learn
	 * @param abstractionLevel ai model first parameter : the abstraction level
	 * @param learningRate ai model second parameter : the learning rate
	 * @return the number of needed elements to learn for optimal usage, depending on the model parameters
	 */
	public int calcOptNb(int abstractionLevel, double learningRate) {
//		hardcoded for the moment
		return 10000;
	}
	
	public Difficulty getDiff() { return this.diff; }
	
	/**
	 * Check for any missing data file, create them with default values if missing
	 * @throws IOException in case there is a problem writing files in the user directory
	 */
	private void initDataFiles() throws IOException  {
		File dataDir = new File(Ai.DATA_DIRPATH);
		if(!dataDir.exists())
		{
			dataDir.mkdir();
			TextFile.stringToFile("", Ai.DATA_DIRPATH + Ai.MOVES_FILENAME, false);
			TextFile.stringToFile(Ai.CONF_FILE_DEFCONTENT, Ai.DATA_DIRPATH + Ai.CONF_FILENAME, false);

			Matcher mat = Pattern.compile("=(.*?)\n").matcher(Ai.CONF_FILE_DEFCONTENT);
			while(mat.find())
			{
				Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
				config.find();
				int abstractionLevel = Integer.parseInt(config.group(1));
				double learningRate = Double.parseDouble(config.group(2));
				int[] layers = new int[]{Ent.GRID_SIZE, abstractionLevel, Ent.GRID_SIZE};
				Model mod = new Model(layers, learningRate, new SigmoidalTransferFunction());
				mod.save(Ai.DATA_DIRPATH + abstractionLevel + "_" + learningRate + ".srl");
			}
		}
	}

	/**
	 * Unserialize the ai model file
	 * @throws IOException in case there is a problem writing or reading the ai model file
	 * @throws ClassNotFoundException in case the serialized object doesn't match the ai model class
	 */
	private void loadAiModel() throws IOException, ClassNotFoundException {
		Pair<Integer,Double> params = this.getModelParams();
		String filename = params.first + "_" + params.second + ".srl";
		if(!new File(Ai.DATA_DIRPATH + filename).exists()) 
		{
			int[] layers = {Ent.GRID_SIZE, params.second.intValue(), Ent.GRID_SIZE };
			this.model = new Model(layers, params.second, new SigmoidalTransferFunction());
			this.save();
		}
		else
			this.model = Ai.Model.load(Ai.DATA_DIRPATH + filename);
		
		System.out.println("Loaded model : " + filename);
	}
	
	/**
	 * Sort the ai output indexes from best to worst based on the greatest decimal values
	 * @param output the ai output in the form of decimal values
	 * @return the indexes of the ai output sorted based on the greatest decimal values
	 */
	private int[] sortOutput(double[] output)
	{
		int[] sortedIndexes = new int[output.length];
		TreeMap<Double,Integer> map = new TreeMap<Double,Integer>(Collections.reverseOrder());
		for(int i = 0 ; i < output.length ; ++i)
		    map.put( output[i], i );

		Collection<Integer> values = map.values();
		int i = 0;
		for(Integer value : values)
		{
			sortedIndexes[i] = value;
			++i;
		}

		return sortedIndexes;
	}
	
	/**
	 * Represent the ai model
	 * @author Tommy 'Motyak'
	 *
	 */
	static class Model implements Serializable{

		MultiLayerPerceptron p;
		int consideredMoves;
		long fileLastModified;
		
		/**
		 * @param layers the layers
		 * @param learningRate the learning rate
		 * @param fun the transfer function
		 */
		public Model(int[] layers, double learningRate, TransferFunction fun) {
			this.p = new MultiLayerPerceptron(layers, learningRate, fun);
			this.consideredMoves = 0;
			this.fileLastModified = 0;
		}
		
		public int getConsideredMoves() { return this.consideredMoves; }
		public void setConsideredMoves(int n) { this.consideredMoves = n; }
		public long getFileLastModified() { return this.fileLastModified; }
		public void setFileLastModified(long timestamp) { this.fileLastModified = timestamp; } 
		
		/**
		 * Make the ai model generate an output from an input
		 * @param input the ai input
		 * @return the ai output in the form of decimal values
		 */
		public double[] propagateForward(double[] input){ return this.p.forwardPropagation(input); }
		
		
		/**
		 * Make the ai model train from an input and an output
		 * @param input an input
		 * @param output an output
		 * @return the error rate
		 */
		public double propagateBackward(double[] input, double[] output) { return this.p.backPropagate(input, output); }
		
		/**
		 * Serialize the ai model to a file path
		 * @param filePath the file path in which the ai model will be save
		 * @throws IOException in case there is a problem serializing the model
		 */
		public void save(String filePath) throws IOException {
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		}
		
		/**
		 * Unserialize the ai model from a file path
		 * @param filePath the serialized ai model filepath
		 * @return the ai model unserialized
		 * @throws IOException in case the ai model cannot be found or read
		 * @throws ClassNotFoundException in case the serialized object doesn't match the ai model class
		 */
		public static Model load(String filePath) throws IOException, ClassNotFoundException {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Model mod = (Model) ois.readObject();
			ois.close();
			
			return mod;
		}
		
	}
	
	/**
	 * Represent the list of played moves from each player during a game
	 * @author Tommy 'Motyak'
	 *
	 */
	static class Data {
		public List<Move> Xmoves;
		public List<Move> Omoves;
		
		public Data() {
			this.Xmoves = new ArrayList<>();
			this.Omoves = new ArrayList<>();
		}
		
		/**
		 * Clear the list of moves played by each player
		 */
		public void reset()
		{
			this.Xmoves.clear();
			this.Omoves.clear();
		}

		/**
		 * Get all the moves played by a player during the game
		 * @param p the concerned player
		 * @return the moves of the player during the entire game, in a textual form
		 */
		public String getMoves(Player p)
		{
			String res = "";
			List<Move> moves = null;
			if(p == Player.X)
				moves = this.Xmoves;
			else if(p == Player.O)
				moves = this.Omoves;
			
			for(int i = 0 ; i < moves.size() ; ++i)
				res += moves.get(i) + "\n";
			
			return res;
		}
		
		/**
		 * Represent a move played by a player on the grid
		 * @author Tommy 'Motyak'
		 *
		 */
		static class Move {
			public Ent.Grid before;
			public Ent.Grid after;
			
			/**
			 * @param before the state of the grid before the move being played
			 * @param after the state of the grid after the move being played
			 */
			public Move(Ent.Grid before, Ent.Grid after)
			{
				this.before = before;
				this.after = after;
			}
			
			/**
			 * Return the square that has been played in the move
			 * @return the square that has been played in the move
			 */
			public int getPlayedSquare()
			{
				int dim = this.before.getDim();
				int j;
				for(int i = 0 ; i < dim ; ++i)
					for(j = 0 ; j < dim ; ++j)
						if(this.before.at(i, j) != this.after.at(i, j))
							return i;
				return -1;
			}
			
			@Override
			public String toString() {
				return this.before + ";" + this.after;
			}
		}
	}
}
