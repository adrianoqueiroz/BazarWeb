/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.CategoriaJpaController;
import JPA.ItemJpaController;
import JPA.ProdutoJpaController;
import JPA.exceptions.NonexistentEntityException;
import JPA.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
@ViewScoped
public class ProdutoBean implements Serializable {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private Categoria novaCategoria;
    private Categoria categoriaEditada;

    private Produto produto;
    private Produto produtoEditado;
    private Collection<Produto> produtoCollection;
    private Evento evento;
    private int estoque;
    private int idCategoriaSelecionada;

    private boolean adicionarEstoque = true;
    private int qtdAdicionarEstoque;

    public boolean isAdicionarEstoque() {
        return adicionarEstoque;
    }

    public void setAdicionarEstoque(boolean adicionarEstoque) {
        this.adicionarEstoque = adicionarEstoque;
    }

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
        this.novaCategoria = new Categoria();
        this.categoriaEditada = new Categoria();
        this.produto = new Produto();
        this.produtoEditado = new Produto();
        this.evento = new Evento();
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

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void selecionaProdutoEditado(Produto produto) {
        setProdutoEditado(produto);
    }

    public int getQtdAdicionarEstoque() {
        return qtdAdicionarEstoque;
    }

    public void setQtdAdicionarEstoque(int qtdAdicionarEstoque) {
        this.qtdAdicionarEstoque = qtdAdicionarEstoque;
    }

    public void create() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            produto.setEventoId(this.getEvento());

            Categoria categoriaSelecionada = categoriaJpaController.findCategoria(idCategoriaSelecionada);
            produto.setCategoriaId(categoriaSelecionada);

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
        int quantidadeAtual = produto.getQuantidade();
        
        if (adicionarEstoque) {
            produto.setQuantidade(quantidadeAtual + qtdAdicionarEstoque);
        } else {
            produto.setQuantidade(quantidadeAtual - qtdAdicionarEstoque);
        }
        qtdAdicionarEstoque = 0;
        if ((getEstoque(produto)) >= 0) {
            try {
                produtoJpaController.edit(produto);
                produtoEditado = new Produto();
                context.addMessage(null, new FacesMessage("Alterações realizadas!", produto.getNome()));
            } catch (Exception ex) {
                Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            produto.setQuantidade(quantidadeAtual);
            context.addMessage(null, new FacesMessage("Falha!", "O estoque não pode ficar negativo!"));
        }
    }

    public int getEstoque(Produto produto) {
        estoque = 0;
        if (produto.getId() != null) {
            estoque = produto.getQuantidade() - getQtdVendidos(produto);
        }
        return estoque;
    }

    public int getQtdVendidos(Produto produto) {
        Collection<Item> itens = itemJpaController.findItemEntitiesByProduto(produto);
        int totalVendidos = 0;
        for (Item i : itens) {
            totalVendidos += i.getQuantidade();
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
        evento = loginBean.getEventoSelecionado();
        return evento;
    }

    public void editarCategoria() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Categoria c = categoriaJpaController.findCategoria(categoriaEditada.getId());
            String nomeAntigo = c.getNome();
            c.setNome(categoriaEditada.getNome());
            categoriaJpaController.edit(c);

            context.addMessage(null, new FacesMessage("Categoria editada!", nomeAntigo + " -> " + c.getNome()));

            categoriaEditada = new Categoria();

        } catch (NonexistentEntityException ex) {
            context.addMessage(null, new FacesMessage("Categoria não encontrada!", produto.getNome()));
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha ao editar a categoria!", produto.getNome()));
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void adicionarCategoria() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            categoriaJpaController.create(novaCategoria);
            context.addMessage(null, new FacesMessage("Categoria cadastrada!", novaCategoria.getNome()));
            novaCategoria = new Categoria();
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha ao cadastrar a categoria!", novaCategoria.getNome()));
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Categoria getNovaCategoria() {
        return novaCategoria;
    }

    public void setNovaCategoria(Categoria novaCategoria) {
        this.novaCategoria = novaCategoria;
    }

    public Categoria getCategoriaEditada() {
        return categoriaEditada;
    }

    public void setCategoriaEditada(Categoria categoriaEditada) {
        this.categoriaEditada = categoriaEditada;
    }
}
