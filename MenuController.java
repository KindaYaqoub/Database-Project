package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MenuController {

	@FXML
	private TableView<MenuItem> tableView;
	@FXML
	private TableColumn<MenuItem, Integer> idColumn;
	@FXML
	private TableColumn<MenuItem, String> sizeColumn;
	@FXML
	private TableColumn<MenuItem, String> nameColumn;
	@FXML
	private TableColumn<MenuItem, String> typeColumn;
	@FXML
	private TableColumn<MenuItem, Double> priceColumn;
	@FXML
	private TableColumn<MenuItem, String> descriptionColumn;
	@FXML
	private TextField idTextField;
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField priceTextField;
	@FXML
	private TextField descriptionTextField;
	@FXML
	private ComboBox<String> typeComboBox;
	@FXML
	private ComboBox<String> sizeComboBox; // Add size combo box

	@FXML
	private Button viewButton;
	@FXML
	private Button backButton;
	@FXML
	private Button insertButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button updateButton;
	@FXML
	private Button searchButton;

	private ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	private void initialize() {
		typeComboBox.setItems(FXCollections.observableArrayList("Pizza", "Burger", "Pasta"));
		sizeComboBox.setItems(FXCollections.observableArrayList("Small", "Medium", "Regular"));

		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

		// Initialize menuItems list with data from database

	}

//----------------------------------------------------------------------------------------
	@FXML
	private void handleDeleteButtonAction() {
		// Get the selected menu item from the TableView
		MenuItem selectedMenuItem = tableView.getSelectionModel().getSelectedItem();

		// Check if an item is selected
		if (selectedMenuItem == null) {
			showError1("Please select a menu item to delete."); // Show an error if no item is selected
			return;
		}

		// Confirm deletion
		Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Deletion");
		confirmationAlert.setHeaderText("Are you sure you want to delete this item?");
		confirmationAlert.setContentText("Item: " + selectedMenuItem.getName());

		confirmationAlert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Perform the delete operation from the database
				deleteItemFromDatabase(selectedMenuItem.getId());

				// Remove the item from the ObservableList to update the TableView
				menuItems.remove(selectedMenuItem);
			}
		});
	}

//--------------------------------------------------------------------------------------------
	private void deleteItemFromDatabase(int itemId) {
		String deleteQuery = "DELETE FROM menu_item WHERE item_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

			stmt.setInt(1, itemId); // Set the item_id to delete

			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Item deleted successfully.");
			} else {
				showError1("Error deleting item from database.");
			}
		} catch (Exception e) {
			showError1("Error deleting item from database: " + e.getMessage());
		}
	}

//---------------------------------------------------------------------------------------
	@FXML
	private void handleUpdateButtonAction() {
		// Step 1: Get the item_id from the idTextField
		String idText = idTextField.getText();

		// Step 2: Check if the ID is valid
		if (idText.isEmpty()) {
			showError1("Please enter an item ID.");
			return;
		}

		// Step 3: Parse the ID from the TextField and check if it's a valid integer
		int itemId;
		try {
			itemId = Integer.parseInt(idText);
		} catch (NumberFormatException e) {
			showError1("Invalid item ID format.");
			return;
		}

		// Step 4: Get data from the input fields (TextFields, ComboBoxes)
		String name = nameTextField.getText();
		String type = typeComboBox.getValue();
		String size = sizeComboBox.getValue();
		double price;

		// Validate that name and description are valid strings (non-empty and no
		// numbers)
		if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
			showError1("Invalid name. Please enter a valid name (letters only).");
			return;
		}

		if (descriptionTextField.getText().isEmpty() || !descriptionTextField.getText().matches("[a-zA-Z ]+")) {
			showError1("Invalid description. Please enter a valid description (letters only).");
			return;
		}

		// Validate that price is a valid number
		try {
			price = Double.parseDouble(priceTextField.getText());
		} catch (NumberFormatException e) {
			showError1("Invalid price format. Please enter a valid price.");
			return;
		}

		String description = descriptionTextField.getText();

		// Step 5: Update the database
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String query = "UPDATE menu_item SET name = ?, type = ?, size = ?, price = ?, description = ? WHERE item_id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, name);
				stmt.setString(2, type);
				stmt.setString(3, size);
				stmt.setDouble(4, price);
				stmt.setString(5, description);
				stmt.setInt(6, itemId); // Update based on the item_id entered in the idTextField

				int rowsAffected = stmt.executeUpdate();
				if (rowsAffected > 0) {
					// Step 6: Refresh the TableView to show the updated data
					// Find and update the corresponding item in the TableView's data
					for (MenuItem item : menuItems) {
						if (item.getId() == itemId) {
							item.setName(name);
							item.setType(type);
							item.setSize(size);
							item.setPrice(price);
							item.setDescription(description);
							break;
						}
					}

					tableView.refresh(); // Refresh the TableView to reflect the changes

					// Step 7: Show success alert
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Update Successful");
					alert.setHeaderText(null);
					alert.setContentText("The item has been updated successfully.");
					alert.showAndWait();
				} else {
					showError1("Error updating data.");
				}
			}
		} catch (Exception e) {
			showError1("Error updating data: " + e.getMessage());
		}
	}

