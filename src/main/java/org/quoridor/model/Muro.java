package org.quoridor.model;

public class Muro {

	private Posicion origen;
	private Orientacion orientacion;
	private static final int TAMANO_MURO = 2;
	
	public Posicion getOrigen() {return this.origen;}
	public Orientacion getOrientacion() {return this.orientacion;} 
	private void setOrigen(Posicion origen) {this.origen = origen;}
	private void setOrientacion(Orientacion orientacion) {this.orientacion = orientacion;}
	
	// Constructor de paquete reservado para pruebas de invariante DbC en el modelo
	Muro() {
	}
    public Muro(Posicion origen, Orientacion orientacion) {
        // Pre-condiciones
        assert origen != null : "El origen no puede ser nulo";
        assert orientacion != null : "La orientación no puede ser nula";

        this.setOrigen(origen);
        this.setOrientacion(orientacion);

        // Invariante de clase
        assert verificarInvariante() : "Invariante de Muro violada tras la construcción";
    }

	// Invariante de clase: un muro válido debe tener origen y orientación no nulos
	private boolean verificarInvariante() {
		return this.origen != null && this.orientacion != null;
	}

	///
	/// Funciones
	///
	public boolean esPosicionValida(int limiteTablero) {
		// Pre-condiciones e invariante
		assert limiteTablero >= TAMANO_MURO : "El límite del tablero debe ser al menos el tamaño del muro";
		assert verificarInvariante() : "Invariante de Muro violada en esPosicionValida";

		Posicion pos = this.getOrigen();
		boolean valida = pos.getX() >= 0 && pos.getY() >= 0
				&& pos.getX() <= (limiteTablero - TAMANO_MURO)
				&& pos.getY() <= (limiteTablero - TAMANO_MURO);

		// Post-condición
		assert !valida || pos.esValida(limiteTablero)
				: "El origen de un muro válido debe estar dentro del tablero";
		return valida;
	}

    public boolean colisionaCon(Muro otroMuro) {
        // Pre-condiciones e invariante
        assert otroMuro != null : "El otro muro no puede ser nulo";
        assert otroMuro.verificarInvariante() : "El otro muro debe cumplir su invariante";
        assert verificarInvariante() : "Invariante de Muro violada en colisionaCon";

        int x1 = this.getOrigen().getX();
        int y1 = this.getOrigen().getY();
        int x2 = otroMuro.getOrigen().getX();
        int y2 = otroMuro.getOrigen().getY();

        // Coincidencia en origen
        if (x1 == x2 && y1 == y2) {
            return true;
        }

        // Misma orientación y solapamiento parcial en su eje común
        if (this.orientacion == otroMuro.getOrientacion()) {
            if (this.orientacion == Orientacion.HORIZONTAL && y1 == y2) {
                return Math.abs(x1 - x2) < TAMANO_MURO;
            }
            if (this.orientacion == Orientacion.VERTICAL && x1 == x2) {
                return Math.abs(y1 - y2) < TAMANO_MURO;
            }
        }

        return false;
    }

	public boolean bloqueaPaso(Posicion origen, Posicion destino) {
		// Pre-condiciones e invariante
		assert origen != null : "La posición de origen no puede ser nula";
		assert destino != null : "La posición de destino no puede ser nula";
		assert verificarInvariante() : "Invariante de Muro violada en bloqueaPaso";

		int oX = origen.getX();
		int oY = origen.getY();
		int dX = destino.getX();
		int dY = destino.getY();
		int mX = this.getOrigen().getX();
		int mY = this.getOrigen().getY();
		boolean bloquea = false;

		if (this.orientacion == Orientacion.HORIZONTAL) {
			// Un muro horizontal bloquea el paso entre filas mY y mY+1
			if (oX == dX && Math.abs(oY - dY) == 1) {
				if (Math.min(oY, dY) == mY && oX >= mX && oX < mX + TAMANO_MURO) {
					bloquea = true;
				}
			}
		} else {
			// Un muro vertical bloquea el paso entre columnas mX y mX+1
			if (oY == dY && Math.abs(oX - dX) == 1) {
				if (Math.min(oX, dX) == mX && oY >= mY && oY < mY + TAMANO_MURO) {
					bloquea = true;
				}
			}
		}

		// Post-condición: solo puede bloquear el paso entre casillas adyacentes
		assert !bloquea || origen.esAdyacente(destino)
				: "Un muro solo puede bloquear el paso entre casillas adyacentes";
		return bloquea;
	}
}
