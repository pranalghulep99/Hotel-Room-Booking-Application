
import React, { useState, useEffect } from "react"
import DateSlider from "../common/DateSlider"
import { isBefore, parseISO, format } from 'date-fns';
import { isAfter } from "date-fns";


const BookingsTable = ({ bookingInfo, handleBookingCancellation }) => {
	const [currentDateFormatted, setCurrentDateFormatted] = useState(format(new Date(), 'yyyy-MM-dd'));
	const [filteredBookings, setFilteredBookings] = useState(bookingInfo)

	const filterBooknigs = (startDate, endDate) => {
		let filtered = bookingInfo
		if (startDate && endDate) {
			filtered = bookingInfo.filter((booking) => {
				const bookingStarDate = parseISO(booking.checkInDate)
				const bookingEndDate = parseISO(booking.checkOutDate)
				return (
					bookingStarDate >= startDate && bookingEndDate <= endDate && bookingEndDate > startDate
				)
			})
		}
		setFilteredBookings(filtered)
	}

	useEffect(() => {
		setFilteredBookings(bookingInfo)
	}, [bookingInfo])

	return (
		<section className="p-4">
			<DateSlider onDateChange={filterBooknigs} onFilterChange={filterBooknigs} />
			<table className="table table-bordered table-hover shadow">
				<thead>
					<tr>
						<th>S/N</th>
						<th>Booking ID</th>
						<th>Room ID</th>
						<th>Room Type</th>
						<th>Check-In Date</th>
						<th>Check-Out Date</th>
						<th>Guest Name</th>
						<th>Guest Email</th>
						<th>Adults</th>
						<th>Children</th>
						<th>Total Guest</th>
						<th>Confirmation Code</th>
						<th colSpan={2}>Actions</th>
					</tr>
				</thead>
				<tbody className="text-center">
					{filteredBookings.map((booking, index) => (
						<tr key={booking.id}>
							<td>{index + 1}</td>
							<td>{booking.id}</td>
							<td>{booking.room.id}</td>
							<td>{booking.room.roomType}</td>
							<td>{booking.checkInDate}</td>
							<td>{booking.checkOutDate}</td>
							<td>{booking.guestName}</td>
							<td>{booking.guestEmail}</td>
							<td>{booking.numOfAdults}</td>
							<td>{booking.numOfChildren}</td>
							<td>{booking.totalNumOfGuests}</td>
							<td>{booking.bookingConfirmationCode}</td>
							<td>
								{isAfter(parseISO(booking.checkOutDate), parseISO(currentDateFormatted)) ? (
									<button className="btn btn-danger" onClick={() => handleCancelBooking(booking.id)}>
										Cancel
									</button>
								) : (
									"Completed"
								)}
							</td>
						</tr>
					))}
				</tbody>
			</table>
			{filterBooknigs.length === 0 && <p> No booking found for the selected dates</p>}
		</section>
	)
}

export default BookingsTable
