# Release Procedure

This document describes the step-by-step process for creating a release of the accounting application.

## Release Naming Convention

We use calendar versioning (CalVer) with the format: `YYYY.MM-RELEASE`

Examples:
- `2025.11-RELEASE`
- `2025.12-RELEASE`
- `2026.01-RELEASE`

## Pre-Release Checklist

- [ ] All tests pass: `./mvnw clean test`
- [ ] Code is committed and pushed to main branch
- [ ] No uncommitted changes in working directory
- [ ] Documentation is up to date
- [ ] CHANGELOG is updated with release notes

## Release Steps

### 1. Update Version in pom.xml

Edit `pom.xml` and change the version from `0.0.1-SNAPSHOT` to the release version:

```xml
<groupId>com.artivisi</groupId>
<artifactId>accounting-finance</artifactId>
<version>2025.11-RELEASE</version>
```

**Important:** This ensures the JAR artifact will have the correct version in its filename:
- `accounting-finance-2025.11-RELEASE.jar`

### 2. Build the Release Artifact

```bash
./mvnw clean package -DskipTests
```

Verify the artifact name in `target/`:
```bash
ls -lh target/accounting-finance-*.jar
```

You should see:
- `accounting-finance-2025.11-RELEASE.jar` (executable JAR)
- `accounting-finance-2025.11-RELEASE.jar.original` (before repackaging)

### 3. Test the Release Build

```bash
java -jar target/accounting-finance-2025.11-RELEASE.jar
```

Verify:
- Application starts successfully
- All endpoints are accessible
- No errors in logs

### 4. Commit Version Change

```bash
git add pom.xml
git commit -m "Release version 2025.11-RELEASE"
```

### 5. Create Git Tag

```bash
git tag -a 2025.11-RELEASE -m "Release version 2025.11-RELEASE"
```

### 6. Push Changes and Tag

```bash
git push origin main
git push origin 2025.11-RELEASE
```

### 7. Create GitHub Release (Optional)

1. Go to: https://github.com/artivisi/aplikasi-akunting/releases/new
2. Select tag: `2025.11-RELEASE`
3. Release title: `Release 2025.11`
4. Add release notes (from CHANGELOG)
5. Attach the JAR file: `target/accounting-finance-2025.11-RELEASE.jar`
6. Click "Publish release"

### 8. Deploy to Production

```bash
cd deploy/ansible

# Upload the release JAR
scp ../../target/accounting-finance-2025.11-RELEASE.jar \
    root@akunting.artivisi.id:/opt/aplikasi-akunting/app.jar

# Restart the service
ssh root@akunting.artivisi.id "systemctl restart aplikasi-akunting"

# Verify deployment
ssh root@akunting.artivisi.id "systemctl status aplikasi-akunting"
```

Or use the deployment playbook if available:
```bash
ansible-playbook deploy.yml
```

### 9. Verify Production Deployment

1. Check application is running: `https://akunting.artivisi.id`
2. Login with admin credentials
3. Verify critical functionality
4. Check application logs: `ssh root@akunting.artivisi.id "tail -f /var/log/aplikasi-akunting/app.log"`

### 10. Prepare for Next Development Iteration

Update `pom.xml` version to next SNAPSHOT:

```xml
<version>2025.12-SNAPSHOT</version>
```

```bash
git add pom.xml
git commit -m "Prepare for next development iteration: 2025.12-SNAPSHOT"
git push origin main
```

## Post-Release Checklist

- [ ] Git tag created and pushed
- [ ] GitHub release published (if applicable)
- [ ] Production deployment successful
- [ ] Application accessible and functional
- [ ] Version updated to next SNAPSHOT
- [ ] Stakeholders notified of release

## Rollback Procedure

If issues are discovered after deployment:

1. **Quick rollback** - deploy previous version:
   ```bash
   scp target/accounting-finance-2025.10-RELEASE.jar \
       root@akunting.artivisi.id:/opt/aplikasi-akunting/app.jar
   ssh root@akunting.artivisi.id "systemctl restart aplikasi-akunting"
   ```

2. **Database rollback** - restore from backup:
   ```bash
   cd deploy/ansible
   ansible-playbook restore.yml
   ```

3. **Notify team** - document the issue and rollback reason

## Artifact Naming Consistency

To ensure artifact names match the release version across all systems:

### pom.xml
```xml
<version>2025.11-RELEASE</version>
```
↓ produces ↓

### JAR file
```
target/accounting-finance-2025.11-RELEASE.jar
```

### Systemd service (on server)
```
/opt/aplikasi-akunting/app.jar
```
(This is a symlink or copy, filename doesn't change)

### Git tag
```
2025.11-RELEASE
```

**Critical:** Always update `pom.xml` version BEFORE building the release artifact. The version in `pom.xml` determines the JAR filename.

## Troubleshooting

### Wrong JAR filename
If you get `accounting-finance-0.0.1-SNAPSHOT.jar`:
- You forgot to update `pom.xml` before building
- Solution: Update `pom.xml`, rebuild with `./mvnw clean package`

### Maven build fails
- Check Java version: `java -version` (must be Java 25)
- Clean build: `./mvnw clean`
- Check for dependency issues: `./mvnw dependency:tree`

### Deployment fails
- Verify SSH access: `ssh root@akunting.artivisi.id`
- Check disk space: `df -h`
- Check service status: `systemctl status aplikasi-akunting`
- Review logs: `journalctl -u aplikasi-akunting -n 100`

## Version History

Example version history:

| Version | Release Date | Git Tag | Notes |
|---------|-------------|---------|-------|
| 2025.11-RELEASE | 2025-11-29 | 2025.11-RELEASE | Initial release |
| 2025.12-RELEASE | 2025-12-15 | 2025.12-RELEASE | Tax compliance features |
| 2026.01-RELEASE | 2026-01-20 | 2026.01-RELEASE | Payroll module |

## References

- [Deployment Guide](deployment-guide.md)
- [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
- [Semantic Versioning](https://semver.org/)
- [Calendar Versioning](https://calver.org/)
