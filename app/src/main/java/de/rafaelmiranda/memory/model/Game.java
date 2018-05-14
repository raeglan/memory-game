package de.rafaelmiranda.memory.model;

import de.rafaelmiranda.memory.themes.Theme;

/**
 * This is instance of active playing game
 * 
 * @author sromku
 */
public class Game {

	/**
	 * The board configuration
	 */
	public BoardConfiguration boardConfiguration;

	/**
	 * The board arrangment
	 */
	public BoardArrangement boardArrangement;

	/**
	 * The selected theme
	 */
	public Theme theme;

	public GameState gameState;

}
