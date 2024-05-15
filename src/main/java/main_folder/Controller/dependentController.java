package main_folder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Dependent;
import main_folder.Model.Claim;
import main_folder.Model.PolicyHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class dependentController extends Thread implements Initializable {
    //Buttons
    @FXML
    private Button logoutBTN;

    //Stuff for Dependent View
    private ObservableList<Dependent> dependentData = FXCollections.observableArrayList();
    private ObservableList<PolicyHolder> holderData = FXCollections.observableArrayList();
    @FXML
    private TextArea dependentInfo;
    @FXML
    private TextArea dependentHolder;

    //Stuff for Claim View
    @FXML
    private TableView<Claim> claimTable;
    private ObservableList<Claim> claimData = FXCollections.observableArrayList();
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


    //Will be using something else instead of TableView.
    public void RetrieveDependent() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null){
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                    String sql2 = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
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
                                Dependent dependent = new Dependent(
                                        rs.getString("id"),
                                        rs.getString("name"),
                                        rs.getString("email"),
                                        rs3.getString("InsuranceNumber")
                                );
                                dependentData.add(dependent);
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
        for (Dependent dependent : dependentData) {
            dependentInfo.appendText(dependent.toString() + "\n");
        }
    }

    public void RetrieveHolder() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql1 = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
                    String sql2 = "SELECT * FROM \"PolicyHolder_Dependent\" WHERE \"Dependent\" = ?";
                    String sql3 = "SELECT * FROM \"PolicyHolder\" WHERE \"id\" = ?";
                    String sql4 = "SELECT * FROM \"User\" WHERE \"id\" = ?";
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
                            int holderId = rs2.getInt("PolicyHolder");
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, holderId);
                            ResultSet rs3 = stmt3.executeQuery();
                            if (!rs3.next()){
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Retrieve Holder notification");
                                alert.setHeaderText(null);
                                alert.setContentText("The Holder corresponds to this id cannot be found");
                                alert.showAndWait();
                            }
                            else {
                                int userId = rs3.getInt("userID");
                                PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                stmt4.setInt(1, userId);
                                ResultSet rs4 = stmt4.executeQuery();
                                if (rs4.next()){
                                    PolicyHolder holder = new PolicyHolder(
                                            rs4.getString("id"),
                                            rs4.getString("name"),
                                            rs4.getString("email"),
                                            rs3.getString("policyNumber")
                                    );
                                    holderData.add(holder);
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
        for (PolicyHolder holder : holderData) {
            dependentHolder.appendText(holder.printForDependent() + "\n");
        }
    }

    // Retrieve the claim data from the database. Can shorten the queries, there's a redundant one.
    public void RetrieveClaim() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
                    String sql3 = "SELECT * FROM \"Claim\" WHERE \"Insured_Person\" = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, dependentId);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        // handle error
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Retrieve Claim notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Claim corresponds to this id cannot be found");
                        alert.showAndWait();
                    } else {
                        // handle success
                        int customerId = rs.getInt("policyNumber");
                        PreparedStatement stmt3 = conn.prepareStatement(sql3);
                        stmt3.setInt(1, customerId);
                        ResultSet rs3 = stmt3.executeQuery();
                        while (rs3.next()){
                            Claim claim = new Claim(
                                    rs3.getString("id"),
                                    rs3.getString("Claim_Date"),
                                    rs3.getString("Exam_Date"),
                                    rs3.getString("Claim_amount"),
                                    rs3.getString("Insured_Person"),
                                    rs3.getString("Status"),
                                    rs3.getString("Documents"),
                                    rs3.getString("Receiver_Banking_Infor")
                            );
                            claimData.add(claim);
                        }
                    }
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Claims corresponds to this Customer does not exist");
                    alert.showAndWait();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        claimTable.setItems(claimData);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        claimDateColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_Date"));
        examDateColumn.setCellValueFactory(new PropertyValueFactory<>("Exam_Date"));
        claimAmountColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_amount"));
        insuredPersonColumn.setCellValueFactory(new PropertyValueFactory<>("Insured_Person"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
        documentsColumn.setCellValueFactory(new PropertyValueFactory<>("Documents"));
        receiverBankingInfoColumn.setCellValueFactory(new PropertyValueFactory<>("Receiver_Banking_Infor"));
    }

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
        // Create Runnable for RetrieveDependent
        Runnable retrieveDependentRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveDependent();
            }
        };

        // Create Runnable for RetrieveClaim
        Runnable retrieveClaimRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveClaim();
            }
        };

        // Create Runnable for RetrieveHolder
        Runnable retrieveHolderRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveHolder();
            }
        };

        // Create Thread objects
        Thread retrieveDependentThread = new Thread(retrieveDependentRunnable);
        Thread retrieveClaimThread = new Thread(retrieveClaimRunnable);
        Thread retrieveHolderThread = new Thread(retrieveHolderRunnable);

        // Start the threads
        retrieveDependentThread.start();
        retrieveClaimThread.start();
        retrieveHolderThread.start();
    }
}
