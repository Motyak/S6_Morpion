package morpion;

import Mk.Pair;
import Mk.TextFile;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
	private Ihm ihm;
	private Ent ent;
	private Ai ai;
//	private MultiLayerPerceptron aiModel;
//	private Partie_Ent partie;
	

	

	
	Controller(Ihm ihm, Ent ent) throws Exception {
		this.ihm = ihm;
		this.ent = ent;
		this.ai = new Ai(this.ent.getDiff());
		
//		this.initDataFiles();
//		this.loadAiModel();
//		this.partie = new Partie_Ent();
	}
	
//	public static void main(String[] args) throws Exception {
////		TU check lines
//		Ent ent = new Ent();
//		Controller ctrl = new Controller(new Ihm(), ent);
//	}
	
	public void entToIhm() {
		Grille grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille.at(i));
		this.ihm.setTourDeJeu(this.ent.getTourJeu());
	}
	
////	check any missing file, create them with default values if so
//	private void initDataFiles() throws Exception {
//		File dataDir = new File(Controller.DATA_DIRPATH);
//		if(!dataDir.exists())
//		{
//			dataDir.mkdir();
//			TextFile.stringToFile("", Controller.DATA_DIRPATH + Controller.COUPS_FILENAME, false);
//			TextFile.stringToFile(Controller.CONF_FILE_DEFCONTENT, Controller.DATA_DIRPATH + Controller.CONF_FILENAME, false);
//
//			Matcher mat = Pattern.compile("\n.*?:(.*?)\n").matcher(Controller.CONF_FILE_DEFCONTENT);
//			while(mat.find())
//			{
//				Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
//				config.find();
//				int abstractionLevel = Integer.parseInt(config.group(1));
//				double learningRate = Double.parseDouble(config.group(2));
//				int[] layers = new int[]{Ent.TAILLE_GRILLE, abstractionLevel, Ent.TAILLE_GRILLE};
//				MultiLayerPerceptron net = new MultiLayerPerceptron(layers, learningRate, new SigmoidalTransferFunction());
//				if(!net.save(Controller.DATA_DIRPATH + abstractionLevel + "_" + learningRate + ".srl"))
//					throw new Exception("Error creating the file " + abstractionLevel + "_" + learningRate + ".srl");
//			}
//		}
//	}
//	
//	private void loadAiModel() throws IOException {
//		Pair<Integer,Double> params = this.getModelParams();
//		String filename = params.first + "_" + params.second + ".srl";
//
//		this.aiModel = MultiLayerPerceptron.load(Controller.DATA_DIRPATH + filename);
//		System.out.println("Difficulté : " + this.ent.getDiff());
//		System.out.println("Modèle chargé : " + filename);
//	}
	
	public void proposerCoup(int id) throws IOException {
		
		Case c = this.ent.getGrille().at(id);
		
		if(c == Case.VIDE)
		{
			Grille save = new Grille(this.ent.getGrille());
			this.ent.getGrille().set(id, Case.valueOf(this.ent.getTourJeu().toString()));
			if(this.ent.getTourJeu() == Joueur.X)
//				this.partie.coupsX.add(new Coup_Ent(save, new Grille(this.ent.getGrille())));
				this.ai.data.coupsX.add(new Ai.Data.Coup(save, new Grille(this.ent.getGrille())));
			else
//				this.partie.coupsY.add(new Coup_Ent(save, new Grille(this.ent.getGrille())));
				this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Grille(this.ent.getGrille())));
			this.incrementerTourDeJeu();
			this.entToIhm();
			Joueur vainqueur = this.ent.getGrille().finDePartie();
			boolean partieTerminee = (vainqueur != null) || this.ent.getGrille().is_filled();
			if(partieTerminee)
			{
				if(vainqueur != null)
				{
					System.out.println("Le vainqueur est " + vainqueur.toString());
//					TextFile.stringToFile(this.partie.getCoups(vainqueur), 
//							Controller.DATA_DIRPATH + Controller.COUPS_FILENAME, true);
					TextFile.stringToFile(this.ai.data.getCoups(vainqueur), 
							Ai.DATA_DIRPATH + Ai.COUPS_FILENAME, true);
//					this.aiLearns();
					this.ai.learn();
				}
				else
					System.out.println("Aucun gagnant");
				
				
				
				this.ent.getGrille().clear();
//				this.partie.reset();	//clear les coups
				this.ai.data.reset();
				this.ent.setTourJeu(Joueur.values()[0]);
				this.entToIhm();
				return;
			}
			
			
			
			if(this.ent.getMode() == Mode.P_VS_AI)
			{
				save = new Grille(this.ent.getGrille());
//				this.aiPlaysRandomly();
				this.aiPlays();
//				this.partie.coupsY.add(new Coup_Ent(save, new Grille(this.ent.getGrille())));
				this.ai.data.coupsY.add(new Ai.Data.Coup(save, new Grille(this.ent.getGrille())));
				this.incrementerTourDeJeu();
				this.entToIhm();
				vainqueur = this.ent.getGrille().finDePartie();
				partieTerminee = (vainqueur != null) || this.ent.getGrille().is_filled();
				if(partieTerminee)
				{
					if(vainqueur != null)
					{
						System.out.println("Le vainqueur est " + vainqueur.toString());
//						TextFile.stringToFile(this.partie.getCoups(vainqueur), 
//								Controller.DATA_DIRPATH + Controller.COUPS_FILENAME, true);
						TextFile.stringToFile(this.ai.data.getCoups(vainqueur), 
								Ai.DATA_DIRPATH + Ai.COUPS_FILENAME, true);
//						this.aiLearns();
						this.ai.learn();
					}
					else
						System.out.println("Aucun gagnant");
					
					
					
					this.ent.getGrille().clear();
//					this.partie.reset();	//clear les coups
					this.ai.data.reset();
					this.ent.setTourJeu(Joueur.values()[0]);
					this.entToIhm();
				}
			}
		}	
	}
	
	private void aiPlays()
	{
		Grille grille = this.ent.getGrille();
		double[] input = this.grilleToDoubles(grille);
//		int[] output = this.sortedOutput(this.aiModel.forwardPropagation(input));
		int[] output = this.sortedOutput(this.ai.model.forwardPropagation(input));
		int i = 0;
		while(grille.at(output[i]) != Case.VIDE)
			++i;
		grille.set(output[i], Case.O);
	}
	
	private void aiPlaysRandomly()
	{
		Grille grille = this.ent.getGrille();
		int aleat;
		do
		{
			Random r = new Random();
			aleat = r.nextInt(8);
		} while(grille.at(aleat) != Case.VIDE);
		grille.set(aleat, Case.O);
	}
	
