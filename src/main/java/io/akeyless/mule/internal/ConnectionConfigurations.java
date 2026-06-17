package io.akeyless.mule.internal;

/**
 * Holds connection parameters for the Akeyless connector.
 */
public class ConnectionConfigurations {

	private String apiUrl;
	private AuthenticationType authenticationType;
	private String accessId;
	private String accessKey;
	private String apiToken;
	private String jwt;
	private String cloudId;
	private String gcpAudience;
	private String secretBasePath;

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
