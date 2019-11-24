package ie.gmit.ds;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import ie.gmit.ds.utils.Passwords;

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
	
	byte[] saltByte = Passwords.getNextSalt();
	
	public Account() {}
	
	/*
	 * Output Account details
	 */
	public Account(int userId, String userName, String email, String hashedPassword, String salt) {
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.hashedPassword = hashedPassword;
		this.salt = salt;
	}
	
	/*
	 * Input Account Details
	 */
	public Account(int userId, String userName, String email, String password) {
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

	@Override
	public String toString() {
		return "Account [userId=" + userId + ", userName=" + userName + ", email=" + email + ", password=" + password
				+ ", hashedPassword=" + hashedPassword + ", salt=" + salt + ", saltByte=" + Arrays.toString(saltByte)
				+ "]";
	}
	
	
	
	
}
