/*
Student#: 220274536
Sur (Family) Name: Markham
Given Name: Sienna
Section: C/Phase 3

Student#: 219447192
Sur (Family) Name: Ryan
Given Name: Eamon
Section: C/Phase 3

Student#: 220416038
Sur (Family) Name: Mollah
Given Name: Mahjabin
Section: C/Phase 3
*/

USE Library_Management;

-- Insert categories
INSERT INTO category (category_name) VALUES
('Science Fiction'),
('History'),
('Literature'),
('Biography'),
('Children'),
('Technology');

-- Insert tags
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
('Mars');

-- Insert books
INSERT INTO book (title, total_copies, available_copies, category_id) VALUES
('The Galactic Journey', 5, 3, 1),
('Time and Space', 4, 2, 1),
('Alien Encounters', 6, 4, 1),
('The Lost Universe', 5, 3, 1),
('Mars Colonies', 3, 2, 1),
('The Ancient World', 4, 4, 2),
('World Wars', 6, 6, 2),
('The Renaissance', 3, 3, 2),
('Historical Wonders', 5, 5, 2),
('Pride and Prejudice', 8, 8, 3),
('The Great Gatsby', 7, 7, 3),
('To Kill a Mockingbird', 6, 6, 3),
('1984', 6, 6, 3),
('Moby Dick', 4, 4, 3);

-- Insert members
INSERT INTO member (name, phone_number, card_number) VALUES
('Laura Smith', '4165552222', '111111111'),
('Daniel Kim', '4165554444', '222222222'),
('Frank Wilson', '4165556666', '333333333'),
('Grace Miller', '4165557777', '444444444'),
('Henry Clark', '4165558888', '555555555'),
('Catherine Lee', '4165559999', '666666666');

-- Insert loan records
INSERT INTO loan (member_id, borrow_date, due_date) VALUES
(1, '2025-09-01', '2025-09-15'),
(2, '2025-09-05', '2025-09-19'),
(3, '2025-09-10', '2025-09-24'),
(4, '2025-09-12', '2025-09-26'),
(5, '2025-09-15', '2025-09-29'),
(6, '2025-09-18', '2025-10-02'),
(1, '2025-09-20', '2025-10-04'),
(2, '2025-09-25', '2025-10-09');

-- Insert loan items (demo fine amounts in clean increments)
INSERT INTO loan_item (loan_id, book_id, return_date, fine_amount, status) VALUES
(1, 1, '2025-09-10', 430, 'borrowed'),
(2, 2, NULL,         360, 'borrowed'),
(3, 3, NULL,         300, 'borrowed'),
(4, 4, NULL,         280, 'borrowed'),
(5, 5, NULL,         230, 'borrowed'),
(6, 6, '2025-10-01', 20,  'returned'),
(7, 1, NULL,         430, 'borrowed'),
(8, 5, NULL,         230, 'borrowed');

-- Insert book tags
INSERT INTO book_tag (book_id, tag_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5), (2, 6),
(3, 3), (3, 7), (3, 8),
(4, 3), (4, 9), (4, 10),
(5, 3), (5, 11), (5, 12);
