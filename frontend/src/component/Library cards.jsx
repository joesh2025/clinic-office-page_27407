import React from 'react'

function LibraryCards({ bookInfo}) {
    return (
        <div style={{border:"2px solid black", margin:"10px", padding:"10px"}}>
            <div>{bookInfo.name}</div>
            <div>Code: {bookInfo.code}</div>
            <div>Price: {bookInfo.price}</div>
        </div>
    )
}

export default LibraryCards