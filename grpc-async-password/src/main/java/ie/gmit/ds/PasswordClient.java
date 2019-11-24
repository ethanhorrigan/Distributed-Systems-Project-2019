package ie.gmit.ds;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import ie.gmit.ds.utils.Constants;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class PasswordClient {

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

	/*
	 * Synchronous boolean to validate the password
	 * Takes in user password, hashed version & the salt used.
	 */
	public boolean validatePassword(String password, ByteString hashed, ByteString salt) {
		try {
			System.out.println("Client: validatePassword");
			BoolValue value = syncPasswordService.validate(PasswordValidateRequest.newBuilder().setPassword(password)
					.setHashedPassword(hashed).setSalt(salt).build());
			
			return(value.getValue());
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return false;
		}
	}
}
