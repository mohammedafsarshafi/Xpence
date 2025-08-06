package com.afsar.xpence;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * PieChartGenerator creates and displays pie charts for expense data visualization.
 * Uses JFreeChart library to generate interactive charts with percentage labels.
 */
public class PieChartGenerator {
    
    /**
     * Generates and displays a pie chart in a new window based on the provided expense data.
     * 
     * @param expenseData HashMap containing category names as keys and expense amounts as values
     * @param title The title to display on the chart
     */
    public void generatePieChart(HashMap<String, Double> expenseData, String title) {
        // Step 1: Create a DefaultPieDataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        // Step 2: Populate the dataset from the input HashMap
        for (HashMap.Entry<String, Double> entry : expenseData.entrySet()) {
            String category = entry.getKey();
            Double amount = entry.getValue();
            
            // Only add categories with positive amounts
            if (amount != null && amount > 0) {
                dataset.setValue(category, amount);
            }
        }
        
        // Step 3: Create the chart using ChartFactory.createPieChart()
        JFreeChart chart = ChartFactory.createPieChart(
            title,          // chart title
            dataset,        // data
            true,           // include legend
            true,           // tooltips
            false           // URLs
        );
        
        // Step 4: Get the PiePlot from the chart
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        
        // Step 5: Set the outline color for all slices correctly
        plot.setDefaultSectionOutlinePaint(Color.WHITE);
        
        // Step 6: Create percentage labels
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
        
        // Step 7: Set the plot background to be transparent
        plot.setBackgroundPaint(null);
        
        // Additional customizations for better appearance
        plot.setOutlineVisible(false);
        plot.setSectionOutlinesVisible(true);
        plot.setDefaultSectionOutlineStroke(new BasicStroke(2.0f));
        
        // Customize chart title
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));
        chart.getTitle().setPaint(Color.DARK_GRAY);
        
        // Step 8: Create a ChartFrame to display the chart
        ChartFrame frame = new ChartFrame(title, chart);
        
        // Step 9: Set the frame's default close operation to dispose on close
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Step 10: Pack the frame and set it to be visible
        frame.pack();
        frame.setVisible(true);
        
        // Print confirmation message
        System.out.println("Pie chart '" + title + "' displayed successfully.");
    }
}