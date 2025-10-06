-- ======================================================
-- SAMPLE DATA INSERTS
-- ======================================================

-- ===== CATEGORY =====
INSERT INTO category (category_name) VALUES
('Science Fiction'),
('History'),
('Literature'),
('Biography'),
('Encyclopedia');

-- ===== TAG =====
INSERT INTO tag (tag_name) VALUES
('Space Travel'),
('Adventure'),
('Exploration'),
('Time Travel'),
('Quantum Physics'),
('Paradox'),
('Extraterrestrial Life'),
('UFOs'),
('Parallel Worlds'),
('Fantasy'),
('Colonization'),
('Mars'),
('Ancient Civilizations'),
('Rome'),
('Empire'),
('World War I'),
('World War II'),
('Renaissance'),
('Art'),
('Culture'),
('Political Leaders'),
('Revolution'),
('Architecture'),
('Wonders'),
('Classic Novel'),
('Romance'),
('Social Critique'),
('Justice'),
('Racial Inequality'),
('Dystopian'),
('Political Fiction'),
('Totalitarian'),
('Tragedy'),
('Social Class'),
('Nature');

-- ===== BOOK =====
INSERT INTO book (title, author, total_copies, available_copies, category_id) VALUES
('The Galactic Journey', 'John Doe', 5, 3, 1),
('Time and Space', 'Alice Smith', 5, 4, 1),
('Alien Encounters', 'Mark Johnson', 5, 2, 1),
('The Lost Universe', 'Emily Brown', 5, 5, 1),
('Mars Colonies', 'Robert Green', 5, 3, 1),
('The Ancient World', 'Sarah White', 5, 4, 2),
('World Wars', 'James Black', 5, 3, 2),
('The Renaissance', 'Laura Blue', 5, 2, 2),
('Revolutionary Leaders', 'David Wilson', 5, 4, 2),
('Historical Wonders', 'Susan Lee', 5, 3, 2),
('Pride and Prejudice', 'Jane Austen', 5, 4, 3),
('The Great Gatsby', 'F. Scott Fitzgerald', 5, 3, 3),
('To Kill a Mockingbird', 'Harper Lee', 5, 2, 3),
('1984', 'George Orwell', 5, 4, 3),
('Moby Dick', 'Herman Melville', 5, 3, 3);

-- ===== MEMBER =====
INSERT INTO member (name, phone_number, card_number) VALUES
('John Doe', '4165551111', '12345678900439'),
('Laura Smith', '4165552222', '12345678900440'),
('Catherine Lee', '4165553333', '12345678900441'),
('Daniel Kim', '4165554444', '12345678900442'),
('Eva Brown', '4165555555', '12345678900443'),
('Frank Wilson', '4165556666', '12345678900444'),
('Grace Miller', '4165557777', '12345678900445'),
('Henry Clark', '4165558888', '12345678900446');


-- ===== LOAN =====
INSERT INTO loan (member_id, borrow_date, due_date) VALUES
(1, '2025-09-01', '2025-09-15'),
(2, '2025-09-05', '2025-09-19'),
(3, '2025-09-10', '2025-09-24'),
(4, '2025-09-12', '2025-09-26'),
(5, '2025-09-15', '2025-09-29'),
(6, '2025-09-18', '2025-10-02'),
(7, '2025-09-20', '2025-10-04'),
(8, '2025-09-25', '2025-10-09');

-- ===== LOAN_ITEM =====
INSERT INTO loan_item (loan_id, book_id, return_date, fine_amount, status) VALUES
(1, 1, '2025-09-10', 0.00, 'returned'),
(1, 2, '2025-09-13', 0.00, 'returned'),
(2, 3, NULL, 0.00, 'borrowed'),
(2, 4, NULL, 0.00, 'borrowed'),
(3, 5, '2025-09-24', 1.50, 'returned'),
(4, 6, NULL, 0.00, 'borrowed'),
(5, 7, '2025-09-29', 0.00, 'returned'),
(6, 8, NULL, 0.00, 'borrowed'),
(7, 9, NULL, 0.00, 'borrowed'),
(8, 10, NULL, 0.00, 'borrowed'),
(8, 11, NULL, 0.00, 'borrowed');

-- ===== BOOK_TAG =====
INSERT INTO book_tag (book_id, tag_id) VALUES
-- Science Fiction
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5), (2, 6),
(3, 7), (3, 3), (3, 8),
(4, 9), (4, 3), (4, 10),
(5, 11), (5, 12), (5, 3),
-- History
(6, 13), (6, 14), (6, 15),
(7, 16), (7, 17), (7, 18),
(8, 18), (8, 19), (8, 20),
(9, 21), (9, 22), (9, 18),
(10, 23), (10, 24), (10, 13),
-- Literature
(11, 25), (11, 26), (11, 27),
(12, 25), (12, 33), (12, 34),
(13, 25), (13, 28), (13, 29),
(14, 30), (14, 31), (14, 32),
(15, 2), (15, 25), (15, 35);