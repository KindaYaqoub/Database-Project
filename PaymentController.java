package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PaymentController {

	@FXML
	private TableView<Payment> paymentTableView;
	@FXML
	private TableColumn<Payment, Integer> paymentIdColumn;
	@FXML
	private TableColumn<Payment, String> paymentTypeColumn;
	@FXML
	private TableColumn<Payment, String> paymentDetailsColumn;
	@FXML
	private TextField paymentTypeField;
	@FXML
	private TextField paymentDetailsField;
	@FXML
	private TextField paymentIdField;

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	private void initialize() {
		paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
		paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		paymentDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
	}

	/////////////////////////////////////////////////////////////////////////////////
	@FXML
	void handleViewButtonAction() {
		// استدعاء البيانات من قاعدة البيانات
		ObservableList<Payment> payments = fetchPaymentsFromDatabase();
		paymentTableView.setItems(payments); // عرض البيانات في TableView
	}

	private ObservableList<Payment> fetchPaymentsFromDatabase() {
		ObservableList<Payment> payments = FXCollections.observableArrayList();

		String query = "SELECT * FROM payment_method";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			// قراءة البيانات من ResultSet وإضافتها إلى القائمة
			while (resultSet.next()) {
				int paymentId = resultSet.getInt("payment_id");
				String type = resultSet.getString("type");
				String details = resultSet.getString("details");
				payments.add(new Payment(paymentId, type, details)); // إضافة البيانات إلى ObservableList
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to fetch payments from database.");
		}

		return payments;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	void handleDeleteButtonAction(ActionEvent event) {
		Payment selectedPayment = paymentTableView.getSelectionModel().getSelectedItem();

		if (selectedPayment != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Are you sure you want to delete this payment?");
			alert.setContentText("This action cannot be undone.");

			// عرض النافذة والتعامل مع النتيجة
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {

					deletePayment(selectedPayment);

					paymentTableView.getItems().remove(selectedPayment);

					showSuccessMessage("Payment deleted successfully!");
				} else {
					showInfoMessage("Deletion cancelled.");
				}
			});
		} else {
			showErrorMessage("Please select a payment to delete.");
		}
	}

	private void deletePayment(Payment payment) {
		String sql = "DELETE FROM payment_method WHERE payment_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, payment.getPaymentId());
			statement.executeUpdate();
			System.out.println("Payment with ID " + payment.getPaymentId() + " deleted from database.");
		} catch (SQLException e) {
			System.out.println("Error deleting payment: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	void handleInsertButtonAction(ActionEvent event) {
		try {
			if (paymentTypeField == null || paymentIdField == null || paymentDetailsField == null) {
				showErrorMessage("Some required fields are not initialized. Please check your setup.");
				return;
			}

			String paymentId = paymentIdField.getText().trim();
			String paymentType = paymentTypeField.getText().trim();
			String paymentDetails = paymentDetailsField.getText().trim();

			if (paymentId.isEmpty() || paymentType.isEmpty() || paymentDetails.isEmpty()) {
				showErrorMessage("Please fill all fields.");
				return;
			}

			if (!isInteger(paymentId)) {
				showErrorMessage("Payment ID must be a valid integer.");
				return;
			}

			if (!isPaymentIdUnique(paymentId)) {
				showErrorMessage("Payment ID already exists. Please choose another one.");
				return;
			}

			addPayment(paymentId, paymentType, paymentDetails);

		} catch (Exception e) {
			showErrorMessage("An unexpected error occurred: " + e.getMessage());
		}
	}

	private boolean isPaymentIdUnique(String paymentId) {
		String sql = "SELECT COUNT(*) FROM payment_method WHERE payment_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, Integer.parseInt(paymentId));
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) == 0; // إذا كانت النتيجة 0 فهذا يعني أن الـ ID غير موجود
			}
		} catch (SQLException e) {
			showErrorMessage("Error checking payment ID: " + e.getMessage());
		}
		return false;
	}

	private void addPayment(String paymentId, String paymentType, String paymentDetails) {
		String sql = "INSERT INTO payment_method (payment_id, type, details) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, Integer.parseInt(paymentId));
			stmt.setString(2, paymentType);
			stmt.setString(3, paymentDetails);

			int rowsInserted = stmt.executeUpdate();

			if (rowsInserted > 0) {
				showSuccessMessage("Payment added successfully!");
				clearFields();

				// تحديث الـ TableView بعد الإضافة مباشرة
				Payment newPayment = new Payment(Integer.parseInt(paymentId), paymentType, paymentDetails);
				paymentTableView.getItems().add(newPayment); // إضافة العنصر الجديد
			}
		} catch (SQLException e) {
			showErrorMessage("Error adding payment: " + e.getMessage());
		}
	}

	private void clearFields() {
		paymentTypeField.clear();
		paymentDetailsField.clear();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	void handleUpdateButtonAction(ActionEvent event) {
		String paymentId = paymentIdField.getText();
		String paymentType = paymentTypeField.getText();
		String paymentDetails = paymentDetailsField.getText();

		if (!isInteger(paymentId)) {
			showErrorMessage("Address ID must be a valid integer.");
			return;
		}
		if (paymentId.isEmpty() || paymentType.isEmpty() || paymentDetails.isEmpty()) {
			showErrorMessage("Please fill all fields.");
		} else {
			updatePayment(paymentId, paymentType, paymentDetails);
		}
	}
//---------------------------------------------------------------------------------------------
	private void updatePayment(String paymentId, String paymentType, String paymentDetails) {
		// استعلام SQL لتحديث البيانات
		String sql = "UPDATE payment_method SET type = ?, details = ? WHERE payment_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			// تعيين القيم في الاستعلام
			stmt.setString(1, paymentType);
			stmt.setString(2, paymentDetails);
			stmt.setInt(3, Integer.parseInt(paymentId));

			// تنفيذ الاستعلام
			int rowsUpdated = stmt.executeUpdate();

			if (rowsUpdated > 0) {
				showSuccessMessage("Payment updated successfully!");
				clearFields(); // مسح الحقول بعد التحديث
				handleViewButtonAction(); // تحديث الجدول لعرض البيانات الجديدة
			} else {
				showErrorMessage("No payment found with the given ID.");
			}
		} catch (SQLException e) {
			showErrorMessage("Error updating payment: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	void handleSearchButtonAction(ActionEvent event) {
		try {
			String searchId = paymentIdField.getText().trim();

			if (!isInteger(searchId)) {
				showErrorMessage("Payment ID must be a valid integer.");
				return;
			}

			if (searchId.isEmpty()) {
				showErrorMessage("Please enter a Payment ID to search.");
				return;
			}

			Payment payment = searchPaymentById(searchId);

			if (payment != null) {
				ObservableList<Payment> payments = FXCollections.observableArrayList(payment);
				paymentTableView.setItems(payments);
			} else {
				showErrorMessage("No payment found with the given ID.");
			}
		} catch (Exception e) {
			showErrorMessage("An error occurred during search: " + e.getMessage());
		}
	}

	private Payment searchPaymentById(String paymentId) throws SQLException {
		String sql = "SELECT * FROM payment_method WHERE payment_id = ?";
		Payment payment = null;

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, Integer.parseInt(paymentId));
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				payment = new Payment(rs.getInt("payment_id"), rs.getString("type"), rs.getString("details"));
			}
		}
		return payment;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void showSuccessMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showInfoMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// دالة لعرض التنبيهات
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void handleBackButtonAction(ActionEvent event) {
	    // إغلاق النافذة الحالية (نافذة Address.fxml)
	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    stage.close();

	    // تحديث ComboBox بعد إغلاق النافذة
	    OrderController1 payment = OrderController1.getInstance();
	    if (payment != null) {
	        payment.updatePaymentComboBox();
	    }
	}


///////////////////////////////////////////////////////////////////////////////////////////////
	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str); // محاولة تحويل النص إلى عدد صحيح
			return true;
		} catch (NumberFormatException e) {
			return false; // إذا حدث خطأ فهذا يعني أن النص ليس عددًا صحيحًا
		}
	}
}