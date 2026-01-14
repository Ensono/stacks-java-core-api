---
agent: agent
name: Ensono Maven Central Migration
model: Auto (copilot)
description: Switch to SNAPSHOTs and the Maven Central Publisher Portal
---

## Purpose

This prompt provides step-by-step instructions for migrating a Java project
from:

1. **Old namespace**: `com.amido` → **New namespace**: `com.ensono`
2. **Old Maven Central publishing** (OSSRH/Nexus Staging) → **New Maven Central
   publishing** (Central Publisher Portal)

> **IMPORTANT**: The legacy OSSRH service reached end-of-life on **June 30th,
> 2025**. All projects must migrate to the new Central Publisher Portal.

---

## Pre-Migration Checklist

Before starting, ensure you have:

- [ ] Access to the project's source code repository
- [ ] Maven wrapper (`mvnw`) or Maven 3.6+ installed
- [ ] GPG key configured for artifact signing (required for Maven Central)
- [ ] Access to the new Maven Central Portal credentials (username token and
      password token)
- [ ] Registered the `com.ensono` namespace on the
      [Central Portal](https://central.sonatype.org/register/namespace/)
- [ ] Enabled SNAPSHOT publishing for your namespace

---

## Step 1: Update Maven Coordinates (pom.xml)

### 1.1 Update the groupId

Find and replace the `groupId` from `com.amido` to `com.ensono`:

**Before:**

```xml
<groupId>com.amido.stacks.modules</groupId>
```

**After:**

```xml
<groupId>com.ensono.stacks.modules</groupId>
```

### 1.2 Update Project Metadata

Update all project metadata to reflect the new organization:

```xml
<url>https://github.com/Ensono/YOUR-PROJECT-NAME.git</url>

<licenses>
  <license>
    <name>MIT License</name>
    <url>http://www.opensource.org/licenses/mit-license.php</url>
  </license>
</licenses>

<developers>
  <developer>
    <name>Ensono Digital</name>
    <email>stacks@ensono.com</email>
    <organization>Ensono</organization>
    <organizationUrl>http://www.ensono.com</organizationUrl>
  </developer>
</developers>

<scm>
  <connection>scm:git:git@github.com:Ensono/YOUR-PROJECT-NAME.git</connection>
  <developerConnection>scm:git:ssh://github.com:Ensono/YOUR-PROJECT-NAME.git</developerConnection>
  <url>https://github.com/Ensono/YOUR-PROJECT-NAME</url>
</scm>
```

---

## Step 2: Update Java Package Names

### 2.1 Rename Package Directories

> [!NOTE] There may be some variance in your source directory structure. Adjust
> paths as necessary, ensure that all occurrences are updated.

Rename all package directories from `com/amido/` to `com/ensono/`:

```bash
# For main source files
find src/main/java -type d -name "amido" -exec bash -c 'mv "$1" "${1/amido/ensono}"' _ {} \;

# For test source files
find src/test/java -type d -name "amido" -exec bash -c 'mv "$1" "${1/amido/ensono}"' _ {} \;
```

### 2.2 Update Package Declarations in Java Files

Update all package declarations in Java files:

```bash
# Find and replace in all Java files
find . -name "*.java" -exec sed -i 's/package com\.amido/package com.ensono/g' {} \;
find . -name "*.java" -exec sed -i 's/import com\.amido/import com.ensono/g' {} \;
```

### 2.3 Update Configuration Files

Search and replace `com.amido` with `com.ensono` in:

- `application.yml` / `application.properties`
- `application-*.yml` / `application-*.properties`
- Any XML configuration files
- Test configuration files
- Swagger/OpenAPI configuration

```bash
# Find all potential files needing updates
grep -r "com\.amido" --include="*.yml" --include="*.yaml" --include="*.properties" --include="*.xml" .
```

---

## Step 3: Remove Old Maven Central Publishing Configuration

### 3.1 Remove OSSRH/Nexus Staging Plugin

Delete the old `nexus-staging-maven-plugin`:

**Delete this:**

```xml
<plugin>
  <groupId>org.sonatype.plugins</groupId>
  <artifactId>nexus-staging-maven-plugin</artifactId>
  <version>1.6.13</version>
  <extensions>true</extensions>
  <configuration>
    <serverId>ossrh</serverId>
    <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
    <autoReleaseAfterClose>true</autoReleaseAfterClose>
  </configuration>
</plugin>
```

### 3.2 Remove Old Distribution Management

Delete the old `<distributionManagement>` section:

**Delete this:**

```xml
<distributionManagement>
  <snapshotRepository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
  </snapshotRepository>
  <repository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
  </repository>
</distributionManagement>
```

### 3.3 Remove Old Maven Deploy Plugin Configuration (if overridden)

Remove any custom configuration of `maven-deploy-plugin` that was tied to OSSRH.

---

## Step 4: Add New Maven Central Publishing Plugin

### 4.1 Add Plugin Version Property

In the `<properties>` section, add:

```xml
<maven-central-publishing-plugin.version>0.9.0</maven-central-publishing-plugin.version>
```

### 4.2 Add Central Publishing Maven Plugin

In the `<build><plugins>` section, add:

```xml
<plugin>
  <groupId>org.sonatype.central</groupId>
  <artifactId>central-publishing-maven-plugin</artifactId>
  <version>${maven-central-publishing-plugin.version}</version>
  <extensions>true</extensions>
  <configuration>
    <publishingServerId>central</publishingServerId>
    <autoPublish>true</autoPublish>
    <waitUntil>published</waitUntil>
  </configuration>
</plugin>
```

### Configuration Options Explained

- `publishingServerId`: References the server `<id>` used in Maven settings
  (injected by the CI/CD pipeline at build time)
- `autoPublish`: When `true`, automatically publishes after validation
  (recommended for CI/CD)
- `waitUntil`: Can be `uploaded`, `validated`, or `published` - controls how
  long the build waits

---

## Step 5: Ensure Required Plugins Are Present

Maven Central requires source JARs, Javadoc JARs, and GPG signatures. Ensure
these plugins are configured:

### 5.1 Maven Source Plugin

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-source-plugin</artifactId>
  <version>3.4.0</version>
  <executions>
    <execution>
      <id>attach-sources</id>
      <goals>
        <goal>jar-no-fork</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### 5.2 Maven Javadoc Plugin

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-javadoc-plugin</artifactId>
  <version>3.12.0</version>
  <executions>
    <execution>
      <id>attach-javadocs</id>
      <goals>
        <goal>jar</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### 5.3 Maven GPG Plugin

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-gpg-plugin</artifactId>
  <version>3.2.8</version>
  <executions>
    <execution>
      <id>sign-artifacts</id>
      <phase>verify</phase>
      <goals>
        <goal>sign</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

---

## Step 6: Configure CI/CD Pipeline Credentials

> **Note**: The `settings.xml` file is in `.gitignore` as it contains
> credentials that should never be committed. Maven Central credentials are
> injected by the CI/CD pipeline at build time.

### 6.1 Link the `maven-credentials` Variable Group

The Azure DevOps variable group `maven-credentials` contains the required
secrets for Maven Central publishing:

| Variable Name    | Description                                        |
| ---------------- | -------------------------------------------------- |
| `MAVEN_ID`       | Server ID matching `publishingServerId` in pom.xml |
| `MAVEN_USERNAME` | Central Portal token username                      |
| `MAVEN_PASSWORD` | Central Portal token password                      |

Add the variable group to the `variables` section under your build stage:

```yaml
stages:
  - stage: Build
    variables:
      # ... other variable groups ...
      - group: maven-credentials
    jobs:
      # ...
```

### 6.2 Configure Maven Repository Server URLs

Update the `maven_repository_server` variable in Azure DevOps to use the correct
Maven Central Portal URLs:

```yaml
- name: maven_repository_server
  ${{ if eq( variables['Build.SourceBranchName'], 'main' ) }}:
    value: central::https://central.sonatype.com
  ${{ if ne( variables['Build.SourceBranchName'], 'main' ) }}:
    value: central::https://central.sonatype.com/repository/maven-snapshots/
```

> **Important**: The `central-publishing-maven-plugin` handles the API path
> internally. Do NOT include `/api/v1/publisher` or other path suffixes - use
> the base URLs shown above.

### 6.3 Pass Credentials to Deploy Template

The `stacks-pipeline-templates` deploy step accepts these parameters and
generates the Maven `settings.xml` at runtime:

```yaml
- template: azDevOps/azure/templates/steps/java/deploy-java.yml@templates
  parameters:
    # ... other parameters ...
    # MAVEN CENTRAL
    maven_id: "$(MAVEN_ID)"
    maven_username: "$(MAVEN_USERNAME)"
    maven_password: "$(MAVEN_PASSWORD)"
    maven_repository_server: "$(maven_repository_server)"
```

The pipeline template generates a `settings.xml` file during the build, ensuring
credentials are never stored in source control.

---

## Step 7: Update Pipeline Build Type

Change from `SNAPSHOT`/`RELEASE` pattern to simpler versioning:

**Before:**

```yaml
- name: build_type
  ${{ if eq( variables['Build.SourceBranchName'], 'main' ) }}:
    value: "RELEASE"
  ${{ else }}:
    value: "SNAPSHOT"
```

**After:**

```yaml
- name: build_type
  ${{ if eq( variables['Build.SourceBranchName'], 'main' ) }}:
    value: ""
  ${{ else }}:
    value: "-SNAPSHOT"
```

> **Important**: The `-SNAPSHOT` suffix is required for non-release builds. See
> [Step 12: SNAPSHOT Publishing](#step-12-snapshot-publishing) for mandatory
> configuration details.

---

## Step 8: Update Documentation

### 8.1 Update README.md

Update dependency coordinates in documentation:

**Before:**

```xml
<dependency>
    <groupId>com.amido.stacks.modules</groupId>
    <artifactId>your-artifact</artifactId>
    <version>1.0.0</version>
</dependency>
```

**After:**

```xml
<dependency>
    <groupId>com.ensono.stacks.modules</groupId>
    <artifactId>your-artifact</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 8.2 Update Any API Documentation

Update Swagger/OpenAPI base packages and group names.

---

## Step 9: Update License File

If the LICENSE file references the old company name:

**Before:**

```text
Copyright (c) 2024 Amido
```

**After:**

```text
Copyright (c) 2026 Ensono
```

---

## Step 10: Verification Steps

### 10.1 Local Build Verification

Run a local build to ensure everything compiles:

```bash
./mvnw clean install -DskipTests -Dgpg.skip=true
```

### 10.2 Full Test Suite

Run the complete test suite:

```bash
./mvnw clean verify
```

### 10.3 Verify Package Structure

Check that Java packages are correctly renamed:

```bash
find src -name "*.java" -exec grep -l "com\.amido" {} \;
# Should return no results
```

### 10.4 Verify POM References

Check that pom.xml has no old references:

```bash
grep -n "amido" pom.xml
# Should return no results
```

### 10.5 Dry Run Publishing (Optional)

Test the publishing configuration without actually publishing:

```bash
./mvnw deploy -DskipTests -Dcentral.skipPublishing=true
```

---

## Step 11: Complete File Checklist

Ensure all these files have been checked and updated where necessary:

### Required Updates

- [ ] `pom.xml` - groupId, metadata, plugins, `-SNAPSHOT` version suffix
- [ ] All `*.java` files - package declarations and imports
- [ ] Azure DevOps `maven-credentials` variable group - `MAVEN_ID`,
      `MAVEN_USERNAME`, `MAVEN_PASSWORD`
- [ ] `README.md` - dependency coordinates
- [ ] `LICENSE` - copyright holder
- [ ] Central Portal namespace - SNAPSHOT publishing enabled

### Potentially Required Updates

- [ ] `application.yml` / `application.properties`
- [ ] `application-*.yml` / `application-*.properties`
- [ ] Any Swagger/OpenAPI configuration
- [ ] Docker files (if they reference packages)
- [ ] CI/CD pipeline files (`azure-pipelines.yml`, etc.)
- [ ] Azure DevOps variable groups
- [ ] `.github/` configuration files
- [ ] Any shell scripts that reference packages

---

## Step 12: SNAPSHOT Publishing

The Maven Central Portal supports `-SNAPSHOT` publishing and **must be
configured** for pre-release builds. This is required for the CI/CD pipeline to
publish non-release versions.

### 12.1 Enable SNAPSHOTs for Your Namespace

1. Navigate to the
   [Central Portal namespaces page](https://central.sonatype.com/publishing/namespaces)
2. Find your namespace (e.g., `com.ensono`)
3. Click the dropdown menu (three dots) on the right
4. Select "Enable SNAPSHOTs"
5. Confirm the action

A badge will appear on the namespace indicating SNAPSHOTs are enabled.

### 12.2 Plugin Configuration for SNAPSHOTs

The `central-publishing-maven-plugin` (version 0.7.0+) supports SNAPSHOT
publishing automatically. When you deploy with a `-SNAPSHOT` version, it uploads
to the correct location.

> **Note**: No additional plugin configuration is required - the plugin detects
> `-SNAPSHOT` versions automatically.

### 12.3 Alternative: Manual Distribution Management (pom.xml)

If you prefer explicit configuration in `pom.xml` instead of relying on pipeline
variables, add this to your `pom.xml`:

```xml
<distributionManagement>
  <snapshotRepository>
    <id>central</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
  </snapshotRepository>
  <repository>
    <id>central</id>
    <url>https://central.sonatype.com</url>
  </repository>
</distributionManagement>
```

> **Important**: The `central-publishing-maven-plugin` handles the API path
> internally. Use the base URL `https://central.sonatype.com` for releases and
> `https://central.sonatype.com/repository/maven-snapshots/` for snapshots - do
> NOT include `/api/v1/publisher` or other API path suffixes.

### 12.4 Consumer Configuration

Projects consuming your SNAPSHOTs need to add this repository to their
`pom.xml`:

```xml
<repositories>
  <repository>
    <name>Central Portal Snapshots</name>
    <id>central-portal-snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

### 12.5 SNAPSHOT Retention Policy

> **Important**: `-SNAPSHOT` releases are automatically cleaned up after **90
> days** of inactivity. Projects under active development should push new
> versions regularly. SNAPSHOTs are not subject to the same immutability
> guarantees as release versions.

---

## Common Issues and Troubleshooting

### Issue: GPG Signing Fails

**Solution**: Ensure GPG agent is running and key is available:

```bash
gpg --list-secret-keys --keyid-format LONG
gpgconf --launch gpg-agent
```

### Issue: 401 Unauthorized on Deploy

**Solution**: Verify your Central Portal token credentials in the Azure DevOps
`maven-credentials` variable group. Ensure `MAVEN_USERNAME` and `MAVEN_PASSWORD`
contain valid tokens generated from the
[Central Portal](https://central.sonatype.org/publish/generate-portal-token/),
not your regular login credentials.

### Issue: Validation Fails on Maven Central

**Solution**: Ensure all requirements are met:

- Source JAR is attached
- Javadoc JAR is attached
- All files are GPG signed
- POM contains all required metadata (name, description, url, license,
  developers, scm)

### Issue: "Already Published" Error

**Solution**: Maven Central artifacts are immutable. You cannot republish the
same version. Increment your version number.

### Issue: Build Fails Finding Old Packages

**Solution**: Clean your local Maven repository:

```bash
rm -rf ~/.m2/repository/com/amido
./mvnw clean install
```

---

## Reference: Complete pom.xml Changes Summary

```diff
- <groupId>com.amido.stacks.modules</groupId>
+ <groupId>com.ensono.stacks.modules</groupId>

- <url>https://github.com/amido/PROJECT_NAME.git</url>
+ <url>https://github.com/Ensono/PROJECT_NAME.git</url>

  <developers>
    <developer>
-     <name>Amido</name>
-     <email>stacks@amido.com</email>
-     <organization>Amido</organization>
-     <organizationUrl>http://www.amido.com</organizationUrl>
+     <name>Ensono Digital</name>
+     <email>stacks@ensono.com</email>
+     <organization>Ensono</organization>
+     <organizationUrl>http://www.ensono.com</organizationUrl>
    </developer>
  </developers>

  <scm>
-   <connection>scm:git:git@github.com:amido/PROJECT_NAME.git</connection>
-   <developerConnection>scm:git:ssh://github.com:amido/PROJECT_NAME.git</developerConnection>
-   <url>https://github.com/amido/PROJECT_NAME</url>
+   <connection>scm:git:git@github.com:Ensono/PROJECT_NAME.git</connection>
+   <developerConnection>scm:git:ssh://github.com:Ensono/PROJECT_NAME.git</developerConnection>
+   <url>https://github.com/Ensono/PROJECT_NAME</url>
  </scm>

  <!-- Remove old OSSRH plugin -->
- <plugin>
-   <groupId>org.sonatype.plugins</groupId>
-   <artifactId>nexus-staging-maven-plugin</artifactId>
-   ...
- </plugin>

  <!-- Add new Central Publishing plugin -->
+ <plugin>
+   <groupId>org.sonatype.central</groupId>
+   <artifactId>central-publishing-maven-plugin</artifactId>
+   <version>0.9.0</version>
+   <extensions>true</extensions>
+   <configuration>
+     <publishingServerId>central</publishingServerId>
+     <autoPublish>true</autoPublish>
+     <waitUntil>published</waitUntil>
+   </configuration>
+ </plugin>
```

---

## Reference Links

- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [Central Publishing Maven Plugin](https://central.sonatype.org/publish/publish-portal-maven/)
- [SNAPSHOT Publishing](https://central.sonatype.org/publish/publish-portal-snapshots/)
- [Generate Portal Token](https://central.sonatype.org/publish/generate-portal-token/)
- [OSSRH Sunset Announcement](https://central.sonatype.org/news/20250326_ossrh_sunset/)
- [Central Portal](https://central.sonatype.com/)

---

## Post-Migration

After successful migration:

1. **Verify on Maven Central**: Check that your artifacts appear at
   `https://central.sonatype.com/search?q=com.ensono`
2. **Update Downstream Projects**: Any projects depending on your artifacts need
   to update their dependencies to use the new `com.ensono` groupId
3. **Archive Old Artifacts**: The old `com.amido` artifacts will remain
   available on Maven Central but should be marked as deprecated in
   documentation
4. **Communicate Changes**: Notify users of the namespace change through release
   notes, README updates, or migration guides
