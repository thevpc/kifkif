package net.vpc.kifkif.stamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;

import net.vpc.common.prs.xml.XmlSerializationException;
import net.vpc.common.prs.xml.XmlSerializer;
import net.vpc.kifkif.Filestamp;
import net.vpc.kifkif.KifKif;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: taha
 * Date: 22 dec. 2004
 * Time: 19:07:06
 */
public class FileContentMessageDigestStampFilter extends AbstractFileStampFilter {
    public FileContentMessageDigestStampFilter() {
    }

    private String algo;

    public FileContentMessageDigestStampFilter(String algo) {
        this.algo = algo;
    }

    protected Filestamp createFilestamp(File file, KifKif kifkif) {
        FileInputStream is = null;
        MessageDigest messageDigest=null;
        try {
            messageDigest=MessageDigest.getInstance(algo);

            is = new FileInputStream(file);
            int r;
            byte[] buffer=new byte[1024];
            while ((r = is.read(buffer)) != -1) {
                messageDigest.update(buffer,0,r);
            }
        } catch (Exception e) {
            //
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return new DefaultFilestamp(new String(messageDigest.digest()));
    }

    public String toString() {
        return "File Content Checksum comparator";
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
