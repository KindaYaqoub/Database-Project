package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class StatController {

	@FXML
	private Label statisticsLabel;

	@FXML
	private Button totalSalesButton;

	@FXML
	private Button totalSalesforeachbranchButton;

	@FXML
	private Button paymentMethodsDistributionButton; // تعريف الـ Button باستخدام الـ fx:id

	@FXML
	private Button totalOrderButton; // تعريف الـ Button باستخدام الـ fx:id

	@FXML
	private Button InventoerButton; // تعريف الـ Button باستخدام الـ fx:id

	@FXML
	private Button BranchNumButton; // تعريف الـ Button باستخدام الـ fx:id
	

	@FXML
	private Button BackButton; // تعريف الـ Button باستخدام الـ fx:id
	@FXML
	private Button TwoBranch; // تعريف الـ Button باستخدام الـ fx:id

	
	@FXML
	private ImageView statisticsImage;

	// يتم استدعاؤه عند النقر على زر "Total sales"
	@FXML
	private void handleTotalSalesButtonAction() {
		try {
			// تحميل واجهة PieChart
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/PieChart.fxml"));
			Parent root = loader.load();

			// إنشاء نافذة جديدة للـ PieChart
			Stage stage = new Stage();
			stage.setTitle("Total Sales Pie Chart");
			stage.setScene(new Scene(root));
			stage.show();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//--------------------------------------------------------------------------------------------	
	@FXML
	private void handleTotalSalesForEachBranchButtonAction() {
		try {
			// تحميل واجهة PieChart الخاصة بالمبيعات لكل فرع
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/StatTotalbranch.fxml"));
			Parent root = loader.load();

			// إنشاء نافذة جديدة للـ PieChart
			Stage stage = new Stage();
			stage.setTitle("Total Sales for Each Branch");
			stage.setScene(new Scene(root));
			stage.show();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//---------------------------------------------------------------------------------------------------
	@FXML
	private void handlePaymentMethodsDistributionButtonAction() {
		try {
			// تحميل واجهة PieChart الخاصة بتوزيع طرق الدفع
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/TotalPyments.fxml"));
			Parent root = loader.load();

			// إنشاء نافذة جديدة للـ PieChart
			Stage stage = new Stage();
			stage.setTitle("Payment Methods Distribution");
			stage.setScene(new Scene(root));
			stage.show();

			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//--------------------------------------------------------------------------------------------------
	@FXML
	private void handleTotalOrderButtonAction() {
	    try {
	        // تحميل واجهة TotalOrder.fxml
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/SampleOrder.fxml"));
	        Parent root = loader.load();

	        // إنشاء نافذة جديدة
	        Stage stage = new Stage();
	        stage.setTitle("Total Orders Distribution");
	        stage.setScene(new Scene(root));
	        stage.show();

	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
//------------------------------------------------------------------------------------------------------
	@FXML
	private void handleBranchStatButtonAction() {
	    try {
	        // تحميل واجهة BranchStat.fxml
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/BranchNum.fxml"));
	        Parent root = loader.load();

	        // إنشاء نافذة جديدة
	        Stage stage = new Stage();
	        stage.setTitle("Branch Statistics");
	        stage.setScene(new Scene(root));
	        stage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
//-----------------------------------------------------------------------------------------------------
	@FXML
	private void handleInventoryButtonAction() {
	    try {
	        // تحميل واجهة InventoryStat.fxml
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/InventorStat.fxml"));
	        Parent root = loader.load();

	        // إنشاء نافذة جديدة
	        Stage stage = new Stage();
	        stage.setTitle("Inventory Statistics");
	        stage.setScene(new Scene(root));
	        stage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
//-----------------------------------------------------------------------------------------------------
	
	@FXML
	private void handleTwoBranchButtonAction() {
	    try {
	        // تحميل واجهة TwoBranch.fxml
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/TwoBranch.fxml"));
	        Parent root = loader.load();

	        // إنشاء نافذة جديدة
	        Stage stage = new Stage();
	        stage.setTitle("Customers who ordered from both Branch 1 and Branch 2");
	        stage.setScene(new Scene(root));
	        stage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

//------------------------------------------------------------------------------------------------------	
	// يتم استدعاؤه عند تهيئة الواجهة
	@FXML
	public void initialize() {
		System.out.println("Controller initialized.");
		if (statisticsLabel != null) {
			statisticsLabel.setText("Welcome to Statistics Manager!");
		} else {
			System.out.println("statisticsLabel is null!");
		}
	}
//------------------------------------------------------------------
	@FXML
	private void handleBackButtonAction(ActionEvent event) {
	    // الحصول على النافذة الحالية
	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    // إغلاق النافذة
	    stage.close();
	}

}
