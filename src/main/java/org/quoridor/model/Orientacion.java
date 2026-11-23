package org.quoridor.model;

/**
 * Orientación espacial de un muro colocado sobre el tablero de Quoridor.
 */
public enum Orientacion {
    /**
     * Muro horizontal de longitud 2: bloquea el paso vertical entre la fila y y la fila y + 1
     * a lo largo de las columnas x y x + 1.
     */
    HORIZONTAL,
    /**
     * Muro vertical de longitud 2: bloquea el paso horizontal entre la columna x y la columna x + 1
     * a lo largo de las filas y e y + 1.
     */
    VERTICAL
}
