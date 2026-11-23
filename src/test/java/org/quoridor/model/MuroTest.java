package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Pruebas unitarias para la clase Muro.
 * Técnicas aplicadas: Caja Negra (Particiones Equivalentes y Valores Límite/Frontera),
 * Caja Blanca (Decision & Condition Coverage en colisionaCon), Data-Driven Testing (@CsvSource)
 * y Programación por Contrato (DbC).
 */
class MuroTest {

    private Muro muro;
    private static final int LIMITE_TABLERO = 9;

    @BeforeEach
    void setUp() throws Exception {
        muro = new Muro();
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente):
     * Verifica que el constructor de paquete reservado para pruebas DbC inicialice origen
     * y orientación en null.
     */
    @Test
    @DisplayName("Constructor de paquete: inicializa origen y orientación en null para pruebas DbC")
    void testConstructor() {
        assertNull(muro.getOrigen());
        assertNull(muro.getOrientacion());
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente):
     * Verifica que el constructor parametrizado asigne correctamente la posición de origen
     * y la orientación del muro.
     */
    @Test
    @DisplayName("Constructor parametrizado: asigna correctamente origen y orientación")
    void testConstructorConParametros() {
        muro = new Muro(new Posicion(0, 1), Orientacion.VERTICAL);

        assertEquals(muro.getOrigen(), new Posicion(0, 1));
        assertEquals(muro.getOrientacion(), Orientacion.VERTICAL);
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera):
     * Comprueba la validez espacial de muros horizontales y verticales en tablero 9x9 ([0..7]),
     * en tablero mínimo de contrato (2x2) y en tablero reducido 7x7 ([0..5]).
     */
    @Test
    @DisplayName("esPosicionValida: límites válidos [0..7] en 9x9, fronteras inválidas y tableros 2x2 / 7x7")
    void testEsPosicionValida() {
        // Partición válida y valores límite/frontera en tablero 9x9 (rango válido [0, 7] en X e Y)
        Muro[] murosValidos = {
                new Muro(new Posicion(0, 0), Orientacion.VERTICAL),
                new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
                new Muro(new Posicion(1, 1), Orientacion.VERTICAL),
                new Muro(new Posicion(1, 1), Orientacion.HORIZONTAL),
                new Muro(new Posicion(6, 6), Orientacion.VERTICAL),
                new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL),
                new Muro(new Posicion(7, 7), Orientacion.VERTICAL),
                new Muro(new Posicion(7, 7), Orientacion.HORIZONTAL),
                new Muro(new Posicion(0, 7), Orientacion.VERTICAL),
                new Muro(new Posicion(0, 7), Orientacion.HORIZONTAL),
                new Muro(new Posicion(7, 0), Orientacion.VERTICAL),
                new Muro(new Posicion(7, 0), Orientacion.HORIZONTAL)
        };

        for (int i = 0; i < murosValidos.length; i++) {
            assertTrue(murosValidos[i].esPosicionValida(LIMITE_TABLERO));
        }

        // Partición inválida y valores frontera fuera de rango (< 0 y > 7 en tablero 9x9)
        Muro[] murosNoValidos = {
                new Muro(new Posicion(-1, -1), Orientacion.VERTICAL),
                new Muro(new Posicion(-1, -1), Orientacion.HORIZONTAL),
                new Muro(new Posicion(8, 8), Orientacion.VERTICAL),
                new Muro(new Posicion(8, 8), Orientacion.HORIZONTAL),
                new Muro(new Posicion(9, 9), Orientacion.VERTICAL),
                new Muro(new Posicion(9, 9), Orientacion.HORIZONTAL),
                new Muro(new Posicion(0, -1), Orientacion.VERTICAL),
                new Muro(new Posicion(0, -1), Orientacion.HORIZONTAL),
                new Muro(new Posicion(-1, 0), Orientacion.VERTICAL),
                new Muro(new Posicion(-1, 0), Orientacion.HORIZONTAL),
                new Muro(new Posicion(7, -1), Orientacion.VERTICAL),
                new Muro(new Posicion(7, -1), Orientacion.HORIZONTAL),
                new Muro(new Posicion(-1, 7), Orientacion.VERTICAL),
                new Muro(new Posicion(-1, 7), Orientacion.HORIZONTAL),
                new Muro(new Posicion(8, 7), Orientacion.VERTICAL),
                new Muro(new Posicion(8, 7), Orientacion.HORIZONTAL),
                new Muro(new Posicion(0, 8), Orientacion.VERTICAL),
                new Muro(new Posicion(0, 8), Orientacion.HORIZONTAL),
                new Muro(new Posicion(7, 8), Orientacion.HORIZONTAL),
                new Muro(new Posicion(8, 0), Orientacion.VERTICAL)
        };

