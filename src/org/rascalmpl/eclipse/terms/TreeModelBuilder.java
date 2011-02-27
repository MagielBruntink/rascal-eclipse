package org.rascalmpl.eclipse.terms;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.pdb.facts.IBool;
import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IDateTime;
import org.eclipse.imp.pdb.facts.IExternalValue;
import org.eclipse.imp.pdb.facts.IInteger;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.INode;
import org.eclipse.imp.pdb.facts.IReal;
import org.eclipse.imp.pdb.facts.IRelation;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.visitors.IValueVisitor;
import org.eclipse.imp.pdb.facts.visitors.VisitorException;
import org.eclipse.imp.services.base.TreeModelBuilderBase;
import org.rascalmpl.eclipse.Activator;
import org.rascalmpl.interpreter.result.ICallableValue;
import org.rascalmpl.values.uptr.Factory;

public class TreeModelBuilder extends TreeModelBuilderBase implements ILanguageService  {
	private Language lang;
	
	public TreeModelBuilder() {}

	@Override
	protected void visitTree(Object root) {
		Language lang = initLanguage(root);
		
		if (lang == null || root == null) return;
		
		IConstructor pt = (IConstructor) root;
		ICallableValue outliner = TermLanguageRegistry.getInstance().getOutliner(lang);

		if (outliner == null) {
			return;
		}
		
		IValue outline = outliner.call(new Type[] {Factory.Tree}, new IValue[] {pt}).getValue();
		
		if (outline instanceof INode) {
			INode node = (INode) outline;
			createTopItem(outline);

			try {
				for (IValue child : node) {
					child.accept(new IValueVisitor<Object>() {
						public Object visitBoolean(IBool o)
						throws VisitorException {
							createSubItem(o);
							return null;
						}

						public Object visitConstructor(IConstructor o)
						throws VisitorException {
							pushSubItem(o);
							for (IValue child : o) {
								child.accept(this);
							}
							popSubItem();
							return null;
						}

						public Object visitDateTime(IDateTime o)
						throws VisitorException {
							createSubItem(o);
							return null;
						}

						public Object visitExternal(IExternalValue o)
						throws VisitorException {
							createSubItem(o);
							return null;
						}

						public Object visitInteger(IInteger o) throws VisitorException {
							createSubItem(o);
							return null;
						}

						public Object visitList(IList o) throws VisitorException {
							for (IValue elem : o) {
								elem.accept(this);
							}
							return null;
						}

						public Object visitMap(IMap o) throws VisitorException {
							for (IValue key : o) {
								pushSubItem(key);
								o.get(key).accept(this);
								popSubItem();
							}
							return null;
						}

						public Object visitNode(INode o) throws VisitorException {
							pushSubItem(o);
							for (IValue child : o) {
								child.accept(this);
							}
							popSubItem();
							return null;
						}

						public Object visitReal(IReal o) throws VisitorException {
							return createSubItem(o);
						}

						public Object visitRelation(IRelation o)
						throws VisitorException {
							for (IValue tuple : o) {
								tuple.accept(this);
							}
							return null;
						}

						public Object visitSet(ISet o) throws VisitorException {
							for (IValue tuple : o) {
								tuple.accept(this);
							}
							return null;
						}

						public Object visitSourceLocation(ISourceLocation o)
						throws VisitorException {
							return createSubItem(o);
						}

						public Object visitString(IString o) throws VisitorException {
							return createSubItem(o);
						}

						public Object visitTuple(ITuple o) throws VisitorException {
							for (IValue field : o) {
								field.accept(this);
							}
							return null;
						}
					});

				}
			} catch (VisitorException e) {
				Activator.getInstance().logException("could not compute outline", e);
			}
		}
	}

	private Language initLanguage(Object root) {
		if (lang == null) {
			if (root instanceof IConstructor) {
				lang = TermLanguageRegistry.getInstance().getLanguage((IConstructor) root);
			}
		}
		return lang;
	}
}
