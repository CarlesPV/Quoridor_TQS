package org.quoridor.model;

import java.util.*;

public class Tablero {

	private List<Muro> murosColocados;
	private Map<Color, Posicion> posicionesJugadores;
	private Map<Color, Integer> filasObjetivo;
	private int tamanoTablero;
	private IValidadorCaminos validadorCaminos;

	public static final int TAMANO_TABLERO = 9;
	public static final int MAX_MUROS = 20;

	public Tablero() {
		this.murosColocados = new ArrayList<>();
		this.posicionesJugadores = new HashMap<>();
		this.filasObjetivo = new HashMap<>();
		this.tamanoTablero = TAMANO_TABLERO;
		this.validadorCaminos = new ValidadorCaminos();

		// Invariante de clase
		assert verificarInvariante() : "Invariante de Tablero violada tras la construcción por defecto";
	}

	public Tablero(IValidadorCaminos validadorCaminos) {
		// Pre-condición
		assert validadorCaminos != null : "El validador de caminos no puede ser nulo";

		this.murosColocados = new ArrayList<>();
		this.posicionesJugadores = new HashMap<>();
		this.filasObjetivo = new HashMap<>();
		this.tamanoTablero = TAMANO_TABLERO;
		this.validadorCaminos = validadorCaminos;

		// Invariante de clase
		assert verificarInvariante() : "Invariante de Tablero violada tras la construcción con inyección";
	}

	// Invariante de clase: colecciones no nulas, tamaño válido y máximo 20 muros
	private boolean verificarInvariante() {
		return this.murosColocados != null
				&& this.murosColocados.size() <= MAX_MUROS
				&& this.posicionesJugadores != null
				&& this.filasObjetivo != null
				&& this.tamanoTablero >= 5
				&& this.validadorCaminos != null;
	}

	public List<Muro> getMurosColocados() { return this.murosColocados; }
	public Map<Color, Posicion> getPosicionesJugadores() { return this.posicionesJugadores; }
	public IValidadorCaminos getValidadorCaminos() { return this.validadorCaminos; }
	public int getTamanoTablero() { return this.tamanoTablero; }

	public void setTamanoTablero(int tamanoTablero) {
		assert tamanoTablero >= 5 && tamanoTablero % 2 == 1
				: "El tamaño del tablero debe ser impar >= 5";
		this.tamanoTablero = tamanoTablero;
		assert verificarInvariante() : "Invariante de Tablero violada tras cambiar el tamaño";
	}

	public void setFilaObjetivo(Color color, int filaObjetivo) {
		assert color != null : "El color no puede ser nulo";
		assert filaObjetivo >= 0 && filaObjetivo < this.tamanoTablero
				: "La fila objetivo debe estar dentro del tablero";
		this.filasObjetivo.put(color, filaObjetivo);
	}

	public int getFilaObjetivo(Color color) {
		assert color != null : "El color no puede ser nulo";
		int filaPorDefecto = (color == Color.AZUL) ? 0 : (this.tamanoTablero - 1);
		return this.filasObjetivo.getOrDefault(color, filaPorDefecto);
	}

	public void reiniciar() {
		assert verificarInvariante() : "Invariante de Tablero violada al reiniciar";
		this.murosColocados.clear();
		this.posicionesJugadores.clear();
		this.filasObjetivo.clear();
		assert verificarInvariante() : "Invariante de Tablero violada tras reiniciar";
	}

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
		if (!origen.esValida(this.tamanoTablero) || !destino.esValida(this.tamanoTablero)) {
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
		if (!muro.esPosicionValida(this.tamanoTablero)) {
			return false;
		}
		if (colisionaConMurosExistentes(muro)) {
			return false;
		}

		// Verificación de caminos para todos los jugadores usando su fila objetivo configurada
		List<Muro> murosSimulados = new ArrayList<>(this.murosColocados);
		murosSimulados.add(muro);
		for (Map.Entry<Color, Posicion> entry : this.posicionesJugadores.entrySet()) {
			int filaObjetivo = getFilaObjetivo(entry.getKey());
			if (!this.validadorCaminos.hayCaminoDisponible(entry.getValue(), filaObjetivo,
					murosSimulados, this.tamanoTablero)) {
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
	public boolean hayCaminoParaTodos(List<Jugador> jugadores) {
		// Pre-condiciones
		assert jugadores != null : "La lista de jugadores no puede ser nula";
		assert verificarInvariante() : "Invariante de Tablero violada en hayCaminoParaTodos";

		for (Jugador jugador : jugadores) {
			assert jugador != null : "Ningún jugador de la lista puede ser nulo";
			if (!this.validadorCaminos.hayCaminoDisponible(jugador.getPosicionActual(),
					jugador.getFilaObjetivo(), this.murosColocados, this.tamanoTablero)) {
				return false;
			}
		}
		return true;
	}

}
