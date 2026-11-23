package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase Tablero.
 * Técnicas aplicadas: Caja Negra (Particiones Equivalentes y Valores Límite/Frontera),
 * Caja Blanca (Loop Testing en bucles simples, Decision & Condition Coverage en movimientos,
 * saltos y colocación de muros), Programación por Contrato (DbC) y Mock Object manual
 * (MockValidadorCaminos para aislar IValidadorCaminos).
 */
class TableroTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() throws Exception {
        tablero = new Tablero();
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente y Valores Constantes):
     * Verifica que el constructor por defecto inicialice colecciones vacías, el validador BFS
     * estándar y las constantes oficiales TAMANO_TABLERO (9) y MAX_MUROS (20).
     */
    @Test
    @DisplayName("Constructor por defecto: inicializa tablero 9x9 vacío con validador estándar")
    void testConstructor() {
        assertNotNull(tablero.getMurosColocados(), "La lista de muros no debe ser null");
        assertTrue(tablero.getMurosColocados().isEmpty(), "El tablero debe comenzar sin muros colocados");
        assertNotNull(tablero.getPosicionesJugadores(), "El mapa de posiciones no debe ser null");
        assertTrue(tablero.getPosicionesJugadores().isEmpty(), "El mapa de posiciones debe iniciar vacío");
        assertNotNull(tablero.getValidadorCaminos(), "El validador de caminos debe inicializarse por defecto");

        // Valores constantes del dominio
        assertEquals(9, Tablero.TAMANO_TABLERO);
        assertEquals(20, Tablero.MAX_MUROS);
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente) y Contrato (DbC):
     * Comprueba el constructor con inyección de dependencias IValidadorCaminos y el rechazo
     * mediante AssertionError cuando el validador recibido es nulo.
     */
    @Test
    @DisplayName("Constructor con inyección: usa el validador proporcionado y rechaza null por DbC")
    void testConstructorConParametros() {
        // Partición válida: validador no nulo inyectado
        IValidadorCaminos validadorReal = new ValidadorCaminos();
        Tablero tableroInyectado = new Tablero(validadorReal);
        assertNotNull(tableroInyectado.getMurosColocados());
        assertTrue(tableroInyectado.getMurosColocados().isEmpty());
        assertEquals(validadorReal, tableroInyectado.getValidadorCaminos(), "Debe usar el validador inyectado");

        // Precondición DbC (partición inválida): validador nulo lanza AssertionError
        assertThrows(AssertionError.class, () -> new Tablero(null),
                "Debe lanzar AssertionError si el validador es nulo");
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes):
     * Verifica la lectura de un color sin posición asignada (null) y el registro/consulta
     * posterior de la posición de un jugador.
     */
    @Test
    @DisplayName("setPosicionJugador y getPosicionJugador: ausencia inicial y registro de posición")
    void testSetYGetPosicionJugador() {
        // Partición sin registrar: debe devolver null
        assertNull(tablero.getPosicionJugador(Color.ROJO),
                "Un color sin posición asignada debe retornar null");

        // Partición válida: registra y recupera la posición del jugador rojo
        Posicion posRojo = new Posicion(4, 0);
        tablero.setPosicionJugador(Color.ROJO, posRojo);
        assertEquals(posRojo, tablero.getPosicionJugador(Color.ROJO),
                "Debe retornar la posición asignada al jugador");
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite) y Caja Blanca
     * (Decision & Condition Coverage):
     * Evalúa todas las decisiones principales de esMovimientoValido: entradas nulas,
     * coordenadas fuera de límites, origen == destino, destino == oponente, paso ortogonal
     * simple libre/bloqueado, salto recto, salto diagonal por muro detrás del oponente,
     * bloqueo lateral en diagonal y oponente no adyacente.
     */
    @Test
    @DisplayName("esMovimientoValido: pasos simples, saltos rectos/diagonales, bordes, muros y nulos")
    void testEsMovimientoValido() {
        Posicion origen = new Posicion(4, 4);
        Posicion destinoValido = new Posicion(4, 5);
        Posicion destinoLejano = new Posicion(6, 6);
        Posicion mismoDestino = new Posicion(4, 4);

        // Partición válida / Decisión (origen.esAdyacente(destino) == true && !hayMuroBloqueante): paso simple
        assertTrue(tablero.esMovimientoValido(origen, destinoValido, null),
                "Debería ser válido un movimiento a una casilla adyacente sin obstáculos");

        // Partición inválida / Decisión (origen.esAdyacente == false, sin oponente): destino lejano
        assertFalse(tablero.esMovimientoValido(origen, destinoLejano, null),
                "No debería ser válido moverse a una casilla lejana sin salto");

        // Partición inválida / Condición (origen.equals(destino) == true)
        assertFalse(tablero.esMovimientoValido(origen, mismoDestino, null),
                "No debería ser válido moverse a la misma casilla de origen");

        // Valores frontera inválidos / Condición (!origen.esValida || !destino.esValida)
        Posicion origenBorde = new Posicion(0, 0);
        Posicion destinoFueraIzquierda = new Posicion(-1, 0);
        Posicion destinoFueraArriba = new Posicion(0, -1);
        assertFalse(tablero.esMovimientoValido(origenBorde, destinoFueraIzquierda, null),
                "No debería ser válido un movimiento fuera del límite izquierdo");
        assertFalse(tablero.esMovimientoValido(origenBorde, destinoFueraArriba, null),
                "No debería ser válido un movimiento fuera del límite superior");
        assertFalse(tablero.esMovimientoValido(new Posicion(-1, 4), new Posicion(0, 4), null),
                "No debería ser válido si el origen está fuera del tablero");

        // Partición inválida / Condición (oponente != null && destino.equals(oponente))
        Posicion oponente = new Posicion(4, 5);
        assertFalse(tablero.esMovimientoValido(origen, oponente, oponente),
                "No debería ser válido moverse directamente a la casilla ocupada por el oponente");

        // Partición válida / Decisión salto recto (saltoRecto.esValida && !hayMuroBloqueante)
        Posicion destinoSaltoRecto = new Posicion(4, 6);
        assertTrue(tablero.esMovimientoValido(origen, destinoSaltoRecto, oponente),
                "Debería permitir saltar en línea recta sobre el oponente si no hay muros");

        // Condición salto recto libre: si el salto recto es válido, destino diagonal debe dar false
        Posicion destinoDiagonal = new Posicion(5, 5);
        assertFalse(tablero.esMovimientoValido(origen, destinoDiagonal, oponente),
                "No debería permitir salto diagonal si no hay muro detrás del oponente");

        // Decisión salto diagonal habilitado: muro horizontal detrás del oponente (entre (4,5) y (4,6))
        tablero.colocarMuro(new Muro(new Posicion(4, 5), Orientacion.HORIZONTAL));
        assertTrue(tablero.esMovimientoValido(origen, destinoDiagonal, oponente),
                "Debería permitir salto diagonal si hay muro detrás del oponente");

        // Condición lateral en diagonal (!hayMuroBloqueante(oponente, destino)): muro vertical bloquea derecha
        tablero.colocarMuro(new Muro(new Posicion(4, 4), Orientacion.VERTICAL));
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(5, 5), oponente));
        assertTrue(tablero.esMovimientoValido(origen, new Posicion(3, 5), oponente));

        // Condición paso simple bloqueado (!hayMuroBloqueante(origen, destino) == false)
        tablero.getMurosColocados().clear();
        tablero.getMurosColocados().add(new Muro(new Posicion(4, 4), Orientacion.HORIZONTAL));
        assertFalse(tablero.esMovimientoValido(origen, destinoValido, null),
                "No debería permitir movimiento si un muro horizontal bloquea el paso");

        // Partición válida: paso adyacente lateral no afectado por el muro horizontal
        Posicion destinoLateral = new Posicion(5, 4);
        assertTrue(tablero.esMovimientoValido(origen, destinoLateral, null),
                "Debería permitir movimiento horizontal que no esté bloqueado");

        // Condición salto recto bloqueado por muro detrás del oponente
        tablero.getMurosColocados().clear();
        tablero.getMurosColocados().add(new Muro(new Posicion(4, 5), Orientacion.HORIZONTAL));
        assertFalse(tablero.esMovimientoValido(origen, destinoSaltoRecto, oponente),
                "No debería permitir salto sobre oponente si hay un muro detrás");

        // Decision & Condition Coverage: origen == null (true/false) y destino == null (true/false)
        assertFalse(tablero.esMovimientoValido(null, destinoValido, null),
                "No debe permitir movimiento si el origen es nulo");
        assertFalse(tablero.esMovimientoValido(origen, null, null),
                "No debe permitir movimiento si el destino es nulo");

        // Decision & Condition Coverage: oponente != null pero origen.esAdyacente(oponente) == false
        tablero.getMurosColocados().clear();
        assertFalse(tablero.esMovimientoValido(origen, destinoSaltoRecto, new Posicion(0, 0)),
                "No debe permitir salto si el oponente no es adyacente al origen");
    }

    /**
     * Prueba de Caja Blanca (Loop Testing — Bucle Simple 1 en colisionaConMurosExistentes):
     * Ejercita sistemáticamente el recorrido de murosColocados para 0, 1, 2, m (5), n-1 (19)
     * y n (20, límite MAX_MUROS) iteraciones, cubriendo tanto recorridos completos como salidas tempranas.
     */
    @Test
    @DisplayName("Loop Testing simple en colisionaConMurosExistentes (0, 1, 2, m, n-1, n iteraciones)")
    void testColisionaConMurosExistentes() {
        // 1. Caso 0 iteraciones (tablero vacío): el bucle no se ejecuta
        Muro muroTest = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        assertFalse(tablero.colisionaConMurosExistentes(muroTest),
                "0 iteraciones: Un tablero sin muros no debe detectar colisiones");

        // 2. Caso 1 iteración:
        // 1 iteración completa sin colisión
        tablero.getMurosColocados().add(new Muro(new Posicion(5, 5), Orientacion.VERTICAL));
        assertFalse(tablero.colisionaConMurosExistentes(muroTest),
                "1 iteración completa: No debe colisionar con un muro distante");

        // 1 iteración con salida temprana por colisión
        tablero.getMurosColocados().clear();
        tablero.getMurosColocados().add(new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL));
        assertTrue(tablero.colisionaConMurosExistentes(muroTest),
                "1 iteración con salida: Debe detectar colisión con el único muro");

        // 3. Caso 2 iteraciones:
        // 2 iteraciones completas sin colisión
        tablero.getMurosColocados().clear();
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(5, 5), Orientacion.VERTICAL));
        assertFalse(tablero.colisionaConMurosExistentes(muroTest),
                "2 iteraciones completas: No debe colisionar tras evaluar 2 muros");

        // 2 iteraciones con salida temprana en el segundo muro
        tablero.getMurosColocados().clear();
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(2, 2), Orientacion.VERTICAL));
        Muro muroCruz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        assertTrue(tablero.colisionaConMurosExistentes(muroCruz),
                "2 iteraciones con salida: Debe iterar el 1º y colisionar en el 2º");

        // 4. Caso m iteraciones típicas (m = 5 muros):
        // 5 iteraciones completas sin colisión
        tablero.getMurosColocados().clear();
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(3, 0), Orientacion.HORIZONTAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 3), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 5), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(6, 0), Orientacion.HORIZONTAL));
        Muro muroLibre = new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL);
        assertFalse(tablero.colisionaConMurosExistentes(muroLibre),
                "m iteraciones completas: No colisiona tras recorrer m muros");

        // m iteraciones con salida temprana en el m-ésimo muro (5º muro)
        Muro muroEnQuinto = new Muro(new Posicion(6, 0), Orientacion.VERTICAL);
        assertTrue(tablero.colisionaConMurosExistentes(muroEnQuinto),
                "m iteraciones con salida: Debe recorrer hasta el 5º muro y colisionar");

        // 5. Configuración de 19/20 muros no colisionantes para n-1 y n iteraciones:
        tablero.getMurosColocados().clear();
        for (int x = 0; x < 8; x++) {
            tablero.getMurosColocados().add(new Muro(new Posicion(x, 0), Orientacion.VERTICAL));
        }
        for (int x = 0; x < 8; x++) {
            tablero.getMurosColocados().add(new Muro(new Posicion(x, 2), Orientacion.VERTICAL));
        }
        for (int x = 0; x < 3; x++) {
            tablero.getMurosColocados().add(new Muro(new Posicion(x, 4), Orientacion.VERTICAL));
        }
        // Caso n-1 iteraciones (19 muros): 19 iteraciones completas sin colisión
        Muro muroEnPosicion20 = new Muro(new Posicion(3, 4), Orientacion.VERTICAL);
        assertFalse(tablero.colisionaConMurosExistentes(muroEnPosicion20),
                "n-1 iteraciones completas: 19 muros recorridos sin colisión");

        // Caso n iteraciones máximas (20 muros):
        tablero.getMurosColocados().add(muroEnPosicion20);

        // 20 iteraciones completas sin colisión
        Muro muroDisjunto = new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL);
        assertFalse(tablero.colisionaConMurosExistentes(muroDisjunto),
                "n iteraciones completas: 20 muros evaluados sin colisión");

        // 20 iteraciones con colisión en el último muro (el número 20)
        Muro muroCruzUltimo = new Muro(new Posicion(3, 4), Orientacion.HORIZONTAL);
        assertTrue(tablero.colisionaConMurosExistentes(muroCruzUltimo),
                "n iteraciones con salida: Debe recorrer los 20 muros y colisionar en el último");
    }

    /**
     * Prueba de Caja Blanca (Decision & Condition Coverage) e Integración con Mock Manual:
     * Verifica todas las guardas de esColocacionMuroValida (límites, colisiones, máximo de 20 muros,
     * bordes frontera válidos) y aísla la validación de caminos mediante MockValidadorCaminos (Mock 1),
     * además de validar un cierre real con dos jugadores y ValidadorCaminos real.
     */
    @Test
    @DisplayName("esColocacionMuroValida: cobertura de decisiones, límite 20 muros y MockValidadorCaminos")
    void testEsColocacionMuroValida() {
        Muro muroValido = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        Muro muroFueraLimites = new Muro(new Posicion(8, 8), Orientacion.HORIZONTAL);

        // Partición válida / todas las guardas en false: muro dentro de límites en tablero vacío
        assertTrue(tablero.esColocacionMuroValida(muroValido),
                "Debería ser válida la colocación de un muro dentro de los límites en tablero vacío");

        // Decisión (!muro.esPosicionValida(tamanoTablero) == true): muro fuera de límites
        assertFalse(tablero.esColocacionMuroValida(muroFueraLimites),
                "No debería ser válida la colocación de un muro fuera de los límites");

        // Decisión (colisionaConMurosExistentes(muro) == true): muro colisiona con existente
        tablero.getMurosColocados().add(muroValido);
        assertFalse(tablero.esColocacionMuroValida(muroValido),
                "No debería ser válida la colocación de un muro que colisiona con otro existente");

        // Valores límite/frontera válidos en las 4 esquinas de colocación (0,0), (7,7), (0,7), (7,0)
        tablero.getMurosColocados().clear();
        assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(0, 0), Orientacion.VERTICAL)),
                "Límite superior izquierdo vertical debe ser válido");
        assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(7, 7), Orientacion.VERTICAL)),
                "Límite inferior derecho vertical debe ser válido");
        assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(0, 7), Orientacion.HORIZONTAL)),
                "Frontera esquina inferior izquierda horizontal debe ser válida");
        assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(7, 0), Orientacion.HORIZONTAL)),
                "Frontera esquina superior derecha horizontal debe ser válida");

        // Decisión (murosColocados.size() >= MAX_MUROS == true): límite máximo de 20 muros
        for (int x = 0; x < 8; x++) {
            tablero.getMurosColocados().add(new Muro(new Posicion(x, 0), Orientacion.VERTICAL));
            tablero.getMurosColocados().add(new Muro(new Posicion(x, 2), Orientacion.VERTICAL));
        }
        for (int x = 0; x < 4; x++) {
            tablero.getMurosColocados().add(new Muro(new Posicion(x, 4), Orientacion.VERTICAL));
        }
        assertEquals(Tablero.MAX_MUROS, tablero.getMurosColocados().size());
        assertFalse(tablero.esColocacionMuroValida(new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL)),
                "No debe permitir colocar muros si ya se ha alcanzado el límite máximo de 20");

        // Mock 1 (Manual - Modelo): Simula IValidadorCaminos registrando invocaciones y argumentos
        MockValidadorCaminos mockValidador = new MockValidadorCaminos();
        Tablero tableroConMock = new Tablero(mockValidador);
        tableroConMock.getPosicionesJugadores().put(Color.ROJO, new Posicion(4, 0));
        tableroConMock.getPosicionesJugadores().put(Color.AZUL, new Posicion(4, 8));

        // Decisión (!validadorCaminos.hayCaminoDisponible == false): ambos jugadores mantienen camino
        mockValidador.setCaminoDisponible(true);
        assertTrue(tableroConMock.esColocacionMuroValida(muroValido),
                "Debe permitir colocar muro si el validador confirma camino disponible");
        assertEquals(2, mockValidador.getVecesInvocado(),
                "Debe invocar al validador exactamente una vez por cada jugador");
        assertNotNull(mockValidador.getUltimosMurosRecibidos());
        assertEquals(1, mockValidador.getUltimosMurosRecibidos().size(),
                "La lista simulada debe contener únicamente el muro candidato");
        assertTrue(mockValidador.getUltimosMurosRecibidos().contains(muroValido),
                "murosSimulados debe incluir el muro candidato");
        assertTrue(mockValidador.getUltimaFilaObjetivo() == 0 || mockValidador.getUltimaFilaObjetivo() == 8,
                "La fila objetivo pasada debe corresponder a alguno de los jugadores");

        // Decisión (!validadorCaminos.hayCaminoDisponible == true): corte temprano al primer jugador cerrado
        mockValidador.reiniciarRegistro();
        mockValidador.setCaminoDisponible(false);
        assertFalse(tableroConMock.esColocacionMuroValida(muroValido),
                "No debe permitir colocar muro si el validador indica que bloquea el camino");
        assertEquals(1, mockValidador.getVecesInvocado(),
                "Debe detener la comprobación en cuanto un jugador no tiene camino");

        // Verificación de filaObjetivo y murosSimulados sin mutar murosColocados reales
        mockValidador.reiniciarRegistro();
        mockValidador.setCaminoDisponible(true);
        Tablero tableroConMuroPrevio = new Tablero(mockValidador);
        Muro muroExistente = new Muro(new Posicion(0, 0), Orientacion.VERTICAL);
        tableroConMuroPrevio.colocarMuro(muroExistente);
        tableroConMuroPrevio.getPosicionesJugadores().put(Color.ROJO, new Posicion(4, 0));
        tableroConMuroPrevio.setFilaObjetivo(Color.ROJO, 8);

        assertTrue(tableroConMuroPrevio.esColocacionMuroValida(muroValido));
        assertEquals(1, mockValidador.getVecesInvocado());
        assertEquals(8, mockValidador.getUltimaFilaObjetivo());
        assertEquals(List.of(muroExistente, muroValido), mockValidador.getUltimosMurosRecibidos());
        assertEquals(1, tableroConMuroPrevio.getMurosColocados().size(),
                "La validación simulada no debe modificar los muros colocados reales");

        // Caso real con 2 jugadores y ValidadorCaminos real: J1 tiene camino pero J2 queda encerrado en (0,8)
        Tablero tableroDosJugadores = new Tablero(new ValidadorCaminos());
        tableroDosJugadores.getPosicionesJugadores().put(Color.ROJO, new Posicion(4, 0));
        tableroDosJugadores.setFilaObjetivo(Color.ROJO, 8);
        tableroDosJugadores.getPosicionesJugadores().put(Color.AZUL, new Posicion(0, 8));
        tableroDosJugadores.setFilaObjetivo(Color.AZUL, 0);

        tableroDosJugadores.colocarMuro(new Muro(new Posicion(0, 7), Orientacion.VERTICAL));
        Muro muroCierreAzul = new Muro(new Posicion(0, 7), Orientacion.HORIZONTAL);

        assertFalse(tableroDosJugadores.esColocacionMuroValida(muroCierreAzul),
                "Debe rechazar el muro si el primer jugador tiene camino pero el segundo queda encerrado");
    }

    // Mock 1 (Manual - Modelo): Simula IValidadorCaminos registrando invocaciones y argumentos
    private static class MockValidadorCaminos implements IValidadorCaminos {
        private boolean caminoDisponible = true;
        private int vecesInvocado = 0;
        private int ultimaFilaObjetivo = -1;
        private List<Muro> ultimosMurosRecibidos;

        public void setCaminoDisponible(boolean caminoDisponible) {
            this.caminoDisponible = caminoDisponible;
        }

        public void reiniciarRegistro() {
            this.vecesInvocado = 0;
            this.ultimaFilaObjetivo = -1;
            this.ultimosMurosRecibidos = null;
        }

        public int getVecesInvocado() {
            return this.vecesInvocado;
        }

        public int getUltimaFilaObjetivo() {
            return this.ultimaFilaObjetivo;
        }

        public List<Muro> getUltimosMurosRecibidos() {
            return this.ultimosMurosRecibidos;
        }

        @Override
        public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo,
                List<Muro> muros, int tamanoTablero) {
            this.vecesInvocado++;
            this.ultimaFilaObjetivo = filaObjetivo;
            this.ultimosMurosRecibidos = (muros != null) ? new ArrayList<>(muros) : null;
            return this.caminoDisponible;
        }

        @Override
        public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo,
                List<Muro> muros, int tamanoTablero) {
            this.vecesInvocado++;
            this.ultimaFilaObjetivo = filaObjetivo;
            this.ultimosMurosRecibidos = (muros != null) ? new ArrayList<>(muros) : null;
            return this.caminoDisponible ? 5 : -1;
        }
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera):
     * Verifica colocarMuro con colocación secuencial, en las cuatro esquinas válidas del tablero
     * y acumulando muros hasta el límite superior exacto de MAX_MUROS (20).
     */
    @Test
    @DisplayName("colocarMuro: inserción secuencial, cuatro esquinas límite y máximo de 20 muros")
    void testColocarMuro() {
        // Partición válida: colocación secuencial de muros independientes
        Muro muro1 = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        Muro muro2 = new Muro(new Posicion(5, 5), Orientacion.VERTICAL);

        tablero.colocarMuro(muro1);
        assertEquals(1, tablero.getMurosColocados().size(),
                "El número de muros debe ser 1 tras colocar el primer muro");
        assertTrue(tablero.getMurosColocados().contains(muro1),
                "La lista de muros debe contener el primer muro colocado");

        tablero.colocarMuro(muro2);
        assertEquals(2, tablero.getMurosColocados().size(),
                "El número de muros debe ser 2 tras colocar el segundo muro");
        assertTrue(tablero.getMurosColocados().contains(muro2),
                "La lista de muros debe contener el segundo muro colocado");

        // Valores límite y frontera válidos: cuatro esquinas extremas de muros [0..7]
        tablero.getMurosColocados().clear();
        Muro muroSupIzq = new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL);
        Muro muroInfDer = new Muro(new Posicion(7, 7), Orientacion.VERTICAL);
        Muro muroSupDer = new Muro(new Posicion(7, 0), Orientacion.HORIZONTAL);
        Muro muroInfIzq = new Muro(new Posicion(0, 7), Orientacion.VERTICAL);

        tablero.colocarMuro(muroSupIzq);
        tablero.colocarMuro(muroInfDer);
        tablero.colocarMuro(muroSupDer);
        tablero.colocarMuro(muroInfIzq);
        assertEquals(4, tablero.getMurosColocados().size());
        assertTrue(tablero.getMurosColocados().contains(muroSupIzq));
        assertTrue(tablero.getMurosColocados().contains(muroInfDer));
        assertTrue(tablero.getMurosColocados().contains(muroSupDer));
        assertTrue(tablero.getMurosColocados().contains(muroInfIzq));

        // Valor límite superior válido: llenar el tablero hasta exactamente MAX_MUROS (20)
        tablero.getMurosColocados().clear();
        for (int x = 0; x < 8; x++) {
            tablero.colocarMuro(new Muro(new Posicion(x, 0), Orientacion.VERTICAL));
            tablero.colocarMuro(new Muro(new Posicion(x, 2), Orientacion.VERTICAL));
        }
        for (int x = 0; x < 4; x++) {
            tablero.colocarMuro(new Muro(new Posicion(x, 4), Orientacion.VERTICAL));
        }
        assertEquals(Tablero.MAX_MUROS, tablero.getMurosColocados().size(),
                "El tablero debe poder contener exactamente el máximo de 20 muros colocados");
    }

    /**
     * Prueba de Caja Blanca (Loop Testing — Bucle Simple 2 en hayCaminoParaTodos):
     * Ejercita el bucle sobre la lista de jugadores para 0, 1, 2, n-1 (3) y n (4) iteraciones,
     * probando tanto recorridos completos exitosos como salidas tempranas por jugador encerrado.
     */
    @Test
    @DisplayName("Loop Testing simple en hayCaminoParaTodos (0, 1, 2, n-1 y n jugadores)")
    void testHayCaminoParaTodos() {
        List<Jugador> jugadores = new ArrayList<>();

        // 1. Caso 0 iteraciones: lista vacía de jugadores -> true
        assertTrue(tablero.hayCaminoParaTodos(jugadores),
                "0 iteraciones: Una lista vacía de jugadores debe retornar true");

        // 2. Caso 1 iteración:
        // 1 iteración completa con camino disponible
        Jugador j1 = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 0), 8, 5);
        jugadores.add(j1);
        assertTrue(tablero.hayCaminoParaTodos(jugadores),
                "1 iteración completa: Debe haber camino disponible para un jugador en tablero despejado");

        // 1 iteración con salida temprana (jugador único encerrado en esquina (0,0))
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 1), Orientacion.HORIZONTAL));
        Jugador jEncerrado = new Jugador("Encerrado", Color.AMARILLO, new Posicion(0, 0), 8, 5);
        assertFalse(tablero.hayCaminoParaTodos(List.of(jEncerrado)),
                "1 iteración con salida: Debe retornar false si el único jugador está encerrado");

        // 3. Caso 2 iteraciones:
        // 2 iteraciones completas con camino disponible para ambos
        tablero.getMurosColocados().clear();
        Jugador j2 = new Jugador("Jugador 2", Color.AZUL, new Posicion(4, 8), 0, 5);
        jugadores.add(j2);
        assertTrue(tablero.hayCaminoParaTodos(jugadores),
                "2 iteraciones completas: Debe haber camino disponible para ambos jugadores");

        // 2 iteraciones con salida temprana en el segundo jugador
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 1), Orientacion.HORIZONTAL));
        assertFalse(tablero.hayCaminoParaTodos(List.of(j1, jEncerrado)),
                "2 iteraciones con salida: El 1º tiene camino pero el 2º está encerrado");

        // 4. Caso n-1 iteraciones (3 jugadores): 3 iteraciones completas sin muros bloqueantes
        tablero.getMurosColocados().clear();
        Jugador j3 = new Jugador("Jugador 3", Color.VERDE, new Posicion(8, 4), 0, 5);
        jugadores.add(j3);
        assertTrue(tablero.hayCaminoParaTodos(jugadores),
                "n-1 iteraciones completas: Los 3 jugadores tienen camino disponible");

        // 5. Caso n iteraciones máximas (4 jugadores):
        // 4 iteraciones completas con camino disponible para todos
        Jugador j4 = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(0, 0), 8, 5);
        jugadores.add(j4);
        assertTrue(tablero.hayCaminoParaTodos(jugadores),
                "n iteraciones completas: Los 4 jugadores tienen camino disponible");

        // 4 iteraciones con salida en el último jugador (el 4º encerrado en (0,0))
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
        tablero.getMurosColocados().add(new Muro(new Posicion(0, 1), Orientacion.HORIZONTAL));
        assertFalse(tablero.hayCaminoParaTodos(jugadores),
                "n iteraciones con salida: Debe recorrer los 4 jugadores y fallar en el último");
    }

    /**
     * Prueba de Caja Blanca y Programación por Contrato (DbC — Precondiciones e Invariante):
     * Verifica la rama de muro nulo en esColocacionMuroValida, las precondiciones nulas e inválidas
     * de colisionaConMurosExistentes, colocarMuro y hayCaminoParaTodos, y la detección de invariante
     * violada al superar los 20 muros.
     */
    @Test
    @DisplayName("Ramas nulas, precondiciones DbC e invariante de Tablero")
    void testRamasNulasPrecondicionesEInvariante() {
        Muro muroValido = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);

        // Rama de muro nulo en esColocacionMuroValida
        assertFalse(tablero.esColocacionMuroValida(null),
                "Un muro nulo nunca debe ser una colocación válida");

        // Precondiciones DbC con argumentos nulos
        assertThrows(AssertionError.class, () -> tablero.colisionaConMurosExistentes(null),
                "colisionaConMurosExistentes debe rechazar muro nulo");
        assertThrows(AssertionError.class, () -> tablero.colocarMuro(null),
                "colocarMuro debe rechazar muro nulo");
        assertThrows(AssertionError.class, () -> tablero.hayCaminoParaTodos(null),
                "hayCaminoParaTodos debe rechazar lista nula");

        List<Jugador> listaConNulo = new ArrayList<>();
        listaConNulo.add(null);
        assertThrows(AssertionError.class, () -> tablero.hayCaminoParaTodos(listaConNulo),
                "hayCaminoParaTodos debe rechazar jugadores nulos en la lista");

        // Precondición DbC de colocarMuro con muro inválido (< 20 muros)
        Muro muroFueraLimites = new Muro(new Posicion(8, 8), Orientacion.HORIZONTAL);
        assertThrows(AssertionError.class, () -> tablero.colocarMuro(muroFueraLimites),
                "colocarMuro debe rechazar un muro inválido");

        // Precondición DbC de colocarMuro al alcanzar MAX_MUROS (20)
        for (int x = 0; x < 8; x++) {
            tablero.colocarMuro(new Muro(new Posicion(x, 0), Orientacion.VERTICAL));
            tablero.colocarMuro(new Muro(new Posicion(x, 2), Orientacion.VERTICAL));
        }
        for (int x = 0; x < 4; x++) {
            tablero.colocarMuro(new Muro(new Posicion(x, 4), Orientacion.VERTICAL));
        }
        Muro muroExtra = new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL);
        assertThrows(AssertionError.class, () -> tablero.colocarMuro(muroExtra),
                "colocarMuro debe lanzar AssertionError al superar el máximo de 20 muros");

        // Violación de invariante de clase (21 muros forzados en murosColocados)
        tablero.getMurosColocados().add(muroExtra);
        assertThrows(AssertionError.class, () -> tablero.colisionaConMurosExistentes(muroValido),
                "colisionaConMurosExistentes debe detectar invariante violada");
        assertThrows(AssertionError.class, () -> tablero.esColocacionMuroValida(muroValido),
                "esColocacionMuroValida debe detectar invariante violada");
        assertThrows(AssertionError.class, () -> tablero.hayCaminoParaTodos(List.of()),
                "hayCaminoParaTodos debe detectar invariante violada");
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite) y Contrato (DbC):
     * Comprueba el tamaño configurable de tablero (7x7), filas objetivo personalizadas,
     * limpieza completa en reiniciar() y precondiciones inválidas de tamaño y fila objetivo.
     */
    @Test
    @DisplayName("Tablero configurable (7x7/9x9), filas objetivo, reinicio y precondiciones DbC")
    void testTamanoConfigurableFilasObjetivoYReinicio() {
        assertEquals(9, tablero.getTamanoTablero());

        // Partición válida: configurar tablero 7x7 y filas objetivo personalizadas
        tablero.setTamanoTablero(7);
        assertEquals(7, tablero.getTamanoTablero());

        tablero.setPosicionJugador(Color.ROJO, new Posicion(3, 0));
        tablero.setFilaObjetivo(Color.ROJO, 6);
        tablero.setPosicionJugador(Color.VERDE, new Posicion(0, 3));
        tablero.setFilaObjetivo(Color.VERDE, 6);

        assertEquals(6, tablero.getFilaObjetivo(Color.ROJO));
        assertEquals(0, tablero.getFilaObjetivo(Color.AZUL));

        // Valores frontera en 7x7: movimiento válido (3,0)->(3,1) frente a salida (6,6)->(7,6)
        assertTrue(tablero.esMovimientoValido(new Posicion(3, 0), new Posicion(3, 1), null));
        assertFalse(tablero.esMovimientoValido(new Posicion(6, 6), new Posicion(7, 6), null));

        // Valores frontera de muro en 7x7: límite válido (5,4) frente a fuera de rango (6,4)
        assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(5, 4), Orientacion.HORIZONTAL)));
        assertFalse(tablero.esColocacionMuroValida(new Muro(new Posicion(6, 4), Orientacion.HORIZONTAL)));

        // Partición válida: reiniciar limpia muros, posiciones y filas objetivo
        tablero.colocarMuro(new Muro(new Posicion(2, 2), Orientacion.VERTICAL));
        tablero.reiniciar();
        assertTrue(tablero.getMurosColocados().isEmpty());
        assertTrue(tablero.getPosicionesJugadores().isEmpty());
        assertNull(tablero.getPosicionJugador(Color.ROJO));

        // Precondiciones DbC inválidas de tamaño (0, par=6) y fila objetivo (color null, fila=7)
        assertThrows(AssertionError.class, () -> tablero.setTamanoTablero(0));
        assertThrows(AssertionError.class, () -> tablero.setTamanoTablero(6));
        assertThrows(AssertionError.class, () -> tablero.setFilaObjetivo(null, 0));
        assertThrows(AssertionError.class, () -> tablero.setFilaObjetivo(Color.ROJO, 7));
    }

    /**
     * Prueba de Caja Blanca (Decision & Condition Coverage completo en saltos):
     * Cubre todas las condiciones atómicas de salto recto y salto diagonal en ambos ejes
     * (dx != 0 y dy != 0): bordes laterales/esquinas, muros detrás del oponente, muros laterales
     * bloqueando una diagonal, destinos fuera de las diagonales válidas y muros interpuestos
     * entre el origen y el oponente (prohibiendo tanto el salto recto como las diagonales).
     */
    @Test
    @DisplayName("Decision & Condition Coverage: saltos rectos/diagonales, bordes y muro intermedio")
    void testEsMovimientoValidoSaltoDiagonalCoberturaCondiciones() {
        // 1. Condición (dx != 0): oponente en borde derecho (x=8), salto recto fuera del tablero
        Posicion origenBorde = new Posicion(7, 4);
        Posicion oponenteBorde = new Posicion(8, 4);
        assertFalse(tablero.esMovimientoValido(origenBorde, new Posicion(9, 4), oponenteBorde),
                "El salto recto fuera del tablero no debe ser válido");
        assertTrue(tablero.esMovimientoValido(origenBorde, new Posicion(8, 3), oponenteBorde),
                "Con oponente en borde derecho, debe permitir salto diagonal hacia (8, 3)");
        assertTrue(tablero.esMovimientoValido(origenBorde, new Posicion(8, 5), oponenteBorde),
                "Con oponente en borde derecho, debe permitir salto diagonal hacia (8, 5)");
        assertFalse(tablero.esMovimientoValido(origenBorde, new Posicion(6, 3), oponenteBorde),
                "En salto horizontal bloqueado (dx != 0), destino no diagonal debe ser inválido");

        // 2. Condición (destino.esValida == false) en esquina superior derecha (8, 0) con dx != 0
        Posicion origenEsquina = new Posicion(7, 0);
        Posicion oponenteEsquina = new Posicion(8, 0);
        assertFalse(tablero.esMovimientoValido(origenEsquina, new Posicion(8, -1), oponenteEsquina),
                "No debe permitir salto diagonal hacia posición fuera del límite superior");
        assertTrue(tablero.esMovimientoValido(origenEsquina, new Posicion(8, 1), oponenteEsquina),
                "Debe permitir salto diagonal válido hacia (8, 1)");

        // 2b. Condición (destino.esValida == false) en esquina inferior izquierda (0, 8) con dy != 0
        Posicion origenBordeIzq = new Posicion(0, 7);
        Posicion oponenteEsquinaInfIzq = new Posicion(0, 8);
        assertFalse(tablero.esMovimientoValido(origenBordeIzq, new Posicion(0, 9), oponenteEsquinaInfIzq),
                "En salto vertical, el salto recto fuera del tablero no debe ser válido");
        assertFalse(tablero.esMovimientoValido(origenBordeIzq, new Posicion(-1, 8), oponenteEsquinaInfIzq),
                "En salto vertical con borde izquierdo, diagonal hacia x = -1 debe ser inválida");
        assertTrue(tablero.esMovimientoValido(origenBordeIzq, new Posicion(1, 8), oponenteEsquinaInfIzq),
                "En salto vertical con borde izquierdo, diagonal hacia (1, 8) debe ser válida");

        // 3. Condiciones (dy != 0): muro detrás + muro lateral en (4,5)->(5,5) + destino no diagonal
        tablero.reiniciar();
        Posicion origen = new Posicion(4, 4);
        Posicion oponente = new Posicion(4, 5);
        tablero.colocarMuro(new Muro(new Posicion(4, 5), Orientacion.HORIZONTAL));
        tablero.colocarMuro(new Muro(new Posicion(4, 4), Orientacion.VERTICAL));

        assertFalse(tablero.esMovimientoValido(origen, new Posicion(4, 6), oponente),
                "Salto recto bloqueado por muro detrás del oponente");
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(5, 5), oponente),
                "Salto diagonal bloqueado por muro lateral");
        assertTrue(tablero.esMovimientoValido(origen, new Posicion(3, 5), oponente),
                "Salto diagonal sin muro lateral debe ser válido");
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(3, 6), oponente),
                "En salto vertical bloqueado (dy != 0), destino no diagonal debe ser inválido");

        // 3b. Condiciones (dx != 0): muro vertical detrás + muro lateral horizontal + destino no diagonal
        tablero.reiniciar();
        Posicion oponenteHoriz = new Posicion(5, 4);
        tablero.colocarMuro(new Muro(new Posicion(5, 4), Orientacion.VERTICAL));
        tablero.colocarMuro(new Muro(new Posicion(5, 3), Orientacion.HORIZONTAL));

        assertFalse(tablero.esMovimientoValido(origen, new Posicion(6, 4), oponenteHoriz),
                "Salto recto horizontal bloqueado por muro detrás del oponente");
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(5, 3), oponenteHoriz),
                "Salto diagonal superior horizontal bloqueado por muro lateral");
        assertTrue(tablero.esMovimientoValido(origen, new Posicion(5, 5), oponenteHoriz),
                "Salto diagonal inferior horizontal sin muro lateral debe ser válido");
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(6, 3), oponenteHoriz),
                "En salto horizontal bloqueado (dx != 0), destino fuera de diagonales válidas debe ser inválido");

        // 4. Condición atómica (!hayMuroBloqueante(origen, oponente) == false): muro entre ambos peones
        tablero.reiniciar();
        tablero.colocarMuro(new Muro(new Posicion(4, 4), Orientacion.HORIZONTAL));
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(4, 6), oponente),
                "No debe permitir salto recto si hay un muro entre origen y oponente");

        // Incluso con muro detrás del oponente, el muro intermedio impide cualquier salto diagonal
        tablero.colocarMuro(new Muro(new Posicion(4, 5), Orientacion.HORIZONTAL));
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(3, 5), oponente),
                "No debe permitir salto diagonal izquierdo si hay muro entre origen y oponente");
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(5, 5), oponente),
                "No debe permitir salto diagonal derecho si hay muro entre origen y oponente");

        // Muro vertical intermedio entre origen y oponente en eje horizontal
        tablero.reiniciar();
        Posicion oponenteHorizontal = new Posicion(5, 4);
        tablero.colocarMuro(new Muro(new Posicion(4, 4), Orientacion.VERTICAL));
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(6, 4), oponenteHorizontal),
                "No debe permitir salto recto horizontal si hay un muro vertical entre origen y oponente");
        tablero.colocarMuro(new Muro(new Posicion(5, 4), Orientacion.VERTICAL));
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(5, 3), oponenteHorizontal),
                "No debe permitir salto diagonal superior si hay muro vertical entre origen y oponente");
        assertFalse(tablero.esMovimientoValido(origen, new Posicion(5, 5), oponenteHorizontal),
                "No debe permitir salto diagonal inferior si hay muro vertical entre origen y oponente");
    }
}
