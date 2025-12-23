# Semgrep Custom Security Rules

This directory contains custom Semgrep rules for detecting security vulnerabilities specific to Spring Boot and Spring Security patterns.

## Rules Overview

### 1. spring-security-auth-bypass.yml
**Purpose:** Detect authentication and authorization bypass vulnerabilities in Spring Security configuration.

**Rules:**
- `spring-security-permitall-on-sensitive-endpoint` - Detects `permitAll()` on sensitive endpoints like admin, payroll, transactions
- `spring-security-missing-preauthorize` - Detects POST/PUT/DELETE controller methods lacking `@PreAuthorize` annotation
- `spring-security-csrf-disabled` - Detects disabled CSRF protection
- `spring-security-cors-allow-all-origins` - Detects CORS allowing all origins (*)
- `spring-security-weak-password-encoder` - Detects MD5, SHA-1, NoOp password encoders
- `spring-security-hardcoded-credentials` - Detects hardcoded passwords in security config

### 2. sql-injection.yml
**Purpose:** Detect SQL injection vulnerabilities in JPA, Hibernate, and JDBC code.

**Rules:**
- `jpa-native-query-injection` - Detects string concatenation in native queries
- `jdbc-template-concatenation` - Detects string concatenation in JdbcTemplate queries
- `unsafe-table-name-in-query` - Detects dynamic table names without whitelist validation
- `hibernate-setstring-with-concat` - Detects concatenation before setString() call

### 3. xss-prevention.yml
**Purpose:** Detect Cross-Site Scripting (XSS) vulnerabilities in templates and controllers.

**Rules:**
- `thymeleaf-unescape-text` - Detects unescaped HTML rendering with `th:utext` or `[(${...})]`
- `direct-html-writing-to-response` - Detects writing to HttpServletResponse without HTML encoding
- `javascript-innerhtml-assignment` - Detects DOM-based XSS via innerHTML assignment
- `missing-html-escape-in-model` - Detects model attributes without HTML escaping

## Running Semgrep

### Locally
```bash
# Install Semgrep
pip install semgrep

# Run all custom rules
semgrep --config .semgrep/ src/

# Run specific ruleset
semgrep --config .semgrep/spring-security-auth-bypass.yml src/

# Auto-fix (where available)
semgrep --config .semgrep/ --autofix src/
```

### CI/CD
Semgrep runs automatically in `.github/workflows/codeql.yml`:
- On every push to main
- On every pull request
- Weekly scheduled scan

Results are uploaded to GitHub Security tab as SARIF.

## Adding New Rules

1. Create a new YAML file in `.semgrep/`
2. Follow the Semgrep rule syntax: https://semgrep.dev/docs/writing-rules/rule-syntax/
3. Test locally: `semgrep --config .semgrep/your-rule.yml --test`
4. Commit and push - CI will pick it up automatically

## Rule Severity Levels

- **ERROR:** Critical security issues that must be fixed (SQL injection, XSS, hardcoded credentials)
- **WARNING:** Important security issues that should be reviewed (missing authorization, CSRF)
- **INFO:** Informational findings for best practices

## False Positives

If a rule produces false positives:
1. Use inline comments to suppress: `// nosemgrep: rule-id`
2. Or add to `.semgrep.yml` allowlist
3. Document why the suppression is safe

## References

- Semgrep Documentation: https://semgrep.dev/docs/
- Spring Security Best Practices: https://spring.io/guides/topicals/spring-security-architecture
- OWASP Top 10: https://owasp.org/www-project-top-ten/
