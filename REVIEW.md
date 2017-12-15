### Names
Some method names don't really make sense
##### Example: 
FirstActivity.java line: 80 & 138 onSigned and onCreated
##### How to improve: 
These methods were made to shorten other methods so the names were not really thought of. Improve this by thinking a bit harder about how to name these type of methods.
### Headers
Headers are present but are not detailed enough.
##### Example: 
Every java file
##### How to improve: 
Spend a little more time on the headers. Maybe add some information about the context (what parameters should be given to this activity etc.)
### Comments
Multiline comments (/\*\*/) and single line comments (//) are used interchangeably and sometimes comments are missing.
##### Example:
Volumeactivity.java a few methods are missing comments. This again because the methods were made to shorten other methods.
##### How to improve:
Only use the multiline comments above methods and inside of methods use the single line comments. And maybe use more single line comments inside of methods.
### Layout
Most lines are short enough to read and no old commented code is present.
##### Example:
N/A
##### How to improve:
N/A
### Formatting
Formatting is also present in all the files and it makes similar parts of the code clearly identifiable.
##### Example:
All the .java files.
##### How to improve:
No need to improve.
### Flow
There are quite a lot of nested anonymous classes and sometimes multiple methods are needed to accomplish a simple thing.
##### Example:
Loading the image in the VolumeActivity.java file is done using multiple methods. This because it needed another thread to retrieve the image etc.
##### How to improve:
Find a better way to handle getting images from the internet and displaying them.
### Idiom
##### Example:
##### How to improve:
### Expressions
Sometimes code is written multiple times where it could be written just once if I had been smarter.
##### Example:
VolumeActivity.java parseJSONResponse() volume.getJSONObject.("volumeInfo") is called 4 times. This makes the expressions where it is calles less readable.
##### How to improve:
This could easily be done in a different variable once to make the expressions in which it is called now more readable.
### Decomposition
##### Example:
##### How to improve:
### Modularization
##### Example:
##### How to improve:
