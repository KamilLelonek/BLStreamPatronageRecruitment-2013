package first.task.android;

import java.io.Serializable;

/**
 * Simple class to represent item with its name and boolean value
 */
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean checked;
	
	public Item(String name) {
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	/* if (checked == true) checked = false; else checked = true; */
	public void switchChecked() {
		this.checked ^= true;
	}
	
	@Override public String toString() {
		return getName();
	}
	
	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof Item)) return false;
		return this.name.equals(((Item) o).name);
	}
}