package dad.contactos.ui;

import controller.MainController;
import dad.contactos.model.Agenda;
import dad.contactos.model.Contacto;
import dad.contactos.model.Telefono;
import dad.contactos.model.TipoTelefono;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class App extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Image icon = new Image(getClass().getResourceAsStream("/images/contacts-icon-32x32.png"));

        
        primaryStage.getIcons().add(icon);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        MainController controller = new MainController();
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Contactos");
        primaryStage.show();

        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
