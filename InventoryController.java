package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class InventoryController {

	@FXML
	private TextField inventoryIdField;
	@FXML
	private TextField quantityField;
	@FXML
	private TextField nameField;
	@FXML
	private TableView<Inventory> tableView; // Linked with fx:id="tableView"

	@FXML
	private TableColumn<Inventory, String> inventoryIdColumn;

	@FXML
	private TableColumn<Inventory, String> quantityColumn;

	@FXML
	private TableColumn<Inventory, String> nameColumn;

	@FXML
	private Button insertButton;
	@FXML
	private Button updateButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button viewButton;
	@FXML
	private Button searchButton;

	@FXML
	private Button backButton;

	private ObservableList<Inventory> inventoryData;
	private ObservableList<Inventory> inventoryList = FXCollections.observableArrayList();

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	@FXML
	public void initialize() {
		inventoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("inventoryItemId"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		inventoryData = FXCollections.observableArrayList();
		tableView.setItems(inventoryList);
	}

//////////////////////////////////////////////////////////////////////////////////////////////
// methode to view data in a table view 	
	@FXML
	private void view() {
		String query = "SELECT * FROM inventory_item";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			// مسح البيانات الحالية من TableView
			tableView.getItems().clear();

			// تعبئة البيانات من قاعدة البيانات إلى TableView
			while (rs.next()) {
				Inventory inventory = new Inventory(rs.getInt("inventory_item_id"), rs.getInt("quantity"),
						rs.getString("name"));
				tableView.getItems().add(inventory);
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "loading data from database was error !"); // عرض رسالة خطأ عند حدوث استثناء
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void insert() {
		// الحصول على القيم المدخلة من الحقول النصية
		String inventoryIdText = inventoryIdField.getText();
		String quantityText = quantityField.getText();
		String nameText = nameField.getText();

		// التحقق من أن جميع الحقول غير فارغة
		if (inventoryIdText.isEmpty() || quantityText.isEmpty() || nameText.isEmpty()) {
			showAlert("Error", "All fields must be filled.");
			return;
		}

		// التحقق من أن الـ inventoryId هو رقم صحيح
		int inventoryId = 0;
		try {
			inventoryId = Integer.parseInt(inventoryIdText);
		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid Inventory ID. Please enter a valid integer.");
			return;
		}

		// التحقق من أن الـ quantity هو رقم صحيح
		int quantity = 0;
		try {
			quantity = Integer.parseInt(quantityText);
		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid Quantity. Please enter a valid integer.");
			return;
		}

		if (!nameText.matches("[a-zA-Z\\s]+")) { // يسمح فقط بالحروف الأبجدية والمسافات
			showAlert("Error", "Invalid Name. Please enter a valid name (only letters and spaces are allowed).");
			return;
		}

		// استعلام SQL لإدخال البيانات
		String query = "INSERT INTO inventory_item (inventory_item_id, quantity, name) VALUES (?, ?, ?)";

		// التحقق مما إذا كان الـ inventoryId موجود بالفعل
		String checkQuery = "SELECT COUNT(*) FROM inventory_item WHERE inventory_item_id = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

			checkStmt.setInt(1, inventoryId);
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				showAlert("Error", "Inventory ID already exists. Please choose a different ID.");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Error checking inventory ID.");
			return;
		}

		// تنفيذ الاستعلام لإدخال البيانات
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, inventoryId); // Set inventory_id
			stmt.setInt(2, quantity); // Set quantity
			stmt.setString(3, nameText); // Set name

			// تنفيذ الاستعلام
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				// تحديث البيانات في الجدول بعد الإدخال
				Inventory newInventory = new Inventory(inventoryId, quantity, nameText);
				tableView.getItems().add(newInventory);
				showAlert("Success", "Inventory item added successfully.");
			} else {
				showAlert("Error", "Failed to add inventory item.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Error inserting data into database.");
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void update() {
		// التحقق من صحة المدخلات
		String inventoryId = inventoryIdField.getText();
		String quantity = quantityField.getText();
		String name = nameField.getText();

		if (inventoryId.isEmpty() || quantity.isEmpty() || name.isEmpty()) {
			showAlert("Validation Error", "Please fill in all fields.");
			return;
		}

		int id = 0;
		try {
			id = Integer.parseInt(inventoryId); // تحويل الـ ID إلى عدد صحيح
		} catch (NumberFormatException e) {
			showAlert("Validation Error", "Inventory ID must be a valid integer.");
			return;
		}

		int qty = 0;
		try {
			qty = Integer.parseInt(quantity); // تحويل الكمية إلى عدد صحيح
		} catch (NumberFormatException e) {
			showAlert("Validation Error", "Quantity must be a valid integer.");
			return;
		}

		// التحقق من صحة الاسم (يجب أن يكون نصًا فقط)
		if (!name.matches("[a-zA-Z]+")) {
			showAlert("Validation Error", "Name must be a valid string.");
			return;
		}

		// رسالة تأكيد قبل تنفيذ التحديث
		Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Update");
		confirmationAlert.setHeaderText("Are you sure you want to update the inventory item?");
		confirmationAlert.setContentText("Inventory ID: " + id + "\nName: " + name + "\nQuantity: " + qty);

		Optional<ButtonType> result = confirmationAlert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			// SQL لتحديث السجل في قاعدة البيانات
			String updateQuery = "UPDATE inventory_item SET quantity = ?, name = ? WHERE inventory_item_id = ?";

			try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

				stmt.setInt(1, qty); // تعيين الكمية
				stmt.setString(2, name); // تعيين الاسم
				stmt.setInt(3, id); // تعيين الـ ID

				// تنفيذ التحديث
				int rowsUpdated = stmt.executeUpdate();

				if (rowsUpdated > 0) {
					// تحديث البيانات في TableView
					for (Inventory inventory : tableView.getItems()) {
						if (inventory.getInventoryItemId() == id) {
							inventory.setQuantity(qty); // تحديث الكمية
							inventory.setName(name); // تحديث الاسم
							break;
						}
					}
					// عرض التحديث في TableView
					tableView.refresh();
					showAlert("Success", "Inventory updated successfully.");
				} else {
					showAlert("Error", "No inventory found with ID: " + id);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Error", "Failed to update inventory.");
			}
		} else {
			showAlert("Update Cancelled", "Inventory update has been cancelled.");
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void delete() {
		// الحصول على العنصر المحدد من الـ TableView
		Inventory selectedInventory = tableView.getSelectionModel().getSelectedItem();

		if (selectedInventory == null) {
			showAlert("Error", "Please select an inventory item to delete.");
			return;
		}

		// الحصول على الـ ID الخاص بالعنصر المحدد
		int id = selectedInventory.getInventoryItemId();

		// رسالة تأكيد قبل الحذف
		Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Delete");
		confirmationAlert.setHeaderText("Are you sure you want to delete the selected inventory item?");
		confirmationAlert.setContentText("Inventory ID: " + id + "\nName: " + selectedInventory.getName());

		Optional<ButtonType> result = confirmationAlert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			// SQL لحذف السجل من قاعدة البيانات
			String deleteQuery = "DELETE FROM inventory_item WHERE inventory_item_id = ?";

			try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

				stmt.setInt(1, id); // تعيين الـ ID الذي سيتم حذفه

				// تنفيذ الحذف
				int rowsDeleted = stmt.executeUpdate();

				if (rowsDeleted > 0) {
					// حذف العنصر من TableView
					tableView.getItems().remove(selectedInventory);

					// عرض رسالة نجاح
					showAlert("Success", "Inventory item deleted successfully.");
				} else {
					showAlert("Error", "No inventory found with ID: " + id);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Error", "Failed to delete inventory item.");
			}
		} else {
			showAlert("Delete Cancelled", "Inventory deletion has been cancelled.");
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void search() {
		// الحصول على المدخلات
		String idInput = inventoryIdField.getText().trim();
		String nameInput = nameField.getText().trim();

		// التأكد من أن المدخلات ليست فارغة
		if (idInput.isEmpty() && nameInput.isEmpty()) {
			showAlert("Error", "Please enter a valid ID or Name to search.");
			return;
		}
		// تحقق إذا كان الاسم يحتوي على نص فقط
		if (!nameInput.isEmpty() && !nameInput.matches("[a-zA-Z\\s]+")) {
			showAlert("Invalid Input", "Please enter a valid name (only letters and spaces are allowed).");
			return;
		}

		// إنشاء استعلام SQL
		String query = "SELECT * FROM inventory_item WHERE ";

		// إذا كان يوجد ID في المدخلات
		if (!idInput.isEmpty()) {
			// التأكد من أن الـ ID هو عدد صحيح
			try {
				int id = Integer.parseInt(idInput);
				query += "inventory_item_id = " + id;
			} catch (NumberFormatException e) {
				showAlert("Invalid Input", "Please enter a valid numeric ID.");
				return;
			}
		}

		if (!nameInput.isEmpty()) {
			if (!idInput.isEmpty()) {
				query += " AND ";
			}
			query += "name LIKE '%" + nameInput + "%'";
		}

		// تنفيذ الاستعلام
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			// مسح البيانات القديمة من الـ TableView
			tableView.getItems().clear();

			// تعبئة البيانات من قاعدة البيانات إلى الـ TableView
			boolean found = false;
			while (rs.next()) {
				Inventory inventory = new Inventory(rs.getInt("inventory_item_id"), rs.getInt("quantity"),
						rs.getString("name"));
				tableView.getItems().add(inventory);
				found = true;
			}

			if (!found) {
				showAlert("No Results", "No inventory item found matching your search.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while searching for inventory.");
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////

	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
//----------------------------------------------------------------------------------
	@FXML
	private void handleBackButtonAction() {
	    // الحصول على النافذة الحالية من خلال الزر
	    Stage stage = (Stage) backButton.getScene().getWindow();
	    // إغلاق النافذة
	    stage.close();
	}

}