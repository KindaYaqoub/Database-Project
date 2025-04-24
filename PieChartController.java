package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PieChartController {

    @FXML
    private PieChart pieChart;

    @FXML
    private Label totalSalesLabel;

    // تفاصيل الاتصال بقاعدة البيانات كمتغيرات ثابتة
    private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
    private static final String USER = "root";
    private static final String PASSWORD = "1221893";

    public void initialize() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // الاتصال بقاعدة البيانات
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // تنفيذ الاستعلام للحصول على إجمالي المبيعات
            String query = "SELECT SUM(price) AS TotalSales FROM orders";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                double totalSales = rs.getDouble("TotalSales");
                totalSalesLabel.setText("Total Sales: " + totalSales);

                // إضافة بيانات الرسم البياني
                PieChart.Data slice = new PieChart.Data("Total Sales", totalSales);
                pieChart.getData().add(slice);
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
