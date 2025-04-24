package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class deliveryController {

	@FXML
	private TableColumn<Delivery, Integer> deliveryIdColumn;

	@FXML
	private TableView<Delivery> tableView;

	@FXML
	private TableColumn<Delivery, Integer> deliveryCustomerID;
	@FXML
	private TableColumn<Delivery, String> deliveryTimeColumn;

	@FXML
	private TableColumn<Delivery, String> statusColumn;;

	@FXML
	private TextField deliveryIdField;
	@FXML
	private TextField deliveryTimeField;
	@FXML
	private DatePicker datePicker;

	@FXML
	private ComboBox<String> HHComboBox;

	@FXML
	private ComboBox<String> MMComboBox;

	@FXML
	private ComboBox<String> statusComboBox;

	@FXML
	private ComboBox<Coustomer> CustomerComboBox;
	private static deliveryController instance; // Static reference to hold the instance

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	public void initialize() {
		instance = this;

		deliveryIdColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryId"));
		deliveryTimeColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryTime"));
		deliveryCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		statusComboBox.setItems(FXCollections.observableArrayList("Pending", "Delivered", "Cancelled"));

		updateCustomerComboBox();

		// Populate hours (HH)
		for (int i = 1; i <= 24; i++) {
			HHComboBox.getItems().add(String.format("%02d", i));
		}

		// Populate minutes (MM)
		for (int i = 0; i < 60; i++) {
			MMComboBox.getItems().add(String.format("%02d", i));
		}

		HHComboBox.setOnAction(event -> onSetDeliveryTime());
		MMComboBox.setOnAction(event -> onSetDeliveryTime());

	}
//----------------------------------------------------------------------------------------------
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
//////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	private void handleView() {
		ObservableList<Delivery> deliveryList = FXCollections.observableArrayList();
		String query = "SELECT * FROM delivery";

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

			tableView.setItems(deliveryList);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleInsert() {
		try {
			// التحقق من أن حقل ID يحتوي على رقم صحيح
			int deliveryId = Integer.parseInt(deliveryIdField.getText());
			String deliveryTime = deliveryTimeField.getText(); // تاريخ ووقت التسليم
			String status = statusComboBox.getValue();
			Coustomer selectedCustomer = CustomerComboBox.getValue();

			// التحقق من أن جميع الحقول قد تم تعبئتها
			if (deliveryTime.isEmpty() || status == null || selectedCustomer == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields!");
				return;
			}

			// التحقق من تنسيق تاريخ ووقت التسليم
			if (!isValidDateTime(deliveryTime)) {
				showAlert(Alert.AlertType.ERROR, "Input Error",
						"Please enter a valid date and time in the format: yyyy-MM-dd HH:mm:ss");
				return;
			}

			// التحقق من أن ID غير مكرر
			if (isDuplicateDeliveryId(deliveryId)) {
				showAlert(Alert.AlertType.ERROR, "Duplicate ID", "Delivery ID already exists!");
				return;
			}

			// إدخال البيانات في قاعدة البيانات
			String query = "INSERT INTO delivery (delivery_id, delivery_time, customer_id, status) VALUES (?, ?, ?, ?)";

			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement preparedStatement = connection.prepareStatement(query)) {

				preparedStatement.setInt(1, deliveryId);
				preparedStatement.setString(2, deliveryTime); // التأكد من أن التاريخ والوقت بتنسيق صحيح
				preparedStatement.setInt(3, selectedCustomer.getCustomerId());
				preparedStatement.setString(4, status);

				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Delivery added successfully!");
					handleView();
				}
			}

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Delivery ID must be a number!");
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error inserting delivery: " + e.getMessage());
		}
	}

	private boolean isValidDateTime(String dateTime) {
		String regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
		return dateTime.matches(regex);
	}

	// التحقق من ID مكرر
	private boolean isDuplicateDeliveryId(int deliveryId) {
		String query = "SELECT delivery_id FROM delivery WHERE delivery_id = ?";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, deliveryId);
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet.next(); // إذا كان هناك صف، فـ ID مكرر

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error checking delivery ID: " + e.getMessage());
			return true; // منع الإدخال في حال الخطأ
		}
	}