//--------------------------------------------------------------------------------------
	@FXML
	private void handleSearchButtonAction() {
		// Step 1: Get the item_id from the idTextField
		String idText = idTextField.getText();

		// Step 2: Check if the ID is valid
		if (idText.isEmpty()) {
			showError1("Please enter an item ID.");
			return;
		}

		// Step 3: Parse the ID from the TextField and check if it's a valid integer
		int itemId;
		try {
			itemId = Integer.parseInt(idText);
		} catch (NumberFormatException e) {
			showError1("Invalid item ID format.");
			return;
		}

		// Step 4: Search for the item in the menuItems list
		MenuItem foundItem = null;
		for (MenuItem item : menuItems) {
			if (item.getId() == itemId) {
				foundItem = item;
				break; // Exit the loop once the item is found
			}
		}

		// Step 5: If item is found, update the TableView to show only the found item
		if (foundItem != null) {
			// Create a new list with only the found item
			ObservableList<MenuItem> foundItemList = FXCollections.observableArrayList();
			foundItemList.add(foundItem);

			// Set the new list to the TableView
			tableView.setItems(foundItemList);
		} else {
			showError1("Item with ID " + itemId + " not found.");
		}
	}

	// Error handling method
	private void showError1(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

//----------------------------------------------------------------------------------------------
	@FXML
	private void handleViewButtonAction() {
		menuItems.clear(); // Clear previous data from the list.
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM menu_item")) { // Query to fetch all records.

			while (rs.next()) { // Loop through all rows in the result set.
				menuItems.add(new MenuItem(rs.getInt("item_id"), // item_id column from table
						rs.getString("size"), // size column from table
						rs.getString("name"), // name column from table
						rs.getString("type"), // type column from table
						rs.getDouble("price"), // price column from table
						rs.getString("description") // description column from table
				));
			}

			tableView.setItems(menuItems); // Set the populated list to the TableView.
		} catch (Exception e) {
			showError1("Error loading data: " + e.getMessage()); // Show an error message if something goes wrong.
		}
	}

//---------------------------------------------------------------------------------------------
	@FXML
	private void handleInsertButtonAction() {
		// Retrieve input values from the text fields and combo boxes.
		String idText = idTextField.getText();
		String name = nameTextField.getText();
		String priceText = priceTextField.getText();
		String description = descriptionTextField.getText();
		String type = typeComboBox.getValue();
		String size = sizeComboBox.getValue();

		// Check if fields are valid (simple validation)
		if (idText.isEmpty() || name.isEmpty() || priceText.isEmpty() || description.isEmpty() || type == null
				|| size == null) {
			showError1("All fields must be filled out.");
			return;
		}

		// Validate that name and description are valid strings (non-empty and no
		// numbers)
		if (!name.matches("[a-zA-Z ]+")) {
			showError1("Invalid name. Please enter a valid name (letters only).");
			return;
		}

		if (!description.matches("[a-zA-Z ]+")) {
			showError1("Invalid description. Please enter a valid description (letters only).");
			return;
		}

		int id = 0;
		double price = 0.0;

		// Check if ID is a valid integer
		try {
			id = Integer.parseInt(idText);
		} catch (NumberFormatException e) {
			showError1("Invalid ID. Please enter a valid number for the ID.");
			return;
		}

		// Check if price is a valid number
		try {
			price = Double.parseDouble(priceText);
		} catch (NumberFormatException e) {
			showError1("Invalid price. Please enter a valid number for the price.");
			return;
		}

		// Check if the ID already exists in the database
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			String checkQuery = "SELECT COUNT(*) FROM menu_item WHERE item_id = ?";
			try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
				checkStmt.setInt(1, id);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					showError1("Item ID already exists. Please choose a unique ID.");
					return;
				}
			}

			// Insert new MenuItem into the database
			String insertQuery = "INSERT INTO menu_item (item_id, size, name, type, price, description) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
				insertStmt.setInt(1, id);
				insertStmt.setString(2, size);
				insertStmt.setString(3, name);
				insertStmt.setString(4, type);
				insertStmt.setDouble(5, price);
				insertStmt.setString(6, description);
				insertStmt.executeUpdate();
			}

			// After successful insertion, clear fields and reload data
			clearFields();
			handleViewButtonAction(); // Refresh the TableView with updated data

		} catch (Exception e) {
			showError1("Error inserting data: " + e.getMessage());
		}
	}

	private void clearFields() {
		idTextField.clear();
		nameTextField.clear();
		priceTextField.clear();
		descriptionTextField.clear();
		typeComboBox.getSelectionModel().clearSelection();
		sizeComboBox.getSelectionModel().clearSelection();
	}

//------------------------------------------------------------------------------------------------------
	@FXML
	private void handleBackButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SamplePassword.fxml"));
			Stage stage = (Stage) backButton.getScene().getWindow(); // الحصول على النافذة الحالية
			stage.setScene(new Scene(loader.load())); // تحميل واجهة SamplePassword
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to load the previous screen.");
		}
	}
	
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title); // العنوان
		alert.setHeaderText(null); // لا يوجد عنوان فرعي
		alert.setContentText(message); // محتوى الرسالة
		alert.showAndWait(); // عرض التنبيه وانتظار تفاعل المستخدم
	}

}
