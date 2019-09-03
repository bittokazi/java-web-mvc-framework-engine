package mvc.ca.orm;

import java.util.List;

public interface MvcORM<T> {
	T findOne(Long id);
	List<T> findAll();
	String getName();
}
