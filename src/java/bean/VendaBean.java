package bean;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.ClienteJpaController;
import jpa.ProdutoJpaController;
import model.Cliente;
import model.Produto;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@SessionScoped
public class VendaBean {

    private List<Venda> vendas;
    private Venda vendaSelecionada;
    private long codigoProcurado;
    private String cpfProcurado;
    private Cliente clienteSelecionado = new Cliente();

    ClienteBean clienteBean = new ClienteBean();

    private Venda novaVenda = new Venda();

    public VendaBean() {
        vendas = new ArrayList<>();
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public void setVendas(List<Venda> vendas) {
        this.vendas = vendas;
    }

    public Venda getVendaSelecionada() {
        return vendaSelecionada;
    }

    public void setVendaSelecionada(Venda vendaSelecionada) {
        this.vendaSelecionada = vendaSelecionada;
    }

    public Cliente getClienteSelecionado() {
        return clienteSelecionado;
    }

    public void setClienteSelecionado(Cliente clienteSelecionado) {
        this.clienteSelecionado = clienteSelecionado;
    }

    public void removeSelecionado() {
        if (vendaSelecionada != null) {
            vendas.remove(vendaSelecionada);
        }
        vendaSelecionada = null;
    }

    public String getCpfProcurado() {
        return cpfProcurado;
    }

    public void setCpfProcurado(String cpfProcurado) {
        this.cpfProcurado = cpfProcurado;
    }

    public Produto buscarProdutoLista(long codigo) {
        for (Venda v : vendas) {
            if (v.getProduto().getCodigo() == codigo) {
                return v.getProduto();
            }
        }
        return null;
    }

    public void inserirProduto() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ProdutoJpaController produtoJpaController = new ProdutoJpaController(emf);
        Produto produtoEncontrado;
        boolean inserir = true;
        FacesContext context = FacesContext.getCurrentInstance();

        //se não existe o produto na lista, insere
        if (buscarProdutoLista(codigoProcurado) != null) {
            inserir = false;
            context.addMessage(null, new FacesMessage("Falha", "O Produto já está na lista!"));
        }

        if (inserir) {
            try {
                produtoEncontrado = produtoJpaController.findProdutoByCodigo(this.getCodigoProcurado());
                Venda venda = new Venda();
                venda.setProduto(produtoEncontrado);
                vendas.add(venda);

                context.addMessage(null, new FacesMessage("Sucesso", venda.getProduto().getNome() + " adicionado"));

            } catch (Exception e) {
                context.addMessage(null, new FacesMessage("Falha", "Produto não encontrado!"));
            } finally {
                codigoProcurado = 0;
                emf.close();
            }
        }
    }

    public long getCodigoProcurado() {
        return codigoProcurado;
    }

    public void setCodigoProcurado(long codigoProcurado) {
        this.codigoProcurado = codigoProcurado;
    }

    public Venda getNovaVenda() {
        return novaVenda;
    }

    public void setNovaVenda(Venda novaVenda) {
        this.novaVenda = novaVenda;
    }

    public void clientePorCpf() {
        FacesContext context = FacesContext.getCurrentInstance();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ClienteJpaController clienteJpa = new ClienteJpaController(emf);
        Cliente clienteBuscado = clienteJpa.findClienteByCpf(cpfProcurado);

        if (clienteBuscado != null) {
            context.addMessage(null, new FacesMessage("Sucesso", "Cliente encontrado!"));
            clienteSelecionado = clienteBuscado;
        } else {
            context.addMessage(null, new FacesMessage("Falha", "Cliente não encontrado!"));
        }
        emf.close();
    }

    public void clientePorCpf2() {
        clienteSelecionado = clienteBean.findClienteByCpf(cpfProcurado);
    }

}
