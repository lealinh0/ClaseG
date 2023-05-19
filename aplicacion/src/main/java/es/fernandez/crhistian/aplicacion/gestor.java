package es.fernandez.crhistian.aplicacion;

import static es.fernandez.crhistian.aplicacion.App.bd;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;

public class gestor {

    @FXML
    private Button Bborrardatos;

    @FXML
    private Button actualizar;

    @FXML
    private Label apellidos;

    @FXML
    private TextField apellidosT;

    @FXML
    private Label contraseña;

    @FXML
    private PasswordField contraseñaT;

    @FXML
    private Label dni;

    @FXML
    private TextField dniT;

    @FXML
    private Button eliminar;

    @FXML
    private Label email;

    @FXML
    private TextField emailT;

    @FXML
    private Label error;

    @FXML
    private ImageView imagen;

    @FXML
    private Pane inferior;
    
    @FXML
    private ChoiceBox<String> comoIdioma;

    @FXML
    private Button insertar;

    @FXML
    private Label nombre;

    @FXML
    private TextField nombreT;

    @FXML
    private Label notas;

    @FXML
    private TextArea notasT;

    @FXML
    private Pane panel;

    @FXML
    private Pane paneld;

    @FXML
    private Pane paneli;

    @FXML
    private Pane superior;
    
    @FXML
    private void initialize(){
        DesBotonesBD();
        dniT.focusedProperty().addListener(new DNIChangerListener());
        
        comoIdioma.getItems().addAll("Castellano", "Inglés");
        comoIdioma.getSelectionModel().select(App.idioma);
        comoIdioma.valueProperty().addListener((ov, p1, p2) -> {
        App.idioma = p2;
        App.textos = ResourceBundle.getBundle("es/fernandez/crhistian/aplicacion/textos_"
              + (p2.equals("Castellano") ? "es_ES" : "en_UK"));
        try {
            App.setRoot("primary");
        } catch (IOException ex) {
            Logger.getLogger(gestor.class.getName()).log(Level.SEVERE, null, ex);
        }
        });
    };
    


    @FXML
    void borrarDatos(ActionEvent event) {
        vaciarCampos();
        DesBotonesBD();
        
    }
    
    private void vaciarCampos() {
        dniT.setText("");
        nombreT.setText("");
        apellidosT.setText("");
        emailT.setText("");
        contraseñaT.setText("");
        notasT.clear();
    }
    
    static final Logger LOG = Logger.getLogger(SqlMaven.class.getSimpleName());


