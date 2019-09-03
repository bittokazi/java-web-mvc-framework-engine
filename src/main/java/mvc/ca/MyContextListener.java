package mvc.ca;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;

import com.google.common.reflect.ClassPath;

import mvc.ca.base.ControllerBase;
import mvc.ca.base.RoutingClass;
import mvc.ca.base.service.InjectableItem;
import mvc.ca.base.service.ServiceInjector;
import mvc.ca.orm.JavaORM;

@WebListener
public class MyContextListener implements ServletContextListener {
	
	ControllerBase controllerBase = ControllerBase.getInstance();
	ServiceInjector serviceInjector = ServiceInjector.getInstance();

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
            ServletContext context = event.getServletContext();

            Dynamic dynamic = context.addServlet("SystemEntryPoint", BootLoader.class);
            dynamic.addMapping("/*");
            
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("mvc-ca-app.properties");
            System.out.println("Found MVC CA Configuration: "+(in != null));
            Properties properties = new Properties();
            try {
	    			properties.load(in);
	    			String pkg = properties.getProperty("mvc.ca.scan");
	    			System.out.println("Searching In "+pkg);
	    			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
    				  if (info.getName().startsWith(pkg+".")) {
    				    final Class<?> clazz = info.load();
    				    System.out.println(clazz.getName());
    				    controllerBase.checkClass(clazz);
    				    serviceInjector.intilizeInjectables(clazz);
    				  }
    				}
	    			for(InjectableItem injectableItem: this.serviceInjector.getInjectableItems()) {
	    				serviceInjector.injectServices(injectableItem.getObject());
	    			}
	    			for(RoutingClass routingClass: this.controllerBase.getRoutingClassList()) {
	    				serviceInjector.injectServices(routingClass.getObject());
	    			}
	    			JavaORM javaORM = new JavaORM();
//	    			System.out.println(""+((OrmTest)javaORM.getAllRepository(OrmTest.class)).getName());
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
    }
    
    

}