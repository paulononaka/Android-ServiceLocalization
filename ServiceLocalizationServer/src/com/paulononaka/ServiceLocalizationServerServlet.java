package com.paulononaka;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class ServiceLocalizationServerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("text/plain");

		if (req.getParameterMap().containsKey("lat") & req.getParameterMap().containsKey("lng")) {

			String lat = req.getParameter("lat");
			String lng = req.getParameter("lng");
			
			resp.getWriter().println("lat: " + lat + " / lng: " + lng);
		} else {
			resp.getWriter().println("entre com os parametros lat e lng");
		}
	}
}
