/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.CategoriaJpaController;
import JPA.ProdutoJpaController;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import model.Categoria;
import model.Produto;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Adriano
 */
@ManagedBean
public class chartRelatorioBean implements Serializable {

    private PieChartModel pie;

    @EJB
    private ProdutoJpaController produtoJpaController;
    @EJB
    private CategoriaJpaController categoriaJpaController;

    private Collection<Categoria> categoriaCollection;
    @PostConstruct
    public void init() {
        createPieModels();
    }

    public PieChartModel getPie() {
        return pie;
    }

    private void createPieModels() {
        createPieModel2();
    }

       private void createPieModel2() {

        pie = new PieChartModel();

        categoriaCollection = categoriaJpaController.findCategoriaEntities();
        for (Categoria c: categoriaCollection){
            List<Produto> produtoCollection = produtoJpaController.findProdutoEntitiesByCategoria(c);
            
            pie.set(c.getNome(), produtoCollection.size());
        }

        pie.setTitle("Vendas por Categoria");
        pie.setLegendPosition("w");
        pie.setFill(true);
        pie.setShowDataLabels(true);
        pie.setDiameter(150);
    }

}
