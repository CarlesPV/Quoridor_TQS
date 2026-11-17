package org.quoridor.controller;

import org.quoridor.model.Jugador;
import org.quoridor.model.Muro;
import org.quoridor.model.Orientacion;
import org.quoridor.model.Partida;
import org.quoridor.model.Posicion;
import org.quoridor.model.Tablero;
import org.quoridor.view.IQuoridorControlador;
import org.quoridor.view.IQuoridorVista;

/**
 * Controlador de Quoridor que conecta el Modelo (Partida) con la Vista pasiva (IQuoridorVista).
 * Implementa el contrato IQuoridorControlador y valida contratos DbC (precondiciones e invariante).
 */
public class QuoridorControlador implements IQuoridorControlador {

    private static final int MUROS_INICIALES_ESTANDAR = 10;

    private final Partida modelo;
    private final IQuoridorVista vista;

    /**
     * Construye un nuevo controlador asociando el modelo y la vista pasiva.
     *
     * @param modelo Instancia de la partida en curso (no nula).
     * @param vista Interfaz pasiva desacoplada del motor visual (no nula).
     */
    public QuoridorControlador(Partida modelo, IQuoridorVista vista) {
        // Precondiciones DbC
        assert modelo != null : "El modelo no puede ser nulo";
        assert vista != null : "La vista no puede ser nula";

        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);

        // Invariante de clase
        assert verificarInvariante() : "Invariante violada tras construcción de QuoridorControlador";
    }

    /**
     * Comprueba la invariante de clase garantizando que las referencias esenciales sigan existiendo.
     *
     * @return true si el estado interno es consistente; false en caso contrario.
     */
    private boolean verificarInvariante() {
        return this.modelo != null && this.vista != null;
    }

    /**
     * Inicia una nueva partida con la configuración estándar de 2 jugadores.
     */
    public void iniciarJuego() {
        assert verificarInvariante() : "Invariante violada al inicio de iniciarJuego";

        this.modelo.configurarPartida(2, Tablero.TAMANO_TABLERO, MUROS_INICIALES_ESTANDAR, true);
        this.modelo.iniciarPartida();
        actualizarVistaCompleta();

        // Postcondición e invariante
        assert !this.modelo.isTerminada() : "La partida debe estar en curso tras iniciarJuego";
        assert verificarInvariante() : "Invariante violada al finalizar iniciarJuego";
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
        assert verificarInvariante() : "Invariante violada al inicio de onCasillaClic";

        if (this.modelo.isTerminada()) {
            this.vista.mostrarError("La partida ya ha terminado. Pulsa Reiniciar.");
            return;
        }

        if (x < 0 || x >= Tablero.TAMANO_TABLERO || y < 0 || y >= Tablero.TAMANO_TABLERO) {
            this.vista.mostrarError("Casilla fuera del tablero.");
            return;
        }

        Posicion destino = new Posicion(x, y);
        boolean exito = this.modelo.realizarMovimiento(destino);
        if (!exito) {
            this.vista.mostrarError("Movimiento no válido.");
            return;
        }

        this.vista.limpiarMensajes();
        this.vista.actualizarTablero(this.modelo.getPosicionesJugadores(), this.modelo.getMurosColocados());

        Jugador ganador = this.modelo.comprobarVictoria();
        if (ganador != null) {
            this.vista.mostrarVictoria(ganador.getNombre());
        } else {
            Jugador actual = this.modelo.getJugadorActual();
            this.vista.mostrarTurno(actual.getNombre(), actual.getColor());
        }

        assert verificarInvariante() : "Invariante violada al finalizar onCasillaClic";
    }

    @Override
    public void onInterseccionClic(int x, int y, Orientacion orientacion) {
        assert verificarInvariante() : "Invariante violada al inicio de onInterseccionClic";

        if (this.modelo.isTerminada()) {
            this.vista.mostrarError("La partida ya ha terminado. Pulsa Reiniciar.");
            return;
        }

        if (orientacion == null || x < 0 || x >= Tablero.TAMANO_TABLERO - 1 || y < 0 || y >= Tablero.TAMANO_TABLERO - 1) {
            this.vista.mostrarError("No se puede colocar el muro en esa posición o no te quedan muros.");
            return;
        }
        
        Muro muro = new Muro(new Posicion(x, y), orientacion);
        boolean exito = this.modelo.colocarMuro(muro);
        
        if (!exito) {
            this.vista.mostrarError("No se puede colocar el muro en esa posición o no te quedan muros.");
            return;
        }
        
        this.vista.limpiarMensajes();
        this.vista.actualizarTablero(this.modelo.getPosicionesJugadores(), this.modelo.getMurosColocados());
        this.vista.actualizarMurosRestantes(this.modelo.getMurosRestantesPorJugador());
        Jugador actual = this.modelo.getJugadorActual();
        this.vista.mostrarTurno(actual.getNombre(), actual.getColor());

        assert verificarInvariante() : "Invariante violada al finalizar onInterseccionClic";
    }

    @Override
    public void onBotonReiniciar() {
        // Pendiente de implementar en paso 4
    }
}
