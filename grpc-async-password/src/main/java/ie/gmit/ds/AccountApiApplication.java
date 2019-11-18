package ie.gmit.ds;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class AccountApiApplication extends Application<AccountApiConfig> {

    public static void main(String[] args) throws Exception {
        new AccountApiApplication().run(args);
    }

	@Override
	public void run(AccountApiConfig configuration, Environment environment) throws Exception {
        final AccountApiResource resource = new AccountApiResource();

        environment.jersey().register(resource);
	}
}
