package main_folder.Controller;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Dependent;
import main_folder.Model.PolicyHolder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
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
    private TextField claimDateDependField;
    @FXML
    private TextField examDateDependField;
    @FXML
    private TextField claimAmountDependField;
    @FXML
    private TextField bankingInfoDependField;
    @FXML
    private ComboBox<Dependent> dependentComboBox;

    //Update Injection
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private ComboBox<Dependent> updateDependComboBox;
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
                                cachedHolderName = rs.getString("id");
                                cachedHolderName.concat("-");
                                cachedHolderName.concat(rs.getString("name"));

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
                    int holderId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"PolicyHolder_Dependent\" WHERE \"PolicyHolder\" = ?";
                    String sql2 = "SELECT * FROM \"Dependent\" WHERE \"userID\" = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, holderId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int dependentId = rs.getInt("Dependent");
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, dependentId);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (rs2.next()) {
                            Dependent dependent = new Dependent(
                                    rs2.getString("id"),
                                    rs2.getString("name"),
                                    rs2.getString("email"),
                                    rs.getString("policyNumber")
                            );
                            dependentData.add(dependent);
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
                // Update the UI on the JavaFX Application thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dependentTable.setItems(dependentData);
                        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                        dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));

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
        //Get just the dependent's data
        dependentTable.setItems(dependentData);
        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));

        //Get their claims too. Because making another method with the same Query is too much work.
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

    /***
     * =========================================
     * This is for the File Claim Tab
     * =========================================*/
    //////////////////////////////////////////////
    public void CreateClaim() {
        database db = new database();
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
                    String claimDate = claimDateField.getText();
                    String examDate = examDateField.getText();
                    String claimAmount = claimAmountField.getText();
                    String bankingInfo = bankingInfoField.getText();
                    String insuredPerson = cachedPolicyNumber;
                    String status = "Pending";
                    String documents = cachedHolderName + "-" + selectFile().getName();
                    newFileName = documents;


                    // Prepare the SQL INSERT statement
                    String sql = "INSERT INTO \"Claim\" (\"Claim_Date\", \"Exam_Date\", \"Claim_amount\", \"Insured_Person\", \"Status\", \"Documents\", \"Receiver_Banking_Infor\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, claimDate);
                    pstmt.setString(2, examDate);
                    pstmt.setString(3, claimAmount);
                    pstmt.setString(4, insuredPerson);
                    pstmt.setString(5, status);
                    pstmt.setString(6, documents);
                    pstmt.setString(7, bankingInfo);

                    // Execute the SQL statement
                    pstmt.executeUpdate();

                    // Call uploadFileToSupabase() method
                    File selectedFile = selectFile();
                    uploadFileToSupabase(selectedFile);

                    System.out.println("Claim created successfully");
                    newFileName="";
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CreateDependentClaim() {
        database db = new database();
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
                    // Get the selected dependent from the ComboBox
                    Dependent selectedDependent = dependentComboBox.getSelectionModel().getSelectedItem();

                    // Get the values from the text fields
                    String claimDate = claimDateDependField.getText();
                    String examDate = examDateDependField.getText();
                    String claimAmount = claimAmountDependField.getText();
                    String bankingInfo = bankingInfoDependField.getText();
                    String insuredPerson = selectedDependent.getPolicyNumber(); // Use the policy number of the selected dependent
                    String status = "Pending";
                    String documents = selectedDependent.getId() + "-" + selectedDependent.getName() + "-" + selectFile().getName(); // Use the id and name of the selected dependent
                    newFileName = documents;

                    // Prepare the SQL INSERT statement
                    String sql = "INSERT INTO \"Claim\" (\"Claim_Date\", \"Exam_Date\", \"Claim_amount\", \"Insured_Person\", \"Status\", \"Documents\", \"Receiver_Banking_Infor\") VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, claimDate);
                    pstmt.setString(2, examDate);
                    pstmt.setString(3, claimAmount);
                    pstmt.setString(4, insuredPerson);
                    pstmt.setString(5, status);
                    pstmt.setString(6, documents);
                    pstmt.setString(7, bankingInfo);

                    // Execute the SQL statement
                    pstmt.executeUpdate();

                    // Call selectFile() and uploadFileToSupabase() methods
                    File selectedFile = selectFile();
                    uploadFileToSupabase(selectedFile);
                    newFileName= "";

                    System.out.println("Dependent claim created successfully");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
     * =========================================
     * This is for the File Select and Upload Tab
     * =========================================*/
    public File selectFile() {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        // Open the FileChooser and get the selected file
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            return selectedFile;
        } else {
            System.out.println("No file selected");
            return null;
        }
    }

    public void uploadFileToSupabase(File selectedFile) {
        if (selectedFile != null) {
            try {
                // Read the file into a byte array
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());

                // Create an HttpClient
                HttpClient client = HttpClient.newHttpClient();

                // Create an HttpRequest
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://mczllvsxcqnfxlewvpkr.supabase.co/storage/v1/object/private/Claim%20Documents%20Bucket" + newFileName))
                        .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1jemxsdnN4Y3FuZnhsZXd2cGtyIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTcxNDAzMzIwMiwiZXhwIjoyMDI5NjA5MjAyfQ.cNEDyg2qEStTXJSfNaF8_JWhW_9t7fAxSxPRA8mJR-A")
                        .header("Content-Type", "application/pdf")
                        .PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                        .build();

                // Send the request and handle the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Response status code: " + response.statusCode());
                System.out.println("Response body: " + response.body());
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("No file selected");
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
                if (dependentTable.getSelectionModel().getSelectedItem() == null) {
                    System.out.println("Please select a dependent");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Dependent notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select a dependent");
                    alert.showAndWait();
                }
                else {
                    // Get the selected dependent from the ComboBox
                    Dependent selectedDependent = updateDependComboBox.getSelectionModel().getSelectedItem();

                    // Get the values from the text fields
                    String newEmail = emailDependField.getText();
                    String newPassword = passwordDependField.getText();
                    int dependentId = Integer.parseInt(selectedDependent.getId());

                    // Prepare the SQL UPDATE statement
                    String sql = "UPDATE \"User\" SET \"email\" = ?, \"password\" = ? WHERE \"id\" = ?";

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
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateClaim() {
        database db = new database();
        try (Connection conn = db.connect()){
            if (conn != null) {
                if (idClaimToFind.getText().isEmpty() || claimDateUpdateField.getText().isEmpty() || amountUpdateField.getText().isEmpty() || bankUpdateField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                }
                else {
                    // Get the values from the text fields
                    String newExamDate = claimDateUpdateField.getText();
                    String newAmount = amountUpdateField.getText();
                    String newBankingInfo = bankUpdateField.getText();
                    int claimId = Integer.parseInt(idClaimToFind.getText());

                    // Select a file
                    File selectedFile = selectFile();
                    newFileName = "Claim" + claimId + "-" + selectedFile.getName() + "-Updated";

                    // Prepare the SQL UPDATE statement
                    String sql = "UPDATE \"Claim\" SET \"Exam_Date\" = ?, \"Claim_amount\" = ?, \"Receiver_Banking_Infor\" = ?, \"Documents\" = ? WHERE \"id\" = ?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newExamDate);
                    pstmt.setString(2, newAmount);
                    pstmt.setString(3, newBankingInfo);
                    pstmt.setString(4, newFileName);
                    pstmt.setInt(5, claimId);

                    // Execute the SQL statement
                    pstmt.executeUpdate();

                    // Upload the file to Supabase
                    uploadFileToSupabase(selectedFile);
                    newFileName="";

                    System.out.println("Claim updated successfully");
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
        loginController.setLoggedInUser(null);
        Stage stage = (Stage) logoutBTN.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/main_folder/login/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void populateComboBoxes() {
        // Set the cell factory for the dependentComboBox
        dependentComboBox.setCellFactory(new Callback<ListView<Dependent>, ListCell<Dependent>>() {
            @Override
            public ListCell<Dependent> call(ListView<Dependent> param) {
                return new ListCell<Dependent>() {
                    @Override
                    protected void updateItem(Dependent item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getName()); // Display the name of the dependent
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        // Set the cell factory for the updateDependentComboBox
        updateDependComboBox.setCellFactory(new Callback<ListView<Dependent>, ListCell<Dependent>>() {
            @Override
            public ListCell<Dependent> call(ListView<Dependent> param) {
                return new ListCell<Dependent>() {
                    @Override
                    protected void updateItem(Dependent item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getName()); // Display the name of the dependent
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
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
                populateComboBoxes();
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
