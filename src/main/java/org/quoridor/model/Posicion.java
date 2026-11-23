package org.quoridor.model;

/**
 * Representa una coordenada discreta (x, y) dentro del tablero de Quoridor.
 * El sistema de coordenadas sitúa el origen (0, 0) en la esquina superior izquierda,
 * donde x representa la columna e y representa la fila.
 */
public class Posicion {

    private int x;
    private int y;

    /**
     * Construye una posición inicializada en el origen (0, 0).
     */
    public Posicion() {
        x = 0;
        y = 0;
        // Invariante de clase
        assert verificarInvariante() : "Invariante de Posicion violada tras la construcción";
    }

    /**
     * Construye una posición con las coordenadas especificadas.
     *
     * @param posX Coordenada horizontal (columna).
     * @param posY Coordenada vertical (fila).
     */
    public Posicion(int posX, int posY) {
        x = posX;
        y = posY;
        // Invariante de clase
        assert verificarInvariante() : "Invariante de Posicion violada tras la construcción";
    }

    /**
     * Verifica la invariante de clase: las coordenadas internas mantienen consistencia con sus accesores.
     *
     * @return true si el estado interno de la posición es coherente.
     */
    private boolean verificarInvariante() {
        return this.x == getX() && this.y == getY();
    }

    /**
     * Obtiene la coordenada horizontal (columna) de la posición.
     *
     * @return Valor entero de la coordenada x.
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la coordenada vertical (fila) de la posición.
     *
     * @return Valor entero de la coordenada y.
     */
    public int getY() {
        return y;
    }

    /**
     * Comprueba si la posición se encuentra dentro de los límites válidos de un tablero cuadrado.
     *
     * @param limiteTablero Tamaño lateral del tablero.
     * @return true si 0 <= x < limiteTablero y 0 <= y < limiteTablero.
     */
    public boolean esValida(int limiteTablero) {
        assert verificarInvariante() : "Invariante de Posicion violada en esValida";
        boolean valida = (x >= 0 && x < limiteTablero) && (y >= 0 && y < limiteTablero);
        // Postcondición
        assert !valida || limiteTablero > 0 : "Una posición válida requiere un límite de tablero positivo";
        return valida;
    }

    /**
     * Calcula la distancia de Manhattan hasta otra posición.
     *
     * @param p Posición destino respecto a la cual medir la distancia.
     * @return Distancia Manhattan |x1 - x2| + |y1 - y2|, siempre mayor o igual que 0.
     */
    public int distanciaManhattan(Posicion p) {
        // Precondición e invariante
        assert p != null : "La posición destino no puede ser nula";
        assert verificarInvariante() : "Invariante de Posicion violada en distanciaManhattan";

        int resultado = Math.abs(this.x - p.x) + Math.abs(this.y - p.y);

        // Postcondición
        assert resultado >= 0 : "La distancia Manhattan no puede ser negativa";
        return resultado;
    }

    /**
     * Determina si otra posición es ortogonalmente adyacente (arriba, abajo, izquierda o derecha).
     *
     * @param p Posición a comparar.
     * @return true si la distancia Manhattan respecto a p es exactamente 1.
     */
    public boolean esAdyacente(Posicion p) {
        // Precondición e invariante
        assert p != null : "La posición a comparar no puede ser nula";
        assert verificarInvariante() : "Invariante de Posicion violada en esAdyacente";

        boolean adyacente = this.distanciaManhattan(p) == 1;

        // Postcondición
        assert !adyacente || !this.equals(p) : "Una posición no puede ser adyacente a sí misma";
        return adyacente;
    }

    /**
     * Desplaza las coordenadas de esta posición sumando los incrementos indicados.
     *
     * @param dx Desplazamiento horizontal en columnas.
     * @param dy Desplazamiento vertical en filas.
     */
    public void desplazar(int dx, int dy) {
        assert verificarInvariante() : "Invariante de Posicion violada al inicio de desplazar";
        int prevX = this.x;
        int prevY = this.y;

        this.x += dx;
        this.y += dy;

        // Postcondición e invariante
        assert this.x == prevX + dx && this.y == prevY + dy
                : "El desplazamiento no se ha aplicado correctamente";
        assert verificarInvariante() : "Invariante de Posicion violada tras desplazar";
    }

    /**
     * Compara si esta posición tiene exactamente las mismas coordenadas que otra posición dada.
     *
     * @param p Posición con la que comparar.
     * @return true si ambas coordenadas x e y coinciden.
     */
    public boolean equals(Posicion p) {
        // Precondición e invariante
        assert p != null : "La posición a comparar no puede ser nula";
        assert verificarInvariante() : "Invariante de Posicion violada en equals";
        return (this.x == p.x && this.y == p.y);
    }

    /**
     * Compara esta posición con otro objeto para verificar igualdad de coordenadas.
     * Necesario para el funcionamiento correcto de colecciones basadas en hash.
     *
     * @param obj Objeto a comparar.
     * @return true si obj es una instancia de Posicion con iguales x e y.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Posicion posicion = (Posicion) obj;
        return x == posicion.x && y == posicion.y;
    }

    /**
     * Calcula el código hash de la posición a partir de sus coordenadas x e y.
     * Necesario para almacenar y buscar posiciones en estructuras de recorrido BFS.
     *
     * @return Código hash consistente con equals(Object).
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}
