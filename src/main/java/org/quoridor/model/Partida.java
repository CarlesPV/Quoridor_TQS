package org.quoridor.model;
import java.util.*;

public class Partida {
	
	// Constructores
	public Partida()
	{
		this.tablero = null;
		this.jugadores = null;
		this.sorteadorTurno = null;
		this.indiceJugadorActual = 0;
		this.terminada = false;
	}
	
	public Partida(Tablero t, ISorteadorTurno s) 
	{
		// Pre-condiciones
        assert(s != null) : "El sorteador de turnos no puede ser null";
        assert(t != null) : "El tablero no puede ser null";
        
		this.tablero = t;
		this.jugadores = new ArrayList<>();
		this.sorteadorTurno = s;
		this.indiceJugadorActual = 0;
		this.terminada = false;
	}
	
	
	// Invariante
	private boolean verificarInvariante() {
        boolean indicesValidos = true;
        if (jugadores != null && !jugadores.isEmpty()) {
            indicesValidos = (indiceJugadorActual >= 0 && indiceJugadorActual < jugadores.size());
        }

        return this.tablero != null
                && this.jugadores != null
                && this.sorteadorTurno != null
                && indicesValidos;
    }

	public void configurarPartida(List<Jugador> jugadoresPersonalizados)
	{
		// Pre-condiciones e invariante
        assert(jugadoresPersonalizados != null) : "La lista de jugadores no puede ser null";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de configurarPartida";

        this.jugadores = new ArrayList<>(jugadoresPersonalizados);
        
        for (Jugador j : jugadores) {
            this.tablero.setPosicionJugador(j.getColor(), j.getPosicionActual());
        }

        // Post-condiciones e invariante
        assert(!jugadores.isEmpty()) : "La lista de jugadores no debe estar vacía";
        assert verificarInvariante() : "Invariante de Partida violada tras configurarPartida";
	}
	
	public void iniciarPartida()
	{
		this.indiceJugadorActual = sorteadorTurno.sortear(jugadores);
        this.terminada = false;
	}
	
	public void realizarMovimiento(Posicion p);
	public boolean colocarMuro(Muro m);
	boolean comprobarVictoria();
	
	Jugador getJugadorActual()
	{
		assert(jugadores != null && !jugadores.isEmpty()) : "No hay jugadores en la partida";
        assert(indiceJugadorActual >= 0 && indiceJugadorActual < jugadores.size()) : "Índice de jugador actual inválido";
        return jugadores.get(indiceJugadorActual);
	}
	
	boolean isTerminada()
	{
		return terminada;
	}
	
	private Tablero tablero;
	private List<Jugador> jugadores;
	private ISorteadorTurno sorteadorTurno;
	private int indiceJugadorActual;
	private boolean terminada;
}
