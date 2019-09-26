package data.map.resources;

public class Resource {

	int quantity;
	ResourceType type;

	public Resource(int quantity, ResourceType type) {
		this.quantity = quantity;
		this.type = type;
	}

	// =========================================================================================================================

	public int take(int x) {
		if (x > quantity)
			x = quantity;

		quantity -= x;

		return x;
	}

	public void add(Resource res) {
		if (type == null)
			type = res.type;
		quantity += res.quantity;
	}

	// =========================================================================================================================

	public boolean isEmpty() {
		return quantity == 0;
	}

	public int getQuantity() {
		return quantity;
	}

	public ResourceType getType() {
		return type;
	}
}
