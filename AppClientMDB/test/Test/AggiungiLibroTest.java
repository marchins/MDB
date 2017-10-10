package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Enumerations.Formato;
import Enumerations.StatoLettura;
import Enumerations.Valutazione;
import Exceptions.ConnectionErrorException;
import Exceptions.LoginException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.Account;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import MultilanguageLabels.GenericLabels;
import java.io.IOException;
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
import org.junit.Test;

public class AggiungiLibroTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", new Date());
        GestoreAccountLocale.login("userjohn", "passwordjohn");
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

    /* Test: aggiungi un libro e verifica il database locale. */
    @Test
    public void test1() throws IOException, ConnectionErrorException {
        Libro libro = new Libro();
        libro.setIsbn("9788889765439");
        libro.setTitolo("Uomini che odiano le donne");
        libro.setAutore("Stieg Larsson");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2004");
        libro.setCopertina("linkAllaCopertinaDiGoogle");

        GestoreLibreriaLocale.aggiungiLibro(libro);

        List<Libro> libri = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        assertEquals(1, libri.size());
        assertEquals("9788889765439", libri.get(0).getIsbn());
        assertEquals("Uomini che odiano le donne", libri.get(0).getTitolo());
        assertEquals("Stieg Larsson", libri.get(0).getAutore());
        assertEquals("Bicocca", libri.get(0).getCasaEditrice());
        assertEquals("2004", libri.get(0).getDataPubblicazione());
        assertEquals("linkAllaCopertinaDiGoogle", libri.get(0).getCopertina());

        List<CopiaUtente> copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();
        assertEquals(1, copie.size());
        assertEquals(1, copie.get(0).getNumeroCopia());
        assertEquals(Formato.CARTACEO, copie.get(0).getFormato());
        assertEquals(StatoLettura.NON_SPECIFICATO, copie.get(0).getStatoLettura());
        assertEquals(Valutazione.NON_VALUTATO, copie.get(0).getValutazione());
        assertEquals(GenericLabels.NOT_SPECIFIED_ITA, copie.get(0).getNomelibreria());
        assertEquals(GenericLabels.NOT_SPECIFIED_ITA, copie.get(0).getPosizioneNellaLibreria());
        Account account = em.createQuery("SELECT a FROM Account a", Account.class).getSingleResult();
        assertEquals(account.getId(), copie.get(0).getAccount().getId());
    }

    /* Test: aggiungi due copie dello stesso libro e verifica le copie utente. */
    @Test
    public void test2() throws IOException, ConnectionErrorException {
        Libro libro = new Libro();
        libro.setIsbn("9998858501122");
        libro.setTitolo("La ragazza che giocava con il fuoco");
        libro.setAutore("Stieg Larsson");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2005");
        libro.setCopertina("linkAllaCopertinaDiGoogle");

        GestoreLibreriaLocale.aggiungiLibro(libro);
        GestoreLibreriaLocale.aggiungiLibro(libro);

        List<Libro> libri = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        assertEquals(1, libri.size());

        List<CopiaUtente> copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();
        assertEquals(2, copie.size());

        assertEquals(1, copie.get(0).getNumeroCopia());
        assertEquals(2, copie.get(1).getNumeroCopia());
    }

    /* Test: aggiungi due libri e due copie ciascuno. */
    @Test
    public void test3() throws IOException, ConnectionErrorException {
        Libro libro1 = new Libro();
        libro1.setIsbn("9990608501122");
        libro1.setTitolo("La regina dei castelli di carta");
        libro1.setAutore("Stieg Larsson");
        libro1.setCasaEditrice("Bicocca");
        libro1.setDataPubblicazione("2006");
        libro1.setCopertina("linkAllaCopertinaDiGoogle");

        Libro libro2 = new Libro();
        libro2.setIsbn("9990608507501");
        libro2.setTitolo("Io uccido");
        libro2.setAutore("Giorgio Faletti");
        libro2.setCasaEditrice("Einaudi");
        libro2.setDataPubblicazione("2001");
        libro2.setCopertina("linkAllaCopertinaDiGoogle");

        // Aggiungi una copia di ciascun libro.
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        GestoreLibreriaLocale.aggiungiLibro(libro2);

        List<Libro> libri = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        assertEquals(2, libri.size());

        List<CopiaUtente> copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();
        assertEquals(2, copie.size());

        // Aggiungi una seconda copia di ciascun libro. 
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        GestoreLibreriaLocale.aggiungiLibro(libro2);

        libri = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        assertEquals(2, libri.size());

        copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();
        assertEquals(4, copie.size());

        List<CopiaUtente> copieLibro1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '9990608501122'", CopiaUtente.class).getResultList();

        assertEquals(2, copieLibro1.size());

        List<CopiaUtente> copieLibro2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '9990608507501'", CopiaUtente.class).getResultList();

        assertEquals(2, copieLibro2.size());
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
