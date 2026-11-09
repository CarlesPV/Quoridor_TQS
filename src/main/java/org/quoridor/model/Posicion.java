package org.quoridor.model;

public class Posicion {
	
	public Posicion() { x=0; y=0; }
	public Posicion(int X, int Y) { x=X; y=Y; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	public boolean esValida(int limiteTablero)
	{
		return (x >= 0 && x < limiteTablero) && (y >= 0 && y < limiteTablero);
	}
	
	public int distanciaManhattan(Posicion p)
	{
		assert(p != null);
		return Math.abs(this.x - p.x) + Math.abs(this.y - p.y);
	}
	
	public boolean esAdyacente(Posicion p);
	public void desplazar(int dx, int dy);
	public boolean equals(Posicion p);
	public void hashCode();

	
	private int x, y;
}