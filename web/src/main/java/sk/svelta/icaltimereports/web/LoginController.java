package sk.svelta.icaltimereports.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import sk.svelta.icaltimereports.ejb.UserFacade;
import sk.svelta.icaltimereports.entity.User;
import sk.svelta.icaltimereports.qualifiers.LoggedIn;
import sk.svelta.icaltimereports.web.util.JsfUtil;

/**
 * Session scoped controller for Login form.
 * This controller holds identity of logged in user.
 *
 * @author Jaroslav Švelta
 */
@Named
@SessionScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    @EJB
    private UserFacade ejbFacade;

    private User user;

    private String username;
    private String password;

    /**
     * Creates a new instance of LoginController
     */
    public LoginController() {
    }

    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the value of username
     *
     * @param username new value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Login method based on
     * <code>HttpServletRequest</code> and security realm.
     *
     * @return path to display
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String result;

        try {
            request.login(username, password);
            JsfUtil.addSuccessMessage("Login success! Welcome back!");

            this.user = ejbFacade.getUserByUsername(username);

            result = "index";
        } catch (ServletException e) {
            LOG.log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage("Invalid user or password. Login invalid!");
            result = "login";
        }

        return result;
    }

    /**
     * Log out user and display index page.
     * @return page to be displayed (index page)
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        String result;
        try {
            this.user = null;

            request.logout();
            // clear the session
            ((HttpSession) context.getExternalContext().getSession(false)).invalidate();
            JsfUtil.addErrorMessage("User successfully logged out!");
        } catch (ServletException ex) {
            LOG.log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage("Critical error during logout process");
        } finally {
            result = "/login";
        }

        return result;
    }

    /**
     * Produces @LoggedIn qualifier, which can be used for injecting logged in user.
     * @return idetntity of logged in user. If noone is logged in, it returns null
     */
    @Produces @LoggedIn
    public User getAuthenticatedUser() {
        return this.user;
    }

    /**
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return this.user != null;
    }

}
