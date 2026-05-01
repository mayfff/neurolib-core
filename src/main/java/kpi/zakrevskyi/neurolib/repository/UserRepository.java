package kpi.zakrevskyi.neurolib.repository;

import java.util.Optional;
import java.util.UUID;
import kpi.zakrevskyi.neurolib.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_liked_books WHERE user_id = :userId", nativeQuery = true)
    void deleteLikedBooksByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_disliked_books WHERE user_id = :userId", nativeQuery = true)
    void deleteDislikedBooksByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_saved_books WHERE user_id = :userId", nativeQuery = true)
    void deleteSavedBooksByUserId(@Param("userId") UUID userId);
}
