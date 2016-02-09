package org.endeavour.enterprise.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.UUID;

@XmlRootElement
public class UserSummary implements Serializable {
	private UUID userUuid;
	private String email;

	public UUID getUserUuid()
	{
		return userUuid;
	}
	public void setUserUuid(UUID userUuid)
	{
		this.userUuid = userUuid;
	}

	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}

	public static UserSummary createFromUser(User user) {
		UserSummary userSummary = new UserSummary();
		userSummary.userUuid = user.getUserUuid();
		userSummary.email = user.getEmail();
		return userSummary;
	}
}
