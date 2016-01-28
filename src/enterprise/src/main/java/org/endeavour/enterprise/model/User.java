package org.endeavour.enterprise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@XmlRootElement
public class User implements Serializable {

    private UUID userUuid;
    private String title;
    private String forename;
    private String surname;
    private String email;
    private List<UserInRole> userInRoles = new ArrayList<>();
    private UUID currentUserInRoleUuid;

    public void setUserInRoles(List<UserInRole> userInRoles)
    {
        this.userInRoles = userInRoles;
    }

    public UUID getUserUuid()
    {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid)
    {
        this.userUuid = userUuid;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getForename()
    {
        return forename;
    }

    public void setForename(String forename)
    {
        this.forename = forename;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public List<UserInRole> getUserInRoles()
    {
        return userInRoles;
    }

    public void addUserInRole(UserInRole userInRole)
    {
        userInRoles.add(userInRole);
    }

    public UUID getCurrentUserInRoleUuid()
    {
        return currentUserInRoleUuid;
    }

    public void setCurrentUserInRoleUuid(UUID currentUserInRoleUuid)
    {
        this.currentUserInRoleUuid = currentUserInRoleUuid;
    }

    @JsonIgnore
    public UserInRole getCurrentUserInRole()
    {
        if (userInRoles == null)
            return null;

        if (currentUserInRoleUuid == null)
            return null;

        for (UserInRole userInRole : userInRoles)
            if (userInRole.getUserInRoleUuid().equals(currentUserInRoleUuid))
                return userInRole;

        return null;
    }
}
