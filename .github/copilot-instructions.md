# AI Agent Instructions for DVJA (Damn Vulnerable Java Application)

## Project Overview
DVJA is a deliberately vulnerable Java web application built with:
- Struts 2.3.30 for MVC framework
- Spring 3.0.5 for dependency injection
- Hibernate 3.3.1 for ORM
- MySQL for database
- Maven for build management

## Key Architecture Points
- Web application uses Struts2 action-based MVC pattern (`src/main/java/com/appsecco/dvja/` contains actions)
- Views are JSP files in `src/main/webapp/WEB-INF/dvja/`
- Database configuration in `src/main/webapp/WEB-INF/config.properties`
- Spring context configuration in `src/main/webapp/WEB-INF/applicationContext.xml`
- Struts configuration in `src/main/resources/struts.xml`

## Development Workflow
1. **Local Setup**:
   ```bash
   # Either use Docker (recommended)
   docker-compose up
   
   # Or manual setup
   mysql -u USER -pPASSWORD dvja < ./db/schema.sql
   mvn clean package
   mvn jetty:run
   ```

2. **Key URLs**:
   - Development server: `http://localhost:8080`
   - Application endpoints defined in `.jsp` files under `webapp/WEB-INF/dvja/`

## Common Patterns
1. **Action Classes**:
   - Extend `com.appsecco.dvja.base.BaseAction`
   - Use `@Action` annotations for routing
   - Follow naming convention: `*Action.java`

2. **Database Access**:
   - Use Hibernate entity annotations
   - Models located in `src/main/java/com/appsecco/dvja/models/`
   - Database operations through Hibernate session management

3. **View Templates**:
   - JSP files with `.jsp` extension
   - Located in `webapp/WEB-INF/dvja/`
   - Use common layouts from `webapp/WEB-INF/dvja/common/`

## Testing & Debugging
- Run tests with: `mvn test`
- Debug logs configured in `src/main/resources/log4j2.xml`
- Use Jetty debug port 8999 (configured in pom.xml)

## Key Files for Reference
- `pom.xml` - Project dependencies and build configuration
- `src/main/resources/struts.xml` - Action mappings and interceptors
- `src/main/webapp/WEB-INF/applicationContext.xml` - Spring bean definitions
- `src/main/webapp/WEB-INF/config.properties` - Database configuration

## Common Gotchas
1. Ensure MySQL is running before starting application
2. Java 1.7+ required (configured in pom.xml)
3. Watch for Hibernate session management in actions
4. Check Struts2 interceptor stack for request processing issues

## Known vulnerable areas (brief)
- JPQL injection (unsafe string concatenation):
   - `src/main/java/com/appsecco/dvja/services/ProductService.java` — `findContainingName(String)` originally concatenated search input into JPQL.
   - `src/main/java/com/appsecco/dvja/services/UserService.java` — `findByLoginUnsafe(String)` originally concatenated login into query; prefer `findByLogin` or parameterized queries.
- Cross-site scripting (XSS):
   - `src/main/webapp/WEB-INF/dvja/ProductList.jsp` previously used `<s:property ... escape="false"/>` for product names. Prefer `escape="true"` or server-side sanitization.
- Command injection:
   - `src/main/java/com/appsecco/dvja/controllers/PingAction.java` executes a shell command built from user input; validate input and avoid shell expansion.
- Open redirect:
   - `src/main/java/com/appsecco/dvja/controllers/RedirectAction.java` + `src/main/resources/struts.xml` accept a raw `url` for redirects; validate against allowed hosts/paths.
- Weak password hashing & predictable reset token:
   - `src/main/java/com/appsecco/dvja/services/UserService.java` uses MD5 for password hashing and reset token generation; use stronger hashing (bcrypt/PBKDF2/Argon2) and random, time-limited tokens.

These items are intentional for DVJA (training). When making fixes in this repo prefer small, reversible PRs that parameterize queries and enable escaping before moving to stronger cryptography or architectural changes.