package org.quoridor.model;

import java.util.*;

public class Tablero {

	private List<Muro> murosColocados;
	private Map<Color, Posicion> posicionesJugadores;
	private IValidadorCaminos validadorCaminos;
	
	public static final int TAMANO_TABLERO = 9;
	public static final int MAX_MUROS = 20;
	
	public Tablero() {
		this.murosColocados = new ArrayList<>();
		this.posicionesJugadores = new HashMap<>();
		this.validadorCaminos = new ValidadorCaminos();
		
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Tablero violada tras la construcción por defecto";
	}
	
	public Tablero(IValidadorCaminos validadorCaminos) {
		// Pre-condición
		assert validadorCaminos != null : "El validador de caminos no puede ser nulo";
		
		this.murosColocados = new ArrayList<>();
		this.posicionesJugadores = new HashMap<>();
		this.validadorCaminos = validadorCaminos;
		
		// Invariante de clase
		assert verificarInvariante() : "Invariante de Tablero violada tras la construcción con inyección";
	}
	
	// Invariante de clase: lista de muros y mapas no nulos, máximo 20 muros
	private boolean verificarInvariante() {
		return this.murosColocados != null
				&& this.murosColocados.size() <= MAX_MUROS
				&& this.posicionesJugadores != null
				&& this.validadorCaminos != null;
	}
	
	public List<Muro> getMurosColocados() { return this.murosColocados; }
	public Map<Color, Posicion> getPosicionesJugadores() { return this.posicionesJugadores; }
	public IValidadorCaminos getValidadorCaminos() { return this.validadorCaminos; }
	public Posicion getPosicionJugador(Color color);
	public void setPosicionJugador(Color color, Posicion posicion);
	
	public boolean esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente);
	public boolean colisionaConMurosExistentes(Muro nuevoMuro);
	public boolean esColocacionMuroValida(Muro muro);
	public void colocarMuro(Muro muro);
	public boolean hayCaminoParaTodos(List<Jugador> jugadores);

}
