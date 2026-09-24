# 05. Flujo de Trabajo en Git, TDD e Integración Continua (CI)

Este documento define las pautas de control de versiones, flujo de desarrollo guiado por pruebas (TDD) y políticas de integración continua aplicadas en el proyecto.

---

## 1. Estrategia de Ramas (*Branching Strategy*)

El desarrollo se organiza mediante ramas funcionales independientes para mantener la estabilidad de la base de código:

- **Rama Principal (`master`)**:
  - Contiene exclusivamente código verificado y estable.
  - Está **protegida**: no se admiten commits directos. La incorporación de nuevo código se realiza únicamente mediante *Pull Requests* (PR).
- **Ramas de Funcionalidad (`feature/<nombre>`)**:
  - Cada clase, módulo o conjunto de métodos se implementa en una rama separada originada desde `master`.
  - Ejemplos de nomenclatura:
    - `feature/posicion`
    - `feature/tablero-muros`
    - `feature/validacion-camino-bfs`
  - Una vez finalizada la implementación y validadas las pruebas, se solicita la integración mediante un Pull Request hacia `master`.

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

La validación de calidad está automatizada mediante pipelines de CI (GitHub Actions):

1. **Disparadores del Pipeline**:
   - El flujo de CI se ejecuta automáticamente ante cualquier `push` y en la apertura o actualización de un `Pull Request` hacia la rama `master`.

2. **Etapas de Verificación**:
   - **Compilación**: Verificación de que el proyecto compila limpiamente.
   - **Ejecución de Pruebas**: Ejecución completa de la suite de pruebas unitarias (`mvn test` o `gradle test`).
   - **Análisis de Calidad y Estilo**: Inspección estática de código (mediante herramientas como Checkstyle) para asegurar el cumplimiento de estándares, tales como:
     - Longitud máxima por línea de código.
     - Convenciones de nomenclatura y formato.
     - Ausencia de advertencias críticas o dependencias no resueltas.

3. **Política de Merge**:
   - Si cualquiera de los pasos anteriores falla (pruebas no superadas o infracción de normas de calidad), el Pull Request queda **bloqueado automáticamente**.
   - No se permite fusionar (*merge*) en `master` hasta que todos los chequeos concluyan con éxito.
