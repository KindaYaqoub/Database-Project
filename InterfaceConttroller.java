package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InterfaceConttroller {

	@FXML
	private Button customerButton;

	@FXML
	private Button managerButton;

	@FXML
	private Button enterButton;

	@FXML
	private void initialize() {
	}

	@FXML
	private void handleCustomerButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/CustomerMenue.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void openNewScreen(String fxmlFile) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to load the new screen.");
		}
	}

	@FXML
	private void handleManagerButtonAction() {
		openNewScreen("/application/SamplePassword1.fxml");
	}
}
