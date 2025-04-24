package application;

public class Delivery {
	private int deliveryId;
	private String deliveryTime;
	private int customerId;
	private String status;

	// Constructor
	public Delivery(int deliveryId, String deliveryTime, int customerId, String status) {
		this.deliveryId = deliveryId;
		this.deliveryTime = deliveryTime;
		this.customerId = customerId;
		this.status = status;
	}

	// Getters and setters
	public int getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(int deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
	    return "deliveryId=" + deliveryId + ", customerId=" + customerId + ", status='" + status + "'";
	}

}