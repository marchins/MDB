package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.*;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import MultilanguageLabels.ErrorLabels;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ModificaInformazioniLibroTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", new Date());
        GestoreAccountLocale.login("userjohn", "passwordjohn");
    }

    /* Test per nome libreria troppo lungo. */
    @Test
    public void test1() throws ModificaInformazioniException, VincoliInputException, ConnectionErrorException {
        Libro libro = new Libro();
        libro.setIsbn("9018858501122");
        libro.setTitolo("La ragazza che giocava con il fuoco");
        libro.setAutore("Stieg Larsson");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2005");
        libro.setCopertina("linkAllaCopertinaDiGoogle");

        GestoreLibreriaLocale.aggiungiLibro(libro);

        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();

        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.LIBRARY_LENGTH_ERROR_ITA);
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia, null, null, null,
                "librerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibreria", null);
    }

    /* Test per posizione nella libreria troppo lungo. */
    @Test
    public void test2() throws ModificaInformazioniException, VincoliInputException, ConnectionErrorException {
        Libro libro = new Libro();
        libro.setIsbn("9778858501199");
        libro.setTitolo("La regina dei castelli di carta");
        libro.setAutore("Stieg Larsson");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2005");
        libro.setCopertina("linkAllaCopertinaDiGoogle");

        GestoreLibreriaLocale.aggiungiLibro(libro);

        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();

        exception.expect(VincoliInputException.class);
        exception.expectMessage(ErrorLabels.POSITION_LENGTH_ERROR_ITA);
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia, null, null, null, null,
                "librerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibrerialibreria");
    }

    @After
    public void tearDownTest() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CopiaUtente cu").executeUpdate();
        em.createQuery("DELETE FROM Libro l").executeUpdate();
        em.getTransaction().commit();

        DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
        dbUtility.svuotaTabellaCopieUtenti();
        dbUtility.svuotaTabellaLibro();
    }

    @AfterClass
    public static void tearDownClass() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Account a").executeUpdate();
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
