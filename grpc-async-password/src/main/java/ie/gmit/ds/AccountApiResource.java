package ie.gmit.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountApiResource {
		private HashMap<Integer, Account> accountMap = new HashMap<>();

		public AccountApiResource() {
		    Account testAcc = new Account(1, "ethan", "ethanhorr@gmail.com", "ethan123");
		    accountMap.put(testAcc.getUserId(), testAcc);
		}
		
		  @GET
		  public List<Account> getAccounts() {
		      // accountMap.values() returns Collection<Account>
		      // Collection is the interface implemented by Java collections like ArrayList, LinkedList etc.
		      // it's basically a generic list.
		      // https://docs.oracle.com/javase/7/docs/api/java/util/Collection.html
		      
		      return new ArrayList<Account>(accountMap.values());
		  }
}
