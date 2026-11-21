package org.quoridor.model;
import java.util.*;

public class SorteadorTurno implements ISorteadorTurno {
	@Override
	public int sortear(List<Jugador> jugadores)
	{
		assert(jugadores != null && !jugadores.isEmpty()) : "La lista de jugadores no puede estar vacía o null";
		
        Random random = new Random();
        int indice = random.nextInt(jugadores.size());
        
        assert indice >= 0 && indice < jugadores.size() : "Indice fuera de rango de jugadores";
        return indice;
    }
}
