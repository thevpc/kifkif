package net.vpc.app.kifkif.content;

import net.vpc.app.kifkif.FileContentComparator;
import net.vpc.common.prs.xml.XmlSerializer;
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
