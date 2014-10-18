package model;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * 
 * Factory design for creating units. 5 in total:
 * Warrior, Mage, Tank, Assassin, Ranger. Each have
 * unique abilities and stats.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class UnitFactory implements Serializable{
	
	private static final long serialVersionUID = 4136806394178348480L;
	private ArrayList<Item> inv;
	private ItemFactory itemFactory;
	
	public UnitFactory() {
		inv = new ArrayList<Item>();
		itemFactory = new ItemFactory();
	}
	
	private class Warrior extends Unit implements Serializable {
		private static final long serialVersionUID = -2558946569267013821L;

		private Warrior() {
			setName("Warrior");
			setDescription("A unit with all-around good stats.");
			setMaxHealth(40);
			setHealth(40);
			setMaxMana(10);
			setMana(10);
			setAttack(25);
			setDefense(10);
			setMaxMovementDistance(5);
			setMovesLeft(5);
			inv.clear();
			inv.add(itemFactory.getPotion());
			setInventory(copyInv());
			setPlayerUnit(false);
			setDead(false);
			setAttackRange(1);
			setSkillName("Strike");
		}

		//Deals 10 damage to an enemy for 5 mana
		@Override
		public boolean useSkill(Unit enemy) {
			if(getMana() - 5 <= 0){
				JOptionPane.showMessageDialog(null, "Not enough mana for that skill.");
				return false;
			} else {
				if (enemy.getHealth() - 10 <= 0) {
					enemy.setHealth(0);
					enemy.setDead(true);
				} else
					enemy.setHealth(enemy.getHealth() - 10);
				
				setMana(getMana() - 5);
				System.out.println(enemy.getName() + "'s health: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
				return true;
			}
		}
	}
	
	private class Tank extends Unit implements Serializable {
		private static final long serialVersionUID = 4321303000381411883L;

		private Tank() {
			setName("Tank");
			setDescription("A defensive unit with low mobility.");
			setMaxHealth(40);
			setHealth(40);
			setMaxMana(10);
			setMana(10);
			setAttack(30);
			setDefense(15);
			setMaxMovementDistance(3);
			setMovesLeft(3);
			inv.clear();
			inv.add(itemFactory.getPotion());
			setInventory(copyInv());
			setPlayerUnit(false);
			setDead(false);
			setAttackRange(1);
			setSkillName("Heal");
		}
		
		//Heals 10 health for 5 mana
		@Override
		public boolean useSkill(Unit enemy) {
			if(getMana() - 5 < 0){
				JOptionPane.showMessageDialog(null, "Not enough mana for that skill.");
				return false;
			} else {
				if (getHealth() == getMaxHealth()){
					JOptionPane.showMessageDialog(null, "This unit's health is already maximum.");
				} else{
					if(getHealth() + 10 > getMaxHealth())
						setHealth(getMaxHealth());
					else
						setHealth(getHealth() + 10);
				}
				
				setMana(getMana() - 5);
				return true;
			}
		}
	}
	
	private class Ranger extends Unit implements Serializable {
		private static final long serialVersionUID = -4685097476177149659L;

		private Ranger() {
			setName("Ranger");
			setDescription("A ranged attacker with low defenses.");
			setMaxHealth(30);
			setHealth(30);
			setMaxMana(12);
			setMana(12);
			setAttack(30);
			setDefense(10);
			setMaxMovementDistance(5);
			setMovesLeft(5);
			inv.clear();
			inv.add(itemFactory.getPotion());
			setInventory(copyInv());
			setPlayerUnit(false);
			setDead(false);
			setAttackRange(3);
			setSkillName("Flaming arrow");
		}
		
		//Deal 10 damage to the enemy for 4 mana
		@Override
		public boolean useSkill(Unit enemy) {
			if(getMana() - 4 < 0){
				JOptionPane.showMessageDialog(null, "Not enough mana for that skill.");
				return false;
			} else {
				if (enemy.getHealth() - 10 <= 0) {
					enemy.setHealth(0);
					enemy.setDead(true);
				} else
					enemy.setHealth(enemy.getHealth() - 10);
				
				setMana(getMana() - 4);
				System.out.println(enemy.getName() + "'s health: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
				return true;
			}
		}
	}
	
	private class Mage extends Unit implements Serializable {
		private static final long serialVersionUID = -2680815083562038159L;

		private Mage() {
			setName("Mage");
			setDescription("A powerful attacker with low mobility and defense.");
			setMaxHealth(30);
			setHealth(30);
			setMaxMana(20);
			setMana(20);
			setAttack(20);
			setDefense(5);
			setMaxMovementDistance(3);
			setMovesLeft(3);
			inv.clear();
			inv.add(itemFactory.getEther());
			setInventory(copyInv());
			setPlayerUnit(false);
			setDead(false);
			setAttackRange(3);
			setSkillName("Fireball");
		}

		//Deal 10 damage to the enemy for 5 mana
		@Override
		public boolean useSkill(Unit enemy) {
			if(getMana() - 5 < 0){
				JOptionPane.showMessageDialog(null, "Not enough mana for that skill.");
				return false;
			} else {
				if (enemy.getHealth() - 10 <= 0) {
					enemy.setHealth(0);
					enemy.setDead(true);
				} else
					enemy.setHealth(enemy.getHealth() - 10);
				
				setMana(getMana() - 5);
				System.out.println(enemy.getName() + "'s health: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
				return true;
			}
		}
	}
	
	private class Assassin extends Unit implements Serializable {
		private static final long serialVersionUID = 7864216385488608058L;

		private Assassin() {
			setName("Assassin");
			setDescription("A dangerous but fragile unit with high mobility.");
			setMaxHealth(30);
			setHealth(30);
			setMaxMana(5);
			setMana(5);
			setAttack(40);
			setDefense(12);
			setMaxMovementDistance(8);
			setMovesLeft(8);
			inv.clear();
			inv.add(itemFactory.getPotion());
			setInventory(copyInv());
			setPlayerUnit(false);
			setDead(false);
			setAttackRange(3);
			setSkillName("Sneak attack");
		}

		//Deal 15 damage to the enemy for 5 mana
		@Override
		public boolean useSkill(Unit enemy) {
			if(getMana() - 5 < 0){
				JOptionPane.showMessageDialog(null, "Not enough mana for that skill.");
				return false;
			} else {
				if (enemy.getHealth() - 15 <= 0) {
					enemy.setHealth(0);
					enemy.setDead(true);
				} else {
					enemy.setHealth(enemy.getHealth() - 15);
					setMana(getMana() - 5);
				}
				System.out.println(enemy.getName() + "'s health: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
				return true;
			}
		}
	}
	
	public Warrior getWarrior() {
		return new Warrior();
	}
	
	public Tank getTank() {
		return new Tank();
	}
	
	public Ranger getRanger() {
		return new Ranger();
	}
	
	public Mage getMage() {
		return new Mage();
	}
	
	public Assassin getAssassin() {
		return new Assassin();
	}
	
	private ArrayList<Item> copyInv() {
		ArrayList<Item> copy = new ArrayList<Item>();
		copy.addAll(inv);
		return copy;
	}
	
}
