package ie.gmit.ds;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;

import ie.gmit.ds.utils.Passwords;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {
	
	private static final Logger logger = Logger.getLogger(PasswordServiceImpl.class.getName());

	public PasswordServiceImpl() {}

	@Override
	public void hash(PasswordHashRequest request, StreamObserver<PasswordHashResponse> responseObserver) {
		logger.info("HASH METHOD IN");

			char[] password = request.getPassword().toCharArray();
			byte[] salt = Passwords.getNextSalt();
			byte[] hashedPassword = Passwords.hash(password, salt);
			
			ByteString hashedPasswordBs = ByteString.copyFrom(hashedPassword);
			ByteString saltBs = ByteString.copyFrom(salt);
					
			PasswordHashResponse response = PasswordHashResponse.newBuilder()
					.setUserId(request.getUserId())
					.setHashedPassword(hashedPasswordBs)
					.setSalt(saltBs)
					.build();
			
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void validate(PasswordValidateRequest request, StreamObserver<BoolValue> responseObserver) {
		try {
			char[] password = request.getPassword().toCharArray();
			byte[] salt = request.getSalt().toByteArray();
			byte[] expectedHash = request.getHashedPassword().toByteArray();
	
			if(Passwords.isExpectedPassword(password, salt, expectedHash))
				responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
			else
				responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
			
			responseObserver.onCompleted();
			
		} catch (StatusRuntimeException e) {
		      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
	           return;
		}
	}
}
