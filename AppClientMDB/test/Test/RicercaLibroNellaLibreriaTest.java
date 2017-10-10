package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.ConnectionErrorException;
import Exceptions.InputRicercaException;
import Exceptions.LoginException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
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

public class RicercaLibroNellaLibreriaTest {

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
    public void test1() throws ConnectionErrorException, InputRicercaException {
        Libro libro1 = new Libro();
        libro1.setIsbn("9990628201188");
        libro1.setTitolo("La regina dei castelli di carta");
        libro1.setAutore("Stieg Larsson");
        libro1.setCasaEditrice("Bicocca");
        libro1.setDataPubblicazione("2006");
        libro1.setCopertina("linkAllaCopertinaDiGoogle");

        Libro libro2 = new Libro();
        libro2.setIsbn("9990445507444");
        libro2.setTitolo("Io uccido");
        libro2.setAutore("Giorgio Faletti");
        libro2.setCasaEditrice("Einaudi");
        libro2.setDataPubblicazione("2001");
        libro2.setCopertina("linkAllaCopertinaDiGoogle");

        Libro libro3 = new Libro();
        libro3.setIsbn("999060850777");
        libro3.setTitolo("Mystic River");
        libro3.setAutore("Dennis Lehane");
        libro3.setCasaEditrice("Einaudi");
        libro3.setDataPubblicazione("2000");
        libro3.setCopertina("linkAllaCopertinaDiGoogle");

        // Aggiungi una copia di ciascun libro.
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        GestoreLibreriaLocale.aggiungiLibro(libro3);
        List<CopiaUtente> risultatoRicerca = GestoreLibreriaLocale.ricercaLibroNellaLibreria("Mystic", null, null, null, null, null);
        assertEquals(1, risultatoRicerca.size());
        assertEquals(libro3.getTitolo(), risultatoRicerca.get(0).getLibro().getTitolo());
        assertEquals(libro3.getAutore(), risultatoRicerca.get(0).getLibro().getAutore());
        assertEquals(libro3.getDataPubblicazione(), risultatoRicerca.get(0).getLibro().getDataPubblicazione());

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
