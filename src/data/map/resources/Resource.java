package data.map.resources;

import java.io.Serializable;

public class Resource implements Serializable {
	private static final long serialVersionUID = -8554005042263955323L;

	public final int EVERYTHING = -1;

	private ResourceType type;
	private int max;
	private int quantity;

	// =========================================================================================================================

	public Resource(ResourceType type, int quantity, int max) {
		this.type = type;
		this.quantity = quantity;
		this.max = max;
	}

	public Resource(ResourceType type, int quantity) {
		this(type, quantity, quantity);
	}

	public Resource() {
		this(null, 0, 0);
	}

	// =========================================================================================================================
	// Add / Remove

	/** Removes the available quantity from this Resource and returns it */
	public int remove(int x) {
		if (x == EVERYTHING || x > quantity)
			x = quantity;

		quantity -= x;

		return x;
	}

	/**
	 * Adds quantity to the stock
	 *
	 * @return false if Resource is full
	 */
	public boolean add(int x) {
		if ((quantity += x) > max)// Overflow
			quantity = max;
		return isFull();
	}

	/**
	 * Take the quantity from the other Resource (if possible)
	 * 
	 * @return false if Resource is full
	 */
	public boolean addFrom(Resource res, int quantity) {
		if (type != res.type)// Impossible
			return false;

		return add(res.remove(quantity));
	}

	/**
	 * Take the maximum possible from the other Resource
	 * 
	 * @return false if Resource is full
	 */
	public boolean addFrom(Resource res) {
		return addFrom(res, getSpaceLeft());
	}

	// =========================================================================================================================

	public void regroup(Resource res) {
		if (type == null)
			type = res.type;
		quantity += res.quantity;
		max = quantity;
	}

	// =========================================================================================================================
	// Getters

	public int getSpaceLeft() {
		return max - quantity;
	}

	public boolean isFull() {
		return quantity == max;
	}

	public boolean isEmpty() {
		return quantity == 0;
	}

	public int getQuantity() {
		return quantity;
	}

	public ResourceType getType() {
		return type;
	}

	public int getMax() {
		return max;
	}

	// =========================================================================================================================

	@Override
	public String toString() {
		return "Resource [type=" + type + ", max=" + max + ", quantity=" + quantity + "]";
	}
}
