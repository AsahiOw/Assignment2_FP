package main_folder.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main_folder.ConnectDatabase.database;

import java.sql.*;
import java.util.Random;
import java.io.IOException;

public class adminController {
    @FXML
    private TextField createCustomerName;
    @FXML
    private TextField createCustomerEmail;
    @FXML
    private TextField createCustomerPassword;
    @FXML
    private ComboBox createCustomerOption;
    @FXML
    private Button createCustomerButton;
    @FXML
    private TextField updateCustomerId;
    @FXML
    private TextField updateCustomerName;
    @FXML
    private TextField updateCustomerEmail;
    @FXML
    private TextField updateCustomerPassword;
    @FXML
    private Button updateCustomerButton;
    @FXML
    private TextField deleteCustomerId;
    @FXML
    private Button deleteCustomerButton;
    @FXML
    private TextField retrieveCustomerId;
    @FXML
    private Button retrieveCustomerButton;
    @FXML
    private TextField createProviderName;
    @FXML
    private TextField createProviderEmail;
    @FXML
    private TextField createProviderPassword;
    @FXML
    private ComboBox createProviderOption;
    @FXML
    private Button createProviderButton;
    @FXML
    private TextField updateProviderId;
    @FXML
    private TextField updateProviderName;
    @FXML
    private TextField updateProviderEmail;
    @FXML
    private TextField updateProviderPassword;
    @FXML
    private Button updateProviderButton;
    @FXML
    private TextField deleteProviderId;
    @FXML
    private Button deleteProviderButton;
    @FXML
    private TextField retrieveProviderId;
    @FXML
    private Button retrieveProviderButton;
    @FXML
    private TableView claimTable;
    @FXML
    private TableColumn idColumn;
    @FXML
    private TableColumn claimDateColumn;
    @FXML
    private TableColumn examDateColumn;
    @FXML
    private TableColumn claimAmountColumn;
    @FXML
    private TableColumn insuredPersonColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private TableColumn documentColumn;
    @FXML
    private TableColumn receiverBankingInfoColumn;
    @FXML
    private Label totalClaimLabel;
    @FXML
    private Button logoutButton;

