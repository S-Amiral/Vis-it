/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSF;

import JSF.util.PaginationHelper;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author angelkiro
 */
@Named(value = "searchManagedBean")
@RequestScoped
public class SearchManagedBean {

    @EJB
    private SessionBeans.PlacesFacade ejbFacade;
    private String textToFind = "";
    private DataModel items = null;

    public DataModel getItems() {
        items = new ListDataModel(ejbFacade.findSearch(textToFind, new int[]{0, 10}));
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
}
