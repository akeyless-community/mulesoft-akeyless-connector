package io.akeyless.mule.internal;

import io.akeyless.client.model.Auth;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AkeylessAuthenticatorTest {

	@Test
	public void buildAccessKeyAuth() throws Exception {
		ConnectionConfigurations config = new ConnectionConfigurations();
		config.setAuthenticationType(AuthenticationType.ACCESS_KEY);
		config.setAccessId("p-abc");
		config.setAccessKey("a2V5\n");

		Auth auth = AkeylessAuthenticator.buildAuth(config);
		assertEquals("p-abc", auth.getAccessId());
		assertEquals("a2V5", auth.getAccessKey());
		assertEquals("access_key", auth.getAccessType());
	}

	@Test
	public void buildJwtAuth() throws Exception {
		ConnectionConfigurations config = new ConnectionConfigurations();
		config.setAuthenticationType(AuthenticationType.JWT);
		config.setAccessId("p-jwt");
		config.setJwt("eyJ.test.token");

		Auth auth = AkeylessAuthenticator.buildAuth(config);
		assertEquals("p-jwt", auth.getAccessId());
		assertEquals("jwt", auth.getAccessType());
		assertEquals("eyJ.test.token", auth.getJwt());
	}

	@Test
	public void buildCloudAuthUsesProvidedCloudId() throws Exception {
		ConnectionConfigurations config = new ConnectionConfigurations();
		config.setAuthenticationType(AuthenticationType.AWS_IAM);
		config.setAccessId("p-aws");
		config.setCloudId("provided-cloud-id");

		Auth auth = AkeylessAuthenticator.buildAuth(config);
		assertNotNull(auth);
		assertEquals("p-aws", auth.getAccessId());
		assertEquals("aws_iam", auth.getAccessType());
		assertEquals("provided-cloud-id", auth.getCloudId());
	}
}
