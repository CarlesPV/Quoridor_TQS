package org.quoridor.model;

public class Jugador {

	private String nombre;
	private Color color;
	private Posicion posicionActual;
	private int filaObjetivo;
	private int murosRestantes;
	
	public Jugador();
	public Jugador(String nombre, Color color, Posicion posicionActual, int filaObjetivo, int murosRestantes);
	
	public String getNombre();
	public Color getColor();
	public Posicion getPosicionActual();
	public int getFilaObjetivo();
	public int getMurosRestantes();
	
	public void moverA(Posicion p);
	public void consumirMuro();
	public boolean hasMurosRestantes();
	public boolean haLlegadoAMeta();
	public void reiniciarEstado(Posicion origen, int murosIniciales);
	
}
