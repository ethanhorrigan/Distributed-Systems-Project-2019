package ie.gmit.ds;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;

import ie.gmit.ds.utils.Constants;
import ie.gmit.ds.utils.Passwords;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class PasswordClient {

	static Scanner userIn = new Scanner(System.in);

	/**
	 * User Input Variables
	 */
	private static int userId;
	private static String username;
	private static String email;
	private static String userPassword;

	/**
	 * Stored Variables
	 */
	static byte[] saltByte;
	
	private ByteString salt;
	private ByteString hashedPassword;
	
	/**
	 * Getters
	 */

	public ByteString getSalt() {
		return salt;
	}

	public ByteString getHashedPassword() {
		return hashedPassword;
	}

	private static boolean input = false;

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

	/*
	 * Aysnc addPassword
	 */
	
	public byte[] hp;
	public String hpString;
	public byte[] sb;
	public String sbString;
	public Account newAcc;
	
	public void addPassword(int userId, String password) {
		StreamObserver<PasswordHashResponse> responseObserver = new StreamObserver<PasswordHashResponse>() {

			@Override
			public void onNext(PasswordHashResponse value) {
				hashedPassword = value.getHashedPassword();
				salt = value.getSalt();
				

				hp = hashedPassword.toByteArray();
				hpString = Arrays.toString(hp);

				sb = salt.toByteArray();
				sbString = Arrays.toString(sb);
			}

			@Override
			public void onError(Throwable t) {
				Status status = Status.fromThrowable(t);
				logger.log(Level.WARNING, "RPC Error: {0}", status);
			}

			@Override
			public void onCompleted() {}
		};
				
		try {
			PasswordHashRequest request = PasswordHashRequest.newBuilder().setUserId(userId).setPassword(password).build();
			asyncPasswordService.hash(request, responseObserver);
			TimeUnit.SECONDS.sleep(Constants.SLEEP_TIME);
		} catch (StatusRuntimeException | InterruptedException ex) {
			logger.log(Level.WARNING, "RPC failed: {0}", ex.fillInStackTrace());
		}

		logger.info("Hashing Finished..");
		return;
	}

	public void validatePassword(Account acc, String password) {
		StreamObserver<BoolValue> responseObserver = new StreamObserver<BoolValue>() {

			@Override
			public void onNext(BoolValue value) {
				if (value.getValue())
					System.out.println("Password is Correct.");
				else
					System.out.println("Password is Incorrect");
			}

			@Override
			public void onError(Throwable t) {
			}

			@Override
			public void onCompleted() {
			}
		};
		try {
			asyncPasswordService.validate(PasswordValidateRequest.newBuilder().setPassword(userPassword)
					.setHashedPassword(hashedPassword).setSalt(salt).build(), responseObserver);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		}
	}

	public static void main(String[] args) throws Exception {
		AccountApiResource resource = new AccountApiResource();
		boolean running = true;
		int target;
		try {
			System.out.println("HOW NOW, WHAT WOULD YOU LIKE TO DO?");
			System.out.println("[1] Create User: ");
			System.out.println("[2] Get Info on User (User ID): ");
			System.out.println("[3] Update User (User ID): ");
			System.out.println("[4] Delete User (User ID): ");
			System.out.println("[5] List all Users: ");
			System.out.println("[6] Login: ");
			System.out.println("[7] Exit: ");
			target = userIn.nextInt();

			while (running) {
				switch (target) {
				case 1:
					// static Account newAccount = new Account(userId, username, email,
					// userPassword);
					System.out.println("Enter User ID:");
					userId = userIn.nextInt();
					System.out.println("Enter Username:");
					username = userIn.next();
					System.out.println("Enter Email:");
					email = userIn.next();

					while (!input) {
						System.out.println("\nEnter Password:\n(Must Contain atleast 2 letters & 2 digits):\n");
						userPassword = userIn.next();
						if (Passwords.isValid(userPassword.toCharArray()))
							input = true;
						else
							System.out.println("Invalid Password.");
					}
					PasswordHashRequest hashRequest = PasswordHashRequest.newBuilder().setUserId(userId)
							.setPassword(userPassword).build();
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
				}

			}
		} finally {
			// Don't stop process, keep alive to receive async response
			Thread.currentThread().join();
		}
	}
}
