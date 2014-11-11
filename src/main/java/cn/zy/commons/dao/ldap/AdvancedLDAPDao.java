package cn.zy.commons.dao.ldap;

import org.springframework.ldap.core.LdapTemplate;

import javax.annotation.Resource;
import javax.naming.Name;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author zy
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AdvancedLDAPDao<T> {

	@Resource
	protected LdapTemplate ldapTemplate;

	private Class<T> clazz;

	public AdvancedLDAPDao() {
		this.clazz = getParameterClass();
	}

	@SuppressWarnings("unchecked")
	private Class<T> getParameterClass() {
		return (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public void save(T entry) {
		ldapTemplate.create(entry);
	}

	public void save(List<T> entries) {
		for (T entry : entries) {
			save(entry);
		}
	}

	public void save(T... entries) {
		for (T entry : entries) {
			save(entry);
		}
	}

	public void update(T entry) {
		ldapTemplate.update(entry);
	}

	public void update(List<T> entries) {
		for (T entry : entries) {
			update(entry);
		}
	}

	public void update(T... entries) {
		for (T entry : entries) {
			update(entry);
		}
	}

	public void delete(T entry) {
		ldapTemplate.delete(entry);
	}

	public void delete(List<T> entries) {
		for (T entry : entries) {
			delete(entry);
		}
	}

	public void delete(T... entries) {
		for (T entry : entries) {
			delete(entry);
		}
	}

	public T get(Name dn) {
		return ldapTemplate.findByDn(dn, clazz);
	}

	public List<T> list() {
		return ldapTemplate.findAll(clazz);
	}
}
