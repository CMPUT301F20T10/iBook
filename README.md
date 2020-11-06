# iBook

> iBook is a book lending application implemented with Java

[![Build Status](https://travis-ci.com/CMPUT301F20T10/iBook.svg?branch=main)](https://travis-ci.com/CMPUT301F20T10/iBook) ![language](https://img.shields.io/badge/language-java-orange.svg)

## UML

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

The **[Mockup]()** is on the wiki page of this repo.

## Contributors

[![Contributors](https://contributors-img.web.app/image?repo=CMPUT301F20T10/iBook)](https://github.com/CMPUT301F20T10/iBook/graphs/contributors)

## License

[MIT](https://opensource.org/licenses/MIT)

Copyright (c) 2020-present, CMPUT301F20T10
