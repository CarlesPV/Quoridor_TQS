# Quoridor (Java) - Proyecto TQS

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-000000?style=for-the-badge&logo=java&logoColor=white)
![Testing](https://img.shields.io/badge/TDD-Strict-brightgreen?style=for-the-badge)

Proyecto de implementación del juego de mesa **Quoridor** desarrollado en Java utilizando **JavaFX** para la interfaz gráfica. Este proyecto destaca por su riguroso enfoque en la calidad del software (TQS - Testing and Quality Software), aplicando de manera estricta el desarrollo guiado por pruebas (TDD) y el patrón arquitectónico Modelo-Vista-Controlador (MVC).

## 🏛️ Arquitectura: Modelo-Vista-Controlador (MVC)

El proyecto implementa un patrón MVC estricto para garantizar una alta cohesión y un bajo acoplamiento:

- **Modelo**: Contiene la lógica pura de negocio (reglas de movimiento, validación de muros, gestión de turnos). No posee dependencias de JavaFX ni del Controlador, operando únicamente con coordenadas espaciales (`Posicion`).
- **Vista (Passive View)**: Implementada en JavaFX. Se encarga únicamente de renderizar la interfaz gráfica basándose en un DTO inmutable (`EstadoPartida`) y notificar interacciones del usuario. Actúa bajo el contrato de la interfaz `IQuoridorVista`.
- **Controlador (`QuoridorController`)**: Orquestador central. Recibe las interacciones de la Vista, verifica y actualiza el Modelo, y proporciona el nuevo estado actualizado a la Vista.

## 🗂️ Estructura del Proyecto

El código está estructurado alrededor de los siguientes componentes principales:

*   **Dominio/Modelo**: `Partida`, `Tablero`, `Jugador`, `Muro`, `Posicion`.
*   **Presentación/Vista**: `VentanaPrincipalJavaFX`, `IQuoridorVista`.
*   **Control/Comunicación**: `QuoridorController`, `EstadoPartida` (DTO).

*Puedes consultar el [Diccionario de Clases](docs/02_Diccionario_Clases.md) y la [Arquitectura Global](docs/01_Arquitectura_Global.md) para más detalles técnicos.*

## 🧪 Estrategia de Pruebas y TDD

El proyecto está diseñado de forma *Data-Driven* para facilitar pruebas automatizadas exhaustivas, siguiendo estrictamente el ciclo de TDD (Red-Green-Refactor) a través de un historial de commits claro.

### Tipos de Pruebas Aplicadas
1. **Pruebas de Caja Negra**: Particiones equivalentes y análisis de valores límite, aplicados a clases base como `Posicion` y `Muro`.
2. **Pruebas de Caja Blanca**:
   - **Path Coverage**: Evaluación de los distintos caminos lógicos en el gestor de partidas (ej. `comprobarVictoria`).
   - **Loop Testing**: Evaluación del algoritmo de búsqueda de caminos (BFS/DFS en `hayCaminoPosible`).
3. **Data-Driven Testing**: Empleo intensivo de `@ParameterizedTest` y `@CsvSource` (JUnit 5) para validar rápidamente múltiples coordenadas espaciales.
4. **Pairwise Testing**: Utilizado para la configuración inicial combinando de forma eficiente parámetros como el número de jugadores y muros restantes.
5. **Estrategia de Mocking (Mockito / Manual)**: Múltiples Mocks enfocados en el aislamiento unitario (ej. mockear `Tablero` al probar `Partida`, o mockear `IQuoridorVista` en el `Controlador`).

*Revisa la [Guía de Pruebas TDD](docs/04_Guia_Pruebas_TDD.md) y el [Flujo de Trabajo Git y CI](docs/05_Flujo_Git_CI.md) para más detalles sobre la metodología.*

## 🌿 Flujo de Trabajo y CI/CD

- **Estrategia de Ramas**: Cada desarrollo se realiza en una rama independiente (`feature/<nombre>`). La rama `master` está protegida y solo recibe cambios mediante *Pull Requests*.
- **TDD en Commits**: Historial de commits estructurado que refleja las iteraciones del ciclo Red-Green-Refactor (con versiones sucesivas de pruebas y código de producción).
- **Integración Continua**: Pipeline automatizado que valida la compilación, ejecuta la suite de pruebas y analiza la calidad/estilo de código (ej. Checkstyle). El *merge* a `master` queda bloqueado si algún paso no concluye con éxito.

## 📄 Documentación Completa
En la carpeta `docs` encontrarás toda la documentación técnica del proyecto:
- [01. Arquitectura Global](docs/01_Arquitectura_Global.md)
- [02. Diccionario de Clases](docs/02_Diccionario_Clases.md)
- [03. Implementación MVC y JavaFX](docs/03_Implementacion_MVC_JavaFX.md)
- [04. Guía de Pruebas y TDD](docs/04_Guia_Pruebas_TDD.md)
- [05. Flujo de Trabajo Git, TDD y CI](docs/05_Flujo_Git_CI.md)
