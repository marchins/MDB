package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.ConnectionErrorException;
import Exceptions.InputRicercaException;
import Exceptions.LoginException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.Libro;
import MultilanguageLabels.ErrorLabels;
import Util.ParametroRicerca;
import java.io.IOException;
import java.net.MalformedURLException;
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
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RicercaGoogleBooksTest {

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

    @Test
    public void test1() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline("9780748124794", ParametroRicerca.ISBN);
        assertEquals(1, risultati.size());
        assertEquals("Dennis Lehane", risultati.get(0).getAutore());
        assertEquals("9780748124794", risultati.get(0).getIsbn());
    }

    @Test
    public void test2() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline("9788858501121", ParametroRicerca.ISBN);

        assertEquals(0, risultati.size());
    }

    @Test
    public void test10() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String titolo = "a";

        for (int i = 1; i < 41; i++) {
            titolo = titolo + "abcde";
        }

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.TITLE_LENGTH_ERROR_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(titolo, ParametroRicerca.TITOLO);
    }

    @Test
    public void test11() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String autore = "a";

        for (int i = 1; i < 41; i++) {
            autore = autore + "abcde";
        }

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.AUTHOR_LENGTH_ERROR_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(autore, ParametroRicerca.AUTORE);
    }

    @Test
    public void test12() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "123456789";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test13() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "12345678901";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test14() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "123456789012";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test15() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "12345678901234";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test16() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "123456a890123";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test17() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String titolo = "a";

        for (int i = 1; i < 43; i++) {
            titolo = titolo + "abcde";
        }

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.TITLE_LENGTH_ERROR_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(titolo, ParametroRicerca.TITOLO);
    }

    @Test
    public void test18() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String autore = "a";

        for (int i = 1; i < 43; i++) {
            autore = autore + "abcde";
        }

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.AUTHOR_LENGTH_ERROR_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(autore, ParametroRicerca.AUTORE);
    }

    @Test
    public void test19() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "12345678";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test20() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "123456789012345";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
    }

    @Test
    public void test21() throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = "abcdefghil";

        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.ISBN_NOT_VALID_ITA);
        List<Libro> risultati = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);
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
