package ie.gmit.ds;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ie.gmit.ds.utils.Constants;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountApiResource {
	
	private HashMap<Integer, HashedAccount> accountMap = new HashMap<>();
	
	//Create a new Instance of PasswordClient
	PasswordClient client = new PasswordClient("localhost", 50551);


	/**
	 * Constructor with test Account.
	 * @throws UnsupportedEncodingException 
	 */
	public AccountApiResource() throws UnsupportedEncodingException {
		Account accTest = new Account(1, "ethan1", "1@gmail.com", "ethan123");
		client.addPassword(accTest.getUserId(), accTest.getPassword());
		
		String hashed = new String(client.getHashedPassword().toByteArray(), Constants.ISO);
		String salt = new String(client.getSalt().toByteArray(), Constants.ISO);
		
		HashedAccount accHashed = new HashedAccount(accTest.getUserId(), accTest.getUserName(), accTest.getEmail(), hashed, salt);
		
		accountMap.put(accTest.getUserId(), accHashed);
	}

	// • List all users
	@GET
	public List<HashedAccount> getAccounts() {
		return new ArrayList<HashedAccount>(accountMap.values());
	}
	
	// • Get info on a specific user
	@GET
    @Path("/{userId}")
    public HashedAccount getAccountByID(@PathParam("userId") int userId) {
        return accountMap.get(userId);
    }
	
	/* Create a new user */
	@POST
	public Response addAccount(Account acc) throws UnsupportedEncodingException {
		client.addPassword(acc.getUserId(), acc.getPassword());
		System.out.println("Hash for new user:" + client.getHashedPassword());
		System.out.println("Salt for new user:" + client.getSalt());
		
		String hashed = new String(client.getHashedPassword().toByteArray(), Constants.ISO);
		String salt = new String(client.getSalt().toByteArray(), Constants.ISO);
		

		HashedAccount hashedAccount = new HashedAccount(acc.getUserId(), acc.getUserName(), acc.getEmail(), hashed, salt);
		
		accountMap.put(hashedAccount.getUserId(), hashedAccount);
		
		return Response.ok(acc).build();
	}
	
	/*
	 * Update User
	 * First remove the user at the userId
	 * Then, re-add the user at userId
	 */
	
	@PUT
	@Path("/{userId}")
	public Response updateUser(Account acc, @PathParam("userId") int userId) throws UnsupportedEncodingException {
		if(check(acc) == false)
			return Response.status(Response.Status.NOT_FOUND).build();
		
		accountMap.remove(acc.getUserId());
		
		client.addPassword(userId, acc.getPassword());
		
		String hashed = new String(client.getHashedPassword().toByteArray(), Constants.ISO);
		String salt = new String(client.getSalt().toByteArray(), Constants.ISO);
		
		HashedAccount hashedAccount = new HashedAccount(acc.getUserId(), acc.getUserName(), acc.getEmail(), hashed, salt);
		
		accountMap.put(hashedAccount.getUserId(), hashedAccount);
		
		return Response.ok(acc).build();
	}
	
	/* Delete User */
	@DELETE
	@Path("/{userId}")
	public Response deleteUser(Account acc, @PathParam("userId") int userId) {
		if(check(acc) == false)
			return Response.status(Response.Status.NOT_FOUND).build();
		
		accountMap.remove(userId);
		return Response.ok().build();
	}
	
	
	/*
	 * TODO add login
	 * Login a user
	*/
	@Path("/login/{userId}/{password}")
	public Response login(@PathParam("userId") int userId, @PathParam("password") String password) {
		//Account loginRequest = accountMap.get(userId);
			//return Response.status(Response.Status.NOT_FOUND).build();
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return Response.ok().build();
	}
	public Boolean check(Account acc) {
		if(acc != null)
			return true;
		return false;
	}
	
	
}
