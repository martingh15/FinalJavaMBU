package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entidades.Usuario;
import logic.CtrlLogin;
import utils.ApplicationException;

/**
 * Servlet implementation class Inicio
 */
@WebServlet("/Inicio")
public class Inicio extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Inicio() {
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
			CtrlLogin ctrl = new CtrlLogin();
			if (request.getParameter("login") != null) {
				Usuario u = ctrl.login(request.getParameter("nombreUsuario"), request.getParameter("password"));

				if (u != null) {
					request.getSession().setAttribute("usuario", u);
					response.sendRedirect("routes/Menu.jsp");

				} else {
					request.getSession().setAttribute("usuario", "erroneo");
					response.sendRedirect("index.jsp");
				}
			}
			if (request.getParameter("registro") != null) {
				response.sendRedirect("routes/Registro.jsp");
			}
		} catch (ApplicationException e) {
			request.getSession().setAttribute("error", e.getMessage());
			response.sendRedirect("/WebPage/routes/MensajeError.jsp");
		}
	}
}
