package morpion;

import java.io.File;

final public class RES {
	private static String resDirPath = System.getProperty("user.dir") + File.separator + "res" + File.separator;
	
///////////////////////////////////////////////////////////////////////////////////////////////	
/* Boutons mode de jeu Joueur contre joueur et Joueur contre AI */
	final static public String P_VS_P_UNPRESSED = resDirPath + "pVsP_unpressed2_128" + ".png";
	final static public String P_VS_P_PRESSED = resDirPath + "pVsP_pressed2_128" + ".png";
	final static public String P_VS_P_HOVER = resDirPath + "pVsP_hover_128" + ".png";
	
	final static public String P_VS_AI_UNPRESSED = resDirPath + "pVsAi_unpressed2_128" + ".png";
	final static public String P_VS_AI_PRESSED = resDirPath + "pVsAi_pressed2_128" + ".png";
	final static public String P_VS_AI_HOVER = resDirPath + "pVsAi_hover_128" + ".png";
	
	static public String getHover(Mode mode) {
		if(mode == Mode.P_VS_AI)
			return RES.P_VS_AI_HOVER;
		else if(mode == Mode.P_VS_P)
			return RES.P_VS_P_HOVER;
		return null;
	}
	
	static public String getPressed(Mode mode) {
		if(mode == Mode.P_VS_AI)
			return RES.P_VS_AI_PRESSED;
		else if(mode == Mode.P_VS_P)
			return RES.P_VS_P_PRESSED;
		return null;
	}
	
	static public String getUnpressed(Mode mode) {
		if(mode == Mode.P_VS_AI)
			return RES.P_VS_AI_UNPRESSED;
		else if(mode == Mode.P_VS_P)
			return RES.P_VS_P_UNPRESSED;
		return null;
	}
///////////////////////////////////////////////////////////////////////////////////////////////
	
/* Icone application */
	final static public String APP_ICON = resDirPath + "icon" + ".png";
	
}
