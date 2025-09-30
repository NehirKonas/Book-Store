-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
<<<<<<< HEAD
-- alter sequence myentity_seq restart with 4;
-- src/main/resources/import.sql
-- Add column for image URL
ALTER TABLE books ADD COLUMN cover_image_url VARCHAR(512);

-- Set image URL for I, Robot
UPDATE books 
SET cover_image_url = 'https://upload.wikimedia.org/wikipedia/en/2/2e/I_Robot_%28book%29.jpg'
WHERE title = 'I, Robot';

-- Authors
INSERT INTO authors (id, name) VALUES
  (1, 'Isaac Asimov'),
  (2, 'Stephen King'),
  (3, 'Antoine de Saint-Exupéry'),  
  (4,  'Ursula K. Le Guin'),
  (5,  'Orhan Pamuk'),
  (6,  'Gabriel García Márquez'),
  (7,  'Mary Shelley'),
  (8,  'Julia Child'),
  (9,  'Yuval Noah Harari'),
  (10, 'Stephen Hawking'),
  (11, 'Nazım Hikmet'),
  (12, 'Franz Kafka'),
  (13, 'Jorge Luis Borges'),
  (14, 'Frank Herbert'),
  (15, 'Arthur Conan Doyle'),
  (18, 'George Orwell'),
  (19, 'H. P. Lovecraft'),
  (20, 'Sun Tzu'),
  (22, 'H. G. Wells'),
  (23, 'Richard Dawkins')
ON DUPLICATE KEY UPDATE name = VALUES(name);
-- Publishers
INSERT INTO publishers (id, name) VALUES
  (1, 'Penguin Random House'),
  (2, 'HarperCollins'),
  (3, 'Gallimard'),  
  (4,  'Ace Books'),
  (5,  'İletişim Yayınları'),
  (6,  'Vintage'),
  (7,  'Knopf'),
  (9,  'Oxford University Press'),
  (10, 'Bantam Books'),
  (11, 'Penguin Classics'),
  (13, 'Dover Publications')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Books (match your enum strings & column names exactly)
INSERT INTO books
(title, format, language, genre, publish_date, authorId, price, page_number, isbn, stock, publisher_id)
VALUES
('I, Robot', 'PHYSICAL', 'ENGLISH', 'SCIFI', '1950-12-02', 1, 10.99, 224, '9780553382563', 8, 1),
('The Left Hand of Darkness', 'PHYSICAL', 'ENGLISH', 'SCIFI', '1969-03-01', 4, 11.99, 304, '9780441478125', 6, 4),
('Frankenstein', 'EBOOK', 'ENGLISH', 'HORROR', '1818-01-01', 7, 5.99, 280, '9780486282114', 12, 6),
('Mastering the Art of French Cooking', 'PHYSICAL', 'ENGLISH', 'COOKING', '1961-10-16', 8, 18.99, 684, '9780375413407', 4, 7),
('Sapiens', 'PHYSICAL', 'ENGLISH', 'NONFICTIONAL', '2011-01-01', 9, 14.50, 498, '9780099590088', 10, 6),
('A Brief History of Time', 'EBOOK', 'ENGLISH', 'NONFICTIONAL', '1988-04-01', 10, 8.99, 256, '9780553380163', 9, 10),
('The Metamorphosis', 'PHYSICAL', 'GERMAN', 'SHORTSTORY', '1915-10-01', 12, 6.50, 102, '9780553213690', 5, 11),
('Labyrinths', 'PHYSICAL', 'SPANISH', 'SHORTSTORY', '1962-01-01', 13, 9.50, 256, '9780811200127', 3, 6),
('Dune', 'PHYSICAL', 'ENGLISH', 'SCIFI', '1965-08-01', 14, 13.99, 688, '9780441172719', 7, 4),
('The Hound of the Baskervilles', 'EBOOK', 'ENGLISH', 'HORROR', '1902-04-01', 15, 4.99, 256, '9780451528018', 9, 11),
('Istanbul: Memories and the City', 'AUDIOBOOK', 'TURKISH', 'HISTORY', '2003-01-01', 5, 7.99, 368, '9789992150001', 2, 5),
('One Hundred Years of Solitude', 'PHYSICAL', 'SPANISH', 'HISTORY', '1967-05-30', 6, 12.50, 417, '9780060883287', 6, 2),
('Selected Poems', 'PHYSICAL', 'TURKISH', 'POETRY', '1960-01-01', 11, 6.99, 320, '9789750800001', 8, 5),
('1984', 'EBOOK', 'ENGLISH', 'SCIFI', '1949-06-08', 18, 6.99, 328, '9780451524935', 11, 11),
('The Art of War', 'PHYSICAL', 'ENGLISH', 'HISTORY', '1910-01-01', 20, 4.50, 112, '9780195014761', 10, 13),
('The Time Machine', 'PHYSICAL', 'ENGLISH', 'SCIFI', '1895-05-07', 22, 5.99, 160, '9780451528551', 7, 11),
('The Call of Cthulhu', 'EBOOK', 'ENGLISH', 'HORROR', '1928-02-01', 19, 3.99, 64, '9780141182346', 9, 11),
('The Selfish Gene', 'PHYSICAL', 'ENGLISH', 'NONFICTIONAL', '1976-03-01', 23, 12.99, 360, '9780198788607', 6, 9),
('The Black Book', 'EBOOK', 'TURKISH', 'HISTORY', '1990-01-01', 5, 8.50, 480, '9789754700116', 5, 5),
('The Dispossessed', 'PHYSICAL', 'ENGLISH', 'SCIFI', '1974-05-01', 4, 11.50, 400, '9780060512750', 6, 4);
INSERT INTO customers (
  birth_date, disc, cart_id, address, email, first_name, last_name, password, phone
) VALUES (
  '1998-04-12', 0, NULL,
  'İstiklal Cd. No:123, Beyoğlu/İstanbul',
  'jane.doe@example.com',
  'Jane', 'Doe',
  'secret123',               -- (store a hash in real apps)
  '+90 555 123 45 67'
);
=======
-- alter sequence myentity_seq restart with 4;
>>>>>>> e404ae3d3e21d67a50377e7cad7e4eb6ff0c1993
