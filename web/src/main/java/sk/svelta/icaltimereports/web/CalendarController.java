package sk.svelta.icaltimereports.web;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
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
    private int selectedItemIndex;
    private Part file;

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
            selectedItemIndex = -1;
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
        selectedItemIndex = -1;
        return PageNavigation.CREATE;
    }


    public PageNavigation create() {
        PageNavigation result = null;
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), "utf-8")) {
        StringBuilder content = new StringBuilder();
            char[] buffer = new char[100];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                content.append(buffer, 0, length);
            }
            selected.setContent(content.toString());
            selected.setFileName(file.getSubmittedFileName());
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

    /**
     * Get list of calnedars in format for selectOneListbox component
     *
     * @return list of calendars
     */
    public SelectItem[] getSelectOneItems() {
        List<Calendar> calendars = getFacade().findAll();
        SelectItem[] items = new SelectItem[calendars.size()];
        int i = 0;
        for (Calendar calendar : calendars) {
            items[i++] = new SelectItem(calendar.getId(), calendar.getName());
        }
        return items;
    }

     /**
     * Get the value of file
     *
     * @return the value of file
     */
   public Part getFile() {
        return file;
    }

    /**
     * Set the value of file
     *
     * @param file new value of file
     */
    public void setFile(Part file) {
        this.file = file;
    }

}