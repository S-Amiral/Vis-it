/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSF;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author angelkiro
 */
@RequestScoped
@Named(value = "authentificationBean")
public class AuthentificationBean {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {
        String page = "/index?faces-redirect=true";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(username, password);
            System.out.println("AuthentificationBeans : Login requested");
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
}
