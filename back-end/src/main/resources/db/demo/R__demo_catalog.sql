INSERT IGNORE INTO branches (name, slug, timezone)
VALUES ('BrewGo Coffee Bar', 'coffee-bar', 'Asia/Phnom_Penh');

INSERT IGNORE INTO branch_hours (branch_id, day_of_week, opens_at, closes_at)
SELECT id, weekdays.day_of_week, '08:00:00', '18:00:00'
FROM branches
CROSS JOIN (
    SELECT 1 AS day_of_week UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7
) AS weekdays
WHERE slug = 'coffee-bar';

INSERT IGNORE INTO categories (name, slug, sort_order)
VALUES ('Coffee', 'coffee', 10), ('Tea', 'tea', 20), ('Bakery', 'bakery', 30);

INSERT IGNORE INTO products (category_id, name, slug, description, allergens, base_price)
SELECT id, 'Iced Latte', 'iced-latte', 'Espresso, chilled milk, and ice.', 'Milk', 2.50
FROM categories WHERE slug = 'coffee'
UNION ALL
SELECT id, 'Cappuccino', 'cappuccino', 'Espresso and silky steamed milk.', 'Milk', 2.75
FROM categories WHERE slug = 'coffee'
UNION ALL
SELECT id, 'Americano', 'americano', 'Espresso lengthened with water.', NULL, 2.00
FROM categories WHERE slug = 'coffee'
UNION ALL
SELECT id, 'Matcha Latte', 'matcha-latte', 'Whisked matcha with steamed milk.', 'Milk', 3.25
FROM categories WHERE slug = 'tea'
UNION ALL
SELECT id, 'Butter Croissant', 'butter-croissant', 'A flaky, golden pastry.', 'Wheat, milk', 2.25
FROM categories WHERE slug = 'bakery';
