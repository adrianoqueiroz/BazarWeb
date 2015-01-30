/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.CategoriaJpaController;
import JPA.EventoJpaController;
import JPA.ItemJpaController;
import JPA.ProdutoJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.Categoria;
import model.Evento;
import model.Item;
import model.Produto;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Adriano
 */
@ManagedBean
@ViewScoped
public class ProdutoBean implements Serializable {

    private Produto produto;
    private Produto produtoEditado;
    private Collection<Produto> produtoCollection;
    private Evento evento;
    private int estoque;
    private int idCategoriaSelecionada;
    @EJB
    private EventoJpaController eventoJpaController;
    @EJB
    private ProdutoJpaController produtoJpaController;
    @EJB
    private CategoriaJpaController categoriaJpaController;
    @EJB
    private ItemJpaController itemJpaController;

    private int editQuantidade;
    private float editPreco;

    public ProdutoBean() {
        this.produtoCollection = new ArrayList<>();
        this.produto = new Produto();
        this.produtoEditado = new Produto();
    }

    public Collection<Produto> getProdutoCollection() {
        produtoCollection = produtoJpaController.findProdutoEntitiesByEvento(this.getEvento());
        return produtoCollection;
    }

    public void setProdutoCollection(Collection<Produto> produtoCollection) {
        this.produtoCollection = produtoCollection;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Produto getProdutoEditado() {
        return produtoEditado;
    }

    public void setProdutoEditado(Produto produtoEditado) {
        this.produtoEditado = produtoEditado;
    }

    public int getEditQuantidade() {
        return editQuantidade;
    }

    public void setEditQuantidade(int editQuantidade) {
        this.editQuantidade = editQuantidade;
    }

    public float getEditPreco() {
        return editPreco;
    }

    public void setEditPreco(float editPreco) {
        this.editPreco = editPreco;
    }

    public void create() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            produto.setEventoId(this.getEvento());

            Categoria categoria = categoriaJpaController.findCategoria(idCategoriaSelecionada);
            produto.setCategoriaId(categoria);

            produtoJpaController.create(produto);

            produto = new Produto();
            context.addMessage(null, new FacesMessage("Produto cadastrado!", produto.getNome()));

        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha!", "Falha"));
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Produto produto) {
        FacesContext context = FacesContext.getCurrentInstance();

        if (getEstoque(produto) >= 0) {
            try {
                produtoJpaController.edit(produto);
                produtoEditado = new Produto();
                context.addMessage(null, new FacesMessage("Alterações realizadas!", produto.getNome()));
            } catch (Exception ex) {
                Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            context.addMessage(null, new FacesMessage("Falha!", "O estoque não pode ficar negativo!"));
        }
    }

    public int getEstoque(Produto produto) {
        estoque = produto.getQuantidade() - getQtdVendidos(produto);
        return estoque;
    }

    public int getQtdVendidos(Produto produto) {
        Collection<Item> itens = itemJpaController.findItemEntities();
        int totalVendidos = 0;
        for (Item i : itens) {
            if (Objects.equals(i.getProdutoId().getId(), produto.getId())) {
                totalVendidos += i.getQuantidade();
            }
        }
        return totalVendidos;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public int getIdCategoriaSelecionada() {
        return idCategoriaSelecionada;
    }

    public void setIdCategoriaSelecionada(int idCategoriaSelecionada) {
        this.idCategoriaSelecionada = idCategoriaSelecionada;
    }

    public List<SelectItem> getSelectItemCategorias() {

        List<Categoria> listaCategorias = categoriaJpaController.findCategoriaEntities();
        List<SelectItem> itens = new ArrayList<>(listaCategorias.size());

        for (Categoria c : listaCategorias) {
            itens.add(new SelectItem(c.getId(), c.getNome()));

        }
        return itens;
    }

    public Evento getEvento() {
        //TODO: pegar o evento atual da sessão
        evento = eventoJpaController.findEvento(1);
        return evento;
    }

    public void onRowEdit(RowEditEvent event) {
        Produto produtoEd = (Produto) event.getObject();

        edit(produtoEd);

        FacesMessage msg = new FacesMessage("Produto Editado", ((Produto) event.getObject()).getNome());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edição cancelada", ((Produto) event.getObject()).getNome());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void selecionaProdutoEditado(Produto produto) {
        setProdutoEditado(produto);
    }
}
