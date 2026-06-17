package io.akeyless.mule.internal;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.display.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection provider for Akeyless authentication and secret retrieval.
 */
public class AkeylessConnectionProvider implements Initialisable, org.mule.runtime.api.connection.PoolingConnectionProvider<AkeylessConnection> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AkeylessConnectionProvider.class);

	@Parameter
	@DisplayName("API URL")
	@Summary("Akeyless API Gateway URL (SaaS default: https://api.akeyless.io)")
	@Optional(defaultValue = "https://api.akeyless.io")
	private String apiUrl;

	@Parameter
	@DisplayName("Authentication Type")
	@Summary("How the connector authenticates to Akeyless")
	@Optional(defaultValue = "ACCESS_KEY")
	private AuthenticationType authenticationType;

	@Parameter
	@DisplayName("Access ID")
	@Summary("Akeyless access ID (required for Access Key, JWT, and cloud IAM auth)")
	@Optional
	private String accessId;

	@Parameter
	@DisplayName("Access Key")
	@Summary("Akeyless access key (required for Access Key authentication)")
	@Password
	@Optional
	private String accessKey;

	@Parameter
	@DisplayName("API Token")
	@Summary("Pre-authenticated Akeyless API token (use with Authentication Type = API Token)")
	@Password
	@Optional
	private String apiToken;

	@Parameter
	@DisplayName("JWT")
	@Summary("Signed JWT for JWT authentication (use with Authentication Type = JWT)")
	@Password
	@Optional
	private String jwt;

	@Parameter
	@DisplayName("Cloud ID")
	@Summary("Optional pre-computed cloud identity token for AWS IAM / Azure AD / GCP. When empty, the connector obtains it from the runtime environment.")
	@Password
	@Optional
	private String cloudId;

	@Parameter
	@DisplayName("GCP Audience")
	@Summary("Optional GCP audience for GCP authentication")
	@Optional
	private String gcpAudience;

	@Parameter
	@DisplayName("Secret Base Path")
	@Summary("Optional folder prefix prepended to relative secret paths in operations (e.g. /mulesoft/prod)")
	@Text
	@Optional
	private String secretBasePath;

	@Override
	public AkeylessConnection connect() throws ConnectionException {
		try {
			ConnectionConfigurations config = buildConfiguration();
			AkeylessConnection connection = new AkeylessConnection(config);
			if (!connection.validate()) {
				throw new ConnectionException("Could not authenticate to Akeyless");
			}
			return connection;
		} catch (Exception e) {
			throw new ConnectionException("Failed to connect to Akeyless: " + e.getMessage(), e);
		}
	}

	@Override
	public void disconnect(AkeylessConnection connection) {
		try {
			connection.disconnect();
		} catch (Exception e) {
			LOGGER.warn("Error disconnecting Akeyless connection", e);
		}
	}

	@Override
	public ConnectionValidationResult validate(AkeylessConnection connection) {
		if (connection.validate()) {
			return ConnectionValidationResult.success();
		}
		return ConnectionValidationResult.failure(
				"Connection to Akeyless could not be validated",
				new RuntimeException("Connection to Akeyless could not be validated"));
	}

	@Override
	public void initialise() throws InitialisationException {
		// no-op
	}

	private ConnectionConfigurations buildConfiguration() {
		ConnectionConfigurations config = new ConnectionConfigurations();
		config.setApiUrl(apiUrl);
		config.setAuthenticationType(authenticationType == null ? AuthenticationType.ACCESS_KEY : authenticationType);
		config.setAccessId(accessId);
		config.setAccessKey(accessKey);
		config.setApiToken(apiToken);
		config.setJwt(jwt);
		config.setCloudId(cloudId);
		config.setGcpAudience(gcpAudience);
		config.setSecretBasePath(secretBasePath);
		return config;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getCloudId() {
		return cloudId;
	}

	public void setCloudId(String cloudId) {
		this.cloudId = cloudId;
	}

	public String getGcpAudience() {
		return gcpAudience;
	}

	public void setGcpAudience(String gcpAudience) {
		this.gcpAudience = gcpAudience;
	}

	public String getSecretBasePath() {
		return secretBasePath;
	}

	public void setSecretBasePath(String secretBasePath) {
		this.secretBasePath = secretBasePath;
	}
}
