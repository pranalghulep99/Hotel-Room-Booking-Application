import React from "react"
// Props:
//    - `currentPage`: Current active page.
//    - `totalPages`: Total number of pages.
//    - `onPageChange`: Callback function for page changes.
const RoomPaginator = ({ currentPage, totalPages, onPageChange }) => {
	// Generates page numbers from 1 to `totalPages`.
	const pageNumbers = Array.from({ length: totalPages }, (_, i) => i + 1)
	// - Dynamically styles the active page.
	// - Provides buttons to navigate between pages.
	return (
		<nav aria-label="Page navigation">
			<ul className="pagination justify-content-center">
				{pageNumbers.map((pageNumber) => (
					<li
						key={pageNumber}
						className={`page-item ${currentPage === pageNumber ? "active" : ""}`}>
						<button onClick={() => onPageChange(pageNumber)} className="page-link">
							{pageNumber}
						</button>
					</li>
				))}
			</ul>
		</nav>
	)
}

export default RoomPaginator
