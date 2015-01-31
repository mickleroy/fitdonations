package au.com.shinetech.repository;

import au.com.shinetech.domain.Charity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Charity entity.
 */
public interface CharityRepository extends JpaRepository<Charity,Long>{

}
