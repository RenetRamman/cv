package com.renet.cvbackend.introduction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {

	List<Introduction> findAllByOrderByIdAsc();

	Optional<Introduction> findByRoleProfileId(Long roleProfileId);

	boolean existsByRoleProfileId(Long roleProfileId);

	boolean existsByRoleProfileIdAndIdNot(Long roleProfileId, Long id);

}
