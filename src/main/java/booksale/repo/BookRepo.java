package booksale.repo;

import booksale.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author);

    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Book> findByStockGreaterThan(Integer stock);

    @Query("SELECT b FROM Book b WHERE " +
            "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR b.stock > 0)")
    List<Book> searchBooks(
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock);
}
}
