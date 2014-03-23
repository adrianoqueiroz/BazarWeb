package bean;

import dao.ClienteDao;
import dao.ProdutoDao;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import model.Item;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@ViewScoped
public class VendaBean {

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
        Item item = new Item();
        ProdutoDao produtoDao = new ProdutoDao();
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
                item.setProdutoId(produtoDao.findByCodigo(codigoProcurado));
                item.setPrecoCompra(item.getProdutoId().getPreco());
                item.setQuantidade(1);
                venda.getItemCollection().add(item);
                context.addMessage(null, new FacesMessage(item.getProdutoId().getNome(), "Produto adicionado na lista."));
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage("Falha", "Produto não encontrado!"));
            }
        }
        calculaValorCompra();
        codigoProcurado = null;
    }

    public void buscarCliente() {
        ClienteDao clienteDao = new ClienteDao();
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            venda.setClienteId(clienteDao.findByCpf(cpfProcurado));
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
