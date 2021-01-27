package net.thevpc.kifkif;

import java.io.File;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:41:17
 */
public class FileRemovedEvent {
    public DuplicateList list;
    public File file;
    public int index;

    public FileRemovedEvent(DuplicateList list, File file, int index) {
        this.list = list;
        this.file = file;
        this.index = index;
    }
}
