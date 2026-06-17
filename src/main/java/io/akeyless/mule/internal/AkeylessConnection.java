package io.akeyless.mule.internal;

import io.akeyless.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Connection object that authenticates to Akeyless and retrieves secret values.
 */
public class AkeylessConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(AkeylessConnection.class);

	private final ConnectionConfigurations configuration;
	private final AkeylessClient client;

	public AkeylessConnection(ConnectionConfigurations configuration) {
		this.configuration = configuration;
		this.client = new AkeylessClient(configuration);
		LOGGER.debug("Akeyless connection created for API URL: {}", configuration.getApiUrl());
	}

	public String readStaticSecret(String secretPath, String key) {
		return read(secretPath, key, SecretKind.STATIC);
	}

	public String readDynamicSecret(String secretPath, String key) {
		return read(secretPath, key, SecretKind.DYNAMIC);
	}

	public String readRotatedSecret(String secretPath, String key) {
		return read(secretPath, key, SecretKind.ROTATED);
	}

	private String read(String secretPath, String key, SecretKind kind) {
		try {
			String resolvedPath = AkeylessClient.resolveSecretPath(configuration.getSecretBasePath(), secretPath);
			switch (kind) {
				case STATIC:
					return client.getStaticSecretValue(resolvedPath, key);
				case DYNAMIC:
					return client.getDynamicSecretValue(resolvedPath, key);
				case ROTATED:
					return client.getRotatedSecretValue(resolvedPath, key);
				default:
					throw new IllegalStateException("Unsupported secret kind: " + kind);
			}
		} catch (ApiException e) {
			throw new IllegalArgumentException("Failed to read " + kind.name().toLowerCase(Locale.ROOT)
					+ " secret from Akeyless: " + e.getMessage(), e);
		}
	}

	public void disconnect() {
		// SDK uses stateless HTTP; nothing to tear down.
	}

	public boolean validate() {
		return client.validateConnection();
	}

	private enum SecretKind {
		STATIC, DYNAMIC, ROTATED
	}
}
