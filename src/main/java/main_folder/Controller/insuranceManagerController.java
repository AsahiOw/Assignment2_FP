package main_folder.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Customer;
import main_folder.Model.Surveyor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class insuranceManagerController {

    @FXML
    private TextField claimIdRequestInfo, claimIdPropose, retrieveFilterClaimIdFind1, retrieveFilterCustomerIdFind1, retrieveFilterClaimIdFind2, retrieveFilterCustomerIdFind2, claimIdApproval, surveyorId, managerRetrieveFilterCriteria;
    @FXML
    private ComboBox<String> approvalDecision;

    @FXML
    private TableView<Claim> filteredClaimsTable2;

    @FXML
    private TableView<Customer> filteredCustomerFindTable2;

    @FXML
    private Label surveyorInfoLabel;

    private main_folder.ConnectDatabase.database database = new database();

    @FXML
    private Button logoutBTN;


    @FXML
    private void ApproveRejectClaim() {
        String claimId = claimIdApproval.getText();
        String decision = approvalDecision.getValue();

        if (claimId.isEmpty() || decision == null) {
            showAlert("Error", "Please enter both Claim ID and decision.");
            return;
        }

        try (Connection conn = database.connect()) {
            String sql = "UPDATE \"Claim\" SET \"Status\" = ? WHERE \"id\" = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, decision);
                pstmt.setInt(2, Integer.parseInt(claimId));
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Success", "Claim status updated successfully.");
                } else {
                    showAlert("Error", "Failed to update claim status.");
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void RetrieveAndFilterClaims2() {
        Integer criteria = Integer.valueOf(retrieveFilterClaimIdFind2.getText());

        if (retrieveFilterClaimIdFind2.getText().isEmpty()) {
            showAlert("Error", "Criteria must be provided.");
            return;
        }

        try (Connection conn = database.connect()) {
            String sql = "SELECT * FROM \"Claim\" WHERE  \"id\"  = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, criteria);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Claim> filteredClaims = new ArrayList<>();
                    while (rs.next()) {
                        Claim claim = new Claim();
                        claim.setId(rs.getInt(1));
                        claim.setClaim_Date(rs.getDate(2));
                        claim.setExam_Date(rs.getDate(3));
                        claim.setClaim_amount(rs.getDouble(4));
                        claim.setInsured_Person(rs.getString(5));
                        claim.setStatus(rs.getString(6));
                        claim.setDocuments(rs.getString(7));
                        claim.setReceiver_Banking_Infor(rs.getString(8));
                        filteredClaims.add(claim);
                    }
                    if (!filteredClaims.isEmpty()) {
                        displayFilteredClaims2(filteredClaims);
                    } else {
                        showAlert("Info", "No claims found for the given criteria.");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void displayFilteredClaims2(List<Claim> claims) {
        filteredClaimsTable2.setItems(FXCollections.observableArrayList(claims));
    }


    @FXML
    private void RetrieveAndFilterCustomers2() {
        Integer criteria = Integer.valueOf(retrieveFilterCustomerIdFind2.getText());

        if (retrieveFilterCustomerIdFind2.getText().isEmpty()) {
            showAlert("Error", "Criteria must be provided.");
            return;
        }

        try (Connection conn = database.connect()) {
            String sql = "SELECT * FROM \"Customer\" WHERE  \"id\"  = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, criteria);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Customer> customers = new ArrayList<>();
                    while (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getInt(1));
                        customer.setInsuranceNumber(rs.getInt(2));
                        customers.add(customer);
                    }
                    if (!customers.isEmpty()) {
                        displayFilteredCustomers2(customers);
                    } else {
                        showAlert("Info", "No claims found for the given criteria.");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void displayFilteredCustomers2(List<Customer> customers) {
        filteredCustomerFindTable2.setItems(FXCollections.observableArrayList(customers));
    }

    @FXML
    private void ViewSurveyorInfo() {
        String surveyorIdText = surveyorId.getText();

        if (surveyorIdText.isEmpty()) {
            showAlert("Error", "Surveyor ID must be provided.");
            return;
        }

        Surveyor surveyor = null;

        try (Connection conn = database.connect()) {
            String sql = "SELECT * FROM  \"InsuranceSurveyor\" INNER JOIN \"User\"  ON \"InsuranceSurveyor\".\"userID\" = \"User\".\"id\" WHERE \"InsuranceSurveyor\".\"userID\" = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(surveyorIdText));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        surveyor = new Surveyor();
                        surveyor.setId(rs.getInt(1));
                        surveyor.setUserId(rs.getInt(2));
                        surveyor.setName(rs.getString(3));
                        surveyor.setEmail(rs.getString(4));
                        surveyor.setUserType(rs.getString(6));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }

        if (surveyor != null) {
            displaySurveyorInfo(surveyor);
        } else {
            showAlert("Info", "No surveyor found with the given ID.");
        }
    }

    @FXML
    private void displaySurveyorInfo(Surveyor surveyor) {
        surveyorInfoLabel.setText(surveyor.toString());
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


}
