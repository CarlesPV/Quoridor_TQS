package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.DisplayName;

class MuroTest {

	private Muro muro;
	private int LIMITE_TABLERO = 9;
	
	@BeforeEach
	void setUp() throws Exception {
		muro = new Muro();
	}

	///
	/// Tests de constructores
	/// 
	@Test
	void testConstructor() {
		assertNull(muro.getOrigen());
		assertNull(muro.getOrientacion());
	}
	
	@Test
	void testConstructorConParametros() {
		muro = new Muro (new Posicion(0,1),Orientacion.VERTICAL);
		
		assertEquals(muro.getOrigen(), new Posicion(0,1));
		assertEquals(muro.getOrientacion(), Orientacion.VERTICAL);
	}
	
	///
	/// Tests de funciones
	/// 
	@Test
	void testEsPosicionValida() {
		// Casos validos de [0, 7] en tablero 9x9
		Muro[] murosValidos = {
				new Muro(new Posicion(0, 0), Orientacion.VERTICAL),
				new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL),
				new Muro(new Posicion(1, 1), Orientacion.VERTICAL),
				new Muro(new Posicion(1, 1), Orientacion.HORIZONTAL),
				new Muro(new Posicion(6, 6), Orientacion.VERTICAL),
				new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL),
				new Muro(new Posicion(7, 7), Orientacion.VERTICAL),
				new Muro(new Posicion(7, 7), Orientacion.HORIZONTAL),
				new Muro(new Posicion(0, 7), Orientacion.VERTICAL),
				new Muro(new Posicion(0, 7), Orientacion.HORIZONTAL),
				new Muro(new Posicion(7, 0), Orientacion.VERTICAL),
				new Muro(new Posicion(7, 0), Orientacion.HORIZONTAL)
		};

		for (int i = 0; i < murosValidos.length; i++) {
			assertTrue(murosValidos[i].esPosicionValida(LIMITE_TABLERO));
		}

		// Casos no validos <0 y >7 en tablero 9x9
		Muro[] murosNoValidos = {
				new Muro(new Posicion(-1, -1), Orientacion.VERTICAL),
				new Muro(new Posicion(-1, -1), Orientacion.HORIZONTAL),
				new Muro(new Posicion(8, 8), Orientacion.VERTICAL),
				new Muro(new Posicion(8, 8), Orientacion.HORIZONTAL),
				new Muro(new Posicion(9, 9), Orientacion.VERTICAL),
				new Muro(new Posicion(9, 9), Orientacion.HORIZONTAL),
				new Muro(new Posicion(0, -1), Orientacion.VERTICAL),
				new Muro(new Posicion(0, -1), Orientacion.HORIZONTAL),
				new Muro(new Posicion(-1, 0), Orientacion.VERTICAL),
				new Muro(new Posicion(-1, 0), Orientacion.HORIZONTAL),
				new Muro(new Posicion(7, -1), Orientacion.VERTICAL),
				new Muro(new Posicion(7, -1), Orientacion.HORIZONTAL),
				new Muro(new Posicion(-1, 7), Orientacion.VERTICAL),
				new Muro(new Posicion(-1, 7), Orientacion.HORIZONTAL),
				new Muro(new Posicion(8, 7), Orientacion.VERTICAL),
				new Muro(new Posicion(8, 7), Orientacion.HORIZONTAL),
				new Muro(new Posicion(0, 8), Orientacion.VERTICAL),
				new Muro(new Posicion(0, 8), Orientacion.HORIZONTAL),
				new Muro(new Posicion(7, 8), Orientacion.HORIZONTAL),
				new Muro(new Posicion(8, 0), Orientacion.VERTICAL)
		};

		for (int i = 0; i < murosNoValidos.length; i++) {
			assertFalse(murosNoValidos[i].esPosicionValida(LIMITE_TABLERO));
		}

		// Límite mínimo de contrato (limiteTablero = 2): únicamente (0,0) es válido
		assertTrue(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL).esPosicionValida(2));
		assertTrue(new Muro(new Posicion(0, 0), Orientacion.VERTICAL).esPosicionValida(2));
		assertFalse(new Muro(new Posicion(1, 0), Orientacion.HORIZONTAL).esPosicionValida(2));
		assertFalse(new Muro(new Posicion(0, 1), Orientacion.VERTICAL).esPosicionValida(2));

		// Tablero 7x7: límite válido en (5,5) y frontera inválida en (6,5) / (5,6)
		assertTrue(new Muro(new Posicion(5, 5), Orientacion.HORIZONTAL).esPosicionValida(7));
		assertTrue(new Muro(new Posicion(5, 5), Orientacion.VERTICAL).esPosicionValida(7));
		assertFalse(new Muro(new Posicion(6, 5), Orientacion.HORIZONTAL).esPosicionValida(7));
		assertFalse(new Muro(new Posicion(5, 6), Orientacion.VERTICAL).esPosicionValida(7));
	}

	@Test
	void testColisionaCon() {
		// 1. Mismo origen: solapamiento total y cruce en cruz
		Muro baseVert = new Muro(new Posicion(2, 2), Orientacion.VERTICAL);
		Muro cruceCruz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		Muro solapadoTotal = new Muro(new Posicion(2, 2), Orientacion.VERTICAL);

		assertTrue(baseVert.colisionaCon(cruceCruz),
				"Debe colisionar si se cruzan en el mismo origen");
		assertTrue(baseVert.colisionaCon(solapadoTotal),
				"Debe colisionar si coinciden exactamente en origen y orientación");

		// 2. Misma orientación: solapamiento parcial en sentido positivo y negativo (distancia = 1)
		Muro solapadoParcialVert = new Muro(new Posicion(2, 3), Orientacion.VERTICAL);
		Muro solapadoParcialVertNeg = new Muro(new Posicion(2, 1), Orientacion.VERTICAL);
		Muro baseHoriz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		Muro solapadoParcialHoriz = new Muro(new Posicion(3, 2), Orientacion.HORIZONTAL);
		Muro solapadoParcialHorizNeg = new Muro(new Posicion(1, 2), Orientacion.HORIZONTAL);

		assertTrue(baseVert.colisionaCon(solapadoParcialVert),
				"Debe colisionar por solapamiento parcial vertical positivo");
		assertTrue(baseVert.colisionaCon(solapadoParcialVertNeg),
				"Debe colisionar por solapamiento parcial vertical negativo");
		assertTrue(baseHoriz.colisionaCon(solapadoParcialHoriz),
				"Debe colisionar por solapamiento parcial horizontal positivo");
		assertTrue(baseHoriz.colisionaCon(solapadoParcialHorizNeg),
				"Debe colisionar por solapamiento parcial horizontal negativo");

		// 3. Casos frontera: muros contiguos en sentido positivo y negativo (distancia = 2, no colisionan)
		Muro contiguoVert = new Muro(new Posicion(2, 4), Orientacion.VERTICAL);
		Muro contiguoVertNeg = new Muro(new Posicion(2, 0), Orientacion.VERTICAL);
		Muro contiguoHoriz = new Muro(new Posicion(4, 2), Orientacion.HORIZONTAL);
		Muro contiguoHorizNeg = new Muro(new Posicion(0, 2), Orientacion.HORIZONTAL);

		assertFalse(baseVert.colisionaCon(contiguoVert),
				"Muros contiguos verticales positivos a distancia 2 no deben colisionar");
		assertFalse(baseVert.colisionaCon(contiguoVertNeg),
				"Muros contiguos verticales negativos a distancia 2 no deben colisionar");
		assertFalse(baseHoriz.colisionaCon(contiguoHoriz),
				"Muros contiguos horizontales positivos a distancia 2 no deben colisionar");
		assertFalse(baseHoriz.colisionaCon(contiguoHorizNeg),
				"Muros contiguos horizontales negativos a distancia 2 no deben colisionar");

		// 4. Muros paralelos sin solapamiento (desplazados lateralmente y en ambos ejes)
		Muro paraleloVert = new Muro(new Posicion(3, 2), Orientacion.VERTICAL);
		Muro paraleloHoriz = new Muro(new Posicion(2, 3), Orientacion.HORIZONTAL);
		Muro desplazadoHoriz = new Muro(new Posicion(3, 3), Orientacion.HORIZONTAL);
		Muro desplazadoVert = new Muro(new Posicion(3, 3), Orientacion.VERTICAL);

		assertFalse(baseVert.colisionaCon(paraleloVert), "Muros verticales paralelos no deben colisionar");
		assertFalse(baseHoriz.colisionaCon(paraleloHoriz), "Muros horizontales paralelos no deben colisionar");
		assertFalse(baseHoriz.colisionaCon(desplazadoHoriz),
				"Muros horizontales con distinta X e Y no deben colisionar");
		assertFalse(baseVert.colisionaCon(desplazadoVert),
				"Muros verticales con distinta X e Y no deben colisionar");

		// 5. Muros perpendiculares que no comparten origen (cruces en T y L válidos sin colisión)
		Muro cruceTFin = new Muro(new Posicion(3, 2), Orientacion.VERTICAL);
		Muro cruceLInf = new Muro(new Posicion(2, 3), Orientacion.VERTICAL);
		Muro cruceTSup = new Muro(new Posicion(2, 1), Orientacion.VERTICAL);

		assertFalse(baseHoriz.colisionaCon(cruceTFin),
				"Cruce en T/L (2,2)H con (3,2)V no debe colisionar");
		assertFalse(baseHoriz.colisionaCon(cruceLInf),
				"Cruce en T/L (2,2)H con (2,3)V no debe colisionar");
		assertFalse(baseHoriz.colisionaCon(cruceTSup),
				"Cruce en T (2,2)H con (2,1)V no debe colisionar");

		// 6. Muro lejano / disjunto
		Muro lejano = new Muro(new Posicion(7, 7), Orientacion.VERTICAL);
		assertFalse(baseVert.colisionaCon(lejano), "Muros completamente separados no deben colisionar");
		
		// 7. Orientaciones distintas con orígenes distintos (sin solapamiento ni cruce)
		Muro distintaOrientacionDistintoOrigen = new Muro(new Posicion(3, 3), Orientacion.VERTICAL);
		assertFalse(baseHoriz.colisionaCon(distintaOrientacionDistintoOrigen),
				"Muros de distinta orientación y distinto origen no deben colisionar si no se interceptan");
	}
	
    /// 
    /// Tests Automatizados (Data-Driven)
    /// 
    @ParameterizedTest(name = "[{index}] Muro {2} en ({0},{1}) bloquea ({3},{4})->({5},{6})? esperado={7}")
    @CsvSource({
    	// --- MURO HORIZONTAL EN (4,4)
    	// 1. Bloqueo en primer segmento (X=4) en ambas direcciones
        "4, 4, HORIZONTAL, 4, 4, 4, 5, true",
        "4, 4, HORIZONTAL, 4, 5, 4, 4, true",
        
        // 2. Bloqueo en segundo segmento (X=5) en ambas direcciones
        "4, 4, HORIZONTAL, 5, 4, 5, 5, true",
        "4, 4, HORIZONTAL, 5, 5, 5, 4, true",
        
        // 3. Valores frontera: pasos verticales inmediatamente adyacentes al muro
        "4, 4, HORIZONTAL, 3, 4, 3, 5, false", // Justo a la izquierda
        "4, 4, HORIZONTAL, 6, 4, 6, 5, false", // Justo a la derecha
        
        // 4. Paso paralelo: movimiento horizontal en las casillas del muro
        "4, 4, HORIZONTAL, 4, 4, 5, 4, false",
        "4, 4, HORIZONTAL, 4, 5, 5, 5, false",
        
        // --- MURO VERTICAL EN (4, 4) ---
        // 5. Bloqueo en primer segmento (Y=4) en ambas direcciones
        "4, 4, VERTICAL,   4, 4, 5, 4, true",
        "4, 4, VERTICAL,   5, 4, 4, 4, true",
        
        // 6. Bloqueo en segundo segmento (Y=5) en ambas direcciones
        "4, 4, VERTICAL,   4, 5, 5, 5, true",
        "4, 4, VERTICAL,   5, 5, 4, 5, true",
        
        // 7. Valores frontera: pasos horizontales inmediatamente adyacentes al muro
        "4, 4, VERTICAL,   4, 3, 5, 3, false", // Justo arriba
        "4, 4, VERTICAL,   4, 6, 5, 6, false", // Justo abajo
        
        // 8. Paso paralelo: movimiento vertical en las casillas del muro
        "4, 4, VERTICAL,   4, 4, 4, 5, false",
        "4, 4, VERTICAL,   5, 4, 5, 5, false",
        
        // --- CASO DESCONECTADO (Paso lejano) ---
        "4, 4, HORIZONTAL, 0, 0, 0, 1, false",

        // --- DISTANCIA MAYOR A 1 EN MISMO EJE (Salto de 2 casillas) ---
        "4, 4, HORIZONTAL, 4, 4, 4, 6, false",
        "4, 4, VERTICAL,   4, 4, 6, 4, false",
        
        // --- Cobertura adicional de condiciones en bloqueaPaso (muro HORIZONTAL en (4,4)) ---
        // min(oY, dY) == mY (4) con oX < mX (oX = 2 < 4)
        "4, 4, HORIZONTAL, 2, 4, 2, 5, false",
        // min(oY, dY) == mY (4) con oX >= mX + 2 (oX = 6 >= 6)
        "4, 4, HORIZONTAL, 6, 4, 6, 5, false",
        // min(oY, dY) != mY (oY=5, dY=6 -> min=5 != 4) con oX en [mX, mX + 2) (oX = 4)
        "4, 4, HORIZONTAL, 4, 5, 4, 6, false",
        "4, 4, HORIZONTAL, 4, 5, 5, 5, false",
        
        // --- Muros en límites del tablero: (0,0) y (7,7) ---
        "0, 0, HORIZONTAL, 0, 0, 0, 1, true",
        "0, 0, VERTICAL,   0, 0, 1, 0, true",
        "7, 7, HORIZONTAL, 7, 7, 7, 8, true",
        "7, 7, VERTICAL,   7, 7, 8, 7, true",

        // --- Paso diagonal y misma casilla ---
        "4, 4, HORIZONTAL, 4, 4, 5, 5, false",
        "4, 4, VERTICAL,   4, 4, 5, 5, false",
        "4, 4, HORIZONTAL, 4, 4, 4, 4, false",
        "4, 4, VERTICAL,   4, 4, 4, 4, false",

        // --- Pasos adyacentes por encima / debajo / izquierda / derecha de un muro en (4,4) ---
        // Por encima del muro horizontal en (4,4) (entre y=3 y y=4): no bloquea
        "4, 4, HORIZONTAL, 4, 3, 4, 4, false",
        // Por debajo del muro horizontal en (4,4) (entre y=4 y y=5): sí bloquea
        "4, 4, HORIZONTAL, 4, 4, 4, 5, true",
        // A la izquierda del muro vertical en (4,4) (entre x=3 y x=4): no bloquea
        "4, 4, VERTICAL,   3, 4, 4, 4, false",
        // A la derecha del muro vertical en (4,4) (entre x=4 y x=5): sí bloquea
        "4, 4, VERTICAL,   4, 4, 5, 4, true"
    })
    
    @DisplayName("Data-Driven: Verificación de bloqueo de paso para muros")
    void testBloqueaPasoDataDriven(int mX, int mY, Orientacion orientacion,
                                   int oX, int oY, int dX, int dY, boolean esperado) {
    	
        muro = new Muro(new Posicion(mX, mY), orientacion);
        Posicion origen = new Posicion(oX, oY);
        Posicion destino = new Posicion(dX, dY);

        assertEquals(esperado, muro.bloqueaPaso(origen, destino));
	}

	@Test
	void testPrecondicionesMuro() {
		Posicion pos = new Posicion(2, 2);
		Muro muroValido = new Muro(pos, Orientacion.HORIZONTAL);
		Muro muroVacio = new Muro();

		// Constructor
		assertThrows(AssertionError.class, () -> new Muro(null, Orientacion.HORIZONTAL));
		assertThrows(AssertionError.class, () -> new Muro(pos, null));

		// esPosicionValida
		assertThrows(AssertionError.class, () -> muroValido.esPosicionValida(1));
		assertThrows(AssertionError.class, () -> muroVacio.esPosicionValida(LIMITE_TABLERO));

		// colisionaCon
		assertThrows(AssertionError.class, () -> muroValido.colisionaCon(null));
		assertThrows(AssertionError.class, () -> muroValido.colisionaCon(muroVacio));
		assertThrows(AssertionError.class, () -> muroVacio.colisionaCon(muroValido));

		// bloqueaPaso
		assertThrows(AssertionError.class, () -> muroValido.bloqueaPaso(null, pos));
		assertThrows(AssertionError.class, () -> muroValido.bloqueaPaso(pos, null));
		assertThrows(AssertionError.class, () -> muroVacio.bloqueaPaso(pos, new Posicion(2, 3)));
	}

	// Prueba de Programación por Contrato: verifica invariante y postcondiciones de Muro
	@Test
	void testContratosDbCMuro() {
		Muro muroHoriz = new Muro(new Posicion(3, 3), Orientacion.HORIZONTAL);
		Muro muroVert = new Muro(new Posicion(5, 5), Orientacion.VERTICAL);
		Muro muroIncompleto = new Muro();

		// Un muro válido cumple el invariante en todas sus operaciones
		assertTrue(muroHoriz.esPosicionValida(LIMITE_TABLERO));
		assertFalse(muroHoriz.colisionaCon(muroVert));
		assertTrue(muroHoriz.bloqueaPaso(new Posicion(3, 3), new Posicion(3, 4)));
		assertFalse(muroHoriz.bloqueaPaso(new Posicion(3, 3), new Posicion(4, 3)));

		// El otro muro en colisionaCon también debe cumplir su invariante
		assertThrows(AssertionError.class, () -> muroHoriz.colisionaCon(muroIncompleto));
		assertThrows(AssertionError.class, () -> muroIncompleto.esPosicionValida(LIMITE_TABLERO));
		assertThrows(AssertionError.class, () -> muroIncompleto.bloqueaPaso(new Posicion(0, 0), new Posicion(0, 1)));
	}
}
