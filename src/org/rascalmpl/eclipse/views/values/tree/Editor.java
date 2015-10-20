package org.rascalmpl.eclipse.views.values.tree;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.rascalmpl.eclipse.Activator;
import org.rascalmpl.eclipse.views.values.ValueEditorInput;
import org.rascalmpl.value.IBool;
import org.rascalmpl.value.IConstructor;
import org.rascalmpl.value.IDateTime;
import org.rascalmpl.value.IExternalValue;
import org.rascalmpl.value.IInteger;
import org.rascalmpl.value.IList;
import org.rascalmpl.value.IMap;
import org.rascalmpl.value.INode;
import org.rascalmpl.value.IRational;
import org.rascalmpl.value.IReal;
import org.rascalmpl.value.ISet;
import org.rascalmpl.value.ISourceLocation;
import org.rascalmpl.value.IString;
import org.rascalmpl.value.ITuple;
import org.rascalmpl.value.IValue;
import org.rascalmpl.value.visitors.IValueVisitor;
import org.rascalmpl.values.ValueFactoryFactory;

public class Editor extends EditorPart {
	public static final String EditorId = "org.rascalmpl.eclipse.views.values.tree.editor";
	private TreeViewer treeViewer;
	private static final Object[] empty = new Object[0];
	
	public Editor() {
	}

	@Override
	public String getTitle() {
		IEditorInput editorInput = getEditorInput();
		
		if (editorInput != null) {
			return editorInput.getName();
		}
		
		return "Value";
	}
	
	public TreeViewer getViewer() {
		return treeViewer;
	}

	public static void open(final IValue value) {
		if (value == null) {
			return;
		}
	 	IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();

		if (win == null && wb.getWorkbenchWindowCount() != 0) {
			win = wb.getWorkbenchWindows()[0];
		}
		
		if (win != null) {
			final IWorkbenchPage page = win.getActivePage();

			if (page != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							page.openEditor(new ValueEditorInput(value, true, 2), Editor.EditorId);
						} catch (PartInitException e) {
							Activator.log("failed to open tree editor", e);
						}
					}
				});
			}
		}
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		if (!(input instanceof ValueEditorInput)) {
			throw new PartInitException("not a value input");
		}
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new ValueContentProvider());
		treeViewer.setLabelProvider(new ValueLabelProvider());
		
		IEditorInput input = getEditorInput();
		treeViewer.setInput(((ValueEditorInput) input).getValue());
	}

	@Override
	public void setFocus() {
	}
	
	private class ValueContentProvider implements ITreeContentProvider {
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getChildren(Object parentElement) {
				return ((IValue) parentElement).accept(new IValueVisitor<Object[], RuntimeException>() {
					

					public Object[] visitBoolean(IBool boolValue) {
						return empty; 
					}

					public Object[] visitConstructor(IConstructor o)
							 {
						Object[] children = new Object[o.arity()];
						int i = 0;
						for (IValue child : o) {
							children[i++] = child;
						}
						return children;
					}

					public Object[] visitExternal(IExternalValue externalValue)
							 {
						return empty;
					}

					public Object[] visitInteger(IInteger o)
							 {
						return empty;
					}

					public Object[] visitRational(IRational o)
							 {
						return empty;
					}

					public Object[] visitList(IList o)  {
						Object[] children = new Object[o.length()];
						int i = 0;
						for (IValue child : o) {
							children[i++] = child;
						}
						return children;
					}

					public Object[] visitMap(IMap o)  {
						Object[] children = new Object[o.size()];
						int i = 0;
						for (IValue child : o) {
							children[i++] = ValueFactoryFactory.getValueFactory().tuple(child, o.get(child));
						}
						return children;
					}

					public Object[] visitNode(INode o)  {
						Object[] children = new Object[o.arity()];
						int i = 0;
						for (IValue child : o) {
							children[i++] = child;
						}
						return children;
					}

					public Object[] visitReal(IReal o)  {
						return empty;
					}

					public Object[] visitRelation(ISet o)
							 {
						return visitSet(o);
					}

					public Object[] visitSet(ISet o)  {
						Object[] children = new Object[o.size()];
						int i = 0;
						for (IValue child : o) {
							children[i++] = child;
						}
						return children;
					}

					public Object[] visitSourceLocation(ISourceLocation o)
							 {
						return empty;
					}

					public Object[] visitString(IString o)  {
						return empty;
					}

					public Object[] visitTuple(ITuple o)  {
						Object[] children = new Object[o.arity()];
						int i = 0;
						for (IValue child : o) {
							children[i++] = child;
						}
						return children;
					}

					public Object[] visitDateTime(IDateTime o)
							 {
						return empty;
					}

          public Object[] visitListRelation(IList o)  {
            return visitList(o);
          }
				});
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length != 0;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
	}
	
	private class ValueLabelProvider implements ILabelProvider {
		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			IValue value = (IValue) element;
			return value.accept(new IValueVisitor<String, RuntimeException>() {

			  public String visitBoolean(IBool boolValue)
			  {
			    return boolValue.toString();
			  }

			  public String visitConstructor(IConstructor o)
			  {
			    return o.getConstructorType().toString();
			  }

			  public String visitExternal(IExternalValue externalValue)
			  {
			    return externalValue.getType().toString();
			  }

			  public String visitInteger(IInteger o)  {
			    return o.toString();
			  }

			  public String visitRational(IRational o)  {
			    return o.toString();
			  }

			  public String visitList(IList o)  {
			    return o.getType().toString();
			  }

			  public String visitMap(IMap o)  {
			    return o.getType().toString();
			  }

			  public String visitNode(INode o)  {
			    return o.getName();
			  }

			  public String visitReal(IReal o)  {
			    return o.toString();
			  }

			  public String visitRelation(ISet o)
			  {
			    return o.getType().toString();
			  }

			  public String visitSet(ISet o)  {
			    return o.getType().toString();
			  }

			  public String visitSourceLocation(ISourceLocation o) {
			    return o.toString();
			  }

			  public String visitString(IString o)  {
			    return o.toString();
			  }

			  public String visitTuple(ITuple o)  {
			    return o.getType().toString();
			  }

			  public String visitDateTime(IDateTime o) {
			    return o.toString();
			  }

			  public String visitListRelation(IList o)  {
			    return o.getType().toString();
			  }
			});
		}
	}
}
