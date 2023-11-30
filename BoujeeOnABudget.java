import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BoujeeOnABudget extends Application {

    private TableView<Expense> expenseTable = new TableView<>();
    private ObservableList<Expense> expenseData = FXCollections.observableArrayList();
    private TextField budgetField = new TextField();
    private TextField incomeField = new TextField(); // Added field for monthly income

    private Stage primaryStage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setBudgetDialog();
    }

    private void setBudgetDialog() {
        TextInputControl budgetInputDialog = new TextField();
        budgetInputDialog.setPromptText("Enter Your Budget");

        TextInputControl incomeInputDialog = new TextField(); // Added field for monthly income
        incomeInputDialog.setPromptText("Enter Your Monthly Income");

        Button setBudgetDialogButton = new Button("Set Budget");

        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(
                new Label("Enter Your Monthly Income:"), incomeInputDialog, // Added line
                new Label("Enter Your Monthly Budget:"), budgetInputDialog, setBudgetDialogButton);

        Scene dialogScene = new Scene(dialogLayout, 300, 200);
        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.setScene(dialogScene);

        setBudgetDialogButton.setOnAction(event -> {
            try {
                double budget = Double.parseDouble(budgetInputDialog.getText());
                double income = Double.parseDouble(incomeInputDialog.getText()); // Added line
                budgetField.setText(String.valueOf(budget));
                incomeField.setText(String.valueOf(income)); // Added line
                dialogStage.close();
                showExpenseTable();
            } catch (NumberFormatException e) {
                // Handle the case where the user didn't enter a valid number
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Invalid budget value. Please enter a valid number.");
                alert.showAndWait();
            }
        });

        dialogStage.showAndWait();
    }

    private void showExpenseTable() {
        primaryStage.setTitle("Boujee on a Budget");

        // Create UI components
        Button finishButton = new Button("Finish");

        // Populate category ComboBox
        ComboBox<String> categoryComboBox = new ComboBox<>();
        String[] expenseCategories = {"Food", "Home", "Work", "Fun", "Misc"};
        categoryComboBox.getItems().addAll(Arrays.asList(expenseCategories));
        categoryComboBox.setStyle(expenseCategories[0]);

        // Set up the expense table
        TableColumn<Expense, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Expense, Number> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<Expense, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));

        List<TableColumn<Expense, ?>> columns = Arrays.asList(nameColumn, amountColumn, categoryColumn, dateColumn);
        expenseTable.getColumns().addAll(columns);
        expenseTable.setItems(expenseData);

        ButtonBase addButton = new Button("Add Expense");

        TextInputControl nameField = new TextField();
        TextInputControl amountField = new TextField();

        // Inside the addButton.setOnAction(event -> {...})
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryComboBox.getValue();
            if (category == null) {
                return;
            }
            LocalDate date = LocalDate.now(); // Expense date is set to the current date

            Expense newExpense = new Expense(name, category, amount, date);
            expenseData.add(newExpense);
            saveExpenseToFile(newExpense);

            // Clear input fields
            nameField.clear();
            amountField.clear();
        });

        // Handle finish button click
        finishButton.setOnAction(event -> {
            saveAllExpensesToFile();
            double totalExpensesValue = calculateTotalExpenses();
            try {
                showSummaryDialog(totalExpensesValue);
            } catch (IOException e) {
                e.printStackTrace();
            } // Show the expense summary dialog
            primaryStage.close(); // Close the expense table screen
        });

        // Create layout
        VBox inputLayout = new VBox(10);

        inputLayout.getChildren().addAll(new Label("Name of Expense:"), nameField, new Label("Amount Spent:"),
                amountField, new Label("Category:"), categoryComboBox, addButton, finishButton);

        HBox mainLayout = new HBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(inputLayout, expenseTable);

        // Set up the scene
        Scene scene = new Scene(new BorderPane(mainLayout), 600, 400);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    private void saveExpenseToFile(Expense newExpense) {
        // Implement saving expenses to a file
    }

    private void saveAllExpensesToFile() {
        try (FileWriter writer = new FileWriter("all_expenses.csv")) {
            for (Expense expense : expenseData) {
                writer.write(expense.getName() + "," + expense.getAmount() + "," + expense.getCategory() + ","
                        + expense.getDate() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSummaryDialog(double totalExpenses) throws IOException {
        // Category breakdown
        Map<String, Double> categoryExpenses = calculateCategoryExpenses();
    
        // Prepare summary text
        StringBuilder summaryText = new StringBuilder("Expense Summary for the Month\n");
        summaryText.append("Total Expenses: $").append(String.format("%.2f", totalExpenses)).append("\n");
    
        double income = Double.parseDouble(incomeField.getText());
        double budget = Double.parseDouble(budgetField.getText());
        double remainingAmount = income - totalExpenses;
        double remainingBudget = budget - totalExpenses;
        summaryText.append("Remaining Income: $").append(String.format("%.2f", remainingAmount)).append("\n");
        summaryText.append("Remaining Monthly Budget: $").append(String.format("%.2f", remainingBudget)).append("\n");
    
        // Append category breakdown to the summary text
        summaryText.append("\nCategory Breakdown:\n");
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            summaryText.append(entry.getKey()).append(": $").append(String.format("%.2f", entry.getValue())).append("\n");
        }
    
        // Display the summary
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Expense Summary");
        alert.setHeaderText(null);
        alert.setContentText(summaryText.toString());
        alert.showAndWait();
    }

    // Helper method to calculate category expenses
    private Map<String, Double> calculateCategoryExpenses() {
        Map<String, Double> categoryExpenses = new HashMap<>();

        for (Expense expense : expenseData) {
            String category = expense.getCategory();
            double amount = expense.getAmount();

            categoryExpenses.merge(category, amount, Double::sum);
        }

        return categoryExpenses;
    }

    // Calculate total expenses
    private double calculateTotalExpenses() {
        // Calculate total expenses from the expenseData list
        return expenseData.stream().mapToDouble(Expense::getAmount).sum();
    }
}