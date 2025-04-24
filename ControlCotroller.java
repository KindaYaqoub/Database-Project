package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ControlCotroller {

	@FXML
	private Button EmployeeManamer;

	@FXML
	private Button CustomerManager;

	@FXML
	private Button MenueItemManager;

	@FXML
	private Button SupplerisManager;
//
	@FXML
	private Button OrderManager;

	@FXML
	private Button EmployeeMenuManager;

	@FXML
	private Button AddressManager;
	@FXML
	private Button BranchManager;
	@FXML
	private Button DeliveryManager;

	@FXML
	private Button PymentMethodManager;

	@FXML
	private Button InventoryManager;

	@FXML
	private Button StatisticsManager;
	@FXML
	private Button Table1Manager;

	@FXML
	private Label label;

	@FXML
	private ImageView imageView;

	@FXML
	private void initialize() {
		EmployeeManamer.setOnAction(event -> handleEmployeeManagerButtonAction());
	}

	@FXML
	private void handleEmployeeManagerButtonAction() {
		try {
			if (EmployeeManamer != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SampleEmployee.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) EmployeeManamer.getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} else {
				System.out.println("Button 'EmployeeManamer' is not initialized properly.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------------------------------------
	@FXML
	private void handleCustomerManagerButtonAction() {
		try {
			if (EmployeeManamer != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SampleCustomer.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) EmployeeManamer.getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} else {
				System.out.println("Button 'EmployeeManamer' is not initialized properly.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleMenuItemButtonAction() {
		try {
			if (EmployeeManamer != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Sample.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) EmployeeManamer.getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} else {
				System.out.println("Button 'EmployeeManamer' is not initialized properly.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//-----------------------------------------------------------------------------------------------
	@FXML
	private void handleSuppliersButtonAction() {
		try {
			if (SupplerisManager != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Suppliers.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) SupplerisManager.getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} else {
				System.out.println("Button 'Suppliers Manager' is not initialized properly.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//---------------------------------------------------------------------------------------------------
	@FXML
	private void handleOrderManagerButtonAction() {
		try {
			if (OrderManager != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SampleOrder.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) OrderManager.getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} else {
				System.out.println("Button 'Order Manager' is not initialized properly.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleMenuManagerButtonAction() {
		try {
			if (EmployeeMenuManager != null) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Sample.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) EmployeeMenuManager.getScene().getWindow();
				stage.setScene(scene);
				stage.show();
			} else {
				System.out.println("Button 'Menu Manager' is not initialized properly.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------------------------------
	@FXML
	private void handleAddressManagerButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Address.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleBranchManagerButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Branch.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//---------------------------------------------------------------------------------------------
	@FXML
	private void handleDeliveryManagerButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Delivery.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//--------------------------------------------------------------------------------------------
	@FXML
	private void handlePaymentManagerButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SamplePymenrMethode.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

///-------------------------------------------------------------------------------------------------------
	@FXML
	private void handleStatisticsManagerButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Statistics.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//---------------------------------------------------------------------------------------------------
	@FXML
	private void handleInventoryManagerButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Inventory.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setTitle("Inventory Manager");
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//-----------------------------------------------------------------------------------------------------
	@FXML
	private void handleTable1ButtonAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Table1.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage newStage = new Stage();
			newStage.setTitle("Table 1");
			newStage.setScene(scene);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
