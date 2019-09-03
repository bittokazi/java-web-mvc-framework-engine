package mvc.ca.base.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import mvc.ca.base.Controller;
import mvc.ca.base.GET;
import mvc.ca.base.POST;
import mvc.ca.base.RoutingClass;

public class ServiceInjector {
	private static ServiceInjector instance = null;
	private List<InjectableItem> injectableItems;
	
	private ServiceInjector() {
		this.injectableItems = new ArrayList<>();
	}
	
	public static ServiceInjector getInstance() {
		if (instance == null) {
			instance = new ServiceInjector();
		}
		return instance;
	}
	
	public void intilizeInjectables(Class<?> className) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(className.isAnnotationPresent(Injectable.class)) {
			Class constructorClasses[] = new Class[]{};
			Constructor classToLoad = Class.forName(className.getName()).getConstructor(constructorClasses);
			
			InjectableItem injectableItem = new InjectableItem();
			injectableItem.setClassName(className);
			injectableItem.setObject(className.newInstance());
			
			this.injectableItems.add(injectableItem);
		}
	}
	
	public void injectServices(Object object) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : object.getClass().getDeclaredFields()) {
			if(field.isAnnotationPresent(InjectService.class)) {
				for(InjectableItem injectableItem: this.injectableItems) {
					if(field.getType().getName().equals(injectableItem.getClassName().getName())) {
						field.setAccessible(true);
						field.set(object, injectableItem.getObject());
						break;
					}
				}
			}
		}
	}

	public List<InjectableItem> getInjectableItems() {
		return injectableItems;
	}

	public void setInjectableItems(List<InjectableItem> injectableItems) {
		this.injectableItems = injectableItems;
	}
	
}
