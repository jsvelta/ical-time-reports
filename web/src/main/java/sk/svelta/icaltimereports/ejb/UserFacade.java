package sk.svelta.icaltimereports.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sk.svelta.icaltimereports.entity.User;

/**
 * Facade to manipulate with user data.
 *
 * @author Jaroslav Švelta
 */
@Stateless
public class UserFacade extends AbstractFacade<User>{

    @PersistenceContext(unitName = "icaltimereports_pu")
    private EntityManager em;

    /**
     * Creates new instance of UserFacade
     */
    public UserFacade() {
        super(User.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * @param username to identify {@link User}
     * @return {@link User} identified by its usernam, or null if such user does not exists
     */
    public User getUserByUsername(String username) {
        Query query = em.createNamedQuery("User.findByUsername");
        query.setParameter("username", username);

        if (query.getResultList().size() > 0) {
            return (User) query.getSingleResult();
        } else {
            return null;
        }
    }

}
