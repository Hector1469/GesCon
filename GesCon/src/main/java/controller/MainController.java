package controller;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import dad.contactos.model.Agenda;
import dad.contactos.model.Contacto;
import dad.contactos.model.Telefono;
import dad.contactos.model.TipoTelefono;
import dad.contactos.model.adapter.ImageAdapter;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener; 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class MainController {
	Agenda agenda = new Agenda();
	Contacto contacto = new Contacto();
	File defaultFile = new File("src/main/resources/images/no-photo-128x128.png");
	Image defaultImage = new Image(defaultFile.toURI().toString());
	Image icon = new Image(getClass().getResourceAsStream("/images/contacts-icon-32x32.png"));
	
	@FXML
    private MenuItem abrirMenuButton;
	@FXML
    private ImageView contactoImagen;

    @FXML
    private Button addContactoButton;

    @FXML
    private Button addTelefonoButton;

    @FXML
    private TextField apellidosTexto;

    @FXML
    private Button buscarButton;

    @FXML
    private Button eliminarContactoButton;

    @FXML
    private Button eliminarFotoButton;

    @FXML
    private Button eliminarTelefonoButton;

    @FXML
    private DatePicker fechaDatePicker;

    @FXML
    private MenuItem guardarMenuButton;

    @FXML
    private ListView<Contacto> listView;

    @FXML
    private TextField nombreTexto;

    @FXML
    private MenuItem nuevoMenuButton;

    @FXML
    private TableColumn<Telefono, String> numeroColumn;

    @FXML
    private MenuItem salirMenuButton;

    @FXML
    private Button siguienteButton;

    @FXML
    private TableView<Telefono> tableView;

    @FXML
    private TableColumn<Telefono, TipoTelefono> tipoColumn;

    @FXML
    private Button volverButton;
    @FXML
    private BorderPane view;
    
    
    @FXML
    private VBox detallesContactosVbox;
    
  //Constructor
    public MainController(){
        
    }

   
    
    @FXML
    private void initialize() {
        try {
            
            File defaultFile = new File("src/main/resources/images/no-photo-128x128.png");
            Image defaultImage = new Image(defaultFile.toURI().toURL().toExternalForm());

            
            contactoImagen.setImage(defaultImage);

            File file = new File("contactos.agenda");
            if (file.exists()) {
                agenda = Agenda.load(file);
                agenda.contactosProperty().set(agenda.getContactos());
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                
                nombreTexto.textProperty().unbindBidirectional(oldValue.nombreProperty());
                apellidosTexto.textProperty().unbindBidirectional(oldValue.apellidosProperty());
                fechaDatePicker.valueProperty().unbindBidirectional(oldValue.fechaNacimientoProperty());
            }

            if (newValue != null) {
               
                nombreTexto.textProperty().bindBidirectional(newValue.nombreProperty());
                apellidosTexto.textProperty().bindBidirectional(newValue.apellidosProperty());
                fechaDatePicker.valueProperty().bindBidirectional(newValue.fechaNacimientoProperty());

                
                if (newValue.getFoto() == null) {
                    contactoImagen.setImage(defaultImage);
                } else {
                    contactoImagen.setImage(newValue.getFoto());
                }

                
                tableView.setItems(FXCollections.observableArrayList(newValue.getTelefonos()));
            } else {
                
                nombreTexto.clear();
                apellidosTexto.clear();
                fechaDatePicker.setValue(null);
                contactoImagen.setImage(defaultImage); 
                tableView.setItems(null);
            }
        });

        
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.telefonosProperty().removeListener(telefonosListener);
            }
            if (newValue != null) {
                newValue.telefonosProperty().addListener(telefonosListener);
                actualizarTablaTelefonos(newValue.getTelefonos());
            }
        });

        
        listView.itemsProperty().bindBidirectional(agenda.contactosProperty());

        numeroColumn.setCellValueFactory(v -> v.getValue().numeroProperty());
        tipoColumn.setCellValueFactory(v -> v.getValue().tipoProperty());

        detallesContactosVbox.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
        eliminarContactoButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());

        
        ObjectBinding<Contacto> ultimoContacto = Bindings.valueAt(
                agenda.contactosProperty(),
                Bindings.size(agenda.contactosProperty()).subtract(1)
        );

        siguienteButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        listView.getSelectionModel().getSelectedItem() == ultimoContacto.get(),
                listView.getSelectionModel().selectedItemProperty(),
                ultimoContacto
        ));

        
        ObjectBinding<Contacto> primerContacto = Bindings.valueAt(agenda.contactosProperty(), 0);

        
        volverButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        listView.getSelectionModel().getSelectedItem() == primerContacto.get(),
                listView.getSelectionModel().selectedItemProperty(),
                primerContacto
        ));

        eliminarTelefonoButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
    }

    
    ChangeListener<ObservableList<Telefono>> telefonosListener = (observable, oldValue, newValue) -> {
        if (newValue != null) {
            actualizarTablaTelefonos(newValue);
        }
    };

    
    private void actualizarTablaTelefonos(ObservableList<Telefono> telefonos) {
        tableView.setItems(telefonos);
    }

        
       

    @FXML
    void actionAbrirMenuButton(ActionEvent event) {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Agenda", "*.agenda"),
                new ExtensionFilter("Todos los archivos", "*.*")
        );
        
        
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        
        
        File archivo = fileChooser.showOpenDialog(null);

        
        if (archivo != null) {
        	abrirAgenda(archivo);
        }
    }


    @FXML
    void actionAddContactoButton(ActionEvent event) {
    	
        Contacto nuevoContacto = new Contacto("Sin nombre", "sin apellido", null);
        
       
        ObservableList<Contacto> contactos = agenda.contactosProperty().get();
        
        
        contactos.add(nuevoContacto);
        
       
        cargarContactosEnListView();
        
    
        listView.getSelectionModel().select(nuevoContacto);
        
        
        nombreTexto.requestFocus();
    }

    @FXML
    void actionAddTelefonoButton(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AgregarTelefono.fxml"));
            Parent root = loader.load();

            
            AgregarTelefonoController controller = loader.getController();

            
            Contacto contactoSeleccionado = listView.getSelectionModel().getSelectedItem();

            
            controller.initData(new Stage(), contactoSeleccionado); 

            
            Scene scene = new Scene(root);

            
            Stage stage = new Stage();
            stage.setScene(scene);
            
            
            Image icon = new Image(getClass().getResourceAsStream("/images/contacts-icon-32x32.png"));
            stage.getIcons().add(icon);
            
            
            stage.setResizable(false);

           
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }


    @FXML
    void actionBuscarButton(ActionEvent event) {
        // Configurar el FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Todas las imágenes", "*.jpg", "*.png", "*.bmp"),
                new ExtensionFilter("Todos los archivos", "*.*")
        );
        
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        
        File archivo = fileChooser.showOpenDialog(null);

        
        if (archivo != null) {
            try {
                
                Contacto selectedContacto = listView.getSelectionModel().getSelectedItem();

                
                Image foto = new Image(archivo.toURI().toURL().toExternalForm());

                
                selectedContacto.setFoto(foto);
                
                
                contactoImagen.setImage(foto);
            } catch (MalformedURLException e) {
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al poner la foto al contacto");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                
                Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                dialogStage.getIcons().add(icon);
            }
        }
    }

    @FXML
    void actionEliminarFotoButton(ActionEvent event) {
        Contacto selectedContacto = listView.getSelectionModel().getSelectedItem();
        if (selectedContacto != null) {
            
            selectedContacto.setFoto(null);
            
            listView.refresh();
        }
    }


    @FXML
    void actionEliminarContactoButton(ActionEvent event) {
    	Contacto selectedContacto = listView.getSelectionModel().getSelectedItem();
    	if (selectedContacto != null) {
	    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	        alert.setTitle("Contactos");
	        alert.setHeaderText("Se va a eliminar al contacto " + selectedContacto.getNombre());
	        alert.setContentText("¿Estás Seguro?");
	        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
	        dialogStage.getIcons().add(icon);
	        Optional<ButtonType> res = alert.showAndWait();
	        if(res.get() == ButtonType.OK) {
	        	agenda.contactosProperty().remove(selectedContacto);
	        	
	            
	        } 
	        
	        cargarContactosEnListView();
        
    	}
    }

    @FXML
    void actionEliminarTelefonoButton(ActionEvent event) {
    	
        Telefono telefonoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        
        if (telefonoSeleccionado != null) {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Contactos");
            alert.setHeaderText("Se va a eliminar el numero: "+telefonoSeleccionado.getNumero());
            alert.setContentText("¿Estas seguro?");
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);

            
            Optional<ButtonType> result = alert.showAndWait();

            
            if (result.isPresent() && result.get() == ButtonType.OK) {
               
                Contacto contactoSeleccionado = listView.getSelectionModel().getSelectedItem();
                contactoSeleccionado.getTelefonos().remove(telefonoSeleccionado);
            }
        }
    }

    @FXML
    void actionGuardarMenuButton(ActionEvent event) {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Agenda");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Agenda", "*.agenda"),
                new ExtensionFilter("Todos los archivos", "*.*")
        );
        
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("agenda");

        
        File archivo = fileChooser.showSaveDialog(null);

        
        if (archivo != null) {
            
            if (!archivo.getName().toLowerCase().endsWith(".agenda")) {
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Archivo no compatible");
                alert.setContentText("Solo se pueden guardar archivos con extensión '.agenda'.");
                Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                dialogStage.getIcons().add(icon);
                alert.showAndWait();
            } else {
                guardarAgenda(archivo);
            }
        }
    }

    @FXML
    void actionNuevoMenuButton(ActionEvent event) throws Exception {
    	
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nueva agenda");
        alert.setHeaderText("Se dispone a crear una nueva agenda.\nSi tiene informacion sin guardar se perdera para siempre");
        alert.setContentText("¿Seguro que desea continuar?");
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(icon);

        
        ButtonType yesButton = new ButtonType("Sí", ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

       
        Optional<ButtonType> result = alert.showAndWait();
        
       
        if (result.isPresent() && result.get() == yesButton) {
            
            this.agenda = new Agenda();
            
            listView.getItems().clear();
        }
    	
    }

    @FXML
    void actionSalirMenuButton(ActionEvent event) {
    	
        Stage primaryStage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        
        primaryStage.close();
        
        Platform.exit();
        
    }

    @FXML
    void actionSiguienteButton(ActionEvent event) {
        
        int currentIndex = listView.getSelectionModel().getSelectedIndex();
        
        
        int totalContacts = agenda.getContactos().size();
        
        
        if (currentIndex < totalContacts - 1) {
            
            listView.getSelectionModel().select(currentIndex + 1);
        }
    }

    @FXML
    void actionVolverButton(ActionEvent event) {
        
        int currentIndex = listView.getSelectionModel().getSelectedIndex();
        
        
        if (currentIndex > 0) {
            
            listView.getSelectionModel().select(currentIndex - 1);
        }
    }
    public BorderPane getView() {
        return view;
    }
    
    

    
    private void abrirAgenda(File archivo) {
        try {
            
            this.agenda = Agenda.load(archivo);
            
            
            cargarContactosEnListView();
        } catch (Exception e) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Abrir agenda");
            alert.setHeaderText("No se pudo cargar la agenda.");
            alert.setContentText(null);
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);
            alert.showAndWait();
        }
    }

    
    private void guardarAgenda(File archivo) {
        try {
            
            agenda.save(archivo);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("Agenda guardada correctamente.");
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);
            alert.showAndWait();
        } catch (Exception e) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al guardar la agenda: " + e.getMessage());
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);
            alert.showAndWait();
        }
    }

    
 // Método para cargar los contactos en la ListView
    private void cargarContactosEnListView() {
        ObservableList<Contacto> contactos = FXCollections.observableArrayList(agenda.getContactos());
        listView.setItems(contactos);
    }
}