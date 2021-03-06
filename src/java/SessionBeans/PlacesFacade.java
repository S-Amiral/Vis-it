/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SessionBeans;

import Entities.Evaluation;
import Entities.Evaluation_;
import Entities.Places;
import Entities.Places_;
import Entities.Users;
import Entities.Users_;
import JSF.PlacesController;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;

/**
 *
 * @author angelkiro
 */
@Stateless
public class PlacesFacade extends AbstractFacade<Places> {

    @PersistenceContext(unitName = "Vis-itPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlacesFacade() {
        super(Places.class);
    }

    public List findRange(String publisher, int[] range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Places> c = cb.createQuery(Places.class);
        Root<Places> m = c.from(Places.class);
        Join<Places, Users> joinUser = m.join(Places_.published_by);
        // faire la selection globale de l'utilisateur publisher
        c.select(m);
        c.where(cb.equal(joinUser.get(Users_.username), publisher));
        // realiser la requete
        TypedQuery<Places> q = em.createQuery(c);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        // executer la requete avec criteres
        return q.getResultList();
    }

    public List<Places> findRange(int[] range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery c = cb.createQuery(Places.class);
        Root<Places> m = c.from(Places.class);
        c.select(m);
        c.where(cb.equal(m.get(Places_.isValidate), true));
        Query q = em.createQuery(c);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public List<Places> findRange(int option, int[] range) {
        switch (option) {
            case PlacesController.VALIDATION:
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery c = cb.createQuery(Places.class);
                Root<Places> m = c.from(Places.class);
                c.select(m);
                c.where(cb.equal(m.get(Places_.isValidate), false));
                Query q = em.createQuery(c);
                q.setMaxResults(range[1] - range[0] + 1);
                q.setFirstResult(range[0]);
                return q.getResultList();
            default:
                return null;
        }

    }

    public int count(String publisher) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        //Indiquer le type de retour de la requête
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        //Indiquer quelles sont les données à intérroger
        Root<Places> rt = criteriaQuery.from(Places.class);
        //Faire la jointure avec la table Users
        Join<Places, Users> joinUser = rt.join(Places_.published_by);
        //Selection avec un count
        criteriaQuery.select(criteriaBuilder.count(rt));
        //Indiquer les conditions
        criteriaQuery.where(criteriaBuilder.equal(joinUser.get(Users_.username), publisher));
        //Création de la requête
        Query q = em.createQuery(criteriaQuery);
        //Retour du résultat
        return ((Long) q.getSingleResult()).intValue();
    }

    public int countItems() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Places> rt = criteriaQuery.from(Places.class);
        criteriaQuery.select(criteriaBuilder.count(rt));
        criteriaQuery.where(criteriaBuilder.equal(rt.get(Places_.isValidate), true));
        Query q = em.createQuery(criteriaQuery);
        return ((Long) q.getSingleResult()).intValue();
    }

    public int count(int option) {
        switch (option) {
            case PlacesController.VALIDATION:
                CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
                CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
                Root<Places> rt = criteriaQuery.from(Places.class);
                criteriaQuery.select(criteriaBuilder.count(rt));
                criteriaQuery.where(criteriaBuilder.equal(rt.get(Places_.isValidate), false));
                Query q = em.createQuery(criteriaQuery);
                return ((Long) q.getSingleResult()).intValue();
            default:
                return 0;
        }
    }

    public int countItems(String textToFind) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Places> rt = criteriaQuery.from(Places.class);
        criteriaQuery.select(criteriaBuilder.count(rt));
        criteriaQuery.where(criteriaBuilder.equal(rt.get(Places_.isValidate), true), criteriaBuilder.like(rt.get(Places_.title), "%" + textToFind + "%"));
        Query q = em.createQuery(criteriaQuery);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List findSearch(Map<String, String> filters, int[] i) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery c = cb.createQuery(Places.class);
        Root<Places> m = c.from(Places.class);
        //Root<Evaluation> rootEvaluation = c.from(entityClass)
        Join<Places, Users> joinUser = m.join(Places_.published_by);
        c.select(m);
        List<Predicate> filtersPredicate = new ArrayList<Predicate>();
        filtersPredicate.add(cb.equal(m.get(Places_.isValidate), true));

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    filtersPredicate.add(cb.like(m.get(Places_.title), "%" + entry.getValue() + "%"));
                    break;
                case "user":
                    filtersPredicate.add(cb.equal(joinUser.get(Users_.username), entry.getValue()));
                    break;
                case "score":
                    break;
                case "date":
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try {
                        Date date = format.parse(entry.getValue());
                        filtersPredicate.add(cb.greaterThanOrEqualTo(m.<Date>get(Places_.published_date), date));
                    } catch (ParseException ex) {
                        Logger.getLogger(PlacesFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
        }
        c.where(cb.and(filtersPredicate.toArray(new Predicate[filtersPredicate.size()])));
        Query q = em.createQuery(c);
        q.setMaxResults(10);
        q.setFirstResult(0);
        return q.getResultList();
    }

    public double getAvg(int placeID) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Double> criteriaQuery = criteriaBuilder.createQuery(Double.class);
        Root<Evaluation> rt = criteriaQuery.from(Evaluation.class);
        Join<Evaluation, Places> joinPlace = rt.join(Evaluation_.place_evaluated);
        criteriaQuery.select(criteriaBuilder.avg(rt.get("score")));
        //Indiquer les conditions
        criteriaQuery.where(criteriaBuilder.equal(joinPlace.get(Places_.id), placeID));
        //Création de la requête
        Query q = em.createQuery(criteriaQuery);
        //Retour du résultat
        Double resultDouble = (Double) q.getSingleResult();
        if (resultDouble == null) {
            return 0.;
        }
        double resultDoubleTwoDecimal = resultDouble * 100;
        int resultInt = (int) resultDoubleTwoDecimal;
        return resultInt / 100.0;
    }

}
