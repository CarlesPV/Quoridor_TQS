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
- [ ] Configurar en los ajustes del repositorio de GitHub la regla de protección de la rama `master` (*Branch Protection Rule*: activar *"Require a pull request before merging"* y *"Require status checks to pass before merging"* seleccionando el job `Compilación, Tests y Checkstyle (Java 21)`).
- [ ] Verificar que el repositorio en GitHub sea **privado** e invitar únicamente al profesor de prácticas correspondiente (`@openacchio` o `@darece`).

---

## 2. Arquitectura MVC e Implementación de Clases

### 2.1 Modelo (`org.quoridor.model`)
- [x] Enumerador `Color` (`ROJO`, `AZUL`, `VERDE`, `AMARILLO`).
- [x] Enumerador `Orientacion` (`HORIZONTAL`, `VERTICAL`).
- [ ] **Clase de bajo nivel `Posicion`**:
  - [ ] Atributos inmutables `x`, `y`, constructor y *getters*.
  - [ ] Métodos de lógica propia: `esValida(int limiteTablero)`, `distanciaManhattan(Posicion otra)`, `esAdyacente(Posicion otra)`, `desplazar(int dx, int dy)`, `equals(Object o)`, `hashCode()`.
- [ ] **Clase de bajo nivel `Muro`**:
  - [ ] Atributos `Posicion origen` y `Orientacion orientacion`.
  - [ ] Métodos de lógica propia: `esPosicionValida(int limiteTablero)`, `colisionaCon(Muro otro)`, `bloqueaPaso(Posicion origen, Posicion destino)`.
- [ ] **Clase de bajo nivel `Jugador`**:
  - [ ] Atributos `nombre`, `color`, `posicionActual`, `filaObjetivo`, `murosRestantes`.
  - [ ] Métodos de lógica propia: `moverA(Posicion p)`, `consumirMuro()`, `hasMurosRestantes()`, `haLlegadoAMeta()`, `reiniciarEstado(Posicion origen, int murosIniciales)`.
- [ ] **Componente de validación de caminos (`IValidadorCaminos` y `ValidadorCaminos`)**:
  - [ ] Métodos `hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero)` (algoritmo BFS) y `construirMatrizVisitados` / `calcularDistanciaMinima`.
- [ ] **Clase `Tablero`**:
  - [ ] Gestión de `murosColocados`, posiciones y `IValidadorCaminos`.
  - [ ] Métodos: `esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente)`, `esColocacionMuroValida(Muro muro)`, `colisionaConMurosExistentes(Muro nuevoMuro)`, `colocarMuro(Muro muro)`, `hayCaminoParaTodos(List<Jugador> jugadores)`.
- [ ] **Interfaz `ISorteadorTurno` y Fachada `Partida`**:
  - [ ] Métodos: `configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio)`, `iniciarPartida()`, `realizarMovimiento(Posicion p)`, `colocarMuro(Muro m)`, `comprobarVictoria()`, `generarEstadoDTO()`.
- [ ] **DTO `EstadoPartida`**:
  - [ ] Estructura inmutable para transferir el estado del Modelo a la Vista sin exponer referencias internas.

### 2.2 Controlador (`org.quoridor.controller`)
- [ ] **Clase `QuoridorController`**:
  - [ ] Métodos de orquestación: `onCasillaClic(int x, int y)`, `onInterseccionClic(int x, int y, Orientacion o)`, `onBotonReiniciar()`.
  - [ ] Comunicación exclusiva mediante el Modelo (`Partida`) y la interfaz `IQuoridorVista`.

### 2.3 Vista y Arranque (`org.quoridor.view` y `org.quoridor.Main`)
- [ ] **Interfaz `IQuoridorVista`**:
  - [ ] Métodos `setControlador`, `mostrarEstado`, `mostrarError`, `mostrarVictoria`.
- [ ] **Clase `VentanaPrincipalJavaFX`**:
  - [ ] Implementación visual en JavaFX del tablero 9x9, peones, muros colocados, indicador de turno/muros restantes y captura de eventos de ratón.
- [ ] **Clase `Main`**:
  - [ ] Inicialización completa de `Partida`, `VentanaPrincipalJavaFX` y `QuoridorController` verificando que la aplicación se ejecute de principio a fin sin errores.

---

## 3. Metodología TDD y Flujo de Ramas

