package morpion;

import Mk.TextFile;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Controller {
	private Ihm ihm;
	private Ent ent;
	
	private MultiLayerPerceptron aiModel;
	
	public final static String DATA_DIRPATH = 
			System.getProperty("user.dir") + File.separator + "data" + File.separator;
	
	public final static String CONF_FILE_DEFCONTENT = 
			"#mode facile\n1:3,0.1\n#mode normal\n2:6,0.75\n#mode difficile\n3:9,0.9\n";
	
	public final static String CONF_FILENAME = "difficulties.conf";
	
	Controller(Ihm ihm, Ent ent) throws Exception {
		this.ihm = ihm;
		this.ent = ent;
		
		this.initDataFiles();
		this.loadAiModel();
	}
	
//	public static void main(String[] args) throws Exception {
		//TU check lines
//		Ent ent = new Ent();
//		Controller ctrl = new Controller(new Ihm(), ent);
		
		
//		Case[] grille = ent.getGrille();
//		grille[0] = Case.VIDE;
//		grille[1] = Case.O;
//		grille[2] = Case.O;
//		
//		grille[3] = Case.O;
//		grille[4] = Case.O;
//		grille[5] = Case.O;
//		
//		grille[6] = Case.X;
//		grille[7] = Case.VIDE;
//		grille[8] = Case.X;
//		
//		ctrl.afficherGrille();
//		Joueur j = ctrl.finDePartie();
//		if(j != null)
//			System.out.println(j.toString());	
//	}
	
	public void entToIhm() {
		Case[] grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille[i]);
		this.ihm.setTourDeJeu(this.ent.getTourJeu());
	}
	
