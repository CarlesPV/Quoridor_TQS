package org.quoridor.model;

import java.util.*;

/**
 * Representa el tablero de Quoridor y encapsula las reglas de movimiento de peones
 * (paso ortogonal simple, salto recto sobre oponente y salto diagonal ante muro o borde)
 * y de colocación válida de muros (límites, ausencia de colisiones y preservación de caminos).
 */
public class Tablero {

    private List<Muro> murosColocados;
    private Map<Color, Posicion> posicionesJugadores;
    private Map<Color, Integer> filasObjetivo;
    private int tamanoTablero;
    private IValidadorCaminos validadorCaminos;

    /** Dimensión estándar lateral del tablero de Quoridor (9x9 casillas). */
    public static final int TAMANO_TABLERO = 9;
    /** Límite máximo total de muros colocables en el tablero durante una partida. */
    public static final int MAX_MUROS = 20;

    /**
     * Construye un tablero estándar de 9x9 con un validador de caminos BFS por defecto.
     */
    public Tablero() {
        this.murosColocados = new ArrayList<>();
        this.posicionesJugadores = new HashMap<>();
        this.filasObjetivo = new HashMap<>();
        this.tamanoTablero = TAMANO_TABLERO;
        this.validadorCaminos = new ValidadorCaminos();

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Tablero violada tras la construcción por defecto";
    }

    /**
     * Construye un tablero inyectando un validador de caminos personalizado.
     *
     * @param validadorCaminos Implementación de IValidadorCaminos a utilizar.
     */
    public Tablero(IValidadorCaminos validadorCaminos) {
        // Precondición
        assert validadorCaminos != null : "El validador de caminos no puede ser nulo";

        this.murosColocados = new ArrayList<>();
        this.posicionesJugadores = new HashMap<>();
        this.filasObjetivo = new HashMap<>();
        this.tamanoTablero = TAMANO_TABLERO;
        this.validadorCaminos = validadorCaminos;

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Tablero violada tras la construcción con inyección";
    }

    /**
     * Comprueba la invariante de clase: colecciones inicializadas, tamaño mínimo válido, validador no nulo
     * y total de muros dentro del máximo permitido.
     *
     * @return true si el estado interno del tablero es consistente.
     */
    private boolean verificarInvariante() {
        return this.murosColocados != null
                && this.murosColocados.size() <= MAX_MUROS
                && this.posicionesJugadores != null
                && this.filasObjetivo != null
                && this.tamanoTablero >= 5
                && this.validadorCaminos != null;
    }

    /**
     * Obtiene la lista de muros actualmente colocados sobre el tablero.
     *
     * @return Lista de Muro colocados.
     */
    public List<Muro> getMurosColocados() {
        return this.murosColocados;
    }

    /**
     * Obtiene el mapa que asocia cada color de jugador con su posición actual.
     *
     * @return Mapa de Color a Posicion.
     */
    public Map<Color, Posicion> getPosicionesJugadores() {
        return this.posicionesJugadores;
    }

    /**
     * Obtiene la instancia del validador de caminos empleada por el tablero.
     *
     * @return Estrategia IValidadorCaminos configurada.
     */
    public IValidadorCaminos getValidadorCaminos() {
        return this.validadorCaminos;
    }

    /**
     * Obtiene la dimensión lateral del tablero cuadrado.
     *
     * @return Número de filas y columnas del tablero.
     */
    public int getTamanoTablero() {
        return this.tamanoTablero;
    }

    /**
     * Configura la dimensión lateral del tablero.
     *
     * @param tamanoTablero Nuevo tamaño lateral del tablero.
     */
    public void setTamanoTablero(int tamanoTablero) {
        // Precondición
        assert tamanoTablero >= 5 && tamanoTablero % 2 == 1
                : "El tamaño del tablero debe ser impar >= 5";
        this.tamanoTablero = tamanoTablero;
        // Invariante de clase
        assert verificarInvariante() : "Invariante de Tablero violada tras cambiar el tamaño";
    }

