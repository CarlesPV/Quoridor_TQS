package org.quoridor.controller;

import org.quoridor.model.*;
import org.quoridor.view.IQuoridorVista;
import org.quoridor.view.IQuoridorControlador;

import java.util.*;

public class QuoridorControlador implements IQuoridorControlador {
	public QuoridorControlador(Partida partida, IQuoridorVista vista);
	
	public void iniciarJuego();
	
	@Override
	public boolean onCasillaClic(int x, int y);
	@Override
	public boolean onInterseccionClic(int x, int y, Orientacion o);
	@Override
	public void onBotonReiniciar();
	private void actualizarVistaCompleta();
	
	Partida modelo;
	IQuoridorVista vista;
}
