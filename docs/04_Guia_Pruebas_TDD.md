# 04. Guía de Pruebas y TDD Estricto

## 1. Flujo TDD y Git (Red-Green-Refactor)
Para garantizar el cumplimiento de TDD estricto, en al menos el **90% de los métodos desarrollados** del Modelo y del Controlador (excluyendo métodos triviales como *getters* y *setters*), el historial de Git debe registrar **un mínimo de dos versiones diferentes tanto del código de prueba como del código desarrollado**:
*   **Iteración 1 (Caso base)**:
    *   `test: [Red] add failing test for ...` (Se escribe la primera versión de la prueba unitaria; falla o no compila).
    *   `feat: [Green] implement initial logic for ...` (Se sube la versión inicial del código desarrollado y, si aplica, sus correcciones/depuraciones para superar el test).
*   **Iteración 2 (Casos límite, frontera o ampliación de lógica)**:
    *   `test: [Red v2] add test case for boundary or exception in ...` (Segunda versión del código de prueba ampliando escenarios).
    *   `feat: [Green v2] update logic to handle boundary case in ...` (Segunda versión del código desarrollado adaptada a los nuevos casos).

*Para más detalles sobre la política de ramas, convenciones y CI, consultar [05. Flujo Git y CI](05_Flujo_Git_CI.md).*

## 2. Pruebas de Caja Negra
Se aplicarán de forma exhaustiva sobre **todos los métodos desarrollados de las clases de bajo nivel** (`Posicion`, `Muro` y `Jugador`), cubriendo como mínimo el **90% de todas las particiones equivalentes** y el **90% de todos los casos posibles de valores límite y valores frontera**.

### 2.1 Particiones Equivalentes y Valores Límite / Frontera
**Ejemplo en `Muro.esPosicionValida(int limiteTablero)` (con `limiteTablero = 9`, intersecciones válidas de `0` a `7`)**:
*   **Particiones Equivalentes**:
    *   Partición válida: $0 \le X \le 7$ y $0 \le Y \le 7$, con orientación no nula (`HORIZONTAL` o `VERTICAL`).
    *   Partición inválida inferior: $X < 0$ o $Y < 0$.
    *   Partición inválida superior: $X \ge 8$ o $Y \ge 8$ (un muro ocupa 2 casillas y no puede iniciarse en el borde final).
*   **Valores Límite y Frontera**:
    *   **Límite inferior**: $X=0, Y=0$ (válido, sobre el límite).
    *   **Frontera inferior**: $X=-1, Y=0$ (inválido, justo fuera) y $X=1, Y=1$ (válido, justo dentro).
    *   **Límite superior**: $X=7, Y=7$ (válido, máximo origen permitido).
    *   **Frontera superior**: $X=6, Y=6$ (válido, justo dentro) y $X=8, Y=8$ (inválido, justo fuera).

El mismo análisis de particiones equivalentes, límites y fronteras se aplica al resto de métodos de bajo nivel: `Posicion.esValida`, `Posicion.distanciaManhattan`, `Posicion.esAdyacente`, `Posicion.desplazar`, `Muro.colisionaCon`, `Muro.bloqueaPaso`, `Jugador.moverA`, `Jugador.consumirMuro`, `Jugador.hasMurosRestantes` y `Jugador.haLlegadoAMeta`.

### 2.2 Pairwise Testing
Aplicado al método `Partida.configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio)`, que combina múltiples parámetros de configuración:
*   **Número de jugadores**: `2`, `4`.
*   **Tamaño de tablero**: `7` (reducido), `9` (estándar).
*   **Muros iniciales por jugador**: `5`, `10`.
*   **Sorteo de turno inicial**: `true`, `false`.

Mediante una tabla *Pairwise* (generando las tuplas que cubren todos los pares de valores entre los 4 parámetros sin evaluar las $2^4 = 16$ combinaciones exhaustivas), se verifica la correcta inicialización y detección de configuraciones incompatibles.

