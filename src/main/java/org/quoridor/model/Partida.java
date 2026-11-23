package org.quoridor.model;

import java.util.*;

/**
 * Fachada principal del modelo de Quoridor.
 * Coordina el ciclo de vida de la partida mostrando vistas del
 * estado para el controlador y la interfaz gráfica.
 */
public class Partida {

    private Tablero tablero;
    private List<Jugador> jugadores;
    private ISorteadorTurno sorteadorTurno;
    private int indiceJugadorActual;
    private boolean terminada;
    private boolean sorteoAleatorio;

    /**
     * Construye una nueva partida con un tablero estándar y un sorteador de turnos aleatorio por defecto.
     */
    public Partida() {
        this.tablero = new Tablero();
        this.jugadores = new ArrayList<>();
        this.sorteadorTurno = new SorteadorTurno();
        this.indiceJugadorActual = 0;
        this.terminada = true;
        this.sorteoAleatorio = true;

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Partida violada tras la construcción por defecto";
    }

    /**
     * Construye una partida inyectando sus dependencias principales.
     *
     * @param t Instancia de Tablero a utilizar.
     * @param s Estrategia de ISorteadorTurno para elegir el turno inicial.
     */
    public Partida(Tablero t, ISorteadorTurno s) {
        // Precondiciones
        assert s != null : "El sorteador de turnos no puede ser null";
        assert t != null : "El tablero no puede ser null";

        this.tablero = t;
        this.jugadores = new ArrayList<>();
        this.sorteadorTurno = s;
        this.indiceJugadorActual = 0;
        this.terminada = true;
        this.sorteoAleatorio = true;

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Partida violada tras la construcción con inyección";
    }

    /**
     * Comprueba la invariante de clase: componentes esenciales no nulos e índice de turno válido
     * siempre que existan jugadores configurados.
     *
     * @return true si el estado interno de la partida es coherente.
     */
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

    /**
     * Configura una partida a partir de una lista personalizada de 2 o 4 jugadores preconstruidos.
     * Reinicia el tablero y registra la posición inicial y fila objetivo de cada participante.
     *
     * @param jugadoresPersonalizados Lista de 2 o 4 jugadores configurados.
     */
    public void configurarPartida(List<Jugador> jugadoresPersonalizados) {
        // Precondiciones e invariante
        assert jugadoresPersonalizados != null : "La lista de jugadores no puede ser null";
        assert jugadoresPersonalizados.size() == 2 || jugadoresPersonalizados.size() == 4
                : "La lista de jugadores debe tener 2 o 4 jugadores";
        for (Jugador j : jugadoresPersonalizados) {
            assert j != null : "Un jugador no puede ser null";
        }
        assert verificarInvariante() : "Invariante de Partida violada al inicio de configurarPartida";

        this.tablero.reiniciar();

        this.jugadores = new ArrayList<>(jugadoresPersonalizados);
        this.indiceJugadorActual = 0;
        this.sorteoAleatorio = true;

        for (Jugador j : jugadores) {
            this.tablero.setPosicionJugador(j.getColor(), j.getPosicionActual());
            this.tablero.setFilaObjetivo(j.getColor(), j.getFilaObjetivo());
        }

        this.terminada = true;

        // Postcondiciones e invariante
        assert !jugadores.isEmpty() : "La lista de jugadores no debe estar vacía";
        assert verificarInvariante() : "Invariante de Partida violada tras configurarPartida";
    }

