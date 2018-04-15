/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSF;

import Entities.Places;
import JSF.util.PaginationHelper;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author angelkiro
 */
@Named(value = "searchManagedBean")
@SessionScoped
public class SearchManagedBean implements Serializable {

    @EJB
    private SessionBeans.PlacesFacade ejbFacade;
    private String textToFind = "";
    private DataModel items = null;
    private PaginationHelper pagination;
    private Places current;

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return ejbFacade.count(textToFind);
                }

                @Override
                public DataModel createPageDataModel() {
                    DataModel dataModel = new ListDataModel(ejbFacade.findSearch(textToFind, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    return dataModel;
                }
            };
        }
        return pagination;
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    public void setItems(DataModel items) {
        this.items = items;
    }

    public String getTextToFind() {
        return textToFind;
    }

    public void setTextToFind(String textToFind) {
        this.textToFind = textToFind;
    }

    /**
     * Creates a new instance of SearchManagedBean
     */
    public SearchManagedBean() {
    }

    private void recreateModel() {
        items = null;
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "Search";
    }

    public int getNumberOfPages() {
        return (int) Math.ceil(ejbFacade.countItems(textToFind) / (double) (getPagination().getPageSize()));
    }

    public String page(int page) {
        getPagination().setPage(page - 1);
        recreateModel();
        return "Search";
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "Search";
    }
    
    public String prepareView() {
        current = (Places) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }
    
    public void newSearch(){
        recreateModel();
        pagination = null;
    }
}
