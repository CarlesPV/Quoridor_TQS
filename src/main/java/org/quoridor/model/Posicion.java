package org.quoridor.model;

public class Posicion {
	
	public Posicion(int X, int Y);
	
	public int getX();
	public int getY();
	public boolean esValida(int limiteTablero);
	public int distanciaManhattan(Posicion p);
	public boolean esAdyacente(Posicion p);
	public void desplazar(int dx, int dy);
	public boolean equals(Posicion p);
	public void hashCode();

	
	private int x, y;
}
