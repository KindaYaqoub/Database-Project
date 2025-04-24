package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class SampleController {

	@FXML
	private TextField nameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button enterButton;

	private int attemptCount = 0; // Counter for login attempts
	private final int MAX_ATTEMPTS = 3; // Maximum allowed attempts

	@FXML
	private void initialize() {
		enterButton.setOnAction(event -> handleEntertocontrolsystemAction());
	}

	// Handle login and navigation
	@FXML
	private void handleEntertocontrolsystemAction() {
		if (validateBeforeOpening()) {
			showAlert("Success", "Login successful!");
			openNewScreen("/application/SamplePassword.fxml");
		} else {
			attemptCount++;
			if (attemptCount >= MAX_ATTEMPTS) {
				handleMaxAttempts();
			} else {
				int remainingAttempts = MAX_ATTEMPTS - attemptCount;
				showAlert("Error", "Incorrect username or password! Remaining attempts: " + remainingAttempts);
			}
		}
	}

	// Method to disable the button and show alert after max attempts
	private void handleMaxAttempts() {
		showAlert("Error", "You have exceeded the maximum number of attempts. Please try again after 10 second!");
		disableFieldsForTimeout();
	}

	// Disable fields for 10 seconds
	private void disableFieldsForTimeout() {
	    nameField.setDisable(true);
	    passwordField.setDisable(true);
	    enterButton.setDisable(true);

	    Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	        @Override
	        public void run() {
	            // Re-enable fields after 10 seconds
	            nameField.setDisable(false);
	            passwordField.setDisable(false);
	            enterButton.setDisable(false);
	            attemptCount = 0; // Reset attempt count
	        }
	    }, 10 * 1000); // 10 seconds in milliseconds
	}

	// Validate login inputs
	private boolean validateBeforeOpening() {
		String enteredName = nameField.getText().trim();
		String enteredPassword = passwordField.getText().trim();

		if (enteredName.isEmpty()) {
			showAlert("Error", "Username cannot be empty!");
			return false;
		}

		if (enteredPassword.isEmpty()) {
			showAlert("Error", "Password cannot be empty!");
			return false;
		}

		if (!isValidUser(enteredName, enteredPassword)) {
			return false;
		}

		return true;
	}

	// Check if the username and password are correct
	private boolean isValidUser(String enteredName, String enteredPassword) {
		String correctName = "أسامة حمدان";
		String correctPassword = "123@@osama";

		return enteredName.equals(correctName) && enteredPassword.equals(correctPassword);
	}

	// Show alert messages
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Open a new screen
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
}
