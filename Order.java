package application;

public class Order {
	private int orderId;
	private int branchId;
	private int customerId;
	private int paymentId;
	private int tableId;
	private int deliveryId;
	private String orderDate;
	private String orderType;
	private double price;

	// Constructor
	public Order(int orderId, int branchId, int customerId, int paymentId, int tableId, int deliveryId,
			String orderDate, String orderType, double price) {
		this.orderId = orderId;
		this.branchId = branchId;
		this.customerId = customerId;
		this.paymentId = paymentId;
		this.tableId = tableId;
		this.deliveryId = deliveryId;
		this.orderDate = orderDate;
		this.orderType = orderType;
		this.price = price;
	}

	// Getters and Setters
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public int getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(int deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	// toString method
	@Override
	public String toString() {
		return "Order{" + "orderId=" + orderId + ", branchId=" + branchId + ", customerId=" + customerId
				+ ", paymentId=" + paymentId + ", tableId=" + tableId + ", deliveryId=" + deliveryId + ", orderDate='"
				+ orderDate + '\'' + ", orderType='" + orderType + '\'' + ", price=" + price + '}';
	}
}
