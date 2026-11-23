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
	    
	    // Caso 1 jugador
	    List<Jugador> jugadores1 = Arrays.asList(
		        new Jugador("J1", Color.ROJO, new Posicion(4,8), 0, 5)
		    );
	    indiceSeleccionado = sorteador.sortear(jugadores1);
	    // Comprobamos que el índice esté dentro de los límites de la lista (0)
	    assertTrue(indiceSeleccionado == 0, 
	        "El índice sorteado debe ser 0.");
	    
	    // Caso 4 jugadores
	    List<Jugador> jugadores4 = Arrays.asList(
		        new Jugador("J1", Color.ROJO, new Posicion(4,8), 0, 5),
		        new Jugador("J2", Color.AZUL, new Posicion(4,0), 8, 5),
		        new Jugador("J3", Color.VERDE, new Posicion(0,4), 4, 5),
		        new Jugador("J4", Color.AMARILLO, new Posicion(8,4), 4, 5)
		    );
	    indiceSeleccionado = sorteador.sortear(jugadores4);
	    // Comprobamos que el índice esté dentro de los límites de la lista (0, 1, 2, 3)
	    assertTrue(indiceSeleccionado >= 0 && indiceSeleccionado < jugadores4.size(), 
	        "El índice sorteado debe ser un índice válido de la lista de jugadores.");
	    
	    // Caso 4 jugadores bucle
	    List<Jugador> jugadoresLoop= Arrays.asList(
		        new Jugador("J1", Color.ROJO, new Posicion(4,8), 0, 5),
		        new Jugador("J2", Color.AZUL, new Posicion(4,0), 8, 5),
		        new Jugador("J3", Color.VERDE, new Posicion(0,4), 4, 5),
		        new Jugador("J4", Color.AMARILLO, new Posicion(8,4), 4, 5)
		    );
	    
	    for (int i = 0; i < 15; i++)
	    {
	    	indiceSeleccionado = sorteador.sortear(jugadoresLoop);
	    	// Comprobamos que el índice esté dentro de los límites de la lista (0 o 1)
	    	assertTrue(indiceSeleccionado >= 0 && indiceSeleccionado < jugadoresLoop.size(), 
	    			"El índice sorteado debe ser un índice válido de la lista de jugadores.");
	    }

	    
	    // Caso 0 jugadores
	    List<Jugador> jugadores0 = new ArrayList<>();
	    // Verificamos que al llamar a sortear con una lista vacía, lanza AssertionError
	    assertThrows(AssertionError.class, () -> sorteador.sortear(jugadores0),
	        "Debe lanzar AssertionError si la lista de jugadores está vacía.");
	    
	    // Caso lista null
	    assertThrows(AssertionError.class, () -> sorteador.sortear(null),
	        "Debe lanzar AssertionError si la lista de jugadores es null.");
	}

}
