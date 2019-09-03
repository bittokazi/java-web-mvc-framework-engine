package mvc.ca;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.ca.base.ControllerBase;
import mvc.ca.base.RouteingInterface;

public class BootLoader extends HttpServlet implements RouteingInterface {
	private static final long serialVersionUID = 1L;
	
	ControllerBase controllerBase = ControllerBase.getInstance();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			controllerBase.searchRoute(request.getRequestURI(), this, request, response);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		try {
			controllerBase.searchRoute(request.getRequestURI(), this, request, response);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void foundRouteResult(boolean found, HttpServletResponse response) {
		if(!found) {
			PrintWriter out;
			try {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				out = response.getWriter();
				out.println("<html><head><title>404 - Route Not Found</title></head><body><h1><center>404 - Route Not Found</center></h1></body></html>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
