package com.project.lakesidehotels.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.lakesidehotels.entities.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	@Query("SELECT DISTINCT r.roomType FROM Room r")
	List<String> findDistinctRoomTypes();

	@Query("SELECT r FROM Room r " +
		       "WHERE r.roomType LIKE %:roomType% " +
		       "AND r.id NOT IN ("
		       + "SELECT br.room.id FROM BookedRoom br "
		       + "WHERE ((:checkInDate <= br.checkOutDate) AND (:checkOutDate >= br.checkInDate))"
		       + ")")
	List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

}
