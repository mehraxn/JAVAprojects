# Library Management System

## Description

Library Management System is an in-memory Java project for tracking books, members, borrowing, and returns. It focuses on keeping related Book and Member state consistent.

## Features

- Add books with unique ISBN values.
- Register members with unique IDs.
- Borrow available books.
- Return books through the member who borrowed them.
- Prevent double borrowing and incorrect returns.
- Search by partial title or author, case-insensitively.
- List currently available books.

## Java concepts practiced

- Object relationships and encapsulation
- Map, Set, and List collections
- Coordinating state across multiple objects
- Validation and state exceptions
- Searching, sorting, and defensive collections

## Main classes

- Book: stores bibliographic information and current borrower.
- Member: stores member identity and borrowed ISBN values.
- Library: manages books, members, borrowing, returns, and searches.
- Main: demonstrates the library workflow.

## How the program works

Books and members are registered with Library. A borrow operation verifies the ISBN, member ID, and availability before updating both objects. A return operation verifies ownership before clearing both records.

## Example usage

~~~powershell
javac -d out src\librarymanagementsystem\*.java
java -cp out librarymanagementsystem.Main
~~~

The demo borrows a book, searches the catalogue, returns the book, and prints availability.

## Possible future improvements

- Add borrowing limits and due dates.
- Track borrowing history.
- Add overdue-fee calculations.
- Add file persistence.
- Add an interactive catalogue menu.
