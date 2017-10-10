package Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AggiungiLibroTest.class,
    CreaCategoriaTest.class,
    LoginTest.class,
    ModificaInformazioniLibroTest.class,
    RegistrazioneTest.class,
    RicercaGoogleBooksTest.class,
    RicercaLibroNellaLibreriaTest.class,
    RimuoviCategoriaTest.class,
    RimuoviLibroTest.class,
    VisualizzaElencoCategorieTest.class,
    VisualizzaLibreriaTest.class,
    VisualizzaSchedaLibroTest.class
})

public class AllTestsOK {

}
