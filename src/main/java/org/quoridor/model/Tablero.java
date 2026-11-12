package org.quoridor.model;

import java.util.List;
import java.util.Map;

public class Tablero {

	private List<Muro> murosColocados;
	private Map<Color, Posicion> posicionesJugadores;
	private IValidadorCaminos validadorCaminos;
	
	public static final int TAMANO_TABLERO = 9;
	public static final int MAX_MUROS = 20;
	
	public Tablero();
	public Tablero(IValidadorCaminos validadorCaminos);
	
	public List<Muro> getMurosColocados();
	public Map<Color, Posicion> getPosicionesJugadores();
	public IValidadorCaminos getValidadorCaminos();
	public Posicion getPosicionJugador(Color color);
	public void setPosicionJugador(Color color, Posicion posicion);
	
	public boolean esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente);
	public boolean colisionaConMurosExistentes(Muro nuevoMuro);
	public boolean esColocacionMuroValida(Muro muro);
	public void colocarMuro(Muro muro);
	public boolean hayCaminoParaTodos(List<Jugador> jugadores);

}
