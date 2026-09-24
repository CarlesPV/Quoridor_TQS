# 02. Diccionario de Clases

## 1. Categorización de Clases, Interfaces y Enumeradores

### 1.1 Modelo (Lógica de Negocio)
*No tiene dependencias de JavaFX ni del Controlador.*

#### Enumeradores
*   `Color`: Rojo, Azul, Verde, Amarillo. (Para los jugadores).
*   `Orientacion`: HORIZONTAL, VERTICAL. (Para los muros).

#### Clases de Bajo Nivel (Candidatas para Pruebas de Caja Negra)
1.  **`Posicion`**
    *   **Responsabilidad**: Representar una coordenada 2D inmutable (x, y) en el tablero.
    *   **Atributos**: `int x`, `int y`.
    *   **Métodos clave**: `getX()`, `getY()`, `equals(Object o)`, `hashCode()`.
2.  **`Muro`**
    *   **Responsabilidad**: Representar un muro colocado en el tablero.
    *   **Atributos**: `Posicion origen` (esquina superior izquierda de la intersección), `Orientacion orientacion`.
    *   **Métodos clave**: `getOrigen()`, `getOrientacion()`.
3.  **`Jugador`**
    *   **Responsabilidad**: Mantener el estado de un jugador.
    *   **Atributos**: `String nombre`, `Color color`, `Posicion posicionActual`, `int murosRestantes`.
    *   **Métodos clave**: `moverA(Posicion p)`, `consumirMuro()`, `hasMurosRestantes()`.

#### Clases de Lógica Compleja (Candidatas para Pruebas de Caja Blanca)
4.  **`Tablero`**
    *   **Responsabilidad**: Gestionar el estado espacial del juego y validar reglas geométricas.
    *   **Atributos**: `List<Muro> murosColocados`, `Map<Color, Posicion> metaJugadores`, `int TAMANO_TABLERO = 9`.
    *   **Métodos clave**: `esMovimientoValido(Posicion origen, Posicion destino)`, `esColocacionMuroValida(Muro muro)`, `hayCaminoPosible(Posicion origen, Posicion destino)` (Usa BFS/DFS).
5.  **`Partida`** (Fachada del Modelo)
    *   **Responsabilidad**: Gestionar el flujo de la partida, turnos y condición de victoria.
    *   **Atributos**: `Tablero tablero`, `List<Jugador> jugadores`, `int indiceJugadorActual`, `boolean terminada`.
    *   **Métodos clave**: `iniciarPartida()`, `realizarMovimiento(Posicion p)`, `colocarMuro(Muro m)`, `comprobarVictoria()`.

### 1.2 Vista (Interfaces y JavaFX)
1.  **`IQuoridorVista`** (Interfaz)
    *   **Responsabilidad**: Contrato que cualquier vista debe cumplir para ser controlada. Permite crear *mocks*.
    *   **Métodos clave**: `mostrarEstado(EstadoPartida estado)`, `mostrarError(String mensaje)`, `setControlador(QuoridorController ctrl)`.
2.  **`VentanaPrincipalJavaFX`** (Implementa `IQuoridorVista`)
    *   **Responsabilidad**: Renderizar la interfaz gráfica usando nodos de JavaFX (Canvas o GridPane).
    *   **Atributos**: Elementos de UI (Canvas, Labels para muros restantes, etc.).

### 1.3 Controlador
1.  **`QuoridorController`**
    *   **Responsabilidad**: Intermediario entre la Vista y el Modelo. Orquesta el flujo basándose en las acciones del usuario.
    *   **Atributos**: `Partida modelo`, `IQuoridorVista vista`.
    *   **Métodos clave**: `onCasillaClic(int x, int y)`, `onInterseccionClic(int x, int y, Orientacion o)`, `onBotonReiniciar()`.

### 1.4 DTOs (Data Transfer Objects)
1.  **`EstadoPartida`** (Record o Clase Inmutable)
    *   **Responsabilidad**: Encapsular el estado del modelo para enviarlo a la vista sin exponer referencias mutables de los objetos del modelo.
    *   **Atributos**: Posiciones de todos los jugadores, lista de muros, turno actual, estado de fin de partida.
