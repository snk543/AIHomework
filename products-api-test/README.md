# Fake Store API Test Suite

This project contains automated tests to validate data from the Fake Store API (https://fakestoreapi.com/products). The tests verify the integrity and validity of product data returned by the API.

## Test Objectives

The test suite validates the following criteria:
- Server response code (expected 200)
- Product data integrity:
  - `title` must not be empty
  - `price` must not be negative
  - `rating.rate` must not exceed 5
- Generates a list of products containing defects

## Prerequisites

- Node.js (version 12 or higher)
- npm (Node Package Manager)

## Setup

1. Navigate to the project directory:
   ```bash
   cd products-api-test
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

## Running the Tests

To run the tests, execute:
```bash
npm test
```

## Test Output

The test suite will output:
- Server response status
- Number of products received
- List of products with defects (if any)
- Summary of test results including:
  - Total number of products tested
  - Number of products with defects

## Example Output

```
Starting API tests...

✓ Server response code is 200
✓ Received 20 products

Products with defects found:

Product ID: 1
Title: Example Product
Defects:
- Invalid rating (must be between 0 and 5)

Test Summary:
Total products tested: 20
Products with defects: 1
```

## Error Handling

The test suite includes error handling for:
- Network errors
- Invalid API responses
- Malformed data
- Server errors

If any critical errors occur, the test will fail with an appropriate error message. 