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
	}

	@Override
	public void hash(PasswordHashRequest request, StreamObserver<BoolValue> responseObserver) {
		try {
			accountList.add(request);
			logger.info("Added New Password " + request);
			responseObserver.onNext(BoolValue.newBuilder().setValue(true).build());
		} catch (RuntimeException ex) {
			responseObserver.onNext(BoolValue.newBuilder().setValue(false).build());
		}
		responseObserver.onCompleted();
	}

	@Override
	public void validate(PasswordValidateRequest request, StreamObserver<PasswordHashResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.validate(request, responseObserver);
	}

	private void createDummyAccounts() {
		accountList.add(PasswordHashRequest.newBuilder().setUserId(1).setPassword(Passwords.generateRandomPassword(Constants.PASSWORD_LENGTH)).build());
		accountList.add(PasswordHashRequest.newBuilder().setUserId(2).setPassword(Passwords.generateRandomPassword(Constants.PASSWORD_LENGTH)).build());
	}


	public static void main(String[] args) {
		char[] password;
		byte[] hashedPassword;
		byte[] salt;
		
		StringBuilder passwordSb = new StringBuilder(Constants.PASSWORD_LENGTH);
		StringBuilder hashedPasswordSb = new StringBuilder(Constants.PASSWORD_LENGTH);
		StringBuilder saltSb = new StringBuilder(Constants.PASSWORD_LENGTH);

		password = Passwords.generateRandomPassword(5).toCharArray();
		salt = Passwords.getNextSalt();
		hashedPassword = Passwords.hash(password, salt);

		for (int j = 0; j < password.length; j++) {
			passwordSb.append(password[j]);
			hashedPasswordSb.append(hashedPassword[j]);
			saltSb.append("["+salt[j]+"]");
		}
		 System.out.println("password:" + passwordSb.toString());
		 System.out.println("salt:" + saltSb.toString());
		 System.out.println("hashedPassword:" + hashedPasswordSb.toString());
		// System.out.println("salt used: " + salt);
		// System.out.println("Hashed Password: "+ hashedPassword.toString());
	}
}
