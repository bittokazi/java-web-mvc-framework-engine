package mvc.ca.base;

import javax.servlet.http.HttpServletResponse;

public interface RouteingInterface {
	void foundRouteResult(boolean found, HttpServletResponse response);
}
