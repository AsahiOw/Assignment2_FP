module org.example.assignment2_fp {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;
    requires java.sql;


    opens main_folder to javafx.fxml;
    exports main_folder;
    exports main_folder.Controller;
    opens main_folder.Controller to javafx.fxml;
}