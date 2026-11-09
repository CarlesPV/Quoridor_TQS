package org.quoridor.model;

public class Muro {

	private Posicion origen;
	private Orientacion orientacion;
	private static final int TAMANO_MURO = 2;
	
	public Posicion getOrigen() {return this.origen;}
	public Orientacion getOrientacion() {return this.orientacion;} 
	private void setOrigen(Posicion origen) {this.origen = origen;}
	private void setOrientacion(Orientacion orientacion) {this.orientacion = orientacion;}
	
	public Muro() {
	}
	public Muro(Posicion origen, Orientacion orientacion) {
		// Pre-condiciones
		assert(origen.getX()>=0);
		assert(origen.getY()>=0);
		
		this.setOrigen(origen);
		this.setOrientacion(orientacion);
	}
	
	/// 
	/// Funciones
	/// 
	public boolean esPosicionValida(int limiteTablero) {
		// Pre-condiciones
		assert(limiteTablero>=TAMANO_MURO);
		assert(this.getOrigen()!=null);
		assert(this.getOrientacion()!=null);
		
		
		Posicion pos = this.getOrigen();
		
		if (pos.getX() >= 0 && pos.getY() >= 0
				&& pos.getX() <= (limiteTablero-TAMANO_MURO) 
				&& pos.getY() <= (limiteTablero-TAMANO_MURO)) {
			 return true;
        }
        return false;
	}
	
    public boolean colisionaCon(Muro otroMuro) {
        // Pre-condiciones
    	assert (otroMuro!=null);
    	assert (otroMuro.getOrigen()!=null);
        assert (otroMuro.getOrientacion()!=null);
        assert (this.getOrigen()!=null);
        assert (this.getOrientacion()!=null);

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
		// Pre-condiciones
		assert (origen != null);
		assert (destino!=null);
		assert (this.getOrigen()!=null);
		assert (this.getOrientacion()!=null);
        
        int oX = origen.getX();
        int oY = origen.getY();
        int dX = destino.getX();
        int dY = destino.getY();
        int mX = this.getOrigen().getX();
        int mY = this.getOrigen().getY();
        
        if (this.orientacion == Orientacion.HORIZONTAL) {
        	 // Un muro horizontal bloquea el paso entre filas mY y mY+1
            if (oX == dX && Math.abs(oY - dY) == 1) {
                if (Math.min(oY, dY) == mY && oX >= mX && oX < mX + TAMANO_MURO) {
                    return true;
                }
            }
        }
        else {
        	// Un muro vertical bloquea el paso entre columnas mX y mX+1
            if (oY == dY && Math.abs(oX - dX) == 1) {
                if (Math.min(oX, dX) == mX && oY >= mY && oY < mY + TAMANO_MURO) {
                    return true;
                }
            }
        }
        return false;
    }
}
