package com.project.lakesidehotels.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.lakesidehotels.entities.BookedRoom;

@Repository
public interface BookingRepository extends JpaRepository<BookedRoom, Long>{

	List<BookedRoom> findByRoomId(long roomId);

	Optional<BookedRoom> findByBookingConfirmationCode(String bookingConfirmationCode);

	List<BookedRoom> findByGuestEmail(String email);

}
