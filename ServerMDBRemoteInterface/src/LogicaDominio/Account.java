package LogicaDominio;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    private String username;

    private String password;
    private String email;
    private String nome;
    private String cognome;
    private String dataNascita;

    @OneToMany(mappedBy = "account")
    private Collection<CopiaUtente> copieUtente;

    @OneToMany(mappedBy = "account")
    private Collection<Amico> amici;

    @OneToMany(mappedBy = "account")
    private Collection<Categoria> categorie;

    // TODO: CREDENZIALI SOCIAL NETWORK ENTITY
    /*@OneToMany(mappedBy = "account")
     private Collection<CredenzialiSocialNetwork> credenzialiSocialNetwork;
     public Collection<CredenzialiSocialNetwork> getCredenzialiSocialNetwork() {
     return credenzialiSocialNetwork;
     }
     public void setCredenzialiSocialNetwork(Collection<CredenzialiSocialNetwork> credenzialiSocialNetwork) {
     this.credenzialiSocialNetwork = credenzialiSocialNetwork;
     }
     */
    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public Collection<CopiaUtente> getCopieUtente() {
        return copieUtente;
    }

    public void setCopieUtente(Collection<CopiaUtente> copieUtente) {
        this.copieUtente = copieUtente;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the username fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LogicaDominio.Account[ id=" + username + " ]";
    }

}
