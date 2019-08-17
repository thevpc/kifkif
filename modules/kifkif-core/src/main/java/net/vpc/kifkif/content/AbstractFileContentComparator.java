package net.vpc.kifkif.content;

import net.vpc.common.prs.xml.XmlSerializer;
import net.vpc.kifkif.FileContentComparator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractFileContentComparator implements FileContentComparator {
    public AbstractFileContentComparator() {
    }

    public void storeXmlNode(XmlSerializer serializer, Document doc, Element element) {
    }

    public void loadXmlNode(XmlSerializer serializer, Element element) {
    }

}
