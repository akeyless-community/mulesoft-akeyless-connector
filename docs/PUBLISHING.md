# Publishing the Akeyless Mule Connector

This guide explains how to make the connector available to MuleSoft users.

## Do you need an Anypoint Exchange organization?

**Yes, if you want MuleSoft users to install the connector from Exchange** (the standard path in Anypoint Studio and CloudHub).

You do **not** need Exchange to host the source code on GitHub. Users can also build from source and install locally.

### How to get an Anypoint organization

1. Sign up for [Anypoint Platform](https://anypoint.mulesoft.com/login/) (trial or paid).
2. After login, open **Access Management** → your organization.
3. Copy the **Organization ID** (UUID). This becomes the Maven `groupId` when publishing to Exchange.

Update `pom.xml` before publishing:

```xml
<groupId>YOUR-ORGANIZATION-UUID</groupId>
```

The reference [HashiCorp Vault connector](https://github.com/mulesoft-catalyst/hashicorp-vault-connector) uses the org UUID as `groupId` for the same reason.

## Option A — Publish to Anypoint Exchange (recommended for discoverability)

This is how MuleSoft users normally find and add connectors in Studio.

### Steps

1. **Configure Maven credentials** in `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>anypoint-exchange</id>
      <username>~~~Client~~~</username>
      <password>YOUR_CONNECTED_APP_CLIENT_ID~?~YOUR_CONNECTED_APP_CLIENT_SECRET</password>
    </server>
  </servers>
</settings>
```

Create a [Connected App](https://docs.mulesoft.com/access-management/connected-apps-developers) in Anypoint Platform with **Design Center Developer** and **Exchange Contributor** scopes.

2. **Set the Exchange repository** in `pom.xml` (already templated):

```xml
<properties>
  <repository.id>anypoint-exchange</repository.id>
  <exchange.url>
    https://maven.anypoint.mulesoft.com/api/v2/organizations/${project.groupId}/maven
  </exchange.url>
</properties>
```

3. **Build** (requires JDK 8 or 11 and Anypoint Studio toolchain):

```bash
mvn clean deploy
```

4. **Publish asset metadata** in Exchange:
   - Open [Anypoint Exchange](https://anypoint.mulesoft.com/exchange/)
   - The connector appears after deploy, or upload manually
   - Set visibility to **Public** so any Anypoint user can find it
   - Add tags: `akeyless`, `secrets`, `security`, `connector`

5. **Users install it** from Studio:
   - Mule Palette → **Search in Exchange** → "Akeyless"
   - Add dependency to the Mule app `pom.xml`:

```xml
<dependency>
  <groupId>YOUR-ORGANIZATION-UUID</groupId>
  <artifactId>akeyless-connector</artifactId>
  <version>1.0.0</version>
  <classifier>mule-plugin</classifier>
</dependency>
```

Official docs: [Publish assets to Exchange using Maven](https://docs.mulesoft.com/exchange/to-publish-assets-maven)

## Option B — GitHub + local install (no Exchange org)

Users clone the repo and install into the local Maven repository:

```bash
git clone https://github.com/akeyless-community/mulesoft-akeyless-connector.git
cd mulesoft-akeyless-connector
mvn clean install
```

Then reference it from a Mule app `pom.xml` using `groupId` `io.akeyless` (development default).

## Option C — MuleSoft Catalyst / community listing

For broader visibility without owning Exchange publishing:

1. Keep the repo public under [akeyless-community](https://github.com/akeyless-community).
2. Open a discussion or PR with [mulesoft-catalyst](https://github.com/mulesoft-catalyst) to list it alongside their community connectors.
3. Share in the [MuleSoft Community](https://help.mulesoft.com/s/) with install instructions linking to this repo and Exchange (once published).

## Option D — MuleSoft partner / marketplace path

For official marketplace-style listing:

- Apply via the [MuleSoft Partner Program](https://www.mulesoft.com/partners)
- Submit the connector for review as a partner asset
- Longer process, but highest visibility in enterprise accounts

## Recommended path for Akeyless Community

| Stage | Action |
|-------|--------|
| **Now** | Source on GitHub (`akeyless-community/mulesoft-akeyless-connector`) |
| **Next** | Create Anypoint trial org → publish to Exchange as **Public** |
| **Then** | Link Exchange asset from README and Akeyless docs |
| **Optional** | MuleSoft Catalyst community listing + partner program |

## Build requirements

- **JDK 8 or 11** (Mule 4.1 SDK parent is not compatible with JDK 17+)
- **Anypoint Studio 7.x** (bundles the correct JDK and Mule tooling)
- Network access to `repository.mulesoft.org` and `akeyless.jfrog.io`
