package model;

import java.io.Serializable;

/**
 * Represents one item, containing a name and description,
 * and a generate performAction method.
 * 
 * @author Terron Ishihara, Zachary Van Uum, Kegan Schaub
 *
 */
public abstract class Item implements Serializable {

	private static final long serialVersionUID = 3375838500131426137L;
	private String name;
	private String description;
	
	public Item(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public abstract void performAction(Unit unit);

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
	
	public String toString() {
		return name;
	}

}
