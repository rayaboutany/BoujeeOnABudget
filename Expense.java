import java.time.LocalDate;

public class Expense {
    private String name;
    private String category;
    private double amount;
    private LocalDate date;

    public Expense(String name, String category, double amount, LocalDate date) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}