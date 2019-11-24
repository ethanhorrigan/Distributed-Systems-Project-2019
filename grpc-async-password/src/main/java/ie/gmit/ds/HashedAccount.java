package ie.gmit.ds;

public class HashedAccount {
	
	private int userId;
	private String username;
	private String email;
	private String hashedPassword;
	private String salt;
	
	public HashedAccount() {
		super();
	}

	public HashedAccount(int userId, String username, String email, String hashedPassword, String salt) {
		super();
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.hashedPassword = hashedPassword;
		this.salt = salt;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	
	
	

}
