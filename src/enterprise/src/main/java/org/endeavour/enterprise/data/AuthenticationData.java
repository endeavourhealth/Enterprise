package org.endeavour.enterprise.data;

import org.endeavour.enterprise.model.Credentials;
import org.endeavour.enterprise.model.Role;
import org.endeavour.enterprise.model.User;
import org.endeavour.enterprise.model.UserInRole;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthenticationData
{
    public boolean areCredentialsValid(Credentials credentials)
    {
        // db call

        return (credentials.getUsername().equals("david.stables@endeavourhealth.org") && credentials.getPassword().equals("1234"));
    }

    public User getUser(UUID userUuid)
    {
        List<User> users = getUsers();

        for (User user : users)
            if (user.getUserUuid().equals(userUuid))
                return user;

        return null;
    }

    public User getUser(String username)
    {
        List<User> users = getUsers();

        for (User user : users)
            if (user.getEmail().equals(username))
                return user;

        return null;
    }

    private List<User> getUsers()
    {
        List<User> users = new ArrayList<>();

        User user = new User();
        user.setUserUuid(UUID.fromString("b860db4c-7270-4e0f-a59f-77fa9b74973f"));
        user.setTitle("Dr");
        user.setForename("David");
        user.setSurname("Stables");
        user.setEmail("david.stables@endeavourhealth.org");

        UserInRole userInRole = new UserInRole();
        userInRole.setUserInRoleUuid(UUID.fromString("50ded6cd-17a1-4a05-9079-c176468ff90b"));
        userInRole.setOrganisationUuid(UUID.fromString("e9f71c8a-be36-42ff-8cd7-f2ab9f188a4f"));
        userInRole.setOrganisationName("Alpha Surgery");
        userInRole.setRole(Role.ADMIN);

        user.addUserInRole(userInRole);

        UserInRole userInRole2 = new UserInRole();
        userInRole2.setUserInRoleUuid(UUID.fromString("19d402a6-30f1-4175-acac-340d73a2ffb2"));
        userInRole2.setOrganisationUuid(UUID.fromString("31287afa-a5b9-4a0e-ac70-0646d0508adb"));
        userInRole2.setOrganisationName("Bravo Hospital");
        userInRole2.setRole(Role.USER);

        user.addUserInRole(userInRole2);

        user.setInitialUserInRoleUuid(userInRole.getUserInRoleUuid());

        users.add(user);

        return users;
    }
}
