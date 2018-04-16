/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SessionBeans;

import Entities.Users;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author angelkiro
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users> {

    @PersistenceContext(unitName = "Vis-itPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersFacade() {
        super(Users.class);
    }

    public void updateUserToAdmin(String username) {
        Query q = em.createNativeQuery("INSERT INTO `USER_GROUP` (`username`, `groupName`) VALUES ('" + username + "', 'admin');");
        q.executeUpdate();
    }

    public void removeUserToAdmin(String username) {
        Query q = em.createNativeQuery("DELETE FROM `USER_GROUP` WHERE `USER_GROUP`.`username`='" + username + "' AND `USER_GROUP`.`groupName`='admin';");
        q.executeUpdate();
    }

    public void updateUserToStandard(String username) {
        Query q = em.createNativeQuery("INSERT INTO `USER_GROUP` (`username`, `groupName`) VALUES ('" + username + "', 'standard');");
        q.executeUpdate();
    }

    public void removeUserToStandard(String username) {
        Query q = em.createNativeQuery("DELETE FROM `USER_GROUP` WHERE `USER_GROUP`.`username`='" + username + "' AND `USER_GROUP`.`groupName`='standard';");
        q.executeUpdate();
    }

    public void updateUserToModerator(String username) {
        Query q = em.createNativeQuery("INSERT INTO `USER_GROUP` (`username`, `groupName`) VALUES ('" + username + "', 'moderator');");
        q.executeUpdate();
    }

    public void removeUserToModerator(String username) {
        Query q = em.createNativeQuery("DELETE FROM `USER_GROUP` WHERE `USER_GROUP`.`username`='" + username + "' AND `USER_GROUP`.`groupName`='moderator';");
        q.executeUpdate();
    }
}
