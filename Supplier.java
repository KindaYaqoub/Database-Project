package application;

public class Supplier {
	private int supplierId;
	private String name;
	private String contactInfo;
	private int address; // Change to Address type
	private String information;

	// Constructor accepting Address object
	public Supplier(int supplierId, String name, String contactInfo, int address, String information) {
		this.supplierId = supplierId;
		this.name = name;
		this.contactInfo = contactInfo;
		this.address = address; // Address object is passed here
		this.information = information;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public String getName() {
		return name;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public int getAddress() {
		return address; // Return Address object
	}

	public String getInformation() {
		return information;
	}
}