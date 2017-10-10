package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.*;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import MultilanguageLabels.GenericLabels;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.*;
import static org.junit.Assert.assertEquals;

public class VisualizzaElencoCategorieTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", new Date());
        GestoreAccountLocale.login("userjohn", "passwordjohn");
    }

    /* Test visualizzazione elenco categorie vuoto. */
    @Test
    public void test1() {
        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        PrintStream stream = System.out;
        //Imposto come output del System il nostro stream di output
        System.setOut(new PrintStream(streamOut));
        GestoreLibreriaLocale.visualizzaElencoCategorie();
        assertEquals(GenericLabels.EMPTY_CATEGORIES_ITA, streamOut.toString().split("\n")[0]);
        System.setOut(stream);
    }

    /* Test visualizzazione elenco categorie. */
    @Test
    public void test2() throws CategoriaGiaEsistenteException, CreazioneCategoriaException, ConnectionErrorException {
        GestoreLibreriaLocale.creaCategoria("Categoria1");
        GestoreLibreriaLocale.creaCategoria("Categoria2");
        GestoreLibreriaLocale.creaCategoria("Categoria3");

        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        PrintStream stream = System.out;
        //Imposto come output del System il nostro stream di output
        System.setOut(new PrintStream(streamOut));
        GestoreLibreriaLocale.visualizzaElencoCategorie();
        assertEquals("Categoria1", streamOut.toString().split("\n")[0]);
        assertEquals("Categoria2", streamOut.toString().split("\n")[1]);
        assertEquals("Categoria3", streamOut.toString().split("\n")[2]);
        System.setOut(stream);

    }

    @After
    public void tearDownTest() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Categoria c").executeUpdate();
        em.getTransaction().commit();

        DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
        dbUtility.svuotaTabellaCategoria();
    }

    @AfterClass
    public static void tearDownClass() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Account c").executeUpdate();
        em.getTransaction().commit();
        em.close();
        DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
        dbUtility.svuotaTabellaAccount();
    }

    private static DatabaseUtilityRemote lookupDatabaseUtilityRemote() {
        try {
            Properties p = new Properties();
            p.put("java.naming.factory.initial", "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            p.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            p.put("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
            Context c = new InitialContext();
            return (DatabaseUtilityRemote) c.lookup("java:global/ServerMDB/ServerMDB-ejb/DatabaseUtility!DatabaseUtility.DatabaseUtilityRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

}
