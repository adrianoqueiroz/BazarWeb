package bean;

import JPA.ClienteJpaController;
import JPA.ProdutoJpaController;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import model.Item;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@ViewScoped
public class VendaBean {
    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    EntityManagerFactory emf;
    @Resource //inject from your application server
    UserTransaction utx;

    private Venda venda = new Venda();
    private Integer codigoProcurado;
    private String cpfProcurado;
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

    public void calculaValorCompra() {
        valorCompra = 0;
        for(Item i : venda.getItemCollection()){
            valorCompra += i.getPrecoCompra()*i.getQuantidade();
        }
    }

    public void finalizarVenda() {
        LoginBean loginBean = new LoginBean();
        venda.setFuncionarioId(loginBean.getFuncionarioLogado());
        venda.setEventoId(loginBean.getEventoSelecionado());
    }
}