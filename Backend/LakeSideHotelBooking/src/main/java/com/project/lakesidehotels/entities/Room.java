package com.project.lakesidehotels.entities;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String roomType;

	private BigDecimal roomPrice;

	private boolean isBooked = false;
	
	@Lob
	private Blob photo;

/*	to track booking history of the room
	If one room is deleted history related to all booking should be deleted
	Anything we update for room it should affect the booking
*/
	@OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade =CascadeType.ALL)
	private List<BookedRoom> bookings;

//	when we create new room booking history should be initialized with empty List to avoid NullPointerException
	public Room() {
		this.bookings=new ArrayList<>();
	}
	
/*	to add new Booking
 * 	add this booking details to previous booking list
 * 	room booked
 * 	confirmationCode
 */
	public void addBooking(BookedRoom booking) {
		if(bookings==null) {
			bookings=new ArrayList<>();
		}
		bookings.add(booking);
		booking.setRoom(this);
		isBooked=true;
		String bookingCode=RandomStringUtils.randomNumeric(10);
		booking.setBookingConfirmationCode(bookingCode);
	}
}
