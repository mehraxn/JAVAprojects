package librarymanagementsystem;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.addBook(new Book("978-1", "Clean Code", "Robert C. Martin"));
        library.addBook(new Book("978-2", "Effective Java", "Joshua Bloch"));
        library.addMember(new Member("M001", "Elena"));
        library.addMember(new Member("M002", "Noah"));

        library.borrowBook("978-2", "M001");
        System.out.println("Effective Java available after borrowing: "
                + library.getBook("978-2").isAvailable());

        System.out.println("Search results for 'java':");
        for (Book book : library.searchBooks("java")) {
            System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
        }

        library.returnBook("978-2", "M001");
        System.out.println("Available books after return: " + library.listAvailableBooks().size());
    }
}
