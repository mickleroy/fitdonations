package au.com.shinetech.repository;

import au.com.shinetech.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Challenge entity.
 */
public interface ChallengeRepository extends JpaRepository<Challenge,Long>{

}
