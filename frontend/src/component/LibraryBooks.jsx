import React from 'react'

function LibraryBooks() {

    const books = [
        {
            code: "WEB 001",
            name: "Full Stack Development",
            price: 100
        },

        {
            code: "SOD 001",
            name: "Software Development",
            price: 90
        },

        {
            code: "SOE 001",
            name: "Software Engineering",
            price: 50
        }

    ]

  return (
    <div>
        <div>
        </div>
        <div>
            <ul>
              { books.map(book => <li key={book.code}>This is the name {book.name} and the price is {book.price}</li>)}
            </ul>
        </div>
    </div>
  )
}

export default LibraryBooks