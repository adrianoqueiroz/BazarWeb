/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.CategoriaJpaController;
import JPA.EventoJpaController;
import JPA.ProdutoJpaController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import model.Categoria;
import model.Evento;
import model.Produto;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ProdutoBean {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    EntityManagerFactory emf;
    @Resource //inject from your application server
    UserTransaction utx;

    private Produto produto;
    private Collection<Produto> produtoCollection;
    private int estoque;
    private int idCategoriaSelecionada;
    
    

    public ProdutoBean() {
        this.produtoCollection = new ArrayList<>();
        this.produto = new Produto();
    }

    public Collection<Produto> getProdutoCollection() {
        ProdutoJpaController produtoJpaController;
        produtoJpaController = new ProdutoJpaController(utx, emf);

        produtoCollection = produtoJpaController.findProdutoEntities();
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
            //TODO: pegar o evento da sessao
            EventoJpaController eventoJpaController = new EventoJpaController(utx, emf);
            Evento evento = eventoJpaController.findEvento(0);
            
            CategoriaJpaController categoriaJpaController = new CategoriaJpaController(utx, emf);
            Categoria categoria = categoriaJpaController.findCategoria(idCategoriaSelecionada);
            
            produto.setEventoId(evento);
            produto.setCategoriaId(categoria);
            
            ProdutoJpaController produtoJpaController = new ProdutoJpaController(utx, emf);
            produtoJpaController.create(produto);
            
            produto = new Produto();
            context.addMessage(null, new FacesMessage("Produto cadastrado!", produto.getNome()));

        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha!", "Falha"));
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getEstoque(Produto produto) {
        //TODO: fazer a verificação em vendas.itemcollection de quantos produtos foram vendidos
        estoque = produto.getQuantidade();
        return estoque;
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
        CategoriaJpaController categoriaJpaController = new CategoriaJpaController((UserTransaction) utx, emf);

        List<Categoria> listaCategorias = categoriaJpaController.findCategoriaEntities();
        List<SelectItem> itens = new ArrayList<>(listaCategorias.size());

        for (Categoria c : listaCategorias) {
            itens.add(new SelectItem(c.getId(), c.getNome()));

        }
        return itens;
    }
}
