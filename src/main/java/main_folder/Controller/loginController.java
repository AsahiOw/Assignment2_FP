package main_folder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main_folder.ConnectDatabase.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginBTN;
    private String loggedInUser;

    public void Login() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                String email = emailField.getText();
                String password = passwordField.getText();

                // Check if email or password fields are empty
                if (email.isEmpty() || password.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login notification");
                    alert.setHeaderText(null);
                    alert.setContentText("This email or password is incorrect");
                    alert.showAndWait();
                    return;
                }

                String sql = "SELECT * FROM \"User\" WHERE \"email\" = ? AND \"password\" = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login notification");
                    alert.setHeaderText(null);
                    alert.setContentText("This email or password is incorrect");
                    alert.showAndWait();
                } else {
                    String userType = rs.getString("userType");
                    loggedInUser = rs.getString("id");
                    Stage stage = (Stage) loginBTN.getScene().getWindow();
                    String formattedUserType = lowerCaseFirstLetter(userType);
                    Parent root = FXMLLoader.load(getClass().getResource("/main_folder/" + formattedUserType + "/" + formattedUserType + ".fxml"));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    private String lowerCaseFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }
}