//	private Pair<Integer,Double> getModelParams() throws IOException
//	{
//		Difficulte diff = this.ent.getDiff();
//		String conf = TextFile.fileToString(Controller.DATA_DIRPATH + Controller.CONF_FILENAME);
//		Matcher mat = Pattern.compile(diff.getValue() + ":(.*?)\n").matcher(conf);
//		mat.find();
//		Matcher config = Pattern.compile("([^,]+),([^,]+)").matcher(mat.group(1));
//		config.find();
//		int abstractionLevel = Integer.parseInt(config.group(1));
//		double learningRate = Double.parseDouble(config.group(2));
//		return new Pair<>(abstractionLevel, learningRate);
//	}
//	
//	private void aiLearns() throws IOException
//	{
//		double[] input, output;
//		BufferedReader reader;
//		reader = new BufferedReader(new FileReader(Controller.DATA_DIRPATH + Controller.COUPS_FILENAME));
//		String line = reader.readLine();
//		while(line != null)
//		{
//			input = new double[Ent.TAILLE_GRILLE];
//			output = new double[Ent.TAILLE_GRILLE];
//			Matcher mat = Pattern.compile("([^;]+);([^;]+)").matcher(line);
//			mat.find();
//			List<String> grilleInput = Arrays.asList(mat.group(1).split("\\s*,\\s*"));
//			List<String> grilleOutput = Arrays.asList(mat.group(2).split("\\s*,\\s*"));
//			for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
//			{
//				input[i] = Double.parseDouble(grilleInput.get(i));
//				output[i] = Double.parseDouble(grilleOutput.get(i));
//			}
//			this.aiModel.backPropagate(input, output);
//			line = reader.readLine();
//		}
//		reader.close();
//		
//		Pair<Integer,Double> params = this.getModelParams();
//		String filename = params.first + "_" + params.second + ".srl";
//		this.aiModel.save(Controller.DATA_DIRPATH + filename);
//		System.out.println("Modèle sauvegardé : " + filename);
//	}
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
	}
	
	private int[] sortedOutput(double[] output)
	{
//		//debug
//		for(int i = 0 ; i < output.length ; ++i)
//			System.out.println(output[i]);
//		System.out.println();
		
		
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
	
	private double[] grilleToDoubles(Grille grille)
	{
		double[] res = new double[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			res[i] = (double)grille.at(i).getValue();
		return res;
	}
}
