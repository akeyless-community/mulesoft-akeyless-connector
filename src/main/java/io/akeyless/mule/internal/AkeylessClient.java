package io.akeyless.mule.internal;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.akeyless.client.ApiClient;
import io.akeyless.client.ApiException;
import io.akeyless.client.api.V2Api;
import io.akeyless.client.model.Auth;
import io.akeyless.client.model.AuthOutput;
import io.akeyless.client.model.GetCertificateValue;
import io.akeyless.client.model.GetCertificateValueOutput;
import io.akeyless.client.model.GetDynamicSecretValue;
import io.akeyless.client.model.GetRotatedSecretValue;
import io.akeyless.client.model.GetSecretValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Thin wrapper around the Akeyless Java SDK for authentication and secret retrieval.
 */
public class AkeylessClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AkeylessClient.class);
	private static final Gson GSON = new Gson();
	private static final String DEFAULT_API_URL = "https://api.akeyless.io";

	private final ConnectionConfigurations configuration;
	private V2Api api;
	private String token;

	public AkeylessClient(ConnectionConfigurations configuration) {
		this.configuration = configuration;
	}

	private synchronized V2Api api() {
		if (api == null) {
			String apiUrl = configuration.getApiUrl();
			String url = apiUrl == null || apiUrl.trim().isEmpty() ? DEFAULT_API_URL : apiUrl.trim();
			String basePath = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
			ApiClient client = new ApiClient();
			client.setBasePath(basePath);
			api = new V2Api(client);
		}
		return api;
	}

	public synchronized String getToken() throws ApiException {
		String apiToken = configuration.getApiToken();
		if (apiToken != null && !apiToken.trim().isEmpty()) {
			return apiToken.trim();
		}
		if (token != null && !token.isEmpty()) {
			return token;
		}

		AuthenticationType authType = configuration.getAuthenticationType();
		if (authType == AuthenticationType.API_TOKEN) {
			throw new ApiException("API token authentication requires the apiToken parameter");
		}

		LOGGER.debug("Authenticating to Akeyless with method={}", authType);
		Auth auth = AkeylessAuthenticator.buildAuth(configuration);
		AuthOutput authOutput = api().auth(auth);
		token = authOutput != null ? authOutput.getToken() : null;
		if (token == null || token.isEmpty()) {
			throw new ApiException("Akeyless authentication did not return a token");
		}
		return token;
	}

	public String getStaticSecretValue(String secretPath, String key) throws ApiException {
		String normalizedPath = normalizeItemPath(secretPath);
		String value = fetchStaticSecret(normalizedPath);
		if (key != null && !key.trim().isEmpty()) {
			return extractJsonField(value, key.trim());
		}
		return value;
	}

	public String getDynamicSecretValue(String secretPath, String key) throws ApiException {
		String normalizedPath = normalizeItemPath(secretPath);
		String value = fetchDynamic(normalizedPath);
		if (key != null && !key.trim().isEmpty()) {
			return extractJsonField(value, key.trim());
		}
		return value;
	}

	public String getRotatedSecretValue(String secretPath, String key) throws ApiException {
		String normalizedPath = normalizeItemPath(secretPath);
		String value = fetchRotated(normalizedPath);
		if (key != null && !key.trim().isEmpty()) {
			return extractJsonField(value, key.trim());
		}
		return value;
	}

	public boolean validateConnection() {
		try {
			getToken();
			return true;
		} catch (ApiException e) {
			LOGGER.warn("Akeyless connection validation failed: {}", e.getMessage());
			return false;
		}
	}

	private String fetchStaticSecret(String path) throws ApiException {
		GetSecretValue body = new GetSecretValue();
		body.setToken(getToken());
		body.setNames(Collections.singletonList(path));
		Map<String, Object> out = api().getSecretValue(body);
		return mapOutputToString(out, path);
	}

	private String fetchDynamic(String path) throws ApiException {
		GetDynamicSecretValue body = new GetDynamicSecretValue();
		body.setToken(getToken());
		body.setName(trimLeadingSlash(path));
		body.setJson(true);
		Map<String, Object> out = api().getDynamicSecretValue(body);
		if (out == null || out.isEmpty()) {
			throw new ApiException("Empty dynamic secret value for: " + path);
		}
		return GSON.toJson(out);
	}

	private String fetchRotated(String path) throws ApiException {
		GetRotatedSecretValue body = new GetRotatedSecretValue();
		body.setToken(getToken());
		body.setNames(trimLeadingSlash(path));
		body.setJson(true);
		Map<String, Object> out = api().getRotatedSecretValue(body);
		if (out == null || out.isEmpty()) {
			throw new ApiException("Empty rotated secret value for: " + path);
		}
		return GSON.toJson(out);
	}

	@SuppressWarnings("unused")
	private String fetchCertificate(String path) throws ApiException {
		GetCertificateValue body = new GetCertificateValue();
		body.setToken(getToken());
		body.setName(trimLeadingSlash(path));
		GetCertificateValueOutput out = api().getCertificateValue(body);
		if (out == null) {
			throw new ApiException("No certificate payload returned for: " + path);
		}
		String certPem = out.getCertificatePem();
		if (certPem != null && !certPem.isBlank()) {
			return certPem;
		}
		throw new ApiException("Certificate response had no PEM data for: " + path);
	}

	private static String mapOutputToString(Map<String, Object> out, String path) throws ApiException {
		if (out == null || out.isEmpty()) {
			throw new ApiException("No value returned for secret: " + path);
		}
		Object value = out.get(path);
		if (value == null && out.size() == 1) {
			value = out.values().iterator().next();
		}
		if (value == null) {
			throw new ApiException("Empty value for secret: " + path);
		}
		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof byte[]) {
			return new String((byte[]) value);
		}
		if (value instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) value;
			if (map.containsKey("value") && map.get("value") instanceof String) {
				return (String) map.get("value");
			}
			return GSON.toJson(map);
		}
		return value.toString();
	}

	private static String extractJsonField(String value, String key) {
		String trimmed = value == null ? "" : value.trim();
		if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
			throw new IllegalArgumentException("Secret value is not JSON; cannot extract key: " + key);
		}
		JsonElement element = JsonParser.parseString(trimmed);
		if (!element.isJsonObject()) {
			throw new IllegalArgumentException("Secret value is not a JSON object; cannot extract key: " + key);
		}
		JsonObject object = element.getAsJsonObject();
		if (!object.has(key)) {
			throw new IllegalArgumentException("Key '" + key + "' not found in secret JSON");
		}
		JsonElement field = object.get(key);
		if (field.isJsonNull()) {
			throw new IllegalArgumentException("Key '" + key + "' is null in secret JSON");
		}
		if (field.isJsonPrimitive()) {
			return field.getAsString();
		}
		return field.toString();
	}

	static String normalizeItemPath(String name) {
		String path = name == null ? "" : name.trim();
		if (path.isEmpty()) {
			return path;
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return path;
	}

	static String resolveSecretPath(String basePath, String secretPath) {
		String path = secretPath == null ? "" : secretPath.trim();
		if (path.isEmpty()) {
			throw new IllegalArgumentException("secretPath must not be empty");
		}
		if (path.startsWith("/")) {
			return normalizeItemPath(path);
		}
		String base = basePath == null ? "" : basePath.trim();
		if (base.isEmpty()) {
			return normalizeItemPath(path);
		}
		if (!base.startsWith("/")) {
			base = "/" + base;
		}
		if (base.endsWith("/")) {
			base = base.substring(0, base.length() - 1);
		}
		return normalizeItemPath(base + "/" + path);
	}

	private static String trimLeadingSlash(String path) {
		if (path == null || path.isEmpty()) {
			return path;
		}
		return path.startsWith("/") ? path.substring(1) : path;
	}
}
