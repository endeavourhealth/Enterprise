package org.endeavour.enterprise.data;

import org.endeavour.enterprise.framework.database.DatabaseConnection;
import org.endeavour.enterprise.framework.database.StoredProcedure;
import org.endeavour.enterprise.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdministrationData
{
    public String getPasswordHash(String emailAddress) throws Exception
    {
        try (Connection connection = DatabaseConnection.get(DatabaseName.ENTERPRISE_CORE))
        {
            try (StoredProcedure storedProcedure = new StoredProcedure(connection, "Administration.GetPasswordHash"))
            {
                storedProcedure.setParameter("@EmailAddress", emailAddress);

                return (String)storedProcedure.executeScalar();
            }
        }
    }

    public User getUser(UUID userUuid)
    {
        return getUsers()
                .stream()
                .filter(t -> t.getUserUuid().equals(userUuid))
                .findFirst()
                .orElse(null);
    }

    public User getUser(String username)
    {
        return getUsers()
                .stream()
                .filter(t -> t.getEmail().equals(username))
                .findFirst()
                .orElse(null);
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

        user.setCurrentUserInRoleUuid(userInRole.getUserInRoleUuid());

        users.add(user);

        return users;
    }

    public List<Organisation> GetOrganisation() throws Exception
    {
        try (Connection conn = DatabaseConnection.get(DatabaseName.ENTERPRISE_CORE))
        {
            try (StoredProcedure storedProcedure = new StoredProcedure(conn, "Administration.GetOrganisation"))
            {
                storedProcedure.setParameter("@OrganisationId", 1);

                ResultSet resultSet = storedProcedure.executeQuery();

                ArrayList<Organisation> result = new ArrayList<>();

                while (resultSet.next())
                {
                    Organisation organisation = new Organisation();
                    organisation.setOrganisationUuid(UUID.fromString(resultSet.getString("OrganisationUuid")));
                    organisation.setName(resultSet.getString("Name"));

                    result.add(organisation);
                }

                return result;
            }
        }
    }
}
