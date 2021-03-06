package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entidades.Personaje;
import entidades.Torneo;
import entidades.Ataque;
import logic.CtrlCombate;
import logic.CtrlTorneo;
import logic.ControladorABMCPersonaje;
import utils.ApplicationException;

/**
 * Servlet implementation class Combate
 */
@WebServlet("/War")
public class War extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private int turno = 1;
	private CtrlCombate controlador;
	private String mensajes = "Resumen de la pelea: ";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public War() {
		super();

		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			controlador = (CtrlCombate) request.getSession().getAttribute("CtrlCombate");
			turno = (int) request.getSession().getAttribute("turno");
			CtrlTorneo ctrlTorneo = new CtrlTorneo();
			ControladorABMCPersonaje ctrlPersonaje = new ControladorABMCPersonaje();
			List<Ataque> ataques;
			if (request.getParameter("atacar") != null) {
				try {
					if (turno == 1) {
						Ataque ataque = controlador.getAtaque(Integer.parseInt(request.getParameter("idAtaque")));
						int energia = ataque.getEnergia_requerida();
						Personaje p = (Personaje) request.getSession().getAttribute("P1");
						if (controlador.ataque(energia, turno)) {
							request.getSession().setAttribute("nombreTurno", controlador.getPerTurno());
							request.getSession().setAttribute("msg",
									"personaje: " + String.valueOf(controlador.getPerTurno()));

							Torneo t = (Torneo) request.getSession().getAttribute("torneo");
							// Asignacion de experiencia
							// Personaje p = (Personaje) request.getSession().getAttribute("P1");
							int cant_experiencia = controlador
									.getExperienciaFromCombate(ctrlTorneo.getIdCombateActivo(t.getId()));
							ctrlPersonaje.updateExperienciaPersonaje(p.getId(), cant_experiencia);

							// Subida de nivel
							Personaje per = ctrlPersonaje.getById(p.getId());
							int id_next_nivel = controlador.getNivelByExperiencia(per.getExperiencia());
							ctrlPersonaje.levelUpPersonaje(per, id_next_nivel);
							int id_next_combate = ctrlTorneo.updateCombateActivo(t.getId());
							per = ctrlPersonaje.getById(p.getId());
							request.getSession().setAttribute("Personaje", per);
							if (id_next_combate == 0) {
								mensajes = "Resumen de la pelea: ";
								request.getRequestDispatcher("routes/Winner.jsp").forward(request, response);
							} else {
								mensajes = "Resumen de la pelea: ";
								request.getRequestDispatcher("routes/WinCombat.jsp").forward(request, response);
							}

						} else {
							if (controlador.isEvadido()) {
									mensajes = "El ataque fue evadido \n\n" +  mensajes;
								controlador.setEvadido(false);
							} else {
                                    mensajes = "Quito a su rival "+ ataque.getEnergia_requerida() + " de vida \n\n" + mensajes;
							}
							mensajes = String.valueOf(p.getNombre()) + " ha atacado usando el ataque " + ataque.getNombre_ataque() + "\n" + mensajes;
					        request.setAttribute("mensaje", mensajes);
							this.terminaTurno(request, response);
						}
					}
				} catch (NumberFormatException e) {
					request.getSession().setAttribute("error", "Error en la energia");
					response.sendRedirect("/WebPage/routes/MensajeError.jsp");
				} catch (ApplicationException e) {
					request.getSession().setAttribute("error", "Error en la energia");
					response.sendRedirect("/WebPage/routes/MensajeError.jsp");
				}
			}

			if (request.getParameter("defender") != null) {

				if (turno == 1) {
					Personaje p = (Personaje) request.getSession().getAttribute("P1");
					mensajes = String.valueOf(p.getNombre()) + " Ha defendido \n\n" +  mensajes;
					request.setAttribute("mensaje", mensajes);
					controlador.defensa(turno);
					this.terminaTurno(request, response);
				}

			}

			if (request.getParameter("continuar") != null) {

				if (turno == 2) {
					Personaje p = (Personaje) request.getSession().getAttribute("P2");
					ataques = controlador.getAtaquesOfPersonajeByEnergia(controlador.getIdPersonajeTurno(turno), turno);
					if (controlador.getNecesitaDefender(turno)) {
						mensajes =  String.valueOf(p.getNombre()) + " Ha defendido \n\n" + mensajes ;
						request.setAttribute("mensaje", mensajes);
						controlador.defensa(turno);
						this.terminaTurno(request, response);
					} else {

						Ataque ataque = controlador.randomAtaque(ataques);
						if (controlador.ataque(controlador.getEnergiaFromRandomAtaque(ataque), turno)) {
							// tiene que ir a la pantalla de perdida
							mensajes = "Resumen de la pelea: ";
							request.getRequestDispatcher("routes/Loser.jsp").forward(request, response);
						} else {
							if (controlador.isEvadido()) {
                                mensajes = "El ataque fue evadido \n\n" + mensajes;
								controlador.setEvadido(false);
							} else {
								mensajes = "Quito a su rival "+ ataque.getEnergia_requerida() + " de vida\n\n" + mensajes;
							}
							mensajes =  String.valueOf(p.getNombre()) + " ha atacado usando el ataque " + ataque.getNombre_ataque() + "\n" + mensajes;
							request.setAttribute("mensaje", mensajes);
							this.terminaTurno(request, response);
						}

					}
				}
			}
		} catch (Exception e) {
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect("/WebPage/routes/MensajeError.jsp");
		}

	}

	private void cambiaTurno() {
		if (turno == 1) {
			turno = 2;
		} else {
			turno = 1;
		}
	}

	private void terminaTurno(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ApplicationException {

		request.getSession().setAttribute("nombreTurno", controlador.getPerTurno());
		this.cambiaTurno();
		request.getSession().setAttribute("turno", turno);
		List<Ataque> ataques = controlador.getAtaquesOfPersonajeByEnergia(controlador.getIdPersonajeTurno(turno),
				turno);
		request.getSession().setAttribute("ataques", ataques);
		request.getRequestDispatcher("routes/Combate.jsp").forward(request, response);
	}

}
