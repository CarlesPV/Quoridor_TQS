package org.quoridor.model;
import java.util.*;

public class Partida {
	
	public Partida();

	public void configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio);
	public void iniciarPartida();
	public void realizarMovimiento(Posicion p);
	public boolean colocarMuro(Muro m);
	boolean comprobarVictoria();
	
	private Tablero tablero;
	private List<Jugador> jugadores;
	private ISorteadorTurno sorteadorTurno;
	private int indiceJugadorActual;
	private boolean terminada;
	private int tamanoTablero;
}
