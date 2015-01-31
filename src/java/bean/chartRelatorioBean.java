/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.CategoriaJpaController;
import JPA.ItemJpaController;
import JPA.ProdutoJpaController;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import model.Categoria;
import model.Item;
import model.Produto;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Adriano
 */
@ManagedBean
public class chartRelatorioBean implements Serializable {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private PieChartModel pie;
    private PieChartModel pieVendasFinalizadas;
    @EJB
    private ItemJpaController itemJpaController;
    @EJB
    private ProdutoJpaController produtoJpaController;
    @EJB
    private CategoriaJpaController categoriaJpaController;

    private Collection<Categoria> categoriaCollection;

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    @PostConstruct
    public void init() {
        createPieModels();
    }

    public PieChartModel getPie() {
        return pie;
    }

    private void createPieModels() {
        createPieVendasFinalizadas();
        createPieModel2();
    }

    private void createPieModel2() {

        pie = new PieChartModel();

        categoriaCollection = categoriaJpaController.findCategoriaEntities();
        for (Categoria c : categoriaCollection) {
            List<Produto> produtoCollection = produtoJpaController.findProdutoEntitiesByCategoria(c);

            pie.set(c.getNome(), produtoCollection.size());
        }

        pie.setTitle("Vendas por Categoria");
        pie.setLegendPosition("w");
        pie.setFill(true);
        pie.setShowDataLabels(true);
        pie.setDiameter(50);
    }

    public PieChartModel getPieVendasFinalizadas() {
        return pieVendasFinalizadas;
    }

    public void setPieVendasFinalizadas(PieChartModel pieVendasFinalizadas) {
        this.pieVendasFinalizadas = pieVendasFinalizadas;
    }

    private void createPieVendasFinalizadas() {

        pieVendasFinalizadas = new PieChartModel();

        categoriaCollection = categoriaJpaController.findCategoriaEntities();
        for (Categoria c : categoriaCollection) {
            List<Item> itemCollection = itemJpaController.findItemEntitiesByEventoWithVendaPagaAndCategoria(loginBean.getEventoSelecionado(), c);

            pieVendasFinalizadas.set(c.getNome(), itemCollection.size());
        }

        pieVendasFinalizadas.setTitle("Vendas por Categoria");
        pieVendasFinalizadas.setLegendPosition("w");
        pieVendasFinalizadas.setFill(true);
        pieVendasFinalizadas.setShowDataLabels(true);
        pieVendasFinalizadas.setDiameter(50);
    }

}
