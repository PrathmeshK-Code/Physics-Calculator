import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PhysicsCalculator extends JFrame {
    private JComboBox<String> formulaComboBox;
    private JPanel inputPanel;
    private JTextField[] inputFields;
    private JLabel resultLabel;
    private Map<String, Integer> formulaInputs;
    private Map<String, String[]> inputLabels;
    private Map<String, String> formulaExpressions;

    public PhysicsCalculator() {
        // Initialize formula data
        formulaInputs = new HashMap<>();
        inputLabels = new HashMap<>();
        formulaExpressions = new HashMap<>();

        // Predefined formulas
        formulaInputs.put("Final Velocity (v = u + at)", 3);
        formulaInputs.put("Force (F = ma)", 2);
        formulaInputs.put("Kinetic Energy (KE = ½mv²)", 2);
        formulaInputs.put("Potential Energy (PE = mgh)", 3);

        inputLabels.put("Final Velocity (v = u + at)", new String[]{"Initial Velocity (u)", "Acceleration (a)", "Time (t)"});
        inputLabels.put("Force (F = ma)", new String[]{"Mass (m)", "Acceleration (a)"});
        inputLabels.put("Kinetic Energy (KE = ½mv²)", new String[]{"Mass (m)", "Velocity (v)"});
        inputLabels.put("Potential Energy (PE = mgh)", new String[]{"Mass (m)", "Gravity (g)", "Height (h)"});

        // Set up the frame
        setTitle("Physics Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(450, 450);
        setMinimumSize(new Dimension(400, 400));
        setLocationRelativeTo(null);

        // Create components
        formulaComboBox = new JComboBox<>(formulaInputs.keySet().toArray(new String[0]));
        formulaComboBox.setPreferredSize(new Dimension(300, 30)); // Ensure dropdown has fixed width
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 5, 5));
        JButton calculateButton = new JButton("Calculate");
        JButton addFormulaButton = new JButton("Add Formula");
        addFormulaButton.setPreferredSize(new Dimension(120, 30)); // Explicit size for button
        resultLabel = new JLabel("Result: ");

        // Create north panel for formula selection and add button
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Changed to FlowLayout
        northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
        northPanel.add(formulaComboBox);
        northPanel.add(addFormulaButton);

        // Create south panel for button and result
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(calculateButton);
        southPanel.add(buttonPanel);
        southPanel.add(resultLabel);

        // Add components to frame
        add(northPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Update input fields for initial formula
        updateInputFields(formulaInputs.get(formulaComboBox.getSelectedItem()));

        // ComboBox listener
        formulaComboBox.addActionListener(e -> {
            String selectedFormula = (String) formulaComboBox.getSelectedItem();
            updateInputFields(formulaInputs.get(selectedFormula));
        });

        // Calculate button listener
        calculateButton.addActionListener(e -> calculateResult());

        // Add Formula button listener
        addFormulaButton.addActionListener(e -> showAddFormulaDialog());

        // Force layout update
        revalidate();
        repaint();

        setVisible(true);
    }

    private void updateInputFields(int numInputs) {
        inputPanel.removeAll();
        inputFields = new JTextField[numInputs];
        String selectedFormula = (String) formulaComboBox.getSelectedItem();
        String[] labels = inputLabels.get(selectedFormula);

        for (int i = 0; i < numInputs; i++) {
            inputPanel.add(new JLabel(labels[i] + ":"));
            inputFields[i] = new JTextField(10);
            inputPanel.add(inputFields[i]);
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void showAddFormulaDialog() {
        JDialog dialog = new JDialog(this, "Add New Formula", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Dialog components
        dialog.add(new JLabel("Formula Name:"));
        JTextField nameField = new JTextField(15);
        dialog.add(nameField);

        dialog.add(new JLabel("Number of Variables (1-5):"));
        JTextField numVarsField = new JTextField(5);
        dialog.add(numVarsField);

        dialog.add(new JLabel("Variable Names (comma-separated):"));
        JTextField varNamesField = new JTextField(15);
        dialog.add(varNamesField);

        dialog.add(new JLabel("Formula Expression (e.g., a * b + c):"));
        JTextField expressionField = new JTextField(15);
        dialog.add(expressionField);

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        dialog.add(addButton);
        dialog.add(cancelButton);

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int numVars = Integer.parseInt(numVarsField.getText().trim());
                String[] varNames = varNamesField.getText().trim().split("\\s*,\\s*");
                String expression = expressionField.getText().trim();

                if (name.isEmpty() || numVars < 1 || numVars > 5 || varNames.length != numVars || expression.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields correctly. Ensure variable count matches names.");
                    return;
                }

                // Add to data structures
                formulaInputs.put(name, numVars);
                inputLabels.put(name, varNames);
                formulaExpressions.put(name, expression);
                formulaComboBox.addItem(name);

                JOptionPane.showMessageDialog(dialog, "Formula added successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number of variables.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void calculateResult() {
        try {
            String selectedFormula = (String) formulaComboBox.getSelectedItem();
            double result = 0;

            if (formulaExpressions.containsKey(selectedFormula)) {
                // Custom formula
                String expression = formulaExpressions.get(selectedFormula);
                String[] varNames = inputLabels.get(selectedFormula);
                Map<String, Double> variables = new HashMap<>();
                for (int i = 0; i < inputFields.length; i++) {
                    variables.put(varNames[i], Double.parseDouble(inputFields[i].getText()));
                }
                result = evaluateExpression(expression, variables);
                resultLabel.setText("Result: " + String.format("%.2f", result));
            } else {
                // Predefined formulas
                switch (selectedFormula) {
                    case "Final Velocity (v = u + at)":
                        double u = Double.parseDouble(inputFields[0].getText());
                        double a = Double.parseDouble(inputFields[1].getText());
                        double t = Double.parseDouble(inputFields[2].getText());
                        result = u + a * t;
                        resultLabel.setText("Result: Final Velocity = " + String.format("%.2f", result) + " m/s");
                        break;

                    case "Force (F = ma)":
                        double m = Double.parseDouble(inputFields[0].getText());
                        double a2 = Double.parseDouble(inputFields[1].getText());
                        result = m * a2;
                        resultLabel.setText("Result: Force = " + String.format("%.2f", result) + " N");
                        break;

                    case "Kinetic Energy (KE = ½mv²)":
                        double m2 = Double.parseDouble(inputFields[0].getText());
                        double v = Double.parseDouble(inputFields[1].getText());
                        result = 0.5 * m2 * v * v;
                        resultLabel.setText("Result: Kinetic Energy = " + String.format("%.2f", result) + " J");
                        break;

                    case "Potential Energy (PE = mgh)":
                        double m3 = Double.parseDouble(inputFields[0].getText());
                        double g = Double.parseDouble(inputFields[1].getText());
                        double h = Double.parseDouble(inputFields[2].getText());
                        result = m3 * g * h;
                        resultLabel.setText("Result: Potential Energy = " + String.format("%.2f", result) + " J");
                        break;
                }
            }
        } catch (NumberFormatException ex) {
            resultLabel.setText("Error: Please enter valid numbers");
        } catch (Exception ex) {
            resultLabel.setText("Error: Invalid formula expression");
        }
    }

    private double evaluateExpression(String expression, Map<String, Double> variables) {
        // Replace variables with their values
        String evalExpression = expression;
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            evalExpression = evalExpression.replaceAll("\\b" + entry.getKey() + "\\b", String.valueOf(entry.getValue()));
        }

        // Simple expression parser (supports +, -, *, /, ^)
        List<String> tokens = tokenizeExpression(evalExpression);
        return evaluateTokens(tokens);
    }

    private List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        expression = expression.replaceAll("\\s", "");

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                current.append(c);
            } else if ("+-*/^".indexOf(c) != -1) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
            } else {
                throw new IllegalArgumentException("Invalid character in expression: " + c);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    private double evaluateTokens(List<String> tokens) {
        // Handle ^ (power) first
        List<String> powerProcessed = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("^")) {
                double left = Double.parseDouble(powerProcessed.remove(powerProcessed.size() - 1));
                double right = Double.parseDouble(tokens.get(++i));
                powerProcessed.add(String.valueOf(Math.pow(left, right)));
            } else {
                powerProcessed.add(tokens.get(i));
            }
        }

        // Handle * and /
        List<String> mulDivProcessed = new ArrayList<>();
        for (int i = 0; i < powerProcessed.size(); i++) {
            if (powerProcessed.get(i).equals("*") || powerProcessed.get(i).equals("/")) {
                double left = Double.parseDouble(mulDivProcessed.remove(mulDivProcessed.size() - 1));
                double right = Double.parseDouble(powerProcessed.get(++i));
                if (powerProcessed.get(i - 1).equals("*")) {
                    mulDivProcessed.add(String.valueOf(left * right));
                } else {
                    mulDivProcessed.add(String.valueOf(left / right));
                }
            } else {
                mulDivProcessed.add(powerProcessed.get(i));
            }
        }

        // Handle + and -
        double result = Double.parseDouble(mulDivProcessed.get(0));
        for (int i = 1; i < mulDivProcessed.size(); i += 2) {
            String op = mulDivProcessed.get(i);
            double next = Double.parseDouble(mulDivProcessed.get(i + 1));
            if (op.equals("+")) {
                result += next;
            } else if (op.equals("-")) {
                result -= next;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhysicsCalculator());
    }
}