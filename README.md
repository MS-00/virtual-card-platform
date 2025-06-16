# Virtual Card Issuance Platform

## Overview

This application is a backend platform for issuing and managing virtual cards. Users can create cards, top up balances, spend from cards, block/unblock cards, and view transaction history. The platform enforces card status (ACTIVE/BLOCKED), rate limits on spending, and provides a simple API tester UI for easy interaction.

## How It Works

- Cards and transactions are managed in-memory (no persistent DB by default).
- Each card has a status (ACTIVE or BLOCKED); only ACTIVE cards can spend.
- All operations are thread-safe using per-card locks.
- Rate limiting is enforced per card (configurable).
- The initial balance is recorded as a transaction of type `INITIAL`.
- All monetary amounts are rounded to 2 decimal places when returned, but there are no controls on the number of decimals in the input.
- The API tester page allows you to create, block/unblock, spend, top up, and view cards and transactions.
- Clicking a card ID in the tester auto-fills all relevant fields for convenience.

## API Tester

Open the tester page in your browser:

[API Tester](http://localhost:8080/api-tester.html)

- After creating a card, click its ID in the cards list to auto-fill all other fields.
- Use the Block/Unblock buttons to change card status.

## Building and Running with Docker

1. **Build the Docker image:**

    ```sh
    docker build -t virtual-card-platform .
    ```

2. **Run the app:**

    ```sh
    docker run -p 8080:8080 virtual-card-platform
    ```

3. **Access the API tester:**

    Open [http://localhost:8080/api-tester.html](http://localhost:8080/api-tester.html)

## Improvements & Limitations

- **Currency:** No currency information is stored or validated; all amounts are assumed to be in a single, unspecified currency.
- **Decimals:** There are no controls on the number of decimals for input amounts; however, all amounts are rounded to 2 decimal places when returned.
- **Persistence:** The current setup uses in-memory storage for cards and locks, which is not suitable for production or scaling. Using a real database (e.g., PostgreSQL, MySQL) is recommended for persistence and scalability.
- **Distributed Locking:** For multi-instance deployments, distributed locking (e.g., Redis) should be used instead of in-memory locks.
- **Validation:** Additional validation (e.g., for cardholder names, amount ranges, or currency) could be added.
- **Security:** No authentication or authorization is implemented; this should be added for real-world use.
