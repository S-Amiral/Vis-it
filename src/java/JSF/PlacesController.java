package JSF;

import Entities.Places;
import JSF.util.JsfUtil;
import JSF.util.PaginationHelper;
import SessionBeans.PlacesFacade;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("placesController")
@SessionScoped
public class PlacesController implements Serializable {

    private boolean isInFullItems = false;
    private boolean isInMyItems = false;
    private boolean isInValidationItems = false;
    private boolean isInSearchItems = false;
    private Places current;
    private DataModel items = null;
    public static final int VALIDATION = 1;
    public static final int SEARCH = 2;

    @EJB
    private SessionBeans.PlacesFacade ejbFacade;
    @EJB
    private SessionBeans.UsersFacade userEjbFacade;
    private PaginationHelper pagination;

    private int selectedItemIndex;

    public PlacesController() {
    }

    public Places getSelected() {
        if (current == null) {
            current = new Places();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PlacesFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null || isInMyItems || isInValidationItems ) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    DataModel dataModel = new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    return dataModel;
                }
            };
        }
        return pagination;
    }

    public PaginationHelper getPagination(int option) {
        if (pagination == null || isInMyItems || isInFullItems) {
            switch (option) {
                case VALIDATION:
                    pagination = new PaginationHelper(10) {

                        @Override
                        public int getItemsCount() {
                            return getFacade().count(option);
                        }

                        @Override
                        public DataModel createPageDataModel() {
                            DataModel dataModel = new ListDataModel(getFacade().findRange(option, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                            return dataModel;
                        }
                    };
                    break;
                default:
                    return pagination;

            }

        }
        return pagination;
    }

    public PaginationHelper getPagination(String publisher) {
        if (pagination == null || isInFullItems || isInValidationItems) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count(publisher);
                }

                @Override
                public DataModel createPageDataModel() {
                    DataModel dataModel = new ListDataModel(getFacade().findRange(publisher, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    return dataModel;
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
        current = (Places) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareValidationView() {
        current = (Places) getValidationItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getValidationItems().getRowIndex();
        return "View";
    }

    public String prepareMyItemsView() {
        current = (Places) getMyItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getMyItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Places();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            current.setPublished_by(userEjbFacade.find(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()));
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PlacesCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Places) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String prepareValidationEdit() {
        current = (Places) getValidationItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getValidationItems().getRowIndex();
        return "Edit";
    }

    public String validatePlace() {
        current = (Places) getValidationItems().getRowData();
        current.setIsValidate(true);
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Le lieu a été approuvé!");
            recreatePagination();
            recreateModel();
            return "Validation";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareMyItemsEdit() {
        current = (Places) getMyItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getMyItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PlacesUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Places) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyValidationItems() {
        current = (Places) getValidationItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getValidationItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "Validation";
    }

    public String destroyMyItems() {
        current = (Places) getMyItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getMyItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "MyPlaces";
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PlacesDeleted"));
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
        isInFullItems = true;
        if (items == null || isInMyItems || isInValidationItems) {
            items = getPagination().createPageDataModel();
            isInMyItems = false;
            isInValidationItems = false;
        }
        return items;
    }

    public DataModel getMyItems() {
        isInMyItems = true;
        if (items == null || isInFullItems || isInValidationItems) {
            items = getPagination(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()).createPageDataModel();
            isInFullItems = false;
            isInValidationItems = false;
        }
        return items;
    }

    public DataModel getValidationItems() {
        isInValidationItems = true;
        if (items == null || isInMyItems || isInFullItems) {
            items = getPagination(VALIDATION).createPageDataModel();
            isInMyItems = false;
            isInFullItems = false;
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String page(int page) {
        getPagination().setPage(page - 1);
        recreateModel();
        return "List";
    }

    public String page(int page, String publisher) {
        getPagination(publisher).setPage(page - 1);
        recreateModel();
        return "MyPlaces";
    }

    public String page(int page, int option) {
        getPagination(option).setPage(page - 1);
        recreateModel();
        return "Validation";
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String next(String publisher) {
        getPagination(publisher).nextPage();
        recreateModel();
        return "MyPlaces";
    }

    public String next(int option) {
        getPagination(option).nextPage();
        recreateModel();
        return "Validation";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public String previous(String publisher) {
        getPagination(publisher).previousPage();
        recreateModel();
        return "MyPlaces";
    }

    public String previous(int option) {
        getPagination(option).previousPage();
        recreateModel();
        return "Validation";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Places getPlaces(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Places.class)
    public static class PlacesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PlacesController controller = (PlacesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "placesController");
            return controller.getPlaces(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Places) {
                Places o = (Places) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Places.class.getName());
            }
        }
    }

    public int getNumberOfPages() {
        return (int) Math.ceil(getFacade().countItems() / (double) (getPagination().getPageSize()));
    }

    public int getNumberOfPages(String publisher) {
        return (int) Math.ceil(getFacade().count(publisher) / (double) getPagination(publisher).getPageSize());
    }

    public int getNumberOfPages(int option) {
        return (int) Math.ceil(getFacade().count(option) / (double) getPagination(option).getPageSize());
    }
}
