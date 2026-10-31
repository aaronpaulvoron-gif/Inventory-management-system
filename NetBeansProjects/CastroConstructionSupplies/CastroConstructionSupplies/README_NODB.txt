NO-DATABASE VERSION: all data lives in memory while the program runs.
Built-in admin login: admin / admin123 (login screen: choose Admin or Customer).

WHAT'S NEW
- Staff is removed from the menu, login and dashboard (the dashboard card is now PENDING ORDERS).
  StaffPanel / StaffDAO / Staff files are still in the project but nothing uses them.
- Products have a Purchase price (cost) and a Selling price. Customers only see the selling price.
- Products screen: filter by Category; the list is grouped by category.
- Inventory: "Needs restock" cards with stock bars, STOCK-IN / ADD BATCH, VIEW BATCHES.
  Every delivery is a batch (batch no., date, quantity, purchase price). Sales use the oldest batch first.
- No payment feature.
- Customers: CANCEL ORDER pop-up in the cart (with confirmation) and on a still-pending order in MY ORDERS
  (asks for a reason; staff see it in the order details).
- Customer forgot password / staff reset customer password (Customers screen).
- The admin cancels a pending order with a reason (Order Queue > Cancel). Sales History has a Cancel Reason column
  ("Staff: Out of stock", "Customer: Changed my mind"). The customer gets an "order cancelled + why" pop-up the
  next time they open the app. In this no-database version that only works while the program stays running.
Original versions of replaced files are in legacy_forms and legacy_forms_v2.
