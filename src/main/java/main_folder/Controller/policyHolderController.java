package main_folder.Controller;

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
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Dependent;
import main_folder.Model.PolicyHolder;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class policyHolderController implements Initializable {
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
    private String dependentPolicyNumber;
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
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql1 = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                    String sql2 = "SELECT * FROM \"PolicyHolder_Dependent\" WHERE \"PolicyHolder\" = ?";
                    String sql3 = "SELECT * FROM \"PolicyHolder\" WHERE \"id\" = ?";
                    String sql4 = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
                    PreparedStatement stmt1 = conn.prepareStatement(sql1);
                    stmt1.setInt(1, dependentId);
                    ResultSet rs1 = stmt1.executeQuery();
                    if (!rs1.next()){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Retrieve Dependent notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Dependent corresponds to this id cannot be found");
                        alert.showAndWait();
                    }
                    else {
                        int relationship = rs1.getInt("id");
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, relationship);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (!rs2.next()){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Holder notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Holder corresponds to this id cannot be found");
                            alert.showAndWait();
                        }
                        else {
                            int holderId = rs2.getInt("Dependent");
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, holderId);
                            ResultSet rs3 = stmt3.executeQuery();
                            while (rs3.next()){
                                int userId = rs3.getInt("userID");
                                PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                stmt4.setInt(1, userId);
                                ResultSet rs4 = stmt4.executeQuery();
                                if (rs4.next()){
                                    Dependent dependent = new Dependent(
                                            rs4.getString("id"),
                                            rs4.getString("name"),
                                            rs4.getString("email"),
                                            rs3.getString("policyNumber")
                                    );
                                    dependentPolicyNumber = rs3.getString("policyNumber");
                                    dependentData.add(dependent);
                                }
                            }
                        }
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
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        dependentTable.setItems(dependentData);
        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
    }

    public void RetrieveClaim() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null){
                System.out.println("Connected to the database");
                try {
                    int policy_holder_id = Integer.getInteger(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"PolicyHolder\" WHERE \"userID\" = ?";
                    String sql2 = "SELECT * FROM \"Claim\" WHERE \"Insured_Person\" = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, policy_holder_id);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()){
                        //Handle Error
                    } else {
                        int customerId = rs.getInt("policyNumber");
                        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                        pstmt2.setInt(1, customerId);
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (!rs2.next()){
                            //Handle Error
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
                            }
                        }
                    }
                }
                catch (NumberFormatException e) {
                    //Handle Error
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
            if (conn != null){
                try {
                    String sql = "SELECT * FROM \"Claim\" WHERE \"Insured_Person\" = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, dependentPolicyNumber);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()){
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
                    //Handle Error
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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

    /***
     * =========================================
     * This is for the File Claim Tab
     * =========================================*/
    //////////////////////////////////////////////

    /***
     * =========================================
     * This is for the Update Information Tab
     * =========================================*/
    //////////////////////////////////////////////

    /***
     * =========================================
     * This is for various buttons
     * =========================================*/
    public void Logout() throws IOException {
        System.out.println("Logout button clicked."); // Debug line
        loginController.setLoggedInUser(null);
        Stage stage = (Stage) logoutBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/main_folder/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
