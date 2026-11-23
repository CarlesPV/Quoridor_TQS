# 06. Checklist de Seguimiento del Proyecto

Este documento registra el estado actual del proyecto **Quoridor_TQS**, detallando las tareas completadas y los requisitos pendientes para completar el desarrollo, las pruebas y la entrega final.

---

## 1. Infraestructura, Configuración y CI/CD

- [x] Estructura de directorios Maven (`src/main/java`, `src/main/resources`, `src/test/java`, `src/test/resources`).
- [x] Configuración de `pom.xml` actualizada a **JavaSE-21** (`maven-compiler-plugin` 3.13.0, `maven-surefire-plugin` 3.2.5 con `-ea`).
- [x] Configuración del entorno Eclipse (`.classpath` y `.settings/org.eclipse.jdt.core.prefs`) sincronizada con **JavaSE-21**.
- [x] Dependencias actualizadas para Java 21: JavaFX `21.0.2`, JUnit Jupiter `5.10.2`, Mockito `5.11.0`, JaCoCo `0.8.12`.
- [x] Reglas de calidad de código definidas en `checkstyle.xml` y vinculadas a `maven-checkstyle-plugin` (`10.14.2`).
- [x] Pipeline de Integración Continua en `.github/workflows/ci.yml` configurado para ejecutarse en `push` y `pull_request` sobre la rama `master`.
- [x] Documentación técnica de diseño, arquitectura MVC, guía de pruebas TDD y flujo Git/CI (`docs/01` a `docs/05`).
- [x] Configurar en los ajustes del repositorio de GitHub la regla de protección de la rama `master` (*Branch Protection Rule*: activar *"Require a pull request before merging"* y *"Require status checks to pass before merging"* seleccionando el job `Compilación, Tests y Checkstyle (Java 21)`).

---

## 2. Arquitectura MVC e Implementación de Clases

### 2.1 Modelo (`org.quoridor.model`)
- [x] Enumerador `Color` (`ROJO`, `AZUL`, `VERDE`, `AMARILLO`).
- [x] Enumerador `Orientacion` (`HORIZONTAL`, `VERTICAL`).
- [x] **Clase de bajo nivel `Posicion`**:
  - [x] Atributos privados `x`, `y`, constructores y *getters*.
  - [x] Métodos de lógica propia: `esValida(int limiteTablero)`, `distanciaManhattan(Posicion p)`, `esAdyacente(Posicion p)`, `desplazar(int dx, int dy)`, `equals(Posicion p)`, `equals(Object obj)`, `hashCode()`.
- [x] **Clase de bajo nivel `Muro`**:
  - [x] Atributos `Posicion origen` y `Orientacion orientacion`.
  - [x] Métodos de lógica propia: `esPosicionValida(int limiteTablero)`, `colisionaCon(Muro otroMuro)`, `bloqueaPaso(Posicion origen, Posicion destino)`.
- [x] **Clase de bajo nivel `Jugador`**:
  - [x] Atributos `nombre`, `color`, `posicionActual`, `filaObjetivo`, `murosRestantes`.
  - [x] Métodos de lógica propia: `moverA(Posicion p)`, `consumirMuro()`, `hasMurosRestantes()`, `haLlegadoAMeta()`, `reiniciarEstado(Posicion origen, int murosIniciales)`.
- [x] **Componente de validación de caminos (`IValidadorCaminos` y `ValidadorCaminos`)**:
  - [x] Métodos `hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero)` (algoritmo BFS) y `calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero)`.
- [x] **Clase `Tablero`**:
  - [x] Gestión de `murosColocados`, `posicionesJugadores`, `filasObjetivo` y `IValidadorCaminos`.
  - [x] Métodos: `esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente)`, `esColocacionMuroValida(Muro muro)`, `colisionaConMurosExistentes(Muro nuevoMuro)`, `colocarMuro(Muro muro)`, `hayCaminoParaTodos(List<Jugador> jugadores)`, `reiniciar()`.
- [x] **Interfaz `ISorteadorTurno`, `SorteadorTurno` y Fachada `Partida`**:
  - [x] Métodos: `configurarPartida(List<Jugador>)`, `configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio)`, `iniciarPartida()`, `realizarMovimiento(Posicion p)`, `colocarMuro(Muro m)`, `comprobarVictoria()`, `getJugadorActual()`.
- [x] **Fachada de estado inmutable en `Partida` (en sustitución de un DTO `EstadoPartida` separado)**:
  - [x] *Getters* de solo lectura (`getJugadores()`, `getPosicionesJugadores()`, `getMurosColocados()`, `getMurosRestantesPorJugador()`) y actualización granular en `IQuoridorVista`.

