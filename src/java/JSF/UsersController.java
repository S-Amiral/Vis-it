package JSF;

import Entities.Groups;
import Entities.Users;
import JSF.util.JsfUtil;
import JSF.util.PaginationHelper;
import SessionBeans.UsersFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named("usersController")
@SessionScoped
public class UsersController implements Serializable {

    private Users userLogged;
    private String usernameLogin;
    private String passwordLogin;

    public String getUsernameLogin() {
        return usernameLogin;
    }

    public void setUsernameLogin(String usernameLogin) {
        this.usernameLogin = usernameLogin;
    }

    public String getPasswordLogin() {
        return passwordLogin;
    }

    public void setPasswordLogin(String passwordLogin) {
        this.passwordLogin = passwordLogin;
    }

    public Users getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(Users userLogged) {
        this.userLogged = userLogged;
    }
    private Users current;
    private DataModel items = null;
    @EJB
    private SessionBeans.UsersFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public UsersController() {
    }

    public Users getSelected() {
        if (current == null) {
            current = new Users();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UsersFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Users) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Users();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsersCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Users) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsersUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Users) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsersDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Users getUsers(java.lang.String id) {
        return ejbFacade.find(id);
    }

    public String login() {
        String page = "/index?faces-redirect=true";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(usernameLogin, passwordLogin);
            userLogged = getUsers(usernameLogin);
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            System.out.println(session.getId());
            session.setAttribute("userLogged", userLogged);
            /*session.setAttribute("username", usernameLogin);
            System.out.println("AuthentificationBeans : Login requested");*/
        } catch (ServletException e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage("Votre email/mot de passe est incorrect"));
            page = "/login?faces-redirect=true";
        }
        return page;
    }

    public String logout() {
        String page = "/index?faces-redirect=true";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);

        try {
            session.invalidate();
            request.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return page;
    }

    @FacesConverter(forClass = Users.class)
    public static class UsersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsersController controller = (UsersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usersController");
            return controller.getUsers(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Users) {
                Users o = (Users) object;
                return getStringKey(o.getUsername());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Users.class.getName());
            }
        }

    }

    public String page(int page) {
        getPagination().setPage(page - 1);
        recreateModel();
        return "List";
    }

    public int getNumberOfPages() {
        return (int) Math.ceil(getFacade().count() / (double) (getPagination().getPageSize()));
    }

    public String setAdmin(String username) {
        current = getUsers(username);
        ejbFacade.updateUserToAdmin(username);
        Groups groupAdmin = new Groups();
        groupAdmin.setGroupName("admin");
        List<Groups> currentGroups = current.getGroups();
        currentGroups.add(groupAdmin);
        current.setGroups(currentGroups);
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(username + " est devenu administrateur");
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String removeAdmin(String username) {
        current = getUsers(username);
        ejbFacade.removeUserToAdmin(username);
        List<Groups> currentGroups = current.getGroups();
        currentGroups.removeIf(group -> group.getGroupName().equals("admin"));
        current.setGroups(currentGroups);
        
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(username + " n'est plus administrateur");
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String setStandard(String username) {
        current = getUsers(username);
        ejbFacade.updateUserToStandard(username);
        Groups groupStandard = new Groups();
        groupStandard.setGroupName("standard");
        List<Groups> currentGroups = current.getGroups();
        currentGroups.add(groupStandard);
        current.setGroups(currentGroups);
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(username + " est devenu membre standard");
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String removeStandard(String username) {
        current = getUsers(username);
        ejbFacade.removeUserToStandard(username);
        List<Groups> currentGroups = current.getGroups();
        currentGroups.removeIf(group -> group.getGroupName().equals("standard"));
        current.setGroups(currentGroups);
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(username + " n'est plus un membre standard");
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String setModerator(String username) {
        current = getUsers(username);
        ejbFacade.updateUserToModerator(username);
        Groups groupModerator = new Groups();
        groupModerator.setGroupName("moderator");
        List<Groups> currentGroups = current.getGroups();
        currentGroups.add(groupModerator);
        current.setGroups(currentGroups);
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(username + " est devenu modérateur");
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String removeModerator(String username) {
        current = getUsers(username);
        ejbFacade.removeUserToModerator(username);
        List<Groups> currentGroups = current.getGroups();
        currentGroups.removeIf(group -> group.getGroupName().equals("moderator"));
        current.setGroups(currentGroups);
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(username + " n'est plus un moderateur");
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public boolean isUserStandard(String user) {
        Users test = getUsers(user);
        if (test == null) {
            return false;
        }
        boolean isStandard = false;
        for (Groups group : test.getGroups()) {
            if (group.getGroupName().equals("standard")) {
                isStandard = true;
            }
        }
        return isStandard;
    }

    public boolean isUserModerator(String user) {
        Users test = getUsers(user);
        if (test == null) {
            return false;
        }
        boolean isModerator = false;
        for (Groups group : test.getGroups()) {
            if (group.getGroupName().equals("moderator")) {
                isModerator = true;
            }
        }
        return isModerator;
    }

    public boolean isUserAdmin(String user) {
        Users test = getUsers(user);
        if (test == null) {
            return false;
        }
        boolean isAdmin = false;
        for (Groups group : test.getGroups()) {
            if (group.getGroupName().equals("admin")) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }
}
