/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSF;

import Entities.Places;
import Entities.Users;
import JSF.util.PaginationHelper;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
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
    private Users currentUser;
    private String datePeriod;
    private int minScore;
    private Map<String, String> filters;

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public String getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(String datePeriod) {
        this.datePeriod = datePeriod;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return ejbFacade.count(textToFind);
                }

                @Override
                public DataModel createPageDataModel() {
                    filters.put("name", textToFind);
                    filters.put("score", String.valueOf(minScore));
                    if (currentUser != null) {
                        filters.put("user", currentUser.getUsername());
                    }
                    if (datePeriod != null) {
                        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                        Date dt = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dt);
                        switch (datePeriod) {
                            case "Cette semaine":
                                c.add(Calendar.DAY_OF_MONTH, -7);
                                filters.put("date", formatDate.format(c.getTime()));
                                break;
                            case "Ce mois":
                                c.add(Calendar.MONTH, -1);
                                filters.put("date", formatDate.format(c.getTime()));
                                break;
                            case "Cette année":
                                c.add(Calendar.YEAR, -1);
                                filters.put("date", formatDate.format(c.getTime()));
                                break;
                            default:
                                break;
                        }
                    }
                    DataModel dataModel = new ListDataModel(ejbFacade.findSearch(filters, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    return dataModel;
                }
            };
        }
        return pagination;
    }

    public DataModel getItems() {
        System.out.println("CurrentUser: " + currentUser + " date Period:" + datePeriod + " Note min:" + minScore);
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

    @PostConstruct
    public void init() {
        filters = new HashMap<String, String>();
    }

    private void recreateModel() {
        items = null;
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "/pages/places/Search.xhtml";
    }

    public int getNumberOfPages() {
        return (int) Math.ceil(ejbFacade.countItems(textToFind) / (double) (getPagination().getPageSize()));
    }

    public String page(int page) {
        getPagination().setPage(page - 1);
        recreateModel();
        return "/pages/places/Search.xhtml";
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "/pages/places/Search.xhtml";
    }

    public String prepareView() {
        current = (Places) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "/pages/places/View.xhtml";
    }

    public void newSearch() {
        filters.clear();
        recreateModel();
        pagination = null;
    }
}