    /**
     * Configura una partida estándar de 2 o 4 jugadores con parámetros validados.
     * En modo de 4 jugadores, Jugador 3 (VERDE) parte de (0, centro) hacia la fila tamanoTablero - 1
     * y Jugador 4 (AMARILLO) parte de (tamanoTablero - 1, centro) hacia la fila 0, manteniendo la
     * evaluación de meta por fila de Jugador.haLlegadoAMeta().
     *
     * @param numJugadores Número de participantes (2 o 4).
     * @param tamanoTablero Dimensión lateral impar del tablero (>= 5).
     * @param murosPorJugador Reserva inicial de muros asignada a cada jugador (0..20).
     * @param sorteoAleatorio true para sortear el turno inicial; false para empezar por el jugador 0.
     */
    public void configurarPartida(int numJugadores, int tamanoTablero,
            int murosPorJugador, boolean sorteoAleatorio) {
        // Precondiciones e invariante
        assert numJugadores == 2 || numJugadores == 4 : "El número de jugadores debe ser 2 o 4";
        assert tamanoTablero >= 5 && tamanoTablero % 2 == 1
                : "El tamaño del tablero debe ser impar >= 5";
        assert murosPorJugador >= 0 && murosPorJugador <= Tablero.MAX_MUROS
                : "El número de muros por jugador debe estar entre 0 y " + Tablero.MAX_MUROS;
        assert verificarInvariante() : "Invariante de Partida violada al inicio de configurarPartida";

        this.tablero.reiniciar();
        this.tablero.setTamanoTablero(tamanoTablero);

        int centro = tamanoTablero / 2;
        List<Jugador> nuevaLista = new ArrayList<>();
        nuevaLista.add(new Jugador("Jugador 1", Color.ROJO,
                new Posicion(centro, 0), tamanoTablero - 1, murosPorJugador));
        nuevaLista.add(new Jugador("Jugador 2", Color.AZUL,
                new Posicion(centro, tamanoTablero - 1), 0, murosPorJugador));

        if (numJugadores == 4) {
            nuevaLista.add(new Jugador("Jugador 3", Color.VERDE,
                    new Posicion(0, centro), tamanoTablero - 1, murosPorJugador));
            nuevaLista.add(new Jugador("Jugador 4", Color.AMARILLO,
                    new Posicion(tamanoTablero - 1, centro), 0, murosPorJugador));
        }

        this.jugadores = nuevaLista;
        this.indiceJugadorActual = 0;
        this.terminada = true;
        this.sorteoAleatorio = sorteoAleatorio;

        for (Jugador j : this.jugadores) {
            this.tablero.setPosicionJugador(j.getColor(), j.getPosicionActual());
            this.tablero.setFilaObjetivo(j.getColor(), j.getFilaObjetivo());
        }

        // Postcondiciones e invariante
        assert this.jugadores.size() == numJugadores : "El número de jugadores configurados no coincide";
        assert this.sorteoAleatorio == sorteoAleatorio : "La política de sorteo no coincide";
        assert verificarInvariante() : "Invariante de Partida violada tras configurarPartida";
    }

    /**
     * Inicia la partida previamente configurada determinando el turno inicial (por sorteo o índice 0)
     * y marcando la partida como activa (terminada = false).
     */
    public void iniciarPartida() {
        // Precondiciones
        assert jugadores != null && !jugadores.isEmpty()
                : "Debe configurar los jugadores antes de iniciar la partida";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de iniciarPartida";

        if (this.sorteoAleatorio) {
            this.indiceJugadorActual = sorteadorTurno.sortear(jugadores);
        } else {
            this.indiceJugadorActual = 0;
        }
        this.terminada = false;

        // Postcondiciones y verificaciones
        assert !this.terminada : "La partida debe marcarse como no terminada al iniciar";
        assert this.indiceJugadorActual >= 0 && this.indiceJugadorActual < jugadores.size()
                : "El índice del jugador actual tras el sorteo está fuera de rango";

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Partida violada tras iniciarPartida";
    }

