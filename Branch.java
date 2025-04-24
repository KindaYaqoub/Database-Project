package application;

public class Branch {
	private int branchId;
	private String phoneNumber;
	private String openingHours;
	private int addressId;

	public Branch(int branchId, String phoneNumber, String openingHours, int addressId) {
		this.branchId = branchId;
		this.phoneNumber = phoneNumber;
		this.openingHours = openingHours;
		this.addressId = addressId;
	}

	public int getBranchId() {
		return branchId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public int getAddressId() {
		return addressId;
	}

	@Override
	public String toString() {
		return "Branch ID: " + branchId + " , Address ID: " +addressId;
	}
}