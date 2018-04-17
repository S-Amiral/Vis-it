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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 *
 * @author angelkiro
 */
@Stateless
public class EvaluationFacade extends AbstractFacade<Evaluation> {

    @PersistenceContext(unitName = "Vis-itPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EvaluationFacade() {
        super(Evaluation.class);
    }

    public int findEvaluation(Places place, Users user) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        //Indiquer le type de retour de la requête
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        //Indiquer quelles sont les données à intérroger
        Root<Evaluation> rt = criteriaQuery.from(Evaluation.class);
        //Faire la jointure avec la table Users
        Join<Evaluation, Users> joinUser = rt.join(Evaluation_.evaluated_by);
        Join<Evaluation, Places> joinPlace = rt.join(Evaluation_.place_evaluated);
        //Selection avec un count
        criteriaQuery.select(criteriaBuilder.count(rt));
        //Indiquer les conditions
        criteriaQuery.where(criteriaBuilder.equal(joinUser.get(Users_.username), user.getUsername()), criteriaBuilder.equal(joinPlace.get(Places_.id), place.getId()));
        //Création de la requête
        Query q = em.createQuery(criteriaQuery);
        //Retour du résultat
        return ((Long) q.getSingleResult()).intValue();
    }

    public Evaluation getEvaluation(Places place, Users find) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        //Indiquer le type de retour de la requête
        CriteriaQuery<Evaluation> criteriaQuery = criteriaBuilder.createQuery(Evaluation.class);
        //Indiquer quelles sont les données à intérroger
        Root<Evaluation> rt = criteriaQuery.from(Evaluation.class);
        //Faire la jointure avec la table Users
        Join<Evaluation, Users> joinUser = rt.join(Evaluation_.evaluated_by);
        Join<Evaluation, Places> joinPlace = rt.join(Evaluation_.place_evaluated);
        //Selection avec un count
        criteriaQuery.select(rt);
        //Indiquer les conditions
        criteriaQuery.where(criteriaBuilder.equal(joinUser.get(Users_.username), find.getUsername()), criteriaBuilder.equal(joinPlace.get(Places_.id), place.getId()));
        //Création de la requête
        Query q = em.createQuery(criteriaQuery);
        //Retour du résultat
        return (Evaluation) q.getSingleResult();
    }

}
