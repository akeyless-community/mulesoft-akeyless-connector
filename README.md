# Akeyless Mule 4 Connector

MuleSoft connector for retrieving secrets from [Akeyless](https://www.akeyless.io) at runtime in Mule 4 applications.

**Repository:** https://github.com/akeyless-community/mulesoft-akeyless-connector

## Features

- **Authentication:** Access Key, API Token, JWT, AWS IAM, Azure AD, GCP
- **Operations:**
  - `get-static-secret-value` — static secrets
  - `get-dynamic-secret-value` — dynamic (just-in-time) credentials
  - `get-rotated-secret-value` — rotated secrets
- Connect to Akeyless SaaS or a self-hosted API Gateway
- Optional secret base path on the connection
- Optional JSON key parameter to extract a field from JSON payloads

## Prerequisites

- Mule Runtime 4.1.4+
- JDK 8 or 11 for building (use Anypoint Studio’s bundled JDK)
- An Akeyless account with a matching auth method configured
- Maven 3.x

## Installation

### From GitHub (build locally)

```bash
git clone https://github.com/akeyless-community/mulesoft-akeyless-connector.git
cd mulesoft-akeyless-connector
mvn clean install
```

Add to your Mule application `pom.xml`:

```xml
<dependency>
  <groupId>io.akeyless</groupId>
  <artifactId>akeyless-connector</artifactId>
  <version>1.0.0</version>
  <classifier>mule-plugin</classifier>
</dependency>
```

### From Anypoint Exchange

Publish the connector to your Anypoint organization and search for **Akeyless** in the Studio palette. See [docs/PUBLISHING.md](docs/PUBLISHING.md) for full Exchange setup.

## Configuration

```xml
<akeyless:config name="Akeyless_Config">
    <akeyless:connection
        apiUrl="${akeyless.apiUrl}"
        authenticationType="ACCESS_KEY"
        accessId="${akeyless.accessId}"
        accessKey="${akeyless.accessKey}"
        secretBasePath="/mulesoft/prod" />
</akeyless:config>
```

### Connection parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `apiUrl` | No | API Gateway URL (default: `https://api.akeyless.io`) |
| `authenticationType` | No | `ACCESS_KEY` (default), `API_TOKEN`, `JWT`, `AWS_IAM`, `AZURE_AD`, `GCP` |
| `accessId` | Yes* | Akeyless Access ID |
| `accessKey` | Yes* | Access Key (for `ACCESS_KEY`) |
| `apiToken` | Yes* | Pre-authenticated token (for `API_TOKEN`) |
| `jwt` | Yes* | Signed JWT (for `JWT`) |
| `cloudId` | No | Pre-computed cloud identity; auto-detected on AWS/Azure/GCP when empty |
| `gcpAudience` | No | GCP audience (for `GCP`) |
| `secretBasePath` | No | Folder prefix for relative secret paths |

\* Depends on `authenticationType`.

### Authentication examples

**JWT** (Mule running anywhere; JWT supplied at deploy time):

```xml
<akeyless:connection
    authenticationType="JWT"
    accessId="${akeyless.accessId}"
    jwt="${akeyless.jwt}" />
```

**AWS IAM** (Mule on EC2/ECS with an IAM role, or provide `cloudId`):

```xml
<akeyless:connection
    authenticationType="AWS_IAM"
    accessId="${akeyless.accessId}" />
```

**Azure AD** (Mule on Azure with managed identity):

```xml
<akeyless:connection
    authenticationType="AZURE_AD"
    accessId="${akeyless.accessId}" />
```

**GCP** (Mule on GCP with a service account):

```xml
<akeyless:connection
    authenticationType="GCP"
    accessId="${akeyless.accessId}"
    gcpAudience="${akeyless.gcpAudience}" />
```

Pass secrets (`accessKey`, `apiToken`, `jwt`) via secure properties or environment variables — not plaintext in source control.

## Operations

### Get static secret value

```xml
<akeyless:get-static-secret-value
    config-ref="Akeyless_Config"
    secretPath="db-password"
    key="password" />
```

### Get dynamic secret value

```xml
<akeyless:get-dynamic-secret-value
    config-ref="Akeyless_Config"
    secretPath="db-dynamic"
    key="password" />
```

### Get rotated secret value

```xml
<akeyless:get-rotated-secret-value
    config-ref="Akeyless_Config"
    secretPath="db-rotated"
    key="password" />
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| `secretPath` | Yes | Akeyless item path (absolute or relative to `secretBasePath`) |
| `key` | No | JSON field name when the returned value is a JSON object |

## Making the connector discoverable for MuleSoft users

| Path | Who can use it | Effort |
|------|----------------|--------|
| **Anypoint Exchange (public)** | Any MuleSoft customer via Studio search | Medium — needs Anypoint org |
| **GitHub + README** | Developers who find the repo | Done |
| **MuleSoft Catalyst listing** | Community users browsing Catalyst repos | Low — open a listing request |
| **Partner marketplace** | Enterprise MuleSoft accounts | High — partner review |

**Recommended:** publish to **public Anypoint Exchange** and link the Exchange asset from this README. Step-by-step instructions: [docs/PUBLISHING.md](docs/PUBLISHING.md).

You do **not** need Exchange to open-source the connector. Exchange is only required for the in-Studio “Search in Exchange” install experience.

## Tests

```bash
mvn test
```

Unit tests cover path resolution and auth payload building. Live integration tests require `AKEYLESS_ACCESS_ID` and `AKEYLESS_ACCESS_KEY` in the environment.

## License

Apache License 2.0 — see [LICENSE](LICENSE).
