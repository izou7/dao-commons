package cn.zy.commons.dao.hibernate;

import org.slf4j.LoggerFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * Provide advanced hibernate  access functions.
 *
 * @author zy
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AdvancedHibernateDao<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedHibernateDao.class);

	@Resource
	private SessionFactory sessionFactory;

	/** Define current entity class. */
	private final Class<T> clazz;

	/** Default constructor. */
	public AdvancedHibernateDao() {
		this.clazz = getParameterClass();
	}

	/**
	 * Persist the given transient instance.
	 *
	 * @param entity A transient instance.
	 */
	public void save(T entity) {
		sessionFactory.getCurrentSession().save(entity);
	}

	/**
	 * Persist all given transient instances in the collection.
	 *
	 * @param entities Collection of transient instances.
	 */
	public void save(Collection<T> entities) {
		for (T entity : entities) {
			save(entity);
		}
	}

	/**
	 * Persist the given transient instances.
	 *
	 * @param entities Array of transient instances.
	 */
	@SuppressWarnings("unchecked")
	public void save(T... entities) {
		for (T entity : entities) {
			save(entity);
		}
	}

	/**
	 * Update the persistent instance with the identifier of the given detached instance.
	 *
	 * @param entity A detached instance containing updated state.
	 */
	public void update(T entity) {
		sessionFactory.getCurrentSession().update(entity);
	}

	/**
	 * Update all persistent instances in the collection.
	 *
	 * @param entities Collection of detached instances containing updated state.
	 */
	public void update(Collection<T> entities) {
		for (T entity : entities) {
			update(entity);
		}
	}

	/**
	 * Update all persistent instances in the array.
	 *
	 * @param entities Array of detached instances containing updated state.
	 */
	public void update(T... entities) {
		for (T entity : entities) {
			update(entity);
		}
	}

	/**
	 * Either {@link #save(Object)} or {@link #update(Object)} the given instance.
	 *
	 * @param entity A transient or detached instance containing new or updated state
	 */
	public void saveOrUpdate(T entity) {
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
	}

	/**
	 * Either {@link #save(Object)} or {@link #update(Object)} all instances in given collection.
	 *
	 * @param entities Collection of transient or detached instances containing new or updated state.
	 */
	public void saveOrUpdate(Collection<T> entities) {
		for (T entity : entities) {
			saveOrUpdate(entity);
		}
	}

	/**
	 * Either {@link #save(Object)} or {@link #update(Object)} all instances in given array.
	 *
	 * @param entities Array of transient or detached instances containing new or updated state.
	 */
	public void saveOrUpdate(T... entities) {
		for (T entity : entities) {
			saveOrUpdate(entity);
		}
	}

	/**
	 * Copy the state of the given object onto the persistent object with the same identifier.
	 * If there is no persistent instance currently associated with the session, it will be loaded.
	 * Return the persistent instance.
	 * If the given instance is unsaved, save a copy of and return it as a newly persistent instance.
	 * The given instance does not become associated with the session.
	 *
	 * @param entity A detached instance with state to be copied.
	 * @return An updated persistent instance.
	 */
	@SuppressWarnings("unchecked")
	public T merge(T entity) {
		return (T) sessionFactory.getCurrentSession().merge(entity);
	}

	/**
	 * Remove a persistent instance from the database.
	 *
	 * @param entity The persistent instance to be removed.
	 */
	public void delete(T entity) {
		if (entity != null) {
			sessionFactory.getCurrentSession().delete(entity);
		}
	}

	/**
	 * Remove all persistent instances in the collection from the database.
	 *
	 * @param entities Collection of persistent instances to be removed.
	 */
	public void delete(Collection<T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	/**
	 * Remove all persistent instances in the array from the database.
	 *
	 * @param entities Array of persistent instances tobe removed.
	 */
	public void delete(T... entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	/**
	 * Remove the persistent instance with the given identifier from the database.
	 *
	 * @param id Identifier
	 */
	public void delete(Serializable id) {
		delete(get(id));
	}

	/**
	 * Remove all persistent instance with the given identifiers in given array from the database.
	 *
	 * @param ids Identifiers.
	 */
	public void delete(Serializable... ids) {
		for (Serializable id : ids) {
			delete(id);
		}
	}

	/**
	 * Return the persistent instance of the given entity class with the given identifier,
	 * or null if there is no such persistent instance.
	 *
	 * @param id Identifier.
	 * @return A persistent instance of null.
	 */
	@SuppressWarnings("unchecked")
	public T get(Serializable id) {
		return (T) sessionFactory.getCurrentSession().get(clazz, id);
	}

	/**
	 * Get all record in database.
	 *
	 * @return All persistent instances in database.
	 */
	@SuppressWarnings("unchecked")
	public List<T> list() {
		return sessionFactory.getCurrentSession().createCriteria(clazz).list();
	}

	/**
	 * Get a part of record in database.
	 *
	 * @param firstResult The first result to retrieve, numbered from 0.
	 * @param maxResults  The maximum number of results.
	 * @return Persistent instances list.
	 */
	@SuppressWarnings("unchecked")
	public List<T> list(int firstResult, int maxResults) {
		return sessionFactory.getCurrentSession().createCriteria(clazz)
				.setFirstResult(firstResult)
				.setMaxResults(maxResults)
				.list();
	}

	/**
	 * Get record total count in database.
	 *
	 * @return Record count.
	 */
	public long count() {
		return (Long) sessionFactory.getCurrentSession().createCriteria(clazz)
				.setProjection(Projections.rowCount())
				.list().iterator().next();
	}

	/**
	 * Execute an HQL query, binding a number of values to "?" parameters in the query string,
	 * then paging with first result and max results.
	 *
	 * @param hql         A query expression in Hibernate query language.
	 * @param firstResult The first result for paging.
	 * @param maxResults  The max results for paging.
	 * @param params      The values of the parameters.
	 * @return A {@link java.util.List} containing the results of the query execution.
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByHQL(String hql, int firstResult, int maxResults, Object... params) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		if (maxResults > 0) {
			query.setFirstResult(firstResult).setMaxResults(maxResults);
		}
		return query.list();
	}

	/**
	 * Get current session to use.
	 *
	 * @return Current session in session factory.
	 */
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	private Class<T> getParameterClass() {
		return (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}
}