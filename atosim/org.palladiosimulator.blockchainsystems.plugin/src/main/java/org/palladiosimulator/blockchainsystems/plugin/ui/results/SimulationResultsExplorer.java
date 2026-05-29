package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.part.ViewPart;

import java.io.File;

public abstract class SimulationResultsExplorer extends ViewPart {

    private final ResultsRepositoryContainer _repositoryContainer = new ResultsRepositoryContainer();
    private TreeViewer _viewer;

    private static class DirectoryContentProvider extends JsonTreeContentProvider {
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ResultsRepositoryContainer c) {
                return c.resultRepositories.toArray();
            }
            return new Object[0];
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ResultsRepository r) {
                return r.getSimulationResultFiles().toArray();
            }
            if (parentElement instanceof SimulationResultFile f) {
                return super.getElements(f.getJsonContent());
            }
            return super.getChildren(parentElement);
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof ResultsRepositoryContainer) return null;
            if (element instanceof SimulationResultFile f) return f.repository;
            if (element instanceof ResultsRepository r) return r.container;
            return super.getParent(element);
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof ResultsRepositoryContainer c) return !c.resultRepositories.isEmpty();
            if (element instanceof ResultsRepository r) return !r.getSimulationResultFiles().isEmpty();
            if (element instanceof SimulationResultFile f) return super.hasChildren(f.getJsonContent());
            return super.hasChildren(element);
        }
    }

    private static class DirectoryLabelProvider extends JsonTreeLabelProvider {
        @Override
        public String getText(Object element) {
            if (element instanceof ResultsRepositoryContainer) return "Repositories";
            if (element instanceof ResultsRepository r) {
                return r.directory != null
                        ? r.directory.getName() + " (" + r.directory.getPath() + ")"
                        : "Unknown Repository";
            }
            if (element instanceof SimulationResultFile f) {
                return f.file != null ? f.file.getName() : "Unknown File";
            }
            return super.getText(element);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayoutFactory.swtDefaults().numColumns(1).spacing(0, 0).equalWidth(true).applyTo(parent);

        _viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(_viewer.getTree());
        _viewer.setContentProvider(new DirectoryContentProvider());
        _viewer.setLabelProvider(new DirectoryLabelProvider());
        _viewer.setInput(_repositoryContainer);

        var toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(createLoadRepositoryAction(parent));

        Action refreshAction = new Action() {
            @Override
            public void run() { _viewer.refresh(); }
        };
        refreshAction.setText("Refresh");
        refreshAction.setToolTipText("Refresh");
        toolBarManager.add(refreshAction);

        parent.layout();
    }

    private Action createLoadRepositoryAction(Composite parent) {
        Action action = new Action() {
            @Override
            public void run() {
                DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.OPEN);
                dialog.setText("Select a Directory");
                dialog.setFilterPath(System.getProperty("user.home"));
                String directoryPath = dialog.open();
                if (directoryPath != null) {
                    File directory = new File(directoryPath);
                    if (directory.isDirectory()) {
                        _repositoryContainer.addRepository(directory);
                        _viewer.refresh();
                    }
                }
            }
        };
        action.setText("Load Repository");
        action.setToolTipText("Load a simulation results repository");
        return action;
    }

    @Override
    public void setFocus() {
        _viewer.getControl().setFocus();
    }
}
