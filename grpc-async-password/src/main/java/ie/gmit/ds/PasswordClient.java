package ie.gmit.ds;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;

import ie.gmit.ds.utils.Passwords;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class PasswordClient {
	
	Scanner userIn = new Scanner(System.in);
	
	/**
	 * User Input Variables
	 */
	private int userId;
	private String userPassword;
	
	/**
	 * Stored Variables
	 */
	byte[] saltByte;
	ByteString salt;
	ByteString hashedPassword;
	
	boolean input = false;
	
	private static final Logger logger = Logger.getLogger(PasswordClient.class.getName());
	private final ManagedChannel channel;
	private final PasswordServiceGrpc.PasswordServiceStub asyncPasswordService;
	private final PasswordServiceGrpc.PasswordServiceBlockingStub syncPasswordService;

	public PasswordClient(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		syncPasswordService = PasswordServiceGrpc.newBlockingStub(channel);
		asyncPasswordService = PasswordServiceGrpc.newStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

    @SuppressWarnings("unused")
	public void addPassword() {
    	
    	System.out.println("Enter User ID:");
    	userId = userIn.nextInt();
	
    	while(!input) {
    		System.out.println("\nEnter Password:\n(Must Contain atleast 2 letters & 2 digits):\n");
    		userPassword = userIn.next();
    		if(Passwords.isValid(userPassword.toCharArray()))
    			input = true;
    		else
    			System.out.println("Invalid Password.");
    	}

    	saltByte = Passwords.getNextSalt();
    	salt = ByteString.copyFrom(saltByte);
    	hashedPassword = ByteString.copyFrom(Passwords.hash(userPassword.toCharArray(), saltByte));
    	
    	PasswordHashRequest password = PasswordHashRequest.newBuilder().setUserId(userId).setPassword(userPassword).build();
    	PasswordHashResponse passwordResponse = PasswordHashResponse
    			.newBuilder()
    			.setUserId(userId)
    			.setHashedPassword(hashedPassword).setSalt(salt).build();
        try {
        	passwordResponse = syncPasswordService.hash(password);
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }

    }
    
    public void validatePassword() {
    	StreamObserver<BoolValue> responseObserver = new StreamObserver<BoolValue>() {

			@Override
			public void onNext(BoolValue value) {
				if(value.getValue())
					System.out.println("Password is Correct.");
				else
					System.out.println("Password is Incorrect");
			}

			@Override
			public void onError(Throwable t) {}

			@Override
			public void onCompleted() {}
    	};
    	try {
    		asyncPasswordService.validate(PasswordValidateRequest
    				.newBuilder().setPassword(userPassword)
    				.setHashedPassword(hashedPassword)
    				.setSalt(salt)
    				.build(), responseObserver);
    	} catch(StatusRuntimeException e) {
    		logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
    	}
    }
    
	public static void main(String[] args) throws Exception {
		PasswordClient client = new PasswordClient("localhost", 50551);
		try {
			client.addPassword();
			client.validatePassword();
		} finally {
			// Don't stop process, keep alive to receive async response
			Thread.currentThread().join();
		}
	}
}
