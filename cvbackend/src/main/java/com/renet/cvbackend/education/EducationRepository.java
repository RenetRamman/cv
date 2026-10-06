package com.renet.cvbackend.education;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<Education, Long> {

	List<Education> findAllByOrderByStartDateDescIdDesc();

}
