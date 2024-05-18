package main_folder.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.application.Platform;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Customer;
import main_folder.Model.Provider;
import main_folder.Model.Record;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.Random;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class policyOwnerController implements Initializable {

    @FXML
    private Button logoutButton;

    @FXML
    private TextField createCustomerName, createCustomerEmail, createCustomerPassword, updateCustomerId, updateCustomerName, updateCustomerEmail, updateCustomerPassword, deleteCustomerId, retrieveCustomerId;

    @FXML
    private ComboBox createCustomerOption;

    @FXML
    private Button createCustomerButton, updateCustomerButton, deleteCustomerButton, retrieveCustomerButton;

    @FXML
    private TableView customerTable;

    @FXML
    private TableColumn customerIdColumn, customerNameColumn, customerEmailColumn, customerUserTypeColumn, customerInsuranceNumberColumn;

    @FXML
    private TextField createClaim, createExamDate, createClaimAmount, createInsuredPerson, createDocuments, receiverBankingInfo, updateClaimID, updateClaimDate, updateExamDate, updateAmount, updateDocuments, updateReceiverBankingInfo, deleteClaim, retrieveClaim;

    @FXML
    private Button createClaimButton, updateClaimButton, deleteClaimButton, retrieveClaimButton;

    @FXML
    private TableView claimTable;

    @FXML
    private TableColumn idColumn, claimDateColumn, examDateColumn, claimAmountColumn, insuredPersonColumn, statusColumn, documentsColumn, receiverBankingInfoColumn;

    @FXML
    private Label userIdLabel, userNameLabel, userEmailLabel, totalCost;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CustomerTable();
        ClaimTable();
    }
    private ExecutorService executorService = Executors.newFixedThreadPool(6);

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
                    // Check if the email already exists
                    String checkEmailSql = "SELECT * FROM \"User\" WHERE \"email\" = ?";
                    PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql);
                    checkEmailStmt.setString(1, createCustomerEmail.getText());
                    ResultSet checkEmailRs = checkEmailStmt.executeQuery();
                    if (checkEmailRs.next()) {
                        // If the email already exists, show an alert and return
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Create Provider notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The email already exists");
                        alert.showAndWait();
                        return;
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
                                    CreateUserRecord();
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
                                                CustomerTable();
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

                                                // Insert a new row into the PolicyOwner_PolicyHolder table
                                                String sql6 = "INSERT INTO \"PolicyOwner_PolicyHolder\" (\"PolicyOwner\", \"PolicyHolder\") VALUES (?, ?)";
                                                PreparedStatement stmt6 = conn.prepareStatement(sql6);
                                                stmt6.setInt(1, PolicyOwnerId); // ID of the related PolicyOwner
                                                stmt6.setInt(2, PolicyHolderId); // ID of the new PolicyHolder
                                                stmt6.executeUpdate();
                                                CustomerTable();
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
                                                // Handle the case where the user ID does not exist in the PolicyOwner table
                                                System.out.println("The user does not exist in the PolicyOwner table");
                                            } else {
                                                int PolicyOwnerId = rs5.getInt("id"); // Get the PolicyOwner ID

                                                // Insert a new row into the InsuranceManager_PolicyOwner table
                                                String sql6 = "INSERT INTO \"InsuranceManager_PolicyOwner\" (\"InsuranceManager\", \"PolicyOwner\") VALUES (?, ?)";
                                                PreparedStatement stmt6 = conn.prepareStatement(sql6);
                                                stmt6.setInt(1, InsuranceManagerId); // ID of the related InsuranceManager
                                                stmt6.setInt(2, PolicyOwnerId); // ID of the new PolicyOwner
                                                stmt6.executeUpdate();
                                                CustomerTable();
                                                System.out.println("Created a new PolicyOwner in the InsuranceManager_PolicyOwner table");
                                            }
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
                            if (!(userType.equals("Dependent") || userType.equals("PolicyHolder"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Only PolicyHolder and Dependent can be updated");
                                alert.showAndWait();
                                return;
                            }

                            // Check if the user exists in the Customer table
                            sql = "SELECT * FROM \"Customer\" WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, customerId);
                            rs = stmt.executeQuery();
                            if (!rs.next()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Customer does not exist in the Customer table");
                                alert.showAndWait();
                                return;
                            }

                            if (updateCustomerName.getText().isEmpty() && updateCustomerEmail.getText().isEmpty() && updateCustomerPassword.getText().isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Update Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Please enter all field");
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
                            UpdateCustomerRecord(customerId);
                            CustomerTable();
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
                            if (!(userType.equals("Dependent") || userType.equals("PolicyHolder"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Delete Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Only PolicyHolder and Dependent can be deleted");
                                alert.showAndWait();
                                return;
                            }

                            // Check if the user exists in the Customer table
                            sql = "SELECT * FROM \"Customer\" WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, customerId);
                            rs = stmt.executeQuery();
                            if (!rs.next()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Delete Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Customer does not exist in the Customer table");
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
                            DeleteCustomerRecord(customerId);
                            CustomerTable();
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
                            if (!(userType.equals("Dependent") || userType.equals("PolicyHolder"))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Retrieve Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("Only PolicyHolder and Dependent can be retrieved");
                                alert.showAndWait();
                                return;
                            }

                            // Check if the user exists in the Customer table
                            sql = "SELECT * FROM \"Customer\" WHERE \"id\" = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, customerId);
                            rs = stmt.executeQuery();
                            if (!rs.next()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Retrieve Customer notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Customer does not exist in the Customer table");
                                alert.showAndWait();
                                return;
                            }

                            // If the user is a Dependent
                            if (userType.equals("Dependent")) {
                                RetrieveCustomerRecord(customerId, "Dependent");
                                sql = "SELECT U.*, C.\"InsuranceNumber\", PH_U.\"name\" AS \"PolicyHolderName\", PH_U.\"email\" AS \"PolicyHolderEmail\" " +
                                        "FROM \"User\" U " +
                                        "JOIN \"Dependent\" D ON U.\"id\" = D.\"userID\" " +
                                        "JOIN \"Customer\" C ON D.\"policyNumber\" = C.\"id\" " +
                                        "JOIN \"PolicyHolder_Dependent\" PD ON D.\"id\" = PD.\"Dependent\" " +
                                        "JOIN \"PolicyHolder\" PH ON PD.\"PolicyHolder\" = PH.\"id\" " +
                                        "JOIN \"User\" PH_U ON PH.\"userID\" = PH_U.\"id\" " +
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
                                            "InsuranceNumber: " + rs.getInt("InsuranceNumber") + "\n" +
                                            "Policy Holder Dependency" + "\n" +
                                            "Policy Holder Name: " + rs.getString("PolicyHolderName") + "\n" +
                                            "Policy Holder Email: " + rs.getString("PolicyHolderEmail");
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Retrieve Customer notification");
                                    alert.setHeaderText(null);
                                    alert.setContentText(userDetails);
                                    alert.showAndWait();
                                }
                            }

                            // If the user is a PolicyHolder
                            if (userType.equals("PolicyHolder")) {
                                RetrieveCustomerRecord(customerId, "PolicyHolder");
                                sql = "SELECT U.*, C.\"InsuranceNumber\", PO_U.\"name\" AS \"PolicyOwnerName\", PO_U.\"email\" AS \"PolicyOwnerEmail\" " +
                                        "FROM \"User\" U " +
                                        "JOIN \"PolicyHolder\" PH ON U.\"id\" = PH.\"userID\" " +
                                        "JOIN \"Customer\" C ON PH.\"policyNumber\" = C.\"id\" " +
                                        "JOIN \"PolicyOwner_PolicyHolder\" POPH ON PH.\"id\" = POPH.\"PolicyHolder\" " +
                                        "JOIN \"PolicyOwner\" PO ON POPH.\"PolicyOwner\" = PO.\"id\" " +
                                        "JOIN \"User\" PO_U ON PO.\"userID\" = PO_U.\"id\" " +
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
                                            "InsuranceNumber: " + rs.getInt("InsuranceNumber") + "\n" +
                                            "Policy Owner Dependency" + "\n" +
                                            "Policy Owner Name: " + rs.getString("PolicyOwnerName") + "\n" +
                                            "Policy Owner Email: " + rs.getString("PolicyOwnerEmail");
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

    public void Logout() throws IOException {
        System.out.println("Logout button clicked."); // Debug line
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/main_folder/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void CustomerTable() {
        executorService.submit(() -> {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                int policyOwnerId = Integer.parseInt(loginController.getLoggedInUser());
                String sql1 = "SELECT U.id, U.name, U.email, U.\"userType\", C_PH.\"InsuranceNumber\" " +
                        "FROM \"User\" U " +
                        "LEFT JOIN \"PolicyHolder\" PH ON U.id = PH.\"userID\" " +
                        "LEFT JOIN \"Customer\" C_PH ON PH.\"policyNumber\" = C_PH.id " +
                        "LEFT JOIN \"PolicyOwner_PolicyHolder\" PO_PH2 ON PH.id = PO_PH2.\"PolicyHolder\" " +
                        "LEFT JOIN \"PolicyOwner\" PO ON PO_PH2.\"PolicyOwner\" = PO.id " +
                        "LEFT JOIN \"User\" UH ON PO.\"userID\" = UH.id " +
                        "WHERE UH.id = ?";

                String sql2 = "SELECT U.id, U.name, U.email, U.\"userType\", C_D.\"InsuranceNumber\" " +
                        "FROM \"User\" U " +
                        "LEFT JOIN \"Dependent\" D ON U.\"id\" = D.\"userID\" " +
                        "LEFT JOIN \"Customer\" C_D ON D.\"policyNumber\" = C_D.id " +
                        "LEFT JOIN \"PolicyHolder_Dependent\" PD ON D.\"id\" = PD.\"Dependent\" " +
                        "LEFT JOIN \"PolicyOwner_PolicyHolder\" PO_PH ON PD.\"PolicyHolder\" = PO_PH.\"PolicyHolder\" " +
                        "LEFT JOIN \"PolicyOwner\" PO_U ON PO_PH.\"PolicyOwner\" = PO_U.id " +
                        "LEFT JOIN \"User\" UH ON PO_U.\"userID\" = UH.id " +
                        "WHERE UH.id = ?";

                PreparedStatement stmt1 = conn.prepareStatement(sql1);
                stmt1.setInt(1, policyOwnerId);
                ResultSet rs1 = stmt1.executeQuery();
                PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setInt(1, policyOwnerId);
                ResultSet rs2 = stmt2.executeQuery();

                // Clear existing data
                Platform.runLater(() -> customerTable.getItems().clear());

                // Add data to table
                while (rs1.next()) {
                    Customer customer = new Customer(
                            rs1.getString("id"),
                            rs1.getString("name"),
                            rs1.getString("email"),
                            rs1.getString("userType"),
                            rs1.getString("InsuranceNumber")
                    );
                    Platform.runLater(() -> customerTable.getItems().add(customer));
                }

                while (rs2.next()) {
                    Customer customer = new Customer(
                            rs2.getString("id"),
                            rs2.getString("name"),
                            rs2.getString("email"),
                            rs2.getString("userType"),
                            rs2.getString("InsuranceNumber")
                            );
                    Platform.runLater(() -> customerTable.getItems().add(customer));
                }

                // Set column data
                Platform.runLater(() -> {
                    customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                    customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                    customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                    customerUserTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
                    customerInsuranceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("InsuranceNumber"));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        });
    }

    private void CreateUserRecord() {
        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                String sqlUser = "SELECT * FROM \"User\" ORDER BY \"id\" DESC LIMIT 1";
                PreparedStatement stmtUser = conn.prepareStatement(sqlUser);
                ResultSet rsUser = stmtUser.executeQuery();

                if (rsUser.next()) {
                    String userType = rsUser.getString("userType");
                    String id = rsUser.getString("id");
                    String loggedInUserId = loginController.getLoggedInUser();

                    String record = userType + " with the id of " + id + " has been created by PolicyOwner id " + loggedInUserId;

                    String sqlRecord = "INSERT INTO \"Record\" (\"record\") VALUES (?)";
                    PreparedStatement stmtRecord = conn.prepareStatement(sqlRecord);
                    stmtRecord.setString(1, record);
                    stmtRecord.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void UpdateCustomerRecord(int id){
        executorService.submit(() -> {
            String record = "Customer with the id of " + id + " has been updated by PolicyOwner id " + loginController.getLoggedInUser();

            database db = new database();
            try (Connection conn = db.connect()) {
                String sql = "INSERT INTO \"Record\" (\"record\") VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, record);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    private void DeleteCustomerRecord(int id){
        executorService.submit(() -> {
            String record = "Customer with the id of " + id + " has been deleted by PolicyOwner id " + loginController.getLoggedInUser();

            database db = new database();
            try (Connection conn = db.connect()) {
                String sql = "INSERT INTO \"Record\" (\"record\") VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, record);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    private void RetrieveCustomerRecord(int id, String userType){
        executorService.submit(() -> {
            String record = userType + " with the id of " + id + " has been retrieved by PolicyOwner id " + loginController.getLoggedInUser();

            database db = new database();
            try (Connection conn = db.connect()) {
                String sql = "INSERT INTO \"Record\" (\"record\") VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, record);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void ClaimTable() {
        
    }

    public void CreateClaim() {

    }

    public void UpdateClaim() {

    }

    public void DeleteClaim() {

    }

    public void RetrieveClaim() {

    }

}
    