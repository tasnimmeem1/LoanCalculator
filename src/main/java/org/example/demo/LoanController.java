package org.example.demo;

import java.text.NumberFormat;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class LoanController {
    private static final NumberFormat currency =
            NumberFormat.getCurrencyInstance(Locale.US);

    @FXML
    private TextField annualInterestRateTextField;

    @FXML
    private Label numberOfYearsLabel;

    @FXML
    private Slider numberOfYearsSlider;

    @FXML
    private TextField loanAmountTextField;

    @FXML
    private TextField monthlyPaymentTextField;

    @FXML
    private TextField totalPaymentTextField;

    @FXML
    public void initialize() {
        // Set up the slider
        numberOfYearsSlider.setMin(1);
        numberOfYearsSlider.setMax(60);
        numberOfYearsSlider.setValue(1);
        numberOfYearsSlider.setMajorTickUnit(1);
        numberOfYearsSlider.setBlockIncrement(1);
        numberOfYearsSlider.setSnapToTicks(true);

        // Display the initial number of years
        numberOfYearsLabel.setText(
                String.valueOf((int) numberOfYearsSlider.getValue())
        );

        // Display initial payment amounts
        monthlyPaymentTextField.setText(currency.format(0));
        totalPaymentTextField.setText(currency.format(0));

        // These are output fields, so the user should not type in them
        monthlyPaymentTextField.setEditable(false);
        totalPaymentTextField.setEditable(false);

        // Update the year label when the slider moves
        numberOfYearsSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int years = newValue.intValue();
                    numberOfYearsLabel.setText(String.valueOf(years));
                }
        );
    }

    @FXML
    private void calculateButtonPressed(ActionEvent event) {
        try {
            // Read and clean the user's input
            String rateText =
                    annualInterestRateTextField.getText().trim();

            String amountText =
                    loanAmountTextField.getText()
                            .replace("$", "")
                            .replace(",", "")
                            .trim();

            if (rateText.isEmpty() || amountText.isEmpty()) {
                throw new NumberFormatException();
            }

            double annualInterestRate =
                    Double.parseDouble(rateText);

            double loanAmount =
                    Double.parseDouble(amountText);

            int numberOfYears =
                    (int) Math.round(numberOfYearsSlider.getValue());

            if (annualInterestRate < 0 ||
                    loanAmount <= 0 ||
                    numberOfYears <= 0) {

                throw new NumberFormatException();
            }

            int numberOfMonths = numberOfYears * 12;

            double monthlyInterestRate =
                    annualInterestRate / 100.0 / 12.0;

            double monthlyPayment;

            // Prevent division by zero for a 0% loan
            if (monthlyInterestRate == 0) {
                monthlyPayment =
                        loanAmount / numberOfMonths;
            } else {
                monthlyPayment =
                        loanAmount *
                                (monthlyInterestRate *
                                        Math.pow(1 + monthlyInterestRate,
                                                numberOfMonths)) /
                                (Math.pow(1 + monthlyInterestRate,
                                        numberOfMonths) - 1);
            }

            double totalPayment =
                    monthlyPayment * numberOfMonths;

            // Display the calculated results
            monthlyPaymentTextField.setText(
                    currency.format(monthlyPayment)
            );

            totalPaymentTextField.setText(
                    currency.format(totalPayment)
            );
        }
        catch (NumberFormatException ex) {
            monthlyPaymentTextField.setText("Invalid input");
            totalPaymentTextField.setText("Invalid input");

            annualInterestRateTextField.requestFocus();
        }
    }
}