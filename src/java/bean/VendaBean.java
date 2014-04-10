package bean;

import JPA.ClienteJpaController;
import JPA.ProdutoJpaController;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
    private Venda venda = new Venda();
    private Integer codigoProcurado;
    private String cpfProcurado;
    private String nomeProcurado;
    private float valorCompra;
    @EJB
    private ProdutoJpaController produtoJpaController;
    @EJB
    private ClienteJpaController clienteJpaController;

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
        valorCompra = calculaValorCompra();
        return valorCompra;
    }

    public void setValorCompra(float valorCompra) {
        this.valorCompra = valorCompra;
    }

    public void inserirProduto() {
        boolean inserir = true;
        FacesContext context = FacesContext.getCurrentInstance();

        for (Item i : venda.getItemCollection()) {
            if (Objects.equals(i.getProdutoId().getCodigo(), codigoProcurado)) {
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
    }

    public void buscarCliente() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            venda.setClienteId(clienteJpaController.findByCpf(cpfProcurado));
            context.addMessage(null, new FacesMessage("Cliente Selecionado", venda.getClienteId().getNome()));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Falha", "Cliente não encontrado!"));
        }
        cpfProcurado = null;
    }

    public Collection<Cliente> buscarClientePorNome() {

        try {
            return clienteJpaController.findByLikeNome(nomeProcurado);
        } catch (Exception e) {
        }
        return null;
    }

    public float calculaValorCompra() {
        int valor = 0;
        for (Item i : venda.getItemCollection()) {
            valor += i.getPrecoCompra() * i.getQuantidade();
        }
        return valor;
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
            context.addMessage(null, new FacesMessage("Falha", "A quantidade máxima atingida!"));
        }

        
    }

    public void decrementaQuantidade(Item item) {
        //TODO: verificar se tem no estoque
        int quantidade = item.getQuantidade();
        quantidade--;
        if (quantidade > 0) {
            item.setQuantidade(quantidade);
        }
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
