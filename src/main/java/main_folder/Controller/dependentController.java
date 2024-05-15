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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class dependentController extends Thread implements Initializable {
    //Buttons
    @FXML
    private Button logoutBTN;

    //Stuff for Dependent View
    @FXML
    private TableView<Dependent> infoTable;
    private ObservableList<Dependent> dependentData = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Dependent, String> dependentEmailColumn;
    @FXML
    private TableColumn<Dependent, String> dependentIdColumn;
    @FXML
    private TableColumn<Dependent, String> dependentNameColumn;
    @FXML
    private TableColumn<Dependent, String> dependentPolicyNumberColumn;

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


    public void RetrieveDependent() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null){
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                    String sql2 = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
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
                        if (rs2.next()) {
                            Dependent dependent = new Dependent(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("email"),
                                    rs2.getString("policyNumber")
                            );
                            System.out.println("Dependent object created: " + dependent); // Debug line
                            dependentData.add(dependent);
                            System.out.println("Dependent object added to ObservableList."); // Debug line
                            System.out.println("Size of dependentData: " + dependentData.size()); // Debug line
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

        infoTable.setItems(dependentData);
        System.out.println("TableView updated with ObservableList."); // Debug line
    }

    //The base code that I reference is meant to insert an ID then a new window pops one out. This will not work.
    public void RetrieveClaim() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
                    String sql2 = "SELECT * FROM \"Customer\" WHERE \"id\" = ?";
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
                        int policyNumber = rs.getInt("policyNumber");
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, policyNumber);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (!rs2.next()){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Claim notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Claim corresponds to this id cannot be found");
                            alert.showAndWait();
                        }
                        else {
                            int customerId = rs2.getInt("id");
                            System.out.println("SQL Query: " + sql3);
                            System.out.println("Parameter: " + customerId);
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, customerId);
                            ResultSet rs3 = stmt3.executeQuery();
                            if (rs3.next()){
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
                                System.out.println("Claim object created: " + claim); // Debug line
                                System.out.println("Claim object added to ObservableList."); // Debug line
                                System.out.println("Size of claimData: " + claimData.size()); // Debug line
                            } else {
                                // handle case where no rows were returned
                                System.out.println("No claims found for customer ID: " + customerId);
                            }
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
                dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
            }
        };

        // Create Runnable for RetrieveClaim
        Runnable retrieveClaimRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveClaim();
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                claimDateColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_Date"));
                examDateColumn.setCellValueFactory(new PropertyValueFactory<>("Exam_Date"));
                claimAmountColumn.setCellValueFactory(new PropertyValueFactory<>("Claim_amount"));
                insuredPersonColumn.setCellValueFactory(new PropertyValueFactory<>("Insured_Person"));
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
                documentsColumn.setCellValueFactory(new PropertyValueFactory<>("Documents"));
                receiverBankingInfoColumn.setCellValueFactory(new PropertyValueFactory<>("Receiver_Banking_Infor"));
            }
        };

        // Create Thread objects
        Thread retrieveDependentThread = new Thread(retrieveDependentRunnable);
        Thread retrieveClaimThread = new Thread(retrieveClaimRunnable);

        // Start the threads
        retrieveDependentThread.start();
        retrieveClaimThread.start();
    }
}
