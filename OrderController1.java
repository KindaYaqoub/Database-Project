package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.sql.SQLException;

public class OrderController1 {

	@FXML
	private TableView<Order> tableView;
	@FXML
	private TableColumn<Order, Integer> orderIdColumn;
	@FXML
	private TableColumn<Order, Integer> branchIdColumn;
	@FXML
	private TableColumn<Order, Integer> customerIdColumn;
	@FXML
	private TableColumn<Order, Integer> paymentIdColumn;
	@FXML
	private TableColumn<Order, Integer> tableIdColumn;
	@FXML
	private TableColumn<Order, Integer> deliveryIdColumn;
	@FXML
	private TableColumn<Order, String> orderDateColumn;
	@FXML
	private TableColumn<Order, String> orderTypeColumn;
	@FXML
	private TableColumn<Order, Float> priceColumn;
	@FXML
	private ComboBox<Branch> branchComboBox;
	@FXML
	private ComboBox<Coustomer> CustomerComboBox;
	@FXML
	private ComboBox<Payment> PaymentComboBox;
	@FXML
	private ComboBox<Table> TableComboBox;
	@FXML
	private ComboBox<Delivery> DeliveryComboBox;

	@FXML
	private CheckBox inRestaurantCheckBox;
	@FXML
	private CheckBox deliveryCheckBox;
	@FXML
	private CheckBox takeAwayCheckBox;
	@FXML
	private DatePicker orderDatePicker;
	@FXML
	private TextField priceField;
	@FXML
	private TextField orderIdField;

	private static OrderController1 instance;

