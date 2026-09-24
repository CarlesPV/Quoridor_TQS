# 04. Guía de Pruebas y TDD Estrícto

## 1. Flujo TDD y Git (Red-Green-Refactor)
Para garantizar el cumplimiento de TDD estricto, el ciclo de desarrollo debe reflejarse en los commits de Git de forma iterativa (con un mínimo de dos versiones por método entre pruebas y código de producción):
*   **Iteración 1 (Caso base)**:
    *   `test: [Red] add failing test for ...` (Se escribe la prueba unitaria inicial; falla o no compila).
    *   `feat: [Green] implement initial logic for ...` (Se implementa el código mínimo para superarla).
*   **Iteración 2 (Casos límite o refactorización)**:
    *   `test: [Red v2] add test case for boundary or exception in ...` (Se amplía la prueba con nuevos escenarios).
    *   `feat: [Green v2] update logic to handle boundary case in ...` o `refactor: optimize internal logic without changing behavior`.

*Para más detalles sobre la política de ramas, convenciones y CI, consultar [05. Flujo Git y CI](05_Flujo_Git_CI.md).*

## 2. Pruebas de Caja Negra (Particiones y Valores Límite)
Se aplicarán sobre clases de bajo nivel como `Posicion` y `Muro`.

**Caso de Uso: Creación de un Muro (`new Muro(posicion, orientacion)`)**
*   **Valores Límite (Posicion X/Y de 0 a 8 para intersecciones en el tablero de 9x9)**:
    *   X=-1, Y=0 (Muro inválido - Fuera izq).
    *   X=8, Y=8 (Muro inválido - Un muro ocupa 2 casillas, la esquina inferior derecha no puede originar un muro).
    *   X=0, Y=0 (Muro válido - Esquina superior izq).
    *   X=7, Y=7 (Muro válido - Máximo límite inferior derecho posible para el origen de un muro).
*   **Particiones Equivalentes**:
    *   Cualquier valor X entre 0 y 7, Y entre 0 y 7 (Válido).
    *   Cualquier valor negativo o mayor que 8 (Inválido).

## 3. Pruebas de Caja Blanca y Automatización

### 3.1 Path Coverage y Loop Testing
- **Path Coverage**: Método `comprobarVictoria()` en `Partida`.
  - Ruta 1: El jugador actual no está en la fila objetivo (retorna `false`).
  - Ruta 2: El jugador actual alcanza la fila objetivo (retorna `true`).
  - Ruta 3: Casos de empate si existieran reglas de tiempo (retorna un estado especial).
- **Loop Testing**: Método `hayCaminoPosible()` en `Tablero`. 
  - Utilizará un algoritmo BFS/DFS que contiene un **bucle anidado** (bucle principal de la cola, y bucle secundario iterando sobre las casillas adyacentes).
  - Test 1: Camino directo sin muros (0 iteraciones por muro, máximo de iteraciones espaciales).
  - Test 2: Camino bloqueado parcialmente (bucle evalúa ramas cortadas).
  - Test 3: Jugador totalmente rodeado (bucle finaliza rápido con resultado falso).

### 3.2 Data-Driven Testing (Automatización)
Ejemplo de `@ParameterizedTest` para validar movimientos básicos del peón:

```java
@ParameterizedTest
@CsvSource({
    "4,8, 4,7, true",   // Movimiento normal adelante
    "4,8, 5,8, true",   // Movimiento lateral
    "4,8, 4,6, false",  // Movimiento de más de 1 casilla
    "4,8, -1,8, false"  // Movimiento fuera del tablero
})
void testEsMovimientoValidoBasico(int oX, int oY, int dX, int dY, boolean esperado) {
    Tablero tablero = new Tablero();
    Posicion origen = new Posicion(oX, oY);
    Posicion destino = new Posicion(dX, dY);
    assertEquals(esperado, tablero.esMovimientoValido(origen, destino));
}
```

### 3.3 Pairwise Testing
Configuración inicial de la partida, probando combinaciones de:
*   Número de jugadores: 2, 4.
*   Tablero: Estándar (9x9).
*   Muros iniciales por jugador: 10 (para 2p), 5 (para 4p).
Al aplicar Pairwise, reduciremos la explosión combinatoria de parámetros de configuración inicial, asegurando que todos los pares de (Jugadores x Muros) sean probados.

## 4. Estrategia de Mocks (Mínimo 5 Mocks)

Más del 50% de estos mocks se usarán para testear el Modelo (ej. `Partida` probada aislando al `Tablero`).

1.  **Mock de `Tablero` (Mockito)**: Usado al testear `Partida`. Simulará `tablero.hayCaminoPosible(...)` para forzar escenarios de colocación de muros sin ejecutar el complejo algoritmo BFS, enfocándose en la gestión del turno de la partida.
2.  **Mock de `IQuoridorVista` (Manual o Spy)**: Usado al testear `QuoridorController`. Guardará si se llamó a `mostrarError()` o `mostrarEstado()` (estado interno `boolean fueLlamado`).
3.  **Mock de `Jugador` (Mockito)**: Usado al testear `Tablero`. Simulará que un jugador tiene o no muros restantes (`hasMurosRestantes()`) para comprobar la regla de negocio al intentar colocar un muro.
4.  **Mock del Dado/Generador de Turno Inicial (Manual)**: Si se implementa un sorteo del primer turno mediante una interfaz `ISorteadorTurno`, se creará un Mock Manual que devuelva un valor fijo constante (ej. siempre devuelve el Jugador 1) para evitar la aleatoriedad en los tests unitarios.
5.  **Mock de `AlmacenamientoPartida` (Mockito)**: (Supuesto de guardado/carga). Interfaz para guardar el estado del juego. Se verificará que el Controlador llama a `guardarPartida()` al hacer clic en un supuesto botón de guardado, simulando éxito o fallo en la I/O.
