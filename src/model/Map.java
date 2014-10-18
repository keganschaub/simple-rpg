package model;

import java.io.Serializable;

/**
 * Object representing the map for the model to use.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class Map implements Serializable {

	private static final long serialVersionUID = 660569687814249238L;
	private Tile[][] grid;
	
	public Map(int level) {
		switch (level) {
		case 1:
			setUpLevel1();
			break;
		case 2:
			setUpLevel2();
			break;
		case 3:
			setUpLevel3();
			break;
		default:
		}
		
	}
	
	private void setUpLevel1() {
		grid = new Tile[24][24];
		
		for (int i=0; i<grid.length; i++) {
			for (int j=0; j<grid[0].length; j++) {
				grid[i][j] = new Tile();
			}
		}
		
		for (int i=0; i<grid.length; i++) {
			for (int j=0; j<4; j++) {
				grid[i][j].setInaccessible(true);
				grid[i][grid.length-1-j].setInaccessible(true);
			}
		}
	}
	
	private void setUpLevel2() {
		grid = new Tile[24][24];
		
		for (int i=0; i<grid.length; i++) {
			for (int j=0; j<grid[0].length; j++) {
				grid[i][j] = new Tile();
			}
		}
		
		for (int i=0; i<grid.length; i++) {
			if (i % 3 != 0){
				grid[i][grid[0].length/2].setInaccessible(true);
			}
		}
	}
	
	private void setUpLevel3() {
		grid = new Tile[24][24];
		
		for (int i=0; i<grid.length; i++) {
			for (int j=0; j<grid[0].length; j++) {
				grid[i][j] = new Tile();
			}
		}
		grid[grid.length/2-2][grid[0].length-1].setInaccessible(true);
		grid[grid.length/2-1][grid[0].length-1].setInaccessible(true);
		grid[grid.length/2-1][grid[0].length-2].setInaccessible(true);
		grid[grid.length/2+1][grid[0].length-2].setInaccessible(true);
		grid[grid.length/2+1][grid[0].length-1].setInaccessible(true);
		grid[grid.length/2+2][grid[0].length-1].setInaccessible(true);
	}
	
	public String toString() {
		String result = "";
		for (int i=0; i<grid.length; i++) {
			for (int j=0; j<grid[0].length; j++) {
				result += grid[i][j].toString();
			}
			result += '\n';
		}
		return result;
	}
	
	public void removeUnit(int x, int y) {
		grid[x][y].setUnit(null);
	}
	
	public boolean placeUnit(Unit unit, int x, int y) {
		if (grid[x][y].getUnit() == null &&
				grid[x][y].getChest() == null) {
			grid[x][y].setUnit(unit);
			unit.setxPos(x);
			unit.setyPos(y);
			return true;
		}
		return false;
	}
	
	// Getters and Setters
	public int getRows() {
		return grid.length;
	}
	
	public int getColumns() {
		return grid[0].length;
	}
	
	public boolean isInaccessible(int x, int y) {
		return grid[x][y].isInaccessible();
	}
	
	public Unit getUnit(int x, int y) {
		return grid[x][y].getUnit();
	}
	
	public Chest getChest(int x, int y) {
		return grid[x][y].getChest();
	}
	
	public void setChest(Chest chest, int x, int y) {
		grid[x][y].setChest(chest);
	}
	
	public void setUnit(Unit unit, int x, int y) {
		grid[x][y].setUnit(unit);
		unit.setxPos(x);
		unit.setyPos(y);
	}
	
	public void setInaccessible(boolean access, int x, int y) {
		grid[x][y].setInaccessible(access);
	}

}