### 2.2 Controlador (`org.quoridor.controller`)
- [x] **Clase `QuoridorControlador`** (implementa `IQuoridorControlador`):
  - [x] Métodos de orquestación: `iniciarJuego()`, `onCasillaClic(int x, int y)`, `onInterseccionClic(int x, int y, Orientacion orientacion)`, `onBotonReiniciar()`.
  - [x] Comunicación exclusiva mediante el Modelo (`Partida`) y la interfaz `IQuoridorVista`.

### 2.3 Vista y Arranque (`org.quoridor.view`, `org.quoridor.Main` y `org.quoridor.QuoridorApp`)
- [x] **Interfaces `IQuoridorVista` e `IQuoridorControlador`**:
  - [x] Métodos `setControlador`, `actualizarTablero`, `mostrarTurno`, `actualizarMurosRestantes`, `mostrarError`, `mostrarVictoria`, `limpiarMensajes`.
- [x] **Clase `VentanaPrincipalJavaFX`**:
  - [x] Implementación visual en JavaFX del tablero 9x9, peones, muros colocados, previsualización de muros, indicador de turno/muros restantes y captura de eventos de ratón.
- [x] **Clases `Main` y `QuoridorApp`**:
  - [x] Inicialización completa de `Partida`, `VentanaPrincipalJavaFX` y `QuoridorControlador` para arranque directo en Java 21 / JavaFX 21.

---

## 3. Metodología TDD y Flujo de Ramas

- [x] Desarrollar cada nueva funcionalidad, corrección o mejora en una rama independiente (`feature/<nombre>`, `fix/<nombre>`, `refactor/<nombre>`, `test/<nombre>`, `docs/<nombre>`) y fusionarla en `master` mediante Pull Request tras superar el CI.
- [x] Registrar en Git **mínimo 2 versiones del código de prueba y 2 versiones del código desarrollado** (`[Red]` $\rightarrow$ `[Green]` $\rightarrow$ `[Red v2]` $\rightarrow$ `[Green v2]`) para al menos el **90% de los métodos desarrollados** del Modelo y del Controlador (excluyendo *getters* y *setters* simples).
- [x] Documentar con Javadoc y comentarios explicativos tanto el código de producción (Modelo, Vista y Controlador) como todos los métodos de test (especificando en cada test la técnica aplicada, partición equivalente, valor límite/frontera, caja blanca, camino o iteraciones).

---

## 4. Programación por Contrato (Design by Contract)

Aplicar Programación por Contrato en al menos el **50% de las clases del Modelo** (implementado en 7/7 clases concretas del Modelo + `QuoridorControlador`):
- [x] **`Posicion`**: Precondiciones en métodos geométricos (`p != null`), postcondiciones en `esValida`, `distanciaManhattan`, `esAdyacente` y `desplazar`, e invariante de clase.
- [x] **`Muro`**: Invariante (`origen != null && orientacion != null`), precondiciones y postcondiciones en `esPosicionValida`, `colisionaCon` y `bloqueaPaso`.
- [x] **`Jugador`**: Invariante de clase (`murosRestantes >= 0 && posicionActual != null`), precondiciones (`hasMurosRestantes()` antes de `consumirMuro()`, `p != null` y válida en `moverA`) y postcondiciones (`murosRestantes == murosPrevios - 1`, `reiniciarEstado`).
- [x] **`Tablero`**: Invariante de clase (`murosColocados != null && murosColocados.size() <= 20`, colecciones y validador no nulos), precondiciones y postcondiciones al registrar muros, configurar tamaño/objetivos y validar caminos.
- [x] **`ValidadorCaminos`, `SorteadorTurno`, `Partida` y `QuoridorControlador`**: Precondiciones, postcondiciones e invariantes de clase verificadas con `-ea`.

---

## 5. Pruebas de Caja Negra

### 5.1 Particiones Equivalentes y Valores Límite / Frontera ($\ge 90\%$ en todos los métodos de clases de bajo nivel)
- [x] **Tests de `Posicion` (`PosicionTest.java`)**:
  - [x] `esValida(int limiteTablero)`: Particiones válidas/inválidas y valores límite (`0`, `8`) y frontera (`-1`, `1`, `7`, `9`), además de tableros `1x1`, `7x7`, `8x8` y límites `<= 0`.
  - [x] `distanciaManhattan(Posicion p)`: Particiones (misma casilla, ortogonal, diagonal, signos negativos, extremos opuestos) y límites.
  - [x] `esAdyacente(Posicion p)`: Particiones (distancia 0, distancia 1 ortogonal en las 4 direcciones, diagonal distancia 2) y valores límite/frontera.
  - [x] `desplazar(int dx, int dy)`: Particiones de desplazamiento positivo, negativo, mixto y nulo.
