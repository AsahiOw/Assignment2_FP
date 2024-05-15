package main_folder.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main_folder.ConnectDatabase.database;

import java.sql.*;
import java.util.Optional;
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
                    if (userType.equals("Dependent") || userType.equals("PolicyHolder") || userType.equals("PolicyOwner")) {
                        // Prompt for the ID of the related user
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Enter Related User ID");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Please enter the ID of the related user:");
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            String relatedUserIdStr = result.get();
                            if (relatedUserIdStr.isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Create Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("No related user ID was entered");
                                alert.showAndWait();
                                return;
                            }
                            int relatedUserId = Integer.parseInt(result.get());
                            // Check if the related user ID exists in the database
                            String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, relatedUserId);
                            ResultSet rs = stmt.executeQuery();
                            if (!rs.next()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Create Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The related user does not exist");
                                alert.showAndWait();
                                return;
                            } else {
                            String relatedUserType = rs.getString("userType");
                            if ((userType.equals("Dependent") && !relatedUserType.equals("PolicyHolder")) ||
                                    (userType.equals("PolicyHolder") && !relatedUserType.equals("PolicyOwner")) ||
                                    (userType.equals("PolicyOwner") && !relatedUserType.equals("InsuranceManager"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Create Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The related user does not have the correct user type");
                                alert.showAndWait();
                                return;
                            }
                                sql = "INSERT INTO \"User\" (\"name\", \"email\", \"password\", \"userType\") VALUES (?, ?, ?, ?)";
                                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                stmt.setString(1, createCustomerName.getText());
                                stmt.setString(2, createCustomerEmail.getText());
                                stmt.setString(3, createCustomerPassword.getText());
                                stmt.setString(4, userType);
                                stmt.executeUpdate();
                                rs = stmt.getGeneratedKeys();
                                int userId = 0;
                                if (rs.next()) {
                                    userId = rs.getInt(1);
                                }
                                System.out.println("Created a " + userType);

                                Random rand = new Random();
                                int insuranceNumber = rand.nextInt((999999999 - 100000000) + 1) + 100000000; // Generate a random 10-digit number
                                String sql2 = "INSERT INTO \"Customer\" (\"InsuranceNumber\") VALUES (?)";
                                PreparedStatement stmt2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                                stmt2.setInt(1, insuranceNumber);
                                stmt2.executeUpdate();
                                ResultSet rs2 = stmt2.getGeneratedKeys();
                                int customerId = 0;
                                if (rs2.next()) {
                                    customerId = rs2.getInt(1);
                                }


                                String sql3 = "INSERT INTO \"" + userType + "\" (\"userID\", \"policyNumber\") VALUES (?, ?)";
                                PreparedStatement stmt3 = conn.prepareStatement(sql3);
                                stmt3.setInt(1, userId);
                                stmt3.setInt(2, customerId);
                                stmt3.executeUpdate();
                                System.out.println("Created a " + userType + " with a new Customer entry");
                                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                                alert2.setTitle("Create Customer notification");
                                alert2.setHeaderText(null);
                                alert2.setContentText("Created a " + userType + " with a new Customer entry");
                                alert2.showAndWait();

                                if (userType.equals("Dependent")) {
                                    // Check if the related user ID exists in the corresponse table
                                    String sql4 = "SELECT * FROM \"PolicyHolder\" WHERE \"userID\" = ?";
                                    PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                    stmt4.setInt(1, relatedUserId);
                                    ResultSet rs4 = stmt4.executeQuery();

                                    if (!rs4.next()) {
                                        // Handle the case where the related user ID does not exist in the PolicyHolder table
                                        System.out.println("The related user does not exist in the PolicyHolder table");
                                    } else {
                                        int policyHolderId = rs4.getInt("id"); // Get the PolicyHolder ID

                                        String sql5 = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
                                        PreparedStatement stmt5 = conn.prepareStatement(sql5);
                                        stmt5.setInt(1, userId);
                                        ResultSet rs5 = stmt5.executeQuery();

                                        if (!rs5.next()) {
                                            // Handle the case where the user ID does not exist in the Dependent table
                                            System.out.println("The user does not exist in the Dependent table");
                                        } else {
                                            int dependentId = rs5.getInt("id"); // Get the Dependent ID

                                            // Insert a new row into the PolicyHolder_Dependent table
                                            String sql6 = "INSERT INTO \"PolicyHolder_Dependent\" (\"PolicyHolder\", \"Dependent\") VALUES (?, ?)";
                                            PreparedStatement stmt6 = conn.prepareStatement(sql6);
                                            stmt6.setInt(1, policyHolderId); // ID of the related PolicyHolder
                                            stmt6.setInt(2, dependentId); // ID of the new Dependent
                                            stmt6.executeUpdate();
                                            System.out.println("Created a new dependency in the PolicyHolder_Dependent table");
                                        }
                                    }
                                }

                                if (userType.equals("PolicyHolder")) {
                                    // Check if the related user ID exists in the corresponse table
                                    String sql4 = "SELECT * FROM \"PolicyOwner\" WHERE \"userID\" = ?";
                                    PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                    stmt4.setInt(1, relatedUserId);
                                    ResultSet rs4 = stmt4.executeQuery();

                                    if (!rs4.next()) {
                                        // Handle the case where the related user ID does not exist in the PolicyOwner table
                                        System.out.println("The related user does not exist in the PolicyOwner table");
                                    } else {
                                        int PolicyOwnerId = rs4.getInt("id"); // Get the PolicyOwner ID

                                        String sql5 = "SELECT * FROM \"PolicyHolder\" WHERE \"userID\" = ?";
                                        PreparedStatement stmt5 = conn.prepareStatement(sql5);
                                        stmt5.setInt(1, userId);
                                        ResultSet rs5 = stmt5.executeQuery();

                                        if (!rs5.next()) {
                                            // Handle the case where the user ID does not exist in the PolicyHolder table
                                            System.out.println("The user does not exist in the PolicyHolder table");
                                        } else {
                                            int PolicyHolderId = rs5.getInt("id"); // Get the PolicyHolder ID

                                            // Insert a new row into the PolicyHolder_Dependent table
                                            String sql6 = "INSERT INTO \"PolicyOwner_PolicyHolder\" (\"PolicyOwner\", \"PolicyHolder\") VALUES (?, ?)";
                                            PreparedStatement stmt6 = conn.prepareStatement(sql6);
                                            stmt6.setInt(1, PolicyOwnerId); // ID of the related PolicyOwner
                                            stmt6.setInt(2, PolicyHolderId); // ID of the new PolicyHolder
                                            stmt6.executeUpdate();
                                            System.out.println("Created a new PolicyHolder in the PolicyOwner_PolicyHolder table");
                                        }
                                    }
                                }

                                if (userType.equals("PolicyOwner")) {
                                    // Check if the related user ID exists in the corresponse table
                                    String sql4 = "SELECT * FROM \"InsuranceManager\" WHERE \"userID\" = ?";
                                    PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                    stmt4.setInt(1, relatedUserId);
                                    ResultSet rs4 = stmt4.executeQuery();

                                    if (!rs4.next()) {
                                        // Handle the case where the related user ID does not exist in the PolicyHolder table
                                        System.out.println("The related user does not exist in the InsuranceManager table");
                                    } else {
                                        int InsuranceManagerId = rs4.getInt("id"); // Get the InsuranceManager ID

                                        String sql5 = "SELECT * FROM \"PolicyOwner\" WHERE \"userID\" = ?";
                                        PreparedStatement stmt5 = conn.prepareStatement(sql5);
                                        stmt5.setInt(1, userId);
                                        ResultSet rs5 = stmt5.executeQuery();

                                        if (!rs5.next()) {
                                            // Handle the case where the user ID does not exist in the Dependent table
                                            System.out.println("The user does not exist in the PolicyOwner table");
                                        } else {
                                            int PolicyOwnerId = rs5.getInt("id"); // Get the PolicyOwner ID

                                            // Insert a new row into the PolicyHolder_Dependent table
                                            String sql6 = "INSERT INTO \"InsuranceManager_PolicyOwner\" (\"InsuranceManager\", \"PolicyOwner\") VALUES (?, ?)";
                                            PreparedStatement stmt6 = conn.prepareStatement(sql6);
                                            stmt6.setInt(1, InsuranceManagerId); // ID of the related InsuranceManager
                                            stmt6.setInt(2, PolicyOwnerId); // ID of the new PolicyOwner
                                            stmt6.executeUpdate();
                                            System.out.println("Created a new PolicyOwner in the InsuranceManager_PolicyOwner table");
                                        }
                                    }
                                }
                            }
                        }
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
                            String userType = rs.getString("userType");
                            if (!(userType.equals("Dependent") || userType.equals("PolicyHolder") || userType.equals("PolicyOwner"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Customer does not exist");
                                alert.showAndWait();
                                return;
                            }
                            if (updateCustomerName.getText().isEmpty() && updateCustomerEmail.getText().isEmpty() && updateCustomerPassword.getText().isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Updated the Customer");
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
                            alert.setContentText("Updated the Customer");
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
                            if (!(userType.equals("Dependent") || userType.equals("PolicyHolder") || userType.equals("PolicyOwner"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Delete Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Customer does not exist");
                                alert.showAndWait();
                                return;
                            }
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
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (createProviderName.getText().isEmpty() || createProviderEmail.getText().isEmpty() || createProviderPassword.getText().isEmpty() || createProviderOption.getValue() == null) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Provider notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                } else {
                    String userType = createProviderOption.getValue().toString();
                    String sql = "INSERT INTO \"User\" (\"name\", \"email\", \"password\", \"userType\") VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, createProviderName.getText());
                    stmt.setString(2, createProviderEmail.getText());
                    stmt.setString(3, createProviderPassword.getText());
                    stmt.setString(4, userType);
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    int userId = 0;
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                    System.out.println("Created a " + userType);

                    if (userType.equals("InsuranceManager") || userType.equals("InsuranceSurveyor")) {
                        sql = "INSERT INTO \"" + userType + "\" (\"userID\") VALUES (?)";
                        stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, userId);
                        stmt.executeUpdate();
                        System.out.println("Created a " + userType + " entry in the corresponding table");
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Create Provider notification");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Created a " + userType + " entry in the corresponding table");
                        alert2.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateProvider() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (updateProviderId.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Provider notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Provider does not exist");
                    alert.showAndWait();
                } else {
                    try {
                        int customerId = Integer.parseInt(updateProviderId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, customerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Update Provider notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Provider does not exist");
                            alert.showAndWait();
                        } else {
                            String userType = rs.getString("userType");
                            if (!(userType.equals("InsuranceManager") || userType.equals("InsuranceSurveyor"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Provider notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Provider does not exist");
                                alert.showAndWait();
                                return;
                            }
                            if (updateProviderName.getText().isEmpty() && updateProviderEmail.getText().isEmpty() && updateProviderPassword.getText().isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Provider notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Updated the Provider");
                                alert.showAndWait();
                                return;
                            }
                            sql = "UPDATE \"User\" SET ";
                            if (!updateProviderName.getText().isEmpty()) {
                                sql += "\"name\" = ?, ";
                            }
                            if (!updateProviderEmail.getText().isEmpty()) {
                                sql += "\"email\" = ?, ";
                            }
                            if (!updateProviderPassword.getText().isEmpty()) {
                                sql += "\"password\" = ?, ";
                            }
                            sql = sql.substring(0, sql.length() - 2); // Remove the last comma and space
                            sql += " WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            int index = 1;
                            if (!updateProviderName.getText().isEmpty()) {
                                stmt.setString(index++, updateProviderName.getText());
                            }
                            if (!updateProviderEmail.getText().isEmpty()) {
                                stmt.setString(index++, updateProviderEmail.getText());
                            }
                            if (!updateProviderPassword.getText().isEmpty()) {
                                stmt.setString(index++, updateProviderPassword.getText());
                            }
                            stmt.setInt(index, customerId);
                            stmt.executeUpdate();
                            System.out.println("Updated the Provider");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Update Provider notification");
                            alert.setHeaderText(null);
                            alert.setContentText("Updated the Provider");
                            alert.showAndWait();
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Update Provider notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Provider does not exist");
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void DeleteProvider() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (deleteProviderId.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Delete Provider notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Provider does not exist");
                    alert.showAndWait();
                } else {
                    try {
                        int providerId = Integer.parseInt(deleteProviderId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, providerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Delete Provider notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Provider does not exist");
                            alert.showAndWait();
                        } else {
                            String userType = rs.getString("userType");
                            if (!(userType.equals("InsuranceManager") || userType.equals("InsuranceSurveyor"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Delete Provider notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Provider does not exist");
                                alert.showAndWait();
                                return;
                            }
                            sql = "DELETE FROM \"" + userType + "\" WHERE \"userID\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, providerId);
                            stmt.executeUpdate();

                            sql = "DELETE FROM \"User\" WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, providerId);
                            stmt.executeUpdate();

                            System.out.println("Deleted the provider");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Delete Provider notification");
                            alert.setHeaderText(null);
                            alert.setContentText("Deleted the provider");
                            alert.showAndWait();
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Delete Provider notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Provider does not exist");
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RetrieveProvider() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (retrieveProviderId.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Provider notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Provider corresponds to this id does not exist");
                    alert.showAndWait();
                } else {
                    try {
                        int providerId = Integer.parseInt(retrieveProviderId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, providerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Provider notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Provider corresponds to this id does not exist");
                            alert.showAndWait();
                        } else {
                            String userType = rs.getString("userType");
                            if (userType.equals("Dependent") || userType.equals("PolicyHolder") || userType.equals("PolicyOwner")) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Retrieve Provider notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Provider corresponds to this id does not exist");
                                alert.showAndWait();
                                return;
                            }
                            if (userType.equals("InsuranceManager") || userType.equals("InsuranceSurveyor")) {
                                sql = "SELECT U.* FROM \"User\" U " +
                                        "JOIN \"" + userType + "\" T ON U.\"id\" = T.\"userID\" " +
                                        "WHERE U.\"id\" = ?";
                                stmt = conn.prepareStatement(sql);
                                stmt.setInt(1, providerId);
                                rs = stmt.executeQuery();
                                if (rs.next()) {
                                    String userDetails = "Userid: " + rs.getInt("id") + "\n" +
                                            "Name: " + rs.getString("name") + "\n" +
                                            "Email: " + rs.getString("email") + "\n" +
                                            "Password: " + rs.getString("password") + "\n" +
                                            "UserType: " + rs.getString("userType");
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Retrieve Provider notification");
                                    alert.setHeaderText(null);
                                    alert.setContentText(userDetails);
                                    alert.showAndWait();
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Retrieve Provider notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Provider corresponds to this id does not exist");
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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