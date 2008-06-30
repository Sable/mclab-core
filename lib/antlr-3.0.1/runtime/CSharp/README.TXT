ANTLR V3 Code Generator and Runtime

30 April, 2007

Kunle Odutola		: kunle UNDERSCORE odutola AT hotmail DOT com


This release has a number of breaking changes from the 3.0b6 release.
The most significant is that there are now TWO assembly dlls:

- Antlr3.Runtime.dll and
- Antlr3.Utility.dll


Antlr3.Runtime.dll is NOT dependent on StringTemplate.dll & antlr.runtime.dll

Antlr3.Utility.dll is dependent on StringTemplate.dll & antlr.runtime.dll for
the DOTGenerator class.

For most of the non-ST work with ANTLR, all you need is a reference to the 
Antlr3.Runtime.dll assembly.


There are a few minor API changes to some interfaces (e.g. obj.Nil -> obj.IsNil).
VS.NET intellisense should help with fixing those in existing projects.


	   
Enjoy!

Kunle Odutola
