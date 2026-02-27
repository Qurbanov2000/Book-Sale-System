package booksale.service;

import booksale.dto.request.BookRequest;
import booksale.dto.response.BookResponse;
import booksale.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import booksale.repo.BookRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;

    public BookResponse addBook(BookRequest request) {
        if (request.getIsbn() != null && bookRepo.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Bu ISBN artıq mövcuddur");
        }

        Book book = mapToEntity(request);
        Book saved = bookRepo.save(book);
        return mapToResponse(saved);
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Kitab tapılmadı"));
        return mapToResponse(book);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Kitab tapılmadı"));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setImageUrl(request.getImageUrl());

        Book updated = bookRepo.save(book);
        return mapToResponse(updated);
    }

    public void deleteBook(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new RuntimeException("Kitab tapılmadı");
        }
        bookRepo.deleteById(id);
    }

    private Book mapToEntity(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setImageUrl(request.getImageUrl());
        return book;
    }

    private BookResponse mapToResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getPrice(),
                book.getStock(),
                book.getImageUrl()
        );
    }
}
