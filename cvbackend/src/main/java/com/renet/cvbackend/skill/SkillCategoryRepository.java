package com.renet.cvbackend.skill;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

	List<SkillCategory> findAllByOrderByIdAsc();

}
