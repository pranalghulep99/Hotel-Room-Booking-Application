package com.project.lakesidehotels.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.lakesidehotels.dto.BookingResponseDTO;
import com.project.lakesidehotels.dto.RoomResponseDTO;
import com.project.lakesidehotels.entities.BookedRoom;
import com.project.lakesidehotels.entities.Room;
import com.project.lakesidehotels.exception.InternalServerException;
import com.project.lakesidehotels.exception.PhotoRetrievingException;
import com.project.lakesidehotels.exception.ResourceNotFoundException;
import com.project.lakesidehotels.service.IBookingService;
import com.project.lakesidehotels.service.IRoomService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

	private final IRoomService roomService;
	
	private final IBookingService bookingService;

	/* to add new room to hotel */
	@PostMapping("/add/new-room")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoomResponseDTO> addNewRoom(@RequestParam("photo") MultipartFile photo,
			@RequestParam("roomType") String roomType, @RequestParam("roomPrice") BigDecimal roomPrice)
			throws SerialException, IOException, SQLException {
		Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
		RoomResponseDTO roomResponse = new RoomResponseDTO(savedRoom.getId(), savedRoom.getRoomType(),
				savedRoom.getRoomPrice());
		return ResponseEntity.ok(roomResponse);
	}

	/* to get all distinct types of rooms in hotel */
	@GetMapping("/room/types")
	public List<String> getRoomTypes() {
		return roomService.getAllRoomTypes();
	}

	/**
	 * to get all the rooms from the hotel along with previous booking details for
	 * each room
	 */
	/**
	 * 1) roomService.getAllRooms();=>getting All the rooms from the database
	 */
	/**
	 * 2) roomService.getRoomPhotoByRoomId(room.getId()); => Getting photo of each
	 * room by roomId then converting blob to string
	 */
	/**
	 * 3) getRoomResponse(room); => retrieving all previous booking for each room
	 */
	@GetMapping("/all-rooms")
	public ResponseEntity<List<RoomResponseDTO>> getAllRooms() throws SQLException, PhotoRetrievingException {
		List<Room> rooms = roomService.getAllRooms();
		List<RoomResponseDTO> roomResponseDTO = new ArrayList<>();
		for (Room room : rooms) {
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
			if (photoBytes != null && photoBytes.length > 0) {
				String base64Photo = Base64.encodeBase64String(photoBytes);
				RoomResponseDTO roomResponse = getRoomResponse(room);
				roomResponse.setPhoto(base64Photo);
				roomResponseDTO.add(roomResponse);
			}
		}
		return ResponseEntity.ok(roomResponseDTO);
	}

	/* Delete room by roomId */
	@DeleteMapping("/delete/room/{roomId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId) {
		roomService.deleteRoom(roomId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/** updating room by @roomId and @return RoomResponseDTO */
	@PutMapping("/update/{roomId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Long roomId, @RequestParam(required = false) String roomType,
			@RequestParam(required = false) BigDecimal roomPrice, @RequestParam(required = false) MultipartFile photo)
			throws IOException, SQLException, InternalServerException {
		byte[] photoBytes = (photo != null) && (!photo.isEmpty()) ? photo.getBytes()
				: roomService.getRoomPhotoByRoomId(roomId);
		Blob photBlob = (photoBytes != null) && (photoBytes.length > 0) ? new SerialBlob(photoBytes) : null;
		Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
		theRoom.setPhoto(photBlob);
		RoomResponseDTO roomResponseDTO = getRoomResponse(theRoom);
		return ResponseEntity.ok(roomResponseDTO);
	}

	/** getting room by roomId */
	@GetMapping("room/{roomId}")
	public ResponseEntity<Optional<RoomResponseDTO>> getRoomById(@PathVariable Long roomId) {
		Optional<Room> theRoom = roomService.getRoomById(roomId);
		return theRoom.map(room -> {
			RoomResponseDTO roomResponseDTO = getRoomResponse(room);
			return ResponseEntity.ok(Optional.of(roomResponseDTO));
		}).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
	}

	/**
	 * Method to get available room based on check-in and check-out date
	 * 
	 * @param The ISO date format is of the form "yyyy-MM-dd" (e.g., "2024-01-17").
	 * @throws SQLException
	 */
	@GetMapping("/available-rooms")
	public ResponseEntity<List<RoomResponseDTO>> getAvailableRooms(
			@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
			@RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
			@RequestParam("roomType") String roomType) throws SQLException {

		List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
		List<RoomResponseDTO> roomResponseDTOs = new ArrayList<>();
		for (Room room : availableRooms) {
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
			if (photoBytes != null && photoBytes.length > 0) {
				String photoBase64 = Base64.encodeBase64String(photoBytes);
				RoomResponseDTO roomResponse = getRoomResponse(room);
				roomResponse.setPhoto(photoBase64);
				roomResponseDTOs.add(roomResponse);
			}
		}
		if (roomResponseDTOs.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(roomResponseDTOs);
		}
	}

	private RoomResponseDTO getRoomResponse(Room room) throws PhotoRetrievingException {
		List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
		List<BookingResponseDTO> bookingInfo = bookings.stream()
				.map(booking -> new BookingResponseDTO(booking.getBookingId(), booking.getCheckInDate(),
						booking.getCheckOutDate(), booking.getBookingConfirmationCode()))
				.toList();
		byte[] photoBytes = null;
		Blob photoBlob = room.getPhoto();
		if (photoBlob != null) {
			try {
				photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
			} catch (Exception e) {
				throw new PhotoRetrievingException("Error retrieving photo");
			}
		}
		return new RoomResponseDTO(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBytes,
				bookingInfo);
	}

	private List<BookedRoom> getAllBookingsByRoomId(long roomId) {
		return bookingService.getAllBookingsByRoomId(roomId);
	}
}