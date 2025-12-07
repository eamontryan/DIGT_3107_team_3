import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class LoanService {

    // ===============================
    // BORROW BOOK (15-day loan rule)
    // ===============================
    public static void borrowBook(Scanner scanner) {
        System.out.println("\n=== BORROW BOOK ===");
        System.out.print("Enter Member ID: ");
        int memberId = getIntInput(scanner);
        System.out.print("Enter Book ID: ");
        int bookId = getIntInput(scanner);

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Transaction for integrity

            try {
                // 1. Validate availability
                String checkSql = "SELECT available_copies, title FROM book WHERE book_id = ?";
                try (PreparedStatement chk = conn.prepareStatement(checkSql)) {
                    chk.setInt(1, bookId);
                    ResultSet rs = chk.executeQuery();

                    if (!rs.next()) {
                        System.out.println("❌ Book does not exist.");
                        return;
                    }

                    int available = rs.getInt("available_copies");
                    String title = rs.getString("title");

                    if (available <= 0) {
                        System.out.println("❌ No copies available for '" + title + "'.");
                        return;
                    }
                }

                // 2. Create loan record
                String loanSql = """
                    INSERT INTO loan (member_id, borrow_date, due_date) 
                    VALUES (?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY))
                """;

                int loanId;
                try (PreparedStatement loanStmt = conn.prepareStatement(loanSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    loanStmt.setInt(1, memberId);
                    loanStmt.executeUpdate();

                    ResultSet loanKeys = loanStmt.getGeneratedKeys();
                    loanKeys.next();
                    loanId = loanKeys.getInt(1);
                }

                // 3. Insert Loan Item
                String loanItemSql = """
                    INSERT INTO loan_item (loan_id, book_id, status)
                    VALUES (?, ?, 'borrowed')
                """;

                try (PreparedStatement liStmt = conn.prepareStatement(loanItemSql)) {
                    liStmt.setInt(1, loanId);
                    liStmt.setInt(2, bookId);
                    liStmt.executeUpdate();
                }

                // 4. Update stock
                String updateSql = "UPDATE book SET available_copies = available_copies - 1 WHERE book_id = ?";
                try (PreparedStatement updStmt = conn.prepareStatement(updateSql)) {
                    updStmt.setInt(1, bookId);
                    updStmt.executeUpdate();
                }

                conn.commit(); // Commit transaction
                System.out.println("✔ Book successfully borrowed! Loan ID = " + loanId);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("⚠ Error borrowing book: " + e.getMessage());
        }
    }


    // ===============================
    // RETURN A BOOK & CALCULATE FINES
    // ===============================
    public static void returnBook(Scanner scanner) {
        System.out.println("\n=== RETURN BOOK ===");
        System.out.print("Enter Loan ID: ");
        int loanId = getIntInput(scanner);
        System.out.print("Enter Book ID: ");
        int bookId = getIntInput(scanner);

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Get due date and status
                String checkSql = """
                    SELECT l.due_date, li.status
                    FROM loan l 
                    JOIN loan_item li ON l.loan_id = li.loan_id 
                    WHERE l.loan_id = ? AND li.book_id = ?
                """;
                
                Date dueDate;
                String status;

                try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                    stmt.setInt(1, loanId);
                    stmt.setInt(2, bookId);
                    ResultSet rs = stmt.executeQuery();

                    if (!rs.next()) {
                        System.out.println("❌ Loan record not found.");
                        return;
                    }
                    dueDate = rs.getDate("due_date");
                    status = rs.getString("status");
                }

                if ("returned".equalsIgnoreCase(status)) {
                    System.out.println("⚠ This book has already been returned.");
                    return;
                }

                // 2. Calculate fine
                String fineSql = "SELECT GREATEST(DATEDIFF(CURDATE(), ?), 0) AS days_late";
                double fine = 0.0;
                try (PreparedStatement fineStmt = conn.prepareStatement(fineSql)) {
                    fineStmt.setDate(1, dueDate);
                    ResultSet fineRs = fineStmt.executeQuery();
                    if (fineRs.next()) {
                        int lateDays = fineRs.getInt("days_late");
                        fine = lateDays * 10.0;
                    }
                }

                // 3. Update loan item
                String returnSql = """
                    UPDATE loan_item
                    SET return_date = CURDATE(), 
                        fine_amount = ?, 
                        status = 'returned'
                    WHERE loan_id = ? AND book_id = ?
                """;

                try (PreparedStatement retStmt = conn.prepareStatement(returnSql)) {
                    retStmt.setDouble(1, fine);
                    retStmt.setInt(2, loanId);
                    retStmt.setInt(3, bookId);
                    retStmt.executeUpdate();
                }

                // 4. Update inventory
                String stockSql = "UPDATE book SET available_copies = available_copies + 1 WHERE book_id = ?";
                try (PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {
                    stockStmt.setInt(1, bookId);
                    stockStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("✔ Book returned!");
                if (fine > 0) {
                    System.out.printf("⚠ LATE RETURN! Fine applied: $%.2f%n", fine);
                }

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("⚠ Error processing return: " + e.getMessage());
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

