package net.vpc.app.kifkif;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import net.vpc.common.prs.xml.XmlSerializationException;
import net.vpc.common.prs.xml.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author vpc
 * Date: 12 janv. 2005
 * Time: 19:54:24
 */
public class DefaultFileSet implements FileSet{
    private File root;
    private FileFilter fileFilter;

    public DefaultFileSet() {

    }

    public DefaultFileSet(File root) {
        this(root,null);
    }

    public DefaultFileSet(File root, FileFilter fileFilter) {
        this.root = root;
        this.fileFilter = fileFilter;
        if (root.isFile() && fileFilter != null) {
            throw new RuntimeException("Unable to add File with File Filter");
        }
    }

    public Iterator<File> iterate(final KifKif kifkif) {
        final FileFilter compoundFilter=new FileFilter() {
            public boolean accept(File pathname) {
                FileFilter globalFileFilter = kifkif.getGlobalFileFilter();
                return
                        (globalFileFilter==null || globalFileFilter.accept(pathname))
                        &&
                        (fileFilter==null || fileFilter.accept(pathname))
                        ;
            }
        };
        if (root.isFile()) {
            ArrayList<File> a = new ArrayList<File>();
            a.add(root);
            return a.iterator();
        } else {
            return new Iterator<File>() {
                Stack<File> s = new Stack<File>();

                {
                    s.push(root);
                }

                public boolean hasNext() {
                    return !s.isEmpty();
                }

                public File next() {
                    File f = s.pop();
                    if (f.isDirectory()) {
                        File[] fs = f.listFiles(compoundFilter);
                        if (fs != null) {
                            for (File file : fs) {
                                s.push(file);
                            }
                        }
                    }
                    return f;
                }

                public void remove() {
                    //not implemented
                }
            };
        }
    }

    public boolean contains(File file, KifKif kifkif) {
        FileFilter globalFileFilter = kifkif.getGlobalFileFilter();
        if(globalFileFilter !=null && !globalFileFilter.accept(file)){
            return false;
        }
        if (fileFilter == null) {
            if (root.isFile()) {
                return file.equals(root);
            } else {
                return file.getPath().replace("\\", "/").startsWith(root.getPath().replace("\\", "/") + "/");
            }
        } else {
            if (fileFilter.accept(file)) {
                if (root.isFile()) {
                    return file.equals(root);
                } else {
                    return file.getPath().replace("\\", "/").startsWith(root.getPath().replace("\\", "/") + "/");
                }
            }
        }
        return false;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String toString() {
        return root.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (toString().equals(obj) && obj instanceof DefaultFileSet) {
            DefaultFileSet f = (DefaultFileSet) obj;
            return root.equals(f.root) && (this.fileFilter == f.fileFilter || (this.fileFilter != null && this.fileFilter.equals(f.fileFilter)));
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    public void storeXmlNode(XmlSerializer serializer, Document doc, Element element) {
        try {
            Field[] declaredFields = getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    element.appendChild(serializer.createNode(doc,field.getName(),field.get(this)));
                }
            }
        } catch (IllegalAccessException e) {
            throw new XmlSerializationException(e);
        }
    }

    public void loadXmlNode(XmlSerializer serializer, Element element) {
        try {
            Field[] declaredFields = getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    Element node = (Element) element.getElementsByTagName(field.getName()).item(0);
                    field.set(this,serializer.createObject(node));
                }
            }
        } catch (IllegalAccessException e) {
            throw new XmlSerializationException(e);
        }
    }
}
