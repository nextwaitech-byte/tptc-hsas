package tz.go.tptc.hsas.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tz.go.tptc.hsas.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @Query("select distinct u from User u join fetch u.role left join fetch u.borderPost where u.active = true and u.deletedAt is null order by u.id")
    List<User> findByActiveTrueAndDeletedAtIsNullOrderByIdAsc();

    @Query("select u from User u join fetch u.role r left join fetch r.permissions left join fetch u.borderPost where u.email = :email and u.deletedAt is null")
    Optional<User> findWithRoleByEmail(String email);

    @Query("select u from User u join fetch u.role r left join fetch r.permissions left join fetch u.borderPost where u.id = :id")
    Optional<User> findWithRoleById(Integer id);
}
