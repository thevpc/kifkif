package net.vpc.kifkif.swing;

import net.vpc.common.swings.prs.PRSManager;

import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 * User: vpc
 * Date: 14 mars 2005
 * Time: 17:23:15
 */
public abstract class AbstractAction2 extends AbstractAction {
    protected AbstractAction2(String actionCommandKey) {
        super(actionCommandKey);
        putValue(Action.ACTION_COMMAND_KEY, actionCommandKey);
        PRSManager.addSupport(this, actionCommandKey);
    }
}
