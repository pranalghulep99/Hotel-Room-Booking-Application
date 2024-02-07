package com.project.lakesidehotels.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialException;

import org.springframework.web.multipart.MultipartFile;

import com.project.lakesidehotels.entities.Room;
import com.project.lakesidehotels.exception.InternalServerException;

public interface IRoomService {

	Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice)
			throws IOException, SerialException, SQLException;

	List<String> getAllRoomTypes();

	List<Room> getAllRooms();

	byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

	void deleteRoom(Long roomId);

	Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes)
			throws InternalServerException;

	Optional<Room> getRoomById(Long roomId);

	List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

}
