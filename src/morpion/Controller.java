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
	private Partie_Ent partie;
	
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
		this.partie = new Partie_Ent();
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
		Grille grille = this.ent.getGrille();
		
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			this.ihm.writeCase(i, grille.at(i));
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
		System.out.println("Modèle chargé : " + abstractionLevel + " , " + learningRate);
	}
	
	public void proposerCoup(int id) {
		
		Case c = this.ent.getGrille().at(id);
		
		if(c == Case.VIDE)
		{
			Grille save = new Grille(this.ent.getGrille());
			this.ent.getGrille().set(id, Case.valueOf(this.ent.getTourJeu().toString()));
			if(this.ent.getTourJeu() == Joueur.X)
				this.partie.coupsX.add(new Coup_Ent(save, new Grille(this.ent.getGrille())));
			else
				this.partie.coupsY.add(new Coup_Ent(save, new Grille(this.ent.getGrille())));
			this.incrementerTourDeJeu();
			this.entToIhm();
//			this.ent.getGrille().afficher();
			Joueur vainqueur = this.ent.getGrille().finDePartie();
			boolean partieTerminee = (vainqueur != null) || this.ent.getGrille().is_filled();
			if(partieTerminee)
			{
				if(vainqueur != null)
				{
					System.out.println("Le vainqueur est " + vainqueur.toString());
//					afficher les coups du vainqueur, DEBUG
					this.partie.afficherCoups(vainqueur);
				}
				else
					System.out.println("Aucun gagnant");
				
				this.ent.getGrille().clear();
				this.partie.reset();	//clear les coups
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
//				this.ent.getGrille().afficher();
				vainqueur = this.ent.getGrille().finDePartie();
				partieTerminee = (vainqueur != null) || this.ent.getGrille().is_filled();
				if(partieTerminee)
				{
					if(vainqueur != null)
					{
						System.out.println("Le vainqueur est " + vainqueur.toString());
//						afficher les coups du vainqueur, DEBUG
						this.partie.afficherCoups(vainqueur);
					}
					else
						System.out.println("Aucun gagnant");
					
					this.ent.getGrille().clear();
					this.partie.reset();	//clear les coups
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
		int[] output = this.sortedOutput(this.aiModel.forwardPropagation(input));
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
	
	private void incrementerTourDeJeu()
	{
		Joueur j = this.ent.getTourJeu();
		j = j.next();
		this.ent.setTourJeu(j);
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
	
	private double[] grilleToDoubles(Grille grille)
	{
		double[] res = new double[Ent.TAILLE_GRILLE];
		for(int i = 0 ; i < Ent.TAILLE_GRILLE ; ++i)
			res[i] = (double)grille.at(i).getValue();
		return res;
	}
}
