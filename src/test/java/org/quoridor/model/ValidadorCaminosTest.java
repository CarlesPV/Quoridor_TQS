package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase ValidadorCaminos (algoritmo BFS).
 * Técnicas aplicadas: Caja Blanca (Loop Testing con dos bucles anidados independientes:
 * hayCaminoDisponible y calcularDistanciaMinima), Caja Negra (Particiones Equivalentes
 * y Valores Frontera en auxiliares) y Programación por Contrato (DbC).
 */
class ValidadorCaminosTest {

    private ValidadorCaminos validador;
    private static final int TAMANO_TABLERO = 9;

    @BeforeEach
    void setUp() throws Exception {
        validador = new ValidadorCaminos();
    }

    /**
     * Construye una barrera horizontal completa que aísla la fila objetivo del resto del tablero.
     *
     * @param tamano Dimensión lateral del tablero.
     * @return Lista de muros horizontales que cierran por completo el acceso a la última fila.
     */
    private List<Muro> construirLaberintoSinMeta(int tamano) {
        List<Muro> muros = new ArrayList<>();
        int filaBloqueo = tamano - 2;
        for (int x = 0; x < tamano - 1; x += 2) {
            muros.add(new Muro(new Posicion(x, filaBloqueo), Orientacion.HORIZONTAL));
        }
        // Asegura cubrir también la última columna (tamano - 1) si tamano es impar (ej. x = 7 en 9x9)
        muros.add(new Muro(new Posicion(tamano - 2, filaBloqueo), Orientacion.HORIZONTAL));
        return muros;
    }

