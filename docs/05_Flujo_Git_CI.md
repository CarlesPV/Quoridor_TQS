# 05. Flujo de Trabajo en Git, TDD e Integración Continua (CI)

Este documento define las pautas de control de versiones, flujo de desarrollo guiado por pruebas (TDD) y políticas de integración continua aplicadas en el proyecto.

---

## 1. Estrategia de Ramas (*Branching Strategy*)

El desarrollo se organiza mediante ramas funcionales independientes para mantener la estabilidad de la base de código:

- **Rama Principal (`master`)**:
  - Contiene exclusivamente código verificado y estable.
  - Está **protegida**: no se admiten commits directos. La incorporación de nuevo código se realiza únicamente mediante *Pull Requests* (PR).
- **Ramas de Desarrollo Independientes (`feature/**`, `fix/**`, `refactor/**`)**:
  - Cada nuevo desarrollo, corrección o mejora se crea de forma independiente en una rama nueva originada desde `master` y posteriormente se fusiona en `master` mediante Pull Request:
  - **`feature/<nombre>` (Nuevas funcionalidades y clases bajo TDD)**:
    - Uso: Implementación de nuevas clases, módulos o métodos junto con sus pruebas.
    - Ejemplos: `feature/posicion`, `feature/tablero-muros`, `feature/validacion-camino-bfs`.
  - **`fix/<nombre>` (Corrección de errores o fallos detectados)**:
    - Uso: Resolución de bugs en la lógica del juego, fallos en casos frontera o ajustes en el pipeline/configuración.
    - Ejemplos: `fix/colision-muros-cruzados`, `fix/salto-oponente-borde`, `fix/checkstyle-warnings`.
  - **`refactor/<nombre>` (Refactorización y mejora de calidad interna)**:
    - Uso: Optimización de código, extracción de métodos o mejoras de diseño y estilo sin alterar el comportamiento externo, manteniendo todos los tests en verde.
    - Ejemplos: `refactor/extraccion-validador-bfs`, `refactor/limpieza-controlador`.
  - Una vez completado el trabajo en cualquiera de estas ramas y superadas las validaciones del CI, se fusiona hacia `master` mediante Pull Request.

---

## 2. Pauta de Commits y Registro de TDD

Para registrar de manera fiable la evolución del diseño bajo TDD estricto (*Red-Green-Refactor*), los commits deben reflejar las diferentes iteraciones de cada método:

### Ciclo Iterativo (Mínimo 2 Versiones por Método)
Cada método desarrollado debe contar en el historial de Git con al menos dos iteraciones secuenciales tanto de sus pruebas como de su código de producción:

1. **Iteración 1 - Caso Base**:
   - `test: [Red] definir prueba inicial para <caso_de_uso>`  
     *(La prueba se escribe primero y falla o no compila).*
   - `feat: [Green] implementar lógica mínima para satisfacer <caso_de_uso>`  
     *(Código necesario y suficiente para hacer pasar la prueba).*

2. **Iteración 2 - Casos Límite / Robustez / Refactorización**:
   - `test: [Red v2] ampliar pruebas con valores límite o condiciones especiales`  
     *(Se añade un nuevo escenario de prueba que evalúa límites o excepciones).*
   - `feat: [Green v2] adaptar método para superar casos límite` o `refactor: optimizar estructura interna sin alterar comportamiento`  
     *(Se actualiza la lógica de producción para soportar el nuevo caso).*

---

## 3. Estándar de Documentación en Código

Tanto las pruebas como el código de producción deben incluir comentarios estructurados para facilitar la trazabilidad técnica:

### En el Código de Pruebas
Cada método de prueba debe indicar explícitamente la técnica de diseño aplicada:
- **Técnica empleada**: Partición Equivalente (válida o inválida), Valor Límite/Frontera, Caja Blanca (*Statement*, *Decision*, *Path Coverage*, *Loop Testing*), *Data-Driven Testing* o *Pairwise*.
- **Comportamiento esperado**: Breve descripción del resultado que se valida.

*Ejemplo:*
```java
// Prueba de Caja Negra: Análisis de Valores Límite
// Caso: Coordenada X inmediatamente fuera del borde superior derecho del tablero (x=9)
// Resultado esperado: esPosicionValida() retorna false
@Test
void testPosicionLimiteFueraTablero() {
    assertFalse(tablero.esPosicionValida(new Posicion(9, 0)));
}
```

### En el Código de Producción
- Código autoexplicativo y modular.
- Comentarios aclaratorios en métodos con lógica algorítmica compleja (como la validación de caminos con BFS o la intersección de muros).

---

## 4. Pull Requests y Políticas de Integración Continua (CI)

La validación de calidad está automatizada mediante el pipeline de GitHub Actions definido en `.github/workflows/ci.yml` sobre **JavaSE-21**:

1. **Disparadores del Pipeline**:
   - El flujo de CI (`build-test-quality`) se ejecuta automáticamente ante cualquier `push` en ramas de trabajo (`feature/**`, `fix/**`, `refactor/**`, `master`) y en la apertura o sincronización de un `Pull Request` hacia la rama principal `master`.

2. **Etapas de Verificación**:
   - **Compilación (`mvn -B clean test-compile`)**: Verificación de que tanto el código fuente como los tests compilan sin errores bajo JDK 21.
   - **Ejecución de Pruebas y Cobertura (`mvn -B test`)**: Ejecución completa de la suite JUnit 5 con aserciones activas (`-ea` para Programación por Contrato) y generación automática del reporte de cobertura de Caja Blanca con JaCoCo (`target/site/jacoco/`).
   - **Análisis de Calidad y Estilo (`mvn -B checkstyle:check`)**: Inspección estática de código mediante `checkstyle.xml` (con severidad `error` y `failsOnError=true`) para validar normas preestablecidas:
     - Longitud máxima de línea (`LineLength` máximo de 120 caracteres) y de método (`MethodLength`).
     - Convenciones de nomenclatura (`PackageName`, `TypeName`, `MethodName`, `MemberName`, `LocalVariableName`, `ConstantName`).
     - Reglas de estructura, uso obligatorio de llaves (`NeedBraces`), encapsulación (`VisibilityModifier`) y limpieza de importaciones (`UnusedImports`, `AvoidStarImport`).

3. **Política de Merge y Bloqueo en `master`**:
   - Si cualquiera de las acciones anteriores produce un error (fallo de compilación, test fallido o infracción en Checkstyle), el job `build-test-quality` termina con código de error y el Pull Request queda **bloqueado automáticamente** mediante las *Branch Protection Rules* de GitHub sobre `master` (*Require status checks to pass before merging*).
   - No se acepta ningún *merge* dentro de `master` hasta que todas las comprobaciones finalicen sin errores.
