package org.quoridor.view;

import org.quoridor.model.Orientacion;

/**
 * Contrato de eventos que la vista delega al controlador.
 * Permite desacoplar la interfaz gráfica de la implementación concreta del controlador.
 */
public interface IQuoridorControlador {

    /**
     * Notifica que el usuario ha hecho clic en una casilla del tablero.
     *
     * @param x Coordenada horizontal de la casilla (0 a 8).
     * @param y Coordenada vertical de la casilla (0 a 8).
     */
    void onCasillaClic(int x, int y);

    /**
     * Notifica que el usuario ha hecho clic en una ranura de muro.
     *
     * @param x Coordenada X de origen del muro.
     * @param y Coordenada Y de origen del muro.
     * @param orientacion Orientación de la ranura pulsada (HORIZONTAL o VERTICAL).
     */
    void onInterseccionClic(int x, int y, Orientacion orientacion);

    /**
     * Notifica que el usuario ha solicitado reiniciar la partida.
     */
    void onBotonReiniciar();
}
