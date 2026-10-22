package org.quoridor.model;

public class Muro {

	private Posicion origen;
	private Orientacion orientacion;
	
	public Posicion getOrigen();
	public Orientacion getOrientacion();
	public Muro();
	public Muro(Posicion origen, Orientacion orientacion);
	
	public boolean esPosicionValida(int limiteTablero);
	public boolean colisionaCon(Muro otroMuro);
	public boolean bloqueaPaso(Posicion origen, Posicion destino);
	
}
