package morpion;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

public class Ai {
	public MultiLayerPerceptron model;
	public Data data;
	private Difficulte diff;
	
	public final static String DATA_DIRPATH = 
			System.getProperty("user.dir") + File.separator + "data" + File.separator;
	
	public final static String CONF_FILE_DEFCONTENT = 
			"#mode facile\n1:3,0.1\n#mode normal\n2:6,0.75\n#mode difficile\n3:9,0.9\n";
	
	public final static String CONF_FILENAME = "config.txt";
	public final static String COUPS_FILENAME = "coups.txt";
	
	public Ai(Difficulte diff) throws Exception {
		this.diff = diff;
		this.data = new Data();
		this.initDataFiles();
		this.loadAiModel();
	}
	
	public void learn() throws IOException
	{
		double[] input, output;
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(Ai.DATA_DIRPATH + Ai.COUPS_FILENAME));
		String line = reader.readLine();
		while(line != null)
		{
			input = new double[Ent.TAILLE_GRILLE];
			output = new double[Ent.TAILLE_GRILLE];
			Matcher mat = Pattern.compile("([^;]+);([^;]+)").matcher(line);
			mat.find();
			List<String> grilleInput = Arrays.asList(mat.group(1).split("\\s*,\\s*"));
			List<String> grilleOutput = Arrays.asList(mat.group(2).split("\\s*,\\s*"));
			for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			{
				input[i] = Double.parseDouble(grilleInput.get(i));
				output[i] = Double.parseDouble(grilleOutput.get(i));
			}
			this.model.backPropagate(input, output);
			line = reader.readLine();
		}
		reader.close();
		
		Pair<Integer,Double> params = this.getModelParams();
		String filename = params.first + "_" + params.second + ".srl";
		this.model.save(Ai.DATA_DIRPATH + filename);
		System.out.println("Modèle sauvegardé : " + filename);
	}
	
	public int[] genOutput(double[] input) {
		return this.sortedOutput(this.model.forwardPropagation(input));
	}
	
	public void editConfigFile() throws IOException
	{
		Desktop.getDesktop().open(new File(Ai.DATA_DIRPATH + Ai.CONF_FILENAME));
	}
	
//	check any missing file, create them with default values if so
	private void initDataFiles() throws Exception {
		File dataDir = new File(Ai.DATA_DIRPATH);
		if(!dataDir.exists())
		{
			dataDir.mkdir();
			TextFile.stringToFile("", Ai.DATA_DIRPATH + Ai.COUPS_FILENAME, false);
			TextFile.stringToFile(Ai.CONF_FILE_DEFCONTENT, Ai.DATA_DIRPATH + Ai.CONF_FILENAME, false);

			Matcher mat = Pattern.compile("\n.*?:(.*?)\n").matcher(Ai.CONF_FILE_DEFCONTENT);
			while(mat.find())
			{
				Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
				config.find();
				int abstractionLevel = Integer.parseInt(config.group(1));
				double learningRate = Double.parseDouble(config.group(2));
				int[] layers = new int[]{Ent.TAILLE_GRILLE, abstractionLevel, Ent.TAILLE_GRILLE};
				MultiLayerPerceptron net = new MultiLayerPerceptron(layers, learningRate, new SigmoidalTransferFunction());
				if(!net.save(Ai.DATA_DIRPATH + abstractionLevel + "_" + learningRate + ".srl"))
					throw new Exception("Error creating the file " + abstractionLevel + "_" + learningRate + ".srl");
			}
		}
	}
	
	private void loadAiModel() throws IOException {
		Pair<Integer,Double> params = this.getModelParams();
		String filename = params.first + "_" + params.second + ".srl";

		this.model = MultiLayerPerceptron.load(Ai.DATA_DIRPATH + filename);
		System.out.println("Difficulté : " + this.diff);
		System.out.println("Modèle chargé : " + filename);
	}
	
	private Pair<Integer,Double> getModelParams() throws IOException
	{
		Difficulte diff = this.diff;
		String conf = TextFile.fileToString(Ai.DATA_DIRPATH + Ai.CONF_FILENAME);
		Matcher mat = Pattern.compile(diff.getValue() + ":(.*?)\n").matcher(conf);
		mat.find();
		Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
		config.find();
		int abstractionLevel = Integer.parseInt(config.group(1));
		double learningRate = Double.parseDouble(config.group(2));
		return new Pair<>(abstractionLevel, learningRate);
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

		public String getCoups(Joueur j)
		{
			String res = "";
			List<Coup> coups = null;
			if(j == Joueur.X)
				coups = this.coupsX;
			else if(j == Joueur.O)
				coups = this.coupsY;
			
			for(int i = 0 ; i < coups.size() ; ++i)
				res += coups.get(i).toString() + "\n";
			
			return res;
		}
		
		static class Coup {
			public Grille avant;
			public Grille apres;
			
			public Coup(Grille avant, Grille apres)
			{
				this.avant = avant;
				this.apres = apres;
			}
			
			public int getNumCaseJouee()
			{
				for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
					if(avant.at(i) != apres.at(i))
						return i;
				return -1;
			}
			
			@Override
			public String toString() {
				String res = "";
				
				int i = 0;
				for(; i < Ent.TAILLE_GRILLE - 1 ; ++i)
					res += this.avant.at(i).getValue() + ",";
				res += this.avant.at(i).getValue() + ";";
				for(i = 0 ; i < Ent.TAILLE_GRILLE - 1 ; ++i)
					res += this.apres.at(i).getValue() + ",";
				res += this.apres.at(i).getValue();
				
				return res;
			}
		}
	}
}