- [ ] Desarrollar cada nueva funcionalidad, corrección o mejora en una rama independiente (`feature/<nombre>`, `fix/<nombre>` o `refactor/<nombre>`) y fusionarla en `master` mediante Pull Request tras superar el CI.
- [ ] Registrar en Git **mínimo 2 versiones del código de prueba y 2 versiones del código desarrollado** (`[Red]` $\rightarrow$ `[Green]` $\rightarrow$ `[Red v2]` $\rightarrow$ `[Green v2]`) para al menos el **90% de los métodos desarrollados** del Modelo y del Controlador (excluyendo *getters* y *setters* simples).
- [ ] Documentar con comentarios explicativos tanto el código desarrollado como todos los métodos de test (especificando en cada test qué caso se comprueba, si es partición equivalente, valor límite/frontera, caja blanca, etc.).

---

## 4. Programación por Contrato (Design by Contract)

Aplicar Programación por Contrato en al menos el **50% de las clases del Modelo** (4 de las 6 clases del Modelo = 66.7%):
- [ ] **`Posicion`**: Precondiciones en métodos geométricos (`otra != null`, `limiteTablero > 0`) y postcondiciones en `desplazar` / `distanciaManhattan`.
- [ ] **`Muro`**: Invariante (`origen != null && orientacion != null`), precondiciones y postcondiciones en `esPosicionValida`, `colisionaCon` y `bloqueaPaso`.
- [ ] **`Jugador`**: Invariante de clase (`murosRestantes >= 0 && posicionActual != null`), precondiciones (`hasMurosRestantes()` antes de `consumirMuro()`, `destino != null` en `moverA`) y postcondiciones (`murosRestantes == murosPrevios - 1`).
- [ ] **`Tablero`**: Invariante de clase (`murosColocados != null && murosColocados.size() <= 20`), precondiciones y postcondiciones al registrar muros y movimientos.

---

## 5. Pruebas de Caja Negra

### 5.1 Particiones Equivalentes y Valores Límite / Frontera ($\ge 90\%$ en todos los métodos de clases de bajo nivel)
- [ ] **Tests de `Posicion` (`PosicionTest.java`)**:
  - [ ] `esValida(int limiteTablero)`: Particiones válidas/inválidas y valores límite (`0`, `8`) y frontera (`-1`, `1`, `7`, `9`).
  - [ ] `distanciaManhattan(Posicion otra)`: Particiones (misma casilla, ortogonal, diagonal, extremos opuestos) y límites.
  - [ ] `esAdyacente(Posicion otra)`: Particiones (distancia 0, distancia 1 ortogonal, diagonal distancia 2, saltos mayores) y valores límite/frontera.
  - [ ] `desplazar(int dx, int dy)`: Particiones de desplazamiento positivo, negativo y nulo.
- [ ] **Tests de `Muro` (`MuroTest.java`)**:
  - [ ] `esPosicionValida(int limiteTablero)`: Particiones válidas (`0..7`), inválidas (`<0`, `>=8`) y valores límite (`0`, `7`) y frontera (`-1`, `1`, `6`, `8`).
  - [ ] `colisionaCon(Muro otro)`: Particiones (mismo origen y misma orientación, mismo origen y orientación perpendicular en cruz, solapamiento parcial en eje, muros disjuntos) y límites adyacentes.
  - [ ] `bloqueaPaso(Posicion origen, Posicion destino)`: Particiones para muros horizontales y verticales frente a pasos en las 4 direcciones.
- [ ] **Tests de `Jugador` (`JugadorTest.java`)**:
  - [ ] `moverA(Posicion p)`: Actualización de posición válida y rechazo de nulos.
  - [ ] `consumirMuro()` y `hasMurosRestantes()`: Particiones (`>0`, `==0`) y valores límite/frontera (`10`, `9`, `1`, `0`).
  - [ ] `haLlegadoAMeta()`: Comparación con `filaObjetivo` en límites (`filaObjetivo - 1`, `filaObjetivo`, `filaObjetivo + 1`).

### 5.2 Pairwise Testing (Mínimo 1 método desarrollado)
- [ ] Implementar test *Pairwise* para `Partida.configurarPartida(int numJugadores, int tamanoTablero, int murosPorJugador, boolean sorteoAleatorio)` cubriendo todas las parejas de valores entre los 4 parámetros (`numJugadores` $\in \{2, 4\}$, `tamanoTablero` $\in \{7, 9\}$, `murosPorJugador` $\in \{5, 10\}$, `sorteoAleatorio` $\in \{\text{true}, \text{false}\}$).

