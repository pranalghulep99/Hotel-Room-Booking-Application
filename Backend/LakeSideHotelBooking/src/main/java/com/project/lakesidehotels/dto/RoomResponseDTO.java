package com.project.lakesidehotels.dto;

import java.math.BigDecimal;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Data Transfer object to show selected information to the user at fronted
 */
@Data
@NoArgsConstructor
public class RoomResponseDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String roomType;

	private BigDecimal roomPrice;

	private boolean isBooked = false;

	private String photo;

	private List<BookingResponseDTO> bookings;

	public RoomResponseDTO(long id, String roomType, BigDecimal roomPrice) {
		super();
		this.id = id;
		this.roomType = roomType;
		this.roomPrice = roomPrice;
	}

	/*
	 * In database photo is getting stored in blob format Converting into
	 * String through Base64 encoding to show on UI, Base64 : apache.tomcat
	 */
	public RoomResponseDTO(long id, String roomType, BigDecimal roomPrice, boolean isBooked, byte[] photoBytes,
			List<BookingResponseDTO> bookings) {
		super();
		this.id = id;
		this.roomType = roomType;
		this.roomPrice = roomPrice;
		this.isBooked = isBooked;
		this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
		this.bookings = bookings;
	}

}
