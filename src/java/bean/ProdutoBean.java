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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.Categoria;
import model.Evento;
import model.Item;
import model.Produto;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ProdutoBean implements Serializable {

    private Produto produto;
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

    public ProdutoBean() {
        this.produtoCollection = new ArrayList<>();
        this.produto = new Produto();
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
}
