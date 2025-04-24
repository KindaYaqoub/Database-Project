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

import java.sql.*;

public class branchController {

	@FXML
	private TableView<Branch> branchTableView;
	@FXML
	private TableColumn<Branch, Integer> addressIdColumn;

	@FXML
	private TableView<Branch> branchTable;
	@FXML
	private TableColumn<Branch, Integer> branchIdColumn;
	@FXML
	private TableColumn<Branch, String> phoneNumberColumn;

	@FXML
	private TableColumn<Branch, String> openingHoursColumn;

	@FXML
	private ComboBox<String> HHfrom;

	@FXML
	private ComboBox<String> MMfrom;

	@FXML
	private ComboBox<String> AMPMfrom;

	@FXML
	private ComboBox<String> HHto;

	@FXML
	private ComboBox<String> MMto;

	@FXML
	private ComboBox<String> AMPMto;

	@FXML
	private TextField branchIdField;
	@FXML
	private TextField phoneNumberField;
	@FXML
	private TextField locationField;
	@FXML
	private TextField openingHoursField;
	private static branchController instance; 
	private ObservableList<Branch> branchList = FXCollections.observableArrayList();
	@FXML
	private ComboBox<Address> addressComboBox;
	private ObservableList<Address> addressList = FXCollections.observableArrayList();

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

//////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	public void initialize() {

		instance = this;

		branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
		phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		addressIdColumn.setCellValueFactory(new PropertyValueFactory<>("addressId")); 
		openingHoursColumn.setCellValueFactory(new PropertyValueFactory<>("openingHours"));
		branchTable.setItems(branchList);
		loadAddressesFromDatabase(); 
		updateAddressComboBox();

		populateComboBoxes();

		HHfrom.setOnAction(event -> onTimeSelection());
		MMfrom.setOnAction(event -> onTimeSelection());
		AMPMfrom.setOnAction(event -> onTimeSelection());
		HHto.setOnAction(event -> onTimeSelection());
		MMto.setOnAction(event -> onTimeSelection());
		AMPMto.setOnAction(event -> onTimeSelection());

	}