    /**
     * Prueba de Caja Blanca (Loop Testing — Bucle Anidado 1 en hayCaminoDisponible) y DbC:
     * Ejercita el bucle interno de vecinos (0, 1, 2, 3 y 4 vecinos válidos) y el bucle externo
     * de la cola BFS (0, 1, 2, m, n-1 y n iteraciones), además de las precondiciones del contrato.
     */
    @Test
    @DisplayName("Loop Testing anidado en hayCaminoDisponible (cola BFS y vecinos válidos)")
    void testHayCaminoDisponible() {
        // --- Bucle externo: 0 iteraciones (inicio.getY() == filaObjetivo, retorno inmediato) ---
        Posicion inicio = new Posicion(4, 0);
        List<Muro> muros = new ArrayList<>();
        assertTrue(validador.hayCaminoDisponible(inicio, 0, muros, TAMANO_TABLERO),
                "0 iteraciones: ya está en la fila objetivo.");

        // --- Bucle interno: 0 vecinos válidos / Bucle externo: 1 iteración (esquina (0,0) cerrada) ---
        List<Muro> murosEsquinaCerrada = List.of(
                new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
                new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        assertFalse(validador.hayCaminoDisponible(new Posicion(0, 0), 8, murosEsquinaCerrada, TAMANO_TABLERO),
                "0 vecinos válidos / 1 iteración: esquina (0,0) cerrada debe retornar false.");

        // --- Bucle interno: 1 vecino válido ---
        // Desde (0,0) con muro HORIZONTAL en (0,0), solo es accesible (1,0) (1 iteración interna).
        // Hacia filaObjetivo = 1, entra en el bucle while y rodea el muro: (0,0) -> (1,0) -> (2,0) -> (2,1).
        List<Muro> unVecinoMuros = List.of(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL));
        assertTrue(validador.hayCaminoDisponible(new Posicion(0, 0), 1, unVecinoMuros, TAMANO_TABLERO),
                "1 vecino válido: camino disponible desde (0,0) hacia fila 1 rodeando muro horizontal.");

        // --- Bucle interno: 2 vecinos válidos (esquina (8,8) sin muros: (8,7) y (7,8)) ---
        assertTrue(validador.hayCaminoDisponible(new Posicion(8, 8), 7, muros, TAMANO_TABLERO),
                "2 vecinos válidos: desde esquina (8,8) hacia fila 7.");

        // --- Bucle interno: 3 vecinos válidos (borde superior (4,0): (4,1), (3,0), (5,0)) ---
        assertTrue(validador.hayCaminoDisponible(new Posicion(4, 0), 1, muros, TAMANO_TABLERO),
                "3 vecinos válidos: desde borde (4,0) hacia fila 1.");

        // --- Bucle interno: 4 vecinos válidos (casilla central (4,4): 4 direcciones libres) ---
        assertTrue(validador.hayCaminoDisponible(new Posicion(4, 4), 5, muros, TAMANO_TABLERO),
                "4 vecinos válidos: desde casilla central (4,4) hacia fila 5.");

        // --- Bucle externo: 1 iteración (meta a 1 paso desde (4,7)) ---
        assertTrue(validador.hayCaminoDisponible(new Posicion(4, 7), 8, muros, TAMANO_TABLERO),
                "1 iteración: meta a 1 paso desde (4,7) hasta fila 8.");

        // --- Bucle externo: 2 iteraciones (meta a 2 pasos y bolsa cerrada de 2 casillas) ---
        assertTrue(validador.hayCaminoDisponible(new Posicion(4, 6), 8, muros, TAMANO_TABLERO),
                "2 iteraciones: meta a 2 pasos desde (4,6) hasta fila 8.");

        List<Muro> espacioDosCasillas = List.of(
                new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
                new Muro(new Posicion(1, 0), Orientacion.VERTICAL));
        assertFalse(validador.hayCaminoDisponible(new Posicion(0, 0), 8, espacioDosCasillas, TAMANO_TABLERO),
                "2 iteraciones en bolsa cerrada de 2 casillas: debe retornar false.");

        // --- Bucle externo: m iteraciones (desvío intermedio por muros) ---
        List<Muro> murosDesvio = new ArrayList<>();
        murosDesvio.add(new Muro(new Posicion(3, 4), Orientacion.HORIZONTAL));
        murosDesvio.add(new Muro(new Posicion(5, 4), Orientacion.HORIZONTAL));
        assertTrue(validador.hayCaminoDisponible(new Posicion(4, 0), 8, murosDesvio, TAMANO_TABLERO),
                "m iteraciones: debe rodear muros intermedios y alcanzar fila 8.");

        // --- Bucle externo: n-1 / n iteraciones (recorrido amplio sin acceso a fila meta) ---
        List<Muro> laberintoAmplio = construirLaberintoSinMeta(TAMANO_TABLERO);
        assertFalse(validador.hayCaminoDisponible(new Posicion(4, 0), 8, laberintoAmplio, TAMANO_TABLERO),
                "n-1 / n iteraciones: recorrido amplio sin acceso a fila meta debe retornar false.");

        // --- Precondiciones DbC inválidas ---
        assertThrows(AssertionError.class,
                () -> validador.hayCaminoDisponible(null, 8, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.hayCaminoDisponible(inicio, TAMANO_TABLERO, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.hayCaminoDisponible(inicio, -1, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.hayCaminoDisponible(inicio, 8, null, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.hayCaminoDisponible(inicio, 8, muros, 0));
        Posicion inicioFuera = new Posicion(-1, 0);
        assertThrows(AssertionError.class,
                () -> validador.hayCaminoDisponible(inicioFuera, 8, muros, TAMANO_TABLERO));
    }

    /**
     * Prueba de Caja Blanca (Loop Testing — Bucle Anidado 2 en calcularDistanciaMinima) y DbC:
     * Ejercita el bucle interno de vecinos (0, 1, 2, 3 y 4 vecinos válidos) y el bucle externo
     * de exploración BFS (0, 1, 2, m, n-1 y n iteraciones), verificando distancias mínimas y -1
     * cuando no existe camino, así como las precondiciones DbC.
     */
    @Test
    @DisplayName("Loop Testing anidado en calcularDistanciaMinima (cola BFS y vecinos válidos)")
    void testCalcularDistanciaMinima() {
        Posicion inicio = new Posicion(4, 0);
        List<Muro> muros = new ArrayList<>();

        // --- Bucle externo: 0 iteraciones (distancia == 0 al estar ya en la fila meta) ---
        assertEquals(0, validador.calcularDistanciaMinima(inicio, 0, muros, TAMANO_TABLERO),
                "0 iteraciones: misma fila inicial debe retornar distancia 0.");

        // --- Bucle interno: 0 vecinos válidos / Bucle externo: 1 iteración (-1 en celda aislada) ---
        List<Muro> murosAislado = List.of(
                new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
                new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        assertEquals(-1, validador.calcularDistanciaMinima(new Posicion(0, 0), 8, murosAislado, TAMANO_TABLERO),
                "0 vecinos válidos / 1 iteración en celda aislada debe retornar -1.");

        // --- Bucle interno: 1 vecino válido ---
        // Desde (0,0) con muro HORIZONTAL en (0,0), solo es accesible (1,0) (1 iteración interna).
        // Ruta óptima hacia filaObjetivo = 1: (0,0) -> (1,0) -> (2,0) -> (2,1), distancia = 3.
        List<Muro> unVecinoMuros = List.of(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL));
        assertEquals(3, validador.calcularDistanciaMinima(new Posicion(0, 0), 1, unVecinoMuros, TAMANO_TABLERO),
                "1 vecino válido: desde (0,0) hasta fila 1 rodeando muro en (0,0) debe retornar distancia 3.");

        // --- Bucle interno: 2 vecinos válidos (esquina (8,8) hacia fila 7 -> 1 paso) ---
        assertEquals(1, validador.calcularDistanciaMinima(new Posicion(8, 8), 7, muros, TAMANO_TABLERO),
                "2 vecinos válidos: desde esquina (8,8) hasta fila 7 debe retornar 1.");

        // --- Bucle interno: 3 vecinos válidos (borde superior (4,0) hacia fila 1 -> 1 paso) ---
        assertEquals(1, validador.calcularDistanciaMinima(new Posicion(4, 0), 1, muros, TAMANO_TABLERO),
                "3 vecinos válidos: desde borde (4,0) hasta fila 1 debe retornar 1.");

        // --- Bucle interno: 4 vecinos válidos (casilla central (4,4) hacia fila 5 -> 1 paso) ---
        assertEquals(1, validador.calcularDistanciaMinima(new Posicion(4, 4), 5, muros, TAMANO_TABLERO),
                "4 vecinos válidos: desde casilla central (4,4) hasta fila 5 debe retornar 1.");

        // --- Bucle externo: 1 iteración (distancia == 1 hacia fila meta adyacente) ---
        assertEquals(1, validador.calcularDistanciaMinima(new Posicion(4, 7), 8, muros, TAMANO_TABLERO),
                "1 iteración: casilla adyacente a fila meta debe retornar distancia 1.");

        // --- Bucle externo: 2 iteraciones (distancia == 2 y -1 en bolsa cerrada de 2 casillas) ---
        assertEquals(2, validador.calcularDistanciaMinima(new Posicion(4, 6), 8, muros, TAMANO_TABLERO),
                "2 iteraciones: distancia 2 hacia fila meta.");

        List<Muro> murosBolsaDos = List.of(
                new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
                new Muro(new Posicion(1, 0), Orientacion.VERTICAL));
        assertEquals(-1, validador.calcularDistanciaMinima(new Posicion(0, 0), 8, murosBolsaDos, TAMANO_TABLERO),
                "2 iteraciones en espacio cerrado de 2 casillas debe retornar -1.");

        // --- Bucle externo: m iteraciones (camino con rodeo de muro horizontal) ---
        List<Muro> murosRodeo = new ArrayList<>();
        murosRodeo.add(new Muro(new Posicion(3, 3), Orientacion.HORIZONTAL));
        assertEquals(9, validador.calcularDistanciaMinima(inicio, 8, murosRodeo, TAMANO_TABLERO),
                "m iteraciones: camino con rodeo debe incrementar la distancia mínima.");

        // --- Bucle externo: n-1 / n iteraciones (recorrido amplio sin acceso a fila meta) ---
        List<Muro> laberintoAmplio = construirLaberintoSinMeta(TAMANO_TABLERO);
        assertEquals(-1, validador.calcularDistanciaMinima(inicio, 8, laberintoAmplio, TAMANO_TABLERO),
                "n-1 / n iteraciones: recorrido amplio sin acceso a fila meta debe retornar -1.");

        // --- Precondiciones DbC inválidas ---
        assertThrows(AssertionError.class,
                () -> validador.calcularDistanciaMinima(null, 8, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.calcularDistanciaMinima(inicio, TAMANO_TABLERO, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.calcularDistanciaMinima(inicio, -1, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.calcularDistanciaMinima(inicio, 8, null, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.calcularDistanciaMinima(inicio, 8, muros, 0));
        Posicion inicioFuera = new Posicion(-1, 0);
        assertThrows(AssertionError.class,
                () -> validador.calcularDistanciaMinima(inicioFuera, 8, muros, TAMANO_TABLERO));
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera) y DbC:
     * Verifica obtenerVecinosValidos en casilla central (4 vecinos), esquina (0,0) con muro
     * bloqueante (1 vecino), esquina (8,8) en límite superior (2 vecinos) y precondiciones DbC.
     */
    @Test
    @DisplayName("obtenerVecinosValidos: casilla central, esquinas límite con muros y precondiciones DbC")
    void testObtenerVecinosValidos() {
        // Partición válida sin restricciones (casilla interior (2,2): 4 vecinos)
        Posicion p = new Posicion(2, 2);
        List<Muro> muros = new ArrayList<>();
        List<Posicion> vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
        Posicion[] esperados = {
                new Posicion(2, 1), // Arriba
                new Posicion(2, 3), // Abajo
                new Posicion(1, 2), // Izquierda
                new Posicion(3, 2)  // Derecha
        };
        for (int i = 0; i < vecinos.size(); i++) {
            assertEquals(esperados[i].getX(), vecinos.get(i).getX());
            assertEquals(esperados[i].getY(), vecinos.get(i).getY());
        }

        // Valor límite inferior (0,0) con muro bloqueando paso hacia abajo: solo (1,0) accesible
        p = new Posicion(0, 0);
        Muro m = new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL);
        muros.add(m);
        vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
        Posicion esperados2 = new Posicion(1, 0); // Derecha
        assertEquals(esperados2.getX(), vecinos.get(0).getX());
        assertEquals(esperados2.getY(), vecinos.get(0).getY());

        // Valor límite superior (8,8): bordes derecho e inferior filtrados, solo (8,7) y (7,8)
        p = new Posicion(8, 8);
        vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
        Posicion[] esperados3 = {
                new Posicion(8, 7), // Arriba
                new Posicion(7, 8)  // Izquierda
        };
        for (int i = 0; i < vecinos.size(); i++) {
            assertEquals(esperados3[i].getX(), vecinos.get(i).getX());
            assertEquals(esperados3[i].getY(), vecinos.get(i).getY());
        }

        // Precondiciones DbC inválidas de obtenerVecinosValidos
        Posicion actualFuera = new Posicion(-1, 0);
        Posicion actualValida = new Posicion(4, 4);
        assertThrows(AssertionError.class,
                () -> validador.obtenerVecinosValidos(null, muros, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.obtenerVecinosValidos(actualValida, null, TAMANO_TABLERO));
        assertThrows(AssertionError.class,
                () -> validador.obtenerVecinosValidos(actualValida, muros, 0));
        assertThrows(AssertionError.class,
                () -> validador.obtenerVecinosValidos(actualFuera, muros, TAMANO_TABLERO));
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes) y Contrato (DbC):
     * Comprueba estaBloqueadoPorMuro para bloqueo horizontal, paso libre, bloqueo vertical
     * y precondiciones con argumentos nulos.
     */
    @Test
    @DisplayName("estaBloqueadoPorMuro: bloqueo horizontal, paso libre, bloqueo vertical y nulos DbC")
    void testEstaBloqueadoPorMuro() {
        // Partición válida de bloqueo: paso vertical bloqueado por muro horizontal
        Posicion oP = new Posicion(4, 0);
        Posicion dP = new Posicion(4, 1);
        Muro m = new Muro(new Posicion(3, 0), Orientacion.HORIZONTAL);
        List<Muro> muros = new ArrayList<>(List.of(m));
        assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));

        // Partición válida sin bloqueo: paso horizontal libre ante muro horizontal
        oP = new Posicion(4, 1);
        dP = new Posicion(5, 1);
        assertFalse(validador.estaBloqueadoPorMuro(oP, dP, muros));

        // Partición válida de bloqueo: paso horizontal bloqueado por muro vertical añadido
        m = new Muro(new Posicion(4, 1), Orientacion.VERTICAL);
        muros.add(m);
        assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));

        Posicion oP2 = new Posicion(4, 0);
        Posicion dP2 = new Posicion(4, 1);

        // Precondiciones DbC inválidas (origen, destino o lista de muros nulos)
        assertThrows(AssertionError.class,
                () -> validador.estaBloqueadoPorMuro(null, dP2, muros));
        assertThrows(AssertionError.class,
                () -> validador.estaBloqueadoPorMuro(oP2, null, muros));
        assertThrows(AssertionError.class,
                () -> validador.estaBloqueadoPorMuro(oP2, dP2, null));
    }
}
