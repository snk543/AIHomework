// Array to store all expenses
let expenses = [];

// Function to add a new expense
function addExpense() {
    const categoryInput = document.getElementById('category');
    const amountInput = document.getElementById('amount');
    
    const category = categoryInput.value.trim();
    const amount = parseFloat(amountInput.value);
    
    // Validate inputs
    if (!category || isNaN(amount) || amount <= 0) {
        alert('Please enter valid category and amount');
        return;
    }
    
    // Add expense to array
    expenses.push({
        category: category,
        amount: amount
    });
    
    // Clear inputs
    categoryInput.value = '';
    amountInput.value = '';
    
    // Update the table
    updateExpenseTable();
}

// Function to delete an expense
function deleteExpense(index) {
    expenses.splice(index, 1);
    updateExpenseTable();
}

// Function to update the expense table
function updateExpenseTable() {
    const tbody = document.getElementById('expenseList');
    tbody.innerHTML = '';
    
    expenses.forEach((expense, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${expense.category}</td>
            <td>$${expense.amount.toLocaleString()}</td>
            <td>
                <button class="delete-btn" onclick="deleteExpense(${index})">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Function to calculate and display expense analysis
function calculateExpenses() {
    if (expenses.length === 0) {
        alert('Please add some expenses first');
        return;
    }
    
    // Calculate total expenses
    const total = expenses.reduce((sum, expense) => sum + expense.amount, 0);
    
    // Calculate average daily expense (assuming 30 days in a month)
    const averageDaily = total / 30;
    
    // Get top 3 expenses
    const topExpenses = [...expenses]
        .sort((a, b) => b.amount - a.amount)
        .slice(0, 3);
    
    // Update the results display
    document.getElementById('totalExpenses').textContent = `$${total.toLocaleString()}`;
    document.getElementById('averageDaily').textContent = `$${averageDaily.toLocaleString()}`;
    
    const topExpensesList = document.getElementById('topExpenses');
    topExpensesList.innerHTML = '';
    topExpenses.forEach(expense => {
        const li = document.createElement('li');
        li.textContent = `${expense.category}: $${expense.amount.toLocaleString()}`;
        topExpensesList.appendChild(li);
    });
}

// Add event listener for Enter key in input fields
document.addEventListener('DOMContentLoaded', () => {
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                addExpense();
            }
        });
    });
}); 