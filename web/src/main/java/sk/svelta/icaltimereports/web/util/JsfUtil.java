package sk.svelta.icaltimereports.web.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Utility class with static functions for easier work with FacesContext
 *
 * @author Jaroslav Švelta
 */
public final class JsfUtil {

    /**
     * This class cannot be instancionated.
     */
    private JsfUtil() {
    }

    /**
     * Add error message to the FacesContext
     *
     * @param msg message to be added
     */
    public static void addErrorMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    /**
     * Add error message to the FacesContext
     *
     * @param e exception to be added
     * @param defaultMsg message to be added if exception contains no usable message
     */
    public static void addErrorMessage(Exception e, String defaultMsg) {
        String msg = e.getLocalizedMessage();
        if (msg == null || msg.isEmpty()) {
            msg = defaultMsg;
        }
        addErrorMessage(msg);
    }

    /**
     * Add success message to the FacesContext.
     *
     * @param msg message to be added
     */
    public static void addSuccessMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

}
