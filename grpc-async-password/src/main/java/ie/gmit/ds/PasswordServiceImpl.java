package ie.gmit.ds;

import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

	@Override
	public void hash(PasswordHashInput request, StreamObserver<PasswordHashOutput> responseObserver) {
		// TODO Auto-generated method stub
		super.hash(request, responseObserver);
	}

	@Override
	public void validate(PasswordValidateInput request, StreamObserver<PasswordValidateOutput> responseObserver) {
		// TODO Auto-generated method stub
		super.validate(request, responseObserver);
	}

}
