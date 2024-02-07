package com.project.lakesidehotels.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.lakesidehotels.entities.BookedRoom;
import com.project.lakesidehotels.entities.Room;
import com.project.lakesidehotels.exception.InvalidBookingRequestException;
import com.project.lakesidehotels.exception.ResourceNotFoundException;
import com.project.lakesidehotels.repository.BookingRepository;

@Service
public class BookingService implements IBookingService {

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private IRoomService roomService;

	@Override
	public List<BookedRoom> getAllBookings() {
		return bookingRepository.findAll();
	}

	@Override
	public List<BookedRoom> getAllBookingsByRoomId(long roomId) {
		return bookingRepository.findByRoomId(roomId);
	}

	@Override
	public BookedRoom getBookingByConfirmationCode(String bookingConfirmationCode) {
		return bookingRepository.findByBookingConfirmationCode(bookingConfirmationCode)
				.orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code :"+bookingConfirmationCode));
	}

	 @Override
	    public List<BookedRoom> getBookingsByUserEmail(String email) {
	        return bookingRepository.findByGuestEmail(email);
	    }

	/**
	 * check-out date should not be before than check-in date => else throw
	 * exception
	 * 
	 * get room by roomId => get all bookings of same room to check Availability for
	 * current booking
	 * 
	 * if(Available) => add this booking to rooms's booking list => then save
	 * booking to database
	 * 
	 * @return bookingConfirmationCode
	 * 
	 *         if(!Available) => throw exception
	 */
	@Override
	public String saveBooking(Long roomId, BookedRoom bookingRequest) {
		if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
			throw new InvalidBookingRequestException("Check-in date must come before check-out date");
		}
		Room room = roomService.getRoomById(roomId).get();
		List<BookedRoom> existingBookigs = room.getBookings();
		boolean isRoomAvailable = checkRoomAvailability(bookingRequest, existingBookigs);
		if (isRoomAvailable) {
			room.addBooking(bookingRequest);
			bookingRepository.save(bookingRequest);
		} else {
			throw new InvalidBookingRequestException("Sorry, This room is not available for the selected dates");
		}
		return bookingRequest.getBookingConfirmationCode();
	}

	@Override
	public void cancelBooking(Long bookingId) {
		bookingRepository.deleteById(bookingId);
	}

	/**
	 * {private method to check room Availability}
	 * 
	 * Suppose existing booking is between 13 to 16
	 * 
	 * Conditions to ignore :
	 * 
	 * 1) 13 - 15, check-in day same && check-out before 16
	 * 
	 * 2) check-in -14, between 13 && 16
	 * 
	 * 3) 12 - 16, check-in before && check-out on existing booking day 16
	 * 
	 * 4) 12 - 17, check-in before && check-out after existing booking day 16
	 * 
	 * 5) check-in - 16, on existing check-out day 16
	 * 
	 * 6) check-out - 13, on existing check-in day 13
	 * 
	 * @return If there is any overlap with existing bookings, the method @returns
	 *         false, * indicating that the room is not available.
	 */
	private boolean checkRoomAvailability(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {

		return existingBookings.stream().noneMatch(
				existingBooking -> (bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
						&& bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
								&& bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
								&& bookingRequest.getCheckOutDate().isEqual(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
								&& bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
								&& bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))
						|| (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
								&& bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
								&& bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate())));
	}
}
