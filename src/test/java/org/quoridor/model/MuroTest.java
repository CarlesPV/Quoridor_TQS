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
		// Casos validos de [0, 7]
		Muro murosValidos[] = {
				new Muro (new Posicion(0,0),Orientacion.VERTICAL),
				new Muro (new Posicion(0,0),Orientacion.HORIZONTAL),
				new Muro (new Posicion(1,1),Orientacion.VERTICAL),
				new Muro (new Posicion(1,1),Orientacion.HORIZONTAL),
				new Muro (new Posicion(6,6),Orientacion.VERTICAL),
				new Muro (new Posicion(6,6),Orientacion.HORIZONTAL),
				new Muro (new Posicion(7,7),Orientacion.VERTICAL),
				new Muro (new Posicion(7,7),Orientacion.HORIZONTAL),
				new Muro (new Posicion(0,7),Orientacion.VERTICAL),
				new Muro (new Posicion(0,7),Orientacion.HORIZONTAL)
				};
		
		for(int i=0; i < murosValidos.length();i++) {
			assertTrue(murosValidos[i].esPosicionValida(LIMITE_TABLERO));
		}
		
		// Casos no validos <0 y >7
		Muro murosNoValidos[] = {
				new Muro (new Posicion(-1,-1),Orientacion.VERTICAL),
				new Muro (new Posicion(-1,-1),Orientacion.HORIZONTAL),
				new Muro (new Posicion(8,8),Orientacion.VERTICAL),
				new Muro (new Posicion(8,8),Orientacion.HORIZONTAL),
				new Muro (new Posicion(9,9),Orientacion.VERTICAL),
				new Muro (new Posicion(9,9),Orientacion.HORIZONTAL),
				new Muro (new Posicion(0,-1),Orientacion.VERTICAL),
				new Muro (new Posicion(0,-1),Orientacion.HORIZONTAL),
				new Muro (new Posicion(8,7),Orientacion.VERTICAL),
				new Muro (new Posicion(8,7),Orientacion.HORIZONTAL)
				};
		
		for(int i=0; i < murosNoValidos.length();i++) {
			assertFalse(murosNoValidos[i].esPosicionValida(LIMITE_TABLERO));
		}
	}
	
	@Test
	void testColisionaCon() {
		// 1. Mismo origen: solapamiento total y cruce en cruz
		Muro baseVert = new Muro(new Posicion(2, 2), Orientacion.VERTICAL);
		Muro cruceCruz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		Muro solapadoTotal = new Muro(new Posicion(2, 2), Orientacion.VERTICAL);

		assertTrue(baseVert.colisionaCon(cruceCruz), "Debe colisionar si se cruzan en el mismo origen");
		assertTrue(baseVert.colisionaCon(solapadoTotal), "Debe colisionar si coinciden exactamente en origen y orientación");

		// 2. Misma orientación: solapamiento parcial en el mismo eje (distancia = 1)
		Muro solapadoParcialVert = new Muro(new Posicion(2, 3), Orientacion.VERTICAL);
		Muro baseHoriz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		Muro solapadoParcialHoriz = new Muro(new Posicion(3, 2), Orientacion.HORIZONTAL);

		assertTrue(baseVert.colisionaCon(solapadoParcialVert), "Debe colisionar por solapamiento parcial vertical");
		assertTrue(baseHoriz.colisionaCon(solapadoParcialHoriz), "Debe colisionar por solapamiento parcial horizontal");

		// 3. Casos frontera: muros consecutivos/adyacentes en el mismo eje (distancia = 2, no colisionan)
		Muro contiguoVert = new Muro(new Posicion(2, 4), Orientacion.VERTICAL);
		Muro contiguoHoriz = new Muro(new Posicion(4, 2), Orientacion.HORIZONTAL);

		assertFalse(baseVert.colisionaCon(contiguoVert), "Muros contiguos verticales a distancia 2 no deben colisionar");
		assertFalse(baseHoriz.colisionaCon(contiguoHoriz), "Muros contiguos horizontales a distancia 2 no deben colisionar");

		// 4. Muros paralelos sin solapamiento (desplazados lateralmente)
		Muro paraleloVert = new Muro(new Posicion(3, 2), Orientacion.VERTICAL);
		Muro paraleloHoriz = new Muro(new Posicion(2, 3), Orientacion.HORIZONTAL);

		assertFalse(baseVert.colisionaCon(paraleloVert), "Muros verticales paralelos no deben colisionar");
		assertFalse(baseHoriz.colisionaCon(paraleloHoriz), "Muros horizontales paralelos no deben colisionar");

		// 5. Muro lejano / disjunto
		Muro lejano = new Muro(new Posicion(7, 7), Orientacion.VERTICAL);
		assertFalse(baseVert.colisionaCon(lejano), "Muros completamente separados no deben colisionar");
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
        "4, 4, HORIZONTAL, 0, 0, 0, 1, false"
    })
    
    @DisplayName("Data-Driven: Verificación de bloqueo de paso para muros")
    void testBloqueaPasoDataDriven(int mX, int mY, Orientacion orientacion,
                                   int oX, int oY, int dX, int dY, boolean esperado) {
    	
        muro = new Muro(new Posicion(mX, mY), orientacion);
        Posicion origen = new Posicion(oX, oY);
        Posicion destino = new Posicion(dX, dY);

        assertEquals(esperado, muro.bloqueaPaso(origen, destino));
	}
}
