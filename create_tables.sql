CREATE DATABASE IF NOT EXISTS Library_Management;
USE Library_Management;

-- =========================
-- Create Database User
-- =========================
-- Create user with a strong random password
CREATE USER IF NOT EXISTS 'test_user'@'localhost' IDENTIFIED BY 'Lb9mK$2pQxR7wN4';

-- Grant necessary privileges on Library_Management database
GRANT SELECT, INSERT, UPDATE, DELETE ON Library_Management.* TO 'test_user'@'localhost';

-- Apply privilege changes
FLUSH PRIVILEGES;

-- Drop tables if they already exist
DROP TABLE IF EXISTS book_tag;
DROP TABLE IF EXISTS loan_item;
DROP TABLE IF EXISTS loan;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS member;

-- =========================
-- Table: category
-- =========================
CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(30) NOT NULL
);

-- =========================
-- Table: tag
-- =========================
CREATE TABLE tag (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(100) NOT NULL
);

-- =========================
-- Table: book
-- =========================
CREATE TABLE book (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    author VARCHAR(50) NOT NULL,
    total_copies INT UNSIGNED NOT NULL,
    available_copies INT UNSIGNED NOT NULL,
    category_id INT,
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES category(category_id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- =========================
-- Table: member
-- =========================
CREATE TABLE member (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    card_number VARCHAR(14) NOT NULL UNIQUE
);

-- =========================
-- Table: loan (represents one checkout/transaction)
-- =========================
CREATE TABLE loan (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    CONSTRAINT fk_loan_member FOREIGN KEY (member_id) REFERENCES member(member_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- =========================
-- Table: loan_item (books in a loan)
-- =========================
CREATE TABLE loan_item (
    loan_id INT NOT NULL,
    book_id INT NOT NULL,
    return_date DATE,
    fine_amount DECIMAL(6,2) DEFAULT 0.00 CHECK (fine_amount >= 0),
    status ENUM('borrowed', 'returned', 'lost') NOT NULL,
    PRIMARY KEY (loan_id, book_id),
    CONSTRAINT fk_loanitem_loan FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_loanitem_book FOREIGN KEY (book_id) REFERENCES book(book_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- =========================
-- Table: book_tag (associative entity)
-- =========================
CREATE TABLE book_tag (
    book_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (book_id, tag_id),
    CONSTRAINT fk_booktag_book FOREIGN KEY (book_id) REFERENCES book(book_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_booktag_tag FOREIGN KEY (tag_id) REFERENCES tag(tag_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);