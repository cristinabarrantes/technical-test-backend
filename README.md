# Wallets Service
It includes two endpoints for these operations:
- Get a wallet using its identifier.
- Top-up money in that wallet using a credit card number. It has to charge that amount internally using a third-party platform.

The are two kind of tests:
- Unit tests with the name pattern `*Test.java`
- Integration tests with the name pattern `*IT.java`

It has been added `jacoco` plugin to generate a coverage report. After running
`mvn verify`, an `index.html` file is generated under `target/jacoco-report`.
In the report it can be seen that current coverage is 90%.