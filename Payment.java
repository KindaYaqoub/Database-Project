package application;

public class Payment {

	private int paymentId;
	private String type;
	private String details;

	// Constructor
	public Payment(int paymentId, String type, String details) {
		this.paymentId = paymentId;
		this.type = type;
		this.details = details;
	}

	// Constructor
	public Payment(String type, String details) {
		this.type = type;
		this.details = details;
	}

	// Getter and Setter methods
	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
	    return "paymentId=" + paymentId + ", type=" + type;
	}

}
