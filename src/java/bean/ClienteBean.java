package bean;

import JPA.ClienteJpaController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
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
    private ClienteJpaController clienteJpaController;

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
         FacesContext context = FacesContext.getCurrentInstance();
        try {
            clienteJpaController.create(novoCliente);
            context.addMessage(null, new FacesMessage("Cliente cadastrado!", novoCliente.getNome()));
            novoCliente = new Cliente();
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha!", novoCliente.getNome()));
            Logger.getLogger(ClienteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Cliente findClienteByCpf(String cpfProcurado) {
        return null;
    }
}
