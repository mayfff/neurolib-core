package kpi.zakrevskyi.neurolib.repository;

import kpi.zakrevskyi.neurolib.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_liked_books WHERE book_id = :bookId", nativeQuery = true)
    void deleteLikesByBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_disliked_books WHERE book_id = :bookId", nativeQuery = true)
    void deleteDislikesByBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_saved_books WHERE book_id = :bookId", nativeQuery = true)
    void deleteSavesByBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM book_authors WHERE book_id = :bookId", nativeQuery = true)
    void deleteBookAuthorsByBookId(@Param("bookId") UUID bookId);
}
