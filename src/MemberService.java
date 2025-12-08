import java.sql.*;
import java.util.Scanner;

public class MemberService {

    // a. Record member name, contact information, and library card number
    public static void addMember(Scanner scanner) {
        System.out.println("\n=== ADD NEW MEMBER ===");

        System.out.print("Enter member name: ");
        String name = scanner.nextLine();

        // Validate phone number (10 digits)
        String phoneNumber;
        while (true) {
            System.out.print("Enter phone number (10 digits): ");
            phoneNumber = scanner.nextLine();
            if (phoneNumber.matches("\\d{10}")) {
                break;
            }
            System.err.println("Error: Phone number must be exactly 10 digits. Please try again.");
        }

        // Validate card number (14 digits)
        String cardNumber;
        while (true) {
            System.out.print("Enter library card number (14 digits): ");
            cardNumber = scanner.nextLine();
            if (cardNumber.matches("\\d{14}")) {
                break;
            }
            System.err.println("Error: Card number must be exactly 14 digits. Please try again.");
        }

        String query = "INSERT INTO member (name, phone_number, card_number) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, cardNumber);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int memberId = rs.getInt(1);
                    System.out.println("\nMember added successfully!");
                    System.out.println("Member ID: " + memberId);
                    System.out.println("Name: " + name);
                    System.out.println("Phone: " + phoneNumber);
                    System.out.println("Card Number: " + cardNumber);
                }
            } else {
                System.out.println("Failed to add member.");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("Error: Card number already exists.");
            } else {
                System.err.println("Error adding member: " + e.getMessage());
            }
        }
    }

    // View all members
    public static void viewAllMembers() {
        String query = "SELECT member_id, name, phone_number, card_number FROM member ORDER BY member_id";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== ALL MEMBERS ===");
            System.out.println(String.format("%-10s %-30s %-15s %-20s",
                "Member ID", "Name", "Phone Number", "Card Number"));
            System.out.println("=".repeat(80));

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int memberId = rs.getInt("member_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone_number");
                String cardNumber = rs.getString("card_number");

                System.out.println(String.format("%-10d %-30s %-15s %-20s",
                    memberId, name, phone, cardNumber));
            }

            if (!hasResults) {
                System.out.println("No members found.");
            }

        } catch (SQLException e) {
            System.err.println("Error viewing members: " + e.getMessage());
        }
    }

    // Search member by ID
    public static void searchMemberById(Scanner scanner) {
        System.out.print("\nEnter member ID: ");
        int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String query = "SELECT member_id, name, phone_number, card_number FROM member WHERE member_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n=== MEMBER DETAILS ===");
                System.out.println("Member ID: " + rs.getInt("member_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Phone: " + rs.getString("phone_number"));
                System.out.println("Card Number: " + rs.getString("card_number"));
            } else {
                System.out.println("Member not found.");
            }

        } catch (SQLException e) {
            System.err.println("Error searching member: " + e.getMessage());
        }
    }

    // Update member information
    public static void updateMember(Scanner scanner) {
        System.out.print("\nEnter member ID to update: ");
        int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // First check if member exists
        String checkQuery = "SELECT * FROM member WHERE member_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, memberId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Member not found.");
                return;
            }

            System.out.println("\nCurrent Details:");
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Phone: " + rs.getString("phone_number"));
            System.out.println("Card Number: " + rs.getString("card_number"));

            System.out.println("\nEnter new details (press Enter to keep current value):");

            System.out.print("New name: ");
            String newName = scanner.nextLine();
            if (newName.isEmpty()) newName = rs.getString("name");

            // Validate phone number (10 digits)
            String newPhone;
            while (true) {
                System.out.print("New phone number (10 digits): ");
                newPhone = scanner.nextLine();
                if (newPhone.isEmpty()) {
                    newPhone = rs.getString("phone_number");
                    break;
                }
                if (newPhone.matches("\\d{10}")) {
                    break;
                }
                System.err.println("Error: Phone number must be exactly 10 digits. Please try again.");
            }

            // Validate card number (14 digits)
            String newCardNumber;
            while (true) {
                System.out.print("New card number (14 digits): ");
                newCardNumber = scanner.nextLine();
                if (newCardNumber.isEmpty()) {
                    newCardNumber = rs.getString("card_number");
                    break;
                }
                if (newCardNumber.matches("\\d{14}")) {
                    break;
                }
                System.err.println("Error: Card number must be exactly 14 digits. Please try again.");
            }

            String updateQuery = "UPDATE member SET name = ?, phone_number = ?, card_number = ? WHERE member_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newName);
                updateStmt.setString(2, newPhone);
                updateStmt.setString(3, newCardNumber);
                updateStmt.setInt(4, memberId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("\nMember updated successfully!");
                } else {
                    System.out.println("Failed to update member.");
                }
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("Error: Card number already exists.");
            } else {
                System.err.println("Error updating member: " + e.getMessage());
            }
        }
    }

    // b. Track all books borrowed and returned
    public static void viewMemberLoanHistory(Scanner scanner) {
        System.out.print("\nEnter member ID: ");
        int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        QueryService.retrieveMemberBorrowingHistory(memberId);
    }

    // Delete member
    public static void deleteMember(Scanner scanner) {
        System.out.print("\nEnter member ID to delete: ");
        int memberId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String query = "DELETE FROM member WHERE member_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, memberId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Member deleted successfully.");
            } else {
                System.out.println("Member not found.");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                System.err.println("Error: Cannot delete member with existing loans. Return all books first.");
            } else {
                System.err.println("Error deleting member: " + e.getMessage());
            }
        }
    }
}