    /**
     * Registra la fila objetivo que debe alcanzar el jugador del color indicado.
     *
     * @param color Color del jugador.
     * @param filaObjetivo Índice de la fila meta dentro del rango [0, tamanoTablero - 1].
     */
    public void setFilaObjetivo(Color color, int filaObjetivo) {
        // Precondiciones
        assert color != null : "El color no puede ser nulo";
        assert filaObjetivo >= 0 && filaObjetivo < this.tamanoTablero
                : "La fila objetivo debe estar dentro del tablero";
        this.filasObjetivo.put(color, filaObjetivo);
    }

    /**
     * Obtiene la fila objetivo asociada a un color, o la fila estándar por defecto si no se configuró.
     *
     * @param color Color del jugador consultado.
     * @return Índice de la fila meta (0 por defecto para AZUL, tamanoTablero - 1 para el resto).
     */
    public int getFilaObjetivo(Color color) {
        // Precondición
        assert color != null : "El color no puede ser nulo";
        int filaPorDefecto = (color == Color.AZUL) ? 0 : (this.tamanoTablero - 1);
        return this.filasObjetivo.getOrDefault(color, filaPorDefecto);
    }

    /**
     * Limpia todos los muros colocados, las posiciones de los jugadores y las filas objetivo.
     */
    public void reiniciar() {
        assert verificarInvariante() : "Invariante de Tablero violada al reiniciar";
        this.murosColocados.clear();
        this.posicionesJugadores.clear();
        this.filasObjetivo.clear();
        assert verificarInvariante() : "Invariante de Tablero violada tras reiniciar";
    }

    /**
     * Consulta la posición actual registrada en el tablero para un jugador por su color.
     *
     * @param color Color del jugador.
     * @return Posicion del jugador o null si no está registrado.
     */
    public Posicion getPosicionJugador(Color color) {
        // Precondición
        assert color != null : "El color no puede ser nulo";
        return this.posicionesJugadores.get(color);
    }

    /**
     * Actualiza o registra la posición de un jugador en el mapa del tablero.
     *
     * @param color Color del jugador.
     * @param posicion Nueva posición del peón.
     */
    public void setPosicionJugador(Color color, Posicion posicion) {
        // Precondiciones
        assert color != null : "El color no puede ser nulo";
        assert posicion != null : "La posicion no puede ser nula";
        this.posicionesJugadores.put(color, posicion);
    }

