package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*; 
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class MainController extends Application {

	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/Resturent";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "1221893";

	
	
	private Connection connection;

	
	@FXML
	private TextField tableIdField;

	@FXML
	private TextField capacityField;

	@FXML
	private TextField branchIdField;

	

	@FXML
	private TableView<Table> tableView;

	@FXML
	private TableColumn<Table, Integer> tableIdColumn;

	@FXML
	private TableColumn<Table, Integer> capacityColumn;

	@FXML
	private TableColumn<Table, Integer> branchIdColumn;

	@FXML
	private Button insertButton, deleteButton, updateButton, searchButton, viewButton, backButton, AddNewBranch;

	private ObservableList<Table> tableList;
	@FXML
	private ComboBox<Branch> branchComboBox;
	private static MainController instance; // Static reference to hold the instance

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("application/Table1.fxml"));
		Scene scene = new Scene(loader.load());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Dining Table Manager");
		primaryStage.show();
	}

	@FXML
	private void initialize() {
		instance = this;

		try {
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
		}

		tableList = FXCollections.observableArrayList();

		tableIdColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
		capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
		branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));

		tableView.setItems(tableList);
		updateBranchComboBox();
	}

	public static MainController getInstance() {
		return instance;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateBranchComboBox() {
		ObservableList<Branch> branchList = FXCollections.observableArrayList();
		String query = "SELECT branch_id, phoneNumber, openinghours, address_id FROM branch";

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void insertDiningTable() {
	    String tableIdText = tableIdField.getText();
	    String capacityText = capacityField.getText();
	    Branch selectedBranch = branchComboBox.getValue();

	    if (tableIdText.isEmpty() || capacityText.isEmpty() || selectedBranch == null) {
	        showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields.");
	        return;
	    }

	    try {
	        int tableId = Integer.parseInt(tableIdText);
	        int capacity = Integer.parseInt(capacityText);
	        int branchId = selectedBranch.getBranchId();

	        String checkQuery = "SELECT COUNT(*) FROM diningtable WHERE table_id = ?";
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

	            checkStmt.setInt(1, tableId);
	            ResultSet resultSet = checkStmt.executeQuery();
	            if (resultSet.next() && resultSet.getInt(1) > 0) {
	                showAlert(Alert.AlertType.ERROR, "Duplicate Error", "Table ID already exists.");
	                return;
	            }
	        }

	        String insertQuery = "INSERT INTO diningtable (table_id, capacity, branch_id) VALUES (?, ?, ?)";
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

	            insertStmt.setInt(1, tableId);
	            insertStmt.setInt(2, capacity);
	            insertStmt.setInt(3, branchId);
	            insertStmt.executeUpdate();

	            showAlert(Alert.AlertType.INFORMATION, "Success", "Dining table added successfully.");
	            loadData();
	        }

	    } catch (NumberFormatException e) {
	        showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numeric values for Table ID or Capacity.");
	    } catch (SQLException e) {
	        showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding dining table: " + e.getMessage());
	    }
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void deleteDiningTable() {
	    Table selectedTable = tableView.getSelectionModel().getSelectedItem();

	    if (selectedTable == null) {
	        showAlert(Alert.AlertType.WARNING, "Warning", "Please select a table to delete.");
	        return;
	    }

	    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmationAlert.setTitle("Confirmation");
	    confirmationAlert.setHeaderText("Are you sure you want to delete this table?");
	    confirmationAlert.setContentText("Table ID: " + selectedTable.getTableId());

	    Optional<ButtonType> result = confirmationAlert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	        String deleteQuery = "DELETE FROM diningtable WHERE table_id = ?";
	        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
	            stmt.setInt(1, selectedTable.getTableId());
	            int affectedRows = stmt.executeUpdate();

	            if (affectedRows > 0) {
	                showAlert(Alert.AlertType.INFORMATION, "Success", "Table deleted successfully.");
	                tableList.remove(selectedTable);
	            } else {
	                showAlert(Alert.AlertType.WARNING, "Not Found", "Table ID not found.");
	            }
	        } catch (SQLException e) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete table: " + e.getMessage());
	        }
	    } else {
	        showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Delete operation cancelled.");
	    }
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void updateDiningTable() {
	    try {
	        int tableId = Integer.parseInt(tableIdField.getText());
	        int capacity = Integer.parseInt(capacityField.getText());
	        Branch selectedBranch = branchComboBox.getSelectionModel().getSelectedItem();

	        if (selectedBranch == null) {
	            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a Branch ID from the ComboBox.");
	            return;
	        }

	        int branchId = selectedBranch.getBranchId();

	        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
	        confirmationAlert.setTitle("Confirmation");
	        confirmationAlert.setHeaderText("Are you sure you want to update this table?");
	        confirmationAlert.setContentText("Table ID: " + tableId + "\nCapacity: " + capacity + "\nBranch ID: " + branchId);

	        Optional<ButtonType> result = confirmationAlert.showAndWait();
	        if (result.isPresent() && result.get() == ButtonType.OK) {
	            String updateQuery = "UPDATE diningtable SET capacity = ?, branch_id = ? WHERE table_id = ?";
	            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
	                stmt.setInt(1, capacity);
	                stmt.setInt(2, branchId);
	                stmt.setInt(3, tableId);

	                int affectedRows = stmt.executeUpdate();
	                if (affectedRows > 0) {
	                    showAlert(Alert.AlertType.INFORMATION, "Success", "Table updated successfully.");
	                } else {
	                    showAlert(Alert.AlertType.WARNING, "Not Found", "Table ID not found.");
	                }
	            }
	            loadData();
	        } else {
	            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Update operation cancelled by user.");
	        }
	    } catch (SQLException e) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update table: " + e.getMessage());
	    } catch (NumberFormatException e) {
	        showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values for Table ID or Capacity.");
	    }
	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void searchDiningTable() {
	    try {
	        int tableId = Integer.parseInt(tableIdField.getText());

	        String searchQuery = "SELECT * FROM diningtable WHERE table_id = ?";
	        try (PreparedStatement stmt = connection.prepareStatement(searchQuery)) {
	            stmt.setInt(1, tableId);
	            ResultSet resultSet = stmt.executeQuery();

	            tableList.clear();

	            if (!resultSet.next()) {
	                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No table found with the given Table ID.");
	                return;
	            }
	            do {
	                int foundTableId = resultSet.getInt("table_id");
	                int capacity = resultSet.getInt("capacity");
	                int branchId = resultSet.getInt("branch_id");

	                tableList.add(new Table(foundTableId, capacity, branchId));
	            } while (resultSet.next());

	            tableView.setItems(tableList);

	        } catch (SQLException e) {
	            showAlert(Alert.AlertType.ERROR, "Database Error", "Error searching for table: " + e.getMessage());
	        }
	    } catch (NumberFormatException e) {
	        showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric Table ID.");
	    }
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void loadData() {
		tableList.clear();
		String query = "SELECT * FROM diningtable";

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				int tableId = resultSet.getInt("table_id");
				int capacity = resultSet.getInt("capacity");
				int branchId = resultSet.getInt("branch_id");

				tableList.add(new Table(tableId, capacity, branchId));
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading dining tables: " + e.getMessage());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleBackAction() {
		Stage stage = (Stage) backButton.getScene().getWindow();
		stage.close();
		OrderController1 table = OrderController1.getInstance();
		if (table != null) {
			table.updateTableComboBox();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void AddNewBranch() {
		try {
			// تحميل الـ FXML الخاص بنوافذ الإدخال الخاصة بالفرع
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Branch.fxml")); // تأكد من مسار ملف FXML

			// إنشاء نافذة جديدة
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Add New Branch");
			stage.setScene(new Scene(root));
			stage.show(); // عرض النافذة الجديدة

		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add New Branch window.");
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}