- [x] **Tests de `Muro` (`MuroTest.java`)**:
  - [x] `esPosicionValida(int limiteTablero)`: Particiones válidas (`0..7`), inválidas (`<0`, `>=8`), valores límite (`0`, `7`), frontera (`-1`, `1`, `6`, `8`) y tableros `2x2` / `7x7`.
  - [x] `colisionaCon(Muro otroMuro)`: Particiones (mismo origen y misma orientación, mismo origen y orientación perpendicular en cruz, solapamiento parcial en eje, cruces en T/L, muros disjuntos) y límites adyacentes (distancia `2`).
  - [x] `bloqueaPaso(Posicion origen, Posicion destino)`: Particiones para muros horizontales y verticales frente a pasos en las 4 direcciones, paralelos y desconectados.
- [x] **Tests de `Jugador` (`JugadorTest.java`)**:
  - [x] `moverA(Posicion p)`: Actualización de posición válida en esquinas/bordes y rechazo DbC de nulos y fuera de tablero.
  - [x] `consumirMuro()` y `hasMurosRestantes()`: Particiones (`>0`, `==0`) y valores límite/frontera (`10`, `9`, `5`, `1`, `0`).
  - [x] `haLlegadoAMeta()`: Comparación con `filaObjetivo` en metas extremas (`0`, `8`) y límites sobre meta intermedia (`filaObjetivo - 1`, `filaObjetivo`, `filaObjetivo + 1`).
- [x] **Tests de `SorteadorTurno` (`SorteadorTurnoTest.java`)**:
  - [x] `sortear(List<Jugador>)`: Particiones de `1`, `2` y `4` jugadores, estabilidad de rango en bucle y rechazo DbC de lista vacía o nula.

### 5.2 Pairwise Testing (Mínimo 1 método desarrollado)
- [x] Test *Pairwise* en `PartidaTest.testConfigurarPartidaPairwise` para `Partida.configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio)` cubriendo en 6 casos todas las parejas de valores entre los 4 parámetros (`numJugadores` $\in \{2, 4\}$, `tamanoTablero` $\in \{7, 9\}$, `murosPorJugador` $\in \{5, 10\}$, `sorteoAleatorio` $\in \{\text{true}, \text{false}\}$).

---

## 6. Pruebas de Caja Blanca

### 6.1 Statement Coverage (Todo el código)
- [x] Alcanzar y verificar mediante reporte JaCoCo actualizado (`target/site/jacoco/index.html`) el 100% de cobertura de sentencias (*Statement Coverage*) en todo el código del Modelo y Controlador.

### 6.2 Decision Coverage y Condition Coverage (Mínimo 3 métodos con $\ge 2$ condicionales)
- [x] **Método 1**: `Muro.colisionaCon(Muro otroMuro)` — cubiertas todas las decisiones y condiciones atómicas en `MuroTest.testColisionaCon`.
- [x] **Método 2**: `Tablero.esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente)` — cubiertas todas las decisiones y condiciones atómicas (paso simple, salto recto, saltos diagonales por borde/muro y bloqueo por muro intermedio) en `TableroTest.testEsMovimientoValido` y `TableroTest.testEsMovimientoValidoSaltoDiagonalCoberturaCondiciones`.
- [x] **Método 3**: `Tablero.esColocacionMuroValida(Muro muro)` — cubiertas todas las decisiones y condiciones atómicas en `TableroTest.testEsColocacionMuroValida` y `TableroTest.testRamasNulasPrecondicionesEInvariante`.

### 6.3 Path Coverage (Mínimo 2 métodos + Diagramas de Actividades UML)
- [x] **Método 1**: `Partida.comprobarVictoria()` — tests de los 4 caminos independientes en `PartidaTest.testComprobarVictoria` (diagrama de actividades UML pendiente para el informe en la sección 9).
- [x] **Método 2**: `Partida.colocarMuro(Muro m)` — tests de los 5 caminos independientes en `PartidaTest.testColocarMuro` (diagrama de actividades UML pendiente para el informe en la sección 9).

