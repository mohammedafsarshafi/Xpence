package com.afsar.xpence;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.InputStream;

public class ModelTrainer {

    public static void main(String[] args) throws Exception {
        // Load Data
        System.out.println("Loading data...");
        InputStream is = ModelTrainer.class.getClassLoader().getResourceAsStream("training-data.csv");
        CSVLoader loader = new CSVLoader();
        loader.setSource(is);
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        // Convert Description attribute to String type
        NominalToString ntsFilter = new NominalToString();
        ntsFilter.setAttributeIndexes("1");
        ntsFilter.setInputFormat(data);
        data = Filter.useFilter(data, ntsFilter);

        // Create and build the StringToWordVector filter
        System.out.println("Building filter...");
        StringToWordVector stwFilter = new StringToWordVector();
        stwFilter.setInputFormat(data);
        Instances filteredData = Filter.useFilter(data, stwFilter);
        System.out.println("Filter built successfully.");

        // Train Classifier
        System.out.println("Training model...");
        NaiveBayes classifier = new NaiveBayes();
        classifier.buildClassifier(filteredData);

        // Save BOTH the classifier and the filter
        System.out.println("Saving model and filter...");
        SerializationHelper.write("expense-classifier.model", classifier);
        SerializationHelper.write("expense-filter.model", stwFilter); // SAVE THE FILTER
        System.out.println("Model and filter saved successfully.");
    }
}