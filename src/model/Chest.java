package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents one chest which may be place on the field.
 * Generates one random item to give to a unit that opens it.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public class Chest implements Serializable {

	private static final long serialVersionUID = 895054340323884650L;
	private Random gen;
	private Item item;
	private ArrayList<Item> allItems;
	private boolean opened;
	
	public Chest() {
		item = null;
		allItems = new ArrayList<Item>();
		gen = new Random();
		opened = false;
	}
	
	public Chest(ArrayList<Item> allItems) {
		this.allItems = new ArrayList<Item>();
		this.allItems.addAll(allItems);
		gen = new Random();
		int index = gen.nextInt(allItems.size());
		item = allItems.get(index);
	}
	
	public Item getItem() {
		if (!opened) {
			opened = true;
			return item;
		}
		return null;
	}
	
	public boolean isOpened() {
		return opened;
	}
	
	public void setAllItems(ArrayList<Item> allItems) {
		this.allItems.clear();
		this.allItems.addAll(allItems);
		item = allItems.get(gen.nextInt(allItems.size()));
	}

}
