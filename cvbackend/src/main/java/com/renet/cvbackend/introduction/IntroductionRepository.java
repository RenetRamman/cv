package com.renet.cvbackend.introduction;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {

	List<Introduction> findAllByOrderByIdAsc();

	boolean existsByRoleProfileId(Long roleProfileId);

	boolean existsByRoleProfileIdAndIdNot(Long roleProfileId, Long id);

}
