# Test
<b>UML: </b>
The UML is devided into 3 parts, since it's too complicated that having them in one single diagram would make it really hard to read. 
1. The <i>UML_Overall.png</i> focuses on the most important anticipated classes;
2. The <i>UML_pageLevelDetails.png</i> provides a deeper view of the classes, interfaces and their relationships, focusing on the pages, fragments and their relationships (how they communicate with/generate other pages; how they interact with the databse);
3. Currently, The <i>UML_FragmentLevelDetails.png</i> is all about the ViewBookFragment class. Since we decide (for now) to have different button logics (requesting/returning/borrowing...) for the page used to view the book details, according to the book to be viewed and the user openning this page, the classes like ViewBookFragment will have 6 ways of interacting with other classes (though they will be used by other classes in the same way).  
