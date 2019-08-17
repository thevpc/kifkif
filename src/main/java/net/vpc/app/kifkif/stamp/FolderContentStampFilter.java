package net.vpc.app.kifkif.stamp;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import net.vpc.app.kifkif.Filestamp;
import net.vpc.app.kifkif.KifKif;
import net.vpc.common.prs.xml.XmlSerializationException;
import net.vpc.common.prs.xml.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FolderContentStampFilter extends AbstractFileStampFilter {

    public FolderContentStampFilter() {
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        String[] s = file.list();
        if (s == null) {
            //file no more exist
            return new DefaultFilestamp("0");
        }
        Arrays.sort(s);
        if (kifkif.isCaseInsensitiveNames()) {
            for (int i = 0; i < s.length; i++) {
                s[i] = s[i].toLowerCase();
            }
        }
        int x = 0;
        int y = 0;
        for (String s1 : s) {
            int r = s1.hashCode();
            x = 31 * x + r;
            y = 17 * y + r;
        }
        return new DefaultFilestamp(String.valueOf(x) + ";" + String.valueOf(y));
    }

    public void storeXmlNode(XmlSerializer serializer, Document doc, Element element) {
        try {
            Field[] declaredFields = getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    element.appendChild(serializer.createNode(doc, field.getName(), field.get(this)));
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
                    field.set(this, serializer.createObject(node));
                }
            }
        } catch (IllegalAccessException e) {
            throw new XmlSerializationException(e);
        }
    }
}
