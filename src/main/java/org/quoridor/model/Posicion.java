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
	
	public boolean esAdyacente(Posicion p)
	{
		assert(p != null);
		return this.distanciaManhattan(p) == 1;
	}
	
	public void desplazar(int dx, int dy)
	{
		this.x += dx;
		this.y += dy;
	}
	
	public boolean equals(Posicion p)
	{
		assert(p != null);
		return (this.x == p.x && this.y == p.y);
	}
	
	// Necesario para que los metodos de HashSet funcionen correctamente
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Posicion posicion = (Posicion) obj;
        return x == posicion.x && y == posicion.y;
    }
	
	// Necesario para poder construir camino BFS
	@Override
	public int hashCode()
	{
		return java.util.Objects.hash(x, y);
	}

	
	private int x, y;
}