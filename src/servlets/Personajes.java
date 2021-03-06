package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Personaje;
import entidades.AtributosRolNivel;
import entidades.Usuario;
import entidades.Rol;
import logic.ControladorABMCPersonaje;
import utils.ApplicationException;

/**
 * Servlet implementation class Personajes
 */
@WebServlet("/Personajes")
public class Personajes extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Personajes() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ControladorABMCPersonaje ctrlPersonaje = new ControladorABMCPersonaje();
			HttpSession session = request.getSession();
			if (request.getParameter("guardarPersonaje") != null) {
				// Personaje per = this.mapCharacterFromForm(request);
				Personaje per = (Personaje) request.getSession().getAttribute("currentPersonaje");
				Usuario currentUser = (Usuario) session.getAttribute("usuario");

				int id_personaje = ctrlPersonaje.agregarPersonaje(per);
				ctrlPersonaje.insertarPersonajeUsuario(id_personaje, currentUser.getId());
				String selected_attacks[] = request.getParameterValues("selectedAttacks");
				if (request.getParameterValues("selectedAttacks") == null) {
					throw new ApplicationException(new Exception(), "No ha elegido ataques para el personaje");
				}
				for (String id_attack : selected_attacks) {
					ctrlPersonaje.insertarPersonajeAtaque(id_personaje, Integer.parseInt(id_attack));
				}
				response.sendRedirect("routes/Menu.jsp");
			}

			if (request.getParameter("agregarAtaques") != null) {
				Personaje currentPersonaje = this.mapCharacterFromForm(request);
				request.getSession().setAttribute("currentPersonaje", currentPersonaje);
				response.sendRedirect("routes/AgregarAtaquesPersonaje.jsp");
			}

			if (request.getParameter("selectRole") != null) {
				String selected_role[] = request.getParameterValues("selectedRole");
				if (selected_role[0].equalsIgnoreCase("")) {
					throw new ApplicationException(new Exception(), "No ha elegido rol");
				}
				int id_role = Integer.parseInt(selected_role[0]);
				AtributosRolNivel atr = ctrlPersonaje.puntosTotalesSegunRolNivel(id_role, 1);
				Rol selectedRole = ctrlPersonaje.getOneRoleById(id_role);
				request.getSession().setAttribute("selectedRole", selectedRole);
				request.getSession().setAttribute("atributos", atr);
				request.getRequestDispatcher("routes/AgregarPersonaje.jsp").forward(request, response);
			}

			if (request.getParameter("cancelar") != null) {
				response.sendRedirect("routes/Menu.jsp");
			}
		} catch (ApplicationException e) {
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect("routes/MensajeError.jsp");
		}
	}

	public Personaje mapCharacterFromForm(HttpServletRequest request) throws ApplicationException {
		Personaje per = new Personaje();
		AtributosRolNivel atr = (AtributosRolNivel) request.getSession().getAttribute("atributos");
		if(request.getParameter("nombre").equalsIgnoreCase("")) {
			throw new ApplicationException(new Exception(), "El nombre del personaje no puede estar vac�o");
		}
		per.setNombre(request.getParameter("nombre"));
		per.setVida(atr.getVida());
		per.setEnergia(atr.getEnergia());
		per.setDefensa(atr.getDefensa());
		per.setEvasion(atr.getEvasion());
		Rol selectedRole = (Rol) (request.getSession().getAttribute("selectedRole"));
		per.setId_rol(selectedRole.getId_rol());
		return per;
	}

}