    /**
     * Intenta mover el peón del jugador activo hacia la posición p.
     * Recorre a los demás jugadores para comprobar ocupación directa de p y detectar
     * candidatos de salto (tanto en partidas de 2 como de 4 jugadores). Si el movimiento es válido,
     * actualiza la posición, comprueba la victoria y, en caso de continuar la partida,
     * avanza el turno al siguiente jugador.
     *
     * @param p Posición de destino solicitada.
     * @return true si el movimiento fue legal y se ejecutó; false si la partida estaba
     *         terminada, la casilla estaba ocupada o el movimiento incumplía las reglas.
     */
    public boolean realizarMovimiento(Posicion p) {
        // Precondiciones
        assert p != null : "La posición de destino no puede ser null";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de realizarMovimiento";

        if (terminada) {
            return false;
        }

        Jugador jugadorActual = getJugadorActual();
        Posicion origen = jugadorActual.getPosicionActual();
        Posicion oponenteFallback = null;
        List<Posicion> candidatosSalto = new ArrayList<>();

        // Buscar si algún oponente ocupa el destino o es adyacente a origen y a p (soporta 2 y 4 jugadores)
        for (Jugador j : jugadores) {
            if (!j.getColor().equals(jugadorActual.getColor())) {
                Posicion posOtro = j.getPosicionActual();
                if (posOtro != null && posOtro.equals(p)) {
                    return false;
                }
                if (posOtro != null && origen.esAdyacente(posOtro) && posOtro.esAdyacente(p)) {
                    candidatosSalto.add(posOtro);
                }
                if (oponenteFallback == null && posOtro != null) {
                    oponenteFallback = posOtro;
                }
            }
        }

        boolean movimientoValido = false;
        if (!candidatosSalto.isEmpty()) {
            for (Posicion candidato : candidatosSalto) {
                if (tablero.esMovimientoValido(origen, p, candidato)) {
                    movimientoValido = true;
                    break;
                }
            }
        } else {
            movimientoValido = tablero.esMovimientoValido(origen, p, oponenteFallback);
        }

        if (movimientoValido) {
            int turnoAnterior = indiceJugadorActual;
            jugadorActual.moverA(p);
            tablero.setPosicionJugador(jugadorActual.getColor(), p);

            if (jugadorActual.haLlegadoAMeta()) {
                this.terminada = true;
            } else {
                indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
                // Postcondición de cambio de turno
                assert indiceJugadorActual != turnoAnterior : "El turno debe avanzar a otro jugador";
            }

            // Invariante de clase tras éxito
            assert verificarInvariante() : "Invariante de Partida violada tras realizarMovimiento exitoso";
            return true;
        }

        return false;
    }

    /**
     * Intenta colocar el muro m en nombre del jugador activo.
     * Verifica que la partida esté en curso, que el jugador conserve muros disponibles y que
     * la colocación sea válida en el tablero; si tiene éxito, coloca el muro, consume una unidad
     * de la reserva del jugador y avanza el turno.
     *
     * @param m Muro que desea colocar el jugador actual.
     * @return true si el muro se colocó correctamente; false en caso contrario.
     */
    public boolean colocarMuro(Muro m) {
        // Precondiciones
        assert m != null : "El muro a colocar no puede ser null";
        assert verificarInvariante() : "Invariante de Partida violada al inicio de colocarMuro";

        if (terminada) {
            return false;
        }

        Jugador jugadorActual = getJugadorActual();

        if (!jugadorActual.hasMurosRestantes()) {
            return false;
        }

        if (tablero.esColocacionMuroValida(m)) {
            int murosPreviosJugador = jugadorActual.getMurosRestantes();
            int murosPreviosTablero = tablero.getMurosColocados().size();
            int turnoAnterior = indiceJugadorActual;

            tablero.colocarMuro(m);
            jugadorActual.consumirMuro();
            indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();

            // Postcondiciones
            assert jugadorActual.getMurosRestantes() == murosPreviosJugador - 1
                    : "Los muros del jugador deben decrementar en 1";
            assert tablero.getMurosColocados().size() == murosPreviosTablero + 1
                    : "Los muros del tablero deben aumentar en 1";
            assert indiceJugadorActual != turnoAnterior : "El turno debe avanzar tras colocar muro";

            // Invariante de clase tras éxito
            assert verificarInvariante() : "Invariante de Partida violada tras colocarMuro exitoso";
            return true;
        }
        return false;
    }

