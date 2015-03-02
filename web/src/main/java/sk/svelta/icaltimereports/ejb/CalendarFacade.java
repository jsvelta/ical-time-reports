package sk.svelta.icaltimereports.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sk.svelta.icaltimereports.entity.Calendar;

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

}
