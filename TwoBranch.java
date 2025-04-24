package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import java.sql.*;

public class TwoBranch {

	@FXML
	private PieChart pieChart;

	@FXML
	private VBox detailsVBox; // VBox to hold the customer details

	// Method to fetch the customers who ordered from both branch 1 and branch 2
	public void initialize() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish connection (update with your DB details)
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Resturent", "root", "1221893");

			// Query to get customers who ordered from both branch 1 and branch 2
			String query = "SELECT customer_id, customer_name " + "FROM customer " + "WHERE customer_id IN ( "
					+ "    SELECT customer_id FROM orders WHERE branch_id = 1 " + ") " + "AND customer_id IN ( "
					+ "    SELECT customer_id FROM orders WHERE branch_id = 2 " + ");";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			// Title for the Pie Chart
			Label titleLabel = new Label("Customers who ordered from both Branch 1 and Branch 2");
			detailsVBox.getChildren().add(titleLabel);

			// Iterate through the result set and populate the pie chart and details
			while (rs.next()) {
				int customerId = rs.getInt("customer_id");
				String customerName = rs.getString("customer_name");

				// Add data to PieChart
				PieChart.Data slice = new PieChart.Data(customerName, 1); // Assigning a value of 1 for each customer
				pieChart.getData().add(slice);

				// Create Label for each customer and add it to VBox
				Label customerLabel = new Label(customerName);
				detailsVBox.getChildren().add(customerLabel);

				// Optionally, add a tooltip to show customer ID when hovering over the slice
				Tooltip tooltip = new Tooltip("Customer ID: " + customerId);
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