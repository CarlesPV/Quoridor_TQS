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
	public Posicion getPosicionJugador(Color color) {
		return this.posicionesJugadores.get(color);
	}

	public void setPosicionJugador(Color color, Posicion posicion) {
		this.posicionesJugadores.put(color, posicion);
	}
	
	public boolean esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente) {
		if (origen == null || destino == null) {
			return false;
		}
		if (!origen.esValida(TAMANO_TABLERO) || !destino.esValida(TAMANO_TABLERO)) {
			return false;
		}
		if (origen.equals(destino)) {
			return false;
		}
		if (oponente != null && destino.equals(oponente)) {
			return false;
		}

		// Movimiento adyacente ortogonal simple
		if (origen.esAdyacente(destino)) {
			return !hayMuroBloqueante(origen, destino);
		}

		// Salto sobre oponente
		if (oponente != null && origen.esAdyacente(oponente)) {
			if (!hayMuroBloqueante(origen, oponente)) {
				int dx = oponente.getX() - origen.getX();
				int dy = oponente.getY() - origen.getY();
				Posicion saltoRecto = new Posicion(oponente.getX() + dx, oponente.getY() + dy);
				if (destino.equals(saltoRecto) && !hayMuroBloqueante(oponente, saltoRecto)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean hayMuroBloqueante(Posicion origen, Posicion destino) {
		for (Muro muro : this.murosColocados) {
			if (muro.bloqueaPaso(origen, destino)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean colisionaConMurosExistentes(Muro nuevoMuro) {
		// Pre-condición
		assert nuevoMuro != null : "El nuevo muro no puede ser nulo";
		assert verificarInvariante() : "Invariante de Tablero violada en colisionaConMurosExistentes";

		for (Muro muro : this.murosColocados) {
			if (muro.colisionaCon(nuevoMuro)) {
				return true;
			}
		}
		return false;
	}
	public boolean esColocacionMuroValida(Muro muro) {
		assert verificarInvariante() : "Invariante de Tablero violada en esColocacionMuroValida";

		if (muro == null) {
			return false;
		}
		if (this.murosColocados.size() >= MAX_MUROS) {
			return false;
		}
		if (!muro.esPosicionValida(TAMANO_TABLERO)) {
			return false;
		}
		if (colisionaConMurosExistentes(muro)) {
			return false;
		}

		// Verificación de caminos para todos los jugadores
		List<Muro> murosSimulados = new ArrayList<>(this.murosColocados);
		murosSimulados.add(muro);
		for (Map.Entry<Color, Posicion> entry : this.posicionesJugadores.entrySet()) {
			int filaObjetivo = (entry.getKey() == Color.AZUL) ? 0 : (TAMANO_TABLERO - 1);
			if (!this.validadorCaminos.hayCaminoDisponible(entry.getValue(), filaObjetivo,
					murosSimulados, TAMANO_TABLERO)) {
				return false;
			}
		}

		return true;
	}
	public void colocarMuro(Muro muro) {
		// Pre-condiciones
		assert muro != null : "El muro a colocar no puede ser nulo";
		assert this.murosColocados.size() < MAX_MUROS : "Se ha alcanzado el límite máximo de 20 muros";
		assert esColocacionMuroValida(muro) : "La colocación del muro debe ser válida";

		int murosPrevios = this.murosColocados.size();
		this.murosColocados.add(muro);

		// Post-condiciones
		assert this.murosColocados.size() == murosPrevios + 1
				: "El número de muros colocados debe incrementarse en 1";
		assert this.murosColocados.contains(muro)
				: "La lista de muros debe contener el muro recién colocado";

		// Invariante de clase
		assert verificarInvariante() : "Invariante de Tablero violada tras colocar muro";
	}
	public boolean hayCaminoParaTodos(List<Jugador> jugadores);

}
