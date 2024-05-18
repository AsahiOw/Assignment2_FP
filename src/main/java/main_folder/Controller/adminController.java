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

public class adminController implements Initializable {
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
    private TableColumn documentsColumn;
    @FXML
    private TableColumn receiverBankingInfoColumn;
    @FXML
    private Label totalClaimLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private TableColumn customerIdColumn;
    @FXML
    private TableColumn customerNameColumn;
    @FXML
    private TableColumn customerEmailColumn;
    @FXML
    private TableColumn customerPasswordColumn;
    @FXML
    private TableColumn customerUserTypeColumn;
    @FXML
    private TableColumn customerInsuranceNumberColumn;
    @FXML
    private TableColumn customerIdDependencyColumn;
    @FXML
    private TableView customerTable;
    @FXML
    private TableColumn providerIdColumn;
    @FXML
    private TableColumn providerNameColumn;
    @FXML
    private TableColumn providerEmailColumn;
    @FXML
    private TableColumn providerPasswordColumn;
    @FXML
    private TableColumn providerUserTypeColumn;
    @FXML
    private TableColumn providerIdDependencyColumn;
    @FXML
    private TableView providerTable;
    @FXML
    private Button updateRecordButton;
    @FXML
    private TableView recordTable;
    @FXML
    private TableColumn idRecordColumn;
    @FXML
    private TableColumn dateRecordColumn;
    @FXML
    private TableColumn contextRecordColumn;

