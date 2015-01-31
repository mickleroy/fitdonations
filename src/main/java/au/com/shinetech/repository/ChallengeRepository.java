package au.com.shinetech.repository;

import au.com.shinetech.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the Challenge entity.
 */
public interface ChallengeRepository extends JpaRepository<Challenge,Long>{
    List<Challenge> findByUserLoginAndFinishedOrderByStartDateDesc(String login, boolean finished);
    
    @Query("select c from Challenge c where c.startDate < now() and c.endDate >= now()")
    List<Challenge> findAllOngoing();
    
    @Query("select c from Challenge c where c.endDate < now() and c.finished = false")
    List<Challenge> findAllForProcessing();
}
