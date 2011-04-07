/*******************************************************************************
 * Copyright (c) 2009-2011 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Bert Lisser - Bert.Lisser@cwi.nl (CWI)
 *   * Paul Klint - Paul.Klint@cwi.nl - CWI
*******************************************************************************/
package org.rascalmpl.eclipse.library.vis;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.rascalmpl.library.vis.FigurePApplet;

public class FigureEditorInput implements IEditorInput {
	
	final private IConstructor fig;  
	final private IEvaluatorContext ctx;
	final private IString name;
	
	public IConstructor getFig() {
		return fig;
	}	

	public IEvaluatorContext getCtx() {
		return ctx;
	}

	public FigureEditorInput(IString name, IConstructor fig,  IEvaluatorContext ctx) {
		this.fig = fig;
		this.ctx = ctx;
		this.name = name;
	}
	
	public boolean exists() {
		return fig != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public IString getIString() {
		return  name;
	}
	
	public String getName() {
		return name.getValue();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return name.getValue();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

}
