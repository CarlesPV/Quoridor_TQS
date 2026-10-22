package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
				new Muro (new Posicion(0,0),Orientacion.HORIZONTAL)
				};
		
		for(int i=0; i < murosValidos.length();i++) {
			assertTrue(murosValidos[i].esPosicionValida(LIMITE_TABLERO));
		}
		
		// Casos no validos <0 y >7
		Muro murosNoValidos[] = {
				new Muro (new Posicion(-1,-1),Orientacion.VERTICAL),
				new Muro (new Posicion(-1,-1),Orientacion.HORIZONTAL)
				};
		
		for(int i=0; i < murosNoValidos.length();i++) {
			assertFalse(murosNoValidos[i].esPosicionValida(LIMITE_TABLERO));
		}
	}
	
	@Test
	void testColisionaCon() {
		muro = new Muro(new Posicion(0,0),Orientacion.VERTICAL);
		Muro muro2 = new Muro(new Posicion(0,0), Orientacion.HORIZONTAL);
		Muro muro3 = new Muro(new Posicion(0,0), Orientacion.VERTICAL);
		Muro muro4 = new Muro(new Posicion(7,7), Orientacion.VERTICAL);
		
		assertTrue(muro.colisionaCon(muro2));
		assertTrue(muro.colisionaCon(muro3));
		
		assertFalse(muro.colisionaCon(muro4));
	}
	
    /// 
    /// Tests Automatizados (Data-Driven)
    /// 
    @ParameterizedTest(name = "[{index}] Muro {2} en ({0},{1}) bloquea ({3},{4})->({5},{6})? esperado={7}")
    @CsvSource({
        // --- MURO HORIZONTAL EN (4, 4) ---
        "4, 4, HORIZONTAL, 4, 4, 4, 5, true",
        "4, 4, HORIZONTAL, 4, 5, 4, 4, true",
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
