import React, { useEffect, useState } from "react"
import { deleteRoom, getAllRooms } from "../utils/ApiFunctions"
import { Col, Row } from "react-bootstrap"
import RoomFilter from "../common/RoomFilter"
import RoomPaginator from "../common/RoomPaginator"
import { FaEdit, FaEye, FaPlus, FaTrashAlt } from "react-icons/fa"
import { Link } from "react-router-dom"

// This component manages the display of existing rooms, allowing for pagination and filtering.
const ExistingRooms = () => {
	// Stores the list of rooms fetched from the server.
	const [rooms, setRooms] = useState([{ id: "", roomType: "", roomPrice: "" }])

	// Keeps track of the current page being displayed in the pagination.
	const [currentPage, setCurrentPage] = useState(1)

	// Defines the number of rooms shown per page.
	const [roomsPerPage] = useState(8)

	// Manages the loading state of room data.
	const [isLoading, setIsLoading] = useState(false)

	// Stores rooms based on the selected room type filter.
	const [filteredRooms, setFilteredRooms] = useState([{ id: "", roomType: "", roomPrice: "" }])

	// Keeps track of the selected room type for filtering.
	const [selectedRoomType, setSelectedRoomType] = useState("")

	// Manage error and success messages.
	const [errorMessage, setErrorMessage] = useState("")
	const [successMessage, setSuccessMessage] = useState("")

	// to fetch the initial list of rooms when the component mounts.
	useEffect(() => {
		fetchRooms()
	}, [])

	// Function to fetch rooms from the server:
	const fetchRooms = async () => {
		setIsLoading(true)
		try {
			const result = await getAllRooms()
			setRooms(result)
			setIsLoading(false)
		} catch (error) {
			setErrorMessage(error.message)
			setIsLoading(false)
		}
	}

	// updates to `rooms` and `selectedRoomType` to filter and paginate the room data.
	useEffect(() => {
		if (selectedRoomType === "") {
			setFilteredRooms(rooms)
		} else {
			const filteredRooms = rooms.filter((room) => room.roomType === selectedRoomType)
			setFilteredRooms(filteredRooms)
		}
		setCurrentPage(1)
	}, [rooms, selectedRoomType])

	// Handle pagination clicks:
	const handlePaginationClick = (pageNumber) => {
		setCurrentPage(pageNumber)
	}

	// to delete a room by its ID using the `deleteRoom` API function.
	const handleDelete = async (roomId) => {
		try {
			const result = await deleteRoom(roomId)
			if (result === "") {
				setSuccessMessage(`Room No ${roomId} was delete`)
				fetchRooms()
			} else {
				console.error(`Error deleting room : ${result.message}`)
			}
		} catch (error) {
			setErrorMessage(error.message)
		}
		setTimeout(() => {
			setSuccessMessage("")
			setErrorMessage("")
		}, 3000)
	}

	const calculateTotalPages = (filteredRooms, roomsPerPage, rooms) => {
		const totalRooms = filteredRooms.length > 0 ? filteredRooms.length : rooms.length
		return Math.ceil(totalRooms / roomsPerPage)
	}

	// Determine room indexes for current page:
	const indexOfLastRoom = currentPage * roomsPerPage
	const indexOfFirstRoom = indexOfLastRoom - roomsPerPage
	const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom)

	return (
		<>
			{/* Display success or error messages */}
			<div className="container col-md-8 col-lg-6">
				{successMessage && <p className="alert alert-success mt-5">{successMessage}</p>}

				{errorMessage && <p className="alert alert-danger mt-5">{errorMessage}</p>}
			</div>
			{/* Display loading message or room data */}
			{isLoading ? (
				<p>Loading existing rooms</p>
			) : (
				<>
					{/* Existing Rooms Section */}
					<section className="mt-5 mb-5 container">
						<div className="d-flex justify-content-between mb-3 mt-5">
							<h2>Existing Rooms</h2>
						</div>
						{/* RoomFilter and Add Room Link */}
						<Row>
							<Col md={6} className="mb-2 md-mb-0">
								<RoomFilter data={rooms} setFilteredData={setFilteredRooms} />
							</Col>

							<Col md={6} className="d-flex justify-content-end">
								<Link to={"/add-room"}>
									<FaPlus /> Add Room
								</Link>
							</Col>
						</Row>
						{/* Room Data Table */}
						<table className="table table-bordered table-hover">
							<thead>
								<tr className="text-center">
									<th>ID</th>
									<th>Room Type</th>
									<th>Room Price</th>
									<th>Actions</th>
								</tr>
							</thead>

							<tbody>
								{/* Map through currentRooms and display room details */}
								{currentRooms.map((room) => (
									<tr key={room.id} className="text-center">
										<td>{room.id}</td>
										<td>{room.roomType}</td>
										<td>{room.roomPrice}</td>
										<td className="gap-2">
											{/* View and Edit Room Links */}
											<Link to={`/edit-room/${room.id}`} className="gap-2">
												<span className="btn btn-info btn-sm">
													<FaEye />
												</span>
												<span className="btn btn-warning btn-sm ml-5">
													<FaEdit />
												</span>
											</Link>
											{/* Delete Room Button */}
											<button
												className="btn btn-danger btn-sm ml-5"
												onClick={() => handleDelete(room.id)}>
												<FaTrashAlt />
											</button>
										</td>
									</tr>
								))}
							</tbody>
						</table>
						{/* Pagination Component */}
						<RoomPaginator
							currentPage={currentPage}
							totalPages={calculateTotalPages(filteredRooms, roomsPerPage, rooms)}
							onPageChange={handlePaginationClick}
						/>
					</section>
				</>
			)}
		</>
	)
}

export default ExistingRooms
