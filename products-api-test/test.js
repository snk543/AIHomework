const axios = require('axios');
const { expect } = require('chai');
const fs = require('fs');
const path = require('path');

// API endpoint
const API_URL = 'https://fakestoreapi.com/products';

// Create results directory if it doesn't exist
const resultsDir = path.join(__dirname, 'test-results');
if (!fs.existsSync(resultsDir)) {
    fs.mkdirSync(resultsDir);
}

// Get current timestamp for file names
const timestamp = new Date().toISOString().replace(/[:.]/g, '-');

// Function to validate a single product
function validateProduct(product) {
    const defects = [];

    // Check if title exists and is not empty
    if (!product.title || product.title.trim() === '') {
        defects.push('Empty or missing title');
    }

    // Check if price is not negative
    if (typeof product.price !== 'number' || product.price < 0) {
        defects.push('Invalid or negative price');
    }

    // Check if rating.rate exists and is not greater than 5
    if (!product.rating || typeof product.rating.rate !== 'number' || product.rating.rate > 5) {
        defects.push('Invalid rating (must be between 0 and 5)');
    }

    return defects;
}

// Function to print product details
function printProductDetails(product) {
    return `
Product Details:
ID: ${product.id}
Title: ${product.title}
Price: $${product.price}
Rating: ${product.rating.rate}/5 (${product.rating.count} reviews)`;
}

// Function to save results to files
function saveResults(results) {
    // Save JSON results
    const jsonPath = path.join(resultsDir, `test-results-${timestamp}.json`);
    fs.writeFileSync(jsonPath, JSON.stringify(results, null, 2));
    console.log(`\nJSON results saved to: ${jsonPath}`);

    // Save human-readable results
    const txtPath = path.join(resultsDir, `test-results-${timestamp}.txt`);
    const txtContent = `API Test Results
================
Time: ${new Date().toLocaleString()}

Server Response: ${results.serverStatus}
Products Tested: ${results.totalProducts}

${results.productsWithDefects.length > 0 ? 
    `Products with Defects:\n${results.productsWithDefects.map(p => 
        `\nProduct ID: ${p.id}\nTitle: ${p.title}\nDefects:\n${p.defects.map(d => `- ${d}`).join('\n')}`
    ).join('\n')}` : 
    'All products passed validation'}

Sample Product:${results.sampleProduct}

Statistics:
- Total products tested: ${results.totalProducts}
- Products with defects: ${results.productsWithDefects.length}
- Average product price: $${results.averagePrice}
- Average rating: ${results.averageRating}/5`;

    fs.writeFileSync(txtPath, txtContent);
    console.log(`Text results saved to: ${txtPath}`);
}

// Main test function
async function runTests() {
    try {
        console.log('Starting API tests...\n');

        // Test 1: Verify server response code
        const response = await axios.get(API_URL);
        expect(response.status).to.equal(200);
        console.log('✓ Server response code is 200');

        const products = response.data;
        expect(products).to.be.an('array');
        console.log(`✓ Received ${products.length} products\n`);

        // Test 2: Validate each product
        const productsWithDefects = [];
        let totalPrice = 0;
        let averageRating = 0;

        products.forEach((product, index) => {
            const defects = validateProduct(product);
            if (defects.length > 0) {
                productsWithDefects.push({
                    id: product.id,
                    title: product.title,
                    defects: defects
                });
            }
            
            // Calculate statistics
            totalPrice += product.price;
            averageRating += product.rating.rate;
        });

        // Prepare results object
        const results = {
            timestamp: new Date().toISOString(),
            serverStatus: 'OK (200)',
            totalProducts: products.length,
            productsWithDefects: productsWithDefects,
            sampleProduct: printProductDetails(products[0]),
            averagePrice: (totalPrice / products.length).toFixed(2),
            averageRating: (averageRating / products.length).toFixed(2)
        };

        // Print results
        if (productsWithDefects.length > 0) {
            console.log('Products with defects found:');
            productsWithDefects.forEach(product => {
                console.log(`\nProduct ID: ${product.id}`);
                console.log(`Title: ${product.title}`);
                console.log('Defects:');
                product.defects.forEach(defect => console.log(`- ${defect}`));
            });
        } else {
            console.log('✓ All products passed validation');
        }

        // Print sample product details
        console.log('\nSample Product Details:');
        console.log(printProductDetails(products[0]));

        // Summary
        console.log(`\nTest Summary:`);
        console.log(`Total products tested: ${products.length}`);
        console.log(`Products with defects: ${productsWithDefects.length}`);
        console.log(`Average product price: $${results.averagePrice}`);
        console.log(`Average rating: ${results.averageRating}/5`);

        // Save results to files
        saveResults(results);

    } catch (error) {
        console.error('Test failed:', error.message);
        if (error.response) {
            console.error('Response status:', error.response.status);
            console.error('Response data:', error.response.data);
        }
        process.exit(1);
    }
}

// Run the tests
runTests(); 