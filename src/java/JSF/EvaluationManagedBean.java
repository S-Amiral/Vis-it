/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSF;

import Entities.Evaluation;
import Entities.Places;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;

/**
 *
 * @author angelkiro
 */
@Named(value = "evaluationManagedBean")
@RequestScoped
public class EvaluationManagedBean {

    @ManagedProperty(value = "#{placesController}")
    private PlacesController placesController;

    public PlacesController getPlacesController() {
        return placesController;
    }

    public void setPlacesController(PlacesController placesController) {
        this.placesController = placesController;
    }

    @EJB
    private SessionBeans.EvaluationFacade ejbFacade;

    @EJB
    private SessionBeans.UsersFacade userEjbFacade;

    private int score;

    @PostConstruct
    private void init() {
        //Only to display 50 as default value for the rating
        score = 50;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Creates a new instance of EvaluationManagedBean
     */
    public EvaluationManagedBean() {

    }

    public void deleteEvaluation(Places place) {
        ejbFacade.remove(ejbFacade.getEvaluation(place, userEjbFacade.find(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser())));
    }

    public boolean isPlaceAlreadyEvaluated(Places place) {
        int value = ejbFacade.findEvaluation(place, userEjbFacade.find(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()));
        return value != 0;
    }

    public void addScoreToPlace(Places place) {
        Evaluation evaluation = new Evaluation();
        evaluation.setScore(score);
        evaluation.setEvaluated_by(userEjbFacade.find(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()));
        evaluation.setPlace_evaluated(place);
        ejbFacade.create(evaluation);
    }

    public int getScore(Places place) {
        return ejbFacade.getEvaluation(place, userEjbFacade.find(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser())).getScore();
    }
}
