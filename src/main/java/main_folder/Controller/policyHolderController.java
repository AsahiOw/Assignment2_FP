/**
 * @Nguyen Ha Tuan Nguyen - s3978072
 * @Luong Tuan Kiet - s3980288
 * @Tran Tuan Minh - s3804812
 * @Nguyen Thanh Tung - s3979489
 * <Ooptional>
 */
package main_folder.Controller;

/**
 * @Tran Tuan Minh <OOPtional>
 */

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Dependent;
import main_folder.Model.PolicyHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class policyHolderController implements Initializable {
    private String cachedPolicyNumber;
    private String cachedHolderName;
    private String newFileName;
    //Buttons
    @FXML
    private Button logoutBTN;

    //Other Stuff
    private ObservableList<PolicyHolder> holderData = FXCollections.observableArrayList();
    private ObservableList<Dependent> dependentData = FXCollections.observableArrayList();
    @FXML
    private TextArea holderInfo;
    @FXML
    private TableView<Dependent> dependentTable = new TableView<>();
    @FXML
    private TableColumn<Dependent, String> dependentIdColumn;
    @FXML
    private TableColumn<Dependent, String> dependentNameColumn;
    @FXML
    private TableColumn<Dependent, String> dependentEmailColumn;
    @FXML
    private TableColumn<Dependent, String> dependentPolicyNumberColumn;

    //Claim Injection
    private ObservableList<Claim> claimData = FXCollections.observableArrayList();
    private ObservableList<Claim> dependentClaimData = FXCollections.observableArrayList();
    @FXML
    private TableView<Claim> holderClaimTable = new TableView<>();
    @FXML
    private TableColumn<Claim, String> idColumn;
    @FXML
    private TableColumn<Claim, String> claimDateColumn;
    @FXML
    private TableColumn<Claim, String> examDateColumn;
    @FXML
    private TableColumn<Claim, String> claimAmountColumn;
    @FXML
    private TableColumn<Claim, String> insuredPersonColumn;
    @FXML
    private TableColumn<Claim, String> statusColumn;
    @FXML
    private TableColumn<Claim, String> documentsColumn;
    @FXML
    private TableColumn<Claim, String> receiverBankingInfoColumn;

    //Dependent Claim Injection
    @FXML
    private TableView<Claim> dependentClaimTable = new TableView<>();
    @FXML
    private TableColumn<Claim, String> idDependentColumn;
    @FXML
    private TableColumn<Claim, String> claimDateDependentColumn;
    @FXML
    private TableColumn<Claim, String> examDateDependentColumn;
    @FXML
    private TableColumn<Claim, String> claimAmountDependentColumn;
    @FXML
    private TableColumn<Claim, String> insuredPersonDependentColumn;
    @FXML
    private TableColumn<Claim, String> statusDependentColumn;
    @FXML
    private TableColumn<Claim, String> documentsDependentColumn;
    @FXML
    private TableColumn<Claim, String> receiverBankingInfoDependentColumn;


    //Create Claim injections
    @FXML
    private Button submitCreateClaimDependent;
    @FXML
    private Button submitCreateClaim;
    @FXML
    private TextField claimDateField;
    @FXML
    private TextField examDateField;
    @FXML
    private TextField claimAmountField;
    @FXML
    private TextField bankingInfoField;
    @FXML
    private TextField insuredDependField;
    @FXML
    private TextField claimDateDependField;
    @FXML
    private TextField examDateDependField;
    @FXML
    private TextField claimAmountDependField;
    @FXML
    private TextField bankingInfoDependField;

    //Update Injection
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField idDependUpdateField;
    @FXML
    private TextField emailDependField;
    @FXML
    private TextField passwordDependField;
    @FXML
    private Button submitUpdate;
    @FXML
    private Button updateDependent;
    @FXML
    private TextField idClaimToFind;
    @FXML
    private TextField claimDateUpdateField;
    @FXML
    private TextField amountUpdateField;
    @FXML
    private TextField bankUpdateField;


    /***
     * =========================================
     * This is all for the View Information Tab
     * =========================================*/
    public void RetrieveHolder() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null){
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                    String sql2 = "SELECT * FROM \"PolicyHolder\" WHERE \"userID\" = ?";
                    String sql3 = "SELECT * FROM \"Customer\" WHERE \"id\" = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, dependentId);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        // handle error
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Retrieve Customer notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Customer corresponds to this id cannot be found");
                        alert.showAndWait();
                    } else {
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, dependentId);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (!rs2.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Dependent notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Dependent corresponds to this id cannot be found");
                            alert.showAndWait();
                        }else {
                            int customerId = rs2.getInt("policyNumber");
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, customerId);
                            ResultSet rs3 = stmt3.executeQuery();
                            if (rs3.next()) {
                                PolicyHolder holder = new PolicyHolder(
                                        rs.getString("id"),
                                        rs.getString("name"),
                                        rs.getString("email"),
                                        rs3.getString("InsuranceNumber")
                                );
                                holderData.add(holder);
                                cachedPolicyNumber = rs2.getString("policyNumber");
                                cachedHolderName = rs.getString("name");

                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // handle error
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Customer corresponds to this id does not exist");
                    alert.showAndWait();
                }
                // Update the UI on the JavaFX Application thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        for (PolicyHolder holder : holderData) {
                            holderInfo.setText(holder.toString());
                        }
                    }
                });
            }
        }
        catch( SQLException e){
            e.printStackTrace();
        }
        for (PolicyHolder holder : holderData) {
            holderInfo.setText(holder.toString());
        }
    }

    public void RetrieveDependents() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int userId = Integer.parseInt(loginController.getLoggedInUser());

                    // Prepare the SQL SELECT statement
                    String sql = "SELECT U.id, U.name, U.email, C_D.\"InsuranceNumber\" " +
                                 "FROM \"User\" U " +
                                 "LEFT JOIN \"Dependent\" D ON U.\"id\" = D.\"userID\" " +
                                 "LEFT JOIN \"Customer\" C_D ON D.\"policyNumber\" = C_D.\"id\" " +
                                 "LEFT JOIN \"PolicyHolder_Dependent\" PD ON D.\"id\" = PD.\"Dependent\" " +
                                 "LEFT JOIN \"PolicyHolder\" PH_U ON PD.\"PolicyHolder\" = PH_U.\"id\" " +
                                 "LEFT JOIN \"User\" UH ON PH_U.\"userID\" = UH.id " +
                                 "WHERE UH.id = ?";

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Dependent dependent = new Dependent(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("InsuranceNumber")
                        );
                        dependentData.add(dependent);
                    }
                }
                catch (NumberFormatException e) {
                    // handle error
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Customer corresponds to this id does not exist");
                    alert.showAndWait();
                }
                // Update the UI on the JavaFX Application thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dependentTable.setItems(dependentData);
                        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                        dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
                    }
                });
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RetrieveClaim() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null){
                System.out.println("Connected to the database");
                try {
                    int policy_holder_id = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"PolicyHolder\" WHERE \"userID\" = ?";
                    String sql2 = "SELECT * FROM \"Claim\" WHERE \"Insured_Person\" = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, policy_holder_id);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()){
                        //Handle Error
                        System.out.println("No PolicyHolder found with the given userID");
                    } else {
                        int customerId = rs.getInt("policyNumber");
                        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                        pstmt2.setInt(1, customerId);
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (!rs2.next()){
                            //Handle Error
                            System.out.println("No Claim found with the given Insured_Person");
                        } else {
                            //Display Claim
                            while (rs2.next()){
                                Claim claim = new Claim(
                                        rs2.getString("id"),
                                        rs2.getString("Claim_Date"),
                                        rs2.getString("Exam_Date"),
                                        rs2.getString("Claim_amount"),
                                        rs2.getString("Insured_Person"),
                                        rs2.getString("Status"),
                                        rs2.getString("Documents"),
                                        rs2.getString("Receiver_Banking_Infor")
                                );
                                claimData.add(claim);
                                System.out.println("Claim added"); // Debug line
                            }
                        }
                    }
                }
                catch (NumberFormatException e) {
                    //Handle Error
                    System.out.println("Error parsing userID to integer");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        holderClaimTable.setItems(claimData);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        claimDateColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_Date"));
        examDateColumn.setCellValueFactory(new PropertyValueFactory<>("Exam_Date"));
        claimAmountColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_amount"));
        insuredPersonColumn.setCellValueFactory(new PropertyValueFactory<>("Insured_Person"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
        documentsColumn.setCellValueFactory(new PropertyValueFactory<>("Documents"));
        receiverBankingInfoColumn.setCellValueFactory(new PropertyValueFactory<>("Receiver_Banking_Infor"));
    }

    public void RetrieveDependentClaim() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int userId = Integer.parseInt(loginController.getLoggedInUser());

                    // Prepare the SQL SELECT statement
                    String sql = "SELECT C.* " +
                            "FROM \"Claim\" C " +
                            "JOIN \"Dependent\" D ON C.\"Insured_Person\" = D.\"policyNumber\" " +
                            "JOIN \"PolicyHolder_Dependent\" PD ON D.\"id\" = PD.\"Dependent\" " +
                            "JOIN \"PolicyHolder\" PH_U ON PD.\"PolicyHolder\" = PH_U.\"id\" " +
                            "JOIN \"User\" UH ON PH_U.\"userID\" = UH.id " +
                            "WHERE UH.id = ?";

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

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
                        dependentClaimData.add(claim);
                    }
                }
                catch (NumberFormatException e) {
                    // handle error
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Dependent Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Claims corresponds to this User does not exist");
                    alert.showAndWait();
                }
                // Update the UI on the JavaFX Application thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dependentClaimTable.setItems(dependentClaimData);
                        idDependentColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                        claimDateDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_Date"));
                        examDateDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Exam_Date"));
                        claimAmountDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_amount"));
                        insuredPersonDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Insured_Person"));
                        statusDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
                        documentsDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Documents"));
                        receiverBankingInfoDependentColumn.setCellValueFactory(new PropertyValueFactory<>("Receiver_Banking_Infor"));
                    }
                });
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * =========================================
     * This is for the File Claim Tab
     * =========================================
     * @return*/
    //////////////////////////////////////////////
    public int CreateClaim() {
        database db = new database();
        int generatedClaimId = -1;
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (claimDateField.getText().isEmpty() || examDateField.getText().isEmpty() || claimAmountField.getText().isEmpty() || bankingInfoField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                }
                else {
                    // Get the values from the text fields
                    Date claimDate = Date.valueOf(claimDateField.getText());
                    Date examDate = Date.valueOf(examDateField.getText());
                    Float claimAmount = Float.valueOf(claimAmountField.getText());
                    String bankingInfo = bankingInfoField.getText();
                    Integer insuredPerson = Integer.valueOf(cachedPolicyNumber);
                    String status = "Processing";
                    String documents = "Documents.pdf";


                    // Prepare the SQL INSERT statement
                    String sql = "INSERT INTO \"Claim\" (\"Claim_Date\", \"Exam_Date\", \"Claim_amount\", \"Insured_Person\", \"Status\", \"Documents\", \"Receiver_Banking_Infor\") VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setDate(1, claimDate);
                    pstmt.setDate(2, examDate);
                    pstmt.setFloat(3, claimAmount);
                    pstmt.setInt(4, insuredPerson);
                    pstmt.setString(5, status);
                    pstmt.setString(6, documents);
                    pstmt.setString(7, bankingInfo);

                    // Execute the SQL statement
                    pstmt.executeUpdate();

                    // Retrieve the generated claim ID
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        generatedClaimId = rs.getInt(1);
                    }

                    System.out.println("Claim created successfully");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedClaimId;
    }

    public void createRecord() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                // Create a new claim and get its ID
                int claimId = CreateClaim();

                // Prepare the record string
                String record = cachedHolderName + " created claim with an ID of: " + claimId;

                // Prepare the SQL INSERT statement
                String sql = "INSERT INTO \"Record\" (\"record\") VALUES (?)";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, record);

                // Execute the SQL statement
                pstmt.executeUpdate();

                System.out.println("Record created successfully");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int CreateDependentClaim() {
        database db = new database();
        int generatedClaimId = -1;
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (claimDateDependField.getText().isEmpty() || examDateDependField.getText().isEmpty() || claimAmountDependField.getText().isEmpty() || bankingInfoDependField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Dependent Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                }
                else {
                    // Get the values from the text fields
                    Date claimDate = Date.valueOf(claimDateDependField.getText());
                    Date examDate = Date.valueOf(examDateDependField.getText());
                    Float claimAmount = Float.valueOf(claimAmountDependField.getText());
                    String bankingInfo = bankingInfoDependField.getText();
                    Integer insuranceNumber = Integer.valueOf(insuredDependField.getText()); // Use the policy number of the selected dependent
                    String status = "Processing";
                    String documents = "Documents.pdf";

                    // Prepare the SQL INSERT statement
                    String sql = "INSERT INTO \"Claim\" (\"Claim_Date\", \"Exam_Date\", \"Claim_amount\", \"Insured_Person\", \"Status\", \"Documents\", \"Receiver_Banking_Infor\") VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    String sql2 = "SELECT * FROM \"Customer\" WHERE \"InsuranceNumber\" = ?";
                    PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                    pstmt2.setInt(1, insuranceNumber);
                    ResultSet rs = pstmt2.executeQuery();
                    // Retrieve the generated claim ID
                    ResultSet rs2 = pstmt.getGeneratedKeys();

                    if (rs.next()) {
                        int insuredPerson = Integer.parseInt(rs.getString("id"));

                        String checkSql = "SELECT * FROM \"PolicyHolder_Dependent\" WHERE \"Dependent\" = ? AND \"PolicyHolder\" = ?";
                        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                        checkStmt.setInt(1, insuredPerson); // assuming insuranceNumber is the dependent's ID
                        checkStmt.setInt(2, Integer.parseInt(loginController.getLoggedInUser())); // assuming this is the policy holder's ID

                        ResultSet checkRs = checkStmt.executeQuery();
                        if (!checkRs.next()) {
                            // The dependent is not associated with the policy holder
                            System.out.println("This dependent is not associated with the current policy holder.");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Create Dependent Claim notification");
                            alert.setHeaderText(null);
                            alert.setContentText("This is not your dependent. Please select a dependent associated with your policy.");
                            alert.showAndWait();
                            return -1;
                        } else {
                            pstmt.setDate(1, claimDate);
                            pstmt.setDate(2, examDate);
                            pstmt.setFloat(3, claimAmount);
                            pstmt.setInt(4, insuredPerson);
                            pstmt.setString(5, status);
                            pstmt.setString(6, documents);
                            pstmt.setString(7, bankingInfo);

                            // Execute the SQL statement
                            pstmt.executeUpdate();

                            System.out.println("Dependent claim created successfully");
                            if (rs2.next()) {
                                generatedClaimId = rs2.getInt(1);
                            }
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedClaimId;
    }

    public void createRecordDependent() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                // Create a new claim and get its ID
                int claimId = CreateDependentClaim();

                // Prepare the record string
                String record = cachedHolderName + " created claim with an ID of: " + claimId;

                // Prepare the SQL INSERT statement
                String sql = "INSERT INTO \"Record\" (\"record\") VALUES (?)";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, record);

                // Execute the SQL statement
                pstmt.executeUpdate();

                System.out.println("Record created successfully");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * =========================================
     * This is for the Update Information Tab
     * =========================================*/
    //////////////////////////////////////////////
    public void UpdateHolder() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (holderInfo.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Holder notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                }
                else {
                    // Get the values from the text fields
                    String newEmail = emailField.getText();
                    String newPassword = passwordField.getText();
                    int holderId = Integer.parseInt(loginController.getLoggedInUser());

                    // Prepare the SQL UPDATE statement
                    String sql = "UPDATE \"User\" SET \"email\" = ?, \"password\" = ? WHERE \"id\" = ?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newEmail);
                    pstmt.setString(2, newPassword);
                    pstmt.setInt(3, holderId);

                    // Execute the SQL statement
                    pstmt.executeUpdate();

                    System.out.println("Holder updated successfully");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateDependent() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (idDependUpdateField.getText().isEmpty() && emailDependField.getText().isEmpty() && passwordDependField.getText().isEmpty()){
                    System.out.println("Please select a dependent");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Dependent notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill in all the new information");
                    alert.showAndWait();
                }
                else {
                    // Get the values from the text fields
                    String newEmail = emailDependField.getText();
                    String newPassword = passwordDependField.getText();
                    int dependentId = Integer.parseInt(idDependUpdateField.getText());

                    // Prepare the SQL UPDATE statement
                    String sql = "UPDATE \"User\" SET \"email\" = ?, \"password\" = ? WHERE \"id\" = ?";
                    String checkSql = "SELECT * FROM \"PolicyHolder_Dependent\" WHERE \"Dependent\" = ? AND \"PolicyHolder\" = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setInt(1, Integer.parseInt(idDependUpdateField.getText())); // assuming this is the dependent's ID
                    checkStmt.setInt(2, Integer.parseInt(loginController.getLoggedInUser())); // assuming this is the policy holder's ID

                    ResultSet checkRs = checkStmt.executeQuery();
                    if (!checkRs.next()) {
                        // The dependent is not associated with the policy holder
                        System.out.println("This dependent is not associated with the current policy holder.");
                        return;
                    } else {

                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, newEmail);
                        pstmt.setString(2, newPassword);
                        pstmt.setInt(3, dependentId);

                        // Execute the SQL statement
                        pstmt.executeUpdate();

                        System.out.println("Dependent updated successfully");
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateClaim() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (idClaimToFind.getText().isEmpty() && claimDateUpdateField.getText().isEmpty() && amountUpdateField.getText().isEmpty() && bankUpdateField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                }
                else {
                    // Get the values from the text fields
                    Date newExamDate = Date.valueOf(claimDateUpdateField.getText());
                    Float newAmount = Float.valueOf(amountUpdateField.getText());
                    String newBankingInfo = bankUpdateField.getText();
                    int claimId = Integer.parseInt(idClaimToFind.getText());

                    // Check if the claim is associated with the policy holder or his dependents
                    String checkSql = "SELECT C.* " +
                            "FROM \"Claim\" C " +
                            "JOIN \"Dependent\" D ON C.\"Insured_Person\" = D.\"policyNumber\" " +
                            "JOIN \"PolicyHolder_Dependent\" PD ON D.\"id\" = PD.\"Dependent\" " +
                            "JOIN \"PolicyHolder\" PH_U ON PD.\"PolicyHolder\" = PH_U.\"id\" " +
                            "JOIN \"User\" UH ON PH_U.\"userID\" = UH.id " +
                            "WHERE (UH.id = ? OR C.\"Insured_Person\" = ?) AND C.\"id\" = ?";

                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setInt(1, Integer.parseInt(loginController.getLoggedInUser())); // assuming this is the policy holder's ID
                    checkStmt.setInt(2, Integer.parseInt(cachedPolicyNumber)); // assuming this is the policy holder's policy number
                    checkStmt.setInt(3, claimId);

                    ResultSet checkRs = checkStmt.executeQuery();
                    if (!checkRs.next()) {
                        // The claim is not associated with the policy holder or his dependents
                        System.out.println("This claim is not associated with the current policy holder or his dependents.");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Update Claim notification");
                        alert.setHeaderText(null);
                        alert.setContentText("This is not your claim or your dependent's claim. Please select a claim associated with your policy.");
                        alert.showAndWait();
                        return;
                    }

                    // Prepare the SQL UPDATE statement
                    String sql = "UPDATE \"Claim\" SET \"Exam_Date\" = ?, \"Claim_amount\" = ?, \"Receiver_Banking_Infor\" = ? WHERE \"id\" = ?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setDate(1, newExamDate);
                    pstmt.setFloat(2, newAmount);
                    pstmt.setString(3, newBankingInfo);
                    pstmt.setInt(4, claimId);

                    // Execute the SQL statement
                    pstmt.executeUpdate();

                    System.out.println("Claim updated successfully");

                    // Prepare the record string
                    String record = cachedHolderName + " update claim with an ID of: " + claimId;

                    // Prepare the SQL INSERT statement
                    String sql2 = "INSERT INTO \"Record\" (\"record\") VALUES (?)";

                    PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                    pstmt2.setString(1, record);

                    // Execute the SQL statement
                    pstmt2.executeUpdate();

                    System.out.println("Record created successfully");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /***
     * =========================================
     * This is for various buttons that needs it's own logic.
     * =========================================*/
    public void Logout() throws IOException {
        System.out.println("Logout button clicked."); // Debug line
        Stage stage = (Stage) logoutBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/main_folder/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void LoginRecord(){
        String loggedInUserId = loginController.getLoggedInUser();
        String record = "PolicyHolder logged in with id " + loggedInUserId;

        database db = new database();
        try (Connection conn = db.connect()) {
            String sql = "INSERT INTO \"Record\" (\"record\") VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, record);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LoginRecord();
        //Create Runnable for Holder methods
        Runnable retrieveHolderRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveHolder();
                RetrieveClaim();
            }
        };

        //Create Runnable for Dependent methods
        Runnable retrieveDependentRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveDependents();
                RetrieveDependentClaim();
            }
        };

        //Create Thread Objects
        Thread retrieveHolderThread = new Thread(retrieveHolderRunnable);
        Thread retrieveDependentThread = new Thread(retrieveDependentRunnable);

        //Start the Threads
        retrieveHolderThread.start();
        retrieveDependentThread.start();
    }
}
