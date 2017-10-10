package GestoreLibreriaRemoto;

import LogicaDominio.Autenticazione;
import LogicaDominio.Categoria;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import javax.ejb.Remote;

@Remote
public interface GestoreLibreriaRemotoRemote {

    Boolean aggiungiCategoriaInRemoto(Categoria categoria, Autenticazione autenticazione);

    Boolean rimuoviCategoriaInRemoto(Categoria categoria, Autenticazione autenticazione);

    Boolean aggiungiLibroInRemoto(Libro libro, CopiaUtente copiaUtente, Autenticazione autenticazione);

    Boolean aggiornaCopiaUtenteInRemoto(CopiaUtente copiaUtente, Autenticazione autenticazione);

    Boolean rimuoviCopiaInRemoto(CopiaUtente copiaUtente, Autenticazione autenticazione);

}
