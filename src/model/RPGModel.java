package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Coordinates all actions on the map
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class RPGModel extends Observable implements Serializable {

	private static final long serialVersionUID = -7435775674590185049L;
	private Map map;
	private ArrayList<Item> allItems;
	private ArrayList<Unit> playerUnits;
	private ArrayList<Unit> enemyUnits;
	private Unit currentUnit;
	private UnitFactory unitFactory;
	private String gameMode;

	// Constructs appropriate map for each mode
	public RPGModel(String gameMode) {
		unitFactory = new UnitFactory();
		
		if (gameMode.equals("STANDARD")) {
			this.gameMode = "STANDARD";
			setUpStandardBattle();
		}
		else if (gameMode.equals("SURVIVAL")) {
			this.gameMode = "SURVIVAL";
			setUpSurvivalBattle();
		} else if (gameMode.equals("BOSS")) {
			this.gameMode = "BOSS";
			setUpBossBattle();
		}
	}
	
	private void setUpStandardBattle() {
		// Default map
		map = new Map(1);
		
		possibleUnits();
		
		// Default possible items
		allItems = new ArrayList<Item>();
		ItemFactory itemFactory = new ItemFactory();
		allItems.add(itemFactory.getPotion());	
		allItems.add(itemFactory.getEther());
		allItems.add(itemFactory.getElixir());
			
		// Default player/enemy units
		placeUnits();
				
		// Default starting unit
		currentUnit = playerUnits.get(0);
				
		// Default chest placement
		map.setChest(new Chest(allItems), map.getRows()/2, map.getColumns()/2);
		map.setChest(new Chest(allItems), map.getRows()/2+1, map.getColumns()/2+1);
	}
	
	private void setUpSurvivalBattle() {
		// Default map
		map = new Map(2);
		
		possibleUnits();
		
		// Default possible items
		allItems = new ArrayList<Item>();
		ItemFactory itemFactory = new ItemFactory();
		allItems.add(itemFactory.getPotion());	
		allItems.add(itemFactory.getEther());
		allItems.add(itemFactory.getElixir());
			
		// Default player/enemy units
		placeUnits();
				
		// Default starting unit
		currentUnit = playerUnits.get(0);
		
		// Default chest placement
		map.setChest(new Chest(allItems), map.getRows()/2, map.getColumns()/2);
		map.setChest(new Chest(allItems), map.getRows()/2+1, map.getColumns()/2+1);
	}
	
	private void setUpBossBattle() {
		// Default map
		map = new Map(3);
		
		possibleUnits();
		
		// Default possible items
		allItems = new ArrayList<Item>();
		ItemFactory itemFactory = new ItemFactory();
		allItems.add(itemFactory.getPotion());	
		allItems.add(itemFactory.getEther());
		allItems.add(itemFactory.getElixir());
			
		// Default player/enemy units
		placeUnits();
				
		// Default starting unit
		currentUnit = playerUnits.get(0);
		
		// Default chest placement
		map.setChest(new Chest(allItems), 5, map.getColumns()/2);
		map.setChest(new Chest(allItems), map.getRows()/2-2, map.getColumns()-2);
		map.setChest(new Chest(allItems), map.getRows()/2+2, map.getColumns()-2);
		map.setChest(new Chest(allItems), map.getRows()-5, map.getColumns()/2);;
	}
	
	// Creates initial list that contains one of each type of unit
	// from which the user can choose
	private void possibleUnits(){
		playerUnits = new ArrayList<Unit>();
		enemyUnits = new ArrayList<Unit>();
		
		playerUnits.add(unitFactory.getWarrior());
		playerUnits.add(unitFactory.getTank());
		playerUnits.add(unitFactory.getRanger());
		playerUnits.add(unitFactory.getMage());
		playerUnits.add(unitFactory.getAssassin());
		
		if (gameMode.equals("STANDARD")) {
			enemyUnits.add(unitFactory.getWarrior());
			enemyUnits.add(unitFactory.getWarrior());
			enemyUnits.add(unitFactory.getWarrior());
			enemyUnits.add(unitFactory.getMage());
			enemyUnits.add(unitFactory.getRanger());
			for (int i=0; i<enemyUnits.size(); i++) {
				enemyUnits.get(i).setMaxHealth(10);
				enemyUnits.get(i).setHealth(10);
				enemyUnits.get(i).setAttack(40);
				enemyUnits.get(i).setDefense(5);
			}
		} else if (gameMode.equals("SURVIVAL")) {
			for (int i=0; i<15; i++) {
				enemyUnits.add(unitFactory.getAssassin());
				enemyUnits.get(i).setMaxHealth(5);
				enemyUnits.get(i).setHealth(5);
				enemyUnits.get(i).setAttack(40);
				enemyUnits.get(i).setDefense(5);
			}
		} else if (gameMode.equals("BOSS")) {
			enemyUnits.add(unitFactory.getTank());
			enemyUnits.get(0).setMaxHealth(100);
			enemyUnits.get(0).setHealth(100);
			enemyUnits.get(0).setAttack(100);
			enemyUnits.get(0).setDefense(10);
		}
	}
	
	// Places the player units depending on mode
	public void placeUnits(){
		playerUnits.get(0).setPlayerUnit(true);
		playerUnits.get(1).setPlayerUnit(true);
		playerUnits.get(2).setPlayerUnit(true);
		playerUnits.get(3).setPlayerUnit(true);
		playerUnits.get(4).setPlayerUnit(true);
		
		if (gameMode.equals("STANDARD")) {
			map.setUnit(playerUnits.get(0), 3, 5);
			map.setUnit(playerUnits.get(1), 6, 5);
			map.setUnit(playerUnits.get(2), 9, 5);
			map.setUnit(playerUnits.get(3), 12, 5);
			map.setUnit(playerUnits.get(4), 15, 5);
			map.setUnit(enemyUnits.get(0), 3, 18);
			map.setUnit(enemyUnits.get(1), 6, 18);
			map.setUnit(enemyUnits.get(2), 9, 18);
			map.setUnit(enemyUnits.get(3), 12, 18);
			map.setUnit(enemyUnits.get(4), 15, 18);
		}
		else if (gameMode.equals("SURVIVAL")) {
			for (int i=0; i<playerUnits.size(); i++) {
				map.setUnit(playerUnits.get(i), i+map.getRows()/2, 0);
			}
			for (int i=0; i<enemyUnits.size(); i++) {
				map.setUnit(enemyUnits.get(i), i+5, map.getColumns()-1);
			}
		} else if (gameMode.equals("BOSS")) {
			for (int i=0; i<playerUnits.size(); i++) {
				map.setUnit(playerUnits.get(i), i+map.getRows()/2, 0);
			}
			map.setUnit(enemyUnits.get(0), map.getRows()/2, map.getColumns()-1);
		}
		currentUnit = playerUnits.get(0);
	}
	
	// Finalizes player units once user selects them
	public void setPlayerUnits(ArrayList<Unit> playerUnits){
		this.playerUnits.clear();
		
		for (Unit u : playerUnits) {
			if (u.getName().equals("Warrior"))
				this.playerUnits.add(unitFactory.getWarrior());
			else if (u.getName().equals("Tank"))
				this.playerUnits.add(unitFactory.getTank());
			else if (u.getName().equals("Ranger"))
				this.playerUnits.add(unitFactory.getRanger());
			else if (u.getName().equals("Mage"))
				this.playerUnits.add(unitFactory.getMage());
			else if (u.getName().equals("Assassin"))
				this.playerUnits.add(unitFactory.getAssassin());
		}
		
	}
	
	public ArrayList<Unit> getPlayerUnits() {
		return playerUnits;
	}
	
	public String toString() {
		return map.toString();
	}
	
	public Unit getCurrentUnit() {
		return currentUnit;
	}
	
	public String getGameMode() {
		return gameMode;
	}
	
	public Map getMap() {
		return map;
	}
	
	// Moves each unit; one execution of this method moves
	// the unit one space in the indicated direction
	// Cannot move into occupied or inaccessible tiles
	public void move(Unit unit, int x, int y) {
		if (x >= 0 && x <= map.getRows() && y >= 0 && y <= map.getColumns()) {
			if (!map.isInaccessible(x, y) && 
					map.getUnit(x, y) == null && 
					map.getChest(x, y) == null) {
				int previousX = unit.getxPos();
				int previousY = unit.getyPos();
				
				if (map.placeUnit(unit, x, y)) {
					map.removeUnit(previousX, previousY);

					unit.setMovesLeft(unit.getMovesLeft() - 1);

					if (unit.getMovesLeft() <= 0) {
						unit.setMovesLeft(unit.getMaxMovementDistance());
						switchTurns();
					}
				}
			} else {
				if (!unit.isPlayerUnit()) {
					Unit target = map.getUnit(x,y);
					if (target != null &&
							target.isPlayerUnit()) {
						attack(unit,x,y);
						switchTurns();
					}
					else
						switchTurns();
				} 
			}
			setChanged();
			notifyObservers();
		}
	}
	
	// Changes the current unit to next to unit's turn
	public void switchTurns() {
		currentUnit.setMovesLeft(currentUnit.getMaxMovementDistance());
		if (playerUnits.contains(currentUnit)) {
			if (playerUnits.indexOf(currentUnit)+1 < playerUnits.size())
				currentUnit = playerUnits.get(playerUnits.indexOf(currentUnit)+1);
			else
				currentUnit = enemyUnits.get(0);
		} else {
			if (enemyUnits.indexOf(currentUnit)+1 < enemyUnits.size())
				currentUnit = enemyUnits.get(enemyUnits.indexOf(currentUnit)+1);
			else
				currentUnit = playerUnits.get(0);
		}
		setChanged();
		notifyObservers();
	}
	
	// Executes the specified unit's attack on the specified target's coordinates
	public boolean attack(Unit attacker, int targetX, int targetY) {
		int attackerX = attacker.getxPos();
		int attackerY = attacker.getyPos();
		Unit target = map.getUnit(targetX,targetY);
		if (target != null &&
				Math.abs(attackerX-targetX) <= attacker.getAttackRange() &&
				Math.abs(attackerY-targetY) <= attacker.getAttackRange()) {
			attacker.attack(target);
			attacker.setMovesLeft(attacker.getMaxMovementDistance());
			System.out.println(currentUnit.getName() + "'s health: " + target.getHealth() + "/" + target.getMaxHealth());
			return true;
		}
		return false;
	}
	
	// Executes the specified unit's attack in a certain direction,
	// within the unit's attack range
	public boolean attack(Unit attacker, String direction) {
		Unit target = getTarget(attacker, direction);
		if (target != null) {
			attacker.attack(target);
			attacker.setMovesLeft(attacker.getMaxMovementDistance());
			System.out.println(currentUnit.getName() + "'s health: " + target.getHealth() + "/" + target.getMaxHealth());
			return true;
		}
		return false;
	}
	
	// A helper method for attack() that calculates the first unit in the attack path, determined by the attacker's attack range
	public Unit getTarget(Unit attacker, String direction){
		Unit target = null;
		int range = attacker.getAttackRange();
		int attackerX = attacker.getxPos();
		int attackerY = attacker.getyPos();
		
		// Check in specified direction. If the range check goes outside of the bounds, break out of the loop
		// If a unit is found, set target to be that unit and break out of the loop
		if(direction.equals("l")){
			if(attackerY-range < 0){
				for(int i=0; i<attackerY; i++){
					if(map.getUnit(attackerX, i) != null
							&& !map.getUnit(attackerX, i).isPlayerUnit()){
						target = map.getUnit(attackerX, i);
						break;
					}
				}
			} else {
				for(int i=attackerY-range; i<attackerY; i++){
					if(map.getUnit(attackerX, i) != null
							&& !map.getUnit(attackerX, i).isPlayerUnit()){
						target = map.getUnit(attackerX, i);
						break;
					}
				}
			}
		} else if (direction.equals("r")){
			if(attackerY+range > map.getColumns()){
				for(int i=map.getColumns(); i>attackerY; i--){
					if(map.getUnit(attackerX, i) != null
							&& !map.getUnit(attackerX, i).isPlayerUnit()){
						target = map.getUnit(attackerX, i);
						break;
					}
				}
			} else {
				for(int i=attackerY+range; i>attackerY; i--){
					if(map.getUnit(attackerX, i) != null
							&& !map.getUnit(attackerX, i).isPlayerUnit()){
						target = map.getUnit(attackerX, i);
						break;
					}
				}
			}
		} else if (direction.equals("u")){
			if(attackerX-range < 0){
				for(int i=0; i<attackerX; i++){
					if(map.getUnit(i, attackerY) != null
							 && !map.getUnit(i, attackerY).isPlayerUnit()){
						target = map.getUnit(i, attackerY);
						break;
					}
				}
			} else {
				for(int i=attackerX-range; i<attackerX; i++){
					if(map.getUnit(i, attackerY) != null
							 && !map.getUnit(i, attackerY).isPlayerUnit()){
						target = map.getUnit(i, attackerY);
						break;
					}
				}
			}
		} else if (direction.equals("d")){
			if(attackerX+range > map.getRows()){
				for(int i=map.getRows(); i>attackerX; i--){
					if(map.getUnit(i, attackerY) != null
							 && !map.getUnit(i, attackerY).isPlayerUnit()){
						target = map.getUnit(i, attackerY);
						break;
					}
				}
			} else {
				
				for(int i=attackerX+range; i>attackerX; i--){
					if(map.getUnit(i, attackerY) != null
							 && !map.getUnit(i, attackerY).isPlayerUnit()){
						target = map.getUnit(i, attackerY);
						break;
					}
				}
			}
		}
		return target;
	}
	
	public boolean openChest(Unit unit, int chestX, int chestY) {
		int unitX = unit.getxPos();
		int unitY = unit.getyPos();
		Chest chest = map.getChest(chestX,chestY);
		if (chest != null &&
				!chest.isOpened() &&
				Math.abs(unitX-chestX) <= 1 && 
				Math.abs(unitY-chestY) <= 1) {
			unit.openChest(chest);
			map.setChest(null, chestX, chestY);
			return true;
		}
		return false;
	}
	
	// Helper method for the view to execute trading
	public Unit unitAvailableToTrade(int unitX, int unitY) {
		Unit unit = map.getUnit(unitX,unitY);
		if (unit != null &&
				unit.isPlayerUnit() &&
				!unit.getInventory().isEmpty()) {
			return unit;
		}
		return null;
	}
	
	// Executes the trade
	public boolean tradeItem(Unit unit, Unit otherUnit, Item unitItem, Item otherItem) {
		if (unit != null && otherUnit != null &&
				unit.isPlayerUnit() && otherUnit.isPlayerUnit()) {
			unit.giveItem(unitItem,otherUnit);
			otherUnit.giveItem(otherItem, unit);
			return true;
		}
		return false;
	}
	
	// If all enemy units have health = 0, then it is game over and player wins
	// End starts as true, if it remains true after all the checks, then the game is over
	public boolean isVictory(){
		// Check to see if all enemy units have health = 0
		// If there is an enemy unit whose health != 0, the game isn't over
		for(int i=0; i<enemyUnits.size(); i++){
			if(enemyUnits.get(i).getHealth() == 0){
				map.removeUnit(enemyUnits.get(i).getxPos(), enemyUnits.get(i).getyPos());
			}
			else 
				return false;
		}
		
		return true;
	}
		
	
	// If all player units have health = 0, then it is game over and player wins
	// End starts as true, if it remains true after all the checks, then the game is over
	public boolean gameOver(){
		// Check to see if all player units have health = 0
		// If there is an player unit whose health != 0, the game isn't over
		for(int i=0; i<playerUnits.size(); i++){
			if(playerUnits.get(i).getHealth() != 0){
				return false;
			}
		}
		return true;
	}
	
	// Executes the AI
	// Enemies move toward the closest unit and attack once
	// they've reached the closest unit. Not the smartest AI;
	// also stop once they've hit a chest (they don't move
	// around chests unless the player unit placement facilitates it)
	public void executeEnemyAI() {	
		Unit closestUnit = findClosestPlayerUnit();
		
		int x = currentUnit.getxPos();
		int y = currentUnit.getyPos();
		int playerX = closestUnit.getxPos();
		int playerY = closestUnit.getyPos();
		
		if (Math.abs(x-playerX) >= Math.abs(y-playerY)) {
			if (x-playerX <= 0)
				move(currentUnit, x+1, y);
			else
				move(currentUnit, x-1, y);
		}
		else {
			if (y-playerY <= 0)
				move(currentUnit, x, y+1);
			else
				move(currentUnit, x, y-1);
		}
	}
	
	private Unit findClosestPlayerUnit() {
		int x = currentUnit.getxPos();
		int y = currentUnit.getyPos();
		int closestDistance = map.getRows();
		Unit closestUnit = null;
		
		for (int i=0; i<map.getRows(); i++) {
			for (int j=0; j<map.getColumns(); j++) {
				if (map.getUnit(i,j) != null &&
						map.getUnit(i,j).isPlayerUnit()) {
					if (Math.abs(map.getUnit(i,j).getxPos() - x) < closestDistance ) {
						closestDistance = Math.abs(map.getUnit(i,j).getxPos() - x);
						closestUnit = map.getUnit(i,j);
					}
					if (Math.abs(map.getUnit(i,j).getyPos() - y) < closestDistance) {
						closestDistance = Math.abs(map.getUnit(i,j).getyPos() - y);
						closestUnit = map.getUnit(i,j);
					}
				}
			}
		}
		return closestUnit;
	}
	
	

}
