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

### 1. Create Account
- **Endpoint**: `POST /api/account`
- **Description**: Creates a new Account.
- **Request Body**:
  ```json
  {
  "name": "John Doe",
  "accountNumber": "00045678912"
  }
  ```
- **Response**:
   ```json
  {
  "code": 201,
  "message": "Account created successfully",
  "data": {
    "createdAt": "2025-01-27T17:43:45.6521029",
    "updatedAt": "2025-01-27T17:43:45.6521029",
    "id": 1,
    "name": "John Doe",
    "accountNumber": "00045678912",
    "balance": 0
    }
  }
  ```
  
### 1. Get Balance
- **Endpoint**: `GET /api/account/balance/{accountNumber}`
- **Description**: Returns the current balance of the account.
- **Response**:
  ```json
  {
  "code": 200,
  "message": "Balance fetched successfully",
  "data": {
    "balance": 0
   }
  }
  ```
### 2. Deposit Money
- **Endpoint**: `POST /api/account/deposit`
- **Description**: Credits the account with the specified amount.
- **Request Body**:
  ```json
  {
  "accountNumber": "00045678912",
  "amount": 15000
  }
  ```
- **Response**:
   ```json
  {
  "code": 200,
  "message": "Deposit successful",
  "data": {
    "createdAt": "2025-01-27T18:01:44.7467041",
    "updatedAt": "2025-01-27T18:01:44.7467041",
    "id": 1,
    "amount": 15000,
    "type": "DEPOSIT",
    "account": {
      "createdAt": "2025-01-27T17:43:45.652103",
      "updatedAt": "2025-01-27T18:01:44.8099435",
      "id": 1,
      "name": "John Doe",
      "accountNumber": "00045678912",
      "balance": 15000
    }
   }
  }
  ```
### 3. Withdraw Money
- **Endpoint**: `POST /api/account/withdraw`
- **Description**: Deducts the specified amount from the account.
- **Request Body**:
  ```json
  {
  "accountNumber": "00045678912",
  "amount": 5000
  }
  ```
- **Response**:
   ```json
  {
  "code": 200,
  "message": "Withdrawal successful",
  "data": {
    "createdAt": "2025-01-27T18:06:16.6498293",
    "updatedAt": "2025-01-27T18:06:16.6498293",
    "id": 2,
    "amount": 5000,
    "type": "WITHDRAWAL",
    "account": {
      "createdAt": "2025-01-27T17:43:45.652103",
      "updatedAt": "2025-01-27T18:06:16.6663907",
      "id": 1,
      "name": "John Doe",
      "accountNumber": "00045678912",
      "balance": 10000
    }
   }
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
   git clone https://github.com/Kihagi/sparksmatrix-bank-task.git
   cd sparksmatrix-bank-task
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
