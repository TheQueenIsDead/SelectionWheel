package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Build the scene
        Pane primaryPane = new Pane();
        SelectionWheel wheel = new SelectionWheel(primaryPane, 500, 500, 6);
        Scene primaryScene = new Scene(primaryPane, 500, 500);


        primaryStage.setTitle("Test Selections");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
