package bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.ClienteJpaController;
import model.Cliente;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
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

    public Cliente findClienteByCpf(String cpfProcurado) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ClienteJpaController clienteJpa = new ClienteJpaController(emf);
        Cliente clienteEncontrado = null;
        boolean inserir = true;
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            clienteEncontrado = clienteJpa.findClienteByCpf(cpfProcurado);
            context.addMessage(null, new FacesMessage("Sucesso", "Cliente encontrado!"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Falha", "Cliente não encontrado!"));
        } finally {
            emf.close();
        }

        return clienteEncontrado;
    }
}
