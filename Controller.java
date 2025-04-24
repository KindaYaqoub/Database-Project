package application;

import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Controller {

	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/Resturent";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "1221893";
	private Connection connection;

	@FXML
	private TableView<Employee> tableView;

	@FXML
	private TableColumn<Employee, Integer> empIdColumn;
	@FXML
	private TableColumn<Employee, String> contactInfoColumn;
	@FXML
	private TableColumn<Employee, Double> salaryColumn;
	@FXML
	private TableColumn<Employee, Integer> branchIdColumn;
	@FXML
	private TableColumn<Employee, String> positionColumn;
	@FXML
	private TableColumn<Employee, String> nameColumn;

	@FXML
	private TextField empIdField;
	@FXML
	private TextField contactInfoField;
	@FXML
	private TextField salaryField;
	@FXML
	private TextField branchIdField;
	@FXML
	private TextField positionField;
	@FXML
	private TextField nameField;

	private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		// Bind TableView columns to Employee fields
		empIdColumn.setCellValueFactory(new PropertyValueFactory<>("empId"));
		contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
		salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
		branchIdColumn.setCellValueFactory(new PropertyValueFactory<>("branchId"));
		positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		// Link employeeList to tableView
		tableView.setItems(employeeList);
	}

	@FXML
	private void handleView() {
		ObservableList<Employee> employeeList = getEmployeeData();
		tableView.setItems(employeeList);
	}

	// استرجاع البيانات من قاعدة البيانات
	private ObservableList<Employee> getEmployeeData() {
		ObservableList<Employee> employeeList = FXCollections.observableArrayList();

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				Statement statement = connection.createStatement()) {

			String query = "SELECT * FROM employee";
			ResultSet resultSet = statement.executeQuery(query);

			// قراءة البيانات من ResultSet وإضافتها إلى القائمة
			while (resultSet.next()) {
				int empId = resultSet.getInt("emp_id");
				String contactInfo = resultSet.getString("contactInfo");
				String position = resultSet.getString("position");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				int branchId = resultSet.getInt("branch_id");

				// إنشاء كائن Employee وإضافته إلى القائمة
				Employee employee = new Employee(empId, contactInfo, salary, branchId, position, name);
				employeeList.add(employee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeeList;
	}

	// ------------------------------------------------------------------------------------------------
	@FXML
	private void handleInsert() {
		try {
			// Get the input from the TextFields
			int empId = Integer.parseInt(empIdField.getText());
			String contactInfo = contactInfoField.getText();
			double salary = Double.parseDouble(salaryField.getText());
			int branchId = Integer.parseInt(branchIdField.getText());
			String position = positionField.getText();
			String name = nameField.getText();

			// Check if any field is empty
			if (contactInfo.isEmpty() || position.isEmpty() || name.isEmpty()) {
				showAlert("Error", "Empty Fields", "Please fill in all fields.");
				return;
			}

			// Check if the empId already exists in the database
			if (isEmpIdExists(empId)) {
				showAlert("Error", "Duplicate Employee ID",
						"The Employee ID already exists. Please enter a unique ID.");
				return;
			}

			// Check if branchId exists in the branch table
			if (!isValidBranchId(branchId)) {
				showAlert("Error", "Invalid Branch ID", "The branch ID you entered does not exist.");
				return;
			}

			// Create a new Employee object
			Employee newEmployee = new Employee(empId, contactInfo, salary, branchId, position, name);

			// Insert the employee into the database
			String insertSQL = "INSERT INTO employee (emp_id, contactInfo, salary, branch_id, position, name) VALUES (?, ?, ?, ?, ?, ?)";

			try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
					PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
				stmt.setInt(1, empId);
				stmt.setString(2, contactInfo);
				stmt.setDouble(3, salary);
				stmt.setInt(4, branchId);
				stmt.setString(5, position);
				stmt.setString(6, name);
				int rowsAffected = stmt.executeUpdate();

				if (rowsAffected > 0) {
					// Add to employee list
					employeeList.add(newEmployee); // Add the new employee to the list

					// Update TableView automatically
					updateTableView(); // Call updateTableView to refresh the TableView

					showAlert("Success", "Employee Added", "The employee has been added successfully.");
				} else {
					showAlert("Error", "Insertion Failed", "The employee could not be added.");
				}
			} catch (SQLException e) {
				showAlert("Error", "Database Error", "An error occurred while inserting the employee.");
				e.printStackTrace();
			}

			// Clear the input fields after insertion
			clearFields();

		} catch (NumberFormatException e) {
			showAlert("Error", "Invalid Input", "Please enter valid values.");
		}
	}

	// Method to update the TableView after adding a new employee
	private void updateTableView() {
		tableView.setItems(employeeList); // Refresh the TableView with the updated list
	}

	// Method to check if Employee ID already exists in the database
	private boolean isEmpIdExists(int empId) {
		boolean exists = false;
		String checkEmpIdSQL = "SELECT COUNT(*) FROM employee WHERE emp_id = ?";

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				PreparedStatement stmt = connection.prepareStatement(checkEmpIdSQL)) {
			stmt.setInt(1, empId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				exists = true;
			}
		} catch (SQLException e) {
			showAlert("Error", "Database Error", "An error occurred while checking the Employee ID.");
			e.printStackTrace();
		}

		return exists;
	}

	// Method to validate Branch ID
	private boolean isValidBranchId(int branchId) {
		boolean exists = false;
		String checkBranchSQL = "SELECT COUNT(*) FROM branch WHERE branch_id = ?";

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				PreparedStatement stmt = connection.prepareStatement(checkBranchSQL)) {
			stmt.setInt(1, branchId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				exists = true; // Branch ID exists in the database
			}
		} catch (SQLException e) {
			showAlert("Error", "Database Error", "An error occurred while checking the branch ID.");
			e.printStackTrace();
		}

		return exists; // Returns true if the branch ID exists, false otherwise
	}

	// -----------------------------------------------------------------------------------------------
	@FXML
	public void handleDelete() {
		Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();

		if (selectedEmployee == null) {
			showAlert("Warning", "No Employee Selected", "Please select an employee to delete.");
			return;
		}

		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Delete Confirmation");
		confirmAlert.setHeaderText(null);
		confirmAlert.setContentText("Are you sure you want to delete this employee?");

		if (confirmAlert.showAndWait().get() == ButtonType.OK) {
			boolean isDeleted = deleteEmployeeFromDatabase(selectedEmployee.getEmpId());

			if (isDeleted) {
				tableView.getItems().remove(selectedEmployee);

				showAlert("Success", "Employee Deleted", "The employee has been successfully deleted.");
			} else {
				showAlert("Error", "Deletion Failed", "The employee could not be deleted.");
			}
		}
	}

	// Method to delete employee from the database
	private boolean deleteEmployeeFromDatabase(int empId) {
		String deleteSQL = "DELETE FROM employee WHERE emp_id = ?";

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
			stmt.setInt(1, empId);
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			showAlert("Error", "Database Error", "An error occurred while deleting the employee.");
			e.printStackTrace();
			return false;
		}
	}

	// ---------------------------------------------------------------------------------------------------------
	@FXML
	private void handleUpdate() {
		int searchId;
		try {
			// استخراج رقم العامل من الـ empIdField
			searchId = Integer.parseInt(empIdField.getText().trim());
		} catch (NumberFormatException e) {
			showAlert("Input Error", "Invalid Employee ID", "Please enter a valid Employee ID.");
			return;
		}

		// الحصول على البيانات من الـ TextFields
		String contactInfo = contactInfoField.getText().trim();
		String position = positionField.getText().trim();
		String name = nameField.getText().trim();

		// التحقق من صحة البيانات المدخلة في الـ TextFields
		double salary;
		try {
			salary = Double.parseDouble(salaryField.getText().trim());
		} catch (NumberFormatException e) {
			showAlert("Input Error", "Invalid Salary", "Please enter a valid salary.");
			return;
		}

		int branchId;
		try {
			branchId = Integer.parseInt(branchIdField.getText().trim());
		} catch (NumberFormatException e) {
			showAlert("Input Error", "Invalid Branch ID", "Please enter a valid Branch ID.");
			return;
		}

		// البحث عن الموظف بناءً على الـ empId المدخل
		Employee employeeToUpdate = getEmployeeById(searchId);

		if (employeeToUpdate == null) {
			showAlert("Error", "Employee Not Found", "No employee found with the given ID.");
			return;
		}

		// تحديث البيانات في الكائن employee
		employeeToUpdate.setContactInfo(contactInfo);
		employeeToUpdate.setSalary(salary);
		employeeToUpdate.setBranchId(branchId);
		employeeToUpdate.setPosition(position);
		employeeToUpdate.setName(name);

		// تحديث الموظف في قاعدة البيانات
		updateEmployeeInDatabase(employeeToUpdate);

		// تحديث الـ TableView بعد التعديل
		updateEmployeeInTableView(employeeToUpdate);

		showAlert("Success", "Employee Updated", "The employee information has been updated successfully.");
	}

	private Employee getEmployeeById(int searchId) {
		String query = "SELECT * FROM employee WHERE emp_id = ?";
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				PreparedStatement stmt = connection.prepareStatement(query)) {

			// تعيين الـ empId في الاستعلام
			stmt.setInt(1, searchId);

			// تنفيذ الاستعلام
			ResultSet resultSet = stmt.executeQuery();

			// إذا تم العثور على الموظف، يتم إنشاء كائن Employee وتعبئته بالبيانات
			if (resultSet.next()) {
				String contactInfo = resultSet.getString("contactInfo");
				double salary = resultSet.getDouble("salary");
				int branchId = resultSet.getInt("branch_id");
				String position = resultSet.getString("position");
				String name = resultSet.getString("name");

				// إنشاء كائن Employee باستخدام البيانات المسترجعة
				return new Employee(searchId, contactInfo, salary, branchId, position, name);
			}

		} catch (SQLException e) {
			showAlert("Error", "Database Error", "An error occurred while retrieving employee data.");
			e.printStackTrace();
		}

		// في حالة عدم العثور على الموظف، نعيد القيمة null
		return null;
	}

	private void updateEmployeeInTableView(Employee employeeToUpdate) {
		// الحصول على السجل الموجود في الـ TableView والذي له نفس الـ empId
		for (Employee employee : employeeList) {
			if (employee.getEmpId() == employeeToUpdate.getEmpId()) {
				// تحديث البيانات في الكائن الموجود في الـ TableView
				employee.setContactInfo(employeeToUpdate.getContactInfo());
				employee.setSalary(employeeToUpdate.getSalary());
				employee.setBranchId(employeeToUpdate.getBranchId());
				employee.setPosition(employeeToUpdate.getPosition());
				employee.setName(employeeToUpdate.getName());

				// تحديث الـ TableView
				tableView.refresh(); // لتحديث العرض بعد التعديل
				break;
			}
		}
	}

	private void updateEmployeeInDatabase(Employee employeeToUpdate) {
		String updateSQL = "UPDATE employee SET contactInfo = ?, salary = ?, branch_id = ?, position = ?, name = ? WHERE emp_id = ?";

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
				PreparedStatement stmt = connection.prepareStatement(updateSQL)) {

			// تعيين القيم المدخلة في الـ TextFields إلى الـ PreparedStatement
			stmt.setString(1, employeeToUpdate.getContactInfo());
			stmt.setDouble(2, employeeToUpdate.getSalary());
			stmt.setInt(3, employeeToUpdate.getBranchId());
			stmt.setString(4, employeeToUpdate.getPosition());
			stmt.setString(5, employeeToUpdate.getName());
			stmt.setInt(6, employeeToUpdate.getEmpId());

			// تنفيذ التحديث
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				showAlert("Success", "Employee Updated", "The employee information has been updated successfully.");
			} else {
				showAlert("Error", "Update Failed", "The employee information could not be updated.");
			}

		} catch (SQLException e) {
			showAlert("Error", "Database Error", "An error occurred while updating the employee.");
			e.printStackTrace();
		}
	}

