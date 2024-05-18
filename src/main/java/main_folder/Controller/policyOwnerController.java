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
import javafx.stage.Stage;
import javafx.util.Callback;
import main_folder.ConnectDatabase.database;
import main_folder.Model.Claim;
import main_folder.Model.Dependent;
import main_folder.Model.PolicyHolder;
import main_folder.Model.PolicyOwner;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class policyOwnerController implements Initializable {
    private String cachedPolicyNumber;
    private String cachedHolderName;
    private String newFileName;
    //Buttons
    @FXML
    private Button logoutBTN;

    //Other Stuff
    private ObservableList<PolicyOwner> OwnerData = FXCollections.observableArrayList();
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
    public void RetrieveOwner() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int dependentId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                    String sql2 = "SELECT * FROM \"PolicyOwner\" WHERE \"userID\" = ?";
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
                        } else {
                            int customerId = rs2.getInt("policyNumber");
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, customerId);
                            ResultSet rs3 = stmt3.executeQuery();
                            if (rs3.next()) {
                                PolicyOwner owner = new PolicyOwner(
                                        rs.getString("id"),
                                        rs.getString("name"),
                                        rs.getString("email"),
                                        rs3.getString("InsuranceNumber")
                                );
                                OwnerData.add(owner);
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
                        for (PolicyOwner owner : OwnerData) {
                            holderInfo.setText(owner.toString());
                        }
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (PolicyOwner owner : OwnerData) {
            holderInfo.setText(owner.toString());
        }
    }

    public void RetrieveHolder() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                System.out.println("Database connection successful."); // Debug line
                try {
                    int userId = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"PolicyOwner\" WHERE \"userID\" = ?";
                    String sql2 = "SELECT * FROM \"PolicyOwner_PolicyHolder\" WHERE \"PolicyOwner\" = ?";
                    String sql3 = "SELECT * FROM \"PolicyOwner\" WHERE \"id\" = ?";
                    String sql4 = "SELECT * FROM \"User\" WHERE \"id\" = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {

                    } else {
                        int holderId = rs.getInt("id");
                        PreparedStatement stmt2 = conn.prepareStatement(sql2);
                        stmt2.setInt(1, holderId);
                        ResultSet rs2 = stmt2.executeQuery();
                        while (rs2.next()) {
                            int dependentId = rs2.getInt("Dependent");
                            PreparedStatement stmt3 = conn.prepareStatement(sql3);
                            stmt3.setInt(1, dependentId);
                            ResultSet rs3 = stmt3.executeQuery();
                            while (rs3.next()) {
                                PreparedStatement stmt4 = conn.prepareStatement(sql4);
                                stmt4.setInt(1, dependentId);
                                ResultSet rs4 = stmt4.executeQuery();
                                if (rs4.next()) {
                                    Dependent dependent = new Dependent(
                                            rs3.getString("id"),
                                            rs4.getString("name"),
                                            rs4.getString("email"),
                                            rs3.getString("policyNumber")
                                    );
                                    dependentData.add(dependent);
                                }
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
                        dependentTable.setItems(dependentData);
                        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                        dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Get just the dependent's data
        dependentTable.setItems(dependentData);
        dependentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dependentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dependentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dependentPolicyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
    }

    public void RetrieveClaim() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                System.out.println("Connected to the database");
                try {
                    int policy_holder_id = Integer.parseInt(loginController.getLoggedInUser());
                    String sql = "SELECT * FROM \"PolicyOwner\" WHERE \"userID\" = ?";
                    String sql2 = "SELECT * FROM \"Claim\" WHERE \"Insured_Person\" = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, policy_holder_id);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        //Handle Error
                        System.out.println("No PolicyOwner found with the given userID");
                    } else {
                        int customerId = rs.getInt("policyNumber");
                        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                        pstmt2.setInt(1, customerId);
                        ResultSet rs2 = pstmt2.executeQuery();
                        if (!rs2.next()) {
                            //Handle Error
                            System.out.println("No Claim found with the given Insured_Person");
                        } else {
                            //Display Claim
                            while (rs2.next()) {
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
                } catch (NumberFormatException e) {
                    //Handle Error
                    System.out.println("Error parsing userID to integer");
                }
            }
        } catch (SQLException e) {
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
     * =========================================
     * @return*/
    //////////////////////////////////////////////
    public int CreateClaim() {
        database db = new database();
        int generatedClaimId = -1;
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (claimDateField.getText().isEmpty() || examDateField.getText().isEmpty() || claimAmountField.getText().isEmpty() || bankingInfoField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Customer notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                } else {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedClaimId;
    }

    public void createRecord() {
        database db = new database();
        try (Connection conn = db.connect()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CreateDependentClaim() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (claimDateDependField.getText().isEmpty() || examDateDependField.getText().isEmpty() || claimAmountDependField.getText().isEmpty() || bankingInfoDependField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Dependent Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                } else {
                    // Get the selected dependent from the ComboBox
                    Dependent selectedDependent = dependentComboBox.getSelectionModel().getSelectedItem();

                    // Get the values from the text fields
                    Date claimDate = Date.valueOf(claimDateDependField.getText());
                    Date examDate = Date.valueOf(examDateDependField.getText());
                    Float claimAmount = Float.valueOf(claimAmountDependField.getText());
                    String bankingInfo = bankingInfoDependField.getText();
                    Integer insuredPerson = Integer.valueOf(selectedDependent.getPolicyNumber()); // Use the policy number of the selected dependent
                    String status = "Processing";
                    String documents = "Documents.pdf";

                    // Prepare the SQL INSERT statement
                    String sql = "INSERT INTO \"Claim\" (\"Claim_Date\", \"Exam_Date\", \"Claim_amount\", \"Insured_Person\", \"Status\", \"Documents\", \"Receiver_Banking_Infor\") VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
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
                }
            }
        } catch (SQLException e) {
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
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (holderInfo.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Holder notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                } else {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateDependent() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (dependentTable.getSelectionModel().getSelectedItem() == null) {
                    System.out.println("Please select a dependent");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Dependent notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select a dependent");
                    alert.showAndWait();
                } else {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateClaim() {
        database db = new database();
        try (Connection conn = db.connect()) {
            if (conn != null) {
                if (idClaimToFind.getText().isEmpty() && claimDateUpdateField.getText().isEmpty() && amountUpdateField.getText().isEmpty() && bankUpdateField.getText().isEmpty()) {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Claim notification");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter all fields");
                    alert.showAndWait();
                } else {
                    // Get the values from the text fields
                    Date newExamDate = Date.valueOf(claimDateUpdateField.getText());
                    Float newAmount = Float.valueOf(amountUpdateField.getText());
                    String newBankingInfo = bankUpdateField.getText();
                    int claimId = Integer.parseInt(idClaimToFind.getText());


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
                }
            }
        } catch (SQLException e) {
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
                RetrieveOwner();
                RetrieveClaim();
            }
        };

        //Create Runnable for Dependent methods
        Runnable retrieveDependentRunnable = new Runnable() {
            @Override
            public void run() {
                RetrieveHolder();
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
