package org.quoridor.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quoridor.model.Partida;
import org.quoridor.view.IQuoridorVista;

/**
 * Pruebas unitarias para el controlador QuoridorControlador utilizando Mockito (IQuoridorVista).
 */
class QuoridorControladorTest {

    private Partida modelo;
    private IQuoridorVista vistaMock;
    private QuoridorControlador controlador;

    @BeforeEach
    void setUp() {
        modelo = new Partida();
        vistaMock = mock(IQuoridorVista.class);
        controlador = new QuoridorControlador(modelo, vistaMock);
    }

    /**
     * Prueba de Caja Blanca / Interacción con Mock:
     * Verifica que el constructor registre el controlador en la vista y que iniciarJuego arranque
     * la partida y sincronice inicialmente el tablero y el turno en la vista.
     */
    @Test
    @DisplayName("Constructor e iniciarJuego básico sincronizan el inicio de la partida en la vista")
    void testConstructorEIniciarJuegoBasico() {
        // El constructor debe registrar el controlador en la vista pasiva
        verify(vistaMock).setControlador(controlador);

        // Al iniciar juego, la partida debe estar en curso y actualizar el tablero
        controlador.iniciarJuego();
        assertFalse(modelo.isTerminada(), "La partida debe estar en curso tras iniciarJuego.");
        verify(vistaMock).actualizarTablero(any(), any());
        verify(vistaMock).mostrarTurno(any(), any());
    }
}
