package org.quoridor.model;
import java.util.*;

public class ValidadorCaminos implements IValidadorCaminos {
	@Override
	public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);
	
	@Override
	public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros);
	
	// Métodos auxiliares
	List<Posicion> obtenerVecinosValidos(Posicion actual, List<Muro> muros, int tamanoTablero)
	{
		List<Posicion> vecinos = new ArrayList<>();
        
        // Movimientos posibles: Arriba, Abajo, Izquierda, Derecha
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        
        for (int i = 0; i < 4; i++) 
        {
            Posicion destino = new Posicion(actual.getX() + dx[i], actual.getY() + dy[i]);

            vecinos.add(destino);
        }

        return vecinos;
	}
	
	boolean estaBloqueadoPorMuro(Posicion origen, Posicion destino, List<Muro> muros)
	{
		assert(origen != null) : "La posicion origen es null";
		assert(destino != null) : "La posicion destino es null";
		
		for (Muro muro : muros) 
		{
	        if (muro.bloqueaPaso(origen, destino)) 
	        {
	            return true;
	        }
	    }
		return false;
	}
	
}