//[----------------------------------------------------------------

	@FXML
	private void handleSearch() {
		int searchId;
		try {
			// الحصول على empId من الـ TextField
			searchId = Integer.parseInt(empIdField.getText().trim());
		} catch (NumberFormatException e) {
			showAlert("Input Error", "Invalid Employee ID", "Please enter a valid Employee ID.");
			return;
		}

		// البحث عن الموظف باستخدام الـ empId
		Employee employee = getEmployeeById(searchId);

		if (employee == null) {
			showAlert("Error", "Employee Not Found", "No employee found with the given ID.");
		} else {
			// استدعاء دالة updateTableView لتحديث الـ TableView ليعرض الموظف الذي تم العثور
			// عليه فقط
			updateTableViewWithSingleEmployee(employee);

			showAlert("Success", "Employee Found", "Employee information has been retrieved successfully.");
		}
	}

	
	private void updateTableViewWithSingleEmployee(Employee employee) {
		ObservableList<Employee> currentData = FXCollections.observableArrayList(); // إنشاء قائمة جديدة لعرض الموظف فقط
		currentData.add(employee); 
		tableView.setItems(currentData); // تحديث الـ TableView لعرض الموظف فقط
	}

	// --------------------------------------------------
	
	private void clearFields() {
		empIdField.clear();
		contactInfoField.clear();
		salaryField.clear();
		branchIdField.clear();
		positionField.clear();
		nameField.clear();
	}

	
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

//------------------------------------------------------------------------------
	@FXML
	private void handleBackButtonAction(ActionEvent event) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SamplePassword.fxml"));
			Parent root = loader.load();

			
			Scene scene = new Scene(root);

			
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
