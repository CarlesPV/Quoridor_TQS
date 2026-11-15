package org.quoridor.model;
import java.util.*;

public class Partida {
	
	// Constructores
	public Partida()
	{
		this.tablero = new Tablero();
		this.jugadores = new ArrayList<>();
		this.sorteadorTurno = new SorteadorTurno();
		this.indiceJugadorActual = 0;
		this.terminada = true;
		this.sorteoAleatorio = true;

		// Invariante de clase
		assert verificarInvariante() : "Invariante de Partida violada tras la construcción por defecto";
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
		this.terminada = true;
		this.sorteoAleatorio = true;

		// Invariante de clase
		assert verificarInvariante() : "Invariante de Partida violada tras la construcción con inyección";
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
        assert(!jugadoresPersonalizados.isEmpty()) : "La lista de jugadores no puede estar vacía";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de configurarPartida";

        this.jugadores = new ArrayList<>(jugadoresPersonalizados);
        
        for (Jugador j : jugadores) {
            this.tablero.setPosicionJugador(j.getColor(), j.getPosicionActual());
        }

        // Post-condiciones e invariante
        assert(!jugadores.isEmpty()) : "La lista de jugadores no debe estar vacía";
        assert verificarInvariante() : "Invariante de Partida violada tras configurarPartida";
	}

	public void configurarPartida(int numJugadores, int tamanoTablero,
			int murosPorJugador, boolean sorteoAleatorio) {
		// Versión inicial mínima para partida estándar de 2 jugadores en tablero 9x9
		List<Jugador> listaBase = new ArrayList<>();
		listaBase.add(new Jugador("Jugador 1", Color.ROJO,
				new Posicion(4, 0), 8, murosPorJugador));
		listaBase.add(new Jugador("Jugador 2", Color.AZUL,
				new Posicion(4, 8), 0, murosPorJugador));

		this.jugadores = listaBase;
		this.indiceJugadorActual = 0;
		this.terminada = true;
		this.sorteoAleatorio = sorteoAleatorio;

		for (Jugador j : this.jugadores) {
			this.tablero.setPosicionJugador(j.getColor(), j.getPosicionActual());
		}

		assert verificarInvariante() : "Invariante de Partida violada tras configurarPartida";
	}
	
	public void iniciarPartida()
	{
		// Pre-condiciones
        assert(jugadores != null && !jugadores.isEmpty()) : "Debe configurar los jugadores antes de iniciar la partida";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de iniciarPartida";
        
		if (this.sorteoAleatorio) {
			this.indiceJugadorActual = sorteadorTurno.sortear(jugadores);
		} else {
			this.indiceJugadorActual = 0;
		}
        this.terminada = false;
        
        // Post-condiciones y verificaciones
        assert(!this.terminada) : "La partida debe marcarse como no terminada al iniciar";
        assert(this.indiceJugadorActual >= 0 && this.indiceJugadorActual < jugadores.size())
                : "El índice del jugador actual tras el sorteo está fuera de rango";

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Partida violada tras iniciarPartida";
	}
	
	public boolean realizarMovimiento(Posicion p)
	{
		// Pre-condiciones
        assert(p != null) : "La posición de destino no puede ser null";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de realizarMovimiento";

        if (terminada) 
        {
            return false;
        }
		
		Jugador jugadorActual = getJugadorActual();
        Posicion origen = jugadorActual.getPosicionActual();
        Posicion oponentePos = null;
        // Obtener posicion del jugador oponente
        for (Jugador j : jugadores) 
        {
            if (!j.getColor().equals(jugadorActual.getColor())) 
            {
            	oponentePos = j.getPosicionActual();
            }
        }

        boolean movimientoValido = tablero.esMovimientoValido(origen, p, oponentePos);

        if (movimientoValido) {
        	int turnoAnterior = indiceJugadorActual;
            jugadorActual.moverA(p);
            tablero.setPosicionJugador(jugadorActual.getColor(), p);
            
            if (jugadorActual.haLlegadoAMeta()) 
            {
                this.terminada = true;
            } 
            else 
            {
            	indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
                // Post-condición de cambio de turno
                assert(indiceJugadorActual != turnoAnterior) : "El turno debe avanzar a otro jugador";
            }
            
            // Invariante de clase tras éxito
            assert verificarInvariante() : "Invariante de Partida violada tras realizarMovimiento exitoso";
            return true;
        }
        
        return false;
	}
	
