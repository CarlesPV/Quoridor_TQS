package org.quoridor.model;

public class ValidadorCaminos implements IValidadorCaminos {
	@Override
	public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);
	
	@Override
	public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros);
	
	// Métodos auxiliares
	List<Posicion> obtenerVecinosValidos(Posicion actual, List<Muro> muros, int tamanoTablero);
	boolean estaBloqueadoPorMuro(Posicion origen, Posicion destino, List<Muro> muros);
	
}
