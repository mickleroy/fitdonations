package au.com.shinetech.repository;

import au.com.shinetech.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Device entity.
 */
public interface DeviceRepository extends JpaRepository<Device,Long>{

}
