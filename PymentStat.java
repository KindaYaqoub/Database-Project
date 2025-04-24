package application;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

import java.sql.*;

public class PymentStat { // تأكد من أن اسم الفئة مطابق بشكل صحيح

	@FXML
	private PieChart pieChart;

	// Method to fetch and display data in PieChart
	public void showPaymentMethodsDistribution() {
		// استخدام try-with-resources لضمان إغلاق الاتصال والموارد
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Resturent", "root",
				"1221893");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT payment_method.type, COUNT(*) AS Count " + "FROM customer "
						+ "JOIN orders ON customer.customer_id = orders.customer_id "
						+ "JOIN payment_method ON orders.payment_id = payment_method.payment_id "
						+ "WHERE payment_method.type IN ('Credit Card', 'Cash', 'Mobile Payment') "
						+ "GROUP BY payment_method.type")) {

			pieChart.getData().clear(); // مسح البيانات القديمة من PieChart

			// معالجة النتائج من قاعدة البيانات وإضافة البيانات إلى PieChart
			while (rs.next()) {
				String paymentType = rs.getString("type");
				int count = rs.getInt("Count");

				// إضافة البيانات إلى PieChart
				PieChart.Data slice = new PieChart.Data(paymentType, count);
				pieChart.getData().add(slice);

				// إضافة Tooltip لعرض مزيد من التفاصيل عند التمرير على الشريحة
				Tooltip tooltip = new Tooltip(paymentType + ": " + count + " Customers");
				Tooltip.install(slice.getNode(), tooltip);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// يمكنك إضافة رسالة خطأ للمستخدم هنا مثل:
			System.out.println("حدث خطأ أثناء الاتصال بقاعدة البيانات: " + e.getMessage());
		}
	}

	@FXML
	public void initialize() {
		// تأكد من استدعاء الوظيفة عند تحميل FXML
		showPaymentMethodsDistribution();
	}
}
