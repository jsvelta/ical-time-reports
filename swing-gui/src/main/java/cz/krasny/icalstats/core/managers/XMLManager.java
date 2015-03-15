package cz.krasny.icalstats.core.managers;

import cz.krasny.icalstats.data.classes.ICalRepresentation;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/***
 * Provides access to XML files.
 * @author Tomas Krasny
 */
public class XMLManager {
    
    private static XMLManager instance = null;
    private static final String filename = "data_icr.xml";
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    public static XMLManager getInstance(){
        if(instance == null) instance = new XMLManager();
        return instance;
    }
    
    private XMLManager() {}
    
    /* Tests if this run is the first. */
    private void testFirstRun() throws Exception{
        File f = new File(filename);
        if(!f.exists() || f.isDirectory()){
            saveICalRepresentations(new ArrayList<ICalRepresentation>());
        }
    }
    
    /* Loads ical representations from XML file. */
    public List<ICalRepresentation> loadICalRepresentations() throws Exception{
        testFirstRun();
        List<ICalRepresentation> list = new ArrayList<>();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));
        doc.getDocumentElement().normalize();
        NodeList n_list = doc.getElementsByTagName(XML_SINGLE_ICR);
        Node node = null;
        Element element = null;
        String name, path, url;
        ICalRepresentation icr = null;
        for(int i = 0 ; i < n_list.getLength() ; i++){
            node = n_list.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                icr = new ICalRepresentation();
                element = (Element) node;
                name = element.getElementsByTagName(XML_ICR_NAME).item(0).getTextContent();
                url = element.getElementsByTagName(XML_ICR_URL).item(0).getTextContent();
                path = element.getElementsByTagName(XML_ICR_PATH).item(0).getTextContent();
                if(url.length() == 0) 
                    icr.setPath(path);
                else
                    icr.setUrl(new URL(url));
                if(name.length() == 0) 
                    throw new IllegalArgumentException("Calendar name can not be empty.");
                icr.setName(name);
                list.add(icr);
            }
        }
        return list;
    }
    
    /* Saves ical representations to XML file */
    public void saveICalRepresentations(List<ICalRepresentation> list_icr) throws Exception{
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        doc.setXmlStandalone(true);
        Element root = doc.createElement(XML_ICAL_REPRESENTATIONS);
        doc.appendChild(root);
        Element single_icr, name, url, path;
        for(ICalRepresentation icr: list_icr){
            single_icr = doc.createElement(XML_SINGLE_ICR);
            name = doc.createElement(XML_ICR_NAME);
            name.appendChild(doc.createTextNode(icr.getName()));
            url = doc.createElement(XML_ICR_URL);
            url.appendChild(doc.createTextNode(icr.getUrl() == null ? "" : icr.getUrl().toString()));
            path = doc.createElement(XML_ICR_PATH);
            path.appendChild(doc.createTextNode(icr.getPath() == null ? "" : icr.getPath()));
            single_icr.appendChild(name);
            single_icr.appendChild(url);
            single_icr.appendChild(path);
            root.appendChild(single_icr);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource ds = new DOMSource(doc);
        StreamResult sr = new StreamResult(new FileOutputStream(filename));
        t.transform(ds, sr);
    }
    
    public String getFilename(){
        return filename;
    }
    
    private static final String XML_ICAL_REPRESENTATIONS = "icalrepresentations";
    private static final String XML_SINGLE_ICR = "icr";
    private static final String XML_ICR_NAME = "name";
    private static final String XML_ICR_URL = "url";
    private static final String XML_ICR_PATH = "path";
    
}
