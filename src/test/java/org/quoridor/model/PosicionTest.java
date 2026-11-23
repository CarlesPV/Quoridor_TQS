package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Pruebas unitarias para la clase Posicion.
 * Técnicas aplicadas: Caja Negra (Particiones Equivalentes y Valores Límite/Frontera),
 * Data-Driven Testing mediante archivo CSV y Programación por Contrato (DbC).
 */
class PosicionTest {

    Posicion p1;
    Posicion p2;

    @BeforeEach
    void setUp() throws Exception {
        p1 = new Posicion(); // (0,0)
        p2 = new Posicion(); // (0,0)
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes):
     * Verifica que el constructor por defecto inicialice en (0, 0) y el constructor parametrizado
     * almacene correctamente coordenadas nulas, positivas, negativas y mixtas.
     */
    @Test
    @DisplayName("Constructores de Posicion: por defecto (0,0) y parametrizado con coordenadas diversas")
    void testConstructoresPosicion() {
        // Partición válida: constructor por defecto en el origen (0, 0)
        Posicion porDefecto = new Posicion();
        assertEquals(0, porDefecto.getX());
        assertEquals(0, porDefecto.getY());

        // Partición válida: origen explícito (0, 0)
        Posicion ceroExplicito = new Posicion(0, 0);
        assertEquals(0, ceroExplicito.getX());
        assertEquals(0, ceroExplicito.getY());

        // Partición válida: coordenadas positivas dentro de tablero
        Posicion positivas = new Posicion(4, 7);
        assertEquals(4, positivas.getX());
        assertEquals(7, positivas.getY());

        // Partición válida: coordenadas negativas (utilizadas al evaluar desplazamientos fuera de borde)
        Posicion negativas = new Posicion(-3, -6);
        assertEquals(-3, negativas.getX());
        assertEquals(-6, negativas.getY());

        // Partición válida: combinación mixta de signos
        Posicion mixtas = new Posicion(-1, 8);
        assertEquals(-1, mixtas.getX());
        assertEquals(8, mixtas.getY());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera):
     * Comprueba la validez de coordenadas respecto a distintos tamaños de tablero (9x9, 8x8, 7x7, 1x1)
     * y límites inválidos no positivos (0 y -1).
     */
    @Test
    @DisplayName("esValida: esquinas límite, fronteras fuera de rango y tamaños de tablero variables")
    void testEsValida() {
        // Valores límite válidos (esquinas extremas en tablero 9x9: [0..8] x [0..8])
        assertTrue(p1.esValida(9));
        assertTrue(new Posicion(8, 8).esValida(9));
        assertTrue(new Posicion(0, 8).esValida(9));
        assertTrue(new Posicion(8, 0).esValida(9));

        // Valores frontera internos válidos en tablero 9x9
        assertTrue(new Posicion(1, 1).esValida(9));
        assertTrue(new Posicion(7, 7).esValida(9));

        // Valores frontera inválidos inmediatamente fuera de rango en X, en Y y en ambos ejes (9x9)
        assertFalse(new Posicion(-1, 0).esValida(9));
        assertFalse(new Posicion(0, -1).esValida(9));
        assertFalse(new Posicion(-1, -1).esValida(9));
        assertFalse(new Posicion(9, 0).esValida(9));
        assertFalse(new Posicion(0, 9).esValida(9));
        assertFalse(new Posicion(9, 9).esValida(9));

        // Partición con cambio de límite: en tablero de tamaño 8, (8, 8) pasa a ser inválida
        assertFalse(new Posicion(8, 8).esValida(8));

        // Valor límite inferior de tablero válido (limiteTablero = 1): solo (0, 0) es válida
        assertTrue(new Posicion(0, 0).esValida(1));
        assertFalse(new Posicion(1, 0).esValida(1));
        assertFalse(new Posicion(0, 1).esValida(1));

        // Partición de tablero reducido 7x7: límite válido (6, 6) y fronteras inválidas (7, 6) / (6, 7)
        assertTrue(new Posicion(6, 6).esValida(7));
        assertFalse(new Posicion(7, 6).esValida(7));
        assertFalse(new Posicion(6, 7).esValida(7));

        // Partición inválida: límites de tablero no positivos (0 y -1)
        assertFalse(new Posicion(0, 0).esValida(0));
        assertFalse(new Posicion(0, 0).esValida(-1));
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite) y Contrato (DbC):
     * Verifica el cálculo de la distancia Manhattan para distancia 0, 1, 2, diagonal máxima en 9x9,
     * simetría, signos negativos en uno o ambos ejes y rechazo de argumento nulo.
     */
    @Test
    @DisplayName("distanciaManhattan: distancias ortogonales, simetría, signos negativos y precondición nula")
    void testDistanciaManhattan() {
        // Valor límite inferior: misma posición (distancia = 0)
        assertEquals(0, p1.distanciaManhattan(p2));

        // Partición válida: adyacencia ortogonal en X e Y (distancia = 1)
        assertEquals(1, new Posicion(3, 3).distanciaManhattan(new Posicion(4, 3)));
        assertEquals(1, new Posicion(3, 3).distanciaManhattan(new Posicion(3, 4)));

        // Partición válida: desplazamiento diagonal (distancia = 2)
        assertEquals(2, new Posicion(3, 3).distanciaManhattan(new Posicion(4, 4)));

        // Valor límite superior en tablero 9x9: esquina (0,0) a esquina opuesta (8,8) = 16
        assertEquals(16, new Posicion(0, 0).distanciaManhattan(new Posicion(8, 8)));

        // Partición válida: propiedad conmutativa / simetría y coordenadas negativas en ambos ejes
        p2 = new Posicion(5, 5);
        assertEquals(10, p1.distanciaManhattan(p2));
        assertEquals(10, p2.distanciaManhattan(p1));

        p1 = new Posicion(-5, -5);
        assertEquals(20, p1.distanciaManhattan(p2));

        // Partición válida: diferencia negativa aislada en eje X y en eje Y
        assertEquals(5, new Posicion(-2, 4).distanciaManhattan(new Posicion(3, 4)));
        assertEquals(5, new Posicion(4, -2).distanciaManhattan(new Posicion(4, 3)));

        // Precondición DbC (partición inválida): posición destino nula lanza AssertionError
        assertThrows(AssertionError.class, () -> p1.distanciaManhattan(null));
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Frontera) y Contrato (DbC):
     * Comprueba las cuatro direcciones ortogonales adyacentes (abajo, derecha, izquierda, arriba),
     * posiciones no adyacentes (diagonal y misma casilla) y argumento nulo.
     */
    @Test
    @DisplayName("esAdyacente: cuatro direcciones ortogonales, casos no adyacentes y argumento nulo")
    void testEsAdyacente() {
        // Partición válida: adyacencia ortogonal hacia abajo (y + 1)
        p2 = new Posicion(0, 1);
        assertTrue(p1.esAdyacente(p2)); // caso adyacencia abajo

        // Partición válida: adyacencia ortogonal hacia la derecha (x + 1)
        p2 = new Posicion(1, 0);
        assertTrue(p1.esAdyacente(p2)); // caso adyacencia derecha

        p2 = new Posicion(1, 1);

        // Partición válida: adyacencia ortogonal hacia la izquierda (x - 1)
        p1 = new Posicion(0, 1);
        assertTrue(p2.esAdyacente(p1)); // caso adyacencia izquierda

        // Partición válida: adyacencia ortogonal hacia arriba (y - 1)
        p1 = new Posicion(1, 0);
        assertTrue(p2.esAdyacente(p1)); // caso adyacencia arriba

        // Valor frontera inválido: casilla diagonal (distancia Manhattan = 2)
        p1 = new Posicion(0, 0);
        assertFalse(p1.esAdyacente(p2)); // caso límite no adyacente

        // Valor frontera inválido: misma posición (distancia Manhattan = 0)
        p2 = new Posicion(0, 0);
        assertFalse(p1.esAdyacente(p2)); // caso límite misma posicion

        // Precondición DbC (partición inválida): argumento nulo lanza AssertionError
        assertThrows(AssertionError.class, () -> p1.esAdyacente(null));
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes):
     * Verifica el desplazamiento acumulado de coordenadas ante incrementos nulos, unitarios
     * positivos/negativos, signos mixtos y desplazamientos de gran magnitud.
     */
    @Test
    @DisplayName("desplazar: incrementos nulos, unitarios, signos mixtos y valores grandes")
    void testDesplazar() {
        // Valor frontera: desplazamiento nulo (0, 0) mantiene las coordenadas intactas
        p1.desplazar(0, 0);
        assertEquals(0, p1.getX());
        assertEquals(0, p1.getY());

        // Partición válida: desplazamientos unitarios positivos en X, Y y ambos
        p1.desplazar(1, 0); // solo x
        assertEquals(1, p1.getX());
        assertEquals(0, p1.getY());

        p1.desplazar(0, 1); // solo y
        assertEquals(1, p1.getX());
        assertEquals(1, p1.getY());

        p1.desplazar(1, 1); // ambos
        assertEquals(2, p1.getX());
        assertEquals(2, p1.getY());

        // Partición válida: desplazamientos unitarios negativos en ambos ejes y por separado
        p1.desplazar(-1, -1); // ambos negativo
        assertEquals(1, p1.getX());
        assertEquals(1, p1.getY());

        p1.desplazar(-1, 0); // solo x negativo
        assertEquals(0, p1.getX());
        assertEquals(1, p1.getY());

        p1.desplazar(0, -1); // solo y negativo
        assertEquals(0, p1.getX());
        assertEquals(0, p1.getY());

        // Partición válida: signos mixtos (+dx, -dy) y (-dx, +dy)
        p1.desplazar(3, -2);
        assertEquals(3, p1.getX());
        assertEquals(-2, p1.getY());

        p1.desplazar(-4, 5);
        assertEquals(-1, p1.getX());
        assertEquals(3, p1.getY());

        // Partición válida: desplazamientos de gran magnitud
        p1.desplazar(100, -50);
        assertEquals(99, p1.getX());
        assertEquals(-47, p1.getY());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes) y Contrato (DbC):
     * Comprueba la sobrecarga equals(Posicion) para igualdad simétrica, diferencia en X,
     * diferencia en Y, coordenadas intercambiadas y precondición con argumento nulo.
     */
    @Test
    @DisplayName("equals(Posicion): igualdad, diferencias por eje, coordenadas invertidas y nulo")
    void testEqualsPosicion() {
        // Partición válida: posiciones iguales y simetría
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));

        // Partición inválida (desigualdad): distinta coordenada X
        p2 = new Posicion(1, 0);
        assertFalse(p1.equals(p2));

        // Partición inválida (desigualdad): distinta coordenada Y
        p2 = new Posicion(0, 1);
        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));

        // Partición inválida: coordenadas intercambiadas (3,5) vs (5,3)
        Posicion a = new Posicion(3, 5);
        Posicion b = new Posicion(5, 3);
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));

        // Precondición DbC: argumento Posicion nulo lanza AssertionError
        assertThrows(AssertionError.class, () -> p1.equals((Posicion) null));
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes) y Contrato Java hashCode/equals:
     * Verifica consistencia de hashCode para objetos iguales, diferenciación ante cambios de eje
     * e integración correcta con colecciones HashSet.
     */
    @Test
    @DisplayName("hashCode: consistencia con equals e integración con HashSet")
    void testHashCode() {
        // Partición válida: objetos iguales producen idéntico hashCode
        assertEquals(p1.hashCode(), p2.hashCode());

        // Partición válida: distinta coordenada X o Y produce hashCode distinto
        Posicion p3 = new Posicion(0, 0);
        Posicion p4 = new Posicion(1, 0);
        assertNotEquals(p3.hashCode(), p4.hashCode());

        p3 = new Posicion(0, 0);
        p4 = new Posicion(0, 1);
        assertNotEquals(p3.hashCode(), p4.hashCode());

        // Partición válida: búsqueda por valor en HashSet (esencial para BFS en ValidadorCaminos)
        Set<Posicion> posiciones = new HashSet<>();
        posiciones.add(new Posicion(6, 3));
        assertTrue(posiciones.contains(new Posicion(6, 3)));

        posiciones.add(new Posicion(2, 5));
        assertFalse(posiciones.contains(new Posicion(5, 2)));
    }

    /**
     * Prueba de Caja Blanca (Cobertura de ramas de Object.equals):
     * Cubre las ramas de reflexividad (this == obj), obj == null, clase incompatible,
     * igualdad de coordenadas y desigualdad en X o Y.
     */
    @Test
    @DisplayName("equals(Object): reflexividad, nulo, tipo distinto e igualdad de coordenadas")
    void testEqualsObject() {
        // Rama 1: misma referencia (this == obj)
        Object mismaRef = p1;
        assertTrue(p1.equals(mismaRef));

        // Rama 2: referencia nula y objeto de otra clase (getClass() != obj.getClass())
        assertFalse(p1.equals((Object) null));
        assertFalse(p1.equals("no es posicion"));

        // Rama 3: comparación por valor de coordenadas (iguales, distinta X, distinta Y)
        assertTrue(p1.equals((Object) new Posicion(0, 0)));
        assertFalse(p1.equals((Object) new Posicion(1, 0)));
        assertFalse(p1.equals((Object) new Posicion(0, 1)));
    }

    /**
     * Prueba Data-Driven Testing (Automatización con @CsvFileSource):
     * Ejecuta baterías externas de pares de coordenadas desde /data/movimientos_test.csv
     * verificando el comportamiento esperado de adyacencia ortogonal.
     */
    @ParameterizedTest(name = "[{index}] ({0},{1}) adyacente a ({2},{3})? esperado={4}")
    @CsvFileSource(resources = "/data/movimientos_test.csv", numLinesToSkip = 1)
    @DisplayName("Data-Driven: Verificación de adyacencia ortogonal desde CSV")
    void testEsAdyacenteDataDriven(int oX, int oY, int dX, int dY, boolean esperado) {
        Posicion origen = new Posicion(oX, oY);
        Posicion destino = new Posicion(dX, dY);
        assertEquals(esperado, origen.esAdyacente(destino));
    }

    /**
     * Prueba de Programación por Contrato (DbC):
     * Verifica explícitamente el cumplimiento de precondiciones, postcondiciones e invariante
     * en los métodos distanciaManhattan, desplazar, esAdyacente y equals.
     */
    @Test
    @DisplayName("Contratos DbC: precondiciones, postcondiciones e invariante en Posicion")
    void testContratosDbCPosicion() {
        // Invariante tras construcción por defecto y parametrizada
        Posicion origen = new Posicion();
        Posicion destino = new Posicion(3, 4);

        // Postcondición de distanciaManhattan (>= 0) y valor exacto
        int dist = origen.distanciaManhattan(destino);
        assertTrue(dist >= 0, "La distancia Manhattan debe cumplir la postcondición >= 0");
        assertEquals(7, dist);
        assertEquals(0, origen.distanciaManhattan(origen));

        // Postcondición de desplazar: coordenadas finales == prev + delta
        int prevX = destino.getX();
        int prevY = destino.getY();
        destino.desplazar(-2, 3);
        assertEquals(prevX - 2, destino.getX());
        assertEquals(prevY + 3, destino.getY());

        // Postcondición de esAdyacente: una posición no puede ser adyacente a sí misma
        assertFalse(destino.esAdyacente(destino));

        // Precondiciones inválidas con argumento nulo deben lanzar AssertionError
        assertThrows(AssertionError.class, () -> origen.distanciaManhattan(null));
        assertThrows(AssertionError.class, () -> origen.esAdyacente(null));
        assertThrows(AssertionError.class, () -> origen.equals((Posicion) null));
    }
}
