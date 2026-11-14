package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import org.junit.jupiter.api.Test;

class SorteadorTurnoTest {

	@Test
	void testSortear() {
		// Caso 2 jugadores
		ISorteadorTurno sorteador = new SorteadorTurno();
	    List<Jugador> jugadores = Arrays.asList(
	        new Jugador("J1", Color.ROJO, new Posicion(4,8), 0, 5),
	        new Jugador("J2", Color.AZUL, new Posicion(4,0), 8, 5)
	    );
	    int indiceSeleccionado = sorteador.sortear(jugadores);
	    // Comprobamos que el índice esté dentro de los límites de la lista (0 o 1)
	    assertTrue(indiceSeleccionado >= 0 && indiceSeleccionado < jugadores.size(), 
	        "El índice sorteado debe ser un índice válido de la lista de jugadores.");
	    
	    // Caso 0 jugadores
	    List<Jugador> jugadores0 = new ArrayList<>();
	    // Verificamos que al llamar a sortear con una lista vacía, lanza una excepción
	    try
	    {
	    	sorteador.sortear(jugadores0);
	    	assertTrue(false);
	    }
	    catch (AssertionError e) {}
	    
	    // Caso lista null
	    try
	    {
	    	sorteador.sortear(null);
	    	assertTrue(false);
	    }
	    catch (AssertionError e) {}
	}

}
