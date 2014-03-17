package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.ClienteJpaController;
import model.Cliente;

/**
 *
 * @author Adriano
 */
@ManagedBean
@ViewScoped
public class ClienteBean {
    Cliente novoCliente = new Cliente();
    String cpfProcurado;

    /**
     * Creates a new instance of ClienteBean
     */
    public ClienteBean() {
        
    }

    public String getCpfProcurado() {
        return cpfProcurado;
    }

    public void setCpfProcurado(String cpfProcurado) {
        this.cpfProcurado = cpfProcurado;
    }

    public Cliente getNovoCliente() {
        return novoCliente;
    }

    public void setNovoCliente(Cliente novoCliente) {
        this.novoCliente = novoCliente;
    }
    
        public void create() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ClienteJpaController clienteJpa = new ClienteJpaController(emf);
        clienteJpa.create(novoCliente);
        emf.close();
    }
        
    public Cliente findClienteByCpf() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ClienteJpaController clienteJpa = new ClienteJpaController(emf);
        emf.close();
        return clienteJpa.findClienteByCpf(cpfProcurado);
    }    
}
