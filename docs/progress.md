# BrewGo implementation progress

## Sprint 1

- The application uses `front-end/` and `back-end/`. The Java API uses `com.example.back_end` with `Controller`, `Model`, `Repository`, and `service` packages. Each service interface and its implementation are sibling files.
- Added a responsive React and Tailwind shell, live read-only menu, and staff sign-in.
- Added Flyway migrations for the café, catalog, orders, payments, and status history. A separate demo profile loads sample data.
- Added staff session authentication, CSRF protection, and initial role guards. Staff workspaces currently show the signed-in user.
- Added local Compose setup, an OpenAPI contract for the implemented endpoints, and CI checks for the frontend and backend.

## Sprint 1 verification

- Prettier, ESLint, TypeScript, and the Vite production build passed.
- Three backend integration tests passed against a fresh MySQL 8.4 database. The tests covered Flyway migrations, catalog queries, CSRF-protected staff sessions, and barista role restrictions.
- A 360 px Chromium browser check passed for the landing page, live menu and category filters, and admin sign-in and sign-out. The app ran locally with Vite and Spring Boot. Docker Compose still needs a container run.

The landing photograph was made with built-in ImageGen and saved as `front-end/src/assets/cafe-hero.webp` and `front-end/src/assets/cafe-hero-small.webp`.

> Photorealistic editorial café photograph for a premium coffee shop website hero. Wide 3:2 composition, closeup at a low three-quarter angle. One cappuccino with beautiful simple rosetta latte art in a tactile cream ceramic cup and saucer, foreground on a warm walnut café counter, softly blurred subtle coffee shop beyond. Natural window light from right with warm shadows, quiet caramel, cream and espresso colors, filmic real texture and refined composition. Cup occupies center and lower right third with visual breathing room. No people, no letters, no text, no watermarks, no logos.

## Decisions

- BrewGo is a self-practice demo. The demo data uses one branch, the Asia/Phnom_Penh timezone, and USD example prices.
- A valid pay-on-pickup order will start as `CONFIRMED` with payment `PENDING`. The barista will see the order and its requested pickup time immediately. Checkout and the barista queue are future milestones.
- The planned handoff rule requires a cashier to verify payment before an order is completed.
- The public catalog is shared across the demo. An optional branch filter validates that the branch is active.
- API routes use the unversioned `/api` prefix.
- Administrators can create, list, update, and disable products through the API. Updates to price or currency are recorded with the administrator who made the change.

## Acceptance checklist

| ID | Status | Evidence or remaining work |
| --- | --- | --- |
| AT-01 | Pending | Resolve a valid table QR token and display the table context. |
| AT-02 | Pending | Reject unknown and disabled table tokens at order placement. |
| AT-03 | Partial | Menu cards open DB-backed product details. Customization and cart totals remain pending. |
| AT-04 | Partial | Migrations constrain option mappings. Checkout option validation remains pending. |
| AT-05 | Pending | Detect price changes and request confirmation before placing the order. |
| AT-06 | Partial | Order, item snapshot, and payment tables exist. The checkout transaction remains pending. |
| AT-07 | Partial | Idempotency keys have a scoped uniqueness constraint. Checkout replay and tracking-link recovery remain pending. |
| AT-08 | Partial | Branch hours and pickup lead time are represented in the schema. Slot validation remains pending. |
| AT-09 | Pending | Confirm payment, start a pickup automatically, and deliver orders to the barista queue. |
| AT-10 | Partial | The API integration test signs in a barista and verifies admin access is denied. Payment authorization remains pending. |
| AT-11 | Pending | Enforce staff branch scope in order operations. |
| AT-12 | Pending | Require a valid private token on guest tracking endpoints. |
| AT-13 | Pending | Enforce transitions and commit status history in the same transaction. |
| AT-14 | Pending | Implement cancellation with reasons and an immutable terminal state. |
| AT-15 | Partial | Browser checks verified the landing page and menu at 360 px. Full mobile checkout remains pending. |
| AT-16 | Pending | Add SSE, snapshot refresh, and polling fallback. |
| AT-17 | Partial | Admin API can disable products and the public catalog filters them out. Admin screens and checkout validation remain pending. |
| AT-18 | Pending | Add reporting based on verified payments. |
