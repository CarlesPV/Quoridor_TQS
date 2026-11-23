package org.quoridor.model;

/**
 * Representa un muro físico de longitud TAMANO_MURO casillas en el tablero de Quoridor.
 * Cada muro se identifica por su esquina de origen (mX, mY) y su Orientacion:
 *  * Orientacion.HORIZONTAL: bloquea el paso vertical entre la fila mY y mY + 1
 *       en las columnas mX y mX + 1.
 *  * Orientacion.VERTICAL: bloquea el paso horizontal entre la columna mX y mX + 1
 *       en las filas mY y mY + 1.
 */
public class Muro {

    private Posicion origen;
    private Orientacion orientacion;
    private static final int TAMANO_MURO = 2;

    /**
     * Constructor de paquete reservado exclusivamente para pruebas de invariante DbC en el modelo.
     */
    Muro() {
    }

    /**
     * Construye un muro válido a partir de su coordenada de origen y su orientación.
     *
     * @param origen Esquina superior izquierda desde la que se extiende el muro.
     * @param orientacion Orientación del muro.
     */
    public Muro(Posicion origen, Orientacion orientacion) {
        // Precondiciones
        assert origen != null : "El origen no puede ser nulo";
        assert orientacion != null : "La orientación no puede ser nula";

        this.setOrigen(origen);
        this.setOrientacion(orientacion);

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Muro violada tras la construcción";
    }

    /**
     * Obtiene la posición de origen del muro.
     *
     * @return Coordenada de la esquina superior izquierda del muro.
     */
    public Posicion getOrigen() {
        return this.origen;
    }

    /**
     * Obtiene la orientación espacial del muro.
     *
     * @return Orientación HORIZONTAL o VERTICAL.
     */
    public Orientacion getOrientacion() {
        return this.orientacion;
    }

    /**
     * Asigna la posición de origen del muro.
     *
     * @param origen Nueva posición de origen.
     */
    private void setOrigen(Posicion origen) {
        this.origen = origen;
    }

    /**
     * Asigna la orientación espacial del muro.
     *
     * @param orientacion Nueva orientación del muro.
     */
    private void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    /**
     * Comprueba la invariante de clase: un muro válido debe tener origen y orientación no nulos.
     *
     * @return true si el estado del muro cumple su invariante.
     */
    private boolean verificarInvariante() {
        return this.origen != null && this.orientacion != null;
    }

    /**
     * Verifica si el muro completo cabe dentro de las ranuras internas de un tablero de tamaño dado.
     * Al tener longitud TAMANO_MURO, sus coordenadas deben cumplir
     * 0 <= x <= limiteTablero - 2 y 0 <= y <= limiteTablero - 2.
     *
     * @param limiteTablero Dimensión lateral del tablero.
     * @return true si el muro no sobresale del tablero; false en caso contrario.
     */
    public boolean esPosicionValida(int limiteTablero) {
        // Precondiciones e invariante
        assert limiteTablero >= TAMANO_MURO : "El límite del tablero debe ser al menos el tamaño del muro";
        assert verificarInvariante() : "Invariante de Muro violada en esPosicionValida";

        Posicion pos = this.getOrigen();
        boolean valida = pos.getX() >= 0 && pos.getY() >= 0
                && pos.getX() <= (limiteTablero - TAMANO_MURO)
                && pos.getY() <= (limiteTablero - TAMANO_MURO);

        // Postcondición
        assert !valida || pos.esValida(limiteTablero)
                : "El origen de un muro válido debe estar dentro del tablero";
        return valida;
    }

    /**
     * Determina si este muro colisiona con otro muro existente.
     * Dos muros colisionan si comparten el mismo punto de intersección central o
     * si comparten orientación, eje y se solapan parcialmente.
     *
     * @param otroMuro Muro existente con el que comprobar la colisión.
     * @return true si ambos muros se cruzan o se solapan; false si son compatibles.
     */
    public boolean colisionaCon(Muro otroMuro) {
        // Precondiciones e invariante
        assert otroMuro != null : "El otro muro no puede ser nulo";
        assert otroMuro.verificarInvariante() : "El otro muro debe cumplir su invariante";
        assert verificarInvariante() : "Invariante de Muro violada en colisionaCon";

        int x1 = this.getOrigen().getX();
        int y1 = this.getOrigen().getY();
        int x2 = otroMuro.getOrigen().getX();
        int y2 = otroMuro.getOrigen().getY();

        // Coincidencia en origen (mismo punto central de cruce)
        if (x1 == x2 && y1 == y2) {
            return true;
        }

        // Misma orientación y solapamiento parcial en su eje común
        if (this.orientacion == otroMuro.getOrientacion()) {
            if (this.orientacion == Orientacion.HORIZONTAL && y1 == y2) {
                return Math.abs(x1 - x2) < TAMANO_MURO;
            }
            if (this.orientacion == Orientacion.VERTICAL && x1 == x2) {
                return Math.abs(y1 - y2) < TAMANO_MURO;
            }
        }

        return false;
    }

    /**
     * Comprueba si este muro impide el paso directo entre dos casillas adyacentes.
     *
     * @param origen Casilla de partida.
     * @param destino Casilla de llegada.
     * @return true si origen y destino son ortogonalmente adyacentes y el muro
     *         se interpone entre ambas; false en caso contrario.
     */
    public boolean bloqueaPaso(Posicion origen, Posicion destino) {
        // Precondiciones e invariante
        assert origen != null : "La posición de origen no puede ser nula";
        assert destino != null : "La posición de destino no puede ser nula";
        assert verificarInvariante() : "Invariante de Muro violada en bloqueaPaso";

        int oX = origen.getX();
        int oY = origen.getY();
        int dX = destino.getX();
        int dY = destino.getY();
        int mX = this.getOrigen().getX();
        int mY = this.getOrigen().getY();
        boolean bloquea = false;

        if (this.orientacion == Orientacion.HORIZONTAL) {
            // Un muro horizontal bloquea el paso vertical entre filas mY y mY + 1 en columnas [mX, mX + 1]
            if (oX == dX && Math.abs(oY - dY) == 1) {
                if (Math.min(oY, dY) == mY && oX >= mX && oX < mX + TAMANO_MURO) {
                    bloquea = true;
                }
            }
        } else {
            // Un muro vertical bloquea el paso horizontal entre columnas mX y mX + 1 en filas [mY, mY + 1]
            if (oY == dY && Math.abs(oX - dX) == 1) {
                if (Math.min(oX, dX) == mX && oY >= mY && oY < mY + TAMANO_MURO) {
                    bloquea = true;
                }
            }
        }

        // Postcondición: solo puede bloquear el paso entre casillas adyacentes
        assert !bloquea || origen.esAdyacente(destino)
                : "Un muro solo puede bloquear el paso entre casillas adyacentes";
        return bloquea;
    }
}
