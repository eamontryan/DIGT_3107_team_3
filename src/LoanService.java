import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

public class LoanService {

    // ===============================
    // BORROW BOOK (15-day loan rule)
    // ===============================
    public static void borrowBook(Connection conn, int memberId, int bookId) {
        try {

            // 1. Validate availability
            String checkSql = "SELECT available_copies FROM book WHERE book_id = ?";
            PreparedStatement chk = conn.prepareStatement(checkSql);
            chk.setInt(1, bookId);
            ResultSet rs = chk.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Book does not exist.");
                return;
            }

            int available = rs.getInt("available_copies");

            if (available <= 0) {
                System.out.println("❌ No copies available.");
                return;
            }

            // 2. Create loan record
            String loanSql = """
                INSERT INTO loan (member_id, borrow_date, due_date) 
                VALUES (?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY))
            """;

            PreparedStatement loanStmt = conn.prepareStatement(loanSql, PreparedStatement.RETURN_GENERATED_KEYS);
            loanStmt.setInt(1, memberId);
            loanStmt.executeUpdate();

            ResultSet loanKeys = loanStmt.getGeneratedKeys();
            loanKeys.next();
            int loanId = loanKeys.getInt(1);

            // 3. Insert Loan Item
            String loanItemSql = """
                INSERT INTO loan_item (loan_id, book_id, status)
                VALUES (?, ?, 'borrowed')
            """;

            PreparedStatement liStmt = conn.prepareStatement(loanItemSql);
            liStmt.setInt(1, loanId);
            liStmt.setInt(2, bookId);
            liStmt.executeUpdate();

            // 4. Update stock
            String updateSql = "UPDATE book SET available_copies = available_copies - 1 WHERE book_id = ?";
            PreparedStatement updStmt = conn.prepareStatement(updateSql);
            updStmt.setInt(1, bookId);
            updStmt.executeUpdate();

            System.out.println("✔ Book successfully borrowed! Loan ID = " + loanId);

        } catch (Exception e) {
            System.out.println("⚠ Error borrowing book:");
            e.printStackTrace();
        }
    }


    // ===============================
    // RETURN A BOOK & CALCULATE FINES
    // ===============================
    public static void returnBook(Connection conn, int loanId, int bookId) {
        try {
            // 1. Get due date
            String dueDateSql = """
                SELECT l.due_date 
                FROM loan l 
                JOIN loan_item li ON l.loan_id = li.loan_id 
                WHERE l.loan_id = ? AND li.book_id = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(dueDateSql);
            stmt.setInt(1, loanId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Loan record not found.");
                return;
            }

            Date dueDate = rs.getDate("due_date");

            // 2. Calculate fine
            String fineSql = "SELECT GREATEST(DATEDIFF(CURDATE(), ?), 0) AS days_late";
            PreparedStatement fineStmt = conn.prepareStatement(fineSql);
            fineStmt.setDate(1, dueDate);
            ResultSet fineRs = fineStmt.executeQuery();
            fineRs.next();

            int lateDays = fineRs.getInt("days_late");
            double fine = lateDays * 10.0;

            // 3. Update loan item
            String returnSql = """
                UPDATE loan_item
                SET return_date = CURDATE(), 
                    fine_amount = ?, 
                    status = 'returned'
                WHERE loan_id = ? AND book_id = ?
            """;

            PreparedStatement retStmt = conn.prepareStatement(returnSql);
            retStmt.setDouble(1, fine);
            retStmt.setInt(2, loanId);
            retStmt.setInt(3, bookId);
            retStmt.executeUpdate();

            // 4. Update inventory
            String stockSql = "UPDATE book SET available_copies = available_copies + 1 WHERE book_id = ?";
            PreparedStatement stockStmt = conn.prepareStatement(stockSql);
            stockStmt.setInt(1, bookId);
            stockStmt.executeUpdate();

            System.out.println("✔ Book returned! Fine applied: $" + fine);

        } catch (Exception e) {
            System.out.println("⚠ Error processing return:");
            e.printStackTrace();
        }
    }
}

