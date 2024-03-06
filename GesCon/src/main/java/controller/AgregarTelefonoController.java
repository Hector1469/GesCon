package controller;

import dad.contactos.model.Contacto;
import dad.contactos.model.Telefono;
import dad.contactos.model.TipoTelefono;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class AgregarTelefonoController {

    @FXML
    private TextField telefonoTextField;

    @FXML
    private ComboBox<TipoTelefono> tipoComboBox;

    @FXML
    private Button addButton;
    
    @FXML
    private Button cancelButton;
    private Stage stage;
    private Contacto contacto;

    
    public void initData(Stage stage, Contacto contacto) {
        this.stage = stage;
        this.contacto = contacto;
        tipoComboBox.getItems().addAll(TipoTelefono.values());
        tipoComboBox.setPromptText("Selecciona un tipo");

        
        addButton.disableProperty().bind(
                telefonoTextField.textProperty().isEmpty()
                        .or(tipoComboBox.valueProperty().isNull())
        );
    }

    @FXML
    void agregarTelefono(ActionEvent event) {
        String numero = telefonoTextField.getText();
        TipoTelefono tipo = tipoComboBox.getValue();
        if (numero != null && !numero.isEmpty() && tipo != null && contacto != null) {
            
            Telefono nuevoTelefono = new Telefono(numero, tipo);
            
            contacto.getTelefonos().add(nuevoTelefono);
            Stage currentStage = (Stage) addButton.getScene().getWindow(); 
            currentStage.close();
        }
    }

    @FXML
    void cancelar(ActionEvent event) {
    	
        Stage currentStage = (Stage) cancelButton.getScene().getWindow(); 
        
        currentStage.close();
    }
}