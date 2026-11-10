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
		assert filaObjetivo >= 0 && filaObjetivo < TAMANO_TABLERO : "La fila objetivo debe estar dentro del tablero";
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
	
	public void moverA(Posicion p);
	public void consumirMuro();
	public boolean hasMurosRestantes();
	public boolean haLlegadoAMeta();
	public void reiniciarEstado(Posicion origen, int murosIniciales);
	
}
