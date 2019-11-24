package ie.gmit.ds;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.protobuf.ByteString;
import ie.gmit.ds.utils.Constants;

/*
 * Author: Ethan Horrigan
 * Account API Resource to handle API Operations.
 */

@Path("/accounts")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AccountApiResource {

	/*
	 * HashMap to store Account Data.
	 */
	private HashMap<Integer, HashedAccount> accountMap = new HashMap<>();

	/*
	 * Create a new Instance of PasswordClient with the port 50551
	 */
	PasswordClient client = new PasswordClient("localhost", 50551);
	
	/*
	 * Login Boolean to determine if details are correct.
	 */
	boolean log;

	/*
	 * Validator used to check if a value is within the userAccount HashMap.
	 * https://docs.oracle.com/javaee/7/api/javax/validation/Validator.html
	 */
	//private Validator validator;
	
	/**
	 * Constructor to add a Test Account and Hashed Version.
	 * @throws UnsupportedEncodingException
	 */
	public AccountApiResource() throws UnsupportedEncodingException {
		Account accTest = new Account(1, "ethan1", "1@gmail.com", "ethan123");
		client.addPassword(accTest.getUserId(), accTest.getPassword());

		String hashed = new String(client.getHashedPassword().toByteArray(), Constants.ISO);
		String salt = new String(client.getSalt().toByteArray(), Constants.ISO);

		HashedAccount accHashed = new HashedAccount(accTest.getUserId(), accTest.getUserName(), accTest.getEmail(),
				hashed, salt);

		accountMap.put(accTest.getUserId(), accHashed);
	}

	/*
	 * Lists all the users stored in the HashedAccount HashMap
	 */
	@GET
	public List<HashedAccount> getAccounts() {
		return new ArrayList<HashedAccount>(accountMap.values());
	}

	/*
	 * Returns Account Information of a specific user
	 */
	@GET
	@Path("/{userId}")
	public Response getAccountByID(@PathParam("userId") int userId) {
		if (!checkHashedAccount(accountMap.get(userId)))
			return Response.status(Response.Status.NOT_FOUND).entity(Constants.USER_NOT_FOUND + userId).build();
		return Response.ok(accountMap.get(userId)).build();
	}

	/*
	 * Creates a new user
	 * Response.Status.CONFLICT
	 * Adapted From: https://stackoverflow.com/questions/3825990/http-response-code-for-post-when-resource-already-exists
	 */
	@POST
	public Response addAccount(Account acc) throws UnsupportedEncodingException {
		if (checkAccount(acc.getUserId()))
			return Response.status(Response.Status.CONFLICT).entity(Constants.USER_TAKEN + acc.getUserId()).build();
		client.addPassword(acc.getUserId(), acc.getPassword());
		System.out.println("Hash for new user:" + client.getHashedPassword());
		System.out.println("Salt for new user:" + client.getSalt());

		String hashed = new String(client.getHashedPassword().toByteArray(), Constants.ISO);
		String salt = new String(client.getSalt().toByteArray(), Constants.ISO);

		HashedAccount hashedAccount = new HashedAccount(acc.getUserId(), acc.getUserName(), acc.getEmail(), hashed,
				salt);

		accountMap.put(hashedAccount.getUserId(), hashedAccount);

		return Response.ok(acc).build();
	}

	/*
	 * Update User First remove the user at the userId Then, re-add the user at
	 * userId
	 */
	@PUT
	@Path("/{userId}")
	public Response updateUser(Account acc, @PathParam("userId") int userId) throws UnsupportedEncodingException {
		if (!checkAccount(userId))
			return Response.status(Response.Status.NOT_FOUND).entity(Constants.USER_NOT_FOUND + userId).build();

		accountMap.remove(userId);

		client.addPassword(acc.getUserId(), acc.getPassword());

		String hashed = new String(client.getHashedPassword().toByteArray(), Constants.ISO);
		String salt = new String(client.getSalt().toByteArray(), Constants.ISO);

		HashedAccount hashedAccount = new HashedAccount(acc.getUserId(), acc.getUserName(), acc.getEmail(), hashed,
				salt);

		accountMap.put(hashedAccount.getUserId(), hashedAccount);

		return Response.ok(acc).build();
	}

	/*
	 * Deletes user at the given userId
	 * Returns Response USER_NOT_FOUND if the user does not exist.
	 * Removes user if the userId exists.
	 */
	@DELETE
	@Path("/{userId}")
	public Response deleteUser(Account acc, @PathParam("userId") int userId) {
		if (!checkAccount(userId))
			return Response.status(Response.Status.NOT_FOUND).entity(Constants.USER_NOT_FOUND + userId).build();

		accountMap.remove(userId);
		return Response.ok().build();
	}

	/*
	 * TODO add login Login a user
	 */
	@POST
	@Path("/login/")
	public Response login(AccountLogin login) throws UnsupportedEncodingException {
		
		if (!checkAccount(login.getUserId()))
			return Response.status(Response.Status.PRECONDITION_FAILED).entity(Constants.LOGIN_FAILED + login.getUserId()).build();
		

			HashedAccount hashedUser = accountMap.get(login.getUserId());
		
			byte[] hashedByte = hashedUser.getHashedPassword().getBytes(Constants.ISO);
			byte[] saltByte = hashedUser.getSalt().getBytes(Constants.ISO);
		
			ByteString hashed = ByteString.copyFrom(hashedByte);
			ByteString salt = ByteString.copyFrom(saltByte);
		
			System.out.println(hashedByte);
			System.out.println(salt);
			
			log = client.validatePassword(login.getPassword(), hashed, salt);
			

		if(!log)
			return Response.status(Response.Status.PRECONDITION_FAILED).entity(Constants.LOGIN_FAILED + login.getUserId()).build();

		return Response.status(Response.Status.ACCEPTED).entity(Constants.LOGIN_SUCCESS + login.getUserId()).build();
	}

	/**
	 * Generic method to check that the accountMap contains the user.
	 * 
	 * @param acc Account
	 * @return true if Account is not null
	 */
	public Boolean checkAccount(int userId) {
		if (accountMap.containsKey(userId))
			return true;
		return false;
	}

	/**
	 * Generic method that returns true if the HashedAccount is not null
	 * 
	 * @param acc HashedAccount
	 * @return true if HashedAccount is not null
	 */
	public Boolean checkHashedAccount(HashedAccount acc) {
		if (acc != null)
			return true;
		return false;
	}

}
