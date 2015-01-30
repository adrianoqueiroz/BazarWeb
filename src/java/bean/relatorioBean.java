/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.CategoriaJpaController;
import JPA.ItemJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.Categoria;
import model.Item;
import model.Relatorio;

/**
 *
 * @author adriano.queiroz
 */
@ManagedBean
@ViewScoped
public class relatorioBean implements Serializable {

    @EJB
    private CategoriaJpaController categoriaJpaController;
    @EJB
    private ItemJpaController itemJpaController;

    private Collection<Relatorio> relatorioCollection = new ArrayList<>();

    private int produtosVendidosGeral;
    private float totalGeral;

    public Collection<Relatorio> getRelatorioCollection() {
        Collection<Categoria> categoriaCollection = categoriaJpaController.findCategoriaEntities();

        List<Item> itemCollection = itemJpaController.findItemEntities();
        
        totalGeral = 0;
        produtosVendidosGeral = 0;
        
        for (Categoria c : categoriaCollection) {
            int qtdProdutos = 0;
            float total = 0;
            for (Item i : itemCollection) {
                if (Objects.equals(i.getProdutoId().getCategoriaId().getId(), c.getId())) {
                    qtdProdutos += i.getQuantidade();
                    total += i.getPrecoVenda() * i.getQuantidade();
                }
            }

            Relatorio relatorio = new Relatorio();
            relatorio.setProdutosVendidos(qtdProdutos);
            relatorio.setCategoria(c.getNome());
            relatorio.setTotal(total);

            relatorioCollection.add(relatorio);
            
            totalGeral += total;
            produtosVendidosGeral += qtdProdutos;
        }

        return relatorioCollection;
    }

    public void setRelatorioCollection(Collection<Relatorio> relatorioCollection) {
        this.relatorioCollection = relatorioCollection;
    }

    public int getProdutosVendidosGeral() {
        return produtosVendidosGeral;
    }

    public void setProdutosVendidosGeral(int produtosVendidosGeral) {
        this.produtosVendidosGeral = produtosVendidosGeral;
    }

    public float getTotalGeral() {
        return totalGeral;
    }

    public void setTotalGeral(float totalGeral) {
        this.totalGeral = totalGeral;
    }

}
