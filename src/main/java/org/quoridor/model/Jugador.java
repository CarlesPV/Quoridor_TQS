package org.quoridor.model;

/**
 * Representa a un jugador de la partida de Quoridor.
 * Mantiene la identidad del jugador, la posición actual de su peón en el tablero,
 * su fila objetivo para alcanzar la victoria y su reserva disponible de muros.
 */
public class Jugador {

    private String nombre;
    private Color color;
    private Posicion posicionActual;
    private int filaObjetivo;
    private int murosRestantes;

    private static final int TAMANO_TABLERO = Tablero.TAMANO_TABLERO;

    /**
     * Constructor de paquete reservado exclusivamente para pruebas de invariante DbC en el modelo.
     */
    Jugador() {
    }

    /**
     * Construye un jugador con su estado inicial completamente validado por contrato.
     *
     * @param nombre Nombre identificativo del jugador.
     * @param color Color del peón asignado al jugador.
     * @param posicionActual Posición inicial del peón dentro del tablero.
     * @param filaObjetivo Índice de la fila meta que el jugador debe alcanzar.
     * @param murosRestantes Cantidad inicial de muros disponibles.
     */
    public Jugador(String nombre, Color color, Posicion posicionActual, int filaObjetivo, int murosRestantes) {
        // Precondiciones
        assert nombre != null && !nombre.trim().isEmpty() : "El nombre no puede ser nulo ni vacío";
        assert color != null : "El color no puede ser nulo";
        assert posicionActual != null : "La posición actual no puede ser nula";
        assert posicionActual.esValida(TAMANO_TABLERO) : "La posición inicial debe ser válida en el tablero";
        assert filaObjetivo >= 0 && filaObjetivo < TAMANO_TABLERO
                : "La fila objetivo debe estar dentro del tablero";
        assert murosRestantes >= 0 : "Los muros restantes no pueden ser negativos";

        this.nombre = nombre;
        this.color = color;
        this.posicionActual = posicionActual;
        this.filaObjetivo = filaObjetivo;
        this.murosRestantes = murosRestantes;

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Jugador violada tras la construcción";
    }

    /**
     * Comprueba la invariante de clase: reserva de muros no negativa y posición actual no nula.
     *
     * @return true si el estado del jugador es consistente.
     */
    private boolean verificarInvariante() {
        return this.murosRestantes >= 0 && this.posicionActual != null;
    }

    /**
     * Obtiene el nombre del jugador.
     *
     * @return Nombre del jugador.
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Obtiene el color asociado al peón del jugador.
     *
     * @return Color del jugador.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Obtiene la posición actual del peón en el tablero.
     *
     * @return Posicion actual del jugador.
     */
    public Posicion getPosicionActual() {
        return this.posicionActual;
    }

    /**
     * Obtiene el índice de la fila que el jugador debe alcanzar para ganar.
     *
     * @return Índice de la fila objetivo.
     */
    public int getFilaObjetivo() {
        return this.filaObjetivo;
    }

    /**
     * Obtiene el número de muros que aún puede colocar el jugador.
     *
     * @return Cantidad de muros disponibles en reserva.
     */
    public int getMurosRestantes() {
        return this.murosRestantes;
    }

    /**
     * Actualiza la posición del peón del jugador a una nueva casilla válida del tablero.
     *
     * @param p Nueva posición de destino.
     */
    public void moverA(Posicion p) {
        // Precondiciones
        assert p != null : "La posición de destino no puede ser nula";
        assert p.esValida(TAMANO_TABLERO) : "La posición de destino debe ser válida en el tablero";

        this.posicionActual = p;

        // Postcondición
        assert this.posicionActual.equals(p) : "La posición actual debe ser la posición de destino";

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Jugador violada tras mover";
    }

    /**
     * Descuenta en una unidad la reserva de muros disponibles tras colocar un muro en el tablero.
     */
    public void consumirMuro() {
        // Precondiciones
        assert this.murosRestantes > 0 : "No quedan muros disponibles para consumir";

        int murosPrevios = this.murosRestantes;
        this.murosRestantes--;

        // Postcondición
        assert this.murosRestantes == murosPrevios - 1
                : "Los muros restantes deben decrementar exactamente en 1";

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Jugador violada tras consumir muro";
    }

    /**
     * Indica si el jugador dispone al menos de un muro en su reserva.
     *
     * @return true si murosRestantes > 0; false en caso contrario.
     */
    public boolean hasMurosRestantes() {
        assert verificarInvariante() : "Invariante de Jugador violada en hasMurosRestantes";
        return this.murosRestantes > 0;
    }

    /**
     * Comprueba si el peón del jugador ha alcanzado su fila objetivo de victoria.
     *
     * @return true si la coordenada y actual coincide con filaObjetivo.
     */
    public boolean haLlegadoAMeta() {
        assert verificarInvariante() : "Invariante de Jugador violada en haLlegadoAMeta";
        return this.posicionActual.getY() == this.filaObjetivo;
    }

    /**
     * Restablece la posición y la reserva de muros del jugador al comenzar una nueva partida.
     *
     * @param origen Posición inicial asignada al jugador (no nula y válida en el tablero).
     * @param murosIniciales Cantidad inicial de muros para la partida.
     */
    public void reiniciarEstado(Posicion origen, int murosIniciales) {
        // Precondiciones
        assert origen != null : "La posición de origen no puede ser nula";
        assert origen.esValida(TAMANO_TABLERO) : "La posición de origen debe ser válida en el tablero";
        assert murosIniciales >= 0 : "Los muros iniciales no pueden ser negativos";

        this.posicionActual = origen;
        this.murosRestantes = murosIniciales;

        // Postcondiciones
        assert this.posicionActual.equals(origen) : "La posición actual debe ser la posición de origen";
        assert this.murosRestantes == murosIniciales
                : "Los muros restantes deben coincidir con los muros iniciales";

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Jugador violada tras reiniciar estado";
    }
}
