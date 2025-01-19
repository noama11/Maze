package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // The start method is the main entry point for all JavaFX applications.

//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
//        Parent root = fxmlLoader.load();
//        MyViewController viewController = fxmlLoader.getController();
//        viewController.initialize();

        // Initializes the model and starts the servers necessary for the application's backend processing.
        IModel model = new MyModel();
        model.startServers();
        // Sets up the ViewModel with the Model as its data source.
        MyViewModel viewModel = new MyViewModel(model);

        // Loads the main view from an FXML file and sets up the scene.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Bunny World"); // Sets the title of the primary stage (window).
        Scene scene = new Scene(root, 850, 600); // Sets the size of the scene.
        primaryStage.setScene(scene);

        // Gets the controller for the view from the FXMLLoader and initializes it with the ViewModel.
        MyViewController viewController = fxmlLoader.getController();
        viewController.setResizeEvent(scene);
        viewController.setViewModel(viewModel);
        viewModel.addObserver(viewController);
        viewController.initialize();

        // Displays the primary stage and sets up the event handler for closing the stage.
        primaryStage.setScene(scene);
        SetStageCloseEvent(primaryStage, model);
        primaryStage.show();
    }
    // Configures the event that is triggered when the user attempts to close the application window.
    public void SetStageCloseEvent(Stage primaryStage, IModel model) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    model.stopServers();
                    primaryStage.close();
                    Platform.exit();
                    System.exit(0);
                } else {
                    windowEvent.consume();
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