    // Method in adminPage.fxml
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupClaimTableColumns();
        setupRecordTableColumns();
        CustomerTable();
        ProviderTable();
        ClaimTable();
        LoginRecord();
        RecordTable();
    }

    private void setupClaimTableColumns(){
        executorService.submit(() -> {
            idColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("id"));
            claimDateColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Claim_Date"));
            examDateColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Exam_Date"));
            claimAmountColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Claim_amount"));
            insuredPersonColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Insured_Person"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Status"));
            documentsColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Documents"));
            receiverBankingInfoColumn.setCellValueFactory(new PropertyValueFactory<Claim, String>("Receiver_Banking_Infor"));
        });
    }

    private void setupRecordTableColumns(){
        executorService.submit(() -> {
            idRecordColumn.setCellValueFactory(new PropertyValueFactory<Record, String>("id"));
            dateRecordColumn.setCellValueFactory(new PropertyValueFactory<Record, String>("date"));
            contextRecordColumn.setCellValueFactory(new PropertyValueFactory<Record, String>("context"));
        });
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
                            // Check if the new email already exists
                            if (!updateCustomerEmail.getText().isEmpty()) {
                                String checkEmailSql = "SELECT * FROM \"User\" WHERE \"email\" = ? AND \"id\" != ?";
                                PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql);
                                checkEmailStmt.setString(1, updateCustomerEmail.getText());
                                checkEmailStmt.setInt(2, customerId);
                                ResultSet checkEmailRs = checkEmailStmt.executeQuery();
                                if (checkEmailRs.next()) {
                                    // If the email already exists and does not belong to the current user, show an alert and return
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Update Customer notification");
                                    alert.setHeaderText(null);
                                    alert.setContentText("The email already exists");
                                    alert.showAndWait();
                                    return;
                                }
                            }

                            // Rest of the method...
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
                            if (userType.equals("PolicyOwner")) {
                                RetrieveCustomerRecord(customerId, "PolicyOwner");
                                sql = "SELECT U.*, C.\"InsuranceNumber\", IM_U.\"name\" AS \"InsuranceManagerName\", IM_U.\"email\" AS \"InsuranceManagerEmail\" " +
                                        "FROM \"User\" U " +
                                        "JOIN \"PolicyOwner\" PO ON U.\"id\" = PO.\"userID\" " +
                                        "JOIN \"Customer\" C ON PO.\"policyNumber\" = C.\"id\" " +
                                        "JOIN \"InsuranceManager_PolicyOwner\" IMPO ON PO.\"id\" = IMPO.\"PolicyOwner\" " +
                                        "JOIN \"InsuranceManager\" IM ON IMPO.\"InsuranceManager\" = IM.\"id\" " +
                                        "JOIN \"User\" IM_U ON IM.\"userID\" = IM_U.\"id\" " +
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
                                            "Insurance Manager Dependency" + "\n" +
                                            "Insurance Manager Name: " + rs.getString("InsuranceManagerName") + "\n" +
                                            "Insurance Manager Email: " + rs.getString("InsuranceManagerEmail");
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

    public void CustomerTable() {
        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                if (conn != null) {
                    String sql = "SELECT U.\"id\", U.\"name\", U.\"email\", U.\"password\", U.\"userType\", " +
                            "CASE " +
                            "WHEN U.\"userType\" = 'Dependent' THEN C_D.\"InsuranceNumber\" " +
                            "WHEN U.\"userType\" = 'PolicyHolder' THEN C_PH.\"InsuranceNumber\" " +
                            "WHEN U.\"userType\" = 'PolicyOwner' THEN C_PO.\"InsuranceNumber\" " +
                            "END AS \"InsuranceNumber\", " +
                            "CASE " +
                            "WHEN U.\"userType\" = 'Dependent' THEN PH_U.\"userID\" " +
                            "WHEN U.\"userType\" = 'PolicyHolder' THEN PO_U.\"userID\" " +
                            "WHEN U.\"userType\" = 'PolicyOwner' THEN IM_U.\"userID\" " +
                            "END AS \"idDependency\" " +
                            "FROM \"User\" U " +
                            "LEFT JOIN \"Dependent\" D ON U.\"id\" = D.\"userID\" " +
                            "LEFT JOIN \"Customer\" C_D ON D.\"policyNumber\" = C_D.\"id\" " +
                            "LEFT JOIN \"PolicyHolder_Dependent\" PD ON D.\"id\" = PD.\"Dependent\" " +
                            "LEFT JOIN \"PolicyHolder\" PH_U ON PD.\"PolicyHolder\" = PH_U.\"id\" " +
                            "LEFT JOIN \"PolicyHolder\" PH ON U.\"id\" = PH.\"userID\" " +
                            "LEFT JOIN \"Customer\" C_PH ON PH.\"policyNumber\" = C_PH.\"id\" " +
                            "LEFT JOIN \"PolicyOwner_PolicyHolder\" POPH ON PH.\"id\" = POPH.\"PolicyHolder\" " +
                            "LEFT JOIN \"PolicyOwner\" PO_U ON POPH.\"PolicyOwner\" = PO_U.\"id\" " +
                            "LEFT JOIN \"PolicyOwner\" PO ON U.\"id\" = PO.\"userID\" " +
                            "LEFT JOIN \"Customer\" C_PO ON PO.\"policyNumber\" = C_PO.\"id\" " +
                            "LEFT JOIN \"InsuranceManager_PolicyOwner\" IMPO ON PO.\"id\" = IMPO.\"PolicyOwner\" " +
                            "LEFT JOIN \"InsuranceManager\" IM_U ON IMPO.\"InsuranceManager\" = IM_U.\"id\" " +
                            "WHERE U.\"userType\" IN ('Dependent', 'PolicyHolder', 'PolicyOwner')";

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();

                    // Clear existing data
                    Platform.runLater(() -> customerTable.getItems().clear());

                    // Add data to table
                    while (rs.next()) {
                        Customer customer = new Customer(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("userType"),
                                rs.getString("InsuranceNumber"),
                                rs.getString("idDependency")
                        );
                        Platform.runLater(() -> customerTable.getItems().add(customer));
                    }

                    // Set column data
                    Platform.runLater(() -> {
                        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                        customerPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
                        customerUserTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
                        customerInsuranceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("InsuranceNumber"));
                        customerIdDependencyColumn.setCellValueFactory(new PropertyValueFactory<>("idDependency"));
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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
                    // Check if the email already exists
                    String checkEmailSql = "SELECT * FROM \"User\" WHERE \"email\" = ?";
                    PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql);
                    checkEmailStmt.setString(1, createProviderEmail.getText());
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
                    String userType = createProviderOption.getValue().toString();
                    if (userType.equals("InsuranceManager")) {
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

                        String sql2 = "INSERT INTO \"" + userType + "\" (\"userID\") VALUES (?)";
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, userId);
                        stmt2.executeUpdate();
                        System.out.println("Created a " + userType + " entry in the corresponding table");
                        CreateUserRecord();
                        ProviderTable();
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Create Provider notification");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Created a " + userType + " entry in the corresponding table");
                        alert2.showAndWait();
                    }

                    if (userType.equals("InsuranceSurveyor")) {
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Enter Related User ID");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Please enter the ID of the related InsuranceManager:");
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            String relatedUserIdStr = result.get();
                            if (relatedUserIdStr.isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Create Provider notification");
                                alert.setHeaderText(null);
                                alert.setContentText("No related user ID was entered");
                                alert.showAndWait();
                                return;
                            }
                            int relatedUserId = Integer.parseInt(result.get());

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

                            String sql2 = "INSERT INTO \"" + userType + "\" (\"userID\") VALUES (?)";
                            PreparedStatement stmt2 = conn.prepareStatement(sql2);
                            stmt2.setInt(1, userId);
                            stmt2.executeUpdate();
                            System.out.println("Created a " + userType + " entry in the corresponding table");
                            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                            alert2.setTitle("Create Provider notification");
                            alert2.setHeaderText(null);
                            alert2.setContentText("Created a " + userType + " entry in the corresponding table");
                            alert2.showAndWait();

                            // Check if the related user ID exists in the corresponse table
                            String sql3 = "SELECT * FROM \"InsuranceManager\" WHERE \"userID\" = ?";
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, relatedUserId);
                            ResultSet rs3 = stmt3.executeQuery();

                            if (!rs3.next()) {
                                // Handle the case where the related user ID does not exist in the PolicyHolder table
                                System.out.println("The related user does not exist in the InsuranceManager table");
                            } else {
                                int InsuranceManagerId = rs3.getInt("id"); // Get the InsuranceManager ID

                                String sql4 = "SELECT * FROM \"InsuranceSurveyor\" WHERE \"userID\" = ?";
                                PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                stmt4.setInt(1, userId);
                                ResultSet rs4 = stmt4.executeQuery();

                                int InsuranceSurveyorId = 0;
                                if (!rs4.next()) {
                                    // Handle the case where the user ID does not exist in the InsuranceSurveyor table
                                    System.out.println("The user does not exist in the InsuranceSurveyor table");
                                } else {
                                    InsuranceSurveyorId = rs4.getInt("id");
                                }
                                // Insert a new row into the InsuranceManager_InsuranceSurveyor table
                                String sql6 = "INSERT INTO \"InsuranceManager_InsuranceSurveyor\" (\"InsuranceManager\", \"InsuranceSurveyor\") VALUES (?, ?)";
                                PreparedStatement stmt6 = conn.prepareStatement(sql6);
                                stmt6.setInt(1, InsuranceManagerId); // ID of the related InsuranceManager
                                stmt6.setInt(2, InsuranceSurveyorId); // ID of the new InsuranceSurveyor
                                stmt6.executeUpdate();
                                CreateUserRecord();
                                ProviderTable();
                                System.out.println("Created a new InsuranceSurveyor in the InsuranceManager_InsuranceSurveyor table");
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
                        int providerId = Integer.parseInt(updateProviderId.getText());
                        String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, providerId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Update Provider notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Provider does not exist");
                            alert.showAndWait();
                        } else {
                            // Check if the new email already exists
                            if (!updateProviderEmail.getText().isEmpty()) {
                                String checkEmailSql = "SELECT * FROM \"User\" WHERE \"email\" = ? AND \"id\" != ?";
                                PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql); // Create a new PreparedStatement
                                checkEmailStmt.setString(1, updateProviderEmail.getText());
                                checkEmailStmt.setInt(2, providerId);
                                ResultSet checkEmailRs = checkEmailStmt.executeQuery(); // Execute the new query with the new PreparedStatement
                                if (checkEmailRs.next()) {
                                    // If the email already exists and does not belong to the current user, show an alert and return
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Update Provider notification");
                                    alert.setHeaderText(null);
                                    alert.setContentText("The email already exists");
                                    alert.showAndWait();
                                    return;
                                }
                            }
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
                                alert.setContentText("Please enter all field");
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
                            stmt.setInt(index, providerId);
                            stmt.executeUpdate();
                            System.out.println("Updated the Provider");
                            UpdateProviderRecord(providerId);
                            ProviderTable();
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
                            DeleteProviderRecord(providerId);
                            ProviderTable();
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
                            if (userType.equals("InsuranceManager")) {
                                RetrieveProviderRecord(providerId, "InsuranceManager");
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
                            if (userType.equals("InsuranceSurveyor")) {
                                RetrieveProviderRecord(providerId, "InsuranceSurveyor");
                                sql = "SELECT U.*, U_IM.\"name\" as IMName, U_IM.\"email\" as IMEmail FROM \"User\" U " +
                                        "JOIN \"InsuranceSurveyor\" INSURV ON U.\"id\" = INSURV.\"userID\" " +
                                        "JOIN \"InsuranceManager_InsuranceSurveyor\" IM_INSURV ON INSURV.\"id\" = IM_INSURV.\"InsuranceSurveyor\" " +
                                        "JOIN \"InsuranceManager\" IM ON IM_INSURV.\"InsuranceManager\" = IM.\"id\" " +
                                        "JOIN \"User\" U_IM ON IM.\"userID\" = U_IM.\"id\" " +
                                        "WHERE U.\"id\" = ?";
                                stmt = conn.prepareStatement(sql);
                                stmt.setInt(1, providerId);
                                rs = stmt.executeQuery();
                                if (rs.next()) {
                                    String userDetails = "Userid: " + rs.getInt("id") + "\n" +
                                            "Name: " + rs.getString("name") + "\n" +
                                            "Email: " + rs.getString("email") + "\n" +
                                            "Password: " + rs.getString("password") + "\n" +
                                            "UserType: " + rs.getString("userType") + "\n" +
                                            "InsuranceManager Dependency" + "\n" +
                                            "InsuranceManager Name: " + rs.getString("IMName") + "\n" +
                                            "InsuranceManager Email: " + rs.getString("IMEmail");
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

    public void ProviderTable() {
        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                if (conn != null) {
                    String sql = "SELECT U.\"id\", U.\"name\", U.\"email\", U.\"password\", U.\"userType\", " +
                            "CASE " +
                            "WHEN U.\"userType\" = 'InsuranceSurveyor' THEN IM2.\"userID\" " +
                            "END AS \"idDependency\" " +
                            "FROM \"User\" U " +
                            "LEFT JOIN \"InsuranceManager\" IM1 ON U.\"id\" = IM1.\"userID\" " +
                            "LEFT JOIN \"InsuranceSurveyor\" INSURV ON U.\"id\" = INSURV.\"userID\" " +
                            "LEFT JOIN \"InsuranceManager_InsuranceSurveyor\" IM_INSURV ON INSURV.\"id\" = IM_INSURV.\"InsuranceSurveyor\" " +
                            "LEFT JOIN \"InsuranceManager\" IM2 ON IM_INSURV.\"InsuranceManager\" = IM2.\"id\" " +
                            "LEFT JOIN \"User\" IM_U ON IM2.\"userID\" = IM_U.\"id\" " +
                            "WHERE U.\"userType\" IN ('InsuranceManager', 'InsuranceSurveyor')";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();

                    // Clear existing data
                    Platform.runLater(() -> providerTable.getItems().clear());

                    // Add data to table
                    while (rs.next()) {
                        Provider provider = new Provider(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("userType"),
                                rs.getString("idDependency")
                        );
                        Platform.runLater(() -> providerTable.getItems().add(provider));
                    }

                    // Set column data
                    Platform.runLater(() -> {
                        providerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                        providerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                        providerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                        providerPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
                        providerUserTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
                        providerIdDependencyColumn.setCellValueFactory(new PropertyValueFactory<>("idDependency"));
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void ClaimTable(){
        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                String sql = "SELECT C.* FROM \"Claim\" C";

                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                // Clear existing data
                claimTable.getItems().clear();

                // Add data to table
                while (rs.next()) {
                    Claim claim = new Claim(
                            rs.getString("id"),
                            rs.getString("Claim_Date"),
                            rs.getString("Exam_Date"),
                            rs.getString("Claim_amount"),
                            rs.getString("Insured_Person"),
                            rs.getString("Status"),
                            rs.getString("Documents"),
                            rs.getString("Receiver_Banking_Infor")
                    );
                    // Update UI on JavaFX Application Thread
                    Platform.runLater(() -> {
                        claimTable.getItems().add(claim);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void Logout() throws IOException {
        System.out.println("Logout button clicked."); // Debug line
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/main_folder/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void updateRecordMethod(){
        RecordTable();
    }
    public void RecordTable(){
        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                String sql = "SELECT R.* FROM \"Record\" R";

                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                // Clear existing data
                recordTable.getItems().clear();

                // Add data to table
                while (rs.next()) {
                    Record record = new Record(
                            rs.getString("id"),
                            rs.getString("record"),
                            rs.getString("created_at")
                    );
                    // Update UI on JavaFX Application Thread
                    Platform.runLater(() -> {
                        recordTable.getItems().add(record);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    //    record section
    private void LoginRecord(){
        executorService.submit(() -> {
            String loggedInUserId = loginController.getLoggedInUser();
            String record = "Admin logged in with id " + loggedInUserId;

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

                    String record = userType + " with the id of " + id + " has been created by Admin id " + loggedInUserId;

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
            String record = "Customer with the id of " + id + " has been updated by Admin id " + loginController.getLoggedInUser();

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
            String record = "Customer with the id of " + id + " has been deleted by Admin id " + loginController.getLoggedInUser();

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
            String record = userType + " with the id of " + id + " has been retrieved by Admin id " + loginController.getLoggedInUser();

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
    private void UpdateProviderRecord(int id){
        executorService.submit(() -> {
            String record = "Provider with the id of " + id + " has been updated by Admin id " + loginController.getLoggedInUser();

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
    private void DeleteProviderRecord(int id){
        executorService.submit(() -> {
            String record = "Provider with the id of " + id + " has been deleted by Admin id " + loginController.getLoggedInUser();

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
    private void RetrieveProviderRecord(int id, String userType){
        executorService.submit(() -> {
            String record = userType + "with the id of " + id + " has been retrieved by Admin id " + loginController.getLoggedInUser();

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
}