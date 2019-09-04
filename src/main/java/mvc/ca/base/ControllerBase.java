package mvc.ca.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.ca.base.data.Body;
import mvc.ca.base.data.RequestProcessor;

public class ControllerBase {
	
	private static ControllerBase instance = null;
	private List<RoutingClass> routingClassList;
	
	private ControllerBase() {
		this.routingClassList = new ArrayList<>();
	}
	
	public static ControllerBase getInstance() {
		if (instance == null) {
			instance = new ControllerBase();
		}
		return instance;
	}
	
	public void checkClass(Class<?> className) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(className.isAnnotationPresent(Controller.class)) {
			 Class constructorClasses[] = new Class[]{};
			 Constructor classToLoad = Class.forName(className.getName()).getConstructor(constructorClasses);
			 
			 for (Method method : className.getDeclaredMethods()) {
				 if(method.isAnnotationPresent(GET.class)) {
					 RoutingClass routingClass = new RoutingClass();
					 //routingClass.setObject(classToLoad.newInstance(classToLoad));
					 routingClass.setObject(className.newInstance());
					 routingClass.setMethod(method);
					 routingClass.setURL(((GET)method.getAnnotation(GET.class)).route());
					 routingClass.setRequestMethod("GET");
					 this.routingClassList.add(routingClass);
				 } else if(method.isAnnotationPresent(POST.class)) {
					 RoutingClass routingClass = new RoutingClass();
					 //routingClass.setObject(classToLoad.newInstance(classToLoad));
					 routingClass.setObject(className.newInstance());
					 routingClass.setMethod(method);
					 routingClass.setURL(((POST)method.getAnnotation(POST.class)).route());
					 routingClass.setRequestMethod("POST");
					 this.routingClassList.add(routingClass);
				 }
			 }
		}
	}
	
	public void searchRoute(String url, RouteingInterface routeingInterface, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		boolean found = false;
		Map<Integer, Integer> matchVote = new HashMap<>();
		Map<Integer, Object> routeParamsList = new HashMap<>();
		int index = 0;
		for(RoutingClass routingClass: this.routingClassList) {
			found = true;
			Map<String, String> routeParams = new HashMap<>();
			System.out.println("Rounte Check ["+url+"]["+(request.getContextPath().equals("/")? routingClass.getURL().split("/") : (request.getContextPath()+routingClass.getURL()))+"]");
			String urlTomatch[]  = request.getContextPath().equals("/")? routingClass.getURL().split("/") : (request.getContextPath()+routingClass.getURL()).split("/");
			String currentUrl[]  = request.getRequestURI().split("/");
			Integer voteCount = currentUrl.length;
			for(int i = 0; i<urlTomatch.length; i++) {
				if(i<currentUrl.length) {
					Pattern pattern = Pattern.compile("\\{(.*?)\\}");
					Matcher matcher = pattern.matcher(urlTomatch[i]);
					if(urlTomatch[i].equals("*")) {
						break;
					}
					else if (matcher.find()) {
						routeParams.put(matcher.group(1), currentUrl[i]);
					}
					else if(urlTomatch[i].equals(currentUrl[i])) {} 
					else {
						found = false;
						break;
					}
				} else {
					found = false;
					break;
				}
				voteCount--;
			}
			if(found && currentUrl.length > urlTomatch.length && urlTomatch[urlTomatch.length-1].equals("*")  && routingClass.getRequestMethod().equals(request.getMethod())) {
				matchVote.put(index, voteCount);
				routeParamsList.put(index, routeParams);
			}
			else if(found && currentUrl.length == urlTomatch.length && routingClass.getRequestMethod().equals(request.getMethod())) {
				matchVote.put(index, voteCount);
				routeParamsList.put(index, routeParams);
			}
//			if(found && routingClass.getRequestMethod().equals(request.getMethod())) {
//				System.out.println("Rounte Found ["+url+"]");
//				for(Entry<String, String> entry: routeParams.entrySet()) {
//					request.setAttribute(entry.getKey(), entry.getValue());
//				}
//				routingClass.getMethod().invoke(routingClass.getObject(), request, response);
//				found = true;
//				break;
//			}
			index++;
		}
		int electedIndex = -1;
		int minVote = request.getRequestURI().split("/").length;
		for(Entry<Integer, Integer> entry: matchVote.entrySet()) {
			if(entry.getValue()<minVote) {
				electedIndex = entry.getKey();
				minVote = entry.getValue();
			}
		}
		System.out.println(electedIndex + " - " + minVote);
		if(electedIndex>-1) {
			for(Entry<Integer, Object> entry: routeParamsList.entrySet()) {
				for(Entry<String, String> entryParam: ((Map<String, String>) entry.getValue()).entrySet()) {
					request.setAttribute(entryParam.getKey(), entryParam.getValue());
				}
			}
			Object[] obj = new Object[this.routingClassList.get(electedIndex).getMethod().getParameterCount()];
			int paramCount = 0;
			for(Parameter parameter: this.routingClassList.get(electedIndex).getMethod().getParameters()) {
				if(parameter.isAnnotationPresent(Body.class)) {
					RequestProcessor.bindJsonToModel(parameter, request, obj, paramCount);
				} else {
					if(parameter.getType().getName().equals("javax.servlet.http.HttpServletRequest")) obj[paramCount] = request;
					if(parameter.getType().getName().equals("javax.servlet.http.HttpServletResponse")) obj[paramCount] = response;
				}
				paramCount++;
			}
			this.routingClassList.get(electedIndex).getMethod().invoke(this.routingClassList.get(electedIndex).getObject(), obj);
		} else {
			routeingInterface.foundRouteResult(false, response);
		}
	}

	public List<RoutingClass> getRoutingClassList() {
		return routingClassList;
	}

	public void setRoutingClassList(List<RoutingClass> routingClassList) {
		this.routingClassList = routingClassList;
	}
	
	
}
