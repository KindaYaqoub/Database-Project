package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import java.sql.*;

public class StatController2 {

	 @FXML
	    private PieChart pieChart;

	@FXML
	private VBox detailsVBox; // VBox to hold the branch details

	// Method to fetch the total sales for each branch and display it in the pie
	// chart
	public void initialize() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish connection (update with your DB details)
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Resturent", "root", "1221893");

			// Query to get total sales for each branch
			String query = "SELECT branch_id, SUM(price) AS TotalSales FROM orders GROUP BY branch_id";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			// Iterate through the result set and populate the pie chart and details
			while (rs.next()) {
				int branchId = rs.getInt("branch_id");
				double totalSales = rs.getDouble("TotalSales");

				// Add data to PieChart
				PieChart.Data slice = new PieChart.Data("Branch " + branchId, totalSales);
				pieChart.getData().add(slice);

				// Create Label for each branch's total sales and add it to VBox
				Label branchLabel = new Label("Branch " + branchId + ": " + totalSales + " USD");
				detailsVBox.getChildren().add(branchLabel);

				// Optionally, add a tooltip to show sales when hovering over the slice
				Tooltip tooltip = new Tooltip("Total Sales: " + totalSales + " USD");
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