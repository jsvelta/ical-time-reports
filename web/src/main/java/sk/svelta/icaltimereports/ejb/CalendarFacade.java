package sk.svelta.icaltimereports.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import sk.svelta.icaltimereports.entity.Calendar;
import sk.svelta.icaltimereports.entity.Calendar_;
import sk.svelta.icaltimereports.entity.User;

/**
 *
 * @author jsvelta
 */
@Stateless
public class CalendarFacade extends AbstractFacade<Calendar> {

    @PersistenceContext(unitName = "icaltimereports_pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalendarFacade() {
        super(Calendar.class);
    }

    public List<Calendar> findByUser(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Calendar> cq = cb.createQuery(Calendar.class);
        Root<Calendar> calendar = cq.from(Calendar.class);
        cq.select(calendar);
        cq.where(cb.equal(calendar.get(Calendar_.user), user));
        return getEntityManager().createQuery(cq).getResultList();
    }

}
