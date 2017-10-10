package TestFinali;

import DatabaseUtility.DatabaseUtilityRemote;
import Enumerations.Formato;
import Enumerations.StatoLettura;
import Enumerations.Valutazione;
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
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RicercaLibroNellaLibreriaTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", new Date());
        GestoreAccountLocale.login("userjohn", "passwordjohn");
    }

    @Before
    public void setUp() {
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

    /* Test con tutti i valori di input specificati. */
    @Test
    public void test1() throws InputRicercaException, ConnectionErrorException, ModificaInformazioniException, VincoliInputException {
        //parametri usati per la ricerca
        String titolo = "nome della";
        String autore = "Umberto Mario";
        Valutazione valutazione = Valutazione.QUATTRO;
        StatoLettura statoLettura = StatoLettura.NON_LETTO;
        Formato formato = Formato.CARTACEO;
        String libreria = "libreria sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("9988776655443");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("9988776655443.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, formato, statoLettura, valutazione, libreria, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Nel nome della verità");
        libro2.setAutore("Umberto Mario Galimberti");
        libro2.setIsbn("8877665544332");
        libro2.setCasaEditrice("Bompiani");
        libro2.setDataPubblicazione("2005");
        libro2.setCopertina("8877665544332.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, formato, statoLettura, valutazione, libreria, null);

        //creo terzo libro
        Libro libro3 = new Libro();
        libro3.setTitolo("Il nome della vittoria");
        libro3.setAutore("Umberto Mario Sironi");
        libro3.setIsbn("7766554433221");
        libro3.setCasaEditrice("Feltrinelli");
        libro3.setDataPubblicazione("2005");
        libro3.setCopertina("7766554433221.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro3);
        //recupero la copia utente
        CopiaUtente copia3 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro3.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia3, formato, statoLettura, valutazione, libreria, null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di trovare tutti i 3 libri inseriti
        assertEquals(3, copie.size());
    }

    /* Test per titolo con lunghezza compresa tra 1 e 200 caratteri. */
    @Test
    public void test2() throws InputRicercaException, ConnectionErrorException {
        //parametro usato per la ricerca
        String titolo = "ragazza fuoco";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("La ragazza che giocava con il fuoco");
        libro1.setAutore("Stieg Larsson");
        libro1.setIsbn("1234567890123");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("2011");
        libro1.setCopertina("1234567890123.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("La ragazza di fuoco - hunger games");
        libro2.setAutore("Suzanne Collins");
        libro2.setIsbn("2345678901234");
        libro2.setCasaEditrice("Mondadori");
        libro2.setDataPubblicazione("2009");
        libro2.setCopertina("2345678901234.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, null, null, null, null, null);

        //mi aspetto di trovare tutti i 3 libri inseriti
        assertEquals(2, copie.size());
    }

    /* Test per autore con lunghezza compresa tra 1 e 200 caratteri. */
    @Test
    public void test3() throws InputRicercaException, ConnectionErrorException {
        //parametro usato per la ricerca
        String autore = "Carlo Alberto";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il biologo furioso");
        libro1.setAutore("Carlo Alberto Redi");
        libro1.setIsbn("3456789012345");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("2011");
        libro1.setCopertina("3456789012345.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("UML for dummies");
        libro2.setAutore("Carlo Albertoni");
        libro2.setIsbn("4567890123456");
        libro2.setCasaEditrice("Mondadori");
        libro2.setDataPubblicazione("2009");
        libro2.setCopertina("4567890123456.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, autore, null, null, null, null);

        //mi aspetto di trovare tutti i 2 libri inseriti
        assertEquals(2, copie.size());
    }

    /* Test per valutazione valido. */
    @Test
    public void test4() throws InputRicercaException, ConnectionErrorException, ModificaInformazioniException, VincoliInputException {
        //parametro usato per la ricerca
        Valutazione valutazione = Valutazione.TRE;

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Fight Club");
        libro1.setAutore("Chuck Palahniuk");
        libro1.setIsbn("5678901234567");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1996");
        libro1.setCopertina("5678901234567.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, null, null, valutazione, null, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Il cinema secondo Hitchcock");
        libro2.setAutore("Francois Truffaut");
        libro2.setIsbn("6789012345678");
        libro2.setCasaEditrice("Il Saggiatore");
        libro2.setDataPubblicazione("2014");
        libro2.setCopertina("6789012345678.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, null, null, valutazione, null, null);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, valutazione, null, null, null);

        //mi aspetto di trovare tutti i 2 libri inseriti
        assertEquals(2, copie.size());
    }

    /* Test per stato lettura valido. */
    @Test
    public void test5() throws InputRicercaException, ConnectionErrorException, ModificaInformazioniException, VincoliInputException {
        //parametro usato per la ricerca
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Fight Club");
        libro1.setAutore("Chuck Palahniuk");
        libro1.setIsbn("7890123456789");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1996");
        libro1.setCopertina("7890123456789.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, null, statoLettura, null, null, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Il cinema secondo Hitchcock");
        libro2.setAutore("Francois Truffaut");
        libro2.setIsbn("8901234567890");
        libro2.setCasaEditrice("Il Saggiatore");
        libro2.setDataPubblicazione("2014");
        libro2.setCopertina("8901234567890.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, null, statoLettura, null, null, null);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, statoLettura, null, null);

        //mi aspetto di trovare tutti i 2 libri inseriti
        assertEquals(2, copie.size());
    }

    /* Test per formato valido. */
    @Test
    public void test6() throws InputRicercaException, ConnectionErrorException, ModificaInformazioniException, VincoliInputException {
        //parametro usato per la ricerca
        Formato formato = Formato.DIGITALE;

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("9012345678901");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("9012345678901.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, formato, null, null, null, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Nel nome della verità");
        libro2.setAutore("Umberto Mario Galimberti");
        libro2.setIsbn("0123456789012");
        libro2.setCasaEditrice("Bompiani");
        libro2.setDataPubblicazione("2005");
        libro2.setCopertina("0123456789012.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, formato, null, null, null, null);

        //creo terzo libro
        Libro libro3 = new Libro();
        libro3.setTitolo("Il nome della vittoria");
        libro3.setAutore("Umberto Mario Sironi");
        libro3.setIsbn("6655443322110");
        libro3.setCasaEditrice("Feltrinelli");
        libro3.setDataPubblicazione("2005");
        libro3.setCopertina("6655443322110.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro3);
        //recupero la copia utente
        CopiaUtente copia3 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro3.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia3, formato, null, null, null, null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, formato, null);

        //mi aspetto di trovare tutti i 3 libri inseriti
        assertEquals(3, copie.size());
    }

    /* Test per libreria con lunghezza compresa tra 1 e 200 caratteri. */
    @Test
    public void test7() throws InputRicercaException, ConnectionErrorException, ModificaInformazioniException, VincoliInputException {
        //parametro usato per la ricerca
        String libreria = "libreria soggiorno sopra il divano";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("La ragazza che giocava con il fuoco");
        libro1.setAutore("Stieg Larsson");
        libro1.setIsbn("5544332211001");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("2011");
        libro1.setCopertina("5544332211001.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, null, null, null, libreria, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("La ragazza di fuoco - hunger games");
        libro2.setAutore("Suzanne Collins");
        libro2.setIsbn("4433221100112");
        libro2.setCasaEditrice("Mondadori");
        libro2.setDataPubblicazione("2009");
        libro2.setCopertina("4433221100112.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, null, null, null, libreria, null);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, libreria);

        //mi aspetto di trovare tutti i 3 libri inseriti
        assertEquals(2, copie.size());
    }

    /* Test per titolo, autore e nome libreria, tutti con lunghezza pari a 1 carattere. */
    @Test
    public void test8() throws ModificaInformazioniException, VincoliInputException, ConnectionErrorException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "N";
        String autore = "L";
        String libreria = "S";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("De Rerum Natura");
        libro1.setAutore("Lucrezio");
        libro1.setIsbn("3322110011223");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("2000");
        libro1.setCopertina("3322110011223.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, null, null, null, "Libreria soggiorno", null);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, null, null, null, libreria);

        //mi aspetto di trovare il libro inserito
        assertEquals(1, copie.size());
    }

    /* Test per titolo, autore e nome libreria, tutti con lunghezza compresa tra 1 e 200 caratteri. */
    @Test
    public void test9() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "Natura";
        String autore = "Lucrezio";
        String libreria = "Soggiorno";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("De Rerum Natura");
        libro1.setAutore("Tito Lucrezio Caro");
        libro1.setIsbn("2211001122334");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("2000");
        libro1.setCopertina("2211001122334.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, null, null, null, "Libreria soggiorno", null);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, null, null, null, libreria);

        //mi aspetto di trovare il libro inserito
        assertEquals(1, copie.size());
    }

    /* Test per titolo, autore e nome libreria, tutti con lunghezza pari a 200 caratteri. */
    @Test
    public void test10() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //compongo il parametro titolo di una sola parola di 200 caratteri
        String titolo = "";
        String tmp = "rerumrerum";
        for (int i = 0; i < 20; i++) {
            titolo = titolo + tmp;
        }
        //compongo il parametro autore di una sola parola di 200 caratteri
        String autore = "";
        String tmp2 = "lucrezio";
        for (int i = 0; i < 25; i++) {
            autore = autore + tmp2;
        }
        //compongo il parametro libreria di una sola parola di 200 caratteri
        String libreria = "";
        String tmp3 = "libreria";
        for (int i = 0; i < 25; i++) {
            libreria = libreria + tmp3;
        }

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo(titolo);
        libro1.setAutore(autore);
        libro1.setIsbn("1100112233445");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("2000");
        libro1.setCopertina("1100112233445.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, null, null, null, libreria, null);

        //ricerco libri per titolo
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, null, null, null, libreria);

        //mi aspetto di trovare il libro inserito
        assertEquals(1, copie.size());
    }

    /* Test per titolo con lunghezza pari a 201 caratteri. */
    @Test
    public void test11() throws InputRicercaException {
        String titolo = "t";
        String venticaratteri = "questison20caratteri";
        for (int i = 0; i <= 9; i++) {
            titolo = titolo + venticaratteri;
        }
        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.TITLE_LENGTH_ERROR_ITA);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, null, null, null, null, null);
    }

    /* Test per autore con lunghezza pari a 201 caratteri. */
    @Test
    public void test12() throws InputRicercaException {
        String autore = "a";
        String venticaratteri = "questison20caratteri";
        for (int i = 0; i <= 9; i++) {
            autore = autore + venticaratteri;
        }
        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.AUTHOR_LENGTH_ERROR_ITA);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, autore, null, null, null, null);
    }

    /* Test per libreria con lunghezza pari a 201 caratteri. */
    @Test
    public void test13() throws InputRicercaException {
        String libreria = "l";
        String venticaratteri = "questison20caratteri";
        for (int i = 0; i <= 9; i++) {
            libreria = libreria + venticaratteri;
        }
        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.LIBRARY_LENGTH_ERROR_ITA);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, libreria);
    }

    /* Test con tutti i valori di input specificati e nel database K libri che soddisfano tutti i vincoli tranne uno. */
    @Test
    public void test14() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "nome della";
        String autore = "Umberto Mario";
        Valutazione valutazione = Valutazione.UNO;
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;
        Formato formato = Formato.DIGITALE;
        String libreria = "libreria sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("0011223344556");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("0011223344556.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, formato, statoLettura, Valutazione.DUE, libreria, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Nel nome della verità");
        libro2.setAutore("Umberto Mario Galimberti");
        libro2.setIsbn("1122334455667");
        libro2.setCasaEditrice("Bompiani");
        libro2.setDataPubblicazione("2005");
        libro2.setCopertina("1122334455667.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, formato, StatoLettura.LETTO, valutazione, libreria, null);

        //creo terzo libro
        Libro libro3 = new Libro();
        libro3.setTitolo("Il nome della vittoria");
        libro3.setAutore("Umberto Mario Sironi");
        libro3.setIsbn("2233445566778");
        libro3.setCasaEditrice("Feltrinelli");
        libro3.setDataPubblicazione("2005");
        libro3.setCopertina("2233445566778.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro3);
        //recupero la copia utente
        CopiaUtente copia3 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro3.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia3, formato, statoLettura, valutazione, "mensola sopra televisione", null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di non trovare nessun libro
        assertEquals(0, copie.size());
    }

    /* Test con tutti i valori di input specificati e nel database 1 libro che soddisfa tutti i vincoli tranne uno. */
    @Test
    public void test15() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "nome della";
        String autore = "Umberto Mario";
        Valutazione valutazione = Valutazione.UNO;
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;
        Formato formato = Formato.DIGITALE;
        String libreria = "libreria sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("3344556677889");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("3344556677889.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, formato, statoLettura, Valutazione.QUATTRO, libreria, null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di non trovare nessun libro
        assertEquals(0, copie.size());
    }

    /* Test per titolo con lunghezza superiore ai 200 caratteri. */
    @Test
    public void test16() throws InputRicercaException {
        String titolo = "titolo";
        String venticaratteri = "questison20caratteri";
        for (int i = 0; i <= 9; i++) {
            titolo = titolo + venticaratteri;
        }
        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.TITLE_LENGTH_ERROR_ITA);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, null, null, null, null, null);
    }

    /* Test per autore con lunghezza superiore ai 200 caratteri. */
    @Test
    public void test17() throws InputRicercaException {
        String autore = "autore";
        String venticaratteri = "questison20caratteri";
        for (int i = 0; i <= 9; i++) {
            autore = autore + venticaratteri;
        }
        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.AUTHOR_LENGTH_ERROR_ITA);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, autore, null, null, null, null);
    }

    /* Test per libreria con lunghezza superiore ai 200 caratteri. */
    @Test
    public void test21() throws InputRicercaException {
        String libreria = "libreria";
        String venticaratteri = "questison20caratteri";
        for (int i = 0; i <= 9; i++) {
            libreria = libreria + venticaratteri;
        }
        exception.expect(InputRicercaException.class);
        exception.expectMessage(ErrorLabels.LIBRARY_LENGTH_ERROR_ITA);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, libreria);
    }

    /* Test con tutti i valori di input specificati e nel database K libri che soddisfano n-m vincoli. */
    @Test
    public void test22() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "nome della";
        String autore = "Giorgio";
        Valutazione valutazione = Valutazione.UNO;
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;
        Formato formato = Formato.DIGITALE;
        String libreria = "libreria sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("4455667788990");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("4455667788990.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, formato, statoLettura, Valutazione.DUE, libreria, null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Nel nome della verità");
        libro2.setAutore("Umberto Mario Galimberti");
        libro2.setIsbn("5566778899001");
        libro2.setCasaEditrice("Bompiani");
        libro2.setDataPubblicazione("2005");
        libro2.setCopertina("5566778899001.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, formato, StatoLettura.LETTO, valutazione, libreria, null);

        //creo terzo libro
        Libro libro3 = new Libro();
        libro3.setTitolo("Il nome della vittoria");
        libro3.setAutore("Umberto Mario Sironi");
        libro3.setIsbn("6677889900112");
        libro3.setCasaEditrice("Feltrinelli");
        libro3.setDataPubblicazione("2005");
        libro3.setCopertina("6677889900112.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro3);
        //recupero la copia utente
        CopiaUtente copia3 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro3.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia3, formato, statoLettura, valutazione, "mensola sopra televisione", null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di non trovare nessun libro
        assertEquals(0, copie.size());
    }

    /* Test con tutti i valori di input specificati e nel database 1 libro che soddisfa n-m vincoli. */
    @Test
    public void test23() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "nome della";
        String autore = "Giorgio";
        Valutazione valutazione = Valutazione.UNO;
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;
        Formato formato = Formato.DIGITALE;
        String libreria = "libreria sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("7788990011223");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("7788990011223.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, formato, StatoLettura.NON_LETTO, Valutazione.DUE, libreria, null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di non trovare nessun libro
        assertEquals(0, copie.size());
    }

    /* Test con tutti i valori di input specificati e nel database K libri che non soddisfano nessun vincolo. */
    @Test
    public void test24() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "Ti prendo e ti porto via";
        String autore = "Niccolo Ammaniti";
        Valutazione valutazione = Valutazione.UNO;
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;
        Formato formato = Formato.DIGITALE;
        String libreria = "sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("8899001122334");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("8899001122334.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, Formato.CARTACEO, StatoLettura.ABBANDONATO, Valutazione.DUE, "salotto", null);

        //creo secondo libro
        Libro libro2 = new Libro();
        libro2.setTitolo("Nel nome della verità");
        libro2.setAutore("Umberto Mario Galimberti");
        libro2.setIsbn("9900112233445");
        libro2.setCasaEditrice("Bompiani");
        libro2.setDataPubblicazione("2005");
        libro2.setCopertina("9900112233445.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro2);
        //recupero la copia utente
        CopiaUtente copia2 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro2.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia2, Formato.CARTACEO, StatoLettura.LETTO, Valutazione.TRE, "camera da letto", null);

        //creo terzo libro
        Libro libro3 = new Libro();
        libro3.setTitolo("Il nome della vittoria");
        libro3.setAutore("Umberto Mario Sironi");
        libro3.setIsbn("1324657980000");
        libro3.setCasaEditrice("Feltrinelli");
        libro3.setDataPubblicazione("2005");
        libro3.setCopertina("1324657980000.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro3);
        //recupero la copia utente
        CopiaUtente copia3 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro3.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia3, Formato.CARTACEO, StatoLettura.NON_LETTO, Valutazione.TRE, "mensola sopra televisione", null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di on trovare nessun libro
        assertEquals(0, copie.size());

    }

    /* Test con tutti i valori di input specificati e nel database 1 libro che non soddisfa nessun vincolo. */
    @Test
    public void test25() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //parametri usati per la ricerca
        String titolo = "Ti prendo e ti porto via";
        String autore = "Niccolo Ammaniti";
        Valutazione valutazione = Valutazione.UNO;
        StatoLettura statoLettura = StatoLettura.IN_LETTURA;
        Formato formato = Formato.DIGITALE;
        String libreria = "sala da pranzo";

        //creo primo libro
        Libro libro1 = new Libro();
        libro1.setTitolo("Il nome della rosa");
        libro1.setAutore("Umberto Mario Eco");
        libro1.setIsbn("1324657980001");
        libro1.setCasaEditrice("Mondadori");
        libro1.setDataPubblicazione("1986");
        libro1.setCopertina("1324657980001.jpg");
        //aggiungo il libro alla libreria
        GestoreLibreriaLocale.aggiungiLibro(libro1);
        //recupero la copia utente
        CopiaUtente copia1 = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro1.getIsbn() + "'", CopiaUtente.class).getSingleResult();
        //aggiorno le informazioni della copia utente
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia1, Formato.CARTACEO, StatoLettura.ABBANDONATO, Valutazione.DUE, "salotto", null);

        //ricerco libri nella libreria specificando tutti i parametri
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria(titolo, autore, valutazione, statoLettura, formato, libreria);

        //mi aspetto di on trovare nessun libro
        assertEquals(0, copie.size());

    }

    /* Test ricerca titolo di n parole con libro di n parole in ordine diverso */
    @Test
    public void test26() throws ConnectionErrorException, InputRicercaException {
        Libro libro = new Libro();
        libro.setTitolo("La ragazza che giocava con il fuoco");
        libro.setAutore("Stieg Larsson");
        libro.setIsbn("9781906694159");
        libro.setCasaEditrice("Marsilio");
        libro.setDataPubblicazione("2006");
        libro.setCopertina("9781906694159.jpg");

        GestoreLibreriaLocale.aggiungiLibro(libro);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria("La ragazza che con il fuoco giocava", null, null, null, null, null);
        assertEquals(1, copie.size());
        assertEquals("La ragazza che giocava con il fuoco", copie.get(0).getLibro().getTitolo());
    }

    /* Test ricerca titolo di n parole con libro di n+m parole */
    @Test
    public void test27() throws ConnectionErrorException, InputRicercaException {
        Libro libro = new Libro();
        libro.setTitolo("La regina dei castelli di carta");
        libro.setAutore("Stieg Larsson");
        libro.setIsbn("9781299025530");
        libro.setCasaEditrice("Marsilio");
        libro.setDataPubblicazione("2007");
        libro.setCopertina("9781299025530.jpg");

        GestoreLibreriaLocale.aggiungiLibro(libro);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria("La regina", null, null, null, null, null);
        assertEquals(1, copie.size());
        assertEquals("La regina dei castelli di carta", copie.get(0).getLibro().getTitolo());
    }

    /* Test ricerca titolo di n parole con libro di n-1 parole */
    @Test
    public void test28() throws ConnectionErrorException, InputRicercaException {
        Libro libro = new Libro();
        libro.setTitolo("Uomini che odiano le donne");
        libro.setAutore("Stieg Larsson");
        libro.setIsbn("9780307577580");
        libro.setCasaEditrice("Marsilio");
        libro.setDataPubblicazione("2005");
        libro.setCopertina("9780307577580.jpg");

        GestoreLibreriaLocale.aggiungiLibro(libro);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria("Uomini che non odiano le donne", null, null, null, null, null);
        assertEquals(0, copie.size());
    }

    /*  Test ricerca titolo di n parole con libro di n-m parole  */
    @Test
    public void test29() throws ConnectionErrorException, InputRicercaException {
        Libro libro = new Libro();
        libro.setTitolo("Il signore degli anelli");
        libro.setAutore("J. R. R. Tolkien");
        libro.setIsbn("9780061917882");
        libro.setCasaEditrice("Bompiani");
        libro.setDataPubblicazione("2004");
        libro.setCopertina("9780061917882.jpg");

        GestoreLibreriaLocale.aggiungiLibro(libro);
        List<CopiaUtente> copie = GestoreLibreriaLocale.ricercaLibroNellaLibreria("Il signore degli anelli il ritorno del re", null, null, null, null, null);
        assertEquals(0, copie.size());
    }

    /* Test ricerca autore di n parole con libro di n parole in ordine diverso */
    @Test
    public void test30() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("L'estate da i numeri");
        libro.setAutore("Gilda Romano Flaccavento");
        libro.setIsbn("1324657980002");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980002.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, "Gilda Flaccavento Romano", null, null, null, null);

        //mi aspetto che venga trovato un risultato
        assertEquals(1, result.size());
        assertEquals("L'estate da i numeri", result.get(0).getLibro().getTitolo());
    }

    /* Test ricerca autore di n parole con libro di n+m parole */
    @Test
    public void test31() throws ConnectionErrorException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("L'estate da i numeri");
        libro.setAutore("Gilda Romano Flaccavento");
        libro.setIsbn("1324657980003");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980003.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, "Gilda Flaccavento", null, null, null, null);

        //mi aspetto che venga trovato un risultato
        assertEquals(1, result.size());
        assertEquals("L'estate da i numeri", result.get(0).getLibro().getTitolo());
    }

    /* Test ricerca autore di n parole con libro di n-1 parole */
    @Test
    public void test32() throws ConnectionErrorException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("L'estate da i numeri");
        libro.setAutore("Gilda Romano");
        libro.setIsbn("1324657980004");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980004.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, "Gilda Flaccavento Romano", null, null, null, null);

        //mi aspetto che venga trovato un risultato
        assertEquals(0, result.size());
    }

    /* Test ricerca autore di n parole con libro di n-m parole */
    @Test
    public void test33() throws ConnectionErrorException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("L'estate da i numeri");
        libro.setAutore("Gilda Romano");
        libro.setIsbn("1324657980005");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980005.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, "Gilda Flaccavento Romano Rossi", null, null, null, null);

        //mi aspetto che venga trovato un risultato
        assertEquals(0, result.size());
    }

    /* Test ricerca nome libreria di n parole con libro di n parole in ordine diverso */
    @Test
    public void test34() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("Fight Club");
        libro.setAutore("Chuck Palahniuk");
        libro.setIsbn("1324657980006");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980006.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);
        //recupero copia utente
        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();
        //modifico le informazioni soggettive impostando il nome della libreria
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia, null, null, null, "libreria camera da letto", null);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, "camera da letto libreria");

        //mi aspetto che venga trovato un risultato
        assertEquals(1, result.size());
        assertEquals("Fight Club", result.get(0).getLibro().getTitolo());
    }

    /* Test ricerca nome libreria di n parole con libro di n+m parole */
    @Test
    public void test35() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("Fight Club");
        libro.setAutore("Chuck Palahniuk");
        libro.setIsbn("1324657980007");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980007.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);
        //recupero copia utente
        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();
        //modifico le informazioni soggettive impostando il nome della libreria
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia, null, null, null, "libreria della camera da letto", null);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, "libreria camera letto");

        //mi aspetto che venga trovato un risultato
        assertEquals(1, result.size());
        assertEquals("Fight Club", result.get(0).getLibro().getTitolo());
    }

    /* Test ricerca nome libreria di n parole con libro di n-1 parole */
    @Test
    public void test36() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("Fight Club");
        libro.setAutore("Chuck Palahniuk");
        libro.setIsbn("1324657980008");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980008.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);
        //recupero copia utente
        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();
        //modifico le informazioni soggettive impostando il nome della libreria
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia, null, null, null, "libreria camera da letto", null);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, "libreria della camera da letto");

        //mi aspetto che venga trovato un risultato
        assertEquals(0, result.size());
    }

    /* Test ricerca nome libreria di n parole con libro di n-m parole */
    @Test
    public void test37() throws ConnectionErrorException, ModificaInformazioniException, VincoliInputException, InputRicercaException {
        //creo libro
        Libro libro = new Libro();
        libro.setTitolo("Fight Club");
        libro.setAutore("Chuck Palahniuk");
        libro.setIsbn("1324657980009");
        libro.setCasaEditrice("Mondadori");
        libro.setDataPubblicazione("1996");
        libro.setCopertina("1324657980009.jpg");
        //aggiungo libro
        GestoreLibreriaLocale.aggiungiLibro(libro);
        //recupero copia utente
        CopiaUtente copia = em.createQuery("SELECT c FROM CopiaUtente c", CopiaUtente.class).getSingleResult();
        //modifico le informazioni soggettive impostando il nome della libreria
        GestoreLibreriaLocale.modificaInformazioniSoggettive(copia, null, null, null, "camera letto", null);

        //ricerco il libro specificanto il nome della libreria con parole in ordine diverso
        List<CopiaUtente> result = GestoreLibreriaLocale.ricercaLibroNellaLibreria(null, null, null, null, null, "libreria della camera da letto");

        //mi aspetto che venga trovato un risultato
        assertEquals(0, result.size());
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