//------------------------------------------------------------------------
	@FXML
	private void handleSearch() {
		String deliveryIdInput = deliveryIdField.getText();

		if (deliveryIdInput.isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a delivery ID to search!");
			return;
		}

		if (!deliveryIdInput.matches("\\d+")) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Delivery ID must be a valid number!");
			return;
		}

		int deliveryId = Integer.parseInt(deliveryIdInput); // تحويل النص إلى عدد صحيح

		ObservableList<Delivery> deliveryList = FXCollections.observableArrayList();
		String query = "SELECT * FROM delivery WHERE delivery_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, deliveryId); // وضع قيمة ID في الاستعلام

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					// إذا وجدنا سجلًا مطابقًا، نقوم بإضافته إلى القائمة لعرضه في الجدول
					String deliveryTime = resultSet.getString("delivery_time");
					int customerId = resultSet.getInt("customer_id");
					String status = resultSet.getString("status");

					Delivery delivery = new Delivery(deliveryId, deliveryTime, customerId, status);
					deliveryList.add(delivery);
				} else {
					showAlert(Alert.AlertType.INFORMATION, "No Results", "No delivery found with the given ID.");
				}

				tableView.setItems(deliveryList); // تحديث الجدول لعرض النتائج

			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error retrieving delivery data: " + e.getMessage());
		}
	}

//------------------------------------------------------------------------
	@FXML
	private void handleUpdate() {
		// قراءة المدخلات
		String deliveryIdInput = deliveryIdField.getText();
		String deliveryTimeInput = deliveryTimeField.getText();
		String statusInput = statusComboBox.getValue();
		Coustomer selectedCustomer = CustomerComboBox.getValue();

		// التحقق من أن الحقول ليست فارغة
		if (isInputValid(deliveryIdInput, deliveryTimeInput, statusInput, selectedCustomer)) {
			int deliveryId = Integer.parseInt(deliveryIdInput); // تحويل deliveryId إلى عدد صحيح
			int customerId = selectedCustomer.getCustomerId(); // أخذ customerId من الكومبوبوكس
			String status = statusInput;

			// تأكيد من المستخدم قبل التحديث
			if (confirmUpdate(deliveryId, selectedCustomer, status)) {
				// إذا وافق المستخدم على التحديث، نتابع تنفيذ التحديث في قاعدة البيانات
				updateDelivery(deliveryId, deliveryTimeInput, customerId, status);
			}
		}
	}

	// التحقق من المدخلات
	private boolean isInputValid(String deliveryIdInput, String deliveryTimeInput, String statusInput,
			Coustomer selectedCustomer) {
		if (deliveryIdInput.isEmpty() || deliveryTimeInput.isEmpty() || statusInput == null
				|| selectedCustomer == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
			return false;
		}

		// التحقق من أن deliveryId هو رقم صحيح
		if (!deliveryIdInput.matches("\\d+")) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Delivery ID must be a valid number.");
			return false;
		}

		return true;
	}

	// عرض تأكيد التحديث
	private boolean confirmUpdate(int deliveryId, Coustomer selectedCustomer, String status) {
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Confirm Update");
		confirmAlert.setHeaderText("Are you sure you want to update this delivery?");
		confirmAlert.setContentText("Delivery ID: " + deliveryId + "\nCustomer: " + selectedCustomer.getCustomerName()
				+ "\nStatus: " + status);

		Optional<ButtonType> result = confirmAlert.showAndWait();
		return result.isPresent() && result.get() == ButtonType.OK;
	}

	// تحديث البيانات في قاعدة البيانات
	private void updateDelivery(int deliveryId, String deliveryTimeInput, int customerId, String status) {
		String query = "UPDATE delivery SET delivery_time = ?, customer_id = ?, status = ? WHERE delivery_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, deliveryTimeInput); // إضافة قيمة deliveryTime
			preparedStatement.setInt(2, customerId); // إضافة قيمة customerId
			preparedStatement.setString(3, status); // إضافة قيمة status
			preparedStatement.setInt(4, deliveryId); // إضافة قيمة deliveryId (لتحديد السجل)

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Update Successful", "The delivery record has been updated.");
				handleView(); // تحديث الجدول بعد التحديث
			} else {
				showAlert(Alert.AlertType.ERROR, "Update Failed", "No delivery found with the given ID.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating the delivery data: " + e.getMessage());
		}
	}

