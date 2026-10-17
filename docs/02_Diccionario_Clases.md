# 02. Diccionario de Clases

## 1. Categorización de Clases, Interfaces y Enumeradores

### 1.1 Modelo (Lógica de Negocio)
*No tiene dependencias de JavaFX ni del Controlador.*

#### Enumeradores
*   `Color`: Rojo, Azul, Verde, Amarillo. (Para los jugadores).
*   `Orientacion`: HORIZONTAL, VERTICAL. (Para los muros).

#### Clases de Bajo Nivel (Sin interacción entre distintas clases de negocio, con retorno de valores o modificación de estado propio)
1.  **`Posicion`** *(Implementa Programación por Contrato)*
    *   **Responsabilidad**: Representar una coordenada 2D inmutable (x, y) en el tablero y realizar cálculos geométricos elementales.
    *   **Atributos**: `int x`, `int y`.
    *   **Métodos clave**: `esValida(int limiteTablero)`, `distanciaManhattan(Posicion otra)`, `esAdyacente(Posicion otra)`, `desplazar(int dx, int dy)`, `getX()`, `getY()`, `equals(Object o)`, `hashCode()`.
2.  **`Muro`** *(Implementa Programación por Contrato)*
    *   **Responsabilidad**: Representar un muro en el tablero y evaluar su validez espacial e intersección geométrica.
    *   **Atributos**: `Posicion origen` (esquina superior izquierda de la intersección), `Orientacion orientacion`.
    *   **Métodos clave**: `esPosicionValida(int limiteTablero)`, `colisionaCon(Muro otro)`, `bloqueaPaso(Posicion origen, Posicion destino)`, `getOrigen()`, `getOrientacion()`.
3.  **`Jugador`** *(Implementa Programación por Contrato)*
    *   **Responsabilidad**: Mantener y actualizar el estado individual de un jugador.
    *   **Atributos**: `String nombre`, `Color color`, `Posicion posicionActual`, `int filaObjetivo`, `int murosRestantes`.
    *   **Métodos clave**: `moverA(Posicion p)`, `consumirMuro()`, `hasMurosRestantes()`, `haLlegadoAMeta()`, `reiniciarEstado(Posicion origen, int murosIniciales)`.

#### Clases de Lógica Compleja e Integración del Modelo (Candidatas para Caja Blanca y Mocks)
4.  **`ValidadorCaminos` / `IValidadorCaminos`**
    *   **Responsabilidad**: Verificar mediante búsqueda en grafos (BFS/DFS) que ningún jugador quede encerrado sin camino hacia su fila objetivo.
    *   **Métodos clave**: `hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero)`, `calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros)`.
5.  **`Tablero`** *(Implementa Programación por Contrato)*
    *   **Responsabilidad**: Gestionar el estado espacial del juego y validar reglas de movimiento y colocación de muros.
    *   **Atributos**: `List<Muro> murosColocados`, `Map<Color, Posicion> posicionesJugadores`, `IValidadorCaminos validadorCaminos`, `int TAMANO_TABLERO = 9`.
    *   **Métodos clave**: `esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente)`, `esColocacionMuroValida(Muro muro)`, `colocarMuro(Muro muro)`, `hayCaminoParaTodos(List<Jugador> jugadores)`.
6.  **`Partida`** (Fachada del Modelo) y **`ISorteadorTurno`**
    *   **Responsabilidad**: Gestionar el flujo de la partida, configuración inicial, sorteo/gestión de turnos y condición de victoria.
    *   **Atributos**: `Tablero tablero`, `List<Jugador> jugadores`, `ISorteadorTurno sorteadorTurno`, `int indiceJugadorActual`, `boolean terminada`.
    *   **Métodos clave**: `configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio)`, `iniciarPartida()`, `realizarMovimiento(Posicion p)`, `colocarMuro(Muro m)`, `comprobarVictoria()`.

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
