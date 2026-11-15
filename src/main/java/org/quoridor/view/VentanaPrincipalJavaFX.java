package org.quoridor.view;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.quoridor.model.Color;
import org.quoridor.model.Muro;
import org.quoridor.model.Orientacion;
import org.quoridor.model.Posicion;

/**
 * Vista concreta en JavaFX encargada de renderizar el tablero 9x9, los muros,
 * el panel informativo y delegar las acciones del usuario al controlador.
 */
public class VentanaPrincipalJavaFX implements IQuoridorVista {

    private static final int TAMANO_TABLERO = 9;
    private static final double TAMANO_CASILLA = 48.0;
    private static final double GROSOR_RANURA = 12.0;
    private static final double RADIO_PEON = 16.0;
    private static final double RADIO_ICONO_PANEL = 7.0;
    private static final double ESPACIADO_ITEM_MUROS = 8.0;

    private static final String CLASE_CASILLA = "casilla";
    private static final String CLASE_RANURA = "ranura-muro";
    private static final String CLASE_INTERSECCION = "interseccion-muro";
    private static final String CLASE_MURO_COLOCADO = "muro-colocado";
    private static final String CLASE_PEON = "peon";
    private static final String CLASE_MENSAJE_ERROR = "mensaje-error";
    private static final String CLASE_MENSAJE_VICTORIA = "mensaje-victoria";

    @FXML
    private GridPane tableroGrid;

    @FXML
    private Label lblTurno;

    @FXML
    private Circle indicadorTurno;

    @FXML
    private VBox panelMurosRestantes;

    @FXML
    private Label lblMensaje;

    @FXML
    private Button btnReiniciar;

    private final StackPane[][] casillas = new StackPane[TAMANO_TABLERO][TAMANO_TABLERO];
    private final Region[][] ranurasHorizontales = new Region[TAMANO_TABLERO][TAMANO_TABLERO - 1];
    private final Region[][] ranurasVerticales = new Region[TAMANO_TABLERO - 1][TAMANO_TABLERO];
    private final Region[][] intersecciones = new Region[TAMANO_TABLERO - 1][TAMANO_TABLERO - 1];

    private IQuoridorControlador controlador;

    /**
     * Inicializa los componentes visuales del tablero 9x9 tras la carga del FXML.
     */
    @FXML
    public void initialize() {
        construirTableroVisual();
    }

    private void construirTableroVisual() {
        if (tableroGrid == null) {
            return;
        }
        tableroGrid.getChildren().clear();
        for (int y = 0; y < TAMANO_TABLERO; y++) {
            for (int x = 0; x < TAMANO_TABLERO; x++) {
                agregarCasilla(x, y);
                if (x < TAMANO_TABLERO - 1) {
                    agregarRanuraVertical(x, y);
                }
                if (y < TAMANO_TABLERO - 1) {
                    agregarRanuraHorizontal(x, y);
                }
                if (x < TAMANO_TABLERO - 1 && y < TAMANO_TABLERO - 1) {
                    agregarInterseccion(x, y);
                }
            }
        }
    }

    private void agregarCasilla(int x, int y) {
        StackPane casilla = new StackPane();
        casilla.setPrefSize(TAMANO_CASILLA, TAMANO_CASILLA);
        casilla.setMinSize(TAMANO_CASILLA, TAMANO_CASILLA);
        casilla.setMaxSize(TAMANO_CASILLA, TAMANO_CASILLA);
        casilla.getStyleClass().add(CLASE_CASILLA);
        casilla.setOnMouseClicked(event -> {
            if (controlador != null) {
                controlador.onCasillaClic(x, y);
            }
        });
        casillas[x][y] = casilla;
        tableroGrid.add(casilla, x * 2, y * 2);
    }

    private void agregarRanuraHorizontal(int x, int y) {
        Region ranura = crearRegion(TAMANO_CASILLA, GROSOR_RANURA, CLASE_RANURA);
        ranura.setOnMouseClicked(event -> {
            if (controlador != null) {
                controlador.onInterseccionClic(x, y, Orientacion.HORIZONTAL);
            }
        });
        ranurasHorizontales[x][y] = ranura;
        tableroGrid.add(ranura, x * 2, y * 2 + 1);
    }

