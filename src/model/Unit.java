package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a unit with all of its stats and
 * attack, useSkill, trade, and useItem methods.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public abstract class Unit implements Serializable {

	private static final long serialVersionUID = 3246251279301377127L;
	private String name;
	private String description;
	private String skillName;
	private int maxHealth;
	private int health;
	private int maxMana;
	private int mana;
	private int attack;
	private int defense;
	private int maxMovementDistance;
	private int movesLeft;
	private ArrayList<Item> inventory;
	private int xPos;
	private int yPos;
	private boolean playerUnit;
	private boolean isDead;
	private int attackRange;
	
	public Unit() {
		
	}
	
	public void attack(Unit enemy) {
		if (enemy.getHealth() - (int)(this.getAttack()/enemy.getDefense()) <= 0) {
			enemy.setHealth(0);
			enemy.setDead(true);
		}
		else
			enemy.setHealth(enemy.getHealth() - (int)(this.getAttack()/enemy.getDefense()));
	}
	
	public void useItem(Item item) {
		item.performAction(this);
		inventory.remove(inventory.indexOf(item));
	}
	
	public void openChest(Chest chest) {
		receiveItem(chest.getItem());
	}
	
	public void giveItem(Item item, Unit unit) {
		unit.receiveItem(item);
		inventory.remove(inventory.indexOf(item));
	}
	
	public void receiveItem(Item item) {
		inventory.add(item);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}
	
	public String getSkillName() {
		return skillName;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getMaxMovementDistance() {
		return maxMovementDistance;
	}

	protected void setMaxMovementDistance(int maxMovementDistance) {
		this.maxMovementDistance = maxMovementDistance;
	}

	public int getMovesLeft() {
		return movesLeft;
	}

	public void setMovesLeft(int movesLeft) {
		this.movesLeft = movesLeft;
	}

	public ArrayList<Item> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<Item> inventory) {
		this.inventory = new ArrayList<Item>();
		this.inventory.addAll(inventory);
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public boolean isPlayerUnit() {
		return playerUnit;
	}

	public void setPlayerUnit(boolean playerUnit) {
		this.playerUnit = playerUnit;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}
	
	public boolean isWarrior(){
		if (name.equalsIgnoreCase("warrior")){
			return true;
		}
		return false;
	}
	
	public boolean isRanger(){
		if (name.equalsIgnoreCase("ranger")){
			return true;
		}
		return false;
	}
	
	public boolean isMage(){
		if (name.equalsIgnoreCase("mage")){
			return true;
		}
		return false;
	}
	
	public boolean isTank(){
		if (name.equalsIgnoreCase("tank")){
			return true;
		}
		return false;
	}
	
	public boolean isAssassin(){
		if (name.equalsIgnoreCase("assassin")){
			return true;
		}
		return false;
	}
	
	public abstract boolean useSkill(Unit enemy);

}
