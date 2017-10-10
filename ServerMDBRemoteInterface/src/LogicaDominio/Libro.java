package LogicaDominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Libro")
public class Libro implements Serializable {

    private static final long serialVersionUID = 200L;
    @Id
    private String isbn;
    private String titolo;
    private String autore;
    private String casaEditrice;
    private String dataPubblicazione;
    private String copertina;
    @OneToMany(mappedBy = "libro")
    private List<CopiaUtente> copieUtente = new ArrayList<>();

    public Libro(String isbn, String titolo, String autore, String casaEditrice, String dataPubblicazione, String copertina) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.casaEditrice = casaEditrice;
        this.dataPubblicazione = dataPubblicazione;
        this.copertina = copertina;
    }

    public Libro() {
        super();
    }

    /*
     public List<CopiaUtente> getCopieUtente() {
     return copieUtente;
     }
     */
    public void setCopieUtente(List<CopiaUtente> copieUtente) {
        this.copieUtente = copieUtente;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getCasaEditrice() {
        return casaEditrice;
    }

    public void setCasaEditrice(String casaEditrice) {
        this.casaEditrice = casaEditrice;
    }

    public String getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(String dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public String getCopertina() {
        return copertina;
    }

    public void setCopertina(String copertina) {
        this.copertina = copertina;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (isbn != null ? isbn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the isbn fields are not set
        if (!(object instanceof Libro)) {
            return false;
        }
        Libro other = (Libro) object;
        return (this.isbn != null || other.isbn == null) && (this.isbn == null || this.isbn.equals(other.isbn));
    }

    @Override
    public String toString() {
        return "LogicaDominio.Libro[ id=" + isbn + " ]";
    }

}
