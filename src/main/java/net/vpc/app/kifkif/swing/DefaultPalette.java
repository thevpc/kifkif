package net.vpc.app.kifkif.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * @author vpc
 * Date: 26 janv. 2005
 * Time: 15:27:23
 */
public class DefaultPalette extends JPanel {
    protected Kkw kkw;
    private ArrayList<Action> actions=new ArrayList<Action>();
    
    public DefaultPalette(Kkw kkw) {
        this.kkw = kkw;
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        kkw.getResultTree().addPropertyChangeListener(JTree.TREE_MODEL_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        onDuplicatesChange();
                    }
                });
        kkw.addPropertyChangeListener(Kkw.PROPERTY_PROCESSING,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        onProcessingChange(((Boolean)evt.getNewValue()).booleanValue());
                    }
                });
        kkw.getResultTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                onTreeSelectionChange();
            }
        });
        kkw.getResultTree().getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                onDuplicateSelectionChange();
            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
                onDuplicatesChange();
            }
        });
    }

    protected void addAction(Action action){
    	actions.add(action);
    }
    
    public Collection<Action> getActions(){
    	return actions;
    }
    
    protected void onDuplicateSelectionChange() {
        onChange();
    }

    protected void onProcessingChange(boolean processing) {
        onChange();
    }

    protected void onDuplicatesChange() {
        onChange();
    }

    protected void onTreeSelectionChange() {
        onChange();
    }

    protected void onChange() {

    }

    public Kkw getKkw() {
        return kkw;
    }
}
