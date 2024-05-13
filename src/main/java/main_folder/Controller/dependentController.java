package main_folder.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Dependent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class dependentController implements Initializable {
    private Integer testLogID = 10;

    //Stuff for Dependent View first
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
    private TextField retrieveClaimID;

    public void RetrieveDependent() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null){
                try {
                    int dependentId = testLogID;
                    String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
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
                        Dependent dependent = new Dependent(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("email")
                        );
                        System.out.println(dependent); // add this line
                        dependentData.add(dependent);
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
        System.out.println(infoTable.getItems()); // add this line
    }

    //The base code that I reference is meant to insert an ID then a new window pops one out. This will not work.
    public void RetrieveClaim() {
//        database db = new database();
//        try (Connection conn = db.connect()){
//            if (conn != null) {
//                if (retrieveClaimID.getText().isEmpty()){
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Retrieve Customer notification");
//                    alert.setHeaderText(null);
//                    alert.setContentText("The Customer corresponds to this id does not exist");
//                    alert.showAndWait();
//                }else {
//                    try {
//                        int claimID = Integer.parseInt(retrieveClaimID.getText());
//                        String sql = "SELECT * FROM \"Claim\" WHERE \"id\" = ?";
//                        PreparedStatement pstmt = conn.prepareStatement(sql);
//                        pstmt.setInt(1, claimID);
//                        ResultSet rs = pstmt.executeQuery();
//                        if (rs.next()) {
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setTitle("Retrieve Claim notification");
//                            alert.setHeaderText(null);
//                            alert.setContentText("Claim ID: " + rs.getInt("id") + "\n" +
//                                    "Claim Date: " + rs.getDate("claim_date") + "\n" +
//                                    "Claim Amount: " + rs.getDouble("claim_amount") + "\n" +
//                                    "Claim Status: " + rs.getString("claim_status") + "\n" +
//                                    "Customer ID: " + rs.getInt("customer_id"));
//                            alert.showAndWait();
//                        } else {
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setTitle("Retrieve Claim notification");
//                            alert.setHeaderText(null);
//                            alert.setContentText("The Claim corresponds to this id does not exist");
//                            alert.showAndWait();
//                        }
//
//                    }
//                    catch (NumberFormatException e) {
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setTitle("Retrieve Customer notification");
//                        alert.setHeaderText(null);
//                        alert.setContentText("The Customer corresponds to this id does not exist");
//                        alert.showAndWait();
//                    }
//                }
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RetrieveDependent();
        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
}
