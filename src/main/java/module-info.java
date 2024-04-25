module org.example.assignment2_fp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.assignment2_fp to javafx.fxml;
    exports org.example.assignment2_fp;
}