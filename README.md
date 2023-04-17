# QRBook 📲📖
QRBook is a mobile application in the Kotlin language that facilitates borrowing books from the library. The user can easily reserve a book in the system by scanning the QR code on a book. The application connects to a local server with a database to access and update information about borrowed books.

## Application:
* __Login page__: allows the user to log in to the application or create an account if they do not already have one
<p align="center">
  <img src="Photos/Login.png" />
</p>

* __Register page__:
<p align="center">
  <img src="Photos/Registration.png" />
</p>

* __Main page__: After successfully logging in, the user is transferred to the main menu where they can borrow a new book (using the qr code), search for a book available in the library, return books or log out of the application
<p align="center">
  <img src="Photos/Main%20menu.png" />
</p>

* __QR Code scanner page__: After clicking on the "camera" button, the camera will be turned on to scan the qr code
<p align="center">
  <img src="Photos/Scan%20panel.png" />
</p>

* __Availability of books__: If the book is available, a button appears for the user to borrow it. If the code of an already owned book is scanned, the user can return his book. If the book turns out to be occupied, the user can only return to the previous page of the application.
<p align="center">
  <img src="Photos/After%20scanning.png" />
</p>

* __Search page__: The user has the option of checking which books are available based on the title, author or year of publication of the book.
<p align="center">
  <img src="Photos/Searching%20page.png" />
</p>

## Server:

## Panel for the librarian:
