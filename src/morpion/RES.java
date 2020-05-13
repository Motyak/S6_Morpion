package morpion;

import java.io.File;

/**
 * Is responsible for giving easy access to resources (images)
 * @author Tommy 'Motyak'
 *
 */
final public class RES {
	private static String resDirPath = System.getProperty("user.dir") + File.separator + "res" + File.separator;
	
///////////////////////////////////////////////////////////////////////////////////////////////	
/* Game modes buttons */
	final static public String P_VS_P_UNPRESSED = resDirPath + "pVsP_unpressed2_128" + ".png";
	final static public String P_VS_P_PRESSED = resDirPath + "pVsP_pressed2_128" + ".png";
	final static public String P_VS_P_HOVER = resDirPath + "pVsP_hover_128" + ".png";
	
	final static public String P_VS_AI_UNPRESSED = resDirPath + "pVsAi_unpressed2_128" + ".png";
	final static public String P_VS_AI_PRESSED = resDirPath + "pVsAi_pressed2_128" + ".png";
	final static public String P_VS_AI_HOVER = resDirPath + "pVsAi_hover_128" + ".png";
	
	/**
	 * Give the path to the hover resource of a particular mode
	 * @param mode a mode
	 * @return the hover resource for the concerned mode
	 */
	static public String getHover(Mode mode) {
		if(mode == Mode.P_VS_AI)
			return RES.P_VS_AI_HOVER;
		else if(mode == Mode.P_VS_P)
			return RES.P_VS_P_HOVER;
		return null;
	}
	
	/**
	 * Give the path to the pressed resources of a particular mode
	 * @param mode a mode
	 * @return the pressed resource for the concerned mode
	 */
	static public String getPressed(Mode mode) {
		if(mode == Mode.P_VS_AI)
			return RES.P_VS_AI_PRESSED;
		else if(mode == Mode.P_VS_P)
			return RES.P_VS_P_PRESSED;
		return null;
	}
	
	/**
	 * Give the path to the unpressed resource of a particular mode
	 * @param mode a mode
	 * @return the unpressed resource for the concerned mode
	 */
	static public String getUnpressed(Mode mode) {
		if(mode == Mode.P_VS_AI)
			return RES.P_VS_AI_UNPRESSED;
		else if(mode == Mode.P_VS_P)
			return RES.P_VS_P_UNPRESSED;
		return null;
	}
///////////////////////////////////////////////////////////////////////////////////////////////
	
/* Application icon */
	final static public String APP_ICON = resDirPath + "icon" + ".png";
	
/* Menu icons (below the arrow) */
	final static public String GEAR_ICON = resDirPath + "gear" + ".png";
	final static public String GAMEPAD_ICON = resDirPath + "gamepad" + ".png";
	
/* Opponent images */
	
	final static public String OPPONENT_AI = resDirPath + "computer_64x64" + ".png";
	final static public String OPPONENT_PERSON = resDirPath + "user_64x64" + ".png";

}