    /**
     * Evalúa si un movimiento de peón desde origen hasta destino es legal según las reglas
     * oficiales de Quoridor considerando un posible oponente candidato de salto:
     *  * Paso simple: desplazamiento ortogonal de 1 casilla sin muro entre origen y destino.
     *  * Salto recto: si el oponente es adyacente sin muro intermedio y la casilla detrás
     *    de él es válida y sin muro, se permite saltar en línea recta sobre el oponente.
     *  * Salto diagonal: si el salto recto está bloqueado por un borde del tablero o un muro
     *    detrás del oponente, se permiten los desplazamientos diagonales perpendiculares libres de muro.
     *
     * @param origen Posición actual del jugador activo.
     * @param destino Casilla objetivo a la que desea desplazarse.
     * @param oponente Posición de un oponente relevante para salto.
     * @return true si el desplazamiento cumple alguna de las reglas válidas de movimiento.
     */
    public boolean esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente) {
        assert verificarInvariante() : "Invariante de Tablero violada";

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

        // Salto sobre oponente (solo si no existe un muro entre origen y oponente)
        if (oponente != null && origen.esAdyacente(oponente) && !hayMuroBloqueante(origen, oponente)) {
            int dx = oponente.getX() - origen.getX();
            int dy = oponente.getY() - origen.getY();
            Posicion saltoRecto = new Posicion(oponente.getX() + dx, oponente.getY() + dy);

            // Si el salto recto es posible, solo se permite saltar recto
            if (saltoRecto.esValida(this.tamanoTablero) && !hayMuroBloqueante(oponente, saltoRecto)) {
                return destino.equals(saltoRecto);
            }

            // Si el salto recto está bloqueado por borde o muro detrás del oponente,
            // se permiten diagonales perpendiculares al eje del salto
            if (dx != 0) {
                Posicion diagArriba = new Posicion(oponente.getX(), oponente.getY() - 1);
                Posicion diagAbajo = new Posicion(oponente.getX(), oponente.getY() + 1);
                if (destino.equals(diagArriba) || destino.equals(diagAbajo)) {
                    return destino.esValida(this.tamanoTablero)
                            && !hayMuroBloqueante(oponente, destino);
                }
            } else if (dy != 0) {
                Posicion diagIzquierda = new Posicion(oponente.getX() - 1, oponente.getY());
                Posicion diagDerecha = new Posicion(oponente.getX() + 1, oponente.getY());
                if (destino.equals(diagIzquierda) || destino.equals(diagDerecha)) {
                    return destino.esValida(this.tamanoTablero)
                            && !hayMuroBloqueante(oponente, destino);
                }
            }
        }

        return false;
    }

    /**
     * Comprueba si algún muro colocado en el tablero bloquea el paso directo entre 2 casillas.
     *
     * @param origen Casilla de salida.
     * @param destino Casilla de llegada.
     * @return true si al menos un muro impide el tránsito entre ambas posiciones.
     */
    private boolean hayMuroBloqueante(Posicion origen, Posicion destino) {
        for (Muro muro : this.murosColocados) {
            if (muro.bloqueaPaso(origen, destino)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si un muro candidato colisiona con alguno ya colocado.
     *
     * @param nuevoMuro Muro candidato a comprobar.
     * @return true si existe colisión con algún muro presente en el tablero.
     */
    public boolean colisionaConMurosExistentes(Muro nuevoMuro) {
        // Precondición e invariante
        assert nuevoMuro != null : "El nuevo muro no puede ser nulo";
        assert verificarInvariante() : "Invariante de Tablero violada en colisionaConMurosExistentes";

        for (Muro muro : this.murosColocados) {
            if (muro.colisionaCon(nuevoMuro)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evalúa si un muro puede colocarse en el tablero cumpliendo todas las reglas:
     * no ser nulo, no superar MAX_MUROS, estar dentro de los límites, no colisionar
     * con muros previos y no dejar a ningún jugador sin camino hacia su fila objetivo.
     *
     * @param muro Muro cuya validez de colocación se desea comprobar.
     * @return true si el muro es legal y seguro para todos los jugadores; false en caso contrario.
     */
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

        // Simular temporalmente el muro y verificar acceso a meta para todos los jugadores registrados
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

    /**
     * Añade un muro validado a la lista de muros colocados en el tablero.
     *
     * @param muro Muro válido a colocar.
     */
    public void colocarMuro(Muro muro) {
        // Precondiciones
        assert muro != null : "El muro a colocar no puede ser nulo";
        assert this.murosColocados.size() < MAX_MUROS : "Se ha alcanzado el límite máximo de 20 muros";
        assert esColocacionMuroValida(muro) : "La colocación del muro debe ser válida";

        int murosPrevios = this.murosColocados.size();
        this.murosColocados.add(muro);

        // Postcondiciones
        assert this.murosColocados.size() == murosPrevios + 1
                : "El número de muros colocados debe incrementarse en 1";
        assert this.murosColocados.contains(muro)
                : "La lista de muros debe contener el muro recién colocado";

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Tablero violada tras colocar muro";
    }

    /**
     * Comprueba si todos los jugadores de la lista dada mantienen al menos un camino libre hasta su meta.
     *
     * @param jugadores Lista de jugadores activos a validar.
     * @return true si todos tienen ruta disponible hasta su fila objetivo;
     *         false si alguno queda cerrado.
     */
    public boolean hayCaminoParaTodos(List<Jugador> jugadores) {
        // Precondiciones e invariante
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
