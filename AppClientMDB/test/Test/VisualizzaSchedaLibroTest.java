package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.ConnectionErrorException;
import Exceptions.InputRicercaException;
import Exceptions.LoginException;
import Exceptions.ModificaInformazioniException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class VisualizzaSchedaLibroTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        String miaData = "28/03/1988";
        Date data = formatDate.parse(miaData);

        // Registrazione di un account. 
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", data);
        GestoreAccountLocale.login("userjohn", "passwordjohn");
    }

    @Test
    public void test1() throws IOException, MalformedURLException, ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {

        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        PrintStream stream = System.out;

        //Imposto come output del System il nostro stream di output
        System.setOut(new PrintStream(streamOut));

        Libro libro = new Libro();
        libro.setIsbn("9784432002176");
        libro.setTitolo("Uomini che odiano le donne");
        libro.setAutore("Stieg Larsson");
        libro.setCasaEditrice("Bicocca");
        libro.setDataPubblicazione("2004");
        libro.setCopertina("9784432002176.jpg");

        GestoreLibreriaLocale.aggiungiLibro(libro);

        List<CopiaUtente> copieUtente = GestoreLibreriaLocale.ricercaLibroNellaLibreria(libro.getTitolo(), null, null, null, null, null);
        CopiaUtente copia = copieUtente.get(0);

        //visualizzo la scheda libro ad esso associata
        GestoreLibreriaLocale.visualizzaSchedaLibro(copia);
        String expectedOutput = "\nIsbn: 9784432002176\n"
                + "Titolo: Uomini che odiano le donne\n"
                + "Autore: Stieg Larsson\n"
                + "Casa editrice: Bicocca\n"
                + "Data di pubblicazione: 2004\n"
                + "Copertina: img/default.jpg\n"
                + "Numero copia: 1\n"
                + "Formato: Cartaceo\n"
                + "Stato lettura: Non specificato\n"
                + "Valutazione: Non valutato\n"
                + "Libreria: Non specificato\n"
                + "Posizione nella libreria: Non specificato\n";
        assertEquals(expectedOutput, streamOut.toString());

        System.setOut(stream);
    }

    @After
    public void tearDownTest() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CopiaUtente c").executeUpdate();
        em.createQuery("DELETE FROM Libro l").executeUpdate();
        em.getTransaction().commit();

        DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
        dbUtility.svuotaTabellaCopieUtenti();
        dbUtility.svuotaTabellaLibro();
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
