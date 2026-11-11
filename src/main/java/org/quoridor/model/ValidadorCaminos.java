package org.quoridor.model;
import java.util.*;

public class ValidadorCaminos implements IValidadorCaminos {
	@Override
	public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);
	
	@Override
	public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero)
	{
		assert(inicio != null) : "La posicion inicial es null";
		assert(filaObjetivo >= 0 && filaObjetivo < tamanoTablero) : "filaObjetivo fuera del rango del tablero";
		
		if (inicio.getY() == filaObjetivo) 
		{
			return 0;
		}
		
		Map<Posicion, Integer> distancias = new HashMap<>();
        Queue<Posicion> cola = new LinkedList<>();

        cola.add(inicio);
        distancias.put(inicio, 0);

        while (!cola.isEmpty()) 
        {
            Posicion actual = cola.poll();
            int distanciaActual = distancias.get(actual);

            if (actual.getY() == filaObjetivo) 
            {
                return distanciaActual;
            }

            for (Posicion vecino : obtenerVecinosValidos(actual, muros, tamanoTablero)) 
            {
                if (!distancias.containsKey(vecino)) 
                {
                    distancias.put(vecino, distanciaActual + 1);
                    cola.add(vecino);
                }
            }
        }
        
        // Si no existe camino
        return -1;
	}
	
	// Métodos auxiliares
	List<Posicion> obtenerVecinosValidos(Posicion actual, List<Muro> muros, int tamanoTablero)
	{
		assert(actual != null) : "La posicion actual es null";
		
		List<Posicion> vecinos = new ArrayList<>();
        
        // Movimientos posibles: Arriba, Abajo, Izquierda, Derecha
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        
        for (int i = 0; i < 4; i++) 
        {
            Posicion destino = new Posicion(actual.getX() + dx[i], actual.getY() + dy[i]);
            if (destino.esValida(tamanoTablero))
            {
            	if (!estaBloqueadoPorMuro(actual, destino, muros))
            	{
            		vecinos.add(destino);
            	}
            }
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
