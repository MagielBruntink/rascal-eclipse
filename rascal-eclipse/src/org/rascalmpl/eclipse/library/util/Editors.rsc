@license{
  Copyright (c) 2009-2011 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
@contributor{Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI}
@contributor{Paul Klint - Paul.Klint@cwi.nl - CWI}
@contributor{Davy Landman - Davy.Landman@cwi.nl - CWI}
@doc{
.Synopsis
Eclipse editing facilties.
}
module util::Editors

// needed for colors
import vis::Figure;

public data LineDecoration = 
    info(int lineNumber, str msg)
  | warning(int lineNumber, str msg)
  | error(int lineNumber, str msg)
  | highlight(int lineNumber, str msg)
  | highlight(int lineNumber, str msg, int level)
  ;


@doc{
.Synopsis
Set custom colors for errors
}
@javaClass{org.rascalmpl.eclipse.library.util.Editors}
public java void setErrorColors(list[Color] colors);

@doc{
.Synopsis
Set custom colors for line highlights.
}
@javaClass{org.rascalmpl.eclipse.library.util.Editors}
public java void setHighlightColors(list[Color] colors);

@doc{Open a source editor (using annotations from location)}
public void edit(loc file){
	edit(file,"Here");
}

@doc{
.Synopsis
Open a source editor (using annotations from location).
}
public void edit(loc file,str msg){
	list[LineDecoration] ld = [];
	if (file.begin?) {
		ld = [info(l, msg) | l <- [file.begin.line..file.end.line+1]];
	}
	else {
		ld = [info(1, msg)];	
	}
	edit(file, ld);
}

@doc{
.Synopsis
Open a source editor (using annotations from location).
}
public void edit(loc file,LineDecoration (int,str) decorator,str msg){
	edit(file,[decorator(i,msg) | i <- [file.begin[0]..file.end[0]+1]]);
}
	
@doc{
.Synopsis
Open a source editor.
}
@javaClass{org.rascalmpl.eclipse.library.util.Editors}
public java void edit(loc file, list[LineDecoration] lineInfo);

alias ComputedLineDecorations = list[LineDecoration] ();
@doc{Open a source editor, but with computed line dectorations}
@javaClass{org.rascalmpl.eclipse.library.util.Editors}
public java void edit(loc file,  ComputedLineDecorations lineInfo);

@doc{
.Synopsis
Provide a closure to add line decorations for file not opened using the edit method.

.Description
Provide a closure to add line decorations for file not opened using the edit method,
but of a certain extensions (such as .java).
}
@javaClass{org.rascalmpl.eclipse.library.util.Editors}
public java void provideDefaultLineDecorations(str extension,  ComputedLineDecorations (loc newFile) handleNewFile);