	public boolean colocarMuro(Muro m)
	{
        // Pre-condiciones
        assert(m != null) : "El muro a colocar no puede ser null";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de colocarMuro";
		
        if (terminada) 
        {
            return false;
        }
        
		Jugador jugadorActual = getJugadorActual();
		
        if (!jugadorActual.hasMurosRestantes()) 
        {
            return false;
        }
		
		if (tablero.esColocacionMuroValida(m)) 
		{
            int murosPreviosJugador = jugadorActual.getMurosRestantes();
            int murosPreviosTablero = tablero.getMurosColocados().size();
            int turnoAnterior = indiceJugadorActual;
			
            tablero.colocarMuro(m);
            jugadorActual.consumirMuro();     
            indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
            
            // Post-condiciones
            assert(jugadorActual.getMurosRestantes() == murosPreviosJugador - 1) :
            	"Los muros del jugador deben decrementar en 1";
            assert(tablero.getMurosColocados().size() == murosPreviosTablero + 1) :
            	"Los muros del tablero deben aumentar en 1";
            assert(indiceJugadorActual != turnoAnterior) : "El turno debe avanzar tras colocar muro";
            
            // Invariante de clase tras éxito
            assert verificarInvariante() : "Invariante de Partida violada tras colocarMuro exitoso";
            return true;
		}
		return false;
	}
	
	public Jugador comprobarVictoria()
	{
		assert verificarInvariante() : "Invariante de Partida violada al inicio de comprobarVictoria";

		// Comprobamos si alguno de los jugadores ha llegado a su meta
        for (Jugador j : jugadores) 
        {
            if (j.haLlegadoAMeta()) 
            {
                this.terminada = true;
                
                // Post-condiciones
                assert(this.terminada) : "La partida debe marcarse como terminada al haber un ganador";
                assert verificarInvariante() : "Invariante de Partida violada tras detectar victoria";
                return j;
            }
        }
        
        return null; // Nadie ha ganado todavía
	}
	
	public Jugador getJugadorActual()
	{
		assert(jugadores != null && !jugadores.isEmpty()) : "No hay jugadores en la partida";
        assert(indiceJugadorActual >= 0 && indiceJugadorActual < jugadores.size()) : 
        	"Índice de jugador actual inválido";
        return jugadores.get(indiceJugadorActual);
	}
	
	public boolean isTerminada()
	{
		return terminada;
	}

	public List<Jugador> getJugadores() {
		assert verificarInvariante() : "Invariante de Partida violada en getJugadores";
		return Collections.unmodifiableList(this.jugadores);
	}

	public Tablero getTablero() {
		assert verificarInvariante() : "Invariante de Partida violada en getTablero";
		return this.tablero;
	}

	public Map<Color, Posicion> getPosicionesJugadores() {
		assert verificarInvariante() : "Invariante de Partida violada en getPosicionesJugadores";
		return Collections.unmodifiableMap(this.tablero.getPosicionesJugadores());
	}

	public List<Muro> getMurosColocados() {
		assert verificarInvariante() : "Invariante de Partida violada en getMurosColocados";
		return Collections.unmodifiableList(this.tablero.getMurosColocados());
	}

	public Map<Color, Integer> getMurosRestantesPorJugador() {
		assert verificarInvariante() : "Invariante de Partida violada en getMurosRestantesPorJugador";
		Map<Color, Integer> muros = new LinkedHashMap<>();
		for (Jugador j : this.jugadores) {
			muros.put(j.getColor(), j.getMurosRestantes());
		}
		return Collections.unmodifiableMap(muros);
	}
	
	private Tablero tablero;
	private List<Jugador> jugadores;
	private ISorteadorTurno sorteadorTurno;
	private int indiceJugadorActual;
	private boolean terminada;
	private boolean sorteoAleatorio;
}
