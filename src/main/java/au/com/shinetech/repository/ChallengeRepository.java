package au.com.shinetech.repository;

import au.com.shinetech.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Challenge entity.
 */
public interface ChallengeRepository extends JpaRepository<Challenge,Long>{
    List<Challenge> findByUserLoginAndFinishedOrderByStartDateDesc(String login, boolean finished);
}
