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

-- 1. List overdue books and associated fines [cite: 18]
SELECT
    b.title AS book_title, [cite: 19]
    m.name AS borrower, [cite: 20]
    l.due_date, [cite: 21]
    DATEDIFF(CURDATE(), l.due_date) AS days_overdue, [cite: 22]
    li.fine_amount [cite: 23]
FROM loan_item li [cite: 24]
JOIN loan l ON li.loan_id = l.loan_id [cite: 25]
JOIN member m ON l.member_id = m.member_id [cite: 26]
JOIN book b ON li.book_id = b.book_id [cite: 27]
WHERE li.status = 'borrowed' AND l.due_date < CURDATE(); [cite: 28]

-- 2. Find most borrowed books [cite: 29]
SELECT
    b.title AS book_title, [cite: 31]
    COUNT(li.book_id) AS total_borrow_count [cite: 32]
FROM loan_item li [cite: 33]
JOIN book b ON li.book_id = b.book_id [cite: 34]
GROUP BY b.book_id [cite: 35]
ORDER BY total_borrow_count DESC [cite: 36]
LIMIT 10; [cite: 36]

-- 3. Get borrowing history of a specific member [cite: 37]
SELECT
    b.title AS book_title, [cite: 38]
    l.borrow_date, [cite: 39]
    l.due_date, [cite: 40]
    li.return_date, [cite: 41]
    li.status [cite: 42]
FROM loan l [cite: 43]
JOIN loan_item li ON l.loan_id = li.loan_id [cite: 44]
JOIN book b ON li.book_id = b.book_id [cite: 45]
WHERE l.member_id = ? -- replace ? with the specific member_id [cite: 46]
ORDER BY l.borrow_date DESC; [cite: 46]

-- 4. List books currently borrowed and due soon (3 days) [cite: 47]
SELECT
    b.title AS book_title, [cite: 48]
    m.name AS borrower, [cite: 49]
    l.due_date [cite: 50]
FROM loan_item li [cite: 51]
JOIN loan l ON li.loan_id = l.loan_id [cite: 52]
JOIN member m ON l.member_id = m.member_id [cite: 53]
JOIN book b ON li.book_id = b.book_id [cite: 54]
WHERE li.status = 'borrowed' [cite: 55]
    AND l.due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 DAY) [cite: 56, 57]
ORDER BY l.due_date; [cite: 58]

-- 5. Identify books never borrowed [cite: 59]
SELECT
    b.book_id, [cite: 61]
    b.title [cite: 62]
FROM book b [cite: 63]
LEFT JOIN loan_item li ON b.book_id = li.book_id [cite: 64]
WHERE li.book_id IS NULL; [cite: 65]

-- 6. Show availability and circulation status of a specific book [cite: 66]
SELECT
    b.title, [cite: 68]
    b.total_copies, [cite: 69]
    b.available_copies, [cite: 70]
    (b.total_copies - b.available_copies) AS borrowed_copies, [cite: 71]
    GROUP_CONCAT(DISTINCT l.due_date ORDER BY l.due_date SEPARATOR ', ') AS expected_return_dates [cite: 72, 73]
FROM book b [cite: 74]
LEFT JOIN loan_item li ON b.book_id = li.book_id AND li.status = 'borrowed' [cite: 75]
LEFT JOIN loan l ON li.loan_id = l.loan_id [cite: 76]
WHERE b.book_id = ? -- replace ? with the book ID [cite: 77]
GROUP BY b.book_id; [cite: 78]

-- 7. Get total fines per member [cite: 79]
SELECT
    m.name AS member_name, [cite: 81]
    SUM(li.fine_amount) AS total_fines [cite: 82]
FROM loan_item li [cite: 83]
JOIN loan l ON li.loan_id = l.loan_id [cite: 84]
JOIN member m ON l.member_id = m.member_id [cite: 85]
GROUP BY m.member_id [cite: 86]
HAVING total_fines > 0 [cite: 87]
ORDER BY total_fines DESC; [cite: 88]

-- 8. Find members who currently have overdue books [cite: 89]
SELECT DISTINCT
    m.member_id, [cite: 91]
    m.name, [cite: 92]
    m.phone_number [cite: 93]
FROM member m [cite: 94]
JOIN loan l ON m.member_id = l.member_id [cite: 95]
JOIN loan_item li ON l.loan_id = li.loan_id [cite: 96]
WHERE li.status = 'borrowed' AND l.due_date < CURDATE(); [cite: 97]

-- 9. List books tagged under a specific category with their tags [cite: 98]
SELECT
    b.title AS book_title, [cite: 99]
    c.category_name, [cite: 100]
    GROUP_CONCAT(t.tag_name SEPARATOR ', ') AS tags [cite: 101]
FROM book b [cite: 102]
LEFT JOIN category c ON b.category_id = c.category_id [cite: 103]
LEFT JOIN book_tag bt ON b.book_id = bt.book_id [cite: 103]
LEFT JOIN tag t ON bt.tag_id = t.tag_id [cite: 104]
WHERE c.category_name = ? -- e.g. 'Science Fiction' [cite: 105]
GROUP BY b.book_id; [cite: 105]