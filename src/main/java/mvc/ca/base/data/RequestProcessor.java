package mvc.ca.base.data;

import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;

public class RequestProcessor {
	
	private static RequestProcessor instance = null;
	
	private RequestProcessor() {
		
	}
	
	public static void bindJsonToModel(Parameter parameter, HttpServletRequest request, Object[] obj, int index) throws InstantiationException, IllegalAccessException {
		 if (request!=null && request.getContentType()!=null && request.getContentType().contains("application/json")) {
			 StringBuffer jb = new StringBuffer();
			 String line = null;
			 try {
				 BufferedReader reader = request.getReader();
				 while ((line = reader.readLine()) != null)
					 jb.append(line);
			 } catch (Exception e) {
				e.printStackTrace();  
			 }
			 obj[index] = new Gson().fromJson(jb.toString(), parameter.getType());
		 } else if (request!=null && request.getContentType()!=null && request.getContentType().contains("application/x-www-form-urlencoded")) {
			 obj[index] = RequestProcessor.populate(parameter.getType().newInstance() ,request);
		 } else {
			 obj[index] = parameter.getType().newInstance();
		 }
	}
	
	public static <T> T populate(Object object , HttpServletRequest request) {
	    try {

//	        object = (T) clazz.newInstance();
	        BeanUtils.populate(object, request.getParameterMap());

	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	    return (T) object;
	}

}
