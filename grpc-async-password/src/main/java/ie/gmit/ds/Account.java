package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Author: Ethan Horrigan
 * Immutable Account Object
 */
public class Account {

	private int userId;
	private String userName;
	private String email;
	private String password;
	private String hashedPassword;
	private String salt;
	
	public Account() {}
	
	public Account(int userId, String userName, String email, String hashedPassword, String salt) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.hashedPassword = hashedPassword;
		this.salt = salt;
	}
	
	public Account(int userId, String userName, String email, String password) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.password = password;
	}

	@JsonProperty
	public int getUserId() {
		return userId;
	}
	@JsonProperty
	public String getUserName() {
		return userName;
	}
	@JsonProperty
	public String getEmail() {
		return email;
	}
	@JsonProperty
	public String getHashedPassword() {
		return hashedPassword;
	}
	@JsonProperty
	public String getSalt() {
		return salt;
	}
	@JsonProperty
	public String getPassword() {
		return password;
	}
	
	
}
