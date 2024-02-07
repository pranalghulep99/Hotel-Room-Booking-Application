package com.project.lakesidehotels.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.lakesidehotels.dto.BookingResponseDTO;
import com.project.lakesidehotels.dto.RoomResponseDTO;
import com.project.lakesidehotels.entities.BookedRoom;
import com.project.lakesidehotels.entities.Room;
import com.project.lakesidehotels.exception.InvalidBookingRequestException;
import com.project.lakesidehotels.exception.ResourceNotFoundException;
import com.project.lakesidehotels.service.IBookingService;
import com.project.lakesidehotels.service.IRoomService;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/bookings")
public class BookingController {

	@Autowired
	private IBookingService bookingService;

	@Autowired
	private IRoomService roomService;

	/** to get all bookings from the database */
	@GetMapping("/all-bookings")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
		List<BookedRoom> bookings = bookingService.getAllBookings();
		List<BookingResponseDTO> bookingList = new ArrayList<>();
		for (BookedRoom booking : bookings) {
			BookingResponseDTO bookingResponse = getBookingResponse(booking);
			bookingList.add(bookingResponse);
		}
		return ResponseEntity.ok(bookingList);
	}

	
	
	
	/**
	 * get booking by confirmation code 1) Getting booking details in Booked room
	 * 
	 * 2) if(found) => Returning BookingResponseDTO
	 * 
	 * else => return HttpStatus.NOT_FOUND with error message
	 */
	@GetMapping("/confirmation/{confirmationCode}")
	public ResponseEntity<?> getBookingByConfirmationCode(
			@PathVariable("confirmationCode") String bookingConfirmationCode) {
		try {
			BookedRoom booking = bookingService.getBookingByConfirmationCode(bookingConfirmationCode);
			BookingResponseDTO bookingResponseDTO = getBookingResponse(booking);
			return ResponseEntity.ok(bookingResponseDTO);
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	
	
	
	/** Save new booking to database */
	@PostMapping("/room/{roomId}/booking")
	public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest) {
		try {
			String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
			return ResponseEntity.ok("Room booked successfully, your confirmation code : " + confirmationCode);
		} catch (InvalidBookingRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	
	
	
	/** to cancel booking */
	@DeleteMapping("/booking/{bookingId}/delete")
	public void cancelBooking(@PathVariable Long bookingId) {
		bookingService.cancelBooking(bookingId);
	}

	
	 @GetMapping("/user/{email}/bookings")
	    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUserEmail(@PathVariable String email) {
	        List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
	        List<BookingResponseDTO> bookingResponses = new ArrayList<>();
	        for (BookedRoom booking : bookings) {
	            BookingResponseDTO bookingResponse = getBookingResponse(booking);
	            bookingResponses.add(bookingResponse);
	        }
	        return ResponseEntity.ok(bookingResponses);
	    }

	
	/**
	 * The result of getRoomById(...) is wrapped in an optional (indicated by the
	 * .get() method),
	 * 
	 * 1) Getting room by booking.roomId
	 * 
	 * 2) creating new RoomResponseDTO by filling room details
	 * 
	 * 3) returning new BookingResponseDTO including { booking details +
	 * RoomResponseDTO }
	 */
	private BookingResponseDTO getBookingResponse(BookedRoom booking) {
		Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
		RoomResponseDTO roomResponseDTO = new RoomResponseDTO(theRoom.getId(), theRoom.getRoomType(),
				theRoom.getRoomPrice());

		return new BookingResponseDTO(booking.getBookingId(), booking.getCheckInDate(), booking.getCheckOutDate(),
				booking.getGuestFullName(), booking.getGuestEmail(), booking.getNumOfAdults(),
				booking.getNumOfChildren(), booking.getTotalNumOfGuest(), booking.getBookingConfirmationCode(),
				roomResponseDTO);
	}
}
