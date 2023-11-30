import javafx.scene.control.Alert;

public class SummaryPage {
    private double budget;
    private double totalExpenses;

    public SummaryPage(double budget, double totalExpenses) {
        this.budget = budget;
        this.totalExpenses = totalExpenses;
    }

    public void showSummary() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Expense Summary");
        alert.setHeaderText("Expense Summary for the Month");

        // Calculate remaining budget
        double remainingAmount = budget - totalExpenses;

        String summaryText = "Total Expenses: $" + String.format("%.2f", totalExpenses);
        summaryText += "\nRemaining Budget: $" + String.format("%.2f", remainingAmount);

        alert.setContentText(summaryText);
        alert.showAndWait();
    }
}