package main_folder.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class insuranceSurveyorController {
    @FXML
    private Button logoutBTN;

    @FXML
    private TextField searchBarCustomer;

    @FXML
    private Button searchButtonCustomer;

    @FXML
    private TableView customerTable;

    @FXML
    private TableColumn customerIdColumn;

    @FXML
    private TableColumn customerNameColumn;

    @FXML
    private TableColumn customerEmailColumn;

    @FXML
    private TableColumn customerUserTypeColumn;

    @FXML
    private TableColumn customerInsuranceNumberColumn;

    @FXML
    private TableColumn customerIdDependencyColumn;

    @FXML
    private TextField RequestClaim;

    @FXML
    private Button SendRequestedClaim;

    @FXML
    private TextField searchBarClaim;

    @FXML
    private Button searchButtonClaim;

    @FXML
    private Label totalCost;

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
    private Label userIdLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userEmailLabel;

    public void initialize() {
        CustomerTable();
        setupClaimTableColumns();
        ClaimTable();
        userDetail();
        costCalculation();
        LoginRecord();
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

    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    public void Logout() throws IOException {
        System.out.println("Logout button clicked."); // Debug line
        Stage stage = (Stage) logoutBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/main_folder/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void SearchCustomer() {
        String searchTerm = searchBarCustomer.getText();

        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                String sql;
                PreparedStatement stmt;

                if (searchTerm == null || searchTerm.trim().isEmpty()) {
                    // If user does not input any value, show the default data
                    sql = "SELECT U.\"id\", U.\"name\", U.\"email\", U.\"password\", U.\"userType\", " +
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
                    stmt = conn.prepareStatement(sql);
                } else {
                    // If user inputs a value, search all columns in the User table
                    sql = "SELECT U.\"id\", U.\"name\", U.\"email\", U.\"password\", U.\"userType\", " +
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
                            "WHERE U.\"userType\" IN ('Dependent', 'PolicyHolder', 'PolicyOwner') AND (" +
                            "CAST(U.\"id\" AS TEXT) LIKE ? OR " +
                            "LOWER(U.\"name\") LIKE ? OR " +
                            "LOWER(U.\"email\") LIKE ? OR " +
                            "LOWER(U.\"password\") LIKE ? OR " +
                            "LOWER(U.\"userType\") LIKE ?)";
                    stmt = conn.prepareStatement(sql);
                    for (int i = 1; i <= 5; i++) {
                        stmt.setString(i, "%" + searchTerm + "%");
                    }
                }

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
                    customerUserTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
                    customerInsuranceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("InsuranceNumber"));
                    customerIdDependencyColumn.setCellValueFactory(new PropertyValueFactory<>("idDependency"));
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void CustomerTable(){
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

    public void SendRequest() {
        String claimId = RequestClaim.getText().trim();

        if (claimId.isEmpty()) {
            showAlert("Please enter all fields");
            return;
        }

        // Check if the input string can be parsed to a int
        try {
            Integer.parseInt(claimId);
        } catch (NumberFormatException e) {
            showAlert("This claim is invalid");
            return;
        }

        database db = new database();
        try (Connection conn = db.connect()) {
            String selectSql = "SELECT * FROM \"Claim\" WHERE \"id\" = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, Integer.parseInt(claimId));
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next() || !rs.getString("Status").equals("Processing")) {
                showAlert("This claim is invalid");
                return;
            }

            String updateSql = "UPDATE \"Claim\" SET \"Status\" = 'Pending' WHERE \"id\" = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, Integer.parseInt(claimId));
            updateStmt.executeUpdate();

            showAlert("Request successfully");
            RequestClaimRecord(claimId);
            ClaimTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    public void SearchClaim(){
        String searchTerm = searchBarClaim.getText();

        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                String sql;
                PreparedStatement stmt;

                if (searchTerm == null || searchTerm.trim().isEmpty()) {
                    // If user does not input any value, show the default data
                    sql = "SELECT C.* FROM \"Claim\" C ";
                    stmt = conn.prepareStatement(sql);
                } else {
                    // If user inputs a value, search all columns in the Claim table
                    sql = "SELECT C.* FROM \"Claim\" C WHERE (" +
                            "CAST(C.\"id\" AS TEXT) LIKE ? OR " +
                            "CAST(C.\"Claim_Date\" AS TEXT) LIKE ? OR " +
                            "CAST(C.\"Exam_Date\" AS TEXT) LIKE ? OR " +
                            "CAST(C.\"Claim_amount\" AS TEXT) LIKE ? OR " +
                            "CAST(C.\"Insured_Person\" AS TEXT) LIKE ? OR " +
                            "LOWER(C.\"Status\") LIKE ? OR " +
                            "LOWER(C.\"Documents\") LIKE ? OR " +
                            "LOWER(C.\"Receiver_Banking_Infor\") LIKE ?)";
                    stmt = conn.prepareStatement(sql);
                    for (int i = 1; i <= 8; i++) {
                        stmt.setString(i, "%" + searchTerm + "%");
                    }
                }

                ResultSet rs = stmt.executeQuery();

                // Clear existing data
                Platform.runLater(() -> claimTable.getItems().clear());

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
    private void userDetail(){
        String loggedInUserId = loginController.getLoggedInUser();

        if (loggedInUserId != null) {
            // Connect to the database and query the User table for a user with the given ID
            database db = new database();
            try (Connection conn = db.connect();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM \"User\" WHERE \"id\" = ?")) {
                stmt.setInt(1, Integer.parseInt(loggedInUserId));
                ResultSet rs = stmt.executeQuery();

                // If the user exists, update the labels with the user's details
                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");

                    // Update UI on JavaFX Application Thread
                    Platform.runLater(() -> {
                        userIdLabel.setText(loggedInUserId);
                        userNameLabel.setText(name);
                        userEmailLabel.setText(email);
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Handle case where no user is logged in
            Platform.runLater(() -> {
                userIdLabel.setText("No user logged in");
                userNameLabel.setText("No user logged in");
                userEmailLabel.setText("No user logged in");
            });
        }
    }
    private void costCalculation() {
        executorService.submit(() -> {
            database db = new database();
            try (Connection conn = db.connect()) {
                // Count the number of PolicyOwners
                String sqlPolicyOwners = "SELECT COUNT(*) FROM \"User\" WHERE \"userType\" = 'PolicyOwner'";
                PreparedStatement stmtPolicyOwners = conn.prepareStatement(sqlPolicyOwners);
                ResultSet rsPolicyOwners = stmtPolicyOwners.executeQuery();
                int countPolicyOwners = rsPolicyOwners.next() ? rsPolicyOwners.getInt(1) : 0;

                // Count the number of PolicyHolders
                String sqlPolicyHolders = "SELECT COUNT(*) FROM \"User\" WHERE \"userType\" = 'PolicyHolder'";
                PreparedStatement stmtPolicyHolders = conn.prepareStatement(sqlPolicyHolders);
                ResultSet rsPolicyHolders = stmtPolicyHolders.executeQuery();
                int countPolicyHolders = rsPolicyHolders.next() ? rsPolicyHolders.getInt(1) : 0;

                // Count the number of Dependents
                String sqlDependents = "SELECT COUNT(*) FROM \"User\" WHERE \"userType\" = 'Dependent'";
                PreparedStatement stmtDependents = conn.prepareStatement(sqlDependents);
                ResultSet rsDependents = stmtDependents.executeQuery();
                int countDependents = rsDependents.next() ? rsDependents.getInt(1) : 0;

                // Calculate the total cost
                int totalCosts = countPolicyOwners * 100 + countPolicyHolders * 100 + countDependents * 60;

                // Calculate the cost for each user type
                int costPolicyOwners = countPolicyOwners * 100;
                int costPolicyHolders = countPolicyHolders * 100;
                int costDependents = countDependents * 60;

                // Create the formatted string
                String formattedCosts = String.format("PolicyOwner: $%d\nPolicyHolder: $%d\nDependent: $%d\nTotal: $%d",
                        costPolicyOwners, costPolicyHolders, costDependents, totalCosts);

                // Update the "TotalCost" label
                Platform.runLater(() -> totalCost.setText(formattedCosts));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

//    record section
    private void LoginRecord(){
        executorService.submit(() -> {
            String loggedInUserId = loginController.getLoggedInUser();
            String record = "InsuranceSurveyor logged in with id " + loggedInUserId;

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

    private void RequestClaimRecord(String ClaimID){
        executorService.submit(() -> {
            String loggedInUserId = loginController.getLoggedInUser();
            String record = "InsuranceSurveyor with id " + loggedInUserId + " requested claim with id " + ClaimID;

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