    private void agregarRanuraVertical(int x, int y) {
        Region ranura = crearRegion(GROSOR_RANURA, TAMANO_CASILLA, CLASE_RANURA);
        ranura.setOnMouseClicked(event -> {
            if (controlador != null) {
                controlador.onInterseccionClic(x, y, Orientacion.VERTICAL);
            }
        });
        ranurasVerticales[x][y] = ranura;
        tableroGrid.add(ranura, x * 2 + 1, y * 2);
    }

    private void agregarInterseccion(int x, int y) {
        Region interseccion = crearRegion(GROSOR_RANURA, GROSOR_RANURA, CLASE_INTERSECCION);
        intersecciones[x][y] = interseccion;
        tableroGrid.add(interseccion, x * 2 + 1, y * 2 + 1);
    }

    private Region crearRegion(double ancho, double alto, String claseCss) {
        Region region = new Region();
        region.setPrefSize(ancho, alto);
        region.setMinSize(ancho, alto);
        region.setMaxSize(ancho, alto);
        region.getStyleClass().add(claseCss);
        return region;
    }

    @FXML
    private void manejarReiniciar() {
        if (controlador != null) {
            controlador.onBotonReiniciar();
        }
    }

    @Override
    public void setControlador(IQuoridorControlador controlador) {
        this.controlador = controlador;
    }

    @Override
    public void actualizarTablero(Map<Color, Posicion> posicionesJugadores, List<Muro> murosColocados) {
        limpiarTableroVisual();
        if (posicionesJugadores != null) {
            for (Map.Entry<Color, Posicion> entrada : posicionesJugadores.entrySet()) {
                renderizarPeon(entrada.getKey(), entrada.getValue());
            }
        }
        if (murosColocados != null) {
            for (Muro muro : murosColocados) {
                renderizarMuro(muro);
            }
        }
    }

    private void limpiarTableroVisual() {
        for (int y = 0; y < TAMANO_TABLERO; y++) {
            for (int x = 0; x < TAMANO_TABLERO; x++) {
                if (casillas[x][y] != null) {
                    casillas[x][y].getChildren().clear();
                }
                if (x < TAMANO_TABLERO - 1 && ranurasVerticales[x][y] != null) {
                    ranurasVerticales[x][y].getStyleClass().remove(CLASE_MURO_COLOCADO);
                }
                if (y < TAMANO_TABLERO - 1 && ranurasHorizontales[x][y] != null) {
                    ranurasHorizontales[x][y].getStyleClass().remove(CLASE_MURO_COLOCADO);
                }
                if (x < TAMANO_TABLERO - 1 && y < TAMANO_TABLERO - 1 && intersecciones[x][y] != null) {
                    intersecciones[x][y].getStyleClass().remove(CLASE_MURO_COLOCADO);
                }
            }
        }
    }

    private void renderizarPeon(Color color, Posicion posicion) {
        if (color == null || posicion == null) {
            return;
        }
        int x = posicion.getX();
        int y = posicion.getY();
        if (x < 0 || x >= TAMANO_TABLERO || y < 0 || y >= TAMANO_TABLERO || casillas[x][y] == null) {
            return;
        }
        Circle peon = new Circle(RADIO_PEON);
        peon.getStyleClass().addAll(CLASE_PEON, obtenerClaseColorPeon(color));
        casillas[x][y].getChildren().add(peon);
    }

