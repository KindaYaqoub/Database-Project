package application;

public class MenuItem {
    private int id;
    private String size;  // إضافة size
    private String name;
    private String type;
    private double price;
    private String description;

    // تحديث الـ constructor ليشمل size
    public MenuItem(int id, String size, String name, String type, double price, String description) {
        this.id = id;
        this.size = size;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
