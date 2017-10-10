package LogicaDominio;

import Enumerations.Formato;
import Enumerations.StatoLettura;
import Enumerations.Valutazione;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import org.eclipse.persistence.annotations.UuidGenerator;

@Entity
@UuidGenerator(name = "UUID-GEN")
public class CopiaUtente implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(generator = "UUID-GEN", strategy = GenerationType.TABLE)
    private String id;

    private int numeroCopia;

    @ManyToOne
    private Libro libro;

    @ManyToOne
    private Account account;

    @Enumerated(EnumType.STRING)
    private Formato formato;

    @Enumerated(EnumType.STRING)
    private StatoLettura statoLettura;

    @Enumerated(EnumType.STRING)
    private Valutazione valutazione;

    private String nomelibreria;
    private String posizioneNellaLibreria;

    private String copertinaLocale;

    // FIXME: MAPPED BY
    @ManyToOne
    private Amico prestataA;

    @ManyToOne
    private Amico prestataDa;

    @ManyToMany(mappedBy = "copieLibriAssociate", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Collection<Categoria> categorieAssegnate;

    public void setStatoLettura(StatoLettura statoLettura) {
        this.statoLettura = statoLettura;
    }

    public void setValutazione(Valutazione valutazione) {
        this.valutazione = valutazione;
    }

    public void setNomelibreria(String nomelibreria) {
        this.nomelibreria = nomelibreria;
    }

    public void setPosizioneNellaLibreria(String posizioneNellaLibreria) {
        this.posizioneNellaLibreria = posizioneNellaLibreria;
    }

    public StatoLettura getStatoLettura() {
        return statoLettura;
    }

    public Valutazione getValutazione() {
        return valutazione;
    }

    public String getNomelibreria() {
        return nomelibreria;
    }

    public String getPosizioneNellaLibreria() {
        return posizioneNellaLibreria;
    }

    public Collection<Categoria> getCategorieAssegnate() {
        return categorieAssegnate;
    }

    public void setCategorieAssegnate(Collection<Categoria> categorieAssegnate) {
        this.categorieAssegnate = categorieAssegnate;
    }

    public String getId() {
        return id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Formato getFormato() {
        return formato;
    }

    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    public String getCopertinaLocale() {
        return copertinaLocale;
    }

    public void setCopertinaLocale(String copertinaLocale) {
        this.copertinaLocale = copertinaLocale;
    }

    public int getNumeroCopia() {
        return numeroCopia;
    }

    public void setNumeroCopia(int numeroCopia) {
        this.numeroCopia = numeroCopia;
    }

    public void aggiungiCategoria(Categoria categoria) {
        if (!getCategorieAssegnate().contains(categoria)) {
            getCategorieAssegnate().add(categoria);
        }
        if (!categoria.getCopieLibriAssociate().contains(this)) {
            categoria.getCopieLibriAssociate().add(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) numeroCopia;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the numeroCopia fields are not set
        if (!(object instanceof CopiaUtente)) {
            return false;
        }
        CopiaUtente other = (CopiaUtente) object;
        if (this.numeroCopia != other.numeroCopia) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LogicaDominio.CopiaUtente[ id=" + numeroCopia + " ]";
    }

}
