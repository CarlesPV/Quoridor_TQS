package org.quoridor;

import javafx.application.Application;

/**
 * Punto de entrada estándar de la aplicación Quoridor.
 * Delega en {@link QuoridorApp} sin extender {@link Application} para permitir
 * el arranque directo en Java 21 sin configurar --module-path manualmente.
 */
public final class Main {

    private Main() {
        // Clase lanzadora no instanciable
    }

    /**
     * Arranca la interfaz gráfica JavaFX de Quoridor.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        Application.launch(QuoridorApp.class, args);
    }
}
