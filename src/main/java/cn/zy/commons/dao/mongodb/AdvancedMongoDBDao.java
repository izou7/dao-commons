package cn.zy.commons.dao.mongodb;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Provide advanced MongoDB access functions.
 * 
 * @author zy
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AdvancedMongoDBDao<T> {

	/** Inject MongoDB Operations. */
	@Resource
	protected MongoOperations operations;

	/** Class type. */
	private final Class<T> clazz;

	/**
	 * Default constructor.
	 */
	public AdvancedMongoDBDao() {

		// Finalize class type.
		clazz = this.getParameterClass();
	}

	/**
	 * Get current class type.
	 * 
	 * @return Current instance class.
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getParameterClass() {
		return (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Persist the given document.
	 * 
	 * @param entity
	 *            The document to persist.
	 */
	public void save(T entity) {
		operations.save(entity);
	}

	/**
	 * Persist all documents in the given collection.
	 * 
	 * @param entities
	 *            The documents collection to persist.
	 */
	public void save(Collection<T> entities) {
		for (T entity : entities) {
			save(entity);
		}
	}

	/**
	 * Persist all given transient documents.
	 * 
	 * @param entities
	 *            The transient documents array to persist.
	 */
	public void save(@SuppressWarnings("unchecked") T... entities) {
		for (T entity : entities) {
			save(entity);
		}
	}

	/**
	 * Update document by id.
	 * 
	 * @param id
	 *            Document ID.
	 * @param contents
	 *            Content to update.
	 */
	public void update(Serializable id, Map<String, Object> contents) {
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update();
		for (Entry<String, Object> content : contents.entrySet()) {
			update.set(content.getKey(), content.getValue());
		}
		this.operations.updateFirst(query, update, clazz);
	}

	/**
	 * Update by restrictions and contents.
	 * 
	 * @param restrictions
	 *            Query restrictions.
	 * @param contents
	 *            Content to update.
	 */
	public void update(Map<String, Object> restrictions,
			Map<String, Object> contents) {

		Query query = new Query();
		for (Entry<String, Object> restriction : restrictions.entrySet()) {
			query.addCriteria(Criteria.where(restriction.getKey()).is(
					restriction.getValue()));
		}

		Update update = new Update();
		for (Entry<String, Object> content : contents.entrySet()) {
			update.set(content.getKey(), content.getValue());
		}

		this.operations.updateMulti(query, update, clazz);
	}

	/**
	 * Delete by id.
	 * 
	 * @param id
	 */
	public void delete(Serializable id) {
		operations.remove(new Query(Criteria.where("id").is(id)), clazz);
	}

	/**
	 * Returns a document with the given id mapped onto the given class.
	 * 
	 * @param id
	 *            the identifier of the persistent instance
	 * @return the persistent instance, or null if not found
	 */
	public T get(Serializable id) {
		return operations.findById(id, clazz);
	}

	/**
	 * List all document.
	 * 
	 * @return All document in database.
	 */
	public List<T> list() {
		return operations.findAll(clazz);
	}

	/**
	 * List document by restrictions.<br>
	 * Between each key in the restrictions map, it using 'AND' logic, and if
	 * the value is an array, it using 'OR'.
	 * 
	 * @param restrictions
	 *            Query restrictions.
	 * @return The document list.
	 */
	public List<T> list(Map<String, Object> restrictions) {

		Query query = new Query();
		for (Entry<String, Object> current : restrictions.entrySet()) {

			String key = current.getKey();
			Object value = current.getValue();

			if (value instanceof Object[]) {
				query.addCriteria(Criteria.where(key).in((Object[])value));
			} else {
				query.addCriteria(Criteria.where(key).is(value));
			}
		}
		return this.operations.find(query, clazz);
	}

	/**
	 * List all document by pagination.
	 * 
	 * @param firstResult
	 *            the index of the first result object to be retrieved (numbered
	 *            from 0)
	 * @param maxResults
	 *            the maximum number of result objects to retrieve (or <=0 for
	 *            no limit)
	 * @return containing 0 or more persistent documents
	 */
	public List<T> list(int firstResult, int maxResults) {
		Query query = new Query().skip(firstResult).limit(maxResults);
		return operations.find(query, clazz);
	}

	/**
	 * List document by restrictions by pagination.<br>
	 * Between each key in the restrictions map, it using 'AND' logic, and if
	 * the value is an array, it using 'OR'.
	 * 
	 * @param restrictions
	 *            Query restrictions.
	 * @param firstResult
	 *            the index of the first result object to be retrieved (numbered
	 *            from 0)
	 * 
	 * @param maxResults
	 *            the maximum number of result objects to retrieve (or <=0 for
	 *            no limit)
	 * @return The document list.
	 */
	public List<T> list(Map<String, Object> restrictions, int firstResult,
			int maxResults) {

		Query query = new Query();
		for (Entry<String, Object> current : restrictions.entrySet()) {

			String key = current.getKey();
			Object value = current.getValue();

			if (value instanceof Object[]) {
				query.addCriteria(Criteria.where(key).in((Object[]) value));
			} else {
				query.addCriteria(Criteria.where(key).is(value));
			}
		}
		query.skip(firstResult).limit(maxResults);
		return this.operations.find(query, clazz);
	}

	/**
	 * Get total document count in the database.
	 * 
	 * @return document total count.
	 */
	public long count() {
		return operations.count(new Query(), clazz);
	}

	/**
	 * Get document count satisfy the restrictions in the database.
	 * 
	 * @param restrictions
	 *            Count restrictions.
	 * @return Satisfied result count.
	 */
	public long count(Map<String, Object> restrictions) {

		Query query = new Query();
		for (Entry<String, Object> current : restrictions.entrySet()) {

			String key = current.getKey();
			Object value = current.getValue();

			if (value instanceof Object[]) {
				query.addCriteria(Criteria.where(key).in((Object[])value));
			} else {
				query.addCriteria(Criteria.where(key).is(value));
			}
		}
		return this.operations.count(query, clazz);
	}
}