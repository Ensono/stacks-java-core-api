# Dependency Management Strategy

## Overview

This project uses Maven for dependency management with a multi-module structure. The majority of dependency versions are inherited from the parent POM (`stacks-modules-parent:3.0.111`), which centralizes version control.

## Dependency Landscape

### Parent POM

- **Artifact**: `com.ensono.stacks.modules:stacks-modules-parent`
- **Version**: 3.0.111
- **Managed Versions**: ~95% of all transitive dependencies
- **Update Frequency**: Quarterly (managed by parent project)

### Local Overrides

The `java/pom.xml` module overrides only one property:

| Property                    | Current | Latest | Status                          |
|-----------------------------|---------|--------|---------------------------------|
| `springdoc.openapi.version` | 2.2.0   | 3.0.1  | MAJOR version upgrade available |

## Available Updates (as of 2026-01-09)

### MAJOR Version Upgrades (Deferred)

These require careful testing and are typically deferred until explicit needs arise:

| Dependency          | Current | Available  | Impact | Notes                                       |
|---------------------|---------|------------|--------|---------------------------------------------|
| Spring Security     | 6.5.7   | 7.0.2      | High   | Requires code refactoring; breaking changes |
| Spring Session      | 3.5.3   | 4.0.x      | High   | Significant API changes                     |
| Spring Web Services | 4.1.2   | 5.0.0      | Medium | Requires dependency tree review             |
| Springdoc OpenAPI   | 2.2.0   | 3.0.1      | Medium | API compatibility check needed              |
| Testcontainers      | 1.21.3  | 2.0.3      | Medium | 50+ modules affected                        |
| Jedis               | 6.0.0   | 7.2.0      | Low    | Redis client upgrade                        |
| Wiremock            | 3.13.2  | 4.0.0-beta | Low    | Pre-release; wait for stable                |

### MINOR Upgrades (Safe)

These introduce new features and non-breaking changes:

| Dependency        | Current | Available | Impact                      |
|-------------------|---------|-----------|-----------------------------|
| Checkstyle Plugin | 12.3.0  | 13.0.0    | Plugin enhancement          |
| XmlUnit           | 2.10.4  | 2.11.0    | Testing utility enhancement |

### PATCH Upgrades (Low Risk)

These are bug fixes and security patches:

| Dependency             | Current | Available | Impact                   |
|------------------------|---------|-----------|--------------------------|
| Testcontainers (Patch) | 1.21.3  | 1.21.4    | Bug fixes, same API      |
| SnakeYAML              | 2.4     | 2.5       | Security/stability fixes |
| Maven Compiler Plugin  | 3.13.0  | 3.14.1    | Build performance        |

## Update Strategy

### Current Policy: Conservative (Patches/Minors Only)

1. **Apply PATCH updates immediately** — These are safe and should be merged regularly
2. **Apply MINOR updates quarterly** — After brief testing in a branch
3. **MAJOR updates on demand** — Only when explicitly required by feature work or security need

### Rationale

- **Stability**: Minimizes breaking changes in production
- **Testing**: Reduces regression risk on each deployment
- **Dependency Hell**: Avoids version conflicts from cascading upgrades
- **Parent POM Control**: Most versioning is centralized; local overrides are minimal

## How to Check for Updates

### List All Available Updates

```bash
cd java
./mvnw versions:display-dependency-updates
./mvnw versions:display-property-updates
```

### Apply Updates (Manual Approach)

Edit `java/pom.xml` to update the `springdoc.openapi.version` property or request parent POM updates via the parent project.

### Apply Updates (Maven Plugin)

```bash
# PATCH updates only
./mvnw versions:use-releases \
  -DallowMajorUpdates=false \
  -DallowMinorUpdates=false \
  -DallowIncrementalUpdates=true

# MINOR + PATCH updates
./mvnw versions:use-releases \
  -DallowMajorUpdates=false \
  -DallowMinorUpdates=true \
  -DallowIncrementalUpdates=true
```

## Verification

After applying updates:

```bash
# Compile with updated dependencies
./mvnw clean compile -DskipTests

# Run tests
./mvnw clean test

# Full verification (includes Checkstyle, JavaDoc, JaCoCo)
./mvnw clean verify -Dgpg.skip

# Or with GPG signing
./mvnw clean verify
```

## Special Cases

### GPG Signing Timeouts

If `mvn verify` times out on GPG signing (YubiKey-backed keys):

```bash
# Skip GPG for verification cycles (safe for development)
./mvnw clean verify -Dgpg.skip

# For CI/CD pipelines: GPG signing is enforced in commits via pre-commit hooks
```

### Parent POM Updates

To update the parent POM version:

1. Check the [stacks-modules-parent](https://github.com/Ensono/stacks-modules-parent) repository
2. Update `<version>3.0.111</version>` in `java/pom.xml` `<parent>` section
3. Run `./mvnw clean verify -Dgpg.skip` to test all transitive dependency changes
4. Commit with message: `chore(deps): bump parent POM to [version]`

## Compliance & Security

### FIPS 140-2 / Cryptographic Standards

All cryptographic libraries are managed by the parent POM and vetted for compliance. Verify compatibility before major upgrades.

### Dependency Scanning

Pre-commit hooks and CI/CD pipelines scan for:

- CVEs via dependency-check or similar tools
- License compliance (Apache 2.0, MIT, etc.)
- Deprecated dependencies

### Audit Trail

All dependency updates are committed with conventional commit messages:

```text
chore(deps): update [library] to [version]
chore(deps): bump parent POM to [version]
fix(deps): security update for [library] CVE-[id]
```

## References

- [Maven Versions Plugin](https://www.mojohaus.org/versions/versions-maven-plugin/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [stacks-modules-parent](https://github.com/Ensono/stacks-modules-parent)
- [Dependency Management Guide](./copilot-instructions.md)
