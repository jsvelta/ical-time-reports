package sk.svelta.icaltimereports.web;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import sk.svelta.icaltimereports.ejb.UserFacade;
import sk.svelta.icaltimereports.entity.User;
import sk.svelta.icaltimereports.web.util.JsfUtil;
import sk.svelta.icaltimereports.web.util.MD5Util;
import sk.svelta.icaltimereports.web.util.PageNavigation;

/**
 * Controller for pages to manage users:
 * List, View, Edit, Create
 *
 * @author Jaroslav Švelta
 */
@Named
@SessionScoped
public class UserController implements Serializable {

    private static final long serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    private ListDataModel<User> items;
    private User selected;
    private int selectedItemIndex;

    @EJB
    private UserFacade ejbFacade;

    @Inject
    private LoginController loginController;

    /**
     * Creates new instance of UserController
     */
    public UserController() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    /**
     * @return <code>ListDateModel</code> filled with all {@link User}s
     */
    public ListDataModel<User> getItems() {
        if (this.items == null) {
            this.items = new ListDataModel<>(getFacade().findAll());
        }
        return this.items;
    }

    /**
     * Get {@link User} to be displayed in View, Edit, Create page.
     * @return selected (current) {@link User} or null if no user is selected
     */
    public User getSelected() {
        if (this.selected == null) {
            this.selected = new User();
            this.selectedItemIndex = -1;
        }
        return this.selected;
    }

    /**
     * Destroy items cache.
     */
    private void recreateModel() {
        this.items = null;
    }

    /**
     * Prepare data for List page.
     *
     * @return page to be displayed (List)
     */
    public String prepareList() {
        if (loginController.isLoggedIn()) {
            recreateModel();
            return PageNavigation.LIST.getText();
        } else {
            return "/login";
        }
    }

    /**
     * Prepare data for Create page
     *
     * @return page to be displayed (Create)
     */
    public PageNavigation prepareCreate() {
        this.selected = new User();
        this.selectedItemIndex = -1;
        return PageNavigation.CREATE;
    }

    /**
     * Prepare data for Edit page
     *
     * @return  page to be displayed (Edit)
     */
    public PageNavigation prepareEdit() {
        this.selected = getItems().getRowData();
        this.selectedItemIndex = getItems().getRowIndex();
        return PageNavigation.EDIT;
    }

    /**
     * Prepare data for View page
     *
     * @return  page to be displayed (View)
     */
    public PageNavigation prepareView() {
        this.selected = getItems().getRowData();
        this.selectedItemIndex = getItems().getRowIndex();
        return PageNavigation.VIEW;
    }

    /**
     * Check if exists {@link User} with same <code>username</code> as <code>u.username</code>.
     * @param p {@link User} to be checked
     * @return true if {@link User} exists, false otherwise
     */
    private boolean isUserDuplicated(User u) {
        return (getFacade().getUserByUsername(u.getUsername()) != null);
    }

    /**
     * Create a new user with data stored in <code>selected</code> property.
     *
     * @return page to be displayed (List after successfuly created user, null otherwise)
     */
    public String create() {
        String result = null;
        try {
            if (!isUserDuplicated(selected)) {
                selected.setPassword(MD5Util.generateHash(selected.getPassword()));
                getFacade().create(selected);
                JsfUtil.addSuccessMessage("Customer was successfully created.");
                recreateModel();
                if (loginController.isLoggedIn()) {
                    result = PageNavigation.LIST.getText();
                } else {
                    result = "/login";
                }
            } else {
                JsfUtil.addErrorMessage("Customer username is already in our database. Please try other username.");
            }
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            JsfUtil.addErrorMessage("An unexpected error occurred during customer creation. Please try again.");
        }
        return result;
    }

    /**
     * Delete selected {@link User} and display next {@link User}
     * from the list. If it was the last {@link User} in the list,
     * display <code>List</code> page.
     *
     * @return page name to be displayed (View or List)
     */
    public PageNavigation destroyAndView() {
        performDestroy();
        recreateModel();
        updateSelectedItem();
        if (this.selectedItemIndex >= 0) {
            return PageNavigation.VIEW;
        } else {
            return PageNavigation.LIST;
        }
    }

    /**
     * Delete selected user
     */
    private void performDestroy() {
        try {
            getFacade().remove(this.selected);
            JsfUtil.addSuccessMessage("User was successfully deleted.");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e.getMessage());
        }
    }

    private void updateSelectedItem() {
        int count = getFacade().count();
        if (this.selectedItemIndex >= count) {
            this.selectedItemIndex = count - 1;
        }
        if (this.selectedItemIndex >= 0) {
            this.selected = getFacade().findAll().get(this.selectedItemIndex);
        }
    }

}
