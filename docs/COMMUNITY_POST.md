# MuleSoft Community post (copy and publish)

Publish this at [MuleSoft Community](https://help.mulesoft.com/s/) → **Ask a Question** or **Share an Idea** (choose the board closest to *Connectors* / *Integration*).

---

**Title:** Community Mule 4 connector for Akeyless secrets (static, dynamic, rotated)

**Body:**

We published an open-source **Akeyless Mule 4 connector** for teams running MuleSoft who want to fetch secrets at runtime without storing them in application config or property files.

**Repository:** https://github.com/akeyless-community/mulesoft-akeyless-connector

### What it does

- Retrieves **static**, **dynamic**, and **rotated** secrets from Akeyless by item path
- Supports authentication via **Access Key**, **API Token**, **JWT**, and **cloud IAM** (AWS, Azure, GCP)
- Works with Akeyless SaaS (`https://api.akeyless.io`) or a self-hosted API Gateway
- Built as a standard Mule 4 extension module (Mule SDK), same pattern as other community Catalyst connectors

### Operations

| Operation | Use case |
|-----------|----------|
| `get-static-secret-value` | Static secrets |
| `get-dynamic-secret-value` | Just-in-time dynamic credentials |
| `get-rotated-secret-value` | Rotated secrets |

### Quick start

```bash
git clone https://github.com/akeyless-community/mulesoft-akeyless-connector.git
cd mulesoft-akeyless-connector
mvn clean install
```

Add to your Mule app `pom.xml`:

```xml
<dependency>
  <groupId>io.akeyless</groupId>
  <artifactId>akeyless-connector</artifactId>
  <version>1.0.0</version>
  <classifier>mule-plugin</classifier>
</dependency>
```

Example connection (Access Key auth):

```xml
<akeyless:config name="Akeyless_Config">
    <akeyless:connection
        apiUrl="https://api.akeyless.io"
        authenticationType="ACCESS_KEY"
        accessId="${akeyless.accessId}"
        accessKey="${akeyless.accessKey}"
        secretBasePath="/mulesoft/prod" />
</akeyless:config>

<akeyless:get-static-secret-value
    config-ref="Akeyless_Config"
    secretPath="db-password" />
```

Pass `accessKey` and other credentials via secure properties or environment variables at deploy time.

### Documentation

- README: https://github.com/akeyless-community/mulesoft-akeyless-connector#readme
- Publishing / Exchange guide: https://github.com/akeyless-community/mulesoft-akeyless-connector/blob/main/docs/PUBLISHING.md

### Feedback welcome

Issues and contributions: https://github.com/akeyless-community/mulesoft-akeyless-connector/issues

If you use Akeyless with MuleSoft, we'd appreciate feedback on auth methods and operations you'd like to see next.

---

**Tags to add in the community UI (if available):** `Mule 4`, `Connector`, `Secrets`, `Security`
