package org.quoridor;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.quoridor.controller.QuoridorControlador;
import org.quoridor.model.Partida;
import org.quoridor.view.VentanaPrincipalJavaFX;

/**
 * Aplicación JavaFX principal de Quoridor.
 * Carga la vista FXML, inicializa el modelo y conecta el controlador definitivo.
 */
public class QuoridorApp extends Application {

    private static final String RUTA_FXML = "/view/ventana_principal.fxml";
    private static final String TITULO_VENTANA = "Quoridor";
    private static final double ANCHO_MINIMO = 860.0;
    private static final double ALTO_MINIMO = 690.0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(QuoridorApp.class.getResource(RUTA_FXML));
        Parent root = loader.load();

        VentanaPrincipalJavaFX vista = loader.getController();
        Partida modelo = new Partida();
        QuoridorControlador controlador = new QuoridorControlador(modelo, vista);
        controlador.iniciarJuego();

        Scene scene = new Scene(root);
        primaryStage.setTitle(TITULO_VENTANA);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(ANCHO_MINIMO);
        primaryStage.setMinHeight(ALTO_MINIMO);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
