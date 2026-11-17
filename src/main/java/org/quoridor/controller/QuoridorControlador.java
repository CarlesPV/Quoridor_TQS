package org.quoridor.controller;

import org.quoridor.model.Jugador;
import org.quoridor.model.Orientacion;
import org.quoridor.model.Partida;
import org.quoridor.model.Tablero;
import org.quoridor.view.IQuoridorControlador;
import org.quoridor.view.IQuoridorVista;

/**
 * Controlador de Quoridor que conecta el Modelo (Partida) con la Vista pasiva (IQuoridorVista).
 * Implementa el contrato IQuoridorControlador para responder a eventos de la interfaz de usuario.
 */
public class QuoridorControlador implements IQuoridorControlador {

    private static final int MUROS_INICIALES_ESTANDAR = 10;

    private final Partida modelo;
    private final IQuoridorVista vista;

    /**
     * Construye un nuevo controlador asociando el modelo y la vista pasiva.
     *
     * @param modelo Instancia de la partida en curso.
     * @param vista Interfaz pasiva desacoplada del motor visual.
     */
    public QuoridorControlador(Partida modelo, IQuoridorVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
    }

    /**
     * Inicia una nueva partida con la configuración estándar de 2 jugadores.
     */
    public void iniciarJuego() {
        this.modelo.configurarPartida(2, Tablero.TAMANO_TABLERO, MUROS_INICIALES_ESTANDAR, true);
        this.modelo.iniciarPartida();
        actualizarVistaCompleta();
    }

    /**
     * Sincroniza el estado completo del modelo con la vista pasiva.
     */
    private void actualizarVistaCompleta() {
        this.vista.actualizarTablero(this.modelo.getPosicionesJugadores(), this.modelo.getMurosColocados());
        Jugador actual = this.modelo.getJugadorActual();
        this.vista.mostrarTurno(actual.getNombre(), actual.getColor());
        this.vista.actualizarMurosRestantes(this.modelo.getMurosRestantesPorJugador());
        this.vista.limpiarMensajes();
    }

    @Override
    public void onCasillaClic(int x, int y) {
        // Pendiente de implementar en paso 2
    }

    @Override
    public void onInterseccionClic(int x, int y, Orientacion orientacion) {
        // Pendiente de implementar en paso 3
    }

    @Override
    public void onBotonReiniciar() {
        // Pendiente de implementar en paso 4
    }
}
