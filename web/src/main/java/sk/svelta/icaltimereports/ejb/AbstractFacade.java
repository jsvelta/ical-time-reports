package sk.svelta.icaltimereports.ejb;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import sk.svelta.icaltimereports.web.util.JsfUtil;

/**
 *
 * @author Jaroslav Švelta
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade() {
    }

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * @return typed <code>List</code> of all <code>entities</code>.
     */
    public List<T> findAll() {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Find entity by id
     * @param id entity id
     * @return entity
     */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    private boolean validateConstraints(T entity) {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (constraintViolations.size() > 0) {
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> cv = iterator.next();
                JsfUtil.addErrorMessage(cv.getRootBeanClass().getSimpleName() + "." + cv.getPropertyPath() + " " + cv.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Persists new entity in entity manager.
     *
     * @param entity <code>entity</code> to be persisted.
     */
    public void create(T entity) {
        if (validateConstraints(entity)) {
            getEntityManager().persist(entity);
        }
    }

    /**
     * Remove <code>entity</code> from entity manager.
     *
     * @param entity <code>entity</code> to be removed
     */
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * @return number of entities
     */
    public int count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(entityClass)));
        return getEntityManager().createQuery(cq).getSingleResult().intValue();
    }

}
