package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class ValidadorCaminosTest {
	
	private ValidadorCaminos validador;
    private int TAMANO_TABLERO = 9;

	@BeforeEach
	void setUp() throws Exception {
		validador = new ValidadorCaminos();
	}

	@Test
	void testHayCaminoDisponible() {
		Posicion inicio = new Posicion(4, 0);
        int filaObjetivo = 8;
        List<Muro> muros = new ArrayList<>();

        boolean resultado = validador.hayCaminoDisponible(inicio, filaObjetivo, muros, TAMANO_TABLERO);
        
        assertTrue(resultado, "Debería haber camino libre si no hay muros.");
	}

	@Test
	void testCalcularDistanciaMinima() {
		// Caso sin muros
		Posicion inicio = new Posicion(4, 0);
        int filaObjetivo = 8;
        List<Muro> muros = new ArrayList<>();
        int distancia = validador.calcularDistanciaMinima(inicio, filaObjetivo, muros, TAMANO_TABLERO);
        assertEquals(8, distancia, "La distancia mínima en línea recta desde la fila 0 hasta la 8 debe ser 8.");
        
        // Caso muro en medio
        muros.add(new Muro(new Posicion(3, 6), Orientacion.HORIZONTAL));
        distancia = validador.calcularDistanciaMinima(inicio, filaObjetivo, muros, TAMANO_TABLERO); 
        assertEquals(9, distancia, "La distancia mínima en línea recta desde la fila 0 hasta la 8 con una desviación debe ser 9.");
        
        // Caso distancia 0
        distancia = validador.calcularDistanciaMinima(inicio, 0, muros, TAMANO_TABLERO); 
        assertEquals(0, distancia, "La distancia mínima para llegar a la misma fila en la que comienza debe ser 0.");
        
        // Caso encerrar jugador
        muros.add(new Muro(new Posicion(3, 0), Orientacion.VERTICAL));
        muros.add(new Muro(new Posicion(4, 0), Orientacion.VERTICAL));
        muros.add(new Muro(new Posicion(3, 1), Orientacion.HORIZONTAL));
        distancia = validador.calcularDistanciaMinima(inicio, filaObjetivo, muros, TAMANO_TABLERO); 
        assertEquals(-1, distancia, "La distancia mínima debe ser -1 ya que no existe el camino.");
        
		// Caso posicion inicial null
		try
		{
			validador.calcularDistanciaMinima(null, filaObjetivo, muros, TAMANO_TABLERO);
			assertTrue(false);
		}
		catch (AssertionError e) {}
		
		// Caso filaObjetivo más grande que tamano_tablero
		try
		{
			validador.calcularDistanciaMinima(inicio, 9, muros, TAMANO_TABLERO);
			assertTrue(false);
		}
		catch (AssertionError e) {}
		
		// Caso filaObjetivo más pequeño que 0
		try
		{
			validador.calcularDistanciaMinima(inicio, -1, muros, TAMANO_TABLERO);
			assertTrue(false);
		}
		catch (AssertionError e) {}
	}

	@Test
	void testObtenerVecinosValidos() {
		// Caso sin restricciones
		Posicion p = new Posicion(2, 2);
		List<Muro> muros = new ArrayList<>();
		List<Posicion> vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
		Posicion[] esperados = {
			    new Posicion(2, 1), // Arriba
			    new Posicion(2, 3), // Abajo
			    new Posicion(1, 2), // Izquierda
			    new Posicion(3, 2)  // Derecha
			};
		for (int i = 0; i < vecinos.size(); i++) {
	        assertEquals(esperados[i].getX(), vecinos.get(i).getX());
	        assertEquals(esperados[i].getY(), vecinos.get(i).getY());
	    }
		
		// Caso con restricciones límites negativos y con muros
		p = new Posicion(0, 0);
		Muro m = new Muro(new Posicion(0,0), Orientacion.HORIZONTAL);
		muros.add(m);
		vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
		Posicion esperados2 = new Posicion(1, 0); // Derecha
	    assertEquals(esperados2.getX(), vecinos.get(0).getX());
	    assertEquals(esperados2.getY(), vecinos.get(0).getY());
	    
		// Caso con restricciones límites mayores a tamano_tablero
		p = new Posicion(8, 8);
		vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
		Posicion[] esperados3 = {
			    new Posicion(8, 7), // Arriba
			    new Posicion(7, 8), // Izquierda
			};
		for (int i = 0; i < vecinos.size(); i++) {
	        assertEquals(esperados3[i].getX(), vecinos.get(i).getX());
	        assertEquals(esperados3[i].getY(), vecinos.get(i).getY());
	    }
		
		// Caso posicion actual null
		try
		{
			validador.obtenerVecinosValidos(null, muros, TAMANO_TABLERO);
			assertTrue(false);
		}
		catch (AssertionError e) {}
		
	}

	@Test
	void testEstaBloqueadoPorMuro() {
		// Caso bloqueado horizontalmente
		Posicion oP = new Posicion(4,0);
		Posicion dP = new Posicion(4,1);
		Muro m = new Muro(new Posicion(3,0), Orientacion.HORIZONTAL);
		List<Muro> muros = new ArrayList<>(List.of(m));
		assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));
		
		// Caso sin estar bloqueado
		oP = new Posicion(4,1);
		dP = new Posicion(5,1);
		assertFalse(validador.estaBloqueadoPorMuro(oP, dP, muros));
		
		// Caso bloqueado verticalmente
		m = new Muro(new Posicion(4,1), Orientacion.VERTICAL);
		muros.add(m);
		assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));
		
		// Caso posicion origen null
		try
		{
			validador.estaBloqueadoPorMuro(null, dP, muros);
			assertTrue(false);
		}
		catch (AssertionError e) {}
		
		// Caso posicion destino null
		try
		{
			validador.estaBloqueadoPorMuro(oP, null, muros);
			assertTrue(false);
		}
		catch (AssertionError e) {}
	}

}
