@license{
  Copyright (c) 2009-2011 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
@contributor{Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI}
@contributor{Davy Landman - Davy.Landman@cwi.nl - CWI}

@doc{
Name: IDE

Synopsis: Extend an IDE with interactive, language-specific, features (Eclipse only).

Usage: `import util::IDE;`

Description:

The IDE Meta-tooling Platform, [IMP](http://www.eclipse.org/imp/) for short, is a collection of API and tools to support constructing IDEs for programming languages and domain specific languages. Using the IDE library, you can instantiate the services that IMP provides for any language implemented in Rascal.

Rascal is also a part of the collection of IMP tools and (will be) hosted shortly on eclipse.org.

To instantiate an IDE for a language implemented using Rascal, use the following steps:
* Define the grammar for the language.
* Define a parse function for the language.
* Register the language.


Now you can step-by-step extend the IDE for your language with additional features:
<toc Rascal/Libraries/util/IDE 1>

To be able to reuse your IDE between runs of Eclipse, read about [Plugin]s.
To add annotations to trees, just after parsing, you should register an `annotator` function.

Examples:

<screen>
import util::IDE;
start syntax ABC = [a-z]+;
layout Whitespace = [\ \t\n]*;
start[ABC] abc(str x, loc l) { 
  return parse(#start[ABC], x, l); 
}
registerLanguage("The ABC language", "abc", abc);
// After this, your __current__ Eclipse instance will start editors for all files ending in `.abc` and parse them using the `abc` function. The editor will provide some default highlighting features.
//
// To add annotations to trees, just after parsing, you should register an 'annotator' function:
registerAnnotator("abc", Tree (Tree t) { return t[@doc="Hello!"]; });
</screen>




Benefits:

Pitfalls:

Questions:


}
module util::IDE

// Especially annotations defined in this module are relevant for util::IDE
import ParseTree;
import vis::Figure;
import util::ContentCompletion;
extend Message;

@doc{
Synopsis: Data type to describe contributions to the menus of the IDE.

Pitfalls:

This data type is not yet complete.

The categories do not support changing the font name or the font size.
}
data Contribution 
     = popup(Menu menu)
     | menu(Menu menu)
     | categories(map[str categoryName, FontProperties fontStyle] styleMap)
     | builder(set[Message] ((&T<:Tree) tree) messages)
     | annotator((&T<:Tree) (&T<:Tree input) annotator)
     | liveUpdater(lrel[loc,str] (&T<:Tree input) updater)
     | outliner(node (&T<:Tree input) outliner)
     | proposer(list[CompletionProposal] (&T<:Tree input, str prefix, int requestOffset) proposer, str legalPrefixChars)
     ;
  
data Menu 
     = action(str label, void (Tree tree, loc selection) action)
     | action(str label, void (str selStr, loc selLoc) handler) // for non rascal menu's
     | toggle(str label, bool() state, void(Tree tree, loc selection) action)
     | edit(str label, str (Tree tree, loc selection) edit)
     | group(str label, list[Menu] members)
     | menu(str label, list[Menu] members)
     ;
  

@doc{
Synopsis: Annotate an outline node with a label.
}

anno str node@label;

@doc{
Synopsis: Annotate an outline node with a link.
}
anno loc node@\loc;  // a link for an outline node


@doc{
Synopsis: Register a language extension and a parser for use in Eclipse.
}
@reflect{Use the evaluator to parse editor contents and apply functions to parse trees}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void registerLanguage(str name, str extension, Tree (str input, loc origin) parse);

@doc{
Synopsis: Register an annotator.

Description:

  Register a tree processor for annotating a tree with [$ParseTree/doc],
  [$ParseTree/link], or [$ParseTree/message]
  annotations. See [ParseTree] for available annotations. The annotations are
  processed by the editor to generate visual effects such as error markers, hyperlinks
  and documentation hovers.
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
@deprecated{use an annotator contribution instead}
public java void registerAnnotator(str name, (&T<:Tree) (&T<:Tree input) annotator);

@doc{
Synopsis: Register an outliner.

Description:

Register an outliner function. An outliner maps a parse tree to a simpler
tree that summarizes the contents of a file. This summary is used to generate the outline
view in Eclipse. 

Use the  [$IDE/label] and [$IDE/loc] annotations on each node to guide how each outline
item is displayed, which item it links to and what image is displayed next to it.
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
@deprecated{use an outliner contribution instead}
public java void registerOutliner(str name, node (&T<:Tree input) outliner);

@doc{

Synopsis: Register contributions to Eclipse menus.

Description:

Register a number of contributions to the menus of the IDE.
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void registerContributions(str name, set[Contribution] contributions);

@doc{
Synopsis: Clear all registered languages.

Description:
Remove all registered languages.

Pitfalls:
Use with caution! This will clear all registered languages (for debugging purposes).
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void clearLanguages();

@doc{
Synopsis: Clear a registered language.

Description:

Remove a registered language.

Benefits:

When in doubt about the state of your IDE additions, you can always remove the language, close all editors and check if everything is gone, then re-register your language again.

Pitfalls:

Use with caution! This will clear a registered language (for debugging purposes).
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void clearLanguage(str name);

@doc{
Synopsis: Register contributions to menus of a non-Rascal editor.

Description:
Register a number of contributions to the menus of a non-Rascal code editor:
* `name`: eclipse editor id
* `contributions`: (edit is not supported), and Tree parameter of the callback will be empty).
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void registerNonRascalContributions(str name, set[Contribution] contributions);

@doc{
Synopsis: Clear all non-Rascal IDE contributions.
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void clearNonRascalContributions();

@doc{
Synopsis: Clear all non-Rascal IDE contributions for a specific editor.

Description:

* `name`: Eclipse editor id.
}
@javaClass{org.rascalmpl.eclipse.library.util.IDE}
public java void clearNonRascalContribution(str name);

