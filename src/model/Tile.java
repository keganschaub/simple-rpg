package model;

import java.io.Serializable;

/**
 * Represents one tile on the map. Can only contain one 
 * unit exor one chest, or is inaccessible
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class Tile implements Serializable {

	private static final long serialVersionUID = -4250187646424954018L;
	private Unit unit;
	private Chest chest;
	private boolean inaccessible;
	
	public Tile() {
		inaccessible = false;
	}
	
	public Tile(Unit unit) {
		if (chest == null)
			this.unit = unit;
		inaccessible = false;
	}
	
	public Tile(Chest chest) {
		if (unit == null)
			this.chest = chest;
		inaccessible = false;
	}
	
	public String toString() {
		if (unit != null && unit.isDead())
			unit = null;
		if (unit != null && unit.isPlayerUnit())
			return "P";
		if (unit != null && !unit.isPlayerUnit())
			return "U";
		if (chest != null)
			return "C";
		if (inaccessible)
			return "#";
		return "-";
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		if (chest == null && !inaccessible)
			this.unit = unit;
	}

	public Chest getChest() {
		return chest;
	}

	public void setChest(Chest chest) {
		if (unit == null && !inaccessible)
			this.chest = chest;
	}

	public boolean isInaccessible() {
		return inaccessible;
	}

	public void setInaccessible(boolean inaccessible) {
		this.inaccessible = inaccessible;
	}

}
