package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.*;
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

public class RimuoviLibroTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", new Date());
        GestoreAccountLocale.login("userjohn", "passwordjohn");
    }

    /* Test rimozione di un libro con una singola copia. */
    @Test
    public void test1() throws RimozioneCopiaException, ConnectionErrorException {
        Libro libro = new Libro();
        libro.setIsbn("0018858991195");
        libro.setTitolo("Fango");
        libro.setAutore("Niccolò Ammaniti");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2005");
        libro.setCopertina("linkAllaCopertinaDiGoogle");

        GestoreLibreriaLocale.aggiungiLibro(libro);

        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();

        GestoreLibreriaLocale.rimuoviLibro(copia);

        // Verifica che non ci siano copie utente nel DB.
        List<CopiaUtente> copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();
        assertEquals(0, copie.size());

        // Verifica che non ci siano libri nel DB. 
        List<Libro> libri = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        assertEquals(0, libri.size());
    }

    /* Test rimozione di un libro con una doppia copia. */
    @Test
    public void test2() throws RimozioneCopiaException, ConnectionErrorException {
        Libro libro = new Libro();
        libro.setIsbn("0017758991122");
        libro.setTitolo("Come dio Comanda");
        libro.setAutore("Niccolò Ammaniti");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2005");
        libro.setCopertina("linkAllaCopertinaDiGoogle");

        GestoreLibreriaLocale.aggiungiLibro(libro);
        GestoreLibreriaLocale.aggiungiLibro(libro);

        List<CopiaUtente> copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();

        GestoreLibreriaLocale.rimuoviLibro(copie.get(0));

        // Verifica che ci sia una copia utente nel DB. 
        copie = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getResultList();
        assertEquals(1, copie.size());
        // Verifica che ci sia un libro nel DB. 
        List<Libro> libri = em.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
        assertEquals(1, libri.size());

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