### 6.4 Loop Testing (Mínimo 2 bucles simples y 2 bucles anidados)
- [x] **Bucle Simple 1**: `Tablero.colisionaConMurosExistentes(Muro nuevoMuro)` (casos de `0, 1, 2, m, n-1, n` iteraciones en `TableroTest.testColisionaConMurosExistentes`).
- [x] **Bucle Simple 2**: `Tablero.hayCaminoParaTodos(List<Jugador> jugadores)` (casos de `0, 1, 2, n-1, n` jugadores en `TableroTest.testHayCaminoParaTodos`).
- [x] **Bucle Anidado 1**: `ValidadorCaminos.hayCaminoDisponible(...)` (bucle externo de cola BFS `0, 1, 2, m, n-1, n` + bucle interno de vecinos `0, 1, 2, 3, 4` en `ValidadorCaminosTest.testHayCaminoDisponible`).
- [x] **Bucle Anidado 2**: `ValidadorCaminos.calcularDistanciaMinima(...)` (bucle externo de cola BFS `0, 1, 2, m, n-1, n` + bucle interno de vecinos `0, 1, 2, 3, 4` en `ValidadorCaminosTest.testCalcularDistanciaMinima`).

---

## 7. Automatización (Data-Driven Testing)

Implementar pruebas *Data-Driven* (`@ParameterizedTest` con `@CsvFileSource` / `@CsvSource`) para al menos **2 métodos desarrollados de clases de bajo nivel**:
- [x] Archivo CSV de datos creado en `src/test/resources/data/movimientos_test.csv`.
- [x] **Método 1 de bajo nivel**: `Posicion.esAdyacente(Posicion p)` en `PosicionTest.testEsAdyacenteDataDriven` leyendo casos parametrizados desde CSV (`@CsvFileSource`).
- [x] **Método 2 de bajo nivel**: `Muro.bloqueaPaso(Posicion origen, Posicion destino)` en `MuroTest.testBloqueaPasoDataDriven` mediante `@ParameterizedTest` (`@CsvSource`).

---

## 8. Mock Objects (Mínimo 5 Mocks: >50% en el Modelo y $\ge 2$ Manuales)

- [x] **Mock 1 (Manual - Test del Modelo `Tablero`)**: `MockValidadorCaminos` en `TableroTest.java` implementando `IValidadorCaminos` para probar `Tablero.esColocacionMuroValida` aislando el BFS.
- [x] **Mock 2 (Manual - Test del Modelo `Partida`)**: `SorteadorTurnoFijoMock` en `PartidaTest.java` implementando `ISorteadorTurno` para fijar el turno inicial en `Partida.iniciarPartida()`.
- [x] **Mock 3 (Mockito - Test del Modelo `Partida`)**: Mock de `Tablero` en `PartidaTest.java` inyectado en `Partida` para verificar el flujo de turnos, movimientos y colocación de muros.
- [x] **Mock 4 (Mockito - Test del Modelo `Partida`)**: Mocks de `Jugador` en `PartidaTest.java` para simular disponibilidad de muros (`hasMurosRestantes()`) y llegada a meta (`haLlegadoAMeta()`).
- [x] **Mock 5 (Mockito - Test del Controlador `QuoridorControlador`)**: Mock de `IQuoridorVista` en `QuoridorControladorTest.java` para verificar la interacción del Controlador con la Vista (`setControlador`, `actualizarTablero`, `mostrarTurno`, `actualizarMurosRestantes`, `mostrarError`, `mostrarVictoria`, `limpiarMensajes`).

---

## 9. Documentación de Entrega (Campus Virtual)

- [ ] **Informe de Prácticas**:
  - [ ] Explicación detallada de todos los tipos de test realizados (TDD, Caja Negra, Programación por Contrato, Caja Blanca, Data-Driven, Pairwise y Mocks).
  - [ ] Capturas de pantalla con el porcentaje de *Statement Coverage* de cada uno de los métodos.
  - [ ] Capturas de pantalla en color (plugin EclEmma de Eclipse o reporte JaCoCo) de cada método o bucle analizado en *Decision Coverage*, *Condition Coverage*, *Path Coverage* y *Loop Testing*.
  - [ ] Diagramas de flujo (diagramas de actividades en UML) de cada uno de los métodos analizados en *Path Coverage*.
- [ ] **Hoja de Autoevaluación**:
  - [ ] Completar con los nombres de los integrantes del grupo (máximo 2 estudiantes), la propuesta de calificación y el enlace al repositorio privado de GitHub.
  - [ ] Realizar una única entrega por grupo antes del **30 de noviembre de 2026 a las 8:30**.