	// Database credentials
	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	private void initialize() {
		instance = this;
		if (tableView == null) {
			System.err.println("TableView is null. Check fx:id in the FXML file.");
		}
		// Initialize TableView columns
		orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
		customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
		tableIdColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
		deliveryIdColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryId"));
		orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		orderTypeColumn.setCellValueFactory(new PropertyValueFactory<>("orderType"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		// Load data into ComboBoxes
		updateBranchComboBox();
		updateCustomerComboBox();
		updatePaymentComboBox();
		updateTableComboBox();
		updateDeliveryComboBox();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateBranchComboBox() {
		ObservableList<Branch> branchList = FXCollections.observableArrayList();
		String query = "SELECT branch_id, phoneNumber, openinghours, address_id FROM branch";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int branchId = resultSet.getInt("branch_id");
				String phoneNumber = resultSet.getString("phoneNumber");
				String openingHours = resultSet.getString("openinghours");
				int addressId = resultSet.getInt("address_id");

				Branch branch = new Branch(branchId, phoneNumber, openingHours, addressId);
				branchList.add(branch);
			}

			branchComboBox.setItems(branchList);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading branch data: " + e.getMessage());
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateCustomerComboBox() {
		ObservableList<Coustomer> customerList = FXCollections.observableArrayList();
		String query = "SELECT customer_id, customer_name, address_id, phone FROM customer";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int customerId = resultSet.getInt("customer_id");
				String customerName = resultSet.getString("customer_name");
				int addressId = resultSet.getInt("address_id");
				String phone = resultSet.getString("phone");

				Coustomer customer = new Coustomer(customerId, customerName, addressId, phone);
				customerList.add(customer);
			}

			CustomerComboBox.setItems(customerList);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading customer data: " + e.getMessage());
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updatePaymentComboBox() {
		ObservableList<Payment> paymentList = FXCollections.observableArrayList();
		String query = "SELECT payment_id, type, details FROM payment_method";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int paymentId = resultSet.getInt("payment_id");
				String type = resultSet.getString("type");
				String details = resultSet.getString("details");

				Payment payment = new Payment(paymentId, type, details);
				paymentList.add(payment);
			}

			PaymentComboBox.setItems(paymentList);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading payment data: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateTableComboBox() {
		ObservableList<Table> tableList = FXCollections.observableArrayList();
		String query = "SELECT table_id, capacity, branch_id FROM diningtable";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int tableId = resultSet.getInt("table_id");
				int capacity = resultSet.getInt("capacity");
				int branchId = resultSet.getInt("branch_id");

				Table table = new Table(tableId, capacity, branchId);
				tableList.add(table);
			}

			TableComboBox.setItems(tableList);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading table data: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateDeliveryComboBox() {
		ObservableList<Delivery> deliveryList = FXCollections.observableArrayList();
		// تعديل الاستعلام ليشمل شرط التصفية بحيث يتم جلب التسليمات التي ليست حالتها
		// "Cancelled"
		String query = "SELECT delivery_id, delivery_time, customer_id, status FROM delivery WHERE status != 'Cancelled'";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int deliveryId = resultSet.getInt("delivery_id");
				String deliveryTime = resultSet.getString("delivery_time");
				int customerId = resultSet.getInt("customer_id");
				String status = resultSet.getString("status");

				Delivery delivery = new Delivery(deliveryId, deliveryTime, customerId, status);
				deliveryList.add(delivery);
			}

			// تعيين قائمة التسليمات في ComboBox
			DeliveryComboBox.setItems(deliveryList);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading delivery data: " + e.getMessage());
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	public void handleViewButtonAction() {
		ObservableList<Order> orderList = FXCollections.observableArrayList();
		String query = "SELECT order_id, branch_id, customer_id, payment_id, table_id, delivery_id, order_date, order_type, price FROM orders";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int orderId = resultSet.getInt("order_id");
				int branchId = resultSet.getInt("branch_id");
				int customerId = resultSet.getInt("customer_id");
				int paymentId = resultSet.getInt("payment_id");
				int tableId = resultSet.getInt("table_id");
				int deliveryId = resultSet.getInt("delivery_id");
				String orderDate = resultSet.getString("order_date");
				String orderType = resultSet.getString("order_type");
				double price = resultSet.getDouble("price");

				Order order = new Order(orderId, branchId, customerId, paymentId, tableId, deliveryId, orderDate,
						orderType, price);
				orderList.add(order);
			}

			// Set the data to the TableView
			tableView.setItems(orderList);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading order data: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleInsert() {
		String insertOrderQuery = "INSERT INTO orders (order_id, branch_id, customer_id, payment_id, order_date, table_id, delivery_id, order_type, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		if (orderIdField.getText().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Order ID is required.");
			return;
		}

		int orderId;
		try {
			orderId = Integer.parseInt(orderIdField.getText());
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Order ID must be a valid integer.");
			return;
		}

		float price;
		try {
			price = Float.parseFloat(priceField.getText());
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
			return;
		}

		Branch selectedBranch = branchComboBox.getSelectionModel().getSelectedItem();
		Coustomer selectedCustomer = CustomerComboBox.getSelectionModel().getSelectedItem();
		Payment selectedPayment = PaymentComboBox.getSelectionModel().getSelectedItem();
		Table selectedTable = TableComboBox.getSelectionModel().getSelectedItem();
		Delivery selectedDelivery = DeliveryComboBox.getSelectionModel().getSelectedItem();
		String orderType = getSelectedOrderType(); // التحقق من نوع الطلب
		LocalDate orderDate = orderDatePicker.getValue(); // الحصول على التاريخ من DatePicker

		// التحقق من تعبئة جميع الحقول المطلوبة
		if (orderIdField.getText().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Order ID is required.");
			return;
		}
		if (selectedBranch == null) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Branch is required.");
			return;
		}
		if (selectedCustomer == null) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Customer is required.");
			return;
		}
		if (selectedPayment == null) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Payment method is required.");
			return;
		}
		if (orderDate == null) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Order date is required.");
			return;
		}
		if (orderType == null) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Order type is required.");
			return;
		}
		if (price <= 0) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be greater than 0.");
			return;
		}

		if (!isSingleOrderTypeSelected()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select only one order type.");
			return;
		}

		if ("In Restaurant".equals(orderType)) {
			if (selectedTable == null) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a dining table.");
				return;
			}
			if (selectedTable.getBranchId() != selectedBranch.getBranchId()) {
				showAlert(Alert.AlertType.ERROR, "Branch ID Mismatch",
						"The branch ID of the table does not match the selected branch.");
				return;
			}
		} else if ("Delivery".equals(orderType)) {
			if (selectedDelivery == null) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a delivery.");
				return;
			}
			if (selectedDelivery.getCustomerId() != selectedCustomer.getCustomerId()) {
				showAlert(Alert.AlertType.ERROR, "Customer ID Mismatch",
						"The customer ID of the delivery does not match the selected customer.");
				return;
			}
		}

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement checkOrderIdStmt = connection
						.prepareStatement("SELECT COUNT(*) FROM orders WHERE order_id = ?");
				PreparedStatement preparedStatement = connection.prepareStatement(insertOrderQuery)) {

			// التحقق من أن order_id غير مكرر
			checkOrderIdStmt.setInt(1, orderId);
			ResultSet rs = checkOrderIdStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				showAlert(Alert.AlertType.ERROR, "Duplicate Order ID",
						"The order ID already exists. Please enter a unique order ID.");
				return;
			}

			preparedStatement.setInt(1, orderId);
			preparedStatement.setInt(2, selectedBranch.getBranchId());
			preparedStatement.setInt(3, selectedCustomer.getCustomerId());
			preparedStatement.setInt(4, selectedPayment.getPaymentId());
			preparedStatement.setDate(5, java.sql.Date.valueOf(orderDate));

			if ("In Restaurant".equals(orderType)) {
				preparedStatement.setInt(6, selectedTable.getTableId());
				preparedStatement.setNull(7, Types.INTEGER); // لا يوجد توصيل
			} else if ("Delivery".equals(orderType)) {
				preparedStatement.setNull(6, Types.INTEGER); // لا يوجد طاولة
				preparedStatement.setInt(7, selectedDelivery.getDeliveryId());
			} else if ("TakeAway".equals(orderType)) {
				preparedStatement.setNull(6, Types.INTEGER); // لا يوجد طاولة
				preparedStatement.setNull(7, Types.INTEGER); // لا يوجد توصيل
			}

			preparedStatement.setString(8, orderType);
			preparedStatement.setFloat(9, price);

			// تنفيذ الاستعلام
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Order inserted successfully!");
				handleViewButtonAction();
			} else {
				showAlert(Alert.AlertType.WARNING, "Failure", "Failed to insert order.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error inserting order: " + e.getMessage());
		}
	}

	private String getSelectedOrderType() {
		if (inRestaurantCheckBox.isSelected()) {
			return "In Restaurant";
		} else if (deliveryCheckBox.isSelected()) {
			return "Delivery";
		} else if (takeAwayCheckBox.isSelected()) {
			return "TakeAway";
		}
		return null;
	}

	private boolean isSingleOrderTypeSelected() {
		int count = 0;
		if (inRestaurantCheckBox.isSelected())
			count++;
		if (deliveryCheckBox.isSelected())
			count++;
		if (takeAwayCheckBox.isSelected())
			count++;
		return count == 1; // يجب أن يكون خيار واحد فقط
	}
