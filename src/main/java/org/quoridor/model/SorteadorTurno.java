package org.quoridor.model;
import java.util.*;

public class SorteadorTurno implements ISorteadorTurno {
	@Override
	public int sortear(List<Jugador> jugadores)
	{
        Random random = new Random();
        return random.nextInt(1);
    }
}
