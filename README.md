# BrewGo

BrewGo is a café ordering demo. The first milestone provides a browsable menu, staff sign-in, and a MySQL schema for the upcoming ordering workflow.

## Run locally

With Docker Compose installed, copy the example environment file and set a demo admin password in `.env`.

```sh
cp .env.example .env
docker compose up --build
```

Open [BrewGo](http://localhost:5173) to browse the menu. Sign in at [Staff](http://localhost:5173/staff/login) using the demo admin email and password from `.env`. The demo profile seeds one branch and its catalog on startup. It creates the admin account if that email is new. Editing the environment file after the account has been created leaves the stored password in place.

### Develop outside containers

Use Java 25, Node 24, and MySQL 8.4. Copy `.env.example` to `.env` in the repository root if the file is missing. Set `MYSQL_DATABASE`, `MYSQL_USER`, and `MYSQL_PASSWORD` to match your local MySQL instance. The default database is `brewgo_db` on port 3306. It must exist and the MySQL user needs permission to run Flyway migrations. Spring Boot reads this file automatically when started from `back-end/`.

Use `BREWGO_PROFILE=demo` to load the sample catalog and `SESSION_COOKIE_SECURE=false` for local HTTP. The demo staff account is optional and requires both `BREWGO_DEMO_ADMIN_EMAIL` and `BREWGO_DEMO_ADMIN_PASSWORD`. For CI or a separate database, set Spring's `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` environment variables to override the local connection.

```sh
cd back-end
./mvnw spring-boot:run
```

In another terminal:

```sh
cd front-end
npm ci
npm run dev
```

Vite forwards `/api` to `http://localhost:8080`. Set `BREWGO_API_ORIGIN` in the frontend process if the backend uses a different origin.

Try `GET /api/v1/products?q=latte` on port 5173 to search the demo catalog through the API.

## Structure

| Path | Responsibility |
| --- | --- |
| `front-end/src/app` | Routing and shared page layout |
| `front-end/src/features` | Guest catalog and staff session screens |
| `front-end/src/components/ui`, `src/lib/api`, `src/styles` | Shared UI, API client, and Tailwind theme |
| `back-end/src/main/java/com/example/back_end` | Spring application with `Controller`, `Model`, and `Repository` packages |
| `back-end/src/main/java/com/example/back_end/service` | One interface file and one implementation file per service |
| `back-end/src/main/resources/db` | Flyway schema and demo-only data |
| `docs` | [API contract](docs/openapi.yaml) and [decisions and acceptance checklist](docs/progress.md) |

## Checks

Run `npm run format:check`, `npm run lint`, `npm run typecheck`, and `npm run build` from `front-end/`. Run `./mvnw verify` from `back-end/` with the datasource variables pointing to a dedicated empty MySQL 8.4 test database. The integration suite applies Flyway migrations and creates test data in that database.
