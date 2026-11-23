package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
	
	private List<Muro> construirLaberintoSinMeta(int tamano) {
		List<Muro> muros = new ArrayList<>();
		int filaBloqueo = tamano - 2;
		for (int x = 0; x < tamano - 1; x += 2) {
			muros.add(new Muro(new Posicion(x, filaBloqueo), Orientacion.HORIZONTAL));
		}
		// Asegura cubrir también la última columna (tamano - 1) si tamano es impar (ej. x = 7 en 9x9)
		muros.add(new Muro(new Posicion(tamano - 2, filaBloqueo), Orientacion.HORIZONTAL));
		return muros;
	}
	
	/**
	 * Loop Testing — Bucle Anidado 1 (ValidadorCaminos.hayCaminoDisponible):
	 * - Bucle interno (vecinos válidos / muros): 0, 1, 2, 3 y 4 vecinos válidos.
	 * - Bucle externo (cola BFS): 0, 1, 2, m, n-1 y n iteraciones / recorrido amplio del tablero.
	 */
	@Test
	@DisplayName("Loop Testing anidado en hayCaminoDisponible (cola BFS y vecinos válidos)")
	void testHayCaminoDisponible() {
		// --- Bucle externo: 0 iteraciones (inicio.getY() == filaObjetivo) ---
		Posicion inicio = new Posicion(4, 0);
		List<Muro> muros = new ArrayList<>();
		assertTrue(validador.hayCaminoDisponible(inicio, 0, muros, TAMANO_TABLERO),
				"0 iteraciones: ya está en la fila objetivo.");

		// --- Bucle interno: 0 vecinos válidos (esquina (0,0) cerrada por abajo y derecha) ---
		// y Bucle externo: 1 iteración (extrae (0,0), sin vecinos válidos, cola vacía -> false)
		List<Muro> murosEsquinaCerrada = List.of(
				new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
				new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
		assertFalse(validador.hayCaminoDisponible(new Posicion(0, 0), 8, murosEsquinaCerrada, TAMANO_TABLERO),
				"0 vecinos válidos / 1 iteración: esquina (0,0) cerrada debe retornar false.");

		// --- Bucle interno: 1 vecino válido ---
		// Desde (0,0) con HORIZONTAL en (0,0) (bloquea abajo) y VERTICAL en (0,0) (bloquea derecha):
		// si quitamos VERTICAL en (0,0) y bloqueamos (0,0)->(0,1) con HORIZONTAL en (0,0),
		// desde (0,0) solo puede ir a (1,0), y desde ahí alcanzar fila 0.
		List<Muro> unVecinoMuros = List.of(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL));
		assertTrue(validador.hayCaminoDisponible(new Posicion(0, 0), 0, unVecinoMuros, TAMANO_TABLERO),
				"1 vecino válido: camino disponible desde (0,0) hacia fila 0.");

		// --- Bucle interno: 2 vecinos válidos ---
		// Esquina (8,8) sin muros tiene 2 vecinos válidos: (8,7) y (7,8)
		assertTrue(validador.hayCaminoDisponible(new Posicion(8, 8), 7, muros, TAMANO_TABLERO),
				"2 vecinos válidos: desde esquina (8,8) hacia fila 7.");

		// --- Bucle interno: 3 vecinos válidos ---
		// Borde superior (4,0) sin muros tiene 3 vecinos válidos: (4,1), (3,0), (5,0)
		assertTrue(validador.hayCaminoDisponible(new Posicion(4, 0), 1, muros, TAMANO_TABLERO),
				"3 vecinos válidos: desde borde (4,0) hacia fila 1.");

		// --- Bucle interno: 4 vecinos válidos ---
		// Casilla central (4,4) sin muros tiene 4 vecinos válidos: arriba, abajo, izquierda, derecha
		assertTrue(validador.hayCaminoDisponible(new Posicion(4, 4), 5, muros, TAMANO_TABLERO),
				"4 vecinos válidos: desde casilla central (4,4) hacia fila 5.");

		// --- Bucle externo: 1 iteración con meta a 1 paso ---
		assertTrue(validador.hayCaminoDisponible(new Posicion(4, 7), 8, muros, TAMANO_TABLERO),
				"1 iteración: meta a 1 paso desde (4,7) hasta fila 8.");

		// --- Bucle externo: 2 iteraciones (meta a 2 pasos / espacio reducido de 2 casillas) ---
		assertTrue(validador.hayCaminoDisponible(new Posicion(4, 6), 8, muros, TAMANO_TABLERO),
				"2 iteraciones: meta a 2 pasos desde (4,6) hasta fila 8.");

		// Espacio aislado de 2 casillas ((0,0) y (1,0)):
		// - Arriba e izquierda: bordes del tablero (y=0, x=0)
		// - Abajo de (0,0) y (1,0): HORIZONTAL en (0,0)
		// - Derecha de (1,0): VERTICAL en (1,0)
		List<Muro> espacioDosCasillas = List.of(
				new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
				new Muro(new Posicion(1, 0), Orientacion.VERTICAL));
		assertFalse(validador.hayCaminoDisponible(new Posicion(0, 0), 8, espacioDosCasillas, TAMANO_TABLERO),
				"2 iteraciones en bolsa cerrada de 2 casillas: debe retornar false.");

		// --- Bucle externo: m iteraciones (desvío intermedio por muros) ---
		List<Muro> murosDesvio = new ArrayList<>();
		murosDesvio.add(new Muro(new Posicion(3, 4), Orientacion.HORIZONTAL));
		murosDesvio.add(new Muro(new Posicion(5, 4), Orientacion.HORIZONTAL));
		assertTrue(validador.hayCaminoDisponible(new Posicion(4, 0), 8, murosDesvio, TAMANO_TABLERO),
				"m iteraciones: debe rodear muros intermedios y alcanzar fila 8.");

		// --- Bucle externo: n-1 / n iteraciones (recorrido amplio sin acceso a fila meta) ---
		List<Muro> laberintoAmplio = construirLaberintoSinMeta(TAMANO_TABLERO);
		assertFalse(validador.hayCaminoDisponible(new Posicion(4, 0), 8, laberintoAmplio, TAMANO_TABLERO),
				"n-1 / n iteraciones: recorrido amplio sin acceso a fila meta debe retornar false.");

		// --- Precondiciones DbC con assertThrows ---
		assertThrows(AssertionError.class,
				() -> validador.hayCaminoDisponible(null, 8, muros, TAMANO_TABLERO));
		assertThrows(AssertionError.class,
				() -> validador.hayCaminoDisponible(inicio, TAMANO_TABLERO, muros, TAMANO_TABLERO));
		assertThrows(AssertionError.class,
				() -> validador.hayCaminoDisponible(inicio, -1, muros, TAMANO_TABLERO));
	}

	/**
	 * Loop Testing — Bucle Anidado 2 (ValidadorCaminos.calcularDistanciaMinima):
	 * - 0 iteraciones (distancia == 0).
	 * - 1 iteración (distancia == 1 o -1 en celda aislada).
	 * - 2 iteraciones (distancia == 2 o -1 en bolsa cerrada de 2 casillas).
	 * - m iteraciones (camino con rodeo).
	 * - n-1 / n iteraciones (recorrido amplio del tablero).
	 */
	@Test
	@DisplayName("Loop Testing anidado en calcularDistanciaMinima (cola BFS y vecinos válidos)")
	void testCalcularDistanciaMinima() {
		Posicion inicio = new Posicion(4, 0);
		List<Muro> muros = new ArrayList<>();

		// --- Bucle externo: 0 iteraciones (distancia == 0) ---
		assertEquals(0, validador.calcularDistanciaMinima(inicio, 0, muros, TAMANO_TABLERO),
				"0 iteraciones: misma fila inicial debe retornar distancia 0.");

		// --- Bucle interno: 0 vecinos válidos / 1 iteración (-1 en celda aislada) ---
		List<Muro> murosAislado = List.of(
				new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
				new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
		assertEquals(-1, validador.calcularDistanciaMinima(new Posicion(0, 0), 8, murosAislado, TAMANO_TABLERO),
				"0 vecinos válidos / 1 iteración en celda aislada debe retornar -1.");

		// --- Bucle interno: 1 vecino válido ---
		// Desde (0,0) con HORIZONTAL en (0,0) (bloquea (0,0)->(0,1)) y VERTICAL en (0,0) (bloquea (0,0)->(1,0))
		// no hay vecinos; si solo bloqueamos (0,0)->(0,1) con HORIZONTAL en (0,0), el único vecino es (1,0)
		List<Muro> unVecinoMuros = List.of(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL));
		assertEquals(0, validador.calcularDistanciaMinima(new Posicion(0, 0), 0, unVecinoMuros, TAMANO_TABLERO),
				"1 vecino válido: distancia 0 cuando ya está en fila objetivo.");

		// --- Bucle interno: 2 vecinos válidos ---
		// Esquina (8,8) sin muros tiene 2 vecinos válidos: (8,7) y (7,8); hacia fila 7 tarda 1 paso
		assertEquals(1, validador.calcularDistanciaMinima(new Posicion(8, 8), 7, muros, TAMANO_TABLERO),
				"2 vecinos válidos: desde esquina (8,8) hasta fila 7 debe retornar 1.");

		// --- Bucle interno: 3 vecinos válidos ---
		// Borde superior (4,0) sin muros tiene 3 vecinos válidos; hacia fila 1 tarda 1 paso
		assertEquals(1, validador.calcularDistanciaMinima(new Posicion(4, 0), 1, muros, TAMANO_TABLERO),
				"3 vecinos válidos: desde borde (4,0) hasta fila 1 debe retornar 1.");

		// --- Bucle interno: 4 vecinos válidos ---
		// Casilla central (4,4) sin muros tiene 4 vecinos válidos; hacia fila 5 tarda 1 paso
		assertEquals(1, validador.calcularDistanciaMinima(new Posicion(4, 4), 5, muros, TAMANO_TABLERO),
				"4 vecinos válidos: desde casilla central (4,4) hasta fila 5 debe retornar 1.");

		// --- Bucle externo: 1 iteración (distancia == 1) ---
		assertEquals(1, validador.calcularDistanciaMinima(new Posicion(4, 7), 8, muros, TAMANO_TABLERO),
				"1 iteración: casilla adyacente a fila meta debe retornar distancia 1.");

		// --- Bucle externo: 2 iteraciones (distancia == 2 y -1 en bolsa cerrada de 2 casillas) ---
		assertEquals(2, validador.calcularDistanciaMinima(new Posicion(4, 6), 8, muros, TAMANO_TABLERO),
				"2 iteraciones: distancia 2 hacia fila meta.");

		List<Muro> murosBolsaDos = List.of(
				new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
				new Muro(new Posicion(1, 0), Orientacion.VERTICAL));
		assertEquals(-1, validador.calcularDistanciaMinima(new Posicion(0, 0), 8, murosBolsaDos, TAMANO_TABLERO),
				"2 iteraciones en espacio cerrado de 2 casillas debe retornar -1.");

		// --- Bucle externo: m iteraciones (camino con rodeo) ---
		List<Muro> murosRodeo = new ArrayList<>();
		murosRodeo.add(new Muro(new Posicion(3, 3), Orientacion.HORIZONTAL));
		assertEquals(9, validador.calcularDistanciaMinima(inicio, 8, murosRodeo, TAMANO_TABLERO),
				"m iteraciones: camino con rodeo debe incrementar la distancia mínima.");

		// --- Bucle externo: n-1 / n iteraciones (recorrido amplio sin acceso a fila meta) ---
		List<Muro> laberintoAmplio = construirLaberintoSinMeta(TAMANO_TABLERO);
		assertEquals(-1, validador.calcularDistanciaMinima(inicio, 8, laberintoAmplio, TAMANO_TABLERO),
				"n-1 / n iteraciones: recorrido amplio sin acceso a fila meta debe retornar -1.");

		// --- Precondiciones DbC con assertThrows ---
		assertThrows(AssertionError.class,
				() -> validador.calcularDistanciaMinima(null, 8, muros, TAMANO_TABLERO));
		assertThrows(AssertionError.class,
				() -> validador.calcularDistanciaMinima(inicio, TAMANO_TABLERO, muros, TAMANO_TABLERO));
		assertThrows(AssertionError.class,
				() -> validador.calcularDistanciaMinima(inicio, -1, muros, TAMANO_TABLERO));
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
