package com.project.lakesidehotels.service;

import java.util.List;
import java.util.Optional;

import com.project.lakesidehotels.entities.BookedRoom;

public interface IBookingService {

	List<BookedRoom> getAllBookingsByRoomId(long roomId);

	BookedRoom getBookingByConfirmationCode(String bookingConfirmationCode);

	List<BookedRoom> getAllBookings();

	String saveBooking(Long roomId, BookedRoom bookingRequest);

	void cancelBooking(Long bookingId);

	List<BookedRoom> getBookingsByUserEmail(String email);

}
