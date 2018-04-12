/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author angelkiro
 */
@Entity
public class Groups implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 10)
    private String groupName;

    @ManyToMany
    @JoinTable(name = "USER_GROUP", 
            joinColumns = @JoinColumn(
                    name = "groupName",
                    referencedColumnName = "groupName"),
            inverseJoinColumns = @JoinColumn(
                    name = "username",
                    referencedColumnName = "username"))
    private List<Users> users;

    public String getGroupName() {
        
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupName != null ? groupName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Groups)) {
            return false;
        }
        Groups other = (Groups) object;
        if ((this.groupName == null && other.groupName != null) || (this.groupName != null && !this.groupName.equals(other.groupName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Groups[ groupName=" + groupName + " ]";
    }

}
