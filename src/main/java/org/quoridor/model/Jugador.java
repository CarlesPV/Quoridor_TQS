package org.quoridor.model;

public class Jugador {

	private String nombre;
	private Color color;
	private Posicion posicionActual;
	private int filaObjetivo;
	private int murosRestantes;
	
	private static final int TAMANO_TABLERO = 9;
	
	public Jugador() {
	}
	
	public Jugador(String nombre, Color color, Posicion posicionActual, int filaObjetivo, int murosRestantes) {
		// Pre-condiciones
		assert nombre != null && !nombre.trim().isEmpty() : "El nombre no puede ser nulo ni vacío";
		assert color != null : "El color no puede ser nulo";
		assert posicionActual != null : "La posición actual no puede ser nula";
		assert posicionActual.esValida(TAMANO_TABLERO) : "La posición inicial debe ser válida en el tablero";
		assert filaObjetivo >= 0 && filaObjetivo < TAMANO_TABLERO
				: "La fila objetivo debe estar dentro del tablero";
		assert murosRestantes >= 0 : "Los muros restantes no pueden ser negativos";
		
		this.nombre = nombre;
		this.color = color;
		this.posicionActual = posicionActual;
		this.filaObjetivo = filaObjetivo;
		this.murosRestantes = murosRestantes;
		
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Jugador violada tras la construcción";
	}
	
	// Invariante de clase: murosRestantes >= 0 y posicionActual != null
	private boolean verificarInvariante() {
		return this.murosRestantes >= 0 && this.posicionActual != null;
	}
	
	public String getNombre() { return this.nombre; }
	public Color getColor() { return this.color; }
	public Posicion getPosicionActual() { return this.posicionActual; }
	public int getFilaObjetivo() { return this.filaObjetivo; }
	public int getMurosRestantes() { return this.murosRestantes; }
	
	public void moverA(Posicion p) {
		// Pre-condiciones
		assert p != null : "La posición de destino no puede ser nula";
		assert p.esValida(TAMANO_TABLERO) : "La posición de destino debe ser válida en el tablero";
		
		this.posicionActual = p;
		
		// Post-condición
		assert this.posicionActual.equals(p) : "La posición actual debe ser la posición de destino";
		
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Jugador violada tras mover";
	}
	
	public void consumirMuro() {
		// Pre-condiciones
		assert this.murosRestantes > 0 : "No quedan muros disponibles para consumir";
		
		int murosPrevios = this.murosRestantes;
		this.murosRestantes--;
		
		// Post-condición
		assert this.murosRestantes == murosPrevios - 1
				: "Los muros restantes deben decrementar exactamente en 1";
		
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Jugador violada tras consumir muro";
	}

	public boolean hasMurosRestantes() {
		assert verificarInvariante() : "Invariante de Jugador violada en hasMurosRestantes";
		return this.murosRestantes > 0;
	}

	public boolean haLlegadoAMeta() {
		assert verificarInvariante() : "Invariante de Jugador violada en haLlegadoAMeta";
		return this.posicionActual.getY() == this.filaObjetivo;
	}

	public void reiniciarEstado(Posicion origen, int murosIniciales) {
		// Pre-condiciones
		assert origen != null : "La posición de origen no puede ser nula";
		assert origen.esValida(TAMANO_TABLERO) : "La posición de origen debe ser válida en el tablero";
		assert murosIniciales >= 0 : "Los muros iniciales no pueden ser negativos";
		
		this.posicionActual = origen;
		this.murosRestantes = murosIniciales;
		
		// Post-condiciones
		assert this.posicionActual.equals(origen) : "La posición actual debe ser la posición de origen";
		assert this.murosRestantes == murosIniciales
				: "Los muros restantes deben coincidir con los muros iniciales";
		
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Jugador violada tras reiniciar estado";
	}
	
}