////////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	private void handleUpdateOrder() {
		String updateOrderQuery = "UPDATE orders SET branch_id = ?, customer_id = ?, payment_id = ?, order_date = ?, table_id = ?, delivery_id = ?, order_type = ?, price = ? WHERE order_id = ?";

		// الحصول على القيم من عناصر الواجهة
		int orderId = 0;
		try {
			// التأكد من أن orderId هو عدد صحيح
			orderId = Integer.parseInt(orderIdField.getText());
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Order ID must be a valid integer.");
			return;
		}

		Branch selectedBranch = branchComboBox.getSelectionModel().getSelectedItem();
		Coustomer selectedCustomer = CustomerComboBox.getSelectionModel().getSelectedItem();
		Payment selectedPayment = PaymentComboBox.getSelectionModel().getSelectedItem();
		Table selectedTable = TableComboBox.getSelectionModel().getSelectedItem();
		Delivery selectedDelivery = DeliveryComboBox.getSelectionModel().getSelectedItem();
		String orderType = getSelectedOrderType();
		LocalDate orderDate = orderDatePicker.getValue();

		float price = 0.0f;
		try {
			price = Float.parseFloat(priceField.getText());
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
			return;
		}

		if (orderIdField.getText().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Order ID is required.");
			return;
		}
		if (selectedBranch == null || selectedCustomer == null || selectedPayment == null || orderDate == null
				|| orderType == null || price <= 0) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields.");
			return;
		}
		if (!isSingleOrderTypeSelected()) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select only one order type.");
			return;
		}
		if ("In Restaurant".equals(orderType)) {
			if (selectedTable == null || selectedTable.getBranchId() != selectedBranch.getBranchId()) {
				showAlert(Alert.AlertType.ERROR, "Branch ID Mismatch",
						"The branch ID of the table does not match the selected branch.");
				return;
			}
		} else if ("Delivery".equals(orderType)) {
			if (selectedDelivery == null || selectedDelivery.getCustomerId() != selectedCustomer.getCustomerId()) {
				showAlert(Alert.AlertType.ERROR, "Customer ID Mismatch",
						"The customer ID of the delivery does not match the selected customer.");
				return;
			}
		}

		Alert confirmUpdateAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmUpdateAlert.setTitle("Confirm Update");
		confirmUpdateAlert.setHeaderText("Are you sure you want to update this order?");
		confirmUpdateAlert.setContentText("This action will update the order details in the database.");

		Optional<ButtonType> result = confirmUpdateAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {

			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement checkOrderIdStmt = connection
							.prepareStatement("SELECT COUNT(*) FROM orders WHERE order_id = ?");
					PreparedStatement preparedStatement = connection.prepareStatement(updateOrderQuery)) {

				checkOrderIdStmt.setInt(1, orderId);
				ResultSet rs = checkOrderIdStmt.executeQuery();
				if (rs.next() && rs.getInt(1) == 0) {
					showAlert(Alert.AlertType.ERROR, "Order Not Found", "Order ID does not exist.");
					return;
				}

				preparedStatement.setInt(1, selectedBranch.getBranchId());
				preparedStatement.setInt(2, selectedCustomer.getCustomerId());
				preparedStatement.setInt(3, selectedPayment.getPaymentId());
				preparedStatement.setDate(4, java.sql.Date.valueOf(orderDate));

				if ("In Restaurant".equals(orderType)) {
					preparedStatement.setInt(5, selectedTable.getTableId());
					preparedStatement.setNull(6, Types.INTEGER); // لا يوجد توصيل
				} else if ("Delivery".equals(orderType)) {
					preparedStatement.setNull(5, Types.INTEGER); // لا يوجد طاولة
					preparedStatement.setInt(6, selectedDelivery.getDeliveryId());
				} else if ("TakeAway".equals(orderType)) {
					preparedStatement.setNull(5, Types.INTEGER); // لا يوجد طاولة
					preparedStatement.setNull(6, Types.INTEGER); // لا يوجد توصيل
				}

				preparedStatement.setString(7, orderType);
				preparedStatement.setFloat(8, price);
				preparedStatement.setInt(9, orderId); // تحديد الـ orderId الذي سيتم تحديثه

				// تنفيذ الاستعلام
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Order updated successfully!");
					handleViewButtonAction(); // تحديث البيانات في الـ TableView بعد التحديث
				} else {
					showAlert(Alert.AlertType.WARNING, "Failure", "Failed to update order.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating order: " + e.getMessage());
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleSearchOrder() {
		String searchOrderQuery = "SELECT * FROM orders WHERE order_id = ?";
		int orderId;

		try {
			// التحقق من إدخال رقم صحيح
			orderId = Integer.parseInt(orderIdField.getText());
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Order ID must be a valid integer.");
			return;
		}

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(searchOrderQuery)) {

			preparedStatement.setInt(1, orderId);
			ResultSet resultSet = preparedStatement.executeQuery();

			ObservableList<Order> orders = FXCollections.observableArrayList();

			if (resultSet.next()) {
				// إنشاء كائن Order باستخدام القيم المسترجعة
				orders.add(new Order(resultSet.getInt("order_id"), resultSet.getInt("branch_id"),
						resultSet.getInt("customer_id"), resultSet.getInt("payment_id"),
						resultSet.getInt("delivery_id"), // التاريخ كـ String
						resultSet.getInt("table_id"), resultSet.getString("order_date"),
						resultSet.getString("order_type"), resultSet.getFloat("price")));

				// تحديث جدول البيانات
				tableView.setItems(orders);
				showAlert(Alert.AlertType.INFORMATION, "Search Result", "Order found and displayed.");
			} else {
				// إذا لم يتم العثور على الطلب
				showAlert(Alert.AlertType.WARNING, "Not Found", "No order found with the given ID.");
				tableView.getItems().clear(); // تنظيف الجدول
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error searching for order: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleDeleteOrder() {
		// التحقق من تحديد عنصر في الجدول
		Order selectedOrder = tableView.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to delete.");
			return;
		}

		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Delete Confirmation");
		confirmationAlert.setHeaderText(null);
		confirmationAlert.setContentText("Are you sure you want to delete the selected order?");
		Optional<ButtonType> result = confirmationAlert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			String deleteQuery = "DELETE FROM orders WHERE order_id = ?";

			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

				preparedStatement.setInt(1, selectedOrder.getOrderId());
				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					// إزالة الطلب من الجدول
					tableView.getItems().remove(selectedOrder);
					showAlert(Alert.AlertType.INFORMATION, "Success", "Order deleted successfully.");
				} else {
					showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the selected order.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting the order: " + e.getMessage());
			}
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	void handleBackButtonAction(ActionEvent event) {
	    // الحصول على النافذة (stage) الحالية
	    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    // إغلاق النافذة
	    currentStage.close();
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleAddBranch(ActionEvent event) {
		openWindow("/Branch.fxml", "Add New Branch");
	}

	@FXML
	private void handleAddCustomer(ActionEvent event) {
		openWindow("/Coustomer.fxml", "Add New Customer");
	}

	@FXML
	private void handleAddPayment(ActionEvent event) {
		openWindow("/Payment.fxml", "Add New Payment");
	}

	@FXML
	private void handleAddTable(ActionEvent event) {
		openWindow("/Table.fxml", "Add New Table");
	}

	@FXML
	private void handleAddDelivery(ActionEvent event) {
		openWindow("/Delivery.fxml", "Add New Delivery");
	}

	private void openWindow(String fxmlFile, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "", "Could not load the window: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static OrderController1 getInstance() {
		return instance;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Method to show an alert dialog
	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}