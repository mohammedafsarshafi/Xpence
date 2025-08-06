package com.afsar.xpence;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.InputStream;

public class ExpenseCategorizer {

    private Classifier classifier;
    private StringToWordVector filter;
    private Instances header; // Stores the final, correct data structure

    public ExpenseCategorizer() throws Exception {
        // Load the trained model and filter
        classifier = (Classifier) SerializationHelper.read("expense-classifier.model");
        filter = (StringToWordVector) SerializationHelper.read("expense-filter.model");

        // THIS IS THE FIX: Create the header by loading the data AND applying
        // the NominalToString filter, ensuring the Description attribute is STRING type.
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("training-data.csv")) {
            if (is == null) throw new Exception("training-data.csv not found.");
            
            CSVLoader loader = new CSVLoader();
            loader.setSource(is);
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            NominalToString ntsFilter = new NominalToString();
            ntsFilter.setAttributeIndexes("1");
            ntsFilter.setInputFormat(data);
            
            // The header is now based on the data AFTER the type conversion
            this.header = Filter.useFilter(data, ntsFilter);
            this.header.setClassIndex(this.header.numAttributes() - 1);
        }
    }

    public String predictCategory(String description) throws Exception {
        // Create an instance based on the correct STRING header
        Instance newInstance = new DenseInstance(header.numAttributes());
        newInstance.setDataset(header);
        newInstance.setValue(header.attribute("Description"), description);

        // Apply the loaded filter
        filter.input(newInstance);
        Instance filteredInstance = filter.output();

        // Classify the instance
        double predictionIndex = classifier.classifyInstance(filteredInstance);
        return header.classAttribute().value((int) predictionIndex);
    }

    public static void main(String[] args) {
        try {
            ExpenseCategorizer categorizer = new ExpenseCategorizer();

            String[] testDescriptions = {
                    "ZOMATO LTD 25109",
                    "METRO CASH N CARRY MUM",
                    "TATASKY RECHARGE 34321",
                    "IRCTC BOOKING 1452092",
                    "ICICI BANK CREDITCARD PAYMENT",
                    "FIREFLY PAYROLL"
                };

            System.out.println("\n=== PREDICTING CATEGORIES ===");
            for (String desc : testDescriptions) {
                String category = categorizer.predictCategory(desc);
                System.out.println("✓ \"" + desc + "\" -> " + category);
            }
        } catch (Exception e) {
            System.err.println("An error occurred:");
            e.printStackTrace();
        }
    }
}