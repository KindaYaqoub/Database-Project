package application;

public class Address {

	private Integer addressId;
	private String street;
	private String city;

	public Address(Integer addressId, String street, String city) {
		this.addressId = addressId;
		this.street = street;
		this.city = city;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return street + ", " + city;
	}
}
