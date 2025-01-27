# Bank Account Web Service

This is a simple micro web service that simulates a "Bank Account" using Spring Boot. The service allows users to query their balance, deposit money, and withdraw funds. The service enforces specific restrictions on transactions and amounts.

## Technologies Used

- **Java** (Spring Boot)
- **H2 In-Memory Database** for storage and testing
- **Gradle** for build automation
- **JUnit** & **Mockito** for testing
- **Swagger** for API documentation and testing

## Features

- **Balance**: Returns the current balance of the account.
- **Deposit**: Credits the account with the specified amount.
  - Max deposit per day: $150K
  - Max deposit per transaction: $40K
  - Max deposit frequency: 4 transactions per day
- **Withdrawal**: Deducts the specified amount from the account.
  - Max withdrawal per day: $50K
  - Max withdrawal per transaction: $20K
  - Max withdrawal frequency: 3 transactions per day
  - Withdrawals are only allowed if the balance is greater than or equal to the requested withdrawal amount.

## API Endpoints

### 1. Get Balance
- **Endpoint**: `GET /api/account/balance`
- **Description**: Returns the current balance of the account.
- **Response**:
  ```json
  {
    "balance": 1000.0
  }
  ```
### 2. Deposit Money
- **Endpoint**: `POST /api/account/deposit`
- **Description**: Credits the account with the specified amount.
- **Request Body**:
  ```json
  {
    "amount": 5000.0
  }
  ```
- **Response**:
   ```json
  {
  "message": "Deposit successful",
  "newBalance": 6000.0
  }
  ```
### 3. Withdraw Money
- **Endpoint**: `POST /api/account/withdraw`
- **Description**: Deducts the specified amount from the account.
- **Request Body**:
  ```json
  {
  "amount": 2000.0
  }
  ```
- **Response**:
   ```json
  {
  "message": "Withdrawal successful",
  "newBalance": 4000.0
  }
  ```
## Error Handling

The service handles all error cases appropriately, returning proper HTTP status codes and error messages. For example:

- If a withdrawal attempt exceeds $20K, the error message will be: `"Exceeded Maximum Withdrawal Per Transaction"`.
- If a deposit attempt exceeds $40K, the error message will be: `"Exceeded Maximum Deposit Per Transaction"`.
- If the daily deposit limit ($150K) is exceeded, the error message will be: `"Exceeded Maximum Deposit Per Day"`.
- If the daily withdrawal limit ($50K) is exceeded, the error message will be: `"Exceeded Maximum Withdrawal Per Day"`.
- If the deposit frequency limit (4 transactions per day) is exceeded, the error message will be: `"Exceeded Maximum Deposit Frequency"`.
- If the withdrawal frequency limit (3 transactions per day) is exceeded, the error message will be: `"Exceeded Maximum Withdrawal Frequency"`.
- If a withdrawal is attempted with insufficient balance, the error message will be: `"Insufficient Balance"`.

All errors return an appropriate HTTP status code (e.g., `400 Bad Request` or `403 Forbidden`) along with a descriptive error message in the response body.

## Setup and Running the Application

### Prerequisites

- Java 11 or higher
- Gradle

### Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/bank-account-web-service.git
   cd bank-account-web-service
   ```
2. **Build the project**:

 To build the project, run the following command:
  ```bash
   ./gradlew build
  ```
  This command will compile the code, run all tests, and package the application into an executable JAR file.

3. **Run the application**:

 To start the application, use the following command:
  ```bash
  ./gradlew bootRun
  ```
 The application will start and be accessible at http://localhost:8080

### Access the API

Once the application is running, you can interact with the API using the following methods:

1. **Swagger UI**:
   - Open your browser and navigate to `http://localhost:8080/swagger-ui.html`.
   - This provides a user-friendly interface to test all the endpoints.

2. **Using `curl` or Postman**:
   - You can also use tools like `curl` or Postman to send requests to the API endpoints.

## Testing

The project includes unit tests and integration tests written using **JUnit** and **Mockito**. To run the tests, use the following command:

```bash
./gradlew test
```
This will execute all the tests and generate a test report.

## Test Coverage

The test coverage is comprehensive at above 90%, ensuring that all major functionalities and edge cases are tested. 
