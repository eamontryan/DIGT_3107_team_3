import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class QueryService {

    // a. List overdue books and fines
    public static void listOverdueBooksAndFines() {
        String query =
            "SELECT m.name, m.card_number, b.title, l.borrow_date, l.due_date, " +
            "li.return_date, li.fine_amount, li.status " +
            "FROM loan_item li " +
            "JOIN loan l ON li.loan_id = l.loan_id " +
            "JOIN member m ON l.member_id = m.member_id " +
            "JOIN book b ON li.book_id = b.book_id " +
            "WHERE li.status = 'borrowed' AND l.due_date < CURDATE() " +
            "ORDER BY l.due_date";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== OVERDUE BOOKS AND FINES ===");
            System.out.println(String.format("%-20s %-15s %-30s %-12s %-12s %-10s",
                "Member", "Card Number", "Book Title", "Borrow Date", "Due Date", "Fine"));
            System.out.println("=".repeat(110));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                String name = rs.getString("name");
                String cardNumber = rs.getString("card_number");
                String title = rs.getString("title");
                Date borrowDate = rs.getDate("borrow_date");
                Date dueDate = rs.getDate("due_date");
                double fine = rs.getDouble("fine_amount");

                // Calculate days overdue
                LocalDate due = dueDate.toLocalDate();
                LocalDate today = LocalDate.now();
                long daysOverdue = ChronoUnit.DAYS.between(due, today);
                double calculatedFine = daysOverdue * 10.0;

                System.out.println(String.format("%-20s %-15s %-30s %-12s %-12s $%-9.2f",
                    name, cardNumber, title, borrowDate, dueDate, calculatedFine));
            }

            if (!hasResults) {
                System.out.println("No overdue books found.");
            }

        } catch (SQLException e) {
            System.err.println("Error listing overdue books: " + e.getMessage());
        }
    }

    // b. Find most borrowed books
    public static void findMostBorrowedBooks() {
        String query =
            "SELECT b.book_id, b.title, b.author, COUNT(*) as borrow_count " +
            "FROM loan_item li " +
            "JOIN book b ON li.book_id = b.book_id " +
            "GROUP BY b.book_id, b.title, b.author " +
            "ORDER BY borrow_count DESC " +
            "LIMIT 10";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== MOST BORROWED BOOKS ===");
            System.out.println(String.format("%-10s %-40s %-30s %-15s",
                "Book ID", "Title", "Author", "Times Borrowed"));
            System.out.println("=".repeat(100));

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int count = rs.getInt("borrow_count");

                System.out.println(String.format("%-10d %-40s %-30s %-15d",
                    bookId, title, author, count));
            }

        } catch (SQLException e) {
            System.err.println("Error finding most borrowed books: " + e.getMessage());
        }
    }

    // c. List currently borrowed books
    public static void listCurrentlyBorrowedBooks() {
        String query =
            "SELECT m.name, m.card_number, b.title, b.author, l.borrow_date, l.due_date " +
            "FROM loan_item li " +
            "JOIN loan l ON li.loan_id = l.loan_id " +
            "JOIN member m ON l.member_id = m.member_id " +
            "JOIN book b ON li.book_id = b.book_id " +
            "WHERE li.status = 'borrowed' " +
            "ORDER BY l.due_date";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== CURRENTLY BORROWED BOOKS ===");
            System.out.println(String.format("%-20s %-15s %-35s %-30s %-12s %-12s",
                "Member", "Card Number", "Book Title", "Author", "Borrow Date", "Due Date"));
            System.out.println("=".repeat(130));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                String name = rs.getString("name");
                String cardNumber = rs.getString("card_number");
                String title = rs.getString("title");
                String author = rs.getString("author");
                Date borrowDate = rs.getDate("borrow_date");
                Date dueDate = rs.getDate("due_date");

                System.out.println(String.format("%-20s %-15s %-35s %-30s %-12s %-12s",
                    name, cardNumber, title, author, borrowDate, dueDate));
            }

            if (!hasResults) {
                System.out.println("No books currently borrowed.");
            }

        } catch (SQLException e) {
            System.err.println("Error listing currently borrowed books: " + e.getMessage());
        }
    }

    // d. Retrieve a member's borrowing history
    public static void retrieveMemberBorrowingHistory(int memberId) {
        String query =
            "SELECT m.name, m.card_number, b.title, b.author, " +
            "l.borrow_date, l.due_date, li.return_date, li.status, li.fine_amount " +
            "FROM loan_item li " +
            "JOIN loan l ON li.loan_id = l.loan_id " +
            "JOIN member m ON l.member_id = m.member_id " +
            "JOIN book b ON li.book_id = b.book_id " +
            "WHERE m.member_id = ? " +
            "ORDER BY l.borrow_date DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n=== MEMBER BORROWING HISTORY ===");

            boolean hasResults = false;
            String memberName = "";
            String cardNumber = "";

            while (rs.next()) {
                if (!hasResults) {
                    memberName = rs.getString("name");
                    cardNumber = rs.getString("card_number");
                    System.out.println("Member: " + memberName + " (Card: " + cardNumber + ")");
                    System.out.println(String.format("\n%-35s %-30s %-12s %-12s %-12s %-10s %-10s",
                        "Book Title", "Author", "Borrow Date", "Due Date", "Return Date", "Status", "Fine"));
                    System.out.println("=".repeat(130));
                }
                hasResults = true;

                String title = rs.getString("title");
                String author = rs.getString("author");
                Date borrowDate = rs.getDate("borrow_date");
                Date dueDate = rs.getDate("due_date");
                Date returnDate = rs.getDate("return_date");
                String status = rs.getString("status");
                double fine = rs.getDouble("fine_amount");

                String returnDateStr = (returnDate != null) ? returnDate.toString() : "N/A";
                String fineStr = (fine > 0) ? String.format("$%.2f", fine) : "-";

                System.out.println(String.format("%-35s %-30s %-12s %-12s %-12s %-10s %-10s",
                    title, author, borrowDate, dueDate, returnDateStr, status, fineStr));
            }

            if (!hasResults) {
                System.out.println("No borrowing history found for member ID: " + memberId);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving member history: " + e.getMessage());
        }
    }

    // e. Show books due soon (within 3 days)
    public static void showBooksDueSoon() {
        String query =
            "SELECT m.name, m.card_number, m.phone_number, b.title, l.borrow_date, l.due_date " +
            "FROM loan_item li " +
            "JOIN loan l ON li.loan_id = l.loan_id " +
            "JOIN member m ON l.member_id = m.member_id " +
            "JOIN book b ON li.book_id = b.book_id " +
            "WHERE li.status = 'borrowed' " +
            "AND l.due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 DAY) " +
            "ORDER BY l.due_date";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== BOOKS DUE SOON (Within 3 Days) ===");
            System.out.println(String.format("%-20s %-15s %-15s %-35s %-12s %-12s",
                "Member", "Card Number", "Phone", "Book Title", "Borrow Date", "Due Date"));
            System.out.println("=".repeat(120));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                String name = rs.getString("name");
                String cardNumber = rs.getString("card_number");
                String phone = rs.getString("phone_number");
                String title = rs.getString("title");
                Date borrowDate = rs.getDate("borrow_date");
                Date dueDate = rs.getDate("due_date");

                System.out.println(String.format("%-20s %-15s %-15s %-35s %-12s %-12s",
                    name, cardNumber, phone, title, borrowDate, dueDate));
            }

            if (!hasResults) {
                System.out.println("No books due soon.");
            }

        } catch (SQLException e) {
            System.err.println("Error showing books due soon: " + e.getMessage());
        }
    }

    // f. Identify books never borrowed
    public static void identifyNeverBorrowedBooks() {
        String query =
            "SELECT b.book_id, b.title, b.author, c.category_name, b.total_copies, b.available_copies " +
            "FROM book b " +
            "LEFT JOIN category c ON b.category_id = c.category_id " +
            "WHERE b.book_id NOT IN (SELECT DISTINCT book_id FROM loan_item) " +
            "ORDER BY b.title";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== BOOKS NEVER BORROWED ===");
            System.out.println(String.format("%-10s %-40s %-30s %-20s %-12s",
                "Book ID", "Title", "Author", "Category", "Total Copies"));
            System.out.println("=".repeat(120));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category_name");
                int totalCopies = rs.getInt("total_copies");

                System.out.println(String.format("%-10d %-40s %-30s %-20s %-12d",
                    bookId, title, author, category != null ? category : "N/A", totalCopies));
            }

            if (!hasResults) {
                System.out.println("All books have been borrowed at least once.");
            }

        } catch (SQLException e) {
            System.err.println("Error identifying never borrowed books: " + e.getMessage());
        }
    }
}
