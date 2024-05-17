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
import main_folder.Model.Client;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class insuranceSurveyorController {
    @FXML
    private TextField claimDateField;

    @FXML
    private TextField examDateField;

    @FXML
    private TextField claimAmountField;

    @FXML
    private TextField insuredPersonField;

    @FXML
    private TextField statusField;

    @FXML
    private TextField documentsField;

    @FXML
    private TextField receiverBankingInfoField;

    @FXML
    private TableView<Claim> filteredClaimsTable1;

    @FXML
    private TableView<Client> filteredCustomerFindTable1;

    @FXML
    private Button logoutBTN;

    @FXML
    private TextField claimIdRequestInfo, claimIdPropose, retrieveFilterClaimIdFind1, retrieveFilterCustomerIdFind1, retrieveFilterClaimIdFind2, retrieveFilterCustomerIdFind2, claimIdApproval, surveyorId, managerRetrieveFilterCriteria;


    private main_folder.ConnectDatabase.database database = new database();


    @FXML
    private void RetrieveAndFilterClaims1() {
        Integer criteria = Integer.valueOf(retrieveFilterClaimIdFind1.getText());

        if (retrieveFilterClaimIdFind1.getText().isEmpty()) {
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
                        displayFilteredClaims1(filteredClaims);
                    } else {
                        showAlert("Info", "No claims found for the given criteria.");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void displayFilteredClaims1(List<Claim> claims) {
        filteredClaimsTable1.setItems(FXCollections.observableArrayList(claims));
    }

    @FXML
    private void ProposeClaim() {
        String claimDateStr = claimDateField.getText();
        String examDateStr = examDateField.getText();
        double claimAmount = Double.parseDouble(claimAmountField.getText());
        String insuredPerson = insuredPersonField.getText();
        String status = statusField.getText();
        String documents = documentsField.getText();
        String receiverBankingInfo = receiverBankingInfoField.getText();

        try (Connection conn = database.connect()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date claimDate = new Date(dateFormat.parse(claimDateStr).getTime());
            Date examDate = new Date(dateFormat.parse(examDateStr).getTime());

            String sql = "INSERT INTO \"Claim\" (\"Claim_Date\", \"Exam_Date\", \"Claim_amount\", \"Insured_Person\", \"Status\", \"Documents\", \"Receiver_Banking_Infor\") VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, claimDate);
                pstmt.setDate(2, examDate);
                pstmt.setDouble(3, claimAmount);
                pstmt.setInt(4, Integer.parseInt(insuredPerson));
                pstmt.setString(5, status);
                pstmt.setString(6, documents);
                pstmt.setString(7, receiverBankingInfo);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Success", "Claim proposed successfully.");
                } else {
                    showAlert("Error", "Failed to propose claim.");
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
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
    private Label additionalInfoLabel;
    @FXML
    private void RequestAdditionalInfo() {
        String claimId = claimIdRequestInfo.getText();

        try (Connection conn = database.connect()) {
            String sql = "SELECT * FROM \"Claim\" WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(claimId));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        Date claimDate = rs.getDate("Claim_Date");
                        Date examDate = rs.getDate("Exam_Date");
                        double claimAmount = rs.getDouble("Claim_amount");
                        String insuredPerson = rs.getString("Insured_Person");
                        String status = rs.getString("Status");
                        String documents = rs.getString("Documents");
                        String receiverBankingInfo = rs.getString("Receiver_Banking_Infor");

                        String infoText = "ID: " + id + ", " +
                                "Claim Date: " + claimDate + ", " +
                                "Exam Date: " + examDate + ", " +
                                "Claim Amount: " + claimAmount + ", " +
                                "Insured Person: " + insuredPerson + ", " +
                                "Status: " + status + ", " +
                                "Documents: " + documents + ", " +
                                "Receiver Banking Info: " + receiverBankingInfo;
                        System.out.println(infoText);
                        additionalInfoLabel.setText(infoText);
                    } else {
                        showAlert("Error", "No claim found with the provided ID.");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void RetrieveAndFilterCustomers1() {
        Integer criteria = Integer.valueOf(retrieveFilterCustomerIdFind1.getText());

        if (retrieveFilterCustomerIdFind1.getText().isEmpty()) {
            showAlert("Error", "Criteria must be provided.");
            return;
        }

        try (Connection conn = database.connect()) {
            String sql = "SELECT * FROM \"Customer\" WHERE  \"id\"  = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, criteria);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Client> clients = new ArrayList<>();
                    while (rs.next()) {
                        Client client = new Client();
                        client.setId(rs.getInt(1));
                        client.setInsuranceNumber(rs.getInt(2));
                        clients.add(client);
                    }
                    if (!clients.isEmpty()) {
                        displayFilteredCustomers1(clients);
                    } else {
                        showAlert("Info", "No claims found for the given criteria.");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void displayFilteredCustomers1(List<Client> clients) {
        filteredCustomerFindTable1.setItems(FXCollections.observableArrayList(clients));
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