        for (int i = 0; i < murosNoValidos.length; i++) {
            assertFalse(murosNoValidos[i].esPosicionValida(LIMITE_TABLERO));
        }

        // Valor límite inferior de contrato (limiteTablero = 2): únicamente origen (0, 0) cabe
        assertTrue(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL).esPosicionValida(2));
        assertTrue(new Muro(new Posicion(0, 0), Orientacion.VERTICAL).esPosicionValida(2));
        assertFalse(new Muro(new Posicion(1, 0), Orientacion.HORIZONTAL).esPosicionValida(2));
        assertFalse(new Muro(new Posicion(0, 1), Orientacion.VERTICAL).esPosicionValida(2));

        // Partición tablero 7x7: límite válido en (5, 5) y fronteras inválidas en (6, 5) / (5, 6)
        assertTrue(new Muro(new Posicion(5, 5), Orientacion.HORIZONTAL).esPosicionValida(7));
        assertTrue(new Muro(new Posicion(5, 5), Orientacion.VERTICAL).esPosicionValida(7));
        assertFalse(new Muro(new Posicion(6, 5), Orientacion.HORIZONTAL).esPosicionValida(7));
        assertFalse(new Muro(new Posicion(5, 6), Orientacion.VERTICAL).esPosicionValida(7));
    }

    /**
     * Prueba de Caja Blanca (Decision & Condition Coverage) y Caja Negra (Fronteras):
     * Cubre todas las condiciones atómicas de colisionaCon: coincidencia exacta de origen (x1==x2 && y1==y2),
     * misma orientación con solapamiento parcial o separación frontera (distancia = 2), desplazamientos
     * en ambos ejes, y orientaciones distintas con cruces válidos en T/L o sin intersección.
     */
    @Test
    @DisplayName("colisionaCon: cobertura de condiciones atómicas (cruces, solapamientos, paralelos y T/L)")
    void testColisionaCon() {
        // 1. Condición atómica (x1 == x2 && y1 == y2) == true: solapamiento total y cruce en cruz
        Muro baseVert = new Muro(new Posicion(2, 2), Orientacion.VERTICAL);
        Muro cruceCruz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        Muro solapadoTotal = new Muro(new Posicion(2, 2), Orientacion.VERTICAL);

        assertTrue(baseVert.colisionaCon(cruceCruz),
                "Debe colisionar si se cruzan en el mismo origen");
        assertTrue(baseVert.colisionaCon(solapadoTotal),
                "Debe colisionar si coinciden exactamente en origen y orientación");

        // 2. Condiciones (orientacion == otra) == true, eje común igual (y1==y2 / x1==x2) y |delta| < 2 == true
        Muro solapadoParcialVert = new Muro(new Posicion(2, 3), Orientacion.VERTICAL);
        Muro solapadoParcialVertNeg = new Muro(new Posicion(2, 1), Orientacion.VERTICAL);
        Muro baseHoriz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        Muro solapadoParcialHoriz = new Muro(new Posicion(3, 2), Orientacion.HORIZONTAL);
        Muro solapadoParcialHorizNeg = new Muro(new Posicion(1, 2), Orientacion.HORIZONTAL);

        assertTrue(baseVert.colisionaCon(solapadoParcialVert),
                "Debe colisionar por solapamiento parcial vertical positivo");
        assertTrue(baseVert.colisionaCon(solapadoParcialVertNeg),
                "Debe colisionar por solapamiento parcial vertical negativo");
        assertTrue(baseHoriz.colisionaCon(solapadoParcialHoriz),
                "Debe colisionar por solapamiento parcial horizontal positivo");
        assertTrue(baseHoriz.colisionaCon(solapadoParcialHorizNeg),
                "Debe colisionar por solapamiento parcial horizontal negativo");

        // 3. Valores frontera (distancia = 2): misma orientación y eje común, |delta| < 2 == false
        Muro contiguoVert = new Muro(new Posicion(2, 4), Orientacion.VERTICAL);
        Muro contiguoVertNeg = new Muro(new Posicion(2, 0), Orientacion.VERTICAL);
        Muro contiguoHoriz = new Muro(new Posicion(4, 2), Orientacion.HORIZONTAL);
        Muro contiguoHorizNeg = new Muro(new Posicion(0, 2), Orientacion.HORIZONTAL);

        assertFalse(baseVert.colisionaCon(contiguoVert),
                "Muros contiguos verticales positivos a distancia 2 no deben colisionar");
        assertFalse(baseVert.colisionaCon(contiguoVertNeg),
                "Muros contiguos verticales negativos a distancia 2 no deben colisionar");
        assertFalse(baseHoriz.colisionaCon(contiguoHoriz),
                "Muros contiguos horizontales positivos a distancia 2 no deben colisionar");
        assertFalse(baseHoriz.colisionaCon(contiguoHorizNeg),
                "Muros contiguos horizontales negativos a distancia 2 no deben colisionar");

        // 4. Condición (x1==x2 / y1==y2) == false: paralelos desplazados lateralmente y en ambos ejes
        Muro paraleloVert = new Muro(new Posicion(3, 2), Orientacion.VERTICAL);
        Muro paraleloHoriz = new Muro(new Posicion(2, 3), Orientacion.HORIZONTAL);
        Muro desplazadoHoriz = new Muro(new Posicion(3, 3), Orientacion.HORIZONTAL);
        Muro desplazadoVert = new Muro(new Posicion(3, 3), Orientacion.VERTICAL);

        assertFalse(baseVert.colisionaCon(paraleloVert), "Muros verticales paralelos no deben colisionar");
        assertFalse(baseHoriz.colisionaCon(paraleloHoriz), "Muros horizontales paralelos no deben colisionar");
        assertFalse(baseHoriz.colisionaCon(desplazadoHoriz),
                "Muros horizontales con distinta X e Y no deben colisionar");
        assertFalse(baseVert.colisionaCon(desplazadoVert),
                "Muros verticales con distinta X e Y no deben colisionar");

        // 5. Condición (orientacion == otra) == false con orígenes cercanos: cruces válidos en T y L
        Muro cruceTFin = new Muro(new Posicion(3, 2), Orientacion.VERTICAL);
        Muro cruceLInf = new Muro(new Posicion(2, 3), Orientacion.VERTICAL);
        Muro cruceTSup = new Muro(new Posicion(2, 1), Orientacion.VERTICAL);

        assertFalse(baseHoriz.colisionaCon(cruceTFin),
                "Cruce en T/L (2,2)H con (3,2)V no debe colisionar");
        assertFalse(baseHoriz.colisionaCon(cruceLInf),
                "Cruce en T/L (2,2)H con (2,3)V no debe colisionar");
        assertFalse(baseHoriz.colisionaCon(cruceTSup),
                "Cruce en T (2,2)H con (2,1)V no debe colisionar");

        // 6. Partición válida sin colisión: muros lejanos / disjuntos
        Muro lejano = new Muro(new Posicion(7, 7), Orientacion.VERTICAL);
        assertFalse(baseVert.colisionaCon(lejano), "Muros completamente separados no deben colisionar");

        // 7. Condición (orientacion == otra) == false con (x1 != x2 && y1 != y2)
        Muro distintaOrientacionDistintoOrigen = new Muro(new Posicion(3, 3), Orientacion.VERTICAL);
        assertFalse(baseHoriz.colisionaCon(distintaOrientacionDistintoOrigen),
                "Muros de distinta orientación y distinto origen no deben colisionar si no se interceptan");
    }

    /**
     * Prueba Data-Driven Testing (@CsvSource) y Caja Negra (Particiones y Fronteras):
     * Evalúa sistemáticamente el método bloqueaPaso para muros HORIZONTAL y VERTICAL en ambos
     * segmentos, sentidos de cruce, pasos paralelos, casillas frontera adyacentes y pasos lejanos.
     */
    @ParameterizedTest(name = "[{index}] Muro {2} en ({0},{1}) bloquea ({3},{4})->({5},{6})? esperado={7}")
    @CsvSource({
        // --- MURO HORIZONTAL EN (4,4) ---
        // 1. Partición bloqueo: primer segmento (X=4) y segundo segmento (X=5) en ambas direcciones
        "4, 4, HORIZONTAL, 4, 4, 4, 5, true",
        "4, 4, HORIZONTAL, 4, 5, 4, 4, true",
        "4, 4, HORIZONTAL, 5, 4, 5, 5, true",
        "4, 4, HORIZONTAL, 5, 5, 5, 4, true",

        // 2. Valores frontera: pasos verticales inmediatamente exteriores al muro
        "4, 4, HORIZONTAL, 3, 4, 3, 5, false",
        "4, 4, HORIZONTAL, 6, 4, 6, 5, false",

        // 3. Paso paralelo: movimiento horizontal en las casillas contiguas al muro
        "4, 4, HORIZONTAL, 4, 4, 5, 4, false",
        "4, 4, HORIZONTAL, 4, 5, 5, 5, false",

        // --- MURO VERTICAL EN (4,4) ---
        // 4. Partición bloqueo: primer segmento (Y=4) y segundo segmento (Y=5) en ambas direcciones
        "4, 4, VERTICAL,   4, 4, 5, 4, true",
        "4, 4, VERTICAL,   5, 4, 4, 4, true",
        "4, 4, VERTICAL,   4, 5, 5, 5, true",
        "4, 4, VERTICAL,   5, 5, 4, 5, true",

        // 5. Valores frontera: pasos horizontales inmediatamente exteriores al muro
        "4, 4, VERTICAL,   4, 3, 5, 3, false",
        "4, 4, VERTICAL,   4, 6, 5, 6, false",

        // 6. Paso paralelo: movimiento vertical en las casillas contiguas al muro
        "4, 4, VERTICAL,   4, 4, 4, 5, false",
        "4, 4, VERTICAL,   5, 4, 5, 5, false",

        // 7. Casos complementarios VERTICAL (condiciones atómicas): min(oX, dX) == mX fuera de rango Y,
        //    y min(oX, dX) != mX con Y dentro del rango del muro
        "4, 4, VERTICAL,   4, 2, 5, 2, false",
        "4, 4, VERTICAL,   4, 6, 5, 6, false",
        "4, 4, VERTICAL,   5, 4, 6, 4, false",
        "4, 4, VERTICAL,   5, 5, 6, 5, false",

        // --- CASOS DESCONECTADOS Y DISTANCIA > 1 ---
        "4, 4, HORIZONTAL, 0, 0, 0, 1, false",
        "4, 4, HORIZONTAL, 4, 4, 4, 6, false",
        "4, 4, VERTICAL,   4, 4, 6, 4, false"
    })
    @DisplayName("Data-Driven: Verificación de bloqueo de paso para muros")
    void testBloqueaPasoDataDriven(int mX, int mY, Orientacion orientacion,
                                   int oX, int oY, int dX, int dY, boolean esperado) {

        muro = new Muro(new Posicion(mX, mY), orientacion);
        Posicion origen = new Posicion(oX, oY);
        Posicion destino = new Posicion(dX, dY);

        assertEquals(esperado, muro.bloqueaPaso(origen, destino));
    }

    /**
     * Prueba de Programación por Contrato (DbC — Precondiciones e Invariante):
     * Verifica que el constructor y los métodos esPosicionValida, colisionaCon y bloqueaPaso
     * lancen AssertionError ante parámetros nulos, límites inválidos o muros sin inicializar.
     */
    @Test
    @DisplayName("Precondiciones DbC: rechazo de nulos, límites inválidos y muros sin inicializar")
    void testPrecondicionesMuro() {
        Posicion pos = new Posicion(2, 2);
        Muro muroValido = new Muro(pos, Orientacion.HORIZONTAL);
        Muro muroVacio = new Muro();

        // Precondiciones del constructor
        assertThrows(AssertionError.class, () -> new Muro(null, Orientacion.HORIZONTAL));
        assertThrows(AssertionError.class, () -> new Muro(pos, null));

        // Precondiciones e invariante en esPosicionValida
        assertThrows(AssertionError.class, () -> muroValido.esPosicionValida(1));
        assertThrows(AssertionError.class, () -> muroVacio.esPosicionValida(LIMITE_TABLERO));

        // Precondiciones e invariante en colisionaCon
        assertThrows(AssertionError.class, () -> muroValido.colisionaCon(null));
        assertThrows(AssertionError.class, () -> muroValido.colisionaCon(muroVacio));
        assertThrows(AssertionError.class, () -> muroVacio.colisionaCon(muroValido));

        // Precondiciones e invariante en bloqueaPaso
        assertThrows(AssertionError.class, () -> muroValido.bloqueaPaso(null, pos));
        assertThrows(AssertionError.class, () -> muroValido.bloqueaPaso(pos, null));
        assertThrows(AssertionError.class, () -> muroVacio.bloqueaPaso(pos, new Posicion(2, 3)));
    }

    /**
     * Prueba de Programación por Contrato (DbC — Invariante y Postcondiciones):
     * Comprueba que muros correctamente construidos mantienen su invariante tras cada operación
     * y que la invocación con muros incompletos dispara la aserción de invariante.
     */
    @Test
    @DisplayName("Contratos DbC: cumplimiento de invariante y postcondiciones en Muro")
    void testContratosDbCMuro() {
        Muro muroHoriz = new Muro(new Posicion(3, 3), Orientacion.HORIZONTAL);
        Muro muroVert = new Muro(new Posicion(5, 5), Orientacion.VERTICAL);
        Muro muroIncompleto = new Muro();

        // Partición válida: muro completo cumple invariante y postcondiciones en todas sus operaciones
        assertTrue(muroHoriz.esPosicionValida(LIMITE_TABLERO));
        assertFalse(muroHoriz.colisionaCon(muroVert));
        assertTrue(muroHoriz.bloqueaPaso(new Posicion(3, 3), new Posicion(3, 4)));
        assertFalse(muroHoriz.bloqueaPaso(new Posicion(3, 3), new Posicion(4, 3)));

        // Partición inválida: muro incompleto viola la invariante al operar sobre él
        assertThrows(AssertionError.class, () -> muroHoriz.colisionaCon(muroIncompleto));
        assertThrows(AssertionError.class, () -> muroIncompleto.esPosicionValida(LIMITE_TABLERO));
        assertThrows(AssertionError.class, () -> muroIncompleto.bloqueaPaso(new Posicion(0, 0), new Posicion(0, 1)));
    }
}