## 3. Programación por Contrato (Design by Contract)
Se implementa **Programación por Contrato en más del 50% de las clases del Modelo** (concretamente en `Posicion`, `Muro`, `Jugador` y `Tablero`, representando 4 de las 6 entidades del paquete `model`):
*   **Precondiciones**: Verificación de parámetros de entrada al inicio de cada método mediante `assert` (con flag `-ea` activo en Maven Surefire) y validación de argumentos (ej. coordenadas no nulas, `murosRestantes > 0` antes de `consumirMuro()`).
*   **Postcondiciones**: Comprobación del estado resultante antes de retornar (ej. tras `consumirMuro()`, el contador es exactamente el valor previo menos 1; tras `moverA(destino)`, `posicionActual.equals(destino)`).
*   **Invariantes de Clase**: Método `verificarInvariante()` invocado al finalizar constructores y métodos mutadores (ej. en `Jugador`: `murosRestantes >= 0 && posicionActual != null`; en `Tablero`: `murosColocados.size() <= 20`).

## 4. Pruebas de Caja Blanca
Todas las pruebas de Caja Blanca se documentarán en el informe mediante capturas de pantalla de cobertura (EclEmma / JaCoCo):

### 4.1 Statement Coverage (Todo el código)
*   Se garantiza el **Statement Coverage** en la totalidad del código desarrollado del Modelo y Controlador, incluyendo en el informe las capturas con el porcentaje de cobertura de cada método.

### 4.2 Decision Coverage y Condition Coverage (Mínimo 3 métodos con $\ge 2$ condicionales)
Se analizan y prueban exhaustivamente todas las decisiones (`true`/`false` global) y condiciones atómicas en al menos 3 métodos:
1.  **`Muro.colisionaCon(Muro otro)`**: Evalúa si coinciden en origen (solapamiento o cruce en X) o si comparten orientación y se solapan parcialmente en su eje.
2.  **`Tablero.esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente)`**: Evalúa límites del tablero, adyacencia ortogonal, ausencia de muro bloqueante y regla de salto sobre oponente.
3.  **`Tablero.esColocacionMuroValida(Muro muro)`**: Evalúa validez de coordenadas de intersección, no colisión con muros previos y preservación de caminos válidos para todos los jugadores.

### 4.3 Path Coverage (Mínimo 2 métodos + Diagramas de Actividades UML)
Para cada método analizado bajo *Path Coverage* se elaborará su **diagrama de flujo (diagrama de actividades en UML)** junto con el cálculo de caminos independientes (complejidad ciclomática) y sus capturas en EclEmma:
1.  **`Partida.comprobarVictoria()`**:
    *   Ruta 1: La partida ya figura como terminada.
    *   Ruta 2: El jugador actual no ha alcanzado su `filaObjetivo` (retorna `false`).
    *   Ruta 3: El jugador actual alcanza su `filaObjetivo`, marcando la partida como terminada (retorna `true`).
2.  **`Partida.colocarMuro(Muro m)`**:
    *   Ruta 1: La partida ya ha finalizado o el jugador actual no dispone de muros restantes (rechaza la jugada).
    *   Ruta 2: El tablero indica que la colocación del muro es inválida por colisión o bloqueo total de caminos.
    *   Ruta 3: El muro es válido, se registra en el tablero, se descuenta un muro al jugador y se avanza el turno.

### 4.4 Loop Testing (Mínimo 2 bucles simples y 2 bucles anidados)
Se diseñan casos específicos para evaluar 0 iteraciones, 1 iteración, 2 iteraciones, $m$ iteraciones típicas y $n$ (máximo) iteraciones:
*   **Bucles Simples (mínimo 2)**:
    1.  `Tablero.colisionaConMurosExistentes(Muro nuevoMuro)`: Bucle simple que recorre `List<Muro> murosColocados` (tablero vacío = 0 iteraciones; 1 muro; múltiples muros).
    2.  `Tablero.hayCaminoParaTodos(List<Jugador> jugadores)`: Bucle simple que itera sobre la lista de jugadores verificando que ninguno quede encerrado.
