package com.renet.cvbackend.roleprofile;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleProfileRepository extends JpaRepository<RoleProfile, Long> {

	List<RoleProfile> findAllByOrderBySortOrderAscIdAsc();

	Optional<RoleProfile> findBySlug(String slug);

	boolean existsBySlug(String slug);

	boolean existsBySlugAndIdNot(String slug, Long id);

	boolean existsByType(RoleProfileType type);

	boolean existsByTypeAndIdNot(RoleProfileType type, Long id);

}
