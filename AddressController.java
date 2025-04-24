package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AddressController {
	@FXML
	private TableView<Address> addressTableView;
	@FXML
	private TableColumn<Address, Integer> addressIdColumn;
	@FXML
	private TableColumn<Address, String> streetColumn;
	@FXML
	private TableColumn<Address, String> cityColumn;
	@FXML
	private TextField addressIdField;

	@FXML
	private TextField streetField;

	@FXML
	private TextField cityField;

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	void initialize() {
		addressIdColumn.setCellValueFactory(new PropertyValueFactory<>("addressId"));
		streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
	}

	// method to view data from table inside the table view 
	@FXML
	private void handleViewButtonAction(ActionEvent event) {
		ObservableList<Address> addresses = FXCollections.observableArrayList();

		String sql = "SELECT * FROM address";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				int addressId = rs.getInt("address_id");
				String street = rs.getString("street");
				String city = rs.getString("city");

				addresses.add(new Address(addressId, street, city));
			}

			addressTableView.setItems(addresses);

		} catch (SQLException e) {
			showErrorMessage("Error loading addresses: " + e.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// method to search for a data by one of these Address ID, city,street . 
	@FXML
	private void handleSearchButtonAction(ActionEvent event) {
		String addressId = addressIdField.getText().trim();
		String street = streetField.getText().trim();
		String city = cityField.getText().trim();

		
		StringBuilder sql = new StringBuilder("SELECT * FROM address WHERE 1=1");

		boolean hasCondition = false;

		if (!addressId.isEmpty()) {
			sql.append(" AND address_id = ").append(addressId);
			hasCondition = true;
		}

		if (!street.isEmpty()) {
			sql.append(" AND street LIKE '%").append(street).append("%'");
			hasCondition = true;
		}

		if (!city.isEmpty()) {
			sql.append(" AND city LIKE '%").append(city).append("%'");
			hasCondition = true;
		}

		
		if (!hasCondition) {
			showErrorMessage("Please enter at least one search criterion (Address ID, Street, or City).");
			return;
		}

		ObservableList<Address> addresses = FXCollections.observableArrayList();

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql.toString())) {

			boolean found = false;

			
			while (rs.next()) {
				int addressIdValue = rs.getInt("address_id");
				String streetValue = rs.getString("street");
				String cityValue = rs.getString("city");

				addresses.add(new Address(addressIdValue, streetValue, cityValue));
				found = true;
			}

			
			addressTableView.setItems(addresses);

			
			if (!found) {
				String message = "No address found matching the provided criteria.";
				if (!addressId.isEmpty()) {
					message = "No address found with the given Address ID.";
				} else if (!street.isEmpty()) {
					message = "No address found with the given Street.";
				} else if (!city.isEmpty()) {
					message = "No address found with the given City.";
				}
				showErrorMessage(message);
			}

		} catch (SQLException e) {
			showErrorMessage("Error searching addresses: " + e.getMessage());
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// method to Update data by id 
	@FXML
	private void handleUpdateButtonAction(ActionEvent event) {
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Are you sure you want to update this address?");
		alert.setContentText("Please confirm if you want to proceed with the update.");

		
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				
				updateAddress();
			} else {
				
				showCancelMessage("Update canceled.");
			}
		});
	}

	private void updateAddress() {
		String addressId = addressIdField.getText().trim();
		String street = streetField.getText().trim();
		String city = cityField.getText().trim();

		if (addressId.isEmpty() || street.isEmpty() || city.isEmpty()) {
			showErrorMessage("Please fill in all fields before updating.");
			return;
		}
		if (!isInteger(addressId)) {
			showErrorMessage("Address ID must be a valid integer.");
			return;
		}

		if (!checkIfAddressExists(addressId)) {
			showErrorMessage("Address ID does not exist. Please check the ID and try again.");
			return;
		}

		String sql = "UPDATE address SET street = ?, city = ? WHERE address_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, street);
			pstmt.setString(2, city);
			pstmt.setInt(3, Integer.parseInt(addressId));

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				
				updateTableView(addressId, street, city);
				showSuccessMessage("Address updated successfully.");
			} else {
				showErrorMessage("Address update failed. No rows affected.");
			}
		} catch (SQLException e) {
			showErrorMessage("Error updating address: " + e.getMessage());
		}
	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean checkIfAddressExists(String addressId) {
		String sql = "SELECT COUNT(*) FROM address WHERE address_id = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, Integer.parseInt(addressId));

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0; 
				}
			}
		} catch (SQLException e) {
			showErrorMessage("Error checking address existence: " + e.getMessage());
		}
		return false;
	}

	private void updateTableView(String addressId, String street, String city) {
		
		for (Address address : addressTableView.getItems()) {
			if (address.getAddressId() == Integer.parseInt(addressId)) {
				
				address.setStreet(street);
				address.setCity(city);
				
				addressTableView.refresh();
				break;
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleDeleteButtonAction(ActionEvent event) {
		
		Address selectedAddress = addressTableView.getSelectionModel().getSelectedItem();

		if (selectedAddress == null) {
			showErrorMessage("Please select an address to delete.");
			return;
		}

	
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this address?");
		alert.setContentText("This action will also delete related records from other tables.");

		
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				
				deleteAddressFromDatabase(selectedAddress);
			} else {
				
				showCancelMessage("Delete operation was canceled.");
			}
		});
	}

	private void deleteAddressFromDatabase(Address selectedAddress) {
		String sql = "DELETE FROM address WHERE address_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, selectedAddress.getAddressId());

			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				addressTableView.getItems().remove(selectedAddress);
				showSuccessMessage("Address and related records deleted successfully.");
			} else {
				showErrorMessage("No address found with the given ID.");
			}

		} catch (SQLException e) {
			showErrorMessage("Error deleting address: " + e.getMessage());
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleInsertButtonAction(ActionEvent event) {
		String addressId = addressIdField.getText();
		String street = streetField.getText();
		String city = cityField.getText();

		
		if (isAddressIdExists(addressId)) {
			showErrorMessage("Address ID already exists. Please enter a unique Address ID.");
			return;
		}

		
		String sql = "INSERT INTO address (address_id, street, city) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, addressId);
			stmt.setString(2, street);
			stmt.setString(3, city);
			stmt.executeUpdate();

			
			Address newAddress = new Address(Integer.parseInt(addressId), street, city);
			addressTableView.getItems().add(newAddress);

			showSuccessMessage("Address inserted successfully.");
		} catch (SQLException e) {
			showErrorMessage("Error inserting address: " + e.getMessage());
		}
	}

	
	private boolean isAddressIdExists(String addressId) {
		String checkSql = "SELECT COUNT(*) FROM address WHERE address_id = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(checkSql)) {
			stmt.setString(1, addressId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0; 
			}
		} catch (SQLException e) {
			showErrorMessage("Error checking Address ID: " + e.getMessage());
		}
		return false;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	public void handleBackButton(ActionEvent event) {
		
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();

		
		CoustomerController customerController = CoustomerController.getInstance();
		if (customerController != null) {
			customerController.updateAddressComboBox();
		}

		
		branchController bController = branchController.getInstance();
		if (bController != null) {
			bController.updateAddressComboBox();
		}

	}

	private void showCancelMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Operation Canceled");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showSuccessMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText(message);
		alert.showAndWait();
	}

}