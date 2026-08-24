package io.github.devup.tripfinder.accommodation.repository;

import io.github.devup.tripfinder.accommodation.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

}
