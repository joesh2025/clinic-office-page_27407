import React, { useState } from 'react'
import axios from 'axios'
import LibraryButton from "./component/LibraryButton";


function Office() {

    const [officeName, setOfficeName] = useState("");
    const [officeNumber, setOfficeNumber] = useState("");

    const [officeNumberToDelete, setOfficeNumberToDelete] = useState("");

    async function saveOffice(){
        const response = await axios.post('http://localhost:1014/api/offices/save', {
      name: officeName,
    officeNumber: Number(officeNumber) 
            }
        )
        if(response.status === 201 || response.status === 200){
            alert("Office saved successfully");
        }
        if(response.status === 409){
            alert("Office can't be saved")
        }

    }

    async function deleteOffice(){
        const response = await axios.delete(`http://localhost:1014/api/offices/number/${officeNumberToDelete}`)
        if(response.status === 200){
            alert("Office deleted successfully");
        }
        if(response.status === 4009){
            alert("Office can't be deleted")
        }
    }


  return (
    <div>
    <div>
      <h1> SAVE OFFICE</h1>
      <label>Office Name:</label><input type='text' onChange={e=>setOfficeName(e.target.value)}/> <br></br>
      <label>Office Number:</label><input type='text' onChange={e=>setOfficeNumber(e.target.value)}/>

      <LibraryButton buttonLabel="SAVE" onClickButton={saveOffice}/>

    </div>
    <div>
        <h1>DELETE OFFICE</h1>
        <label>Office Number: </label><input type='text' onChange={e=>setOfficeNumberToDelete(e.target.value)}/>

        <LibraryButton buttonLabel="DELETE" onClickButton={deleteOffice}/>
    </div>
    </div>
  )
}

export default Office


