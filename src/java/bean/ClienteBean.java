package bean;

import dao.ClienteDao;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
        ClienteDao clienteDao = new ClienteDao();
        clienteDao.persist(novoCliente);
    }

    public Cliente findClienteByCpf(String cpfProcurado) {
        return null;
    }
}
