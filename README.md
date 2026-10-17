# Quoridor (Java 21) - Proyecto TQS

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-000000?style=for-the-badge&logo=java&logoColor=white)
![Testing](https://img.shields.io/badge/TDD-Strict-brightgreen?style=for-the-badge)

Proyecto de implementación del juego de mesa **Quoridor** desarrollado en **Java 21** utilizando **JavaFX** para la interfaz gráfica. Este proyecto destaca por su riguroso enfoque en la calidad del software (TQS - Testing and Quality Software), aplicando de manera estricta el desarrollo guiado por pruebas (TDD), Programación por Contrato y el patrón arquitectónico Modelo-Vista-Controlador (MVC).

## 🏛️ Arquitectura: Modelo-Vista-Controlador (MVC)

El proyecto implementa un patrón MVC estricto para garantizar una alta cohesión y un bajo acoplamiento:

- **Modelo**: Contiene la lógica pura de negocio (reglas de movimiento, validación de muros, búsqueda de caminos y gestión de turnos). No posee dependencias de JavaFX ni del Controlador, operando únicamente con entidades de dominio.
- **Vista (Passive View)**: Implementada en JavaFX. Se encarga únicamente de renderizar la interfaz gráfica basándose en un DTO inmutable (`EstadoPartida`) y notificar interacciones del usuario. Actúa bajo el contrato de la interfaz `IQuoridorVista`.
- **Controlador (`QuoridorController`)**: Orquestador central. Recibe las interacciones de la Vista, verifica y actualiza el Modelo, y proporciona el nuevo estado actualizado a la Vista.

## 🗂️ Estructura del Proyecto

El código está estructurado alrededor de los siguientes componentes principales:

*   **Dominio/Modelo**: `Partida`, `Tablero`, `ValidadorCaminos` (`IValidadorCaminos`), `ISorteadorTurno`, `Jugador`, `Muro`, `Posicion`, `Color`, `Orientacion`.
*   **Presentación/Vista**: `VentanaPrincipalJavaFX`, `IQuoridorVista`.
*   **Control/Comunicación**: `QuoridorController`, `EstadoPartida` (DTO).

*Puedes consultar el [Diccionario de Clases](docs/02_Diccionario_Clases.md) y la [Arquitectura Global](docs/01_Arquitectura_Global.md) para más detalles técnicos.*

## 🧪 Estrategia de Pruebas y Calidad

El proyecto está diseñado para facilitar pruebas automatizadas exhaustivas, siguiendo estrictamente el ciclo de TDD (Red-Green-Refactor) a través de un historial de commits trazable.

### Técnicas Aplicadas
1. **Pruebas de Caja Negra**:
   - **Particiones Equivalentes y Valores Límite/Frontera**: Aplicados a los métodos de las clases base (`Posicion`, `Muro`, `Jugador`) para validar entradas dentro y fuera de los rangos del tablero.
   - **Pairwise Testing**: Combinación eficiente de parámetros en la configuración inicial de la partida (`Partida.configurarPartida`).
2. **Programación por Contrato (Design by Contract)**: Uso de precondiciones, postcondiciones e invariantes de clase en las entidades del Modelo para asegurar la integridad del estado.
3. **Pruebas de Caja Blanca**:
   - **Statement, Decision y Condition Coverage**: Verificación exhaustiva de sentencias, ramificaciones y condiciones compuestas en la lógica de validación de movimientos y muros.
   - **Path Coverage**: Evaluación de los caminos independientes en los flujos críticos de la partida, respaldados por diagramas de actividades UML.
   - **Loop Testing**: Pruebas sobre bucles simples y anidados, incluyendo el algoritmo BFS de búsqueda de caminos.
4. **Data-Driven Testing (Automatización)**: Empleo de `@ParameterizedTest`, `@CsvSource` y `@CsvFileSource` (JUnit 5) para validar múltiples escenarios espaciales de forma parametrizada.
5. **Estrategia de Mocking (Manuales y Mockito)**: Uso de *Mock Objects* tanto propios como con Mockito para aislar las dependencias del Modelo (ej. `Tablero`, `IValidadorCaminos`, `ISorteadorTurno`) y simular la interacción del `Controlador` con `IQuoridorVista`.

*Revisa la [Guía de Pruebas TDD](docs/04_Guia_Pruebas_TDD.md) y el [Flujo de Trabajo Git y CI](docs/05_Flujo_Git_CI.md) para más detalles sobre la metodología.*

## 🌿 Flujo de Trabajo y CI/CD

- **Estrategia de Ramas**: Cada desarrollo, corrección o refactorización se realiza en una rama independiente (`feature/<nombre>`, `fix/<nombre>`, `refactor/<nombre>`). La rama `master` está protegida y solo recibe cambios mediante *Pull Requests*.
- **TDD en Commits**: Historial de commits estructurado que refleja las iteraciones sucesivas del ciclo Red-Green-Refactor entre las pruebas y el código de producción.
- **Integración Continua (`.github/workflows/ci.yml`)**: Pipeline automatizado en GitHub Actions sobre JDK 21 que valida la compilación, ejecuta la suite de pruebas con cobertura JaCoCo y verifica la calidad de código mediante Checkstyle (`checkstyle.xml`). El *merge* a `master` queda bloqueado ante cualquier error.

## 📄 Documentación Completa
En la carpeta `docs` encontrarás toda la documentación técnica y de seguimiento del proyecto:
- [01. Arquitectura Global](docs/01_Arquitectura_Global.md)
- [02. Diccionario de Clases](docs/02_Diccionario_Clases.md)
- [03. Implementación MVC y JavaFX](docs/03_Implementacion_MVC_JavaFX.md)
- [04. Guía de Pruebas y TDD](docs/04_Guia_Pruebas_TDD.md)
- [05. Flujo de Trabajo Git, TDD y CI](docs/05_Flujo_Git_CI.md)
- [06. Checklist de Seguimiento del Proyecto](docs/06_Checklist_Proyecto.md)

