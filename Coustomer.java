package application;

public class Coustomer {
	private int customerId;
	private String customerName;
	private int addressId;
	private String phone;

	// Constructor
	public Coustomer(int customerId, String customerName, int addressId, String phone) {
		this.customerId = customerId;
		this.customerName = customerName;
		this.addressId = addressId;
		this.phone = phone;
	}

	// Getters and Setters
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
	    return "customerId=" + customerId + ", Name=" + customerName + ", addressId=" + addressId;
	}

}