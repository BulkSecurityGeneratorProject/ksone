package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the TimeSlot entity.
 */
public interface TimeSlotTypeRepository extends JpaRepository<TimeSlotType,Long> {

	List<TimeSlotType> findAllByBox(CrossFitBox box);

}
