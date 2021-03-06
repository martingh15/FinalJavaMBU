<%@page import="utils.ApplicationException"%>
<%@page import="logic.CtrlCombate"%>
<%@page import="entidades.Personaje"%>
<%@page import="entidades.Usuario"%>
<%@page import="entidades.Ataque"%>
<%@page import="entidades.Torneo"%>
<%@page import="logic.CtrlTorneo"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Combate</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">
<style>
body {
	background-color: #0072DD;
}

.form-pers1 {
	padding: 0 10px;
}

h1, h2, label {
	color: white;
}

h1 {
	margin-top: 30px;
}

h2 {
	text-align: center;
	margin-bottom: 10px;
}

button {
	height: 48px;
}

.atacar {
	margin-left: 110px;
}
</style>
</head>

<body>
	<%
		Usuario u = (Usuario) session.getAttribute("usuario");
		if (u == null) {
			response.sendRedirect("/WebPage/index.jsp");
		}
	%>
	<form method="post" action="${pageContext.request.contextPath}/Menu"
		id="menu">
		<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
			<a class="navbar-brand"
				href="${pageContext.request.contextPath}/routes/Menu.jsp">Guerra!</a>
			<button class="navbar-toggler" type="button" data-toggle="collapse"
				data-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav mr-auto">
					<li class="nav-item dropdown my-2 my-sm-0">
						<%
							if (u != null) {
						%> <a class="nav-link dropdown-toggle" href="#"
						id="navbarDropdown" role="button" data-toggle="dropdown"
						aria-haspopup="true" aria-expanded="false"> <%=u.getNombreUsuario()%></a>
						<%
							}
						%>
						<div class="dropdown-menu" aria-labelledby="navbarDropdown">
							<div class="dropdown-item">
								<button name="personaje" class="btn btn-default btn-sm">Crear
									Personaje</button>
							</div>
							<div class="dropdown-item">
								<button name="torneo" class="btn btn-default btn-sm">Torneos</button>
							</div>
							<%
								if (u != null && u.getRol().equals("admin")) {
							%>
							<div class="dropdown-item">
								<button name="ataques" class="btn btn-default btn-sm">Ataques</button>
							</div>
							<%
								}
							%>
							<div class="dropdown-divider"></div>
							<div class="dropdown-item">
								<button name="exit" class="btn btn-danger btn-sm">Salir</button>
							</div>
						</div>
					</li>
				</ul>
			</div>
		</nav>
	</form>
	<%
		Personaje p1 = ((Personaje) session.getAttribute("P1"));
		Personaje p2 = ((Personaje) session.getAttribute("P2"));
		CtrlCombate combate = (CtrlCombate) session.getAttribute("CtrlCombate");
		int vida1, vida2, energia1, energia2;
		vida1 = combate.getVidaP1();
		vida2 = combate.getVidaP2();
		energia1 = combate.getEnergiaP1();
		energia2 = combate.getEnergiaP2();

		//Calculos de barra vida1
		int porcVida1 = (int) (vida1 * 100 / p1.getVida());
		String widthVida1 = "width: " + String.valueOf(porcVida1) + "% !important;";
		//Calculos de barra vida2
		int porcVida2 = (int) (vida2 * 100 / p2.getVida());
		String widthVida2 = "width: " + String.valueOf(porcVida2) + "% !important;";

		//Calculos de barra energia1
		int porcEnergia1 = (int) (energia1 * 100 / p1.getEnergia());
		String widthEnergia1 = "width: " + String.valueOf(porcEnergia1) + "% !important;";
		//Calculos de barra energia2
		int porcEnergia2 = (int) (energia2 * 100 / p2.getEnergia());
		String widthEnergia2 = "width: " + String.valueOf(porcEnergia2) + "% !important;";

		String nombrepersonaje = String.valueOf(p1.getNombre());
		String nombreEnemigo = String.valueOf(p2.getNombre());
		//String mensaje = session.getAttribute("mensaje").toString();
		List<Ataque> ataques = (List<Ataque>) session.getAttribute("ataques");
		//Ordeno ataques por energia requerida desc
		ataques.sort(new Comparator<Ataque>() {
			@Override
			public int compare(Ataque ataque1, Ataque ataque2) {
				return ataque2.getEnergia_requerida() - ataque1.getEnergia_requerida();
			}
		});
		request.setAttribute("ataques", ataques);
		int turno = (int) session.getAttribute("turno");
	%>

	<form method="post" class="form-pers1"
		action="${pageContext.request.contextPath}/War">

		<div class="row" style="margin-top:30px;">
			<div class="col-md-4">
				<h2><%=nombrepersonaje%></h2>
				<label>Nombre</label> <input name="nombre1" type="text"
					class="form-control" disabled value="<%=p1.getNombre()%>">
				<br> <label>Vida</label>
				<div class="progress" style="height: 38px;">
					<div class="progress-bar bg-success" role="progressbar"
						style="<%=widthVida1%>;font-size: 20px;"
						aria-valuenow="<%=porcVida1%>" aria-valuemin="0"
						aria-valuemax="${p1.getVida()} }"><%=String.valueOf(vida1).concat("/").concat(String.valueOf(p1.getVida()))%></div>
				</div>
				<br> <label>Energia</label>
				<div class="progress" style="height: 38px;">
					<div class="progress-bar bg-info" role="progressbar"
						style="<%=widthEnergia1%>;font-size: 20px;"
						aria-valuenow="<%=porcEnergia1%>" aria-valuemin="0"
						aria-valuemax="${p1.getEnergia()}"><%=String.valueOf(energia1).concat("/").concat(String.valueOf(p1.getEnergia()))%></div>
				</div>
				<br> <label>Defensa</label> <input name="defensa1" type="text"
					class="form-control" disabled value="<%=p1.getDefensa()%>">
				<br> <label>Evasion</label> <input name="evasion1" type="text"
					class="form-control" disabled value="<%=p1.getEvasion()%>">
			</div>

			<div class="col-md-4">
				<h2>Turno</h2>
				<br> <input name="nombreTurno" type="text" class="form-control"
					disabled
					value="<%=String.valueOf(session.getAttribute("nombreTurno"))%>">
				<br> <br> <br>
				<%
					if (turno == 1) {
				%>
				<%
					if (ataques != null && ataques.isEmpty() == false) {
				%>
				<h2>Elegir Ataque</h2>

				<select name="idAtaque" class="form-control">
					<c:forEach items="${ataques}" var="ataque">
						<option value="${ataque.id_ataque}">
							<c:out value="${ataque.nombre_ataque}" /> - Requiere:
							<c:out value="${ataque.energia_requerida}" /> de energia
						</option>
					</c:forEach>
				</select> <br> <br>



				<button name="atacar" class="atacar btn btn-danger btn-lg"
					type="submit">Atacar</button>
				<%
					}
				%>
				<button name="defender" class="btn btn-success btn-default"
					type="submit">Defender</button>
				<%
					} else {
				%>
				<button name="continuar" class="btn btn-danger" type="submit">Continuar</button>
				<%
					}
				%>


				<br> <br>
				<%
					if (request.getAttribute("mensaje") != null) {
				%>
				<textarea name="mensajes" rows="10" cols="60" value="hola" disabled><%=request.getAttribute("mensaje")%></textarea>
				<%
					}
				%>
			</div>

			<div class="col-md-4">
				<h2><%=nombreEnemigo%></h2>
				<label>Nombre</label> <input name="nombre1" type="text"
					class="form-control" disabled value="<%=p2.getNombre()%>">
				<br> <label>Vida</label>
				<div class="progress" style="height: 38px;">
					<div class="progress-bar bg-success" role="progressbar"
						style="<%=widthVida2%>;font-size: 20px;"
						aria-valuenow="<%=porcVida2%>" aria-valuemin="0"
						aria-valuemax="${p2.getVida()}"><%=String.valueOf(vida2).concat("/").concat(String.valueOf(p2.getVida()))%></div>
				</div>
				<br> <label>Energia</label>
				<div class="progress" style="height: 38px;">
					<div class="progress-bar bg-info" role="progressbar"
						style="<%=widthEnergia2%>;font-size: 20px;"
						aria-valuenow="<%=porcEnergia2%>" aria-valuemin="0"
						aria-valuemax="${p2.getEnergia()}"><%=String.valueOf(energia2).concat("/").concat(String.valueOf(p2.getEnergia()))%></div>
				</div>
				<br> <label>Defensa</label> <input name="defensa1" type="text"
					class="form-control" disabled value="<%=p2.getDefensa()%>">
				<br> <label>Evasion</label> <input name="evasion1" type="text"
					class="form-control" disabled value="<%=p2.getEvasion()%>">

			</div>
		</div>

	</form>
	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
		integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
		crossorigin="anonymous"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
		integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
		integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
		crossorigin="anonymous"></script>
</body>
</html>
