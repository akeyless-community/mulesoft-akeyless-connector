package io.akeyless.mule.internal;

import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.display.Text;

/**
 * Operations exposed by the Akeyless connector.
 */
public class AkeylessOperations {

	@DisplayName("Akeyless: Get Static Secret Value")
	@Summary("Gets the value of a static secret at the specified Akeyless path")
	@MediaType("application/json")
	public String getStaticSecretValue(
			@DisplayName("Secret Path") @Text final String secretPath,
			@DisplayName("JSON Key") @Summary("Optional field name when the secret value is JSON") @Optional final String key,
			@Connection AkeylessConnection connection) {
		return connection.readStaticSecret(secretPath, key);
	}

	@DisplayName("Akeyless: Get Dynamic Secret Value")
	@Summary("Provision and return credentials from a dynamic secret at the specified Akeyless path")
	@MediaType("application/json")
	public String getDynamicSecretValue(
			@DisplayName("Secret Path") @Text final String secretPath,
			@DisplayName("JSON Key") @Summary("Optional field name when the returned value is JSON") @Optional final String key,
			@Connection AkeylessConnection connection) {
		return connection.readDynamicSecret(secretPath, key);
	}

	@DisplayName("Akeyless: Get Rotated Secret Value")
	@Summary("Gets the current value of a rotated secret at the specified Akeyless path")
	@MediaType("application/json")
	public String getRotatedSecretValue(
			@DisplayName("Secret Path") @Text final String secretPath,
			@DisplayName("JSON Key") @Summary("Optional field name when the returned value is JSON") @Optional final String key,
			@Connection AkeylessConnection connection) {
		return connection.readRotatedSecret(secretPath, key);
	}
}
