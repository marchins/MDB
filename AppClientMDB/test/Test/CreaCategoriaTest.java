package Test;

import DatabaseUtility.DatabaseUtilityRemote;
import Exceptions.CategoriaGiaEsistenteException;
import Exceptions.*;
import GestoreAccountLocale.GestoreAccountLocale;
import GestoreLibreriaLocale.GestoreLibreriaLocale;
import LogicaDominio.Categoria;
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

public class CreaCategoriaTest {

    public static EntityManagerFactory emf;
    public static EntityManager em;

    @BeforeClass
    public static void setUpClass() throws VincoliInputException, LoginException, RegistrazioneException, ParseException, ConnectionErrorException {
        emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
        em = emf.createEntityManager();
        GestoreAccountLocale.registrazione("userjohn", "passwordjohn", "John", "Doe", "johndoe@gmail.com", new Date());
        GestoreAccountLocale.login("userjohn", "passwordjohn");
    }

    /* Test creazione di una categoria. */
    @Test
    public void test1() throws CategoriaGiaEsistenteException, CreazioneCategoriaException, ConnectionErrorException {
        GestoreLibreriaLocale.creaCategoria("categoria1");
        List<Categoria> result = em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
        assertEquals(1, result.size());
        assertEquals("categoria1", result.get(0).getNome());
    }

    /* Test creazione di tre differenti categorie. */
    @Test
    public void test2() throws CategoriaGiaEsistenteException, CreazioneCategoriaException, ConnectionErrorException {
        GestoreLibreriaLocale.creaCategoria("categoria2");
        GestoreLibreriaLocale.creaCategoria("categoria3");
        GestoreLibreriaLocale.creaCategoria("categoria4");
        List<Categoria> result = em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
        assertEquals(3, result.size());
    }

    /* Test di creazione di due categorie con lo stesso nome. */
    @Test(expected = CategoriaGiaEsistenteException.class)
    public void test3() throws CategoriaGiaEsistenteException, CreazioneCategoriaException, ConnectionErrorException {
        GestoreLibreriaLocale.creaCategoria("categoria5");
        GestoreLibreriaLocale.creaCategoria("categoria5");
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
