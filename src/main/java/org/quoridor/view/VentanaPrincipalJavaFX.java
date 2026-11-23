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
    private static final String CLASE_CASILLA_META_AZUL = "casilla-meta-azul";
    private static final String CLASE_CASILLA_META_ROJO = "casilla-meta-rojo";
    private static final String CLASE_RANURA = "ranura-muro";
    private static final String CLASE_RANURA_PREVIEW = "ranura-preview";
    private static final String CLASE_INTERSECCION = "interseccion-muro";
    private static final String CLASE_MURO_COLOCADO = "muro-colocado";
    private static final String CLASE_PEON = "peon";
    private static final String CLASE_MENSAJE_ERROR = "mensaje-error";
    private static final String CLASE_MENSAJE_VICTORIA = "mensaje-victoria";
    private static final String CLASE_STATUS_BAR_VICTORIA = "status-bar-victoria";
    private static final String CLASE_TURN_BADGE_VICTORIA = "turn-badge-victoria";

    @FXML
    private GridPane tableroGrid;

    @FXML
    private HBox turnBadge;

    @FXML
    private Label lblTurno;

    @FXML
    private Circle indicadorTurno;

    @FXML
    private VBox panelMurosRestantes;

    @FXML
    private HBox statusBar;

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

    /**
     * Genera la cuadrícula de 17x17 nodos en el GridPane intercalando casillas,
     * ranuras horizontales, ranuras verticales e intersecciones de muro.
     */
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

    /**
     * Crea y registra una casilla interactiva en las coordenadas (x, y) del tablero.
     *
     * @param x Columna lógica de la casilla (0..8).
     * @param y Fila lógica de la casilla (0..8).
     */
    private void agregarCasilla(int x, int y) {
        StackPane casilla = new StackPane();
        casilla.setPrefSize(TAMANO_CASILLA, TAMANO_CASILLA);
        casilla.setMinSize(TAMANO_CASILLA, TAMANO_CASILLA);
        casilla.setMaxSize(TAMANO_CASILLA, TAMANO_CASILLA);
        casilla.getStyleClass().add(CLASE_CASILLA);
        if (y == 0) {
            casilla.getStyleClass().add(CLASE_CASILLA_META_AZUL);
        } else if (y == TAMANO_TABLERO - 1) {
            casilla.getStyleClass().add(CLASE_CASILLA_META_ROJO);
        }
        casilla.setOnMouseClicked(event -> {
            if (controlador != null) {
                controlador.onCasillaClic(x, y);
            }
        });
        casillas[x][y] = casilla;
        tableroGrid.add(casilla, x * 2, y * 2);
    }

    /**
     * Crea una ranura horizontal de muro bajo la casilla (x, y) con previsualización al pasar
     * el ratón y delegación del clic al controlador usando la coordenada de origen ajustada.
     *
     * @param x Columna de la casilla superior adyacente (0..8).
     * @param y Fila lógica de la ranura horizontal (0..7).
     */
    private void agregarRanuraHorizontal(int x, int y) {
        Region ranura = crearRegion(TAMANO_CASILLA, GROSOR_RANURA, CLASE_RANURA);
        int origenX = Math.min(x, TAMANO_TABLERO - 2);
        ranura.setOnMouseEntered(event -> alternarPreviewMuro(origenX, y, Orientacion.HORIZONTAL, true));
        ranura.setOnMouseExited(event -> alternarPreviewMuro(origenX, y, Orientacion.HORIZONTAL, false));
        ranura.setOnMouseClicked(event -> {
            if (controlador != null) {
                controlador.onInterseccionClic(origenX, y, Orientacion.HORIZONTAL);
            }
        });
        ranurasHorizontales[x][y] = ranura;
        tableroGrid.add(ranura, x * 2, y * 2 + 1);
    }

    /**
     * Crea una ranura vertical de muro a la derecha de la casilla (x, y) con previsualización
     * y delegación del clic al controlador usando la coordenada de origen ajustada.
     *
     * @param x Columna lógica de la ranura vertical (0..7).
     * @param y Fila de la casilla izquierda adyacente (0..8).
     */
    private void agregarRanuraVertical(int x, int y) {
        Region ranura = crearRegion(GROSOR_RANURA, TAMANO_CASILLA, CLASE_RANURA);
        int origenY = Math.min(y, TAMANO_TABLERO - 2);
        ranura.setOnMouseEntered(event -> alternarPreviewMuro(x, origenY, Orientacion.VERTICAL, true));
        ranura.setOnMouseExited(event -> alternarPreviewMuro(x, origenY, Orientacion.VERTICAL, false));
        ranura.setOnMouseClicked(event -> {
            if (controlador != null) {
                controlador.onInterseccionClic(x, origenY, Orientacion.VERTICAL);
            }
        });
        ranurasVerticales[x][y] = ranura;
        tableroGrid.add(ranura, x * 2 + 1, y * 2);
    }

    /**
     * Activa o desactiva la clase CSS de previsualización en los dos segmentos de ranura y
     * la intersección central que compondrían un muro completo de longitud 2.
     *
     * @param origenX Coordenada X de origen del muro previsualizado (0..7).
     * @param origenY Coordenada Y de origen del muro previsualizado (0..7).
     * @param orientacion Orientación del muro (horizontal o vertical).
     * @param activar true para mostrar el resaltado; false para ocultarlo.
     */
    private void alternarPreviewMuro(int origenX, int origenY, Orientacion orientacion, boolean activar) {
        if (orientacion == Orientacion.HORIZONTAL) {
            actualizarClasePreview(ranurasHorizontales[origenX][origenY], activar);
            actualizarClasePreview(ranurasHorizontales[origenX + 1][origenY], activar);
        } else {
            actualizarClasePreview(ranurasVerticales[origenX][origenY], activar);
            actualizarClasePreview(ranurasVerticales[origenX][origenY + 1], activar);
        }
        actualizarClasePreview(intersecciones[origenX][origenY], activar);
    }

    /**
     * Aplica o retira la clase de estilo de previsualización sobre una región si no contiene ya un muro.
     *
     * @param region Región visual de ranura o intersección.
     * @param activar true para añadir la clase de preview; false para eliminarla.
     */
    private void actualizarClasePreview(Region region, boolean activar) {
        if (region == null) {
            return;
        }
        if (activar) {
            if (!region.getStyleClass().contains(CLASE_MURO_COLOCADO)
                    && !region.getStyleClass().contains(CLASE_RANURA_PREVIEW)) {
                region.getStyleClass().add(CLASE_RANURA_PREVIEW);
            }
        } else {
            region.getStyleClass().remove(CLASE_RANURA_PREVIEW);
        }
    }

    /**
     * Crea y posiciona el nodo visual correspondiente a la intersección entre cuatro casillas.
     *
     * @param x Coordenada horizontal de la intersección (0..7).
     * @param y Coordenada vertical de la intersección (0..7).
     */
    private void agregarInterseccion(int x, int y) {
        Region interseccion = crearRegion(GROSOR_RANURA, GROSOR_RANURA, CLASE_INTERSECCION);
        intersecciones[x][y] = interseccion;
        tableroGrid.add(interseccion, x * 2 + 1, y * 2 + 1);
    }

    /**
     * Fabrica una Region de dimensiones fijas con la clase CSS especificada.
     *
     * @param ancho Anchura fija en píxeles.
     * @param alto Altura fija en píxeles.
     * @param claseCss Clase CSS inicial a asignar.
     * @return Instancia configurada de Region.
     */
    private Region crearRegion(double ancho, double alto, String claseCss) {
        Region region = new Region();
        region.setPrefSize(ancho, alto);
        region.setMinSize(ancho, alto);
        region.setMaxSize(ancho, alto);
        region.getStyleClass().add(claseCss);
        return region;
    }

    /**
     * Manejador FXML del botón de reinicio; delega la solicitud al controlador conectado.
     */
    @FXML
    private void manejarReiniciar() {
        if (controlador != null) {
            controlador.onBotonReiniciar();
        }
    }

    /**
     * Asocia el controlador que procesará los eventos de interacción del usuario en la ventana.
     *
     * @param controlador Instancia de IQuoridorControlador encargada de la orquestación.
     */
    @Override
    public void setControlador(IQuoridorControlador controlador) {
        this.controlador = controlador;
    }

    /**
     * Redibuja completamente los peones y los muros colocados sobre la cuadrícula del tablero.
     *
     * @param posicionesJugadores Mapa que asocia cada color de jugador con su posición actual.
     * @param murosColocados Lista de muros actualmente presentes en el tablero.
     */
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

    /**
     * Limpia los peones de todas las casillas y elimina las clases visuales de muros y previsualizaciones.
     */
    private void limpiarTableroVisual() {
        for (int y = 0; y < TAMANO_TABLERO; y++) {
            for (int x = 0; x < TAMANO_TABLERO; x++) {
                if (casillas[x][y] != null) {
                    casillas[x][y].getChildren().clear();
                }
                if (x < TAMANO_TABLERO - 1 && ranurasVerticales[x][y] != null) {
                    ranurasVerticales[x][y].getStyleClass().removeAll(CLASE_MURO_COLOCADO, CLASE_RANURA_PREVIEW);
                }
                if (y < TAMANO_TABLERO - 1 && ranurasHorizontales[x][y] != null) {
                    ranurasHorizontales[x][y].getStyleClass().removeAll(CLASE_MURO_COLOCADO, CLASE_RANURA_PREVIEW);
                }
                if (x < TAMANO_TABLERO - 1 && y < TAMANO_TABLERO - 1 && intersecciones[x][y] != null) {
                    intersecciones[x][y].getStyleClass().removeAll(CLASE_MURO_COLOCADO, CLASE_RANURA_PREVIEW);
                }
            }
        }
    }

    /**
     * Dibuja el círculo representativo del peón de un jugador dentro de la casilla correspondiente.
     *
     * @param color Color del jugador propietario del peón.
     * @param posicion Coordenada actual del peón en el tablero.
     */
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

    /**
     * Marca visualmente las dos ranuras contiguas y la intersección central que ocupa un muro colocado.
     *
     * @param muro Muro colocado a representar en el tablero.
     */
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

    /**
     * Añade la clase CSS de muro colocado a una región visual si aún no la tiene.
     *
     * @param region Ranura o intersección a marcar como ocupada por un muro.
     */
    private void marcarComoMuroColocado(Region region) {
        if (region != null && !region.getStyleClass().contains(CLASE_MURO_COLOCADO)) {
            region.getStyleClass().add(CLASE_MURO_COLOCADO);
        }
    }

    /**
     * Actualiza la insignia de turno en la cabecera mostrando el nombre y el color del jugador activo.
     *
     * @param nombreJugador Nombre del jugador al que corresponde mover o colocar muro.
     * @param colorJugador Color identificativo del jugador activo.
     */
    @Override
    public void mostrarTurno(String nombreJugador, Color colorJugador) {
        if (turnBadge != null) {
            turnBadge.getStyleClass().remove(CLASE_TURN_BADGE_VICTORIA);
        }
        if (lblTurno != null) {
            String nombre = (nombreJugador != null) ? nombreJugador : "-";
            lblTurno.setText("Turno: " + nombre);
        }
        if (indicadorTurno != null) {
            aplicarClaseColorPeon(indicadorTurno, colorJugador);
        }
    }

    /**
     * Reconstruye la lista visual del panel lateral con los muros disponibles de cada jugador.
     *
     * @param murosPorJugador Mapa con el color de cada jugador y su número de muros restantes.
     */
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

            String nombreJugador = formatearNombreJugador(entrada.getKey());
            int cantidad = (entrada.getValue() != null) ? entrada.getValue() : 0;
            String unidad = (cantidad == 1) ? " muro" : " muros";
            Label etiqueta = new Label(nombreJugador + ": " + cantidad + unidad);
            etiqueta.getStyleClass().add("muro-item-label");

            fila.getChildren().addAll(iconoColor, etiqueta);
            panelMurosRestantes.getChildren().add(fila);
        }
    }

    /**
     * Formatea la etiqueta descriptiva de un jugador para el panel de muros restantes.
     *
     * @param color Color del jugador a formatear.
     * @return Texto legible con el número de jugador y su color.
     */
    private String formatearNombreJugador(Color color) {
        if (color == null) {
            return "Jugador";
        }
        return switch (color) {
            case ROJO -> "Jugador 1 (ROJO)";
            case AZUL -> "Jugador 2 (AZUL)";
            case VERDE -> "Jugador 3 (VERDE)";
            case AMARILLO -> "Jugador 4 (AMARILLO)";
        };
    }

    /**
     * Muestra un mensaje de error en la barra de estado inferior con estilo de alerta.
     *
     * @param mensaje Texto descriptivo del error a comunicar al usuario.
     */
    @Override
    public void mostrarError(String mensaje) {
        if (statusBar != null) {
            statusBar.getStyleClass().remove(CLASE_STATUS_BAR_VICTORIA);
        }
        if (lblMensaje == null) {
            return;
        }
        lblMensaje.getStyleClass().remove(CLASE_MENSAJE_VICTORIA);
        if (!lblMensaje.getStyleClass().contains(CLASE_MENSAJE_ERROR)) {
            lblMensaje.getStyleClass().add(CLASE_MENSAJE_ERROR);
        }
        lblMensaje.setText((mensaje != null) ? mensaje : "");
    }

    /**
     * Resalta la barra de estado y la insignia superior anunciando la victoria del jugador ganador.
     *
     * @param nombreGanador Nombre del jugador que ha alcanzado su meta.
     */
    @Override
    public void mostrarVictoria(String nombreGanador) {
        String ganador = (nombreGanador != null) ? nombreGanador : "Jugador";
        if (statusBar != null && !statusBar.getStyleClass().contains(CLASE_STATUS_BAR_VICTORIA)) {
            statusBar.getStyleClass().add(CLASE_STATUS_BAR_VICTORIA);
        }
        if (turnBadge != null && !turnBadge.getStyleClass().contains(CLASE_TURN_BADGE_VICTORIA)) {
            turnBadge.getStyleClass().add(CLASE_TURN_BADGE_VICTORIA);
        }
        if (lblTurno != null) {
            lblTurno.setText("Partida finalizada — Ganador: " + ganador);
        }
        if (lblMensaje != null) {
            lblMensaje.getStyleClass().remove(CLASE_MENSAJE_ERROR);
            if (!lblMensaje.getStyleClass().contains(CLASE_MENSAJE_VICTORIA)) {
                lblMensaje.getStyleClass().add(CLASE_MENSAJE_VICTORIA);
            }
            lblMensaje.setText("¡Victoria para " + ganador + "! Pulsa Reiniciar Partida para volver a jugar.");
        }
    }

    /**
     * Restablece los estilos de estado y borra el texto de avisos en la barra inferior.
     */
    @Override
    public void limpiarMensajes() {
        if (statusBar != null) {
            statusBar.getStyleClass().remove(CLASE_STATUS_BAR_VICTORIA);
        }
        if (turnBadge != null) {
            turnBadge.getStyleClass().remove(CLASE_TURN_BADGE_VICTORIA);
        }
        if (lblMensaje == null) {
            return;
        }
        lblMensaje.getStyleClass().removeAll(CLASE_MENSAJE_ERROR, CLASE_MENSAJE_VICTORIA);
        lblMensaje.setText("");
    }

    /**
     * Sustituye las clases CSS de color de peón en un nodo Circle por la correspondiente al jugador.
     *
     * @param circulo Figura circular que representa un peón o indicador de color.
     * @param color Color del jugador a aplicar (o null para dejarlo sin color específico).
     */
    private void aplicarClaseColorPeon(Circle circulo, Color color) {
        circulo.getStyleClass().removeAll("peon-rojo", "peon-azul", "peon-verde", "peon-amarillo");
        if (color != null) {
            circulo.getStyleClass().add(obtenerClaseColorPeon(color));
        }
    }

    /**
     * Genera el nombre de la clase CSS asociada a un color de jugador.
     *
     * @param color Color del jugador.
     * @return Nombre de clase CSS en minúsculas (por ejemplo, "peon-rojo").
     */
    private String obtenerClaseColorPeon(Color color) {
        return "peon-" + color.name().toLowerCase(Locale.ROOT);
    }
}
