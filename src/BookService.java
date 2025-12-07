import java.sql.*;
import java.util.Scanner;

public class BookService {

    // 1. View all books
    public static void viewAllBooks() {
        String query = """
            SELECT b.book_id, b.title, b.author, c.category_name, b.total_copies, b.available_copies
            FROM book b
            LEFT JOIN category c ON b.category_id = c.category_id
            ORDER BY b.book_id
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== ALL BOOKS ===");
            System.out.println(String.format("%-8s %-35s %-25s %-20s %-10s %-10s",
                "ID", "Title", "Author", "Category", "Total", "Available"));
            System.out.println("=".repeat(115));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category_name");
                int total = rs.getInt("total_copies");
                int available = rs.getInt("available_copies");

                System.out.println(String.format("%-8d %-35s %-25s %-20s %-10d %-10d",
                    id, title, author, category != null ? category : "N/A", total, available));
            }

            if (!hasResults) {
                System.out.println("No books found in the library.");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving books: " + e.getMessage());
        }
    }

    // 2. Add a new book
    public static void addBook(Scanner scanner) {
        System.out.println("\n=== ADD NEW BOOK ===");
        
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        
        // Handling Category
        System.out.println("Available Categories:");
        listCategories(); 
        System.out.print("Enter Category ID (or 0 for none): ");
        int categoryId = getIntInput(scanner);
        scanner.nextLine(); // consume newline

        System.out.print("Enter Total Copies: ");
        int totalCopies = getIntInput(scanner);
        scanner.nextLine(); // consume newline

        if (totalCopies < 0) {
            System.out.println("❌ Total copies cannot be negative.");
            return;
        }

        String insertSql = "INSERT INTO book (title, author, category_id, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
        // Note: available_copies starts equal to total_copies

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            if (categoryId > 0) {
                pstmt.setInt(3, categoryId);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setInt(4, totalCopies);
            pstmt.setInt(5, totalCopies); // available = total initially

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    System.out.println("✔ Book added successfully! Book ID: " + generatedKeys.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    // 12. View availability + circulation of a specific book
    public static void viewBookAvailability(Scanner scanner) {
        System.out.print("\nEnter Book ID to check: ");
        int bookId = getIntInput(scanner);
        scanner.nextLine();

        String query = """
            SELECT b.title, b.total_copies, b.available_copies, 
                   (SELECT COUNT(*) FROM loan_item li WHERE li.book_id = b.book_id AND li.status = 'borrowed') as currently_borrowed,
                   (SELECT COUNT(*) FROM loan_item li WHERE li.book_id = b.book_id) as total_loans
            FROM book b
            WHERE b.book_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                int total = rs.getInt("total_copies");
                int available = rs.getInt("available_copies");
                int borrowed = rs.getInt("currently_borrowed");
                int historyCount = rs.getInt("total_loans");

                System.out.println("\n=== BOOK CIRCULATION DETAILS ===");
                System.out.println("Title:              " + title);
                System.out.println("Total Copies:       " + total);
                System.out.println("Available Copies:   " + available);
                System.out.println("Currently Borrowed: " + borrowed);
                System.out.println("Lifetime Loans:     " + historyCount);
                
                // Integrity check warn
                if (available + borrowed != total) {
                    System.out.println("\n⚠ WARNING: Stock mismatch detected! (Total != Available + Borrowed)");
                }

            } else {
                System.out.println("❌ Book ID not found.");
            }

        } catch (SQLException e) {
            System.err.println("Error checking book: " + e.getMessage());
        }
    }

    // Helper to list categories
    private static void listCategories() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT category_id, category_name FROM category")) {
            while (rs.next()) {
                System.out.println("  " + rs.getInt("category_id") + ". " + rs.getString("category_name"));
            }
        } catch (SQLException e) {
            // Ignore if categories table issue, just don't show list
        }
    }

    private static int getIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
