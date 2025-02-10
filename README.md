# Wallets Service
It includes two endpoints for these operations:
- Get a wallet using its identifier.
- Top-up money in that wallet using a credit card number. It charges that amount internally using the third-party platform Stripe.

The are two kind of tests:
- Unit tests with the name pattern `*Test.java`
- Integration tests with the name pattern `*IT.java`

`jacoco` plugin has been added to generate a coverage report considering unit and integration tests. After running
`mvn verify`, an `index.html` file is generated under `target/jacoco-report`.
The report shows that current coverage is 90%.