# iBook

> iBook is a book lending application implemented with Java

[![Build Status](https://travis-ci.com/CMPUT301F20T10/iBook.svg?branch=main)](https://travis-ci.com/CMPUT301F20T10/iBook) ![language](https://img.shields.io/badge/language-java-orange.svg)

## Warning:warning:

> :warning: **This program has not tested for all Android phones.**
> :warning: **It is currently known that unknown errors will occur in some models.**
> :warning: **It is recommended to use the emulator of Android Studio to compile and run this project.**
>
> **Current Known issue:**
>
> * **some interfaces may go blank in xioami 9 and LG G8. But it works on emulator and Huawei P20**

### Recommended operating environment: Emulator with model Pixel 3 API 28(with Google play Service)



## UML

### Note: The [UML](.doc/UML_Updated.png) has been updated for the Final Checkpoint

The UML is devided into 3 parts, since it's too complicated that having them in one single diagram would make it really hard to read.

* ***[UML_Overall](./doc/UML_Overall.png)*** focuses on the most important anticipated classes
* ***[UML_pageLevelDetails](./doc/UML_pageLevelDetails.png)*** provides a deeper view of the classes, interfaces and their relationships, focusing on the pages, fragments and their relationships
  * how they communicate with/generate other pages
  * how they interact with the databse
* ***[UML_FragmentLevelDetails](./doc/UML_FragmentLevelDetails.png)*** is all about the ViewBookFragment class.
  Since we decide (for now) to have different button logics (requesting/returning/borrowing...) for the page used to view the book details, according to the book to be viewed and the user openning this page, the classes like ViewBookFragment will have 6 ways of interacting with other classes (though they will be used by other classes in the same way).

## Product Backlog

The **[Product Backlog](https://github.com/CMPUT301F20T10/iBook/wiki)** is on the wiki page of this repo.

## Mockup

The Mockup has two parts:

* **[Part 1](./doc/Login,%20Search%20and%20Me.png):**
  * Login in
  * Sign up
  * Search
  * View/Edit Profile
* **[Part 2](./doc/Request,%20Accept,%20borrow%20and%20Return.png):**
  * Request/Accept/Borrow/Return
  * Book list
  * Notifications

The ***[Mockup](./doc/)*** is in the doc folder📂

## Javadoc
The Javadoc is on the ***[GitHub Page](https://cmput301f20t10.github.io/iBook/)*** , or you can find it in the ***[docs](./docs/)*** folder📂

## Contributors

[![Contributors](https://contributors-img.web.app/image?repo=CMPUT301F20T10/iBook)](https://github.com/CMPUT301F20T10/iBook/graphs/contributors)

## License

[MIT](https://opensource.org/licenses/MIT)

Copyright (c) 2020-present, CMPUT301F20T10
