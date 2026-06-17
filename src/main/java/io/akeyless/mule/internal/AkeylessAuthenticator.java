package io.akeyless.mule.internal;

import io.akeyless.client.ApiException;
import io.akeyless.client.model.Auth;
import io.akeyless.cloudid.CloudIdProvider;
import io.akeyless.cloudid.CloudProviderFactory;

/**
 * Builds {@link Auth} requests for all supported authentication methods.
 */
final class AkeylessAuthenticator {

	private AkeylessAuthenticator() {
	}

	static Auth buildAuth(ConnectionConfigurations config) throws ApiException {
		AuthenticationType type = config.getAuthenticationType();
		if (type == null) {
			type = AuthenticationType.ACCESS_KEY;
		}

		switch (type) {
			case API_TOKEN:
				throw new ApiException("API token is used directly; no auth request is required");
			case ACCESS_KEY:
				return buildAccessKeyAuth(config);
			case JWT:
				return buildJwtAuth(config);
			case AWS_IAM:
				return buildCloudAuth(config, "aws_iam");
			case AZURE_AD:
				return buildCloudAuth(config, "azure_ad");
			case GCP:
				return buildGcpAuth(config);
			default:
				throw new ApiException("Unsupported authentication type: " + type);
		}
	}

	private static Auth buildAccessKeyAuth(ConnectionConfigurations config) throws ApiException {
		String accessId = trim(config.getAccessId());
		String accessKey = normalizeAccessKey(config.getAccessKey());
		if (accessId == null || accessId.isEmpty() || accessKey.isEmpty()) {
			throw new ApiException("Access ID and Access Key are required for Access Key authentication");
		}
		Auth auth = new Auth();
		auth.setAccessId(accessId);
		auth.setAccessKey(accessKey);
		auth.setAccessType("access_key");
		return auth;
	}

	private static Auth buildJwtAuth(ConnectionConfigurations config) throws ApiException {
		String accessId = trim(config.getAccessId());
		String jwt = trim(config.getJwt());
		if (accessId == null || accessId.isEmpty() || jwt == null || jwt.isEmpty()) {
			throw new ApiException("Access ID and JWT are required for JWT authentication");
		}
		Auth auth = new Auth();
		auth.setAccessId(accessId);
		auth.setAccessType("jwt");
		auth.setJwt(jwt);
		return auth;
	}

	private static Auth buildCloudAuth(ConnectionConfigurations config, String accessType) throws ApiException {
		String accessId = trim(config.getAccessId());
		if (accessId == null || accessId.isEmpty()) {
			throw new ApiException("Access ID is required for " + accessType + " authentication");
		}
		String cloudId = resolveCloudId(accessType, config.getCloudId());
		Auth auth = new Auth();
		auth.setAccessId(accessId);
		auth.setAccessType(accessType);
		auth.setCloudId(cloudId);
		return auth;
	}

	private static Auth buildGcpAuth(ConnectionConfigurations config) throws ApiException {
		Auth auth = buildCloudAuth(config, "gcp");
		String audience = trim(config.getGcpAudience());
		if (audience != null && !audience.isEmpty()) {
			auth.setGcpAudience(audience);
		}
		return auth;
	}

	private static String resolveCloudId(String accessType, String configuredCloudId) throws ApiException {
		String cloudId = trim(configuredCloudId);
		if (cloudId != null && !cloudId.isEmpty()) {
			return cloudId;
		}
		try {
			CloudIdProvider provider = CloudProviderFactory.getCloudIdProvider(accessType);
			return provider.getCloudId();
		} catch (Exception e) {
			throw new ApiException("Could not obtain cloud identity for " + accessType
					+ ". Run Mule on the matching cloud platform or provide cloudId explicitly. "
					+ e.getMessage(), e);
		}
	}

	static String normalizeAccessKey(String raw) {
		if (raw == null) {
			return "";
		}
		return raw.trim().replaceAll("\\s+", "");
	}

	private static String trim(String value) {
		return value == null ? null : value.trim();
	}
}
