package org.rascalmpl.eclipse.library.vis;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.rascalmpl.library.vis.FigurePApplet;

public class FigureLibrary {
	
	public FigureLibrary(IValueFactory values){
		super();
	}
	
	public void render(IConstructor velem, IEvaluatorContext ctx){
		FigurePApplet vlp = new FigurePApplet(velem, ctx);
		FigureViewer.open(vlp);
	}
}