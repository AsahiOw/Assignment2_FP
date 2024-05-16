package main_folder;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class main extends Application {

    private static Stage stg;
    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login/login.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin/admin.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dependent/dependent.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Ooptional");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}