*   **Bucles Anidados (mínimo 2)**:
    1.  `ValidadorCaminos.hayCaminoDisponible(...)`: Algoritmo BFS cuyo bucle externo procesa la cola de casillas visitadas y cuyo bucle interno itera sobre los 4 desplazamientos ortogonales posibles.
    2.  `ValidadorCaminos.construirMatrizVisitados(int tamano, List<Muro> muros)` / `Tablero.obtenerEstadoCasillas()`: Doble bucle anidado (`for` filas $\times$ `for` columnas) que recorre la cuadrícula completa del tablero.

## 5. Automatización (Data-Driven Testing en Clases de Bajo Nivel)
Se implementan pruebas parametrizadas (*Data-Driven*) con JUnit 5 (`@ParameterizedTest`, `@CsvSource` y `@CsvFileSource` usando `src/test/resources/data/movimientos_test.csv`) para un **mínimo de 2 métodos desarrollados de clases de bajo nivel**:

1.  **Método 1 (`Posicion.esAdyacente(Posicion otra)`)**:
```java
@ParameterizedTest
@CsvFileSource(resources = "/data/movimientos_test.csv", numLinesToSkip = 1)
void testEsAdyacenteDataDriven(int oX, int oY, int dX, int dY, boolean esperado) {
    Posicion origen = new Posicion(oX, oY);
    Posicion destino = new Posicion(dX, dY);
    assertEquals(esperado, origen.esAdyacente(destino));
}
```
2.  **Método 2 (`Muro.bloqueaPaso(Posicion origen, Posicion destino)`)**:
```java
@ParameterizedTest
@CsvSource({
    "4,4, HORIZONTAL, 4,4, 4,5, true",   // Muro horizontal bloquea paso vertical en X=4
    "4,4, HORIZONTAL, 5,4, 5,5, true",   // Segundo segmento del mismo muro horizontal
    "4,4, HORIZONTAL, 4,4, 5,4, false",  // Paso lateral no bloqueado por muro horizontal
    "4,4, VERTICAL,   4,4, 5,4, true"    // Muro vertical bloquea paso horizontal
})
void testBloqueaPasoDataDriven(int mX, int mY, Orientacion orientacion,
                               int oX, int oY, int dX, int dY, boolean esperado) {
    Muro muro = new Muro(new Posicion(mX, mY), orientacion);
    assertEquals(esperado, muro.bloqueaPaso(new Posicion(oX, oY), new Posicion(dX, dY)));
}
```

## 6. Estrategia de Mock Objects (5 Mocks: >50% en el Modelo y $\ge 2$ Manuales)
Se implementan **5 Mock Objects** en total, de los cuales **4 se destinan a pruebas del Modelo (80%)** y **1 simula la interacción con la Vista desde el Controlador**, contando con **al menos 2 mocks creados manualmente** (sin librerías externas) y el resto mediante **Mockito**:

1.  **Mock Manual de `IValidadorCaminos` (Propio - Test del Modelo `Tablero`)**: Implementación manual que permite forzar el resultado de `hayCaminoDisponible(...)` (`true` o `false`) para probar `Tablero.esColocacionMuroValida` sin depender de la ejecución real del BFS.
2.  **Mock Manual de `ISorteadorTurno` (Propio - Test del Modelo `Partida`)**: Implementación manual determinista que fija el índice del jugador inicial para probar `Partida.iniciarPartida()` y la rotación de turnos sin aleatoriedad.
3.  **Mock de `Tablero` (Mockito - Test del Modelo `Partida`)**: Usado al testear `Partida` en aislamiento, simulando respuestas de validación de movimientos y muros para verificar las transiciones de estado del juego.
4.  **Mock de `Jugador` (Mockito - Test del Modelo `Partida` / `Tablero`)**: Simula el estado de muros disponibles (`hasMurosRestantes()`) y posición de meta (`haLlegadoAMeta()`) al verificar las reglas del Modelo.
5.  **Mock de `IQuoridorVista` (Mockito / Spy Manual - Test del Controlador `QuoridorController`)**: Simula la interfaz gráfica para verificar que el Controlador invoca `mostrarEstado()`, `mostrarError()` y `mostrarVictoria()` ante las acciones del usuario.

