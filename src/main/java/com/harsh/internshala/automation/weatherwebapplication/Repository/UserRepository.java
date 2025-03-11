package com.harsh.internshala.automation.weatherwebapplication.Repository;
import com.harsh.internshala.automation.weatherwebapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
}
