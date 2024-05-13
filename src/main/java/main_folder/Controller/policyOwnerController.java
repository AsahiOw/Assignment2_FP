package main_folder.Controller;

import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main_folder.ConnectDatabase.database;

import java.sql.*;

public class policyOwnerController {
    @FXML
    private TextField retrieveClaimID;

    public void RetrieveClaim() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (retrieveClaimID.getText().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Retrieve Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("The Customer corresponds to this id does not exist");
                    alert.showAndWait();
                }else {
                    try {
                        int claimID = Integer.parseInt(retrieveClaimID.getText());
                        String sql = "SELECT * FROM \"Claim\" WHERE \"id\" = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, claimID);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Claim notification");
                            alert.setHeaderText(null);
                            alert.setContentText("Claim ID: " + rs.getInt("id") + "\n" +
                                    "Claim Date: " + rs.getDate("claim_date") + "\n" +
                                    "Claim Amount: " + rs.getDouble("claim_amount") + "\n" +
                                    "Claim Status: " + rs.getString("claim_status") + "\n" +
                                    "Customer ID: " + rs.getInt("customer_id"));
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Retrieve Claim notification");
                            alert.setHeaderText(null);
                            alert.setContentText("The Claim corresponds to this id does not exist");
                            alert.showAndWait();
                        }

                    }
                    catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Retrieve Customer notification");
                        alert.setHeaderText(null);
                        alert.setContentText("The Customer corresponds to this id does not exist");
                        alert.showAndWait();
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
