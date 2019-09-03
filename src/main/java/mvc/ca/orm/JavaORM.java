package mvc.ca.orm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JavaORM {
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public <T> T getAllRepository(Class clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(),  new Class[] { clazz }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String method_name = method.getName();
				System.out.println(method_name);
                Class<?>[] classes = method.getParameterTypes();
                if (method_name.equals("getName") && args==null) {
                		return "LolM";
                }
                	return null;
			}
		});
	}
}
