package model;

import java.io.Serializable;

/**
 * Factory design for creating items. 3 in total:
 * Potion, Ether, Elixir. Each execute a distinct action,
 * all dealing with unit stats.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class ItemFactory implements Serializable {

	private static final long serialVersionUID = -339312812730188054L;

	public ItemFactory() {
	}
	
	private class Potion extends Item {
		private static final long serialVersionUID = -309221339542717259L;
		private Potion(String name, String description) {
			super(name, description);
		}

		@Override
		public void performAction(Unit unit) {
			int addAmount = 10;
			if (unit.getHealth() + addAmount > unit.getMaxHealth())
				unit.setHealth(unit.getMaxHealth());
			else
				unit.setHealth(unit.getHealth() + addAmount);
		}
	}
	
	private class Ether extends Item {
		private static final long serialVersionUID = 6004985162648341391L;
		private Ether(String name, String description) {
			super(name, description);
		}

		@Override
		public void performAction(Unit unit) {
			int addAmount = 5;
			if (unit.getMana() + addAmount > unit.getMaxMana())
				unit.setMana(unit.getMaxMana());
			else
				unit.setMana(unit.getMana() + addAmount);
		}
	}
	
	private class Elixir extends Item {
		private static final long serialVersionUID = 2404755246337253409L;

		private Elixir(String name, String description) {
			super(name, description);
		}

		@Override
		public void performAction(Unit unit) {
			unit.setAttack(unit.getAttack()*2);
		}
	}
	
	public Potion getPotion() {
		return new Potion("Potion", "Heals unit by 10 points.");
	}
	
	public Ether getEther() {
		return new Ether("Ether", "Increases mana by 5.");
	}
	
	public Elixir getElixir() {
		return new Elixir("Elixir", "Permanently doubles attack.");
	}
	
}
