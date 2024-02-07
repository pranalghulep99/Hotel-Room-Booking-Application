package com.project.lakesidehotels.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookedRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;

	@Column(name = "check_in")
	private LocalDate checkInDate;

	@Column(name = "check_out")
	private LocalDate checkOutDate;

	@Column(name = "guest_fullName")
	private String guestFullName;

	@Column(name = "guest_email")
	private String guestEmail;

	@Column(name = "adults")
	private int numOfAdults;

	@Column(name = "children")
	private int numOfChildren;

	@Column(name = "total_guest")
	private int totalNumOfGuest;

	@Column(name = "confirmaition_code")
	private String bookingConfirmationCode;

//	one room can be booked by many people
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id")
	private Room room;

//	to  calculate total number of guest
	public void calculateTotalNumOfGuest() {
		this.totalNumOfGuest = this.numOfAdults + this.numOfChildren;
	}

//	when user fill or change value numOfAdults -> totalNumOfGuest should be recalculate
	public void setNumOfAdults(int numOfAdults) {
		this.numOfAdults = numOfAdults;
		calculateTotalNumOfGuest();
	}

//	when user fill or change value numOfChildren -> totalNumOfGuest should be recalculate
	public void setNumOfChildren(int numOfChildren) {
		this.numOfChildren = numOfChildren;
		calculateTotalNumOfGuest();
	}
	
	public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }

}
