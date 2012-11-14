import org.junit.Test;
import org.jvnet.hyperjaxb3.xml.bind.JAXBContextUtils;
import org.jvnet.hyperjaxb3.xml.bind.JAXBElementUtils;
import org.opensourcesoftwareandme.MailsType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBElement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Hyperjaxb3ExampleTestCase {

    @Test
    public void testMapping() throws Exception {

        // Hibernate configuration
        Map<String, String> hibernateProperties = new HashMap<String, String>();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
        hibernateProperties.put("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");
        hibernateProperties.put("hibernate.connection.url", "jdbc:derby:target/test-database/database;create=true");
        hibernateProperties.put("hibernate.hbm2ddl.auto", "create");

        // initialise Hibernate
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.opensourcesoftwareandme", hibernateProperties);
        EntityManager em = emf.createEntityManager();

        // deserialize test XML document
        JAXBElement jaxbElement = (JAXBElement) JAXBContextUtils.unmarshal("org.opensourcesoftwareandme", readFileAsString("src/test/resources/mails.xml"));
        MailsType mails = (MailsType) JAXBElementUtils.getValue(jaxbElement);

        // persist object
        em.getTransaction().begin();
        em.persist(mails);
        em.getTransaction().commit();

        // retrieve persisted object
        MailsType persistedMails = (MailsType) em.createQuery("from MailsType where hjid = 1").getSingleResult();

        assertEquals("bill@microsoft.com", persistedMails.getMail().get(0).getEnvelope().getFromEnvelope());
        assertEquals("user@cduce.org", persistedMails.getMail().get(0).getEnvelope().getTo());

        em.close();
        emf.close();
    }

    private String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

}
