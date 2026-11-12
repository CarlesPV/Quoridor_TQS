package org.quoridor.model;
import java.util.*;
public interface IValidadorCaminos {
	public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);
	public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);
}
