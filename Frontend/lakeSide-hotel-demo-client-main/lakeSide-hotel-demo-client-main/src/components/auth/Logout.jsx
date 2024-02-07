import React, { useContext } from "react"
import { AuthContext } from "./AuthProvider"
import { Link, useNavigate } from "react-router-dom"
import { logoutUser } from "../utils/ApiFunctions"

const Logout = () => {
	const auth = useContext(AuthContext)
	const navigate = useNavigate()



	const handleLogout = async()=> {
			try {
				 await logoutUser();  // Call the logoutUser function from your API
				 console.log("After logout API call");
				auth.handleLogout();
				navigate("/", { state: { message: "You have been logged out!" } });
				window.location.reload();
			} catch (error) {
				console.error("Error during logout:", error.message);
				// Handle error or display a message to the user
			}
		}

	return (
		<>
			<li>
				<Link className="dropdown-item" to={"/profile"}>
					Profile
				</Link>
			</li>
			<li>
				<hr className="dropdown-divider" />
			</li>
			<button className="dropdown-item" onClick={handleLogout}>
				Logout
			</button>
		</>
	)
}

export default Logout
