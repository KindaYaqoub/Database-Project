package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class SuppliersController {

	@FXML
	private TableView<Supplier> tableView;
	@FXML
	private TableColumn<Supplier, Integer> supplierIdColumn;
	@FXML
	private TableColumn<Supplier, String> nameColumn;
	@FXML
	private TableColumn<Supplier, String> contactInfoColumn;
	@FXML
	private TableColumn<Supplier, Integer> addressColumn;
	@FXML
	private TableColumn<Supplier, String> informationColumn;

	@FXML
	private TextField supplierIdField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField contactInfoField;
	@FXML
	private TextField informationField;

	@FXML
	private ComboBox<Address> addressComboBox; // ComboBox to hold addresses
	@FXML
	private Button insertButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button updateButton;
	@FXML
	private Button searchButton;
	@FXML
	private Button viewButton;
	@FXML
	private Button addNewAddress;
	@FXML
	private Button backButton; // This is the back button

	private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
	private static final String USER = "root";
	private static final String PASSWORD = "1221893";

	private ObservableList<Supplier> suppliersList = FXCollections.observableArrayList();
	private ObservableList<Address> addressList = FXCollections.observableArrayList(); // ObservableList for Address

	@FXML
	public void initialize() {
		// Set up table columns
		supplierIdColumn.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
		informationColumn.setCellValueFactory(new PropertyValueFactory<>("information"));

		// Load addresses from the database and populate the ComboBox
		loadAddressesFromDatabase();
		addressComboBox.setItems(addressList);
	}

	// ------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------------
	@FXML
	private void insertSupplier() {
		String name = nameField.getText();
		String contactInfo = contactInfoField.getText();
		Address selectedAddress = addressComboBox.getValue(); // Get selected address from ComboBox
		String information = informationField.getText();
		String supplierIdInput = supplierIdField.getText(); // Ensure supplier ID field is entered

		// تحقق من أنه تم ملء جميع الحقول
		if (name.isEmpty() || contactInfo.isEmpty() || selectedAddress == null || information.isEmpty()
				|| supplierIdInput.isEmpty()) {
			showAlert("Input Error", "Please fill all fields", "All fields must be filled in.");
			return;
		}

		// التحقق من أن ID المورد هو عدد صحيح
		int supplierId = -1;
		try {
			supplierId = Integer.parseInt(supplierIdInput); // تحويل النص إلى عدد صحيح
		} catch (NumberFormatException e) {
			showAlert("Invalid Input", "Supplier ID must be an integer",
					"Please enter a valid integer for Supplier ID.");
			return;
		}

		int addressId = selectedAddress.getAddressId(); // Get addressId from selected Address

		// التحقق مما إذا كان الـ supplierId موجودًا بالفعل
		if (isSupplierIdExists(supplierId)) {
			showAlert("ID Error", "Supplier ID already exists",
					"The supplier ID is already in use. Please choose a different ID.");
			return;
		}

		// التحقق من أن contact_info فريد
		if (isContactInfoExists(contactInfo)) {
			showAlert("Contact Info Error", "Contact Info already exists",
					"The contact info is already in use. Please choose a different contact info.");
			return;
		}

		// عرض مربع تأكيد
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Insertion");
		alert.setHeaderText("Are you sure you want to add this supplier?");
		alert.setContentText("Supplier ID: " + supplierId + "\nName: " + name + "\nContact Info: " + contactInfo);

		// الانتظار لرد المستخدم
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			String query = "INSERT INTO supplier (supplier_id, name, contact_info, address_id, information) VALUES (?, ?, ?, ?, ?)";

			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement pstmt = connection.prepareStatement(query)) {

				pstmt.setInt(1, supplierId); // Set supplierId
				pstmt.setString(2, name);
				pstmt.setString(3, contactInfo);
				pstmt.setInt(4, addressId); // Set addressId from ComboBox selection
				pstmt.setString(5, information);

				pstmt.executeUpdate();
				showAlert("Success", "Supplier Inserted", "Supplier has been successfully inserted.");
				loadSuppliersFromDatabase(); // Reload suppliers after insertion

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Database Error", "Error inserting supplier",
						"An error occurred while inserting the supplier.");
			}
		} else {
			// إذا اختار المستخدم إلغاء العملية
			showAlert("Cancelled", "Supplier Insertion Cancelled", "The supplier insertion was cancelled.");
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void updateSupplier() {
		String supplierIdInput = supplierIdField.getText();
		String name = nameField.getText();
		String contactInfo = contactInfoField.getText();
		Address selectedAddress = addressComboBox.getValue(); // Get selected address from ComboBox
		String information = informationField.getText();

		// التحقق من أن جميع الحقول قد تم ملؤها
		if (name.isEmpty() || contactInfo.isEmpty() || selectedAddress == null || information.isEmpty()
				|| supplierIdInput.isEmpty()) {
			showAlert("Input Error", "Please fill all fields", "All fields must be filled in.");
			return;
		}

		// التحقق من أن الـ supplierId هو عدد صحيح
		int supplierId = -1;
		try {
			supplierId = Integer.parseInt(supplierIdInput); // تحويل النص إلى عدد صحيح
		} catch (NumberFormatException e) {
			showAlert("Invalid Input", "Supplier ID must be an integer",
					"Please enter a valid integer for Supplier ID.");
			return;
		}

		// التحقق من أن الـ supplierId موجود في قاعدة البيانات
		if (!isSupplierIdExists(supplierId)) {
			showAlert("ID Error", "Supplier ID not found", "The Supplier ID does not exist in the database.");
			return;
		}

		int addressId = selectedAddress.getAddressId(); // Get addressId from selected Address

		// التحقق من أن contact_info فريد
		if (isContactInfoExists(contactInfo)) {
			showAlert("Contact Info Error", "Contact Info already exists",
					"The contact info is already in use. Please choose a different contact info.");
			return;
		}

		// عرض مربع تأكيد
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Update");
		alert.setHeaderText("Are you sure you want to update this supplier?");
		alert.setContentText("Supplier ID: " + supplierId + "\nName: " + name + "\nContact Info: " + contactInfo);

		// الانتظار لرد المستخدم
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			String query = "UPDATE supplier SET name = ?, contact_info = ?, address_id = ?, information = ? WHERE supplier_id = ?";

			try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
					PreparedStatement pstmt = connection.prepareStatement(query)) {

				pstmt.setString(1, name);
				pstmt.setString(2, contactInfo);
				pstmt.setInt(3, addressId); // Set addressId from ComboBox selection
				pstmt.setString(4, information);
				pstmt.setInt(5, supplierId); // Set supplierId

				pstmt.executeUpdate();
				showAlert("Success", "Supplier Updated", "Supplier has been successfully updated.");
				loadSuppliersFromDatabase(); // Reload suppliers after update

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Database Error", "Error updating supplier",
						"An error occurred while updating the supplier.");
			}
		} else {
			// إذا اختار المستخدم إلغاء العملية
			showAlert("Cancelled", "Supplier Update Cancelled", "The supplier update was cancelled.");
		}
	}

