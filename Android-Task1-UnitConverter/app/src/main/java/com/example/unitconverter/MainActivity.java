package com.example.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private Spinner fromUnitSpinner;
    private Spinner toUnitSpinner;
    private EditText inputValue;
    private Button convertButton;
    private TextView resultText;

    private final String[] categories = {
            "Length",
            "Weight",
            "Temperature"
    };

    private final String[] lengthUnits = {
            "Meters",
            "Kilometers",
            "Centimeters",
            "Miles"
    };

    private final String[] weightUnits = {
            "Kilograms",
            "Grams",
            "Pounds"
    };

    private final String[] temperatureUnits = {
            "Celsius",
            "Fahrenheit",
            "Kelvin"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorySpinner = findViewById(R.id.categorySpinner);
        fromUnitSpinner = findViewById(R.id.fromUnitSpinner);
        toUnitSpinner = findViewById(R.id.toUnitSpinner);
        inputValue = findViewById(R.id.inputValue);
        convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);

        setupCategorySpinner();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (position == 0) {
                    setupUnitSpinner(lengthUnits);
                } else if (position == 1) {
                    setupUnitSpinner(weightUnits);
                } else {
                    setupUnitSpinner(temperatureUnits);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        convertButton.setOnClickListener(v -> convertUnits());
    }

    private void setupCategorySpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(adapter);
    }

    private void setupUnitSpinner(String[] units) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        fromUnitSpinner.setAdapter(adapter);
        toUnitSpinner.setAdapter(adapter);
    }

    private void convertUnits() {

        String input = inputValue.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter a value",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {

            double value = Double.parseDouble(input);

            int category = categorySpinner.getSelectedItemPosition();

            String fromUnit =
                    fromUnitSpinner.getSelectedItem().toString();

            String toUnit =
                    toUnitSpinner.getSelectedItem().toString();

            double result;

            if (category == 0) {
                result = convertLength(value, fromUnit, toUnit);

            } else if (category == 1) {
                result = convertWeight(value, fromUnit, toUnit);

            } else {
                result = convertTemperature(value, fromUnit, toUnit);
            }

            resultText.setText(
                    String.format("%.2f %s", result, toUnit)
            );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid number",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private double convertLength(
            double value,
            String from,
            String to) {

        double meters;

        switch (from) {

            case "Meters":
                meters = value;
                break;

            case "Kilometers":
                meters = value * 1000;
                break;

            case "Centimeters":
                meters = value / 100;
                break;

            case "Miles":
                meters = value * 1609.344;
                break;

            default:
                meters = value;
        }

        switch (to) {

            case "Meters":
                return meters;

            case "Kilometers":
                return meters / 1000;

            case "Centimeters":
                return meters * 100;

            case "Miles":
                return meters / 1609.344;

            default:
                return meters;
        }
    }

    private double convertWeight(
            double value,
            String from,
            String to) {

        double kilograms;

        switch (from) {

            case "Kilograms":
                kilograms = value;
                break;

            case "Grams":
                kilograms = value / 1000;
                break;

            case "Pounds":
                kilograms = value * 0.45359237;
                break;

            default:
                kilograms = value;
        }

        switch (to) {

            case "Kilograms":
                return kilograms;

            case "Grams":
                return kilograms * 1000;

            case "Pounds":
                return kilograms / 0.45359237;

            default:
                return kilograms;
        }
    }

    private double convertTemperature(
            double value,
            String from,
            String to) {

        double celsius;

        switch (from) {

            case "Celsius":
                celsius = value;
                break;

            case "Fahrenheit":
                celsius = (value - 32) * 5 / 9;
                break;

            case "Kelvin":
                celsius = value - 273.15;
                break;

            default:
                celsius = value;
        }

        switch (to) {

            case "Celsius":
                return celsius;

            case "Fahrenheit":
                return (celsius * 9 / 5) + 32;

            case "Kelvin":
                return celsius + 273.15;

            default:
                return celsius;
        }
    }
}