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
		try {
			char[] password = request.getPassword().toCharArray();
			byte[] salt = Passwords.getNextSalt();
			byte[] hashedPassword = Passwords.hash(password, salt);
			
			/**
			 * Initially was using StringBuilders, but converting it to a ByteString and using a loop just resulted in messy code.
			 * opted for ByteString instead: https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/ByteString
			 */
			/*
			StringBuilder passwordSb = new StringBuilder(Constants.PASSWORD_LENGTH);
			StringBuilder hashedPasswordSb = new StringBuilder(Constants.PASSWORD_LENGTH);
			StringBuilder saltSb = new StringBuilder(Constants.PASSWORD_LENGTH);
			*/

			ByteString hashedPasswordBs = ByteString.copyFrom(hashedPassword);
			ByteString saltBs = ByteString.copyFrom(salt);
					
			responseObserver.onNext(PasswordHashResponse.newBuilder()
					.setUserId(request.getUserId())
					.setHashedPassword(hashedPasswordBs)
					.setSalt(saltBs)
					.build());
			
			logger.info("Added New Password " + request);
			
		} catch (RuntimeException ex) {
			responseObserver.onNext(PasswordHashResponse.newBuilder().getDefaultInstanceForType());
		}
		responseObserver.onCompleted();
	}

	@Override
	public void validate(PasswordValidateRequest request, StreamObserver<BoolValue> responseObserver) {
		try {
			char[] password = request.getPassword().toCharArray();
			byte[] salt = Passwords.getNextSalt();
			byte[] expectedHash = Passwords.hash(password, salt);
	
			if(Passwords.isExpectedPassword(password, salt, expectedHash))
				responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
			else
				responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
			
		} catch (StatusRuntimeException e) {
		      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
	           return;
		}
	}
}