//--------------------------------------------------------------------------------------
	// Method for deleting a supplier
	@FXML
	private void deleteSupplier() {
		Supplier selectedSupplier = tableView.getSelectionModel().getSelectedItem();
		if (selectedSupplier == null) {
			showAlert("Selection Error", "No Supplier Selected", "Please select a supplier to delete.");
			return;
		}

		String query = "DELETE FROM supplier WHERE supplier_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setInt(1, selectedSupplier.getSupplierId());
			pstmt.executeUpdate();
			showAlert("Success", "Supplier Deleted", "Supplier has been successfully deleted.");
			loadSuppliersFromDatabase(); // Reload suppliers list after deletion

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Database Error", "Error deleting supplier", "An error occurred while deleting the supplier.");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void handleSearchButton() {
		// الحصول على Supplier ID من حقل النص
		String supplierId = supplierIdField.getText();

		// الحصول على الـ addressId من الـ ComboBox
		Address selectedAddress = addressComboBox.getSelectionModel().getSelectedItem();
		String addressId = selectedAddress != null ? String.valueOf(selectedAddress.getAddressId()) : "";

		suppliersList.clear();

		if (supplierId.isEmpty() && addressId.isEmpty()) {
			showAlert("Error", "Please enter either a Supplier ID or select an Address ID to search.",
					Alert.AlertType.ERROR);
			return;
		}

		// بناء الاستعلام بناءً على المدخلات
		String query = "SELECT * FROM supplier WHERE ";
		boolean hasCondition = false;

		if (!supplierId.isEmpty()) {
			query += "supplier_id = ?";
			hasCondition = true;
		}

		if (!addressId.isEmpty()) {
			if (hasCondition) {
				query += " AND ";
			}
			query += "address_id = ?";
		}

		// تنفيذ الاستعلام
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			// تعيين المعاملات في الاستعلام
			int paramIndex = 1;

			if (!supplierId.isEmpty()) {
				preparedStatement.setInt(paramIndex++, Integer.parseInt(supplierId));
			}

			if (!addressId.isEmpty()) {
				preparedStatement.setInt(paramIndex, Integer.parseInt(addressId));
			}

			// تنفيذ الاستعلام وجلب النتائج
			ResultSet resultSet = preparedStatement.executeQuery();

			// إذا لم يتم العثور على نتائج
			if (!resultSet.next()) {
				showAlert("Not Found", "No supplier found with the given criteria.", Alert.AlertType.INFORMATION);
				return;
			}

			// إضافة النتائج إلى TableView
			do {
				int foundSupplierId = resultSet.getInt("supplier_id");
				String name = resultSet.getString("name");
				String contactInfo = resultSet.getString("contact_info");
				String information = resultSet.getString("information");
				int foundAddressId = resultSet.getInt("address_id");

				// إضافة الموردين إلى الـ ObservableList
				suppliersList.add(new Supplier(foundSupplierId, name, contactInfo, foundAddressId, information));
			} while (resultSet.next());

		} catch (SQLException e) {
			showAlert("Error", "Error searching for supplier: " + e.getMessage(), Alert.AlertType.ERROR);
		} catch (NumberFormatException e) {
			showAlert("Error", "Please enter valid numeric values for Supplier ID.", Alert.AlertType.ERROR);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////
	// Method to check if the contact info already exists in the database
	private boolean isContactInfoExists(String contactInfo) {
		String query = "SELECT COUNT(*) FROM supplier WHERE contact_info = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, contactInfo);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0; // If the count is greater than 0, the contact info exists
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; // If the contact info is not found
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private void loadSuppliersFromDatabase() {
		String query = "SELECT supplier_id, name, contact_info, address_id, information FROM supplier";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			suppliersList.clear(); // مسح القائمة الحالية للموردين

			while (rs.next()) {
				int supplierId = rs.getInt("supplier_id");
				String name = rs.getString("name");
				String contactInfo = rs.getString("contact_info");
				int addressId = rs.getInt("address_id"); // استخدام addressId كـ int
				String information = rs.getString("information");

				// إنشاء كائن Supplier مع addressId فقط
				Supplier supplier = new Supplier(supplierId, name, contactInfo, addressId, information);
				suppliersList.add(supplier);
			}

			// تحديث TableView لعرض الموردين
			tableView.setItems(suppliersList);

		} catch (SQLException e) {
			showAlert("Database Error", "Error loading suppliers",
					"An error occurred while loading suppliers from the database.");
			e.printStackTrace();
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void loadAddressesFromDatabase() {
		String query = "SELECT address_id, street, city FROM address"; // Query to fetch all addresses

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				int addressId = rs.getInt("address_id");
				String street = rs.getString("street");
				String city = rs.getString("city");
				Address address = new Address(addressId, street, city); // Create Address object
				addressList.add(address); // Add Address to the list
			}

			addressComboBox.setItems(addressList); // Set the addresses in the ComboBox

		} catch (SQLException e) {
			e.printStackTrace(); // Handle database connection errors
		}
	}

	// ------------------------------------------------------------------------------------------------------

	private boolean isSupplierIdExists(int supplierId) {
		String checkQuery = "SELECT COUNT(*) FROM supplier WHERE supplier_id = ?";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {

			pstmt.setInt(1, supplierId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

//------------------------------------------------------------------------------------------------------

	public void showAlert(String title, String header, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.showAndWait();
	}

//-----------------------------------------------------
	@FXML
	public void addNewAddress(ActionEvent event) {
		try {
			// تحميل ملف FXML الخاص بواجهة العناوين
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Address.fxml"));
			Parent root = fxmlLoader.load();

			// إنشاء نافذة جديدة (Stage)
			Stage stage = new Stage();
			stage.setTitle("Address Manager");
			stage.setScene(new Scene(root));

			// ضبط النافذة الجديدة كنافذة مؤقتة
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			// تحديث قائمة العناوين
			updateAddressComboBox();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

//---------------------------------------------------------------------------------------------

	@FXML
	private void handleBackButtonAction() {
		try {
			// تحميل واجهة SamplePassword.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SamplePassword.fxml"));

			// الحصول على النافذة الحالية من الـ backButton
			Stage stage = (Stage) backButton.getScene().getWindow();
			// تغيير الـ Scene إلى الـ SamplePassword
			stage.setScene(new Scene(loader.load()));
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to load the previous screen: " + e.getMessage());
		}
	}

	// ميثود لعرض التنبيه في حالة حدوث خطأ
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title); // العنوان
		alert.setHeaderText(null); // لا يوجد عنوان فرعي
		alert.setContentText(message); // محتوى الرسالة
		alert.showAndWait(); // عرض التنبيه وانتظار تفاعل المستخدم
	}

}
 