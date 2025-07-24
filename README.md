# Physics-Calculator
A Java Swing-based GUI application for calculating physics formulas with predefined and user-defined expressions.

Java Principles
- OOP: Encapsulation (PhysicsCalculator class), abstraction (data handling via HashMap).
- Generics: Used in HashMap<String, Integer>, HashMap<String, String[]> for type safety.
- Exception Handling: NumberFormatException, IllegalArgumentException for input validation.
- Event-Driven Programming: Action listeners for JComboBox, JButton interactions.
- Swing GUI: JFrame, JPanel, JComboBox, JTextField, JLabel for user interface.

Data Structures
- HashMap: Stores formula data (formulaInputs, inputLabels, formulaExpressions).
- ArrayList: Tokenizes expressions for parsing.
- Array: Manages input fields (JTextField[]).

Libraries & Features
- Java Swing: For GUI components and layout (BorderLayout, GridLayout, FlowLayout).
- Expression Parser: Custom parser for user-defined formulas (supports +, -, *, /, ^).
- Dynamic UI: Updates input fields based on selected formula.
- Dialogs: JDialog for adding custom formulas, JOptionPane for user feedback.