---

## 6. Pruebas de Caja Blanca

### 6.1 Statement Coverage (Todo el código)
- [ ] Alcanzar cobertura de sentencias (*Statement Coverage*) en todo el código del Modelo y Controlador.

### 6.2 Decision Coverage y Condition Coverage (Mínimo 3 métodos con $\ge 2$ condicionales)
- [ ] **Método 1**: `Muro.colisionaCon(Muro otro)` — cubrir todas las decisiones y condiciones atómicas.
- [ ] **Método 2**: `Tablero.esMovimientoValido(Posicion origen, Posicion destino, Posicion oponente)` — cubrir todas las decisiones y condiciones atómicas.
- [ ] **Método 3**: `Tablero.esColocacionMuroValida(Muro muro)` — cubrir todas las decisiones y condiciones atómicas.

### 6.3 Path Coverage (Mínimo 2 métodos + Diagramas de Actividades UML)
- [ ] **Método 1**: `Partida.comprobarVictoria()` — diseñar tests para todos los caminos independientes y elaborar su **diagrama de flujo (diagrama de actividades en UML)**.
- [ ] **Método 2**: `Partida.colocarMuro(Muro m)` — diseñar tests para todos los caminos independientes y elaborar su **diagrama de flujo (diagrama de actividades en UML)**.

### 6.4 Loop Testing (Mínimo 2 bucles simples y 2 bucles anidados)
- [ ] **Bucle Simple 1**: `Tablero.colisionaConMurosExistentes(Muro nuevoMuro)` (casos de 0, 1, 2, $m$ y $n$ iteraciones).
- [ ] **Bucle Simple 2**: `Tablero.hayCaminoParaTodos(List<Jugador> jugadores)` (casos de 0, 1, 2 y 4 jugadores).
- [ ] **Bucle Anidado 1**: `ValidadorCaminos.hayCaminoDisponible(...)` (bucle externo de cola BFS + bucle interno de vecinos ortogonales).
- [ ] **Bucle Anidado 2**: `ValidadorCaminos.construirMatrizVisitados(...)` / `Tablero.obtenerEstadoCasillas()` (doble bucle anidado filas $\times$ columnas).

---

## 7. Automatización (Data-Driven Testing)

Implementar pruebas *Data-Driven* (`@ParameterizedTest` con `@CsvFileSource` / `@CsvSource`) para al menos **2 métodos desarrollados de clases de bajo nivel**:
- [x] Archivo CSV de datos creado en `src/test/resources/data/movimientos_test.csv` (completar filas de casos según se implemente el método).
- [ ] **Método 1 de bajo nivel**: `Posicion.esAdyacente(Posicion otra)` en `PosicionTest.java` leyendo casos parametrizados desde CSV.
- [ ] **Método 2 de bajo nivel**: `Muro.bloqueaPaso(Posicion origen, Posicion destino)` en `MuroTest.java` mediante `@ParameterizedTest`.

---

## 8. Mock Objects (Mínimo 5 Mocks: >50% en el Modelo y $\ge 2$ Manuales)

- [ ] **Mock 1 (Manual - Test del Modelo `Tablero`)**: `MockValidadorCaminos` implementando `IValidadorCaminos` para probar `Tablero.esColocacionMuroValida` aislando el BFS.
- [ ] **Mock 2 (Manual - Test del Modelo `Partida`)**: `MockSorteadorTurno` implementando `ISorteadorTurno` para fijar el turno inicial en `Partida.iniciarPartida()`.
- [ ] **Mock 3 (Mockito - Test del Modelo `Partida`)**: Mock de `Tablero` inyectado en `Partida` para verificar el flujo de turnos, movimientos y colocación de muros.
- [ ] **Mock 4 (Mockito - Test del Modelo `Partida` / `Tablero`)**: Mock de `Jugador` para simular disponibilidad de muros (`hasMurosRestantes()`) y llegada a meta (`haLlegadoAMeta()`).
- [ ] **Mock 5 (Mockito / Spy - Test del Controlador `QuoridorController`)**: Mock de `IQuoridorVista` en `PartidaControllerTest.java` para verificar la interacción del Controlador con la Vista (`mostrarEstado`, `mostrarError`, `mostrarVictoria`).

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