//	check any missing file, create them with default values if so
	public void initDataFiles() throws Exception {
		File dataDir = new File(Controller.DATA_DIRPATH);
		if(!dataDir.exists())
		{
			dataDir.mkdir();
			TextFile.stringToFile(Controller.CONF_FILE_DEFCONTENT, Controller.DATA_DIRPATH + Controller.CONF_FILENAME);

			Matcher mat = Pattern.compile("\n.*?:(.*?)\n").matcher(Controller.CONF_FILE_DEFCONTENT);
			while(mat.find())
			{
				Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
				config.find();
				int abstractionLevel = Integer.parseInt(config.group(1));
				double learningRate = Double.parseDouble(config.group(2));
				int[] layers = new int[]{Ent.TAILLE_GRILLE, abstractionLevel, Ent.TAILLE_GRILLE};
				MultiLayerPerceptron net = new MultiLayerPerceptron(layers, learningRate, new SigmoidalTransferFunction());
				if(!net.save(Controller.DATA_DIRPATH + abstractionLevel + "_" + learningRate + ".srl"))
					throw new Exception("Error creating the file " + abstractionLevel + "_" + learningRate + ".srl");
			}
		}
	}
	
	public void loadAiModel() throws IOException {
		Difficulte diff = this.ent.getDiff();
		String conf = TextFile.fileToString(Controller.DATA_DIRPATH + Controller.CONF_FILENAME);
		Matcher mat = Pattern.compile(diff.getValue() + ":(.*?)\n").matcher(conf);
		mat.find();
		Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
		config.find();
		int abstractionLevel = Integer.parseInt(config.group(1));
		double learningRate = Double.parseDouble(config.group(2));
		String filename = abstractionLevel + "_" + learningRate + ".srl";

		this.aiModel = MultiLayerPerceptron.load(Controller.DATA_DIRPATH + filename);
		System.out.println("Difficulté : " + this.ent.getDiff());
		System.out.println("Modèle chargée : " + abstractionLevel + " , " + learningRate);
	}
	
	public void proposerCoup(int id) {
		
		Case c = this.ent.getGrille()[id];
		if(c == Case.VIDE)
		{
			this.ent.getGrille()[id] = Case.valueOf(this.ent.getTourJeu().toString());
			this.incrementerTourDeJeu();
			this.entToIhm();
			this.afficherGrille();	//debug
			Joueur vainqueur = this.finDePartie();
			boolean partieTerminee = (vainqueur != null) || this.grilleComplete();
			if(partieTerminee)
			{
				if(vainqueur != null)
					System.out.println("Le vainqueur est " + vainqueur.toString());
				else
					System.out.println("Aucun gagnant");
				
				this.clearGrille();
				this.ent.setTourJeu(Joueur.values()[0]);
				this.entToIhm();
				return;
			}
			
			
			
			if(this.ent.getMode() == Mode.P_VS_AI)
			{
//				this.aiPlaysRandomly();
				this.aiPlays();
				this.incrementerTourDeJeu();
				this.entToIhm();
				this.afficherGrille();	//debug
				vainqueur = this.finDePartie();
				partieTerminee = (vainqueur != null) || this.grilleComplete();
				if(partieTerminee)
				{
					if(vainqueur != null)
						System.out.println("Le vainqueur est " + vainqueur.toString());
					else
						System.out.println("Aucun gagnant");
					
					this.clearGrille();
					this.ent.setTourJeu(Joueur.values()[0]);
					this.entToIhm();
				}
			}
		}	
	}
	
	private void aiPlays()
	{
		Case[] grille = this.ent.getGrille();
		double[] input = this.grilleToDoubles(grille);
		int[] output = this.sortedOutput(this.aiModel.forwardPropagation(input));
		int i = 0;
		while(grille[output[i]] != Case.VIDE)
			++i;
		grille[output[i]] = Case.O;
	}
	
	private void aiPlaysRandomly()
	{
		Case[] grille = this.ent.getGrille();
		int aleat;
		do
		{
			Random r = new Random();
			aleat = r.nextInt(8);
		} while(grille[aleat] != Case.VIDE);
		grille[aleat] = Case.O;
	}
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
	
	private void clearGrille()
	{
		Case[] grille = this.ent.getGrille();
		
		Arrays.fill(grille, Case.VIDE);
		
		System.out.println("RESET");
	}
	
	private boolean grilleComplete()
	{
		Case[] grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
		{
			if(grille[i] == Case.VIDE)
				return false;
		}
		return true;
	}
	
	private Joueur finDePartie()
	{
		Joueur j = this.checkLines();
		if(j == null)
			j = this.checkColumns();
		if(j == null)
			j = this.checkDiags();
		return j;
	}
	
	private int sumOfRange(List<Case> cases)
	{
		int sum = 0;
		for(Case c : cases)
			sum += c.getValue();
		
		return sum;
	}
	
	private Joueur checkLines()
	{
		Case[] grille = this.ent.getGrille();
		List<Case> line = new ArrayList<>();
		
		int i = 0;
		int j;
	
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE ; ++j)
				line.add(grille[j]);
			int sum = this.sumOfRange(line);
			if(sum == Ent.DIM_GRILLE * Case.X.getValue())
				return Joueur.X;
			else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
				return Joueur.O;
			line.clear();
		}
		return null;
	}
	
	private Joueur checkColumns()
	{
		Case[] grille = this.ent.getGrille();
		List<Case> column = new ArrayList<>();
		
		int i = 0;
		int j;
	
		for(; i < Ent.DIM_GRILLE ; ++i)	//+0, +1, +2
		{
			for(j = i ; j <= 2 * Ent.DIM_GRILLE + i ; j+=Ent.DIM_GRILLE)
				column.add(grille[j]);
			int sum = this.sumOfRange(column);
			if(sum == Ent.DIM_GRILLE * Case.X.getValue())
				return Joueur.X;
			else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
				return Joueur.O;
			column.clear();
		}
		return null;
	}
	
	private Joueur checkDiags()
	{
		Case[] grille = this.ent.getGrille();
		List<Case> diag = new ArrayList<>();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; i += 4)
			diag.add(grille[i]);
		int sum = this.sumOfRange(diag);
		if(sum == Ent.DIM_GRILLE * Case.X.getValue())
			return Joueur.X;
		else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
			return Joueur.O;
		diag.clear();
		for(int i = 2 ; i <= 2 * Ent.DIM_GRILLE ; i += 2)
			diag.add(grille[i]);
		sum = this.sumOfRange(diag);
		if(sum == Ent.DIM_GRILLE * Case.X.getValue())
			return Joueur.X;
		else if(sum == Ent.DIM_GRILLE * Case.O.getValue())
			return Joueur.O;
		
		return null;
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
	
	private double[] grilleToDoubles(Case[] grille)
	{
		double[] res = new double[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			res[i] = (double)grille[i].getValue();
		return res;
	}
	
	//debug
	private void afficherGrille()
	{
		Case[] grille = this.ent.getGrille();
		
		int i = 0;
		int j = 0;
		System.out.println("--------------------");
		for(; i < Ent.DIM_GRILLE ; ++i)
		{
			for(j = Ent.DIM_GRILLE * i ; j < (i + 1) * Ent.DIM_GRILLE - 1 ; ++j)
			{
				System.out.print(grille[j] + "\t");
			}
			System.out.print(grille[j] + "\n\n");
		}
		System.out.println("--------------------");
	}
}
