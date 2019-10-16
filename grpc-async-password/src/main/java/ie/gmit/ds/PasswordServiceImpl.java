package ie.gmit.ds;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.protobuf.BoolValue;

import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

	private ArrayList<PasswordHashRequest> accountList;
	private static final Logger logger = Logger.getLogger(PasswordServiceImpl.class.getName());

	public PasswordServiceImpl() {
		accountList = new ArrayList<>();
		createDummyAccounts();
		generateRandomPassword();
	}
	
	@Override
	public void hash(PasswordHashRequest request, StreamObserver<PasswordHashResponse> responseObserver) {
		try {
			accountList.add(request);
			logger.info("Added new account:" + request);
			responseObserver.onNext(PasswordHashResponse.newBuilder().setValue(true).build());
		} catch (RuntimeException ex) {
			
		}
	}

	@Override
	public void validate(PasswordValidateRequest request, StreamObserver<PasswordValidateResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.validate(request, responseObserver);
	}

	private void createDummyAccounts() {
		accountList.add(PasswordHashRequest.newBuilder().setUserId("1").setPassword("testPassword").build());
		accountList.add(PasswordHashRequest.newBuilder().setUserId("2").setPassword("testPassword2").build());
	}
	
	private void generateRandomPassword() {
		Passwords.generateRandomPassword(5);
	}
}
