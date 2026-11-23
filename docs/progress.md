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

## Storefront design

The mobile browsing layout draws inspiration from the [Drinkable coffee shop concept](https://dribbble.com/shots/23149677-Drinkable-Coffee-Shop-App-Design). BrewGo uses burgundy and cream, a compact mobile navigation bar, and live product cards across its home, menu, and product pages.

The updated [WebP photography](../front-end/src/assets/images/) was made with built-in ImageGen. The main iced latte image has desktop and mobile sizes. Five product portraits and a café image fallback share the same lighting and color treatment.

<details>
<summary>Image generation prompts</summary>

**Hero.** Original BrewGo specialty coffee photography, editorial commercial website hero, horizontal 3:2. A clear short rounded ribbed glass of iced latte with crisp ice and beautiful rich coffee-and-milk marbling on a dark mahogany walnut cafe counter, the glass and beverage dominate the right-center of the full frame. Refined direct afternoon window light hits the glass from upper right, soft atmospheric dark burgundy-brown coffee shop shadows and a softly blurred warm café background. Strong espresso brown and toasted amber highlights, subtle film grain, clean photographic composition, precise glass detail. Leave a little shadowy breathing room along the left edge for blending into a dark burgundy section background. No people, no branding, no lettering, no graphics, no watermark.

**Menu photo sheet.** Single editorial photography contact sheet for six original coffee shop menu assets, a strictly aligned 3-column by 2-row grid of six equally sized square panels with edge-to-edge tiles, no gutters, no dividing strokes and no text. Each tile is an independent photorealistic ecommerce photograph with the object centered and completely inside its own tile, filling 73 percent of height, a soft shadow, warm light and realistic materials. Row 1 left: clear ribbed iced latte glass with ice and marbled milk on pale beige; row 1 middle: ivory ceramic cappuccino cup on a small saucer with centered rosetta art, mostly top-down on pale warm taupe; row 1 right: short dark espresso-colored ceramic Americano cup with deep black coffee and warm shadow on light biscuit beige; row 2 left: clear tumbler of a layered vivid green iced matcha latte centered on muted sage pale gray; row 2 middle: one beautiful golden flaky butter croissant on warm oatmeal parchment with no dish; row 2 right: centered plain oval cream ceramic espresso cup and saucer in softly lit light beige. Polished independent packshots, catalog consistency, natural soft side lighting, slightly elevated front camera angle except cappuccino. No labels, no graphic design, no lettering, no watermarks. Exact 3-column, 2-row layout, subjects need safe clear margins inside every cell.

**Hot matcha latte.** Square original commercial coffee shop catalog photograph, a single cream ceramic cup and saucer of hot matcha latte with vivid muted sage green steamed milk foam and a small creamy white rosetta, slightly elevated top down perspective, centered fully visible cup with breathing space on a matte light muted pale sage gray tabletop, gentle natural side lighting from upper left and polished soft shadow. Warm editorial color grade, simple background, realistic, product filling 74 percent of image. No ice, no glass, no extra garnish, no text, no logos, no watermarks.

</details>

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
| AT-15 | Partial | Browser checks verified home, menu, and product details at 320, 390, 670, 768, and 1440 px. Full mobile checkout remains pending. |
| AT-16 | Pending | Add SSE, snapshot refresh, and polling fallback. |
| AT-17 | Partial | Admin API can disable products and the public catalog filters them out. Admin screens and checkout validation remain pending. |
| AT-18 | Pending | Add reporting based on verified payments. |
