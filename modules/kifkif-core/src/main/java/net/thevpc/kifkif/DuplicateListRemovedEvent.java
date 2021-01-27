package net.thevpc.kifkif;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public class DuplicateListRemovedEvent {
    public DuplicateList list;
    public int index;

    public DuplicateListRemovedEvent(DuplicateList list, int index) {
        this.list = list;
        this.index = index;
    }
}
