package bean;

import JPA.ClienteJpaController;
import JPA.ItemJpaController;
import JPA.ProdutoJpaController;
import JPA.VendaJpaController;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    private Venda venda = new Venda();
    private Integer codigoProcurado;
    private String cpfProcurado;
    private String nomeProcurado;
    private float valorCompra;

    private Collection<Venda> listaVendas;
    @EJB
    private ProdutoJpaController produtoJpaController;
    @EJB
    private ClienteJpaController clienteJpaController;
    @EJB
    private VendaJpaController vendaJpaController;
    @EJB
    private ItemJpaController itemJpaController;

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

    public String getNomeProcurado() {
        return nomeProcurado;
    }

    public void setNomeProcurado(String nomeProcurado) {
        this.nomeProcurado = nomeProcurado;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public Date getNow() {
        return new Date();
    }

    public void inserirProduto() {
        boolean inserir = true;
        FacesContext context = FacesContext.getCurrentInstance();

        for (Item i : venda.getItemCollection()) {
            if (Objects.equals(i.getProdutoId().getCodigo(), codigoProcurado)) {
                inserir = false;
                context.addMessage(null, new FacesMessage("Falha", "O produto já está no carrinho de compras!"));
                break;
            }
        }

        if (inserir) {
            try {
                Item item = new Item();
                item.setProdutoId(produtoJpaController.findByCodigo(codigoProcurado));
                item.setPrecoVenda(item.getProdutoId().getPreco());
                item.setQuantidade(1);
                venda.getItemCollection().add(item);
                calculaValorCompra();
                context.addMessage(null, new FacesMessage(item.getProdutoId().getNome(), "Produto adicionado ao carrinho de compras."));
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
        context.addMessage(null, new FacesMessage(nomeProduto, "Produto removido do carrinho de compras!"));
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
            valor += i.getPrecoVenda() * i.getQuantidade();
        }
        return valor;
    }

    public void incrementaQuantidade(Item item) {
        FacesContext context = FacesContext.getCurrentInstance();
        int quantidadeMaxima = 6;
        int estoque = 1;
        //TODO: Verificar Estoque

//        estoque = produtoBean.getEstoque(item.getProdutoId());
        if (estoque > 0) {
            int quantidade = item.getQuantidade();
            quantidade++;

            if (quantidade <= quantidadeMaxima) {
                item.setQuantidade(quantidade);
            } else {
                context.addMessage(null, new FacesMessage("Falha", "Quantidade máxima atingida!"));
            }
        } else {
            context.addMessage(null, new FacesMessage("Falha", "Produto indisponível!"));
        }

    }

    public void decrementaQuantidade(Item item) {
        int quantidade = item.getQuantidade();
        quantidade--;
        if (quantidade > 0) {
            item.setQuantidade(quantidade);
        }
    }

    public void finalizarVenda() {
        FacesContext context = FacesContext.getCurrentInstance();
        venda.setFuncionarioId(loginBean.getFuncionarioLogado());
        venda.setEventoId(loginBean.getEventoSelecionado());
        
        try {
            //verificar se o carrinho não está vazio
            if (venda.getItemCollection().size() > 0) {
                if (venda.getFuncionarioId().getNome() != null) {
                    venda.setDataVenda(new Date());
                    for (Item item : venda.getItemCollection()) {
                        itemJpaController.create(item);
                    }
                    vendaJpaController.create(venda);
                    context.addMessage(null, new FacesMessage("Sucesso", "Venda realizada!"));
                } else {
                    context.addMessage(null, new FacesMessage("Falha", "Selecione o cliente!"));
                }

            } else {
                context.addMessage(null, new FacesMessage("Falha", "Nenhum item no carrinho!"));
            }

        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha", "Erro ao persistir os dados!"));
            Logger.getLogger(VendaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Collection<Venda> getListaVendas() {
        listaVendas = vendaJpaController.findVendaEntities();
        return listaVendas;
    }

    public double calculaValorTotal(Venda venda) {
        double total = 0;
        for (Item item : venda.getItemCollection()) {
            total += item.getPrecoVenda() * item.getQuantidade();
        }
        return total;
    }

}