//--------------------------------------------------------------------------
	@FXML
	private void handleDelete() {
		// الحصول على السطر المحدد من الـ TableView
		Delivery selectedDelivery = tableView.getSelectionModel().getSelectedItem();

		// التحقق من أنه تم تحديد عنصر في الجدول
		if (selectedDelivery == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a delivery record to delete.");
			return;
		}

		// الحصول على الـ deliveryId من السجل المحدد
		int deliveryId = selectedDelivery.getDeliveryId();

		// تأكيد من المستخدم قبل الحذف
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Confirm Deletion");
		confirmAlert.setHeaderText("Are you sure you want to delete this delivery?");
		confirmAlert
				.setContentText("Delivery ID: " + deliveryId + "\nCustomer ID: " + selectedDelivery.getCustomerId());

		Optional<ButtonType> result = confirmAlert.showAndWait();

		// إذا وافق المستخدم على الحذف، نتابع عملية الحذف في قاعدة البيانات
		if (result.isPresent() && result.get() == ButtonType.OK) {
			deleteDelivery(deliveryId);
		}
	}

	// دالة لحذف السجل من قاعدة البيانات
	private void deleteDelivery(int deliveryId) {
		String query = "DELETE FROM delivery WHERE delivery_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, deliveryId); // تعيين الـ deliveryId للسجل الذي سيتم حذفه

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "The delivery record has been deleted.");
				handleView(); // تحديث الجدول بعد الحذف
			} else {
				showAlert(Alert.AlertType.ERROR, "Deletion Failed", "No delivery found with the given ID.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting the delivery data: " + e.getMessage());
		}
	}

//------------------------------------------------------------------------------------------
	@FXML
	private void AddNewCustomers() {
		try {
			// تحميل FXML للنافذة الجديدة
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Coustomer.fxml"));
			Parent root = loader.load();

			// إعداد المشهد
			Scene scene = new Scene(root);

			// فتح نافذة جديدة
			Stage stage = new Stage();
			stage.setTitle("Add New Customer");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add customer window.");
			e.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////
	public static deliveryController getInstance() {
		return instance;
	}

///////////////////////////////////////////////////////////////////////////

	@FXML
	public void handleBack(ActionEvent event) {
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();

		OrderController1 delivery = OrderController1.getInstance();
		if (delivery != null) {
			delivery.updateDeliveryComboBox();
		}

	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void onSetDeliveryTime() {
		// Get selected date
		LocalDate selectedDate = datePicker.getValue();
		if (selectedDate == null) {
			showAlert("Delivery Time", "Please select a valid date.");
			return;
		}

		// Get selected time (HH, MM, AM/PM)
		String hour = HHComboBox.getValue();
		String minute = MMComboBox.getValue();

		// Check if all time values are selected
		if (hour == null || minute == null) {
			showAlert("Delivery Time", "Please select valid time values (HH, MM).");
			return;
		}

		// Combine date and time into the desired format
		String formattedDate = selectedDate.toString(); // Example: "2024-12-25"
		String formattedTime = String.format("%02d:%02d:00", Integer.parseInt(hour), Integer.parseInt(minute));
		String deliveryTime = formattedDate + " " + formattedTime;

		// Set the combined value in the TextField
		deliveryTimeField.setText(deliveryTime);

		// Show success message
		showAlert("Delivery Time", "Selected Delivery Time: " + deliveryTime);
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	// Method to show an alert dialog
	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

//////////////////////////////////////////////////////////////////////////////////////////////
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
//-------------------------------------------------------------------------------------------------------------

}