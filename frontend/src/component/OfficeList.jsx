import React, { useState, useEffect } from 'react'
import axios from 'axios'

function OfficeList() {

    const [offices, setOffices] = useState([])
    const [search, setSearch] = useState("")

    async function loadOffices() {
        try {
            const response = await axios.get('http://localhost:1014/api/offices/all')
            setOffices(response.data)
        } catch (error) {
            alert("Could not load offices")
        }
    }

    useEffect(() => {
        loadOffices()
    }, [])

    async function deleteOffice(id) {
        if (!window.confirm("Delete this office?")) {
            return
        }
        try {
            const response = await axios.delete('http://localhost:1014/api/offices/delete/' + id)
            alert(response.data)
            loadOffices()
        } catch (error) {
            if (error.response) {
                alert(error.response.data)
            } else {
                alert("Office can't be deleted")
            }
        }
    }

    const filteredOffices = offices.filter(office =>
        (office.name || "").toLowerCase().includes(search.toLowerCase()) ||
        String(office.officeNumber).includes(search)
    )

    return (
        <div>
            <h1>OFFICES PAGE</h1>

            <label>Search: </label>
            <input
                type="text"
                value={search}
                placeholder="Type office name or office number"
                onChange={e => setSearch(e.target.value)}
            />

            <br /><br />

            <table border="1" cellPadding="8">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Office Number</th>
                        <th>Office Name</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredOffices.length === 0 ? (
                        <tr>
                            <td colSpan="4">No offices found</td>
                        </tr>
                    ) : (
                        filteredOffices.map(office => (
                            <tr key={office.id}>
                                <td>{office.id}</td>
                                <td>{office.officeNumber}</td>
                                <td>{office.name}</td>
                                <td>
                                    <button>Update</button>{" "}
                                    <button onClick={() => deleteOffice(office.id)}>Delete</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    )
}

export default OfficeList