    /**
     * Comprueba si alguno de los jugadores ha alcanzado su fila objetivo.
     * Si existe un ganador, marca la partida como terminada y lo devuelve; en caso contrario,
     * no altera el estado terminada y retorna null.
     *
     * @return Jugador ganador si alguno alcanzó su meta, o null si nadie ha ganado todavía.
     */
    public Jugador comprobarVictoria() {
        assert verificarInvariante() : "Invariante de Partida violada al inicio de comprobarVictoria";

        // Comprobamos si alguno de los jugadores ha llegado a su meta
        for (Jugador j : jugadores) {
            if (j.haLlegadoAMeta()) {
                this.terminada = true;

                // Postcondiciones
                assert this.terminada : "La partida debe marcarse como terminada al haber un ganador";
                assert verificarInvariante() : "Invariante de Partida violada tras detectar victoria";
                return j;
            }
        }

        // Si ningún jugador ha alcanzado su meta, no se altera el estado terminada de la partida
        return null;
    }

    /**
     * Devuelve el jugador al que le corresponde actuar en el turno actual.
     *
     * @return Instancia del Jugador activo.
     */
    public Jugador getJugadorActual() {
        // Precondiciones
        assert jugadores != null && !jugadores.isEmpty() : "No hay jugadores en la partida";
        assert indiceJugadorActual >= 0 && indiceJugadorActual < jugadores.size()
                : "Índice de jugador actual inválido";
        return jugadores.get(indiceJugadorActual);
    }

    /**
     * Indica si la partida se encuentra terminada (o aún no iniciada).
     *
     * @return true si la partida está finalizada o detenida; false si está en curso.
     */
    public boolean isTerminada() {
        return terminada;
    }

    /**
     * Devuelve una vista de la lista de jugadores de la partida.
     *
     * @return Lista de Jugador.
     */
    public List<Jugador> getJugadores() {
        assert verificarInvariante() : "Invariante de Partida violada en getJugadores";
        return Collections.unmodifiableList(this.jugadores);
    }

    /**
     * Obtiene la instancia del tablero gestionado por la partida.
     *
     * @return Tablero asociado a la partida.
     */
    public Tablero getTablero() {
        assert verificarInvariante() : "Invariante de Partida violada en getTablero";
        return this.tablero;
    }

    /**
     * Devuelve una vista del mapa actual de posiciones de todos los jugadores.
     *
     * @return Mapa inmutable de Color a Posicion.
     */
    public Map<Color, Posicion> getPosicionesJugadores() {
        assert verificarInvariante() : "Invariante de Partida violada en getPosicionesJugadores";
        return Collections.unmodifiableMap(this.tablero.getPosicionesJugadores());
    }

    /**
     * Devuelve una vista de los muros colocados actualmente en el tablero.
     *
     * @return Lista de Muro.
     */
    public List<Muro> getMurosColocados() {
        assert verificarInvariante() : "Invariante de Partida violada en getMurosColocados";
        return Collections.unmodifiableList(this.tablero.getMurosColocados());
    }

    /**
     * Devuelve un mapa inmutable preservando el orden de jugadores con los muros restantes de cada uno.
     *
     * @return Mapa de Color a cantidad de muros disponibles.
     */
    public Map<Color, Integer> getMurosRestantesPorJugador() {
        assert verificarInvariante() : "Invariante de Partida violada en getMurosRestantesPorJugador";
        Map<Color, Integer> muros = new LinkedHashMap<>();
        for (Jugador j : this.jugadores) {
            muros.put(j.getColor(), j.getMurosRestantes());
        }
        return Collections.unmodifiableMap(muros);
    }
}
