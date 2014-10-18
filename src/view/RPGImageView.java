package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import model.Item;
import model.Map;
import model.RPGModel;
import model.Unit;
import songplayer.AudioFilePlayer;

/**
 * The view through which the user interacts with the game.
 * Sprites designed by Kegan Schaub. Music composed and
 * recorded by Terron Ishihara. Music requires extra
 * referenced libraries in order to play mp3s. 
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class RPGImageView extends JFrame implements Observer {

	private static final long serialVersionUID = -2922160929049577628L;
	public static String baseDir = System.getProperty("user.dir")
			  + System.getProperty("file.separator") + "src"
		      + System.getProperty("file.separator") + "songs"
		      + System.getProperty("file.separator");
	private RPGModel model;
	private JMenuBar menuBar;
	private JMenu menu;
	private JButton newGame;
	private JButton loadGame;
	private JButton standardMode;
	private JButton survivalMode;
	private JButton bossMode;
	private JMenuItem save;
	private JMenuItem toTitle;
	private AbstractPanel display;
	private JTextArea gameInfo;
	private Unit currentUnit;
	
	private ArrowsListener akl;
	private InteractionListener il;
	private AttackListener al;
	private TradeListener tl;
	private SkillListener sl;
	private AIAttackListener aial;
	
	private Timer attackTimer;
	
	private AudioFilePlayer player;
	
	public static void main(String[] args) {
		JFrame view = new RPGImageView();
		view.setVisible(true);
	}
	
	// Set up initial view with a title screen
	public RPGImageView() {
		setResizable(false);
		setSize(1000,815);
		setLocationRelativeTo(null);
		setTitle("Beep Boop");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		save = new JMenuItem("Save");
		toTitle = new JMenuItem("Return to Title Screen");
		menu.add(save);
		menu.add(toTitle);
		
		model = new RPGModel("STANDARD");
		currentUnit = model.getCurrentUnit();
		
		display = new TitlePanel();
		
		gameInfo = new JTextArea();
		gameInfo.setFont(new Font("Bauhaus 93", Font.PLAIN, 16));
		gameInfo.setLineWrap(true);
		gameInfo.setWrapStyleWord(true);
		gameInfo.setPreferredSize(new Dimension(232,800));
		gameInfo.setForeground(Color.WHITE);
		gameInfo.setBackground(Color.BLACK);
		
		add(display, BorderLayout.CENTER);

		registerListeners();

		player = new AudioFilePlayer(baseDir + "Beep.mp3");
		player.start();
	}
	
	private void registerListeners() {
		save.addActionListener(new SaveListener());
		toTitle.addActionListener(new ToTitleListener());
		
		akl = new ArrowsListener();
		
		aial = new AIAttackListener();
		attackTimer = new Timer(200,aial);
		attackTimer.setRepeats(false);
	}
	
	// Menu only shows during battle
	// Return to Title Screen button will activate this listener
	private class ToTitleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			remove(menu);
			remove(display);
			remove(gameInfo);
			display = new TitlePanel();
			add(display,BorderLayout.CENTER);
			display.updateUI();
			
			try {
				player.terminate();
			} catch (IOException e) {
				System.out.println("Error in terminating audio.");
			}
			player = new AudioFilePlayer(baseDir + "Beep.mp3");
			player.start();
		}
	}
	
	// For the new game button, transitions to selection screen
	private class NewGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			remove(display);
			display = new SelectionPanel();
			add(display,BorderLayout.CENTER);
			display.updateUI();
		}
	}
	
	// Saves the game
	private class SaveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (saveToFile(model,"rpg.save"))
				JOptionPane.showMessageDialog(null, "Save successful!");
			else
				JOptionPane.showMessageDialog(null, "Save unsuccessful.");
		}
	}
	
	// Loads the game, assuming something has been saved
	private class LoadGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			model = (RPGModel) readFromFile("rpg.save");
			if (model != null) {
				JOptionPane.showMessageDialog(null, "Load successful!");
				model.addObserver(getGUI());

				remove(display);
				display = new BattlePanel();
				add(menuBar, BorderLayout.NORTH);
				add(display,BorderLayout.CENTER);
				add(gameInfo,BorderLayout.EAST);
				display.setFocusable(true);
				display.requestFocus();
				display.addKeyListener(akl);
				display.repaint();

				model.addObserver(display);
				updateHUD();

				display.updateUI();
				
				try {
					player.terminate();
				} catch (IOException e) {
					System.out.println("Error in terminating audio.");
				}
				player = new AudioFilePlayer(baseDir + "Pshoo.mp3");
				player.start();
			}
			else
				JOptionPane.showMessageDialog(null, "No save file to load from.");
		}
	}
	
	// Transitions to the battle screen depending on which
	// mode the user chooses
	private class SelectionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			String songToPlay = "";
			if (ae.getSource() == standardMode) {
				model = new RPGModel("STANDARD");
				songToPlay = baseDir + "Pshoo.mp3";
			} else if (ae.getSource() == survivalMode) {
				model = new RPGModel("SURVIVAL");
				songToPlay = baseDir + "smooth.mp3";
			} else if (ae.getSource() == bossMode) {
				model = new RPGModel("BOSS");
				songToPlay = baseDir + "triumph.mp3";
			}
			
			model.addObserver(getGUI());

			remove(display);
			display = new BattlePanel();
			add(menuBar, BorderLayout.NORTH);
			add(display,BorderLayout.CENTER);
			add(gameInfo,BorderLayout.EAST);
			display.setFocusable(true);
			display.requestFocus();
			display.addKeyListener(akl);

			chooseUnitOrder();

			model.addObserver(display);
			updateHUD();

			display.updateUI();
			
			try {
				player.terminate();
			} catch (IOException e) {
				System.out.println("Error in terminating audio.");
			}
			player = new AudioFilePlayer(songToPlay);
			player.start();

		}
		
		private void chooseUnitOrder(){
			ArrayList<Unit> units = model.getPlayerUnits();
			ArrayList<String> unitString = new ArrayList<String>();
			ArrayList<Unit> ordered = new ArrayList<Unit>();
			
			for(int i=0; i<units.size(); i++){
				unitString.add(units.get(i).getName());
			}
			
			while(ordered.size() <= 4){
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
			currentUnit = model.getCurrentUnit();
		}
	}
	
	private class ArrowsListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent ke) {
			Unit currentUnit = model.getCurrentUnit();
			
			int x = currentUnit.getxPos();
			int y = currentUnit.getyPos();

			// Arrow keys move the units
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
			// Pressing "enter" allows player unit to interact with the environment
			// Open chest, trade with player, just end their turn, etc.
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
	
	// Boolean flags to notify paintComponent when to update images for animations
	private boolean hitFlag = false;
	private boolean attackFlag = false;
	private Unit target;

	// Arrow key indicates in which direction to attack
	// Unit hits first enemy within attack range in that direction
	private class AttackListener implements KeyListener {
		public void keyPressed(KeyEvent ke) {
			attackFlag = true;
			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				if (model.attack(currentUnit, "l")) {
					hitFlag = true;
					target = model.getTarget(currentUnit, "l");
					JOptionPane.showMessageDialog(null,
							"You attacked an enemy!");
				} else {
					JOptionPane.showMessageDialog(null,
							"There was nothing to attack.");
				}
			} else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (model.attack(currentUnit, "r")) {
					hitFlag = true;
					target = model.getTarget(currentUnit, "r");
					JOptionPane.showMessageDialog(null,
							"You attacked an enemy!");
				} else {
					JOptionPane.showMessageDialog(null,
							"There was nothing to attack.");
				}
			} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
				if (model.attack(currentUnit, "u")) {
					hitFlag = true;
					target = model.getTarget(currentUnit, "u");
					JOptionPane.showMessageDialog(null,
							"You attacked an enemy!");
				} else {
					JOptionPane.showMessageDialog(null,
							"There was nothing to attack.");
				}
			} else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
				if (model.attack(currentUnit, "d")) {
					hitFlag = true;
					target = model.getTarget(currentUnit, "d");
					JOptionPane.showMessageDialog(null,
							"You attacked an enemy!");
				} else {
					JOptionPane.showMessageDialog(null,
							"There was nothing to attack.");
				}
			}
			hitFlag = false;
			attackFlag = false;
			display.removeKeyListener(al);
			display.addKeyListener(akl);
			model.switchTurns();
		}

		public void keyReleased(KeyEvent arg0) {
		}

		public void keyTyped(KeyEvent arg0) {
		}
	}
		
	// Similar to AttackListener, but for unit skills
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
	
	// Interaction listener for interacting with environment
	// Opening chests is the only available interaction
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
	
	// Allows units to trades with other player units next to them
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
	
	// Listener for the swing Timer that executes the AI
	private class AIAttackListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			model.executeEnemyAI();
		}
	}
	
	private static Object readFromFile(String filename) {
		try {
			FileInputStream f = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(f);
			Object o = in.readObject();
			in.close();
			return o;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	} 
	
	private static boolean saveToFile(Serializable s, String filename) {
		try {
			FileOutputStream f = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(f);
			out.writeObject(s);
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Updates the HUD that displays unit stats and instructions
	public void updateHUD() {
		if(currentUnit.isPlayerUnit()){		// Ensure player cannot access enemy inventory
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
		updateHUD();
		
		// Return to title screen if battle ends / is won
		if (model.isVictory()) {
			display.removeKeyListener(akl);
			
			JOptionPane.showMessageDialog(null, "You did it!");
			
			remove(menuBar);
			remove(display);
			remove(gameInfo);
			display = new TitlePanel();
			add(display,BorderLayout.CENTER);
			display.updateUI();

			try {
				player.terminate();
			} catch (IOException e) {
				System.out.println("Error in terminating audio.");
			}
			player = new AudioFilePlayer(baseDir + "Beep.mp3");
			player.start();
			return;
		}
		
		if (model.gameOver()) {
			JOptionPane.showMessageDialog(null, "Critical Mission Error");
			display.removeKeyListener(akl);
			remove(menuBar);
			remove(display);
			remove(gameInfo);
			display = new TitlePanel();
			add(display,BorderLayout.CENTER);
			display.updateUI();
			try {
				player.terminate();
			} catch (IOException e) {
				System.out.println("Error in terminating audio.");
			}
			player = new AudioFilePlayer(baseDir + "Beep.mp3");
			player.start();
			return;
		}
		
		// Executes the AI if it's the enemy's turn.
		if (!currentUnit.isPlayerUnit()){
			display.removeKeyListener(akl);
			attackTimer.start();
		} else {
			if (display.getKeyListeners().length < 1)
				display.addKeyListener(akl);
		}
	}
	
	// The title screen, simple enough
	private class TitlePanel extends AbstractPanel {

		private static final long serialVersionUID = 1898495348101864693L;
		private JLabel title;
		private JLabel credits;
		
		public TitlePanel() {
			super();
			setBackground(Color.BLACK);
			
			title = new JLabel("Beep Boop", JLabel.CENTER);
			title.setVerticalAlignment(JLabel.CENTER);
			title.setFont(new Font("Bauhaus 93", Font.PLAIN, 72));
			title.setBackground(Color.BLACK);
			title.setForeground(Color.WHITE);
			title.setPreferredSize(new Dimension(500,500));
			
			credits = new JLabel("Design: Zach Van Uum, Kegan Schaub, Terron Ishihara   |   Music: Terron Ishihara   |   Graphics: Kegan Schaub",JLabel.CENTER);
			credits.setVerticalAlignment(JLabel.CENTER);
			credits.setFont(new Font("Bauhaus 93", Font.PLAIN, 16));
			credits.setBackground(Color.BLACK);
			credits.setForeground(Color.WHITE);
			credits.setPreferredSize(new Dimension(1000,100));
			
			newGame = new JButton("New Game");
			//newGame.setPreferredSize(new Dimension(100,100));
			newGame.addActionListener(new NewGameListener());
			
			loadGame = new JButton("Load Game");
			//loadGame.setPreferredSize(new Dimension(100,100));
			loadGame.addActionListener(new LoadGameListener());
			
			this.setLayout(new BorderLayout());
			
			add(newGame,BorderLayout.WEST);
			add(title, BorderLayout.CENTER);
			add(loadGame,BorderLayout.EAST);
			add(credits, BorderLayout.SOUTH);
		}
		@Override
		public void update(Observable arg0, Object arg1) {
			//model = (RPGModel) arg0;
			revalidate();
			repaint();
		}
	}
	
	// Battle screen, uses the map from the model to play 
	// images where they need to be, and animates as needed
	private class BattlePanel extends AbstractPanel {
		
		private static final long serialVersionUID = 9053171202700651695L;
		
		private Map map;
		
		public BattlePanel(){
			setBackground(Color.BLACK);
			
			map = model.getMap();
		}

		public void paintComponent(Graphics g){
			super.paintComponents(g);
			
			Graphics2D gr = (Graphics2D) g;
			
			for(int i=0; i<map.getRows(); i++){
				for(int j=0; j<map.getColumns(); j++){
					// Multiply by 32 since each image is 32x32 pixels large
					int x=j*32;
					int y=i*32;
					Unit unit = map.getUnit(i,j);
				
					if (map.isInaccessible(i, j)) {
						gr.drawImage(reg, x, y, null);
					}
					else if (unit != null) {
						gr.drawImage(tile, x, y, this);
						if (unit.isPlayerUnit()){
							if (unit.isWarrior()){
								gr.drawImage(and, x, y, null);
							}
							else if (unit.isRanger())
								gr.drawImage(or, x, y, null);
							else if (unit.isTank())
								gr.drawImage(exor, x, y, null);
							else if (unit.isMage())
								gr.drawImage(shift2, x, y, null);
							else if (unit.isAssassin())
								gr.drawImage(not, x, y, null);			
						}
						else{
							if (unit.isWarrior())
								gr.drawImage(andEnemy, x, y, null);
							else if (unit.isRanger())
								gr.drawImage(orEnemy, x, y, null);
							else if (unit.isTank())
								gr.drawImage(exorEnemy, x, y, null);
							else if (unit.isMage())
								gr.drawImage(exorEnemy, x, y, null);
							else if (unit.isAssassin())
								gr.drawImage(notEnemy, x, y, null);
						}
						if (currentUnit.equals(unit) && attackFlag == true){
							gr.drawImage(attackImage, x, y, this);
							gr.finalize();
						}
						if (unit.equals(target) && hitFlag == true){
							gr.drawImage(hit, x, y, this);
							gr.finalize();
						}
						if (unit.isDead()){
							gr.drawImage(boom, x, y, this);
							gr.finalize();
						}
						if (unit.isDead() && currentUnit.equals(unit) && !model.isVictory()){
							model.switchTurns();
						}
						if (currentUnit.equals(unit)){		
							gr.setStroke(new BasicStroke(3));
							gr.setColor(Color.YELLOW);
							gr.draw(new Rectangle2D.Float(x, y, 30, 30));
						}
					}
					else if (map.getChest(i,j) != null) {
						gr.drawImage(tile, x, y, this);
						gr.drawImage(pc, x, y, null);
					}
					else{
						gr.drawImage(tile, x, y, this);
						gr.finalize();
					}
				}
			}
		}
		
		public boolean imageUpdate( Image img, int flags, int x, int y, 
			    int w, int h ) {
		    repaint();
		    return true;
		}

		public void update(Observable arg0, Object arg1) {
			//model = (RPGModel) arg0;
			revalidate();
			repaint();
		}
	}
	
	// Allows user to select between modes
	private class SelectionPanel extends AbstractPanel {

		private static final long serialVersionUID = 1898495348101864693L;
		private JLabel select;
		
		public SelectionPanel() {
			super();
			setBackground(Color.BLACK);
			
			select = new JLabel("Select Game Mode", JLabel.CENTER);
			select.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
			select.setVerticalAlignment(JLabel.CENTER);
			select.setBackground(Color.BLACK);
			select.setForeground(Color.WHITE);
			select.setPreferredSize(new Dimension(400,400));
			
			standardMode = new JButton("Standard Mode");
			standardMode.setFont(new Font("Bauhaus 93",Font.PLAIN,28));
			standardMode.setPreferredSize(new Dimension(100,100));
			standardMode.addActionListener(new SelectionListener());
			
			survivalMode = new JButton("Survival Mode");
			survivalMode.setFont(new Font("Bauhaus 93",Font.PLAIN,28));
			survivalMode.setPreferredSize(new Dimension(100,100));
			survivalMode.addActionListener(new SelectionListener());
			
			bossMode = new JButton("Boss Mode");
			bossMode.setFont(new Font("Bauhaus 93",Font.PLAIN,28));
			bossMode.setPreferredSize(new Dimension(100,100));
			bossMode.addActionListener(new SelectionListener());
			
			this.setLayout(new GridLayout(4,1));
			
			this.add(select);
			this.add(standardMode);
			this.add(survivalMode);
			this.add(bossMode);
		}
		@Override
		public void update(Observable arg0, Object arg1) {
			//model = (RPGModel) arg0;
			revalidate();
			repaint();
		}
	}
	
	private RPGImageView getGUI() {
		return this;
	}

}
