# Xpence
An AI-powered command-line tool built with Java and Weka to automatically categorize financial transactions from CSV files.

xpence: AI-Powered Personal Finance Manager
An intelligent command-line application built with Java that leverages a custom-trained machine learning model to automatically categorize financial transactions from a bank statement and generate insightful reports with data visualizations.

To add your own image: take a screenshot of the pie chart, upload it to a site like Imgur, and paste the direct image link here.

🚀 Core Features
🤖 Automated Categorization: Uses a Naive Bayes classifier trained with the Weka library to accurately assign categories like "Food," "Shopping," or "Bills" to expenses based on their transaction descriptions.

📄 Smart CSV Processing: Reads and parses standard .csv bank statements, dynamically identifying the required Description and Amount columns from the header, making it adaptable to different statement formats.

📊 Intelligent Reporting: Generates a detailed financial summary in the console that clearly distinguishes income from expenses and calculates key metrics like total spending, net savings, and savings rate.

📈 Data Visualization: Displays a clean, professional pie chart using the JFreeChart library for an immediate visual breakdown of spending habits, making complex data easy to understand.

📦 Portable Application: Packaged into a single, executable JAR file using Maven, allowing it to be run from any command line without needing an IDE or complex setup.

🛠️ Technology Stack
Core Language: Java (JDK 21)

Build & Dependency Management: Apache Maven

Machine Learning: Weka 3.8

Data Visualization: JFreeChart 1.5.3

⚙️ How to Run the Application
You can run this application easily from your computer's command line.

Prerequisites
Java (Version 17 or higher) must be installed on your system.

Instructions
Download the Application:

Go to the Releases Page of this repository.

Download the latest xpence-*-jar-with-dependencies.jar file.

Prepare Your Data:

Have your bank statement ready as a .csv file.

Ensure your file has at least a Description column and an Amount column. Expenses should be represented by negative numbers (e.g., -250.50) and income by positive numbers.

Run from the Command Line:

Open a terminal or command prompt.

Navigate to the directory where you downloaded the JAR file.

Execute the following command:

java -jar xpence-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Follow the Prompts:

The application will ask you to enter the path to your .csv file. Provide the full or relative path and press Enter.

The financial report will be printed to the console, and a new window containing the pie chart will appear.

📂 Project Architecture
The project is designed with a modular structure to separate concerns, making it easier to maintain and extend.

ModelTrainer.java: A standalone utility responsible for the entire machine learning training pipeline. It reads the raw training data, applies preprocessing filters, trains the classifier, and saves the two essential artifacts: the model itself and the text-processing filter.

ExpenseCategorizer.java: The core prediction engine. This class loads the saved model and filter, providing a clean method to predict the category of any new transaction description. It encapsulates all the complexity of the AI logic.

App.java: The main user-facing application. It orchestrates the entire process, from initializing the ExpenseCategorizer and handling user input to processing the statement file and calling the reporting and visualization modules.

PieChartGenerator.java: A dedicated utility class for data visualization. It takes the final expense data and uses the JFreeChart library to render the pie chart, keeping the UI logic separate from the main application logic.

💡 Development Journey & Key Challenges
This project involved solving several complex machine learning integration problems.

The Data Type Problem: A key challenge was that Weka was incorrectly inferring our text Description column as a NOMINAL type (a fixed list) instead of a STRING type.

Solution: We implemented a NominalToString filter as the first step in our data preprocessing pipeline, ensuring the data was in the correct format before training.

The Filter Consistency Problem: Initial predictions were highly inaccurate because the text "dictionary" (the StringToWordVector filter) used for prediction was different from the one used during training.

Solution: We re-architected the training process to save the configured filter alongside the model. The prediction class now loads both artifacts, guaranteeing perfect consistency between the training and prediction environments. This is the standard, robust methodology for deploying Weka models.

🔮 Future Enhancements
This project serves as a strong foundation that can be extended with several professional features:

Graphical User Interface (GUI): Replace the CLI with a full GUI using JavaFX or Swing.

Database Integration: Use a database like SQLite to store transactions and track spending trends over time.

Advanced Reporting: Add features to generate reports for specific date ranges (e.g., monthly or quarterly summaries).

Budgeting and Goals: Allow users to set monthly budgets for categories and receive alerts on their progress.
