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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CoustomerController {

	@FXML
	private TableView<Coustomer> tableView;

	@FXML
	private TableColumn<Coustomer, Integer> columnCustomerId;

	@FXML
	private TableColumn<Coustomer, String> columnCustomerName;

	@FXML
	private TableColumn<Coustomer, Integer> columnAddressId;

	@FXML
	private TableColumn<Coustomer, String> columnPhone;

	private ObservableList<Coustomer> customerList;

	@FXML
	private TextField customerIdField;

	@FXML
	private TextField customerNameField;

	@FXML
	private TextField phoneField;

	@FXML
	private ComboBox<Address> addressComboBox; // ComboBox now holds Address objects
	private ObservableList<Address> addressList = FXCollections.observableArrayList();
	private static CoustomerController instance; // Static reference to hold the instance

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	public void initialize() {
		instance = this;

		if (tableView == null) {
			System.err.println("TableView is null. Check fx:id in the FXML file.");
		}

		columnCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
		columnCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		columnAddressId.setCellValueFactory(new PropertyValueFactory<>("addressId"));
		columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

		customerList = FXCollections.observableArrayList();
		tableView.setItems(customerList);

		loadAddressesFromDatabase(); 
	}

	// Method to load addresses from the database and add them to the ComboBox
	private void loadAddressesFromDatabase() {
		String query = "SELECT address_id, street, city FROM address"; 

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				int addressId = rs.getInt("address_id");
				String street = rs.getString("street");
				String city = rs.getString("city");
				Address address = new Address(addressId, street, city); 
				addressList.add(address);
			}

			addressComboBox.setItems(addressList); 

			

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

// This method is called when the user selects an address from the ComboBox
	@FXML
	public void onAddressSelected() {
		Address selectedAddress = addressComboBox.getValue();
		if (selectedAddress != null) {
			System.out.println("Selected Address ID: " + selectedAddress.getAddressId());
			System.out.println("Selected Address: " + selectedAddress.getStreet() + ", " + selectedAddress.getCity());
		}
	}
//--------------------------------------------------------------------------------------------------------------------
	// method to view customer data inside  the table view 
	@FXML
	public void viewCustomers() {
		customerList.clear();

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement()) {

			String query = "SELECT * FROM customer";
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				int customerId = resultSet.getInt("customer_id");
				String customerName = resultSet.getString("customer_name");
				int addressId = resultSet.getInt("address_id");
				String phone = resultSet.getString("phone");

				Coustomer customer = new Coustomer(customerId, customerName, addressId, phone);
				customerList.add(customer);
			
			}
		      tableView.setItems(customerList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// method yo delete data from customer table 	
	@FXML
	public void deleteCustomer() {
		
		Coustomer selectedCustomer = tableView.getSelectionModel().getSelectedItem();

		if (selectedCustomer == null) {
			
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please select a customer to delete.");
			alert.showAndWait();
			return;
		}

	
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Delete Confirmation");
		confirmAlert.setHeaderText(null);
		confirmAlert.setContentText("Are you sure you want to delete this customer?");

		if (confirmAlert.showAndWait().get() == ButtonType.OK) {
	
			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					Statement statement = connection.createStatement()) {

				String deleteQuery = "DELETE FROM customer WHERE customer_id = " + selectedCustomer.getCustomerId();
				int rowsAffected = statement.executeUpdate(deleteQuery);

				if (rowsAffected > 0) {
				
					customerList.remove(selectedCustomer);

					Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
					successAlert.setTitle("Success");
					successAlert.setHeaderText(null);
					successAlert.setContentText("The customer has been successfully deleted.");
					successAlert.showAndWait();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Alert errorAlert = new Alert(Alert.AlertType.ERROR);
				errorAlert.setTitle("Error");
				errorAlert.setHeaderText(null);
				errorAlert.setContentText("An error occurred while deleting the customer.");
				errorAlert.showAndWait();
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// method to search data from customer table by using either customer id or customer name .	
	@FXML
	public void searchCustomer() {
		String customerId = customerIdField.getText();
		String customerName = customerNameField.getText();

		customerList.clear();

		
		String query = "SELECT * FROM customer WHERE 1=1";

		if (!customerId.isEmpty()) {
			query += " AND customer_id = " + customerId;
		}

		if (!customerName.isEmpty()) {
			query += " AND customer_name LIKE '%" + customerName + "%'";
		}

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement()) {

			ResultSet resultSet = statement.executeQuery(query);

			boolean foundResults = false; 

			while (resultSet.next()) {
				int id = resultSet.getInt("customer_id");
				String name = resultSet.getString("customer_name");
				int addressId = resultSet.getInt("address_id");
				String phone = resultSet.getString("phone");

				Coustomer customer = new Coustomer(id, name, addressId, phone);
				customerList.add(customer);
				foundResults = true; 
			}

			
			if (!foundResults) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("No Results");
				alert.setHeaderText(null);
				alert.setContentText("No customer found matching the search criteria.");
				alert.showAndWait();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// method to insert new data to customer table 	
	@FXML
	public void insertCustomer() {
		String customerId = customerIdField.getText();
		String customerName = customerNameField.getText();
		String phone = phoneField.getText();
		Address selectedAddress = addressComboBox.getValue(); 
		
		
		if (selectedAddress == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please select an address.");
			alert.showAndWait();
			return;
		}

		
		if (customerId.isEmpty() || customerName.isEmpty() || phone.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please fill in all fields.");
			alert.showAndWait();
			return;
		}

		
		if (!isPhoneUnique(phone)) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("The phone number is already in use.");
			alert.showAndWait();
			return;
		}

		if (!isCustomerIdUnique(customerId)) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("The customer ID is already in use.");
			alert.showAndWait();
			return;
		}

	
		String query = "INSERT INTO customer (customer_id, customer_name, phone, address_id) VALUES (?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, customerId);
			statement.setString(2, customerName);
			statement.setString(3, phone);
			statement.setInt(4, selectedAddress.getAddressId()); 
			statement.executeUpdate();

			
			viewCustomers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private boolean isPhoneUnique(String phone) {
		String query = "SELECT COUNT(*) FROM customer WHERE phone = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, phone);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next() && resultSet.getInt(1) > 0) {
				return false; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true; 
	}

	
	private boolean isCustomerIdUnique(String customerId) {
		String query = "SELECT COUNT(*) FROM customer WHERE customer_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, customerId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next() && resultSet.getInt(1) > 0) {
				return false; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true; 
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// method to update customer data by customer Id
	@FXML
	public void updateCustomer() {
		String customerId = customerIdField.getText();
		String customerName = customerNameField.getText();
		String phone = phoneField.getText();
		Address selectedAddress = addressComboBox.getValue(); 

		
		if (customerId.isEmpty() || customerName.isEmpty() || phone.isEmpty() || selectedAddress == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please fill in all fields.");
			alert.showAndWait();
			return;
		}


		String checkCustomerIdQuery = "SELECT * FROM customer WHERE customer_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement checkCustomerIdStmt = connection.prepareStatement(checkCustomerIdQuery)) {

			checkCustomerIdStmt.setString(1, customerId);
			ResultSet resultSet = checkCustomerIdStmt.executeQuery();

			if (!resultSet.next()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("No customer found with the provided ID.");
				alert.showAndWait();
				return;
			}

			
			String checkPhoneQuery = "SELECT * FROM customer WHERE phone = ? AND customer_id != ?";

			try (PreparedStatement checkPhoneStmt = connection.prepareStatement(checkPhoneQuery)) {
				checkPhoneStmt.setString(1, phone);
				checkPhoneStmt.setString(2, customerId); 
				ResultSet phoneResult = checkPhoneStmt.executeQuery();

				if (phoneResult.next()) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("This phone number is already associated with another customer.");
					alert.showAndWait();
					return;
				}

				
				String updateQuery = "UPDATE customer SET customer_name = ?, phone = ?, address_id = ? WHERE customer_id = ?";

				try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
					updateStmt.setString(1, customerName);
					updateStmt.setString(2, phone);
					updateStmt.setInt(3, selectedAddress.getAddressId());
					updateStmt.setString(4, customerId);

					int rowsAffected = updateStmt.executeUpdate();

					if (rowsAffected > 0) {
						Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
						successAlert.setTitle("Success");
						successAlert.setHeaderText(null);
						successAlert.setContentText("Customer data updated successfully.");
						successAlert.showAndWait();

						
						viewCustomers();
					} else {
						Alert errorAlert = new Alert(Alert.AlertType.ERROR);
						errorAlert.setTitle("Error");
						errorAlert.setHeaderText(null);
						errorAlert.setContentText("Failed to update the customer data.");
						errorAlert.showAndWait();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Error");
			errorAlert.setHeaderText(null);
			errorAlert.setContentText("An error occurred while updating the customer.");
			errorAlert.showAndWait();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	public void addNewAddress(ActionEvent event) {
		try {
			// تحميل ملف FXML الخاص بواجهة العناوين
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/Address.fxml"));
			Parent root = fxmlLoader.load();

			// إنشاء نافذة جديدة (Stage)
			Stage stage = new Stage();
			stage.setTitle("Address Manager");
			stage.setScene(new Scene(root));
			stage.show();

			updateAddressComboBox();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//---------------------------------------------------------------------------------------------------
	// دالة لتحديث ComboBox
	public void updateAddressComboBox() {
		// تحديث الـ ComboBox هنا
		ObservableList<Address> addresses = FXCollections.observableArrayList();
		String query = "SELECT * FROM address"; // تأكد من أن اسم الجدول صحيح
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement()) {

			ResultSet resultSet = statement.executeQuery(query);

			// إحضار جميع العناوين من قاعدة البيانات وإضافتها إلى الـ ObservableList
			while (resultSet.next()) {
				int addressId = resultSet.getInt("address_id");
				String street = resultSet.getString("street");
				String city = resultSet.getString("city");

				Address address = new Address(addressId, street, city);
				addresses.add(address);
			}

			// تحديث الـ ComboBox
			addressComboBox.setItems(addresses);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// دالة للحصول على المرجع (الـ instance)
	public static CoustomerController getInstance() {
		return instance;
	}
//--------------------------------------------------------
	@FXML
	private void handleBackButtonAction(ActionEvent event) {
	    try {
	      
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SamplePassword.fxml"));
	        Parent root = loader.load();

	        // الحصول على مشهد جديد
	        Scene scene = new Scene(root);

	        // الحصول على نافذة التطبيق الحالية
	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

	        // تعيين المشهد الجديد
	        stage.setScene(scene);
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


}