    static void redirigeSalidaError(String nombreFichero) {
        try {

        PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(nombreFichero, true)), true);
        System.setErr(ps);

        } catch (FileNotFoundException ex) {
            System.err.println("No existe el fichero");
            }
        }
    
    
        @FXML
    void actualizarDatos(ActionEvent event) {
        if(datosValidos()){
            try {
            redirigeSalidaError("LogSQL");
            Statement stm;
            String sql = "Update usuarios "
              + "set nombre="+"'"+nombreT.getText()+"', "
              + "apellido="+"'"+apellidosT.getText()+"', "
              + "email="+"'"+emailT.getText()+"', "
              + "password="+"'"+contraseñaT.getText()+"', "
              + "notas="+"'"+notasT.getText()+"' "
              +"where dni= "+"'"+dniT.getText()+"';";
            boolean exito = bd.sqlUpdate(sql).isBlank();
            System.out.println("|"+sql+"|"
            );
            error.setText(
                (exito==true) ? App.textos.getString("realizado_correctamente ") + dniT.getText(): App.textos.getString("Error_en_la_actualizacion_del_registro_ ") + dniT.getText()) ;
            vaciarCampos();
            actualizar.setDisable(false);
            stm = bd.getConexion().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);
            } catch (Exception e) {
                System.out.println(""+e);
                LOG.log(Level.SEVERE, "Error al actualizar datos datos", e);
            }
        }    
    }
    
    
    

    @FXML
    void eliminarDatos(ActionEvent event) {
        if(datosValidos()){
            try {
            redirigeSalidaError("LogSQL");
            Statement stm;
            String sql = "Delete from usuarios "
              +"where dni= "+"'"+dniT.getText()+"'";
            boolean exito = bd.sqlUpdate(sql).isBlank();
            System.out.println("|"+sql+"|"
            );
            error.setText(
                (exito==true) ? dniT.getText()+App.textos.getString(" _realizado_correctamente") : App.textos.getString("Error_en_la_eliminacion_del_registro") + dniT.getText()) ;
            vaciarCampos();
            eliminar.setDisable(false);
            stm = bd.getConexion().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);
            } catch (Exception e) {
                System.out.println(""+e);
                System.out.println("Error al insertar datos");
                LOG.log(Level.SEVERE, "Error al eliminar datos", e);
            } 
        }
        
    }

    @FXML
    void insertarDatos(ActionEvent event) {
        
        if(datosValidos()){
            try {
                redirigeSalidaError("LogSQL");
                Statement stm;

                String sql = "INSERT INTO usuarios (dni, nombre, apellido, email, password, notas)"
                  + "VALUES ("+"'"+dniT.getText()+"'"+","+"'"+nombreT.getText()+"'"+","+"'"+apellidosT.getText()+"'"+","+"'"+emailT.getText()+"'"+","+"'"+contraseñaT.getText()+"'"+","+"'"+notasT.getText()+"')";
                boolean exito = bd.sqlUpdate(sql).isBlank();
                System.out.println("|"+sql+"|"
                );
                error.setText(
                    (exito==true) ? App.textos.getString("_insertado_correctamente") : App.textos.getString("Error_en_del_registro_ ") + dniT.getText()) ;
                vaciarCampos();
                insertar.setDisable(false);
                stm = bd.getConexion().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

            } catch (Exception e) {
//                System.out.println(""+e);
//                System.out.println("Error al insertar datos");
//                LOG.log(Level.SEVERE, "Error al insertar datos", e);

            }
        }
        
        
        
        
    }
    
    private void reiniciarBotonesBD(){
            insertar.setDisable(false);
            actualizar.setDisable(false);
            eliminar.setDisable(false);
        }
    
    
    private void DesBotonesBD(){
            insertar.setDisable(true);
            actualizar.setDisable(true);
            eliminar.setDisable(true);
        }
   
    
    private class DNIChangerListener implements ChangeListener<Boolean>{

        @Override
        public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
            
            if(!t1){
                DesBotonesBD();
                if(dniT.getText().isBlank()){
                   dniT.requestFocus();                       
                }else{
                    if(recupera()){
                        insertar.setDisable(true);
                        actualizar.setDisable(false);
                        eliminar.setDisable(false);
                    }else{
                        insertar.setDisable(false);
                        actualizar.setDisable(true);
                        eliminar.setDisable(true);
                    
                    }
                }                      
            }
        }
    }
    
    private boolean recupera() {

    String sql = "select * from usuarios where dni like '" + dniT.getText() + "'";
    ArrayList<String[]> datos = App.bd.sqlQuery(sql);
    for (String[] dato : datos) {
        nombreT.setText(dato[1]);
        apellidosT.setText(dato[2]);
        emailT.setText(dato[3]);
        contraseñaT.setText(dato[4]);
        notasT.setText(dato[5]);
        return true;
        }

        return false;
    }
    
    private boolean hayNulos() {
        return nombreT.getText().isBlank()
        || apellidosT.getText().isBlank()
        || emailT.getText().isBlank()
        || contraseñaT.getText().isBlank();
    }
    
    
     private boolean datosValidos() {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (hayNulos()) {
            error.setText(App.textos.getString("Hay_campos_requeridos_sin_valor"));
            return false;
            }
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(emailT.getText());
        if (!matcher.matches()) {
            error.setText(App.textos.getString("Email_no_valido"));
            return false;
            }
        if (contraseñaT.getText().length() < 8) {
            error.setText(App.textos.getString("Contraseña_demasiado_corta._Minimo_8"));
            return false;
            }
        return true;
        }
    
     static void mostrarAlertError(String titulo, String texto) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
    }
    
    
    
}
    
    
    
    
    


