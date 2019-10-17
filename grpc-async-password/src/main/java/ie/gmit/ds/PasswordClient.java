package ie.gmit.ds;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.BoolValue;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class PasswordClient {

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

    public void addNewPassword(PasswordHashRequest newPassword) {
        logger.info("Adding new Password: " + newPassword);
        BoolValue result = BoolValue.newBuilder().setValue(false).build();
        try {
            result = syncPasswordService.hash(newPassword);
        } catch (StatusRuntimeException ex) {
            logger.log(Level.WARNING, "RPC failed: {0}", ex.getStatus());
            return;
        }
        if (result.getValue()) {
            logger.info("Successfully added password " + newPassword);
        } else {
            logger.warning("Failed to add password");
        }
    }
    
	public static void main(String[] args) throws Exception {
		PasswordClient client = new PasswordClient("localhost", 50551);
		
		//PasswordHashRequest password = PasswordHashRequest.newBuilder().setId("1234").setName("New Item")
			//	.setDescription("Best New Item").build();
		try {
			//client.addNewInventoryItem(newItem);
			//client.getItems();
		} finally {
			// Don't stop process, keep alive to receive async response
			Thread.currentThread().join();
		}
	}
}
