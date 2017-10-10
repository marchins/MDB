package LogicaDominio;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.eclipse.persistence.annotations.UuidGenerator;

@Entity
@UuidGenerator(name = "UUID-GEN")
public class Amico implements Serializable {

    private static final long serialVersionUID = 10L;

    @Id
    @GeneratedValue(generator = "UUID-GEN", strategy = GenerationType.TABLE)
    private String id;

    private String nome;
    private String email;

    @ManyToOne
    private Account account;

    // FIXME: MAPPED BY
    @OneToMany(targetEntity = CopiaUtente.class, mappedBy = "prestataA")
    private Collection<CopiaUtente> libroPrestato;

    @OneToMany(targetEntity = CopiaUtente.class, mappedBy = "prestataDa")
    private Collection<CopiaUtente> libroRicevutoinPrestito;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Amico)) {
            return false;
        }
        Amico other = (Amico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LogicaDominio.Amico[ id=" + id + " ]";
    }

}
