package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MenuController1 {

    @FXML
    private TableView<MenuItem> tableView;

    @FXML
    private TableColumn<MenuItem, Integer> idColumn;

    @FXML
    private TableColumn<MenuItem, String> sizeColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> typeColumn;

    @FXML
    private TableColumn<MenuItem, Double> priceColumn;

    @FXML
    private TableColumn<MenuItem, String> descriptionColumn;

    private ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();

    private static final String URL = "jdbc:mysql://localhost:3306/Resturent";
    private static final String USER = "root";
    private static final String PASSWORD = "1221893";

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    @FXML
    void handleViewButtonAction(ActionEvent event) {
        menuItems.clear();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM menu_item")) {
            while (resultSet.next()) {
                MenuItem menuItem = new MenuItem(
                        resultSet.getInt("item_id"),
                        resultSet.getString("size"),
                        resultSet.getString("name"),
                        resultSet.getString("type"),
                        resultSet.getDouble("price"),
                        resultSet.getString("description")
                );
                menuItems.add(menuItem);
            }
            tableView.setItems(menuItems);
        } catch (Exception e) {
        	showAlert("Database Error", "An error occurred while loading data.");

            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleBackButtonAction(ActionEvent event) {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
}
