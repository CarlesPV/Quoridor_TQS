package org.quoridor.model;

public class Jugador {

	private String nombre;
	private Color color;
	private Posicion posicionActual;
	private int filaObjetivo;
	private int murosRestantes;
	
	public Jugador() {
	}
	
	public Jugador(String nombre, Color color, Posicion posicionActual, int filaObjetivo, int murosRestantes) {
		this.nombre = nombre;
		this.color = color;
		this.posicionActual = posicionActual;
		this.filaObjetivo = filaObjetivo;
		this.murosRestantes = murosRestantes;
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
