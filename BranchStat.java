package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import java.sql.*;

public class BranchStat {

	@FXML
	private PieChart pieChart;

	@FXML
	private VBox detailsVBox;

	@FXML
	private Label chartTitle;

	public void initialize() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Resturent", "root", "1221893");
			String query = "SELECT branch_id, COUNT(DISTINCT customer_id) AS TotalCustomers " + "FROM orders "
					+ "GROUP BY branch_id " + "ORDER BY TotalCustomers DESC LIMIT 1";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			chartTitle.setText("Branch with the Highest Number of Customers");

			if (rs.next()) {
				int branchId = rs.getInt("branch_id");
				int totalCustomers = rs.getInt("TotalCustomers");

				PieChart.Data slice = new PieChart.Data("Branch " + branchId + ": " + totalCustomers + " Customers",
						totalCustomers);
				pieChart.getData().add(slice);

				Label branchLabel = new Label("Branch " + branchId + ": " + totalCustomers + " Customers");
				detailsVBox.getChildren().add(branchLabel);

				Tooltip tooltip = new Tooltip("Total Customers: " + totalCustomers);
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
