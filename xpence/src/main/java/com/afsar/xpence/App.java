package com.afsar.xpence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseCategorizer categorizer = null;
        
        try {
            // Step 1: Create an instance of ExpenseCategorizer
            System.out.println("Initializing xpence categorizer...");
            categorizer = new ExpenseCategorizer();
            System.out.println("Categorizer loaded successfully!\n");
            
            // Step 2: Prompt user for file path
            System.out.print("Enter the path to your bank statement CSV file: ");
            String filePath = scanner.nextLine().trim();
            
            if (filePath.isEmpty()) {
                System.err.println("Error: No file path provided.");
                return;
            }
            
            // Step 3 & 4: Read and parse the CSV file
            System.out.println("Processing file: " + filePath);
            
            // Step 5: Create HashMap to store spending category totals and income tracker
            HashMap<String, Double> categoryTotals = new HashMap<>();
            double totalIncome = 0.0;
            
            // Step 6: Process each row of the CSV file
            int rowCount = 0;
            int processedRows = 0;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                String[] headers = null;
                int descriptionIndex = -1;
                int amountIndex = -1;
                
                // Read the header row to find column indices
                if ((line = reader.readLine()) != null) {
                    headers = parseCSVLine(line);
                    
                    // Find the indices of Description and Amount columns
                    for (int i = 0; i < headers.length; i++) {
                        String header = headers[i].trim().toLowerCase();
                        if (header.contains("description") || header.equals("desc")) {
                            descriptionIndex = i;
                        } else if (header.contains("amount") || header.equals("amt")) {
                            amountIndex = i;
                        }
                    }
                    
                    if (descriptionIndex == -1) {
                        System.err.println("Error: Could not find 'Description' column in the CSV file.");
                        System.err.println("Available columns: " + String.join(", ", headers));
                        return;
                    }
                    
                    if (amountIndex == -1) {
                        System.err.println("Error: Could not find 'Amount' column in the CSV file.");
                        System.err.println("Available columns: " + String.join(", ", headers));
                        return;
                    }
                    
                    System.out.println("Found columns - Description: " + headers[descriptionIndex] + 
                                     ", Amount: " + headers[amountIndex]);
                }
                
                // Process each data row
                while ((line = reader.readLine()) != null) {
                    rowCount++;
                    
                    if (line.trim().isEmpty()) {
                        continue; // Skip empty lines
                    }
                    
                    try {
                        String[] columns = parseCSVLine(line);
                        
                        if (columns.length <= Math.max(descriptionIndex, amountIndex)) {
                            System.err.println("Warning: Row " + rowCount + " has insufficient columns, skipping.");
                            continue;
                        }
                        
                        // Step 6a: Get the description
                        String description = columns[descriptionIndex].trim();
                        
                        if (description.isEmpty()) {
                            System.err.println("Warning: Row " + rowCount + " has empty description, skipping.");
                            continue;
                        }
                        
                        // Step 6b: Predict the category
                        String category = categorizer.predictCategory(description);
                        
                        // Step 6c: Parse the amount
                        String amountStr = columns[amountIndex].trim();
                        // Remove common currency symbols and whitespace
                        amountStr = amountStr.replaceAll("[₹$,\\s]", "");
                        
                        double amount = Double.parseDouble(amountStr);
                        
                        // Step 6d: Handle income vs expenses based on amount sign
                        if (amount > 0) {
                            // Positive amount = Income - add to income total, don't categorize as expense
                            totalIncome += amount;
                        } else if (amount < 0) {
                            // Negative amount = Expense - add absolute value to spending category
                            double expenseAmount = Math.abs(amount);
                            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + expenseAmount);
                        }
                        // Skip zero amounts
                        
                        processedRows++;
                        
                        // Show progress for large files
                        if (processedRows % 50 == 0) {
                            System.out.println("Processed " + processedRows + " transactions...");
                        }
                        
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Row " + rowCount + " has invalid amount format, skipping.");
                    } catch (Exception e) {
                        System.err.println("Warning: Error processing row " + rowCount + ": " + e.getMessage());
                    }
                }
            }
            
            // Step 7: Print the corrected financial summary report
            printFinancialSummaryReport(categoryTotals, totalIncome, processedRows, rowCount);
            
            // Step 8: Generate pie chart visualization
            generateExpenseChart(categoryTotals, filePath);
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.err.println("Please check that the file path is correct and the file is accessible.");
        } catch (Exception e) {
            System.err.println("Error initializing categorizer or processing data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Parses a CSV line handling quoted fields and commas within quotes
     * 
     * @param line The CSV line to parse
     * @return Array of field values
     */
    private static String[] parseCSVLine(String line) {
        // Simple CSV parser - handles basic quoted fields
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
    
    /**
     * Prints a comprehensive financial summary report separating income and expenses
     * 
     * @param categoryTotals HashMap containing spending category totals
     * @param totalIncome Total income amount
     * @param processedRows Number of successfully processed rows
     * @param totalRows Total number of rows attempted
     */
    private static void printFinancialSummaryReport(HashMap<String, Double> categoryTotals, 
                                                   double totalIncome,
                                                   int processedRows, int totalRows) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                FINANCIAL SUMMARY REPORT");
        System.out.println("=".repeat(60));
        
        System.out.println("Processed " + processedRows + " out of " + totalRows + " transactions");
        System.out.println();
        
        // Section 1: Display Total Income
        System.out.println("INCOME:");
        System.out.println("-".repeat(30));
        System.out.printf("Total Income         : ₹%,12.2f%n", totalIncome);
        System.out.println();
        
        // Section 2: Display detailed breakdown of spending categories
        System.out.println("EXPENSES BY CATEGORY:");
        System.out.println("-".repeat(30));
        
        if (categoryTotals.isEmpty()) {
            System.out.println("No expense transactions found.");
            System.out.println();
        } else {
            // Calculate total spending for percentage calculations
            double totalSpending = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
            
            // Sort categories by spending amount (highest first) and display
            categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    String category = entry.getKey();
                    double amount = entry.getValue();
                    double percentage = totalSpending > 0 ? (amount / totalSpending) * 100 : 0.0;
                    
                    System.out.printf("%-20s : ₹%,10.2f (%5.1f%%)%n", 
                                    category, amount, percentage);
                });
            
            System.out.println();
            
            // Section 3: Display Total Spending
            System.out.println("SPENDING SUMMARY:");
            System.out.println("-".repeat(30));
            System.out.printf("Total Spending       : ₹%,12.2f%n", totalSpending);
            System.out.println();
            
            // Section 4: Calculate and display Net Savings
            double netSavings = totalIncome - totalSpending;
            System.out.println("NET FINANCIAL POSITION:");
            System.out.println("-".repeat(30));
            System.out.printf("Total Income         : ₹%,12.2f%n", totalIncome);
            System.out.printf("Total Spending       : ₹%,12.2f%n", totalSpending);
            System.out.println("-".repeat(35));
            
            if (netSavings >= 0) {
                System.out.printf("Net Savings          : ₹%,12.2f ✓%n", netSavings);
            } else {
                System.out.printf("Net Deficit          : ₹%,12.2f ⚠%n", Math.abs(netSavings));
            }
            
            // Additional insights
            System.out.println();
            if (totalIncome > 0) {
                double savingsRate = (netSavings / totalIncome) * 100;
                if (savingsRate >= 0) {
                    System.out.printf("Savings Rate         : %6.1f%% of income%n", savingsRate);
                } else {
                    System.out.printf("Overspending         : %6.1f%% over income%n", Math.abs(savingsRate));
                }
            }
            
            // Show top spending category
            String topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
            
            System.out.println("Top spending category: " + topCategory);
        }
        
        System.out.println("=".repeat(60));
        System.out.println("Analysis complete!");
    }
    
    /**
     * Generates and displays a pie chart visualization of expense data
     * 
     * @param categoryTotals HashMap containing expense categories and amounts
     * @param filePath Original file path for chart title
     */
    private static void generateExpenseChart(HashMap<String, Double> categoryTotals, String filePath) {
        if (categoryTotals.isEmpty()) {
            System.out.println("\nNo expense data available for chart generation.");
            return;
        }
        
        try {
            System.out.println("\nGenerating expense visualization...");
            
            // Create chart title from file name
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            if (fileName.equals(filePath)) {
                // Handle Windows path separator
                fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            }
            String chartTitle = "Expense Breakdown - " + fileName;
            
            // Generate the pie chart
            PieChartGenerator chartGenerator = new PieChartGenerator();
            chartGenerator.generatePieChart(categoryTotals, chartTitle);
            
            System.out.println("Chart window opened. Close it when finished viewing.");
            
        } catch (Exception e) {
            System.err.println("Error generating pie chart: " + e.getMessage());
            System.err.println("Continuing without chart visualization...");
        }
    }
}