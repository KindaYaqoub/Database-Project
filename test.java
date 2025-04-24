  package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class test extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // تحميل ملف FXML وتخزينه في متغير root
        Parent root = FXMLLoader.load(getClass().getResource("/application/Interface.fxml"));

        // تعيين العنوان
        primaryStage.setTitle("Dining Table Manager");

        // إنشاء المشهد وتعيين الجذر
        primaryStage.setScene(new Scene(root));

        // عرض النافذة
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  // تشغيل تطبيق JavaFX
    }
}
