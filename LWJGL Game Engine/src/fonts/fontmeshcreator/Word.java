package fonts.fontmeshcreator;

import java.util.ArrayList;
import java.util.List;

public class Word {

	private List<Character> characters;
	private double width = 0;
	private double fontSize;

	protected Word(double fontSize) {
		
		this.fontSize = fontSize;
		
		this.characters = new ArrayList<>();
		
	}

	protected void addCharacter(Character character) {

		characters.add(character);
		width += character.getxAdvance() * fontSize;
		
	}
	
	protected List<Character> getCharacters() {

		return characters;
		
	}

	protected double getWordWidth() {

		return width;
		
	}

}
