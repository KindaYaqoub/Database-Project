package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import java.sql.*;

public class InventoryStat {

    @FXML
    private PieChart pieChart;

    @FXML
    private VBox detailsVBox; // VBox to hold the order type details

    // Method to fetch the total orders by order type and display them in a pie chart
    public void initialize() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Establish connection (update with your DB details)
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Resturent", "root", "1221893");

            // Query to get the menu items that use the most inventory items
            String query = "SELECT item_id, COUNT(inventory_item_id) AS inventory_count " +
                           "FROM menu_item_inventory_item " +
                           "GROUP BY item_id " +
                           "HAVING COUNT(inventory_item_id) > 1";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Title for the Pie Chart
            Label titleLabel = new Label("Menu Items Using More Than 1 Inventory Item");
            detailsVBox.getChildren().add(titleLabel);  // Adding title to VBox

            // Iterate through the result set and populate the pie chart and details
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int inventoryCount = rs.getInt("inventory_count");

                // Add data to PieChart
                PieChart.Data slice = new PieChart.Data("Item " + itemId + ": " + inventoryCount + " Inventory Items", inventoryCount);
                pieChart.getData().add(slice);

                // Create Label for each menu item's total inventory items and add it to VBox
                Label itemLabel = new Label("Item " + itemId + ": " + inventoryCount + " Inventory Items");
                detailsVBox.getChildren().add(itemLabel);

                // Optionally, add a tooltip to show inventory count when hovering over the slice
                Tooltip tooltip = new Tooltip("Inventory Count: " + inventoryCount);
                Tooltip.install(slice.getNode(), tooltip);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Ensure all resources are closed properly
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
