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
	
	public Ai(Difficulty diff) throws Exception {
		this.diff = diff;
		this.data = new Data();
		this.initDataFiles();
		this.loadAiModel();
	}
	
	public int learn() throws IOException {
		double[] input, output;
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(Ai.DATA_DIRPATH + Ai.MOVES_FILENAME));
		String line = reader.readLine();
		int nbDeCoups = 0;
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
			this.model.backPropagate(input, output);
			++nbDeCoups;
			line = reader.readLine();
		}
		reader.close();
		return nbDeCoups;
	}
	
	public void save() throws IOException {
		Pair<Integer,Double> params = this.getModelParams();
		String filename = params.first + "_" + params.second + ".srl";
		this.model.save(Ai.DATA_DIRPATH + filename);
		System.out.println("Modèle sauvegardé : " + filename);
	}
	
	public void changeDiff(Difficulty diff) throws Exception
	{
		this.diff = diff;
		this.data.reset();
		this.initDataFiles();
		this.loadAiModel();
	}
	
	public int[] genOutput(double[] input) {
		return this.sortedOutput(this.model.forwardPropagation(input));
	}
	
	public void editConfigFile() throws IOException {
		Desktop.getDesktop().open(new File(Ai.DATA_DIRPATH + Ai.CONF_FILENAME));
	}
	
	public Pair<Integer,Double> getModelParams() throws IOException
	{
		Difficulty diff = this.diff;
		String conf = TextFile.fileToString(Ai.DATA_DIRPATH + Ai.CONF_FILENAME);
		Matcher mat = Pattern.compile(diff.getValue() + "=(.*?)\n").matcher(conf);
		mat.find();
		Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
		config.find();
		int abstractionLevel = Integer.parseInt(config.group(1));
		double learningRate = Double.parseDouble(config.group(2));
		return new Pair<>(abstractionLevel, learningRate);
	}
	
	public void reset() throws IOException {
		Pair<Integer,Double> params = this.getModelParams();
		int[] layers = {Ent.GRID_SIZE, params.second.intValue(), Ent.GRID_SIZE };
		this.model = new Model(layers, params.second, new SigmoidalTransferFunction());
		this.save();
	}
	
//	return the number of needed elements to learn for optimal usage, depending on the model params
	public int calcOptNb(int abstractionLevel, double learningRate) {
//		hardcoded for the moment
		return 10000;
	}
	
	public Difficulty getDiff() { return this.diff; }
	
//	check any missing file, create them with default values if so
	private void initDataFiles() throws Exception {
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

	private void loadAiModel() throws Exception {
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
		
		System.out.println("Modèle chargé : " + filename);
	}
	
	private int[] sortedOutput(double[] output)
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
	
	static class Model implements Serializable{

		MultiLayerPerceptron p;
		int consideredMoves;
		long fileLastModified;
		
		public Model(int[] layers, double learningRate, TransferFunction fun) {
			this.p = new MultiLayerPerceptron(layers, learningRate, fun);
			this.consideredMoves = 0;
			this.fileLastModified = 0;
		}
		
		public int getConsideredMoves() { return this.consideredMoves; }
		public void setConsideredMoves(int n) { this.consideredMoves = n; }
		public long getFileLastModified() { return this.fileLastModified; }
		public void setFileLastModified(long timestamp) { this.fileLastModified = timestamp; } 
		
		public double[] forwardPropagation(double[] input){ return this.p.forwardPropagation(input); }
		public double backPropagate(double[] input, double[] output) { return this.p.backPropagate(input, output); }
		
		public void save(String filePath) throws IOException {
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		}
		
		public static Model load(String filePath) throws Exception {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Model mod = (Model) ois.readObject();
			ois.close();
			
			return mod;
		}
		
	}
	
	static class Data {
		public List<Coup> coupsX;
		public List<Coup> coupsY;
		
		public Data() {
			this.coupsX = new ArrayList<>();
			this.coupsY = new ArrayList<>();
		}
		
		public void reset()
		{
			this.coupsX.clear();
			this.coupsY.clear();
		}

		public String getCoups(Player j)
		{
			String res = "";
			List<Coup> coups = null;
			if(j == Player.X)
				coups = this.coupsX;
			else if(j == Player.O)
				coups = this.coupsY;
			
			for(int i = 0 ; i < coups.size() ; ++i)
				res += coups.get(i) + "\n";
			
			return res;
		}
		
		static class Coup {
			public Ent.Grid avant;
			public Ent.Grid apres;
			
			public Coup(Ent.Grid avant, Ent.Grid apres)
			{
				this.avant = avant;
				this.apres = apres;
			}
			
			public int getNumCaseJouee()
			{
				int dim = this.avant.getDim();
				int j;
				for(int i = 0 ; i < dim ; ++i)
					for(j = 0 ; j < dim ; ++j)
						if(this.avant.at(i, j) != this.apres.at(i, j))
							return i;
				return -1;
			}
			
			@Override
			public String toString() {
				return this.avant + ";" + this.apres;
			}
		}
	}
}
