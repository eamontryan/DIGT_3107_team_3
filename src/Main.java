import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("======================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("======================================");

        while (running) {
            displayMainMenu();
            int choice = getIntInput(scanner);

            switch (choice) {
                case 1:
                    queriesAndReportsMenu(scanner);
                    break;
                case 2:
                    memberManagementMenu(scanner);
                    break;
                case 3:
                    bookManagementMenu(scanner);
                    break;
                case 4:
                    loanManagementMenu(scanner);
                    break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using the Library Management System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n======================================");
        System.out.println("           MAIN MENU");
        System.out.println("======================================");
        System.out.println("1. Queries and Reports");
        System.out.println("2. Member Management");
        System.out.println("3. Book Management");
        System.out.println("4. Loan Management");
        System.out.println("0. Exit");
        System.out.println("======================================");
        System.out.print("Enter your choice: ");
    }

    private static void bookManagementMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n======================================");
            System.out.println("        BOOK MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. View all books");
            System.out.println("2. Add a new book");
            System.out.println("3. Check book availability");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(scanner);
            switch (choice) {
                case 1 -> BookService.viewAllBooks();
                case 2 -> {
                    scanner.nextLine(); // consume newline
                    BookService.addBook(scanner);
                }
                case 3 -> BookService.viewBookAvailability(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void loanManagementMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n======================================");
            System.out.println("        LOAN MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Borrow a book");
            System.out.println("2. Return a book");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(scanner);
            switch (choice) {
                case 1 -> LoanService.borrowBook(scanner);
                case 2 -> LoanService.returnBook(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void queriesAndReportsMenu(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("\n======================================");
            System.out.println("      QUERIES AND REPORTS");
            System.out.println("======================================");
            System.out.println("1. List overdue books and fines");
            System.out.println("2. Find most borrowed books");
            System.out.println("3. List currently borrowed books");
            System.out.println("4. Retrieve a member's borrowing history");
            System.out.println("5. Show books due soon (within 3 days)");
            System.out.println("6. Identify books never borrowed");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(scanner);

            switch (choice) {
                case 1:
                    QueryService.listOverdueBooksAndFines();
                    break;
                case 2:
                    QueryService.findMostBorrowedBooks();
                    break;
                case 3:
                    QueryService.listCurrentlyBorrowedBooks();
                    break;
                case 4:
                    System.out.print("\nEnter member ID: ");
                    int memberId = getIntInput(scanner);
                    QueryService.retrieveMemberBorrowingHistory(memberId);
                    break;
                case 5:
                    QueryService.showBooksDueSoon();
                    break;
                case 6:
                    QueryService.identifyNeverBorrowedBooks();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void memberManagementMenu(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("\n======================================");
            System.out.println("       MEMBER MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add new member");
            System.out.println("2. View all members");
            System.out.println("3. Search member by ID");
            System.out.println("4. Update member information");
            System.out.println("5. View member loan history");
            System.out.println("6. Delete member");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(scanner);

            switch (choice) {
                case 1:
                    scanner.nextLine(); // consume newline
                    MemberService.addMember(scanner);
                    break;
                case 2:
                    MemberService.viewAllMembers();
                    break;
                case 3:
                    MemberService.searchMemberById(scanner);
                    break;
                case 4:
                    MemberService.updateMember(scanner);
                    break;
                case 5:
                    MemberService.viewMemberLoanHistory(scanner);
                    break;
                case 6:
                    MemberService.deleteMember(scanner);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
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
