/*******************************************************************************
 * Copyright (c) 2009-2011 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Mark Hills - Mark.Hills@cwi.nl (CWI)
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
*******************************************************************************/
package org.rascalmpl.eclipse.editor;

import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.rascalmpl.ast.AbstractAST;
import org.rascalmpl.eclipse.outline.TreeModelBuilder.Group;
import org.rascalmpl.value.INode;
import org.rascalmpl.value.ISourceLocation;
import org.rascalmpl.value.IValue;
import org.rascalmpl.values.uptr.ITree;
import org.rascalmpl.values.uptr.TreeAdapter;

import io.usethesource.impulse.editor.ModelTreeNode;
import io.usethesource.impulse.parser.ISourcePositionLocator;

public class NodeLocator implements ISourcePositionLocator {

	public Object findNode(Object ast, int offset) {
		if (ast instanceof ITree) {
			return TreeAdapter.locateLexical((ITree) ast, offset);
		}
		else if (ast instanceof AbstractAST) {
			return ((AbstractAST) ast).findNode(offset);
		}
		else if (ast instanceof ModelTreeNode) {
			return findNode(((ModelTreeNode) ast).getASTNode(), offset);
		}
		return null;
	}

	public Object findNode(Object ast, int startOffset, int endOffset) {
		if (ast instanceof ITree) {
			return TreeAdapter.locateLexical((ITree) ast, startOffset);
		}
		else if (ast instanceof AbstractAST) {
			return ((AbstractAST) ast).findNode(startOffset);
		}
		else if (ast instanceof ModelTreeNode) {
			return findNode(((ModelTreeNode) ast).getASTNode(), startOffset);
		}
		
		return null;
	}

	public int getEndOffset(Object node) {
		return getStartOffset(node) + getLength(node) - 1;
	}

	public IPath getPath(Object node) {
		return null;
	}

	public int getStartOffset(Object node) {
		if(node instanceof Token){
			return ((Token) node).getOffset();
		}
		
		return getLocation(node) == null ? 0 : getLocation(node).getOffset();
	}

	public int getLength(Object node) {
		if(node instanceof Token){
			return ((Token) node).getLength();
		}
		
		return getLocation(node) == null ? 0 : getLocation(node).getLength();
	}
	
	private ISourceLocation getLocation(Object node){
		if (node instanceof ITree){
			return TreeAdapter.getLocation((ITree) node);
		}
		
		if (node instanceof INode) {
			INode n = (INode) node;
			IValue ann = n.asAnnotatable().getAnnotation("loc");
			if (ann != null) {
				return (ISourceLocation) ann;
			}
		} 
		
		if (node instanceof AbstractAST){
			return ((AbstractAST) node).getLocation();
		}
		
		if (node instanceof ModelTreeNode){
			return getLocation(((ModelTreeNode) node).getASTNode());
		}
		
		if (node instanceof Group<?>){
			Group<?> group = (Group<?>) node;
			Iterator<?> i = group.iterator();
			if(i.hasNext()){
				return getLocation(i.next());
			}
			return group.getLocation();
		}
		
		return null;
	}
}