    private void renderizarMuro(Muro muro) {
        if (muro == null || muro.getOrigen() == null || muro.getOrientacion() == null) {
            return;
        }
        int mX = muro.getOrigen().getX();
        int mY = muro.getOrigen().getY();
        if (mX < 0 || mX >= TAMANO_TABLERO - 1 || mY < 0 || mY >= TAMANO_TABLERO - 1) {
            return;
        }
        marcarComoMuroColocado(intersecciones[mX][mY]);
        if (muro.getOrientacion() == Orientacion.HORIZONTAL) {
            marcarComoMuroColocado(ranurasHorizontales[mX][mY]);
            marcarComoMuroColocado(ranurasHorizontales[mX + 1][mY]);
        } else {
            marcarComoMuroColocado(ranurasVerticales[mX][mY]);
            marcarComoMuroColocado(ranurasVerticales[mX][mY + 1]);
        }
    }

    private void marcarComoMuroColocado(Region region) {
        if (region != null && !region.getStyleClass().contains(CLASE_MURO_COLOCADO)) {
            region.getStyleClass().add(CLASE_MURO_COLOCADO);
        }
    }

    @Override
    public void mostrarTurno(String nombreJugador, Color colorJugador) {
        if (lblTurno != null) {
            String nombre = (nombreJugador != null) ? nombreJugador : "-";
            lblTurno.setText("Turno: " + nombre);
        }
        if (indicadorTurno != null) {
            aplicarClaseColorPeon(indicadorTurno, colorJugador);
        }
    }

    @Override
    public void actualizarMurosRestantes(Map<Color, Integer> murosPorJugador) {
        if (panelMurosRestantes == null) {
            return;
        }
        panelMurosRestantes.getChildren().clear();
        if (murosPorJugador == null) {
            return;
        }
        for (Map.Entry<Color, Integer> entrada : murosPorJugador.entrySet()) {
            HBox fila = new HBox(ESPACIADO_ITEM_MUROS);
            fila.setAlignment(Pos.CENTER_LEFT);

            Circle iconoColor = new Circle(RADIO_ICONO_PANEL);
            aplicarClaseColorPeon(iconoColor, entrada.getKey());

            String nombreColor = (entrada.getKey() != null) ? entrada.getKey().name() : "JUGADOR";
            int cantidad = (entrada.getValue() != null) ? entrada.getValue() : 0;
            Label etiqueta = new Label(nombreColor + ": " + cantidad + " muros");
            etiqueta.getStyleClass().add("muro-item-label");

            fila.getChildren().addAll(iconoColor, etiqueta);
            panelMurosRestantes.getChildren().add(fila);
        }
    }

    @Override
    public void mostrarError(String mensaje) {
        if (lblMensaje == null) {
            return;
        }
        lblMensaje.getStyleClass().remove(CLASE_MENSAJE_VICTORIA);
        if (!lblMensaje.getStyleClass().contains(CLASE_MENSAJE_ERROR)) {
            lblMensaje.getStyleClass().add(CLASE_MENSAJE_ERROR);
        }
        lblMensaje.setText((mensaje != null) ? mensaje : "");
    }

    @Override
    public void mostrarVictoria(String nombreGanador) {
        if (lblMensaje == null) {
            return;
        }
        lblMensaje.getStyleClass().remove(CLASE_MENSAJE_ERROR);
        if (!lblMensaje.getStyleClass().contains(CLASE_MENSAJE_VICTORIA)) {
            lblMensaje.getStyleClass().add(CLASE_MENSAJE_VICTORIA);
        }
        String ganador = (nombreGanador != null) ? nombreGanador : "Jugador";
        lblMensaje.setText("¡Victoria para " + ganador + "!");
    }

    @Override
    public void limpiarMensajes() {
        if (lblMensaje == null) {
            return;
        }
        lblMensaje.getStyleClass().removeAll(CLASE_MENSAJE_ERROR, CLASE_MENSAJE_VICTORIA);
        lblMensaje.setText("");
    }

    private void aplicarClaseColorPeon(Circle circulo, Color color) {
        circulo.getStyleClass().removeAll("peon-rojo", "peon-azul", "peon-verde", "peon-amarillo");
        if (color != null) {
            circulo.getStyleClass().add(obtenerClaseColorPeon(color));
        }
    }

    private String obtenerClaseColorPeon(Color color) {
        return "peon-" + color.name().toLowerCase(Locale.ROOT);
    }
}
