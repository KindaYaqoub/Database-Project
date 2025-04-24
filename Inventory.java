package application;

public class Inventory {
    private int inventoryItemId;
    private int quantity;
    private String name;

    public Inventory(int inventoryItemId, int quantity, String name) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
        this.name = name;
    }

    public int getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(int inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
