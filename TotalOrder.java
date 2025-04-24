package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import java.sql.*;

public class TotalOrder {

	@FXML
	private PieChart pieChart;

	@FXML
	private VBox detailsVBox; // VBox to hold the order type details

	// Method to fetch the total orders by order type and display them in a pie
	// chart
	public void initialize() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish connection (update with your DB details)
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Resturent", "root", "1221893");

			// Query to get total orders by order type
			String query = "SELECT order_type, COUNT(order_id) AS TotalOrders FROM orders GROUP BY order_type";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			// Iterate through the result set and populate the pie chart and details
			while (rs.next()) {
				String orderType = rs.getString("order_type"); // Use correct column name
				int totalOrders = rs.getInt("TotalOrders");

				// Add data to PieChart
				PieChart.Data slice = new PieChart.Data(orderType + ": " + totalOrders, totalOrders);
				pieChart.getData().add(slice);

				// Create Label for each order type's total orders and add it to VBox
				Label orderTypeLabel = new Label(orderType + ": " + totalOrders + " Orders");
				detailsVBox.getChildren().add(orderTypeLabel);

				// Optionally, add a tooltip to show orders when hovering over the slice
				Tooltip tooltip = new Tooltip("Total Orders: " + totalOrders);
				Tooltip.install(slice.getNode(), tooltip);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}