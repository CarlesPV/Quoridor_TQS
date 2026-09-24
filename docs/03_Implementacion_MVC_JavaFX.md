# 03. Implementación MVC y JavaFX

## 1. Orquestación mediante el Controlador (Desacoplado)

El `QuoridorController` es el cerebro operativo. Su diseño asegura que no importe si la Vista es una aplicación de terminal, una web o JavaFX. 

**Flujo paso a paso:**
1.  **Inicialización**: En el `master`, se crea la instancia de `Partida` (Modelo). Se crea la instancia de `VentanaPrincipalJavaFX` (Vista). Se crea el `QuoridorController` pasando ambas dependencias al constructor.
2.  **Suscripción**: El Controlador llama a `vista.setControlador(this)` para que la Vista sepa a quién avisar.
3.  **Bucle de Eventos**: 
    - El usuario hace clic en la ventana. La Vista captura el evento de ratón de JavaFX (`MouseEvent`).
    - La Vista traduce las coordenadas del píxel a coordenadas lógicas (ej. clic en intersección X=3, Y=4, Horizontal).
    - La Vista llama a `controlador.onInterseccionClic(3, 4, HORIZONTAL)`.
4.  **Procesamiento**: El Controlador invoca a `modelo.colocarMuro(nuevoMuro)`. 
5.  **Actualización**: Si el Modelo lanza una excepción (ej. `MuroInvalidoException`), el controlador la captura y llama a `vista.mostrarError()`. Si tiene éxito, llama a `vista.mostrarEstado(modelo.generarEstadoDTO())`.

## 2. Renderizado del Estado en JavaFX

La Vista (`VentanaPrincipalJavaFX`) se actualizará en base al objeto `EstadoPartida` DTO.

- **Casillas y Jugadores**: Se puede utilizar un `Canvas` o un `GridPane`. Si usamos `Canvas`, la vista iterará sobre las posiciones de los jugadores en el DTO, calculando: `pixelX = posicion.X * (anchoCasilla + grosorMuro)`, y dibujará un círculo del color correspondiente.
- **Muros**: Basándose en la lista de `Muro` del DTO, la vista dibujará rectángulos.
  - Si un muro está en `X=1, Y=1` con orientación `HORIZONTAL`, se dibuja un rectángulo que cubra el ancho de las casillas `(1,1)` y `(2,1)`, posicionado en el espacio intersticial inferior.
- **Separación de Responsabilidades**: La Vista no calcula si un clic está a distancia válida; simplemente avisa "Clic en casilla 2,3". El renderizado es puramente visual.

## 3. Diseño de la Interfaz IQuoridorVista para Pruebas

Para mantener el principio de inversión de dependencias y facilitar el *mocking* en TDD, la interfaz es vital:

```java
public interface IQuoridorVista {
    // Para inyectar el manejador de eventos
    void setControlador(QuoridorController controlador);
    
    // Para actualizar la UI con la situación actual
    void mostrarEstado(EstadoPartida estado);
    
    // Para feedback al usuario
    void mostrarError(String mensaje);
    
    // Para indicar victoria
    void mostrarVictoria(String nombreGanador);
}
```
Durante las pruebas del Controlador, se utilizará un mock de `IQuoridorVista` (creado con Mockito o manualmente) para verificar que el Controlador invoca `mostrarEstado` tras un movimiento válido, sin necesidad de arrancar el motor de JavaFX.
