package bean;

import JPA.ClienteJpaController;
import JPA.ProdutoJpaController;
import java.io.Serializable;
import java.util.Collection;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import model.Cliente;
import model.Item;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@ViewScoped
public class VendaBean implements Serializable {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    EntityManagerFactory emf;
    @Resource //inject from your application server
    UserTransaction utx;

    private Venda venda = new Venda();
    private Integer codigoProcurado;
    private String cpfProcurado;
    private String nomeProcurado;
    private float valorCompra;

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Integer getCodigoProcurado() {
        return codigoProcurado;
    }

    public void setCodigoProcurado(Integer codigoProcurado) {
        this.codigoProcurado = codigoProcurado;
    }

    public String getCpfProcurado() {
        return cpfProcurado;
    }

    public void setCpfProcurado(String cpfProcurado) {
        this.cpfProcurado = cpfProcurado;
    }

    public float getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(float valorCompra) {
        this.valorCompra = valorCompra;
    }

    public void inserirProduto() {

        ProdutoJpaController produtoJpaController = new ProdutoJpaController(utx, emf);
        boolean inserir = true;
        FacesContext context = FacesContext.getCurrentInstance();

        for (Item i : venda.getItemCollection()) {
            if (i.getProdutoId().getCodigo() == codigoProcurado) {
                inserir = false;
                context.addMessage(null, new FacesMessage("Falha", "O produto já está na lista!"));
                break;
            }
        }

        if (inserir) {
            try {
                Item item = new Item();
                item.setProdutoId(produtoJpaController.findByCodigo(codigoProcurado));
                item.setPrecoCompra(item.getProdutoId().getPreco());
                item.setQuantidade(1);
                venda.getItemCollection().add(item);
                calculaValorCompra();
                context.addMessage(null, new FacesMessage(item.getProdutoId().getNome(), "Produto adicionado na lista."));
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage("Falha", "Produto não encontrado!"));
            }
        }
        codigoProcurado = null;
    }

    public void removeItem(Item item) {
        FacesContext context = FacesContext.getCurrentInstance();
        String nomeProduto = item.getProdutoId().getNome();
        venda.getItemCollection().remove(item);
        context.addMessage(null, new FacesMessage(nomeProduto, "Produto removido do carrinho!"));
        calculaValorCompra();
    }

    public void buscarCliente() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            ClienteJpaController clienteJpaController = new ClienteJpaController(utx, emf);
            venda.setClienteId(clienteJpaController.findByCpf(cpfProcurado));
            context.addMessage(null, new FacesMessage("Cliente Selecionado", venda.getClienteId().getNome()));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Falha", "Cliente não encontrado!"));
        }
        cpfProcurado = null;
    }

    public Collection<Cliente> buscarClientePorNome() {

        try {
            ClienteJpaController clienteJpaController = new ClienteJpaController(utx, emf);
            return clienteJpaController.findByLikeNome(nomeProcurado);
        } catch (Exception e) {
        }
        return null;
    }

    public void calculaValorCompra() {
        valorCompra = 0;
        for (Item i : venda.getItemCollection()) {
            valorCompra += i.getPrecoCompra() * i.getQuantidade();
        }
    }

    public void incrementaQuantidade(Item item) {
        FacesContext context = FacesContext.getCurrentInstance();
        int quantidadeMaxima = 6;
        //TODO: verificar se tem no estoque
        int quantidade = item.getQuantidade();
        quantidade++;

        if (quantidade <= quantidadeMaxima) {
            item.setQuantidade(quantidade);
        } else {
            context.addMessage(null, new FacesMessage("Falha", "A quantidade máxima permitida são " + quantidadeMaxima + "itens!"));
        }

        calculaValorCompra();
    }

    public void decrementaQuantidade(Item item) {
        //TODO: verificar se tem no estoque
        int quantidade = item.getQuantidade();
        quantidade--;
        if (quantidade > 0) {
            item.setQuantidade(quantidade);
        }

        calculaValorCompra();
    }

    public void finalizarVenda() {
        LoginBean loginBean = new LoginBean();
        venda.setFuncionarioId(loginBean.getFuncionarioLogado());
        venda.setEventoId(loginBean.getEventoSelecionado());
    }

    public String getNomeProcurado() {
        return nomeProcurado;
    }

    public void setNomeProcurado(String nomeProcurado) {
        this.nomeProcurado = nomeProcurado;
    }
}