    // Method in adminPage.fxml
    public void CreateCustomer() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (createCustomerName.getText().isEmpty() || createCustomerEmail.getText().isEmpty() || createCustomerPassword.getText().isEmpty() || createCustomerOption.getValue() == null) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                } else {
                    String userType = createCustomerOption.getValue().toString();
                    String sql = "INSERT INTO \"User\" (\"name\", \"email\", \"password\", \"userType\") VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, createCustomerName.getText());
                    stmt.setString(2, createCustomerEmail.getText());
                    stmt.setString(3, createCustomerPassword.getText());
                    stmt.setString(4, userType);
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    int userId = 0;
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                    System.out.println("Created a " + userType);

                    if (userType.equals("Dependent") || userType.equals("PolicyHolder") || userType.equals("PolicyOwner")) {
                        Random rand = new Random();
                        int insuranceNumber = rand.nextInt((999999999 - 100000000) + 1) + 100000000; // Generate a random 10-digit number
                        sql = "INSERT INTO \"Customer\" (\"InsuranceNumber\") VALUES (?)";
                        stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        stmt.setInt(1, insuranceNumber);
                        stmt.executeUpdate();
                        rs = stmt.getGeneratedKeys();
                        int customerId = 0;
                        if (rs.next()) {
                            customerId = rs.getInt(1);
                        }

                        sql = "INSERT INTO \"" + userType + "\" (\"userID\", \"policyNumber\") VALUES (?, ?)";
                        stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, userId);
                        stmt.setInt(2, customerId);
                        stmt.executeUpdate();
                        System.out.println("Created a " + userType + " with a new Customer entry");
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Create Customer notification");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Created a " + userType + " with a new Customer entry");
                        alert2.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateCustomer() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (updateCustomerId.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Customer does not exist");
                    alert.showAndWait();
                } else {
                    try {
                        int customerId = Integer.parseInt(updateCustomerId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, customerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Update Customer notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Customer does not exist");
                            alert.showAndWait();
                        } else {
                            if (updateCustomerName.getText().isEmpty() && updateCustomerEmail.getText().isEmpty() && updateCustomerPassword.getText().isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Updated the customer");
                                alert.showAndWait();
                                return;
                            }
                            sql = "UPDATE \"User\" SET ";
                            if (!updateCustomerName.getText().isEmpty()) {
                                sql += "\"name\" = ?, ";
                            }
                            if (!updateCustomerEmail.getText().isEmpty()) {
                                sql += "\"email\" = ?, ";
                            }
                            if (!updateCustomerPassword.getText().isEmpty()) {
                                sql += "\"password\" = ?, ";
                            }
                            sql = sql.substring(0, sql.length() - 2); // Remove the last comma and space
                            sql += " WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            int index = 1;
                            if (!updateCustomerName.getText().isEmpty()) {
                                stmt.setString(index++, updateCustomerName.getText());
                            }
                            if (!updateCustomerEmail.getText().isEmpty()) {
                                stmt.setString(index++, updateCustomerEmail.getText());
                            }
                            if (!updateCustomerPassword.getText().isEmpty()) {
                                stmt.setString(index++, updateCustomerPassword.getText());
                            }
                            stmt.setInt(index, customerId);
                            stmt.executeUpdate();
                            System.out.println("Updated the customer");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Update Customer notification");
                            alert.setHeaderText(null);
                            alert.setContentText("Updated the customer");
                            alert.showAndWait();
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Update Customer notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Customer does not exist");
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeleteCustomer() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (deleteCustomerId.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Customer does not exist");
                    alert.showAndWait();
                } else {
                    try {
                        int customerId = Integer.parseInt(deleteCustomerId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, customerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Delete Customer notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Customer does not exist");
                            alert.showAndWait();
                        } else {
                            String userType = rs.getString("userType");
                            sql = "DELETE FROM \"" + userType + "\" WHERE \"userID\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, customerId);
                            stmt.executeUpdate();

                            sql = "DELETE FROM \"Customer\" WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, customerId);
                            stmt.executeUpdate();

                            sql = "DELETE FROM \"User\" WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, customerId);
                            stmt.executeUpdate();

                            System.out.println("Deleted the customer");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Delete Customer notification");
                            alert.setHeaderText(null);
                            alert.setContentText("Deleted the customer");
                            alert.showAndWait();
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Delete Customer notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Customer does not exist");
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RetrieveCustomer() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (retrieveCustomerId.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Customer corresponds to this id does not exist");
                    alert.showAndWait();
                } else {
                    try {
                        int customerId = Integer.parseInt(retrieveCustomerId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, customerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Customer notification");
                            alert.setHeaderText(null);
                            alert.setContentText("This User corresponds to this id does not exist");
                            alert.showAndWait();
                        } else {
                            String userType = rs.getString("userType");
                            if (userType.equals("Dependent") || userType.equals("PolicyHolder") || userType.equals("PolicyOwner")) {
                                sql = "SELECT U.*, C.\"InsuranceNumber\" FROM \"User\" U " +
                                        "JOIN \"" + userType + "\" T ON U.\"id\" = T.\"userID\" " +
                                        "JOIN \"Customer\" C ON T.\"policyNumber\" = C.\"id\" " +
                                        "WHERE U.\"id\" = ?";
                                stmt = conn.prepareStatement(sql);
                                stmt.setInt(1, customerId);
                                rs = stmt.executeQuery();
                                if (rs.next()) {
                                    String userDetails = "Userid: " + rs.getInt("id") + "\n" +
                                            "Name: " + rs.getString("name") + "\n" +
                                            "Email: " + rs.getString("email") + "\n" +
                                            "Password: " + rs.getString("password") + "\n" +
                                            "UserType: " + rs.getString("userType") + "\n" +
                                            "InsuranceNumber: " + rs.getInt("InsuranceNumber");
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Retrieve Customer notification");
                                    alert.setHeaderText(null);
                                    alert.setContentText(userDetails);
                                    alert.showAndWait();
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Retrieve Customer notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Customer corresponds to this id does not exist");
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CreateProvider() {
        System.out.println("Create a new provider");
    }

    public void UpdateProvider() {
        System.out.println("Update a provider");
    }

    public void DeleteProvider() {
        System.out.println("Delete a provider");
    }

    public void RetrieveProvider() {
        System.out.println("Retrieve a provider");
    }

    public void TotalCost() {
        System.out.println("Logout");
    }
    public void Logout(){
        System.out.println("Logout");
    }

    // private method
    // CRUD for customer

}