///////////////////////////////////////////////////////////////////////////////////////////////////
	// method to load address ad put them inside the combobox
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
				addressList.add(address); 			}

			addressComboBox.setItems(addressList); 

		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////
	// method to view branch data inside the table view .
	@FXML
	private void viewBranchData() {
		branchList.clear(); 

		String query = "SELECT * FROM branch";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int branchId = resultSet.getInt("branch_id");
				String phoneNumber = resultSet.getString("phoneNumber");
				String openingHours = resultSet.getString("openinghours");
				int addressId = resultSet.getInt("address_id");

				branchList.add(new Branch(branchId, phoneNumber, openingHours, addressId));
			}

			branchTable.setItems(branchList);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// method to search for a data by either branch id or address id or the two together 
	@FXML
	private void searchBranch() {
		String branchId = branchIdField.getText();
		Address selectedAddress = addressComboBox.getSelectionModel().getSelectedItem();
		String addressId = selectedAddress != null ? String.valueOf(selectedAddress.getAddressId()) : "";

		branchList.clear();

		if (branchId.isEmpty() && addressId.isEmpty()) {
			showAlert("Error", "Please enter either a Branch ID or select an Address ID to search.", Alert.AlertType.ERROR);
			return;
		}

		if (!branchId.isEmpty()) {
			try {
				Integer.parseInt(branchId);
			} catch (NumberFormatException e) {
				showAlert("Invalid Input", "Branch ID must be a valid integer.", Alert.AlertType.ERROR);
				return;
			}
		}

		if (!addressId.isEmpty()) {
			try {
				Integer.parseInt(addressId);
			} catch (NumberFormatException e) {
				showAlert("Invalid Input", "Address ID must be a valid integer.", Alert.AlertType.ERROR);
				return;
			}
		}

		String query = "SELECT * FROM branch WHERE ";
		boolean hasCondition = false;

		if (!branchId.isEmpty()) {
			query += "branch_id = ?";
			hasCondition = true;
		}

		if (!addressId.isEmpty()) {
			if (hasCondition) {
				query += " AND ";
			}
			query += "address_id = ?";
		}

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			int paramIndex = 1;

			if (!branchId.isEmpty()) {
				preparedStatement.setInt(paramIndex++, Integer.parseInt(branchId));
			}

			if (!addressId.isEmpty()) {
				preparedStatement.setInt(paramIndex, Integer.parseInt(addressId));
			}

			ResultSet resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				showAlert("Not Found", "No branch found with the given criteria.", Alert.AlertType.INFORMATION);
				return;
			}

			do {
				int foundBranchId = resultSet.getInt("branch_id");
				String phone = resultSet.getString("phoneNumber");
				int foundAddressId = resultSet.getInt("address_id");
				String openingHours = resultSet.getString("openinghours");

				branchList.add(new Branch(foundBranchId, phone, openingHours, foundAddressId));
			} while (resultSet.next());

		} catch (SQLException e) {
			showAlert("Error", "Error searching for branch: " + e.getMessage(), Alert.AlertType.ERROR);
		} catch (NumberFormatException e) {
			showAlert("Error", "Please enter valid numeric values for Branch ID.", Alert.AlertType.ERROR);
		}
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void addBranch() {
	    String branchId = branchIdField.getText();
	    String phone = phoneNumberField.getText();
	    String openingHours = openingHoursField.getText();

	    if (branchId.isEmpty() || phone.isEmpty() || openingHours.isEmpty()) {
	        showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
	        return;
	    }

	    try {
	        Integer.parseInt(branchId);
	    } catch (NumberFormatException e) {
	        showAlert("Invalid Input", "Branch ID must be a valid integer.", Alert.AlertType.ERROR);
	        return;
	    }

	    Address selectedAddress = addressComboBox.getSelectionModel().getSelectedItem();
	    if (selectedAddress == null) {
	        showAlert("Error", "Please select an address from the ComboBox.", Alert.AlertType.ERROR);
	        return;
	    }
	    int addressId = selectedAddress.getAddressId();

	    String checkBranchQuery = "SELECT COUNT(*) FROM branch WHERE branch_id = ?";
	    String checkPhoneQuery = "SELECT COUNT(*) FROM branch WHERE phoneNumber = ?";
	    String insertQuery = "INSERT INTO branch (branch_id, phoneNumber, openinghours, address_id) VALUES (?, ?, ?, ?)";

	    if (!isValidOpeningHours(openingHours)) {
	        showAlert("Error", "Invalid opening hours format. Please use a valid format (e.g., '08:00 AM - 05:00 PM').", Alert.AlertType.ERROR);
	        return;
	    }

	    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement checkBranchStatement = connection.prepareStatement(checkBranchQuery);
	         PreparedStatement checkPhoneStatement = connection.prepareStatement(checkPhoneQuery)) {

	        checkBranchStatement.setInt(1, Integer.parseInt(branchId));
	        ResultSet branchResultSet = checkBranchStatement.executeQuery();
	        if (branchResultSet.next() && branchResultSet.getInt(1) > 0) {
	            showAlert("Error", "The branch ID already exists.", Alert.AlertType.ERROR);
	            return;
	        }

	        checkPhoneStatement.setString(1, phone);
	        ResultSet phoneResultSet = checkPhoneStatement.executeQuery();
	        if (phoneResultSet.next() && phoneResultSet.getInt(1) > 0) {
	            showAlert("Error", "The phone number is already in use by another branch.", Alert.AlertType.ERROR);
	            return;
	        }

	        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
	            preparedStatement.setInt(1, Integer.parseInt(branchId));
	            preparedStatement.setString(2, phone);
	            preparedStatement.setString(3, openingHours);
	            preparedStatement.setInt(4, addressId);

	            int rowsAffected = preparedStatement.executeUpdate();
	            if (rowsAffected > 0) {
	                branchList.add(new Branch(Integer.parseInt(branchId), phone, openingHours, addressId));
	                showAlert("Success", "Branch added successfully!", Alert.AlertType.INFORMATION);
	            } else {
	                showAlert("Error", "Failed to add the branch.", Alert.AlertType.ERROR);
	            }
	        }
	    } catch (SQLException e) {
	        showAlert("Error", "Error adding branch: " + e.getMessage(), Alert.AlertType.ERROR);
	    }
	}

	private boolean isValidOpeningHours(String openingHours) {
	    String regex = "^([01]?[0-9]|2[0-3]):([0-5][0-9]) (AM|PM) - ([01]?[0-9]|2[0-3]):([0-5][0-9]) (AM|PM)$";
	    return openingHours.matches(regex);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// method to update data in branch table 
	@FXML
	private void updateBranchData() {
		String branchId = branchIdField.getText();
		String phone = phoneNumberField.getText();
		String openingHours = openingHoursField.getText();

		Address selectedAddress = addressComboBox.getValue();
		if (selectedAddress == null) {
			showAlert("Error", "Please select an address.", Alert.AlertType.ERROR);
			return;
		}
		int addressId = selectedAddress.getAddressId(); 
		if (branchId.isEmpty() || phone.isEmpty() || openingHours.isEmpty()) {
			showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
			return;
		}

		
		if (!branchId.isEmpty()) {
			try {
				Integer.parseInt(branchId); 
			} catch (NumberFormatException e) {
				showAlert("Invalid Input", "Branch ID must be a valid integer.", Alert.AlertType.ERROR);
				return;
			}
		}

		if (!isValidOpeningHours(openingHours)) {
			showAlert("Error", "Invalid opening hours format. Please use a valid format (e.g., '08:00 AM - 05:00 PM').",
					Alert.AlertType.ERROR);
			return;
		}

	
		String checkPhoneQuery = "SELECT COUNT(*) FROM branch WHERE phoneNumber = ? AND branch_id != ?";
		String updateQuery = "UPDATE branch SET phoneNumber = ?, address_id = ?, openinghours = ? WHERE branch_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement checkPhoneStatement = connection.prepareStatement(checkPhoneQuery)) {

			checkPhoneStatement.setString(1, phone);
			checkPhoneStatement.setInt(2, Integer.parseInt(branchId));
			ResultSet phoneResultSet = checkPhoneStatement.executeQuery();

			if (phoneResultSet.next() && phoneResultSet.getInt(1) > 0) {
				showAlert("Error", "The phone number is already in use by another branch.", Alert.AlertType.ERROR);
				return;
			}

			try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
				preparedStatement.setString(1, phone);
				preparedStatement.setInt(2, addressId); 
				preparedStatement.setString(3, openingHours);
				preparedStatement.setInt(4, Integer.parseInt(branchId));

				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0) {
					showAlert("Success", "Branch updated successfully!", Alert.AlertType.INFORMATION);
					viewBranchData();
				} else {
					showAlert("Error", "Failed to update the branch.", Alert.AlertType.ERROR);
				}
			}
		} catch (SQLException e) {
			showAlert("Error", "Error updating branch: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	private void deleteBranch() {
		
		Branch selectedBranch = branchTable.getSelectionModel().getSelectedItem();

		if (selectedBranch == null) {
			showAlert("Error", "Please select a branch to delete.", Alert.AlertType.ERROR);
			return; 
		}

		
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Deletion");
		confirmationAlert.setHeaderText(null);
		confirmationAlert.setContentText(
				"Are you sure you want to delete the branch with ID: " + selectedBranch.getBranchId() + "?");

		if (confirmationAlert.showAndWait().get() == ButtonType.OK) {

			String deleteQuery = "DELETE FROM branch WHERE branch_id = ?";

			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

				
				preparedStatement.setInt(1, selectedBranch.getBranchId());

				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0) {
				
					branchList.remove(selectedBranch);
					showAlert("Success", "Branch deleted successfully!", Alert.AlertType.INFORMATION);
				} else {
					showAlert("Error", "Failed to delete the branch.", Alert.AlertType.ERROR);
				}

			} catch (SQLException e) {
				showAlert("Error", "Error deleting branch: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// add new address 
	@FXML
	public void addNewAddress(ActionEvent event) {
		try {
		
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Address.fxml"));
			Parent root = fxmlLoader.load();

			
			Stage stage = new Stage();
			stage.setTitle("Address Manager");
			stage.setScene(new Scene(root));
			stage.show();

			updateAddressComboBox();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// update the combobox with the new address 
	
	public void updateAddressComboBox() {
	
		ObservableList<Address> addresses = FXCollections.observableArrayList();
		String query = "SELECT * FROM address"; 
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement()) {

			ResultSet resultSet = statement.executeQuery(query);

			
			while (resultSet.next()) {
				int addressId = resultSet.getInt("address_id");
				String street = resultSet.getString("street");
				String city = resultSet.getString("city");

				Address address = new Address(addressId, street, city);
				addresses.add(address);
			}

		
			addressComboBox.setItems(addresses);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	public void backButton(ActionEvent event) {
		
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();

		MainController branch = MainController.getInstance();
		if (branch != null) {
			branch.updateBranchComboBox();
		}

		OrderController1 branchOrder = OrderController1.getInstance();
		if (branchOrder != null) {
			branchOrder.updateBranchComboBox();
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// fill the combobox with all times to choose 
	private void populateComboBoxes() {
		
		for (int i = 1; i <= 12; i++) {
			HHfrom.getItems().add(String.format("%02d", i));
			HHto.getItems().add(String.format("%02d", i));
		}

		
		for (int i = 0; i < 60; i++) {
			MMfrom.getItems().add(String.format("%02d", i));
			MMto.getItems().add(String.format("%02d", i));
		}

		// Populate AM/PM
		AMPMfrom.getItems().addAll("AM", "PM");
		AMPMto.getItems().addAll("AM", "PM");
	}
//-----------------------------------------------------------------------------------------

	@FXML
	private void onTimeSelection() {
		
		String fromHour = HHfrom.getValue();
		String fromMinute = MMfrom.getValue();
		String fromPeriod = AMPMfrom.getValue();
		String toHour = HHto.getValue();
		String toMinute = MMto.getValue();
		String toPeriod = AMPMto.getValue();

		
		if (fromHour != null && fromMinute != null && fromPeriod != null && toHour != null && toMinute != null
				&& toPeriod != null && !fromHour.isEmpty() && !fromMinute.isEmpty() && !fromPeriod.isEmpty()
				&& !toHour.isEmpty() && !toMinute.isEmpty() && !toPeriod.isEmpty()) {

			
			String startTime = String.format("%02d:%02d %s", Integer.parseInt(fromHour), Integer.parseInt(fromMinute),
					fromPeriod);
			String endTime = String.format("%02d:%02d %s", Integer.parseInt(toHour), Integer.parseInt(toMinute),
					toPeriod);

		
			String openingHours = startTime + " - " + endTime;
			openingHoursField.setText(openingHours); 
			
			String message = "Selected Time: " + openingHours;
			showAlert("Time Selection", message); 
		} else {
			
			showAlert("Time Selection", "Please select valid start and end times.");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static branchController getInstance() {
		return instance;
	}

	
	private void showAlert(String title, String message, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}