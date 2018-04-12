/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SessionBeans;

import Entities.Places;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
