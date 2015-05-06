package sk.svelta.icaltimereports.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import sk.svelta.icaltimereports.ejb.CalendarFacade;
import sk.svelta.icaltimereports.entity.Calendar;
import sk.svelta.icaltimereports.web.util.JsfUtil;
import sk.svelta.icaltimereports.web.util.PageNavigation;

/**
 * Controller for pages to manage calendars: List, View, Edit, Create
 *
 * @author Jaroslav Švelta
 */
@Named
@SessionScoped
public class CalendarController implements Serializable {

    private static final long serialVersionUID = 2;
    private static final Logger LOG = Logger.getLogger(CalendarController.class.getName());

    private ListDataModel<Calendar> items;
    private Calendar selected;

    @EJB
    private CalendarFacade ejbFacade;

    @Inject
    private LoginController loginController;

    /**
     * Creates new instance of ICalendarController
     */
    public CalendarController() {
    }

    private CalendarFacade getFacade() {
        return ejbFacade;
    }

    /**
     * @return <code>ListDateModel</code> filled with all {@link Calendar}s
     */
    public ListDataModel<Calendar> getItems() {
        if (items == null) {
            items = new ListDataModel<>(getFacade().findAll());
        }
        return items;
    }

    /**
     * Get {@link Calendar} to be displayed in View, Edit, Create page.
     * @return selected (current) {@link Calendar} or null if no user is selected
     */
    public Calendar getSelected() {
        if (selected == null) {
            selected = new Calendar();
            selected.setUser(loginController.getAuthenticatedUser());
        }
        return selected;
    }

    /**
     * Destroy items cache.
     */
    private void recreateModel() {
        items = null;
    }

    /**
     * Prepare data for List page.
     *
     * @return page to be displayed (List)
     */
    public PageNavigation prepareList() {
        recreateModel();
        return PageNavigation.LIST;
    }

    /**
     * Prepare data for Create page
     *
     * @return page to be displayed (Create)
     */
    public PageNavigation prepareCreate() {
        selected = new Calendar();
        selected.setUser(loginController.getAuthenticatedUser());
        return PageNavigation.CREATE;
    }


    public PageNavigation create() {
        PageNavigation result = null;
        try {
            getFacade().create(selected);
            JsfUtil.addSuccessMessage("Calendar was successfully created.");
            recreateModel();
            result = PageNavigation.LIST;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            JsfUtil.addErrorMessage(e, "An unexpected error occurred during calendar creation. Please try again.");
        }
        return result;
    }

    public void delete() {
        Calendar cal = getItems().getRowData();
        getFacade().remove(cal);
        recreateModel();
    }

}