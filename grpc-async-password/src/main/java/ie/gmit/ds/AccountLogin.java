package ie.gmit.ds;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Author: Ethan Horrigan
 * Account Login Object
 * 
 * Created to handle login functionality.
 * 
 * Planned on being an immutable object, but XML requires setters.
 */

@XmlRootElement(name= "account")
public class AccountLogin {
	
	private int userId;
	private String password;
	
	
	public AccountLogin(int userId, String password) {
		this.userId = userId;
		this.password = password;
	}
	
	public AccountLogin() {
		super();
	}

	@XmlElement
	@JsonProperty
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@XmlElement
	@JsonProperty
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
