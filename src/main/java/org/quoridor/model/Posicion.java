package org.quoridor.model;

public class Posicion {

	public Posicion() {
		x = 0;
		y = 0;
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Posicion violada tras la construcción";
	}

	public Posicion(int posX, int posY) {
		x = posX;
		y = posY;
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Posicion violada tras la construcción";
	}

	// Invariante de clase: las coordenadas internas mantienen consistencia con sus accesores
	private boolean verificarInvariante() {
		return this.x == getX() && this.y == getY();
	}

	public int getX() { return x; }
	public int getY() { return y; }

	public boolean esValida(int limiteTablero) {
		assert verificarInvariante() : "Invariante de Posicion violada en esValida";
		boolean valida = (x >= 0 && x < limiteTablero) && (y >= 0 && y < limiteTablero);
		assert !valida || limiteTablero > 0 : "Una posición válida requiere un límite de tablero positivo";
		return valida;
	}

	public int distanciaManhattan(Posicion p) {
		// Pre-condición e invariante
		assert p != null : "La posición destino no puede ser nula";
		assert verificarInvariante() : "Invariante de Posicion violada en distanciaManhattan";

		int resultado = Math.abs(this.x - p.x) + Math.abs(this.y - p.y);

		// Post-condición
		assert resultado >= 0 : "La distancia Manhattan no puede ser negativa";
		return resultado;
	}

	public boolean esAdyacente(Posicion p) {
		// Pre-condición e invariante
		assert p != null : "La posición a comparar no puede ser nula";
		assert verificarInvariante() : "Invariante de Posicion violada en esAdyacente";

		boolean adyacente = this.distanciaManhattan(p) == 1;

		// Post-condición
		assert !adyacente || !this.equals(p) : "Una posición no puede ser adyacente a sí misma";
		return adyacente;
	}

	public void desplazar(int dx, int dy) {
		assert verificarInvariante() : "Invariante de Posicion violada al inicio de desplazar";
		int prevX = this.x;
		int prevY = this.y;

		this.x += dx;
		this.y += dy;

		// Post-condición e invariante
		assert this.x == prevX + dx && this.y == prevY + dy
				: "El desplazamiento no se ha aplicado correctamente";
		assert verificarInvariante() : "Invariante de Posicion violada tras desplazar";
	}

	public boolean equals(Posicion p) {
		assert p != null : "La posición a comparar no puede ser nula";
		assert verificarInvariante() : "Invariante de Posicion violada en equals";
		return (this.x == p.x && this.y == p.y);
	}
	
	// Necesario para que los metodos de HashSet funcionen correctamente
	@Override
    public boolean equals(Object obj) {
        if (this == obj) 
        {
        	return true;
        }
        if (obj == null || getClass() != obj.getClass()) 
        {
        	return false;
        }
        
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