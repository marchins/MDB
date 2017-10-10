package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.ConnectionErrorException;
import Exceptions.LoginException;
import Exceptions.RegistrazioneException;
import Exceptions.VincoliInputException;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.Libro;
import MultilanguageLabels.GenericLabels;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class VisualizzaLibreriaTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, RegistrazioneException, ParseException, LoginException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        String miaData = "28/03/1988";
        Date data = formatDate.parse(miaData);

        // Registrazione di un account.
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", data);
        // Login al sistema.
        GestoreAccountLocale.login("userjohn", "passwordjohn");
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
        // Simulo un logout cancellando l'account memorizzato in locale.
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Account a").executeUpdate();
        em.getTransaction().commit();
        em.close();
        // Elimina la registrazione cancellando l'oggetto account nel DB remoto.
        DatabaseUtilityRemote dbUtility = lookupDatabaseUtilityRemote();
        dbUtility.svuotaTabellaAccount();
    }

    /* Test visualizzazione libreria vuota. */
    @Test
    public void test1() {
        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        PrintStream stream = System.out;
        //Imposto come output del System il nostro stream di output
        System.setOut(new PrintStream(streamOut));
        GestoreLibreriaLocale.visualizzaLibreriaDigitale();
        assertEquals(GenericLabels.EMPTY_LIBRARY_ITA, streamOut.toString().split("\n")[0]);
        System.setOut(stream);
    }

    @Test
    public void test2() throws ConnectionErrorException {

        String expectedOutput = "\nTitolo: 1984\n"
                + "Autore: George Orwell\n"
                + "Copertina: img/default.jpg\n"
                + "Titolo: Dracula\n"
                + "Autore: Bram Stoker\n"
                + "Copertina: img/default.jpg\n"
                + "Titolo: La solitudine dei numeri primi\n"
                + "Autore: Paolo Giordano\n"
                + "Copertina: img/default.jpg\n";

        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        PrintStream stream = System.out;
        //Imposto come output del System il nostro stream di output
        System.setOut(new PrintStream(streamOut));

        Libro libroUno = new Libro();
        libroUno.setAutore("Bram Stoker");
        libroUno.setTitolo("Dracula");
        libroUno.setIsbn("9788804492788");
        libroUno.setCasaEditrice("Oscar Mondadori");
        libroUno.setDataPubblicazione("2003");
        libroUno.setCopertina("9788804492788.jpg");

        Libro libroDue = new Libro();
        libroDue.setAutore("George Orwell");
        libroDue.setTitolo("1984");
        libroDue.setIsbn("9788804108191");
        libroDue.setCasaEditrice("Oscar Mondadori");
        libroDue.setDataPubblicazione("1989");
        libroDue.setCopertina("9788804108191.jpg");

        Libro libroTre = new Libro();
        libroTre.setAutore("Paolo Giordano");
        libroTre.setTitolo("La solitudine dei numeri primi");
        libroTre.setIsbn("9788804577027");
        libroTre.setCasaEditrice("Oscar Mondadori");
        libroTre.setDataPubblicazione("2008");
        libroTre.setCopertina("9788804577027.jpg");

        GestoreLibreriaLocale.aggiungiLibro(libroTre);
        GestoreLibreriaLocale.aggiungiLibro(libroDue);
        GestoreLibreriaLocale.aggiungiLibro(libroUno);

        GestoreLibreriaLocale.visualizzaLibreriaDigitale();

        assertEquals(expectedOutput, streamOut.toString());
        System.setOut(stream);
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
