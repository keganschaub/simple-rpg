package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import model.Item;
import model.RPGModel;
import model.Unit;

/**
 * GUI text view from Iteration I of this final project. This does not
 * contain all the elements required for Iteration II and is not intended
 * to be graded for Iteration II.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class RPGTextView extends JFrame implements Observer {

	private static final long serialVersionUID = -2922160929049577628L;
	public static String baseDir = System.getProperty("user.dir")
			  + System.getProperty("file.separator") + "src"
		      + System.getProperty("file.separator") + "songs"
		      + System.getProperty("file.separator");
	private RPGModel model;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem newGame;
	private JTextArea display;
	private JTextArea gameInfo;
	private Unit currentUnit;
	private NewGamePanel newGamePanel;
	
	private ArrowsListener akl;
	private InteractionListener il;
	private AttackListener al;
	private TradeListener tl;
	private SkillListener sl;
	
	public static void main(String[] args) {
		JFrame view = new RPGTextView();
		view.setVisible(true);
	}
	
	public RPGTextView() {
		setResizable(false);
		setSize(700,700);
		setLocationRelativeTo(null);
		setTitle("RPG Final Project"); // change to be title of game
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		model = new RPGModel("Standard");
		currentUnit = model.getCurrentUnit();
		
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		newGame = new JMenuItem("New Game");
		menu.add(newGame);
		
		newGamePanel = new NewGamePanel();

		display = new JTextArea();
		display.setEditable(false);
		display.setFont(new Font("Courier", Font.BOLD, 12));
		display.setText(model.toString());
		display.setSize(500,500);
		
		gameInfo = new JTextArea();
		gameInfo.setSize(300,730);
		gameInfo.setFont(new Font("Courier", Font.PLAIN, 12));
		gameInfo.setLineWrap(true);
		gameInfo.setWrapStyleWord(true);
		
		add(menuBar, BorderLayout.NORTH);
		add(newGamePanel, BorderLayout.CENTER);
		//add(display, BorderLayout.CENTER);
		//add(gameInfo, BorderLayout.EAST);
		
		addObservers();
		registerListeners();
		//SongPlayer.playFile(baseDir + "Beep.mp3");
	}
	
	private void addObservers() {
		model.addObserver(this);
	}
	
	private void registerListeners() {
		newGame.addActionListener(new NewGameListener());
		akl = new ArrowsListener();
		display.addKeyListener(akl);
	}
	
	private class NewGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			model = new RPGModel("Standard");
			addObservers();
			currentUnit = model.getCurrentUnit();
			display.setText(model.toString());
			updateHUD();
		}
	}
	
	private class ArrowsListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent ke) {
			Unit currentUnit = model.getCurrentUnit();
			
			int x = currentUnit.getxPos();
			int y = currentUnit.getyPos();

			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				model.move(currentUnit,x, y - 1);
			} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				model.move(currentUnit,x, y + 1);
			} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
				model.move(currentUnit,x - 1, y);
			} else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
				model.move(currentUnit,x + 1, y);
			}
			// Pressing "i" accesses inventory
			else if (ke.getKeyCode() == KeyEvent.VK_I) {
				if(currentUnit.isPlayerUnit()){
					if(currentUnit.getInventory().isEmpty()){
						JOptionPane.showMessageDialog(null, "This unit's inventory is empty.");
					} else {
						Item value = (Item) JOptionPane.showInputDialog(null, 
		                        "What item would you like to use?", 
		                        "Inventory", 
		                         JOptionPane.QUESTION_MESSAGE, 
		                         null,
		                         currentUnit.getInventory().toArray(), 
		                         currentUnit.getInventory().get(0));
						if (value != null) {
							int index = 0;
							for (int i = 0; i < currentUnit.getInventory().size(); i++) {
								if (currentUnit.getInventory().get(i).toString().equals(value.toString()))
									index = i;
							}
							currentUnit.useItem(currentUnit.getInventory().get(index));
							updateHUD();
							model.switchTurns();
						}
					}
				}
			} 
			//Pressing "enter" allows player unit to interact with the environment
			//Open chest, trade with player, just end their turn, etc.
			else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				if(!currentUnit.isPlayerUnit()){	
				} else {
					String value = (String) JOptionPane.showInputDialog(null, 
	                        "What would you like to do?", "Action",
	                         JOptionPane.QUESTION_MESSAGE, 
	                         null,
	                         new String[] { "Attack", "Skill", "Open chest", "Trade", "End turn" },
							 "Attack");
					if (value != null) {
						if(value.equals("Attack")){
							display.removeKeyListener(akl);
							al = new AttackListener();
							display.addKeyListener(al);
							gameInfo.setText(gameInfo.getText() + "\n\nPress an arrow in the direction of who to attack.");
						} else if(value.equals("Skill")) {
							if(currentUnit.getName().equals("Tank")){
								currentUnit.useSkill(null);
							} else {
								display.removeKeyListener(akl);
								sl = new SkillListener();
								display.addKeyListener(sl);
								gameInfo.setText(gameInfo.getText() + "\n\nPress an arrow in the direction of who to attack.");
							}
						} else if(value.equals("Open chest")) {
							display.removeKeyListener(akl);
							il = new InteractionListener();
							display.addKeyListener(il);
							gameInfo.setText(gameInfo.getText() + "\n\nPress an arrow in the direction of what to interact with.");
						} else if(value.equals("Trade")) {
							display.removeKeyListener(akl);
							tl = new TradeListener();
							display.addKeyListener(tl);
							gameInfo.setText(gameInfo.getText() + "\n\nPress an arrow in the direction of who to trade with.");
						} else if(value.equals("End turn")) {
							model.switchTurns();
						}
					}
				}
			}
		}

		@Override 
		public void keyReleased(KeyEvent arg0) {
			// Don't need
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// Don't need
		}
	}
	
	private class AttackListener implements KeyListener {
		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				if (model.attack(currentUnit, "l"))
					JOptionPane.showMessageDialog(null, "You attacked an enemy!");
				else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (model.attack(currentUnit, "r"))
					JOptionPane.showMessageDialog(null, "You attacked an enemy!");
				else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
				if (model.attack(currentUnit, "u"))
					JOptionPane.showMessageDialog(null, "You attacked an enemy!");
				else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			} else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
				if (model.attack(currentUnit, "d"))
					JOptionPane.showMessageDialog(null, "You attacked an enemy!");
				else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			}
			display.removeKeyListener(al);
			display.addKeyListener(akl);
			model.switchTurns();
		}

		public void keyReleased(KeyEvent arg0) {
		}
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
	private class SkillListener implements KeyListener {
		public void keyPressed(KeyEvent ke) {
			int x = currentUnit.getxPos();
			int y = currentUnit.getyPos();
			
			if (ke.getKeyCode() == KeyEvent.VK_LEFT && y-1 >= 0) {
				if (model.getTarget(currentUnit, "l") != null){
					currentUnit.useSkill(model.getTarget(currentUnit, "l"));
					JOptionPane.showMessageDialog(null, "You used " + currentUnit.getSkillName() + " on an enemy!");
				} else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT && y-1 <= model.getMap().getColumns()) {
				if (model.getTarget(currentUnit, "r") != null) {
					currentUnit.useSkill(model.getTarget(currentUnit, "r"));
					JOptionPane.showMessageDialog(null, "You used " + currentUnit.getSkillName() + " on an enemy!");
				} else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			} else if (ke.getKeyCode() == KeyEvent.VK_UP && x-1 >= 0) {
				if (model.getTarget(currentUnit, "u") != null) {
					currentUnit.useSkill(model.getTarget(currentUnit, "u"));
					JOptionPane.showMessageDialog(null, "You used " + currentUnit.getSkillName() + " on an enemy!");
				} else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			} else if (ke.getKeyCode() == KeyEvent.VK_DOWN && x+1 <= model.getMap().getRows()) {
				if (model.getTarget(currentUnit, "d") != null) {
					currentUnit.useSkill(model.getTarget(currentUnit, "d"));
					JOptionPane.showMessageDialog(null, "You used " + currentUnit.getSkillName() + " on an enemy!");
				} else
					JOptionPane.showMessageDialog(null, "There was nothing to attack.");
			}
			
			display.removeKeyListener(sl);
			display.addKeyListener(akl);
			model.switchTurns();
		}

		public void keyReleased(KeyEvent arg0) {
		}
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
	private class InteractionListener implements KeyListener {
		public void keyPressed(KeyEvent ke) {
			int x = currentUnit.getxPos();
			int y = currentUnit.getyPos();
			
			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				if (model.openChest(currentUnit,x,y-1)) {
					JOptionPane.showMessageDialog(null, "You got one " + currentUnit.getInventory().get(currentUnit.getInventory().size()-1).getName() + "!");
					model.switchTurns();
				}
				else
					JOptionPane.showMessageDialog(null, "No chest available to open. Turn not ended.");
			} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (model.openChest(currentUnit,x,y+1)) {
					JOptionPane.showMessageDialog(null, "You got one " + currentUnit.getInventory().get(currentUnit.getInventory().size()-1).getName() + "!");
					model.switchTurns();
				}
				else
					JOptionPane.showMessageDialog(null, "No chest available to open. Turn not ended.");
			} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
				if (model.openChest(currentUnit,x-1,y)) {
					JOptionPane.showMessageDialog(null, "You got one " + currentUnit.getInventory().get(currentUnit.getInventory().size()-1).getName() + "!");
					model.switchTurns();
				}
				else
					JOptionPane.showMessageDialog(null, "No chest available to open. Turn not ended.");
			} else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
				if (model.openChest(currentUnit,x+1,y)) {
					JOptionPane.showMessageDialog(null, "You got one " + currentUnit.getInventory().get(currentUnit.getInventory().size()-1).getName() + "!");
					model.switchTurns();
				}
				else
					JOptionPane.showMessageDialog(null, "No chest available to open. Turn not ended.");
			}
			display.removeKeyListener(il);
			display.addKeyListener(akl);
		}
		public void keyReleased(KeyEvent arg0) {
		}
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
	private class TradeListener implements KeyListener {
		public void keyPressed(KeyEvent ke) {
			int x = currentUnit.getxPos();
			int y = currentUnit.getyPos();
			
			Unit unitToTradeWith = null;
			
			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				unitToTradeWith = model.unitAvailableToTrade(x,y-1);
			} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				unitToTradeWith = model.unitAvailableToTrade(x,y+1);
			} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
				unitToTradeWith = model.unitAvailableToTrade(x-1,y);
			} else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
				unitToTradeWith = model.unitAvailableToTrade(x+1,y);
			}
			
			Item otherUnitItem = null;
			
			if (unitToTradeWith != null) {
				Item value = (Item) JOptionPane.showInputDialog(null, 
                        "What item would you like to receive?", 
                        "Inventory", 
                         JOptionPane.QUESTION_MESSAGE, 
                         null,
                         unitToTradeWith.getInventory().toArray(), 
                         unitToTradeWith.getInventory().get(0));
				if (value != null) {
					int index = 0;
					for (int i = 0; i < unitToTradeWith.getInventory().size(); i++) {
						if (unitToTradeWith.getInventory().get(i).toString().equals(value.toString()))
							index = i;
					}
					otherUnitItem = unitToTradeWith.getInventory().get(index);
					updateHUD();
				}
			} else {
				JOptionPane.showMessageDialog(null, "No unit available to trade.");
				display.removeKeyListener(tl);
				display.addKeyListener(akl);
				return;
			}
				
			Item currentUnitItem = null;
			
			if(currentUnit.isPlayerUnit()){
				if(currentUnit.getInventory().isEmpty()){
					JOptionPane.showMessageDialog(null, "This unit's inventory is empty.");
					display.removeKeyListener(tl);
					display.addKeyListener(akl);
					return;
				} else {
					Item value = (Item) JOptionPane.showInputDialog(null, 
	                        "What item would you like to give away?", 
	                        "Inventory", 
	                         JOptionPane.QUESTION_MESSAGE, 
	                         null,
	                         currentUnit.getInventory().toArray(), 
	                         currentUnit.getInventory().get(0));
					if (value != null) {
						int index = 0;
						for (int i = 0; i < currentUnit.getInventory().size(); i++) {
							if (currentUnit.getInventory().get(i).toString().equals(value.toString()))
								index = i;
						}
						currentUnitItem = currentUnit.getInventory().get(index);
					}
				}
			}
			
			if (currentUnitItem != null && otherUnitItem != null) {
				if (model.tradeItem(currentUnit, unitToTradeWith, currentUnitItem, otherUnitItem))
					JOptionPane.showMessageDialog(null, "Trade successful!");
				else
					JOptionPane.showMessageDialog(null, "Trade not successful.");
			}
			
			display.removeKeyListener(tl);
			display.addKeyListener(akl);
			model.switchTurns();
		}
		public void keyReleased(KeyEvent arg0) {
		}
		public void keyTyped(KeyEvent arg0) {
		}
	}
	
	private class NewGamePanel extends JPanel {
		private static final long serialVersionUID = 7998267027022073239L;

		private NewGamePanel(){
			JButton newGameButton = new JButton("New Game");
			JButton loadGameButton = new JButton("Load Game");
			newGameButton.addActionListener(new NewGameButtonListener());
			loadGameButton.addActionListener(new LoadGameButtonListener());
			add(newGameButton);
			add(loadGameButton);
		}
		
		private class NewGameButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseUnitOrder();
				setUp();
			}
			
			private void chooseUnitOrder(){
				ArrayList<Unit> units = model.getPlayerUnits();
				ArrayList<String> unitString = new ArrayList<String>();
				ArrayList<Unit> ordered = new ArrayList<Unit>();
				
				for(int i=0; i<units.size(); i++){
					unitString.add(units.get(i).getName());
				}
				
				while(ordered.size() != 5){
					String unit = (String) JOptionPane.showInputDialog(null, 
	                        "Choose the unit to be placed next.", 
	                        "Unit turn order", 
	                         JOptionPane.QUESTION_MESSAGE, 
	                         null,
	                         unitString.toArray(), 
	                         unitString.get(0));
					
					int index = 0;
					for (int i= 0; i < units.size(); i++) {
						if (unitString.get(i).equals(unit))
							index = i;
					}
					if(unit != null){
						ordered.add(units.get(index));
					}
				}
				
				model.setPlayerUnits(ordered);
				model.placeUnits();
			}
		}	
		
		private class LoadGameButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Test load button");
			}
		}
	}
	
	//When player starts a new game, this sets it up
	private void setUp(){
		remove(newGamePanel);
		add(display, BorderLayout.CENTER);
		add(gameInfo, BorderLayout.EAST);
		
		addObservers();
		currentUnit = model.getCurrentUnit();
		display.setText(model.toString());
		updateHUD();
	}
	
	public void updateHUD() {
		if(currentUnit.isPlayerUnit()){							//Ensure player cannot access enemy inventory
			gameInfo.setText("Current unit: " + currentUnit.getName() +
					"\nMoves left: " + currentUnit.getMovesLeft() + 
					"\n\nInventory (Press 'I' to access)\n\n" + 
					"Unit stats:" + 
					"\n     Health: " + currentUnit.getHealth() + "/" + currentUnit.getMaxHealth() +
					"\n     Mana: " + currentUnit.getMana() + 
					"\n     Attack: " + currentUnit.getAttack() + 
					"\n     Defense: " + currentUnit.getDefense() + 
					"\n\nPress 'Enter' to interact with the environment or other units.");
		} else {
			gameInfo.setText("Current unit: " + currentUnit.getName() +
					"\nMoves left: " + currentUnit.getMovesLeft() +
					"\n\nUnit stats:" + 
					"\n     Health: " + currentUnit.getHealth() + "/" + currentUnit.getMaxHealth() +
					"\n     Mana: " + currentUnit.getMana() + 
					"\n     Attack: " + currentUnit.getAttack() + 
					"\n     Defense: " + currentUnit.getDefense());
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		currentUnit = model.getCurrentUnit();
		display.setText(model.toString());
		updateHUD();
		
		if (model.isVictory()) {
			System.out.println("You won!");
			JOptionPane.showMessageDialog(null, "You won!");
			display.removeKeyListener(akl);
			return;
		}
		
		if (model.gameOver()) {
			System.out.println("You were defeated.");
			JOptionPane.showMessageDialog(null, "You were defeated.");
			display.removeKeyListener(akl);
			return;
		}
		
		if (!currentUnit.isPlayerUnit()){
			//System.out.println("Update");
			model.executeEnemyAI();
		}
	}

}
