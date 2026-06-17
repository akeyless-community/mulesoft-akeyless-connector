package io.akeyless.mule.internal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AkeylessClientTest {

	@Test
	public void normalizeItemPathAddsLeadingSlash() {
		assertEquals("/my/secret", AkeylessClient.normalizeItemPath("my/secret"));
		assertEquals("/my/secret", AkeylessClient.normalizeItemPath("/my/secret"));
	}

	@Test
	public void resolveSecretPathUsesAbsolutePathAsIs() {
		assertEquals("/absolute/path",
				AkeylessClient.resolveSecretPath("/mulesoft", "/absolute/path"));
	}

	@Test
	public void resolveSecretPathPrependsBasePath() {
		assertEquals("/mulesoft/prod/db-password",
				AkeylessClient.resolveSecretPath("/mulesoft/prod", "db-password"));
		assertEquals("/mulesoft/prod/db-password",
				AkeylessClient.resolveSecretPath("mulesoft/prod", "db-password"));
	}

	@Test
	public void resolveSecretPathWithoutBasePath() {
		assertEquals("/standalone/secret",
				AkeylessClient.resolveSecretPath(null, "standalone/secret"));
	}

	@Test
	public void resolveSecretPathRejectsEmptyPath() {
		assertThrows(IllegalArgumentException.class,
				() -> AkeylessClient.resolveSecretPath("/base", ""));
	}
}
