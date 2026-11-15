package org.quoridor.view;

import java.util.List;
import java.util.Map;
import org.quoridor.model.Color;
import org.quoridor.model.Muro;
import org.quoridor.model.Posicion;

/**
 * Contrato de la vista pasiva de Quoridor (Passive View).
 * Permite actualizar la interfaz y notificar mensajes sin acoplarse a JavaFX en los tests.
 */
public interface IQuoridorVista {

    /**
     * Asocia el controlador que recibirá los eventos de interacción del usuario.
     *
     * @param controlador Manejador de eventos de la vista.
     */
    void setControlador(IQuoridorControlador controlador);

    /**
     * Actualiza la representación visual del tablero con las posiciones de los peones y los muros colocados.
     *
     * @param posicionesJugadores Mapa con el color de cada jugador y su posición actual en el tablero.
     * @param murosColocados Lista de muros actualmente colocados en el tablero.
     */
    void actualizarTablero(Map<Color, Posicion> posicionesJugadores, List<Muro> murosColocados);

    /**
     * Muestra en la interfaz de usuario de quién es el turno actual.
     *
     * @param nombreJugador Nombre del jugador activo.
     * @param colorJugador Color asignado al jugador activo.
     */
    void mostrarTurno(String nombreJugador, Color colorJugador);

    /**
     * Actualiza el contador de muros disponibles para cada jugador en el panel informativo.
     *
     * @param murosPorJugador Mapa con el color de cada jugador y su cantidad de muros restantes.
     */
    void actualizarMurosRestantes(Map<Color, Integer> murosPorJugador);

    /**
     * Muestra un mensaje de error al usuario (por ejemplo, jugada o colocación de muro inválida).
     *
     * @param mensaje Descripción del error a mostrar.
     */
    void mostrarError(String mensaje);

    /**
     * Muestra el anuncio de fin de partida indicando el jugador ganador.
     *
     * @param nombreGanador Nombre del jugador que ha alcanzado la victoria.
     */
    void mostrarVictoria(String nombreGanador);

    /**
     * Limpia los mensajes previos de error o estado en la interfaz.
     */
    void limpiarMensajes();
}
