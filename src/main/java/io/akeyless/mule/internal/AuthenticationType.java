package io.akeyless.mule.internal;

/**
 * Supported Akeyless authentication methods for the Mule connection.
 */
public enum AuthenticationType {

	ACCESS_KEY("Access Key (API Key)"),
	API_TOKEN("API Token"),
	JWT("JWT"),
	AWS_IAM("AWS IAM"),
	AZURE_AD("Azure AD"),
	GCP("Google Cloud (GCP)");

	private final String label;

	AuthenticationType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
