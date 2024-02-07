package com.project.lakesidehotels.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
	private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String guestName;

    private String guestEmail;

    private int numOfAdults;

    private int numOfChildren;

    private int totalNumOfGuests;

    private String bookingConfirmationCode;

    private RoomResponseDTO room;

	public BookingResponseDTO(Long id, LocalDate checkInDate, LocalDate checkOutDate, String bookingConfirmationCode) {
		this.id = id;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.bookingConfirmationCode = bookingConfirmationCode;
	}
    
    
}
