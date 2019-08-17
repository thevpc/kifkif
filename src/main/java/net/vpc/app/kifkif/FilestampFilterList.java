package net.vpc.app.kifkif;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import net.vpc.common.prs.xml.XmlSerializable;
import net.vpc.common.prs.xml.XmlSerializationException;
import net.vpc.common.prs.xml.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 18:52:28
 */
public class FilestampFilterList implements Serializable, XmlSerializable {
    private ArrayList<FilestampFilter> filters = new ArrayList<FilestampFilter>(3);

    public FilestampFilterList() {
    }

    public FilestampFilterList(FilestampFilter ... filters) {
        this.filters.addAll(Arrays.asList(filters));
    }

    public Filestamp getFirstFilterId(File file, KifKif kifKif) {
        return filters.get(0).getFilestamp(file, null, kifKif);
    }

//    public net.vpc.kifkif.Filestamp getFilterId(File file, int level) {
//        return level==0?getFirstFilterId(file):
//        getNextFilterSig(file, getFilterId(file, level-1), level);
//    }

    public Filestamp getNextFilterSig(File file, Filestamp latestFilterStamp, int level, KifKif kifkif) {
        return filters.get(level).getFilestamp(file, latestFilterStamp, kifkif);
    }

    public int getMaxLevels() {
        return filters.size();
    }

    public FilestampFilter getFilestampFilter(int level) {
        return filters.get(level);
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
