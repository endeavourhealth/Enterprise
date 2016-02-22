package org.endeavour.enterprise.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.UUID;

@XmlRootElement
public class UserSummary implements Serializable {
	private UUID userUuid;
	private String name;
	private String email;
	private boolean isActive;
	private boolean isAdmin;
	private boolean isSuper;

	public UUID getUserUuid()
	{
		return userUuid;
	}
	public void setUserUuid(UUID userUuid)
	{
		this.userUuid = userUuid;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean getIsActive() { return isActive;	}
	public void setIsActive(boolean active) { isActive = active; }

	public boolean getIsAdmin() { return isAdmin; }
	public void setIsAdmin(boolean admin) { isAdmin = admin; }

	public boolean getIsSuper() { return isSuper; }
	public void setIsSuper(boolean aSuper) { isSuper = aSuper; }

/*
	public static UserSummary createFromUser(User user) {
		UserSummary userSummary = new UserSummary();
		userSummary.userUuid = user.getUserUuid();
		userSummary.name = user.getTitle() + ' ' + user.getForename() + ' ' + user.getSurname();
		userSummary.email = user.getEmail();
		userSummary.isActive = true;
		userSummary.isAdmin = (user.getUserInRoles().stream().filter(uir -> uir.getEndUserRole().equals(EndUserRole.ADMIN)).findFirst().orElse(null) != null);
		userSummary.isSuper = (user.getUserInRoles().stream().filter(uir -> uir.getEndUserRole().equals(EndUserRole.SUPER)).findFirst().orElse(null) != null);
		return userSummary;
	}
*/
}
