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
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import model.Categoria;
import model.Item;
import model.Relatorio;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author adriano.queiroz
 */
@ManagedBean
@ViewScoped
public class RelatorioBean implements Serializable {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @EJB
    private CategoriaJpaController categoriaJpaController;
    private Collection<Categoria> categoriaCollection;
    @EJB
    private ItemJpaController itemJpaController;

    private Collection<Relatorio> relatorioCollection;
    private Collection<Relatorio> relatorioCollectionPeriodo;

    private PieChartModel pieVendasFinalizadasGeral;
    private PieChartModel pieVendasFinalizadasPeriodo;

    private int produtosVendidosGeral;
    private float totalGeral;

    private int produtosVendidosPeriodo;
    private float totalPeriodo;

    private java.util.Date dataInicial;
    private java.util.Date dataFinal;

    public RelatorioBean() {
        dataInicial = new Date();
        dataInicial.setHours(0);
        dataInicial.setMinutes(0);
        dataInicial.setSeconds(0);

        dataFinal = new Date();
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    private void createPieVendasFinalizadasGeral() {
        pieVendasFinalizadasGeral = new PieChartModel();

        categoriaCollection = categoriaJpaController.findCategoriaEntities();
        for (Categoria c : categoriaCollection) {
            List<Item> itemCollection = itemJpaController.findItemEventoCategoriaPago(loginBean.getEventoSelecionado(), c);
            int quantidadeVendidos = 0;
            for (Item i : itemCollection) {
                quantidadeVendidos += i.getQuantidade();
            }
            if (quantidadeVendidos > 0) {
                pieVendasFinalizadasGeral.set(c.getNome(), quantidadeVendidos);
            }
        }

        pieVendasFinalizadasGeral.setTitle("Vendas por Categoria");
        pieVendasFinalizadasGeral.setLegendPosition("w");
        pieVendasFinalizadasGeral.setFill(true);
        pieVendasFinalizadasGeral.setShowDataLabels(true);
        pieVendasFinalizadasGeral.setDiameter(50);
    }

    private void createPieVendasFinalizadasPeriodo() {
        pieVendasFinalizadasPeriodo = new PieChartModel();

        categoriaCollection = categoriaJpaController.findCategoriaEntities();
        for (Categoria c : categoriaCollection) {
            List<Item> itemCollection = itemJpaController.EventoCategoriaPeriodoPago(loginBean.getEventoSelecionado(), c, dataInicial, dataFinal);
            int quantidadeVendidos = 0;
            for (Item i : itemCollection) {
                quantidadeVendidos += i.getQuantidade();
            }
            if (quantidadeVendidos > 0) {
                pieVendasFinalizadasPeriodo.set(c.getNome(), quantidadeVendidos);
            }
        }

        pieVendasFinalizadasPeriodo.setTitle("Vendas por Categoria");
        pieVendasFinalizadasPeriodo.setLegendPosition("w");
        pieVendasFinalizadasPeriodo.setFill(true);
        pieVendasFinalizadasPeriodo.setShowDataLabels(true);
        pieVendasFinalizadasPeriodo.setDiameter(50);
    }

    public Collection<Relatorio> getRelatorioCollection() {
        createRelatorioCollection();
        return relatorioCollection;
    }

    private void createRelatorioCollection() {
        categoriaCollection = categoriaJpaController.findCategoriaEntities();

        List<Item> itemCollection;
        relatorioCollection = new ArrayList<>();
        totalGeral = 0;
        produtosVendidosGeral = 0;

        for (Categoria c : categoriaCollection) {
            itemCollection = itemJpaController.findItemEventoCategoriaPago(loginBean.getEventoSelecionado(), c);

            int qtdProdutos = 0;
            float total = 0;

            for (Item i : itemCollection) {
                total += i.getPrecoVenda() * i.getQuantidade();
                qtdProdutos += i.getQuantidade();
            }

            if (qtdProdutos > 0) {
                Relatorio relatorio = new Relatorio();
                relatorio.setProdutosVendidos(qtdProdutos);
                relatorio.setCategoria(c.getNome());
                relatorio.setTotal(total);

                relatorioCollection.add(relatorio);

                totalGeral += total;
                produtosVendidosGeral += qtdProdutos;
            }
        }
    }

    private void createRelatorioCollectionPeriodo() {
        categoriaCollection = categoriaJpaController.findCategoriaEntities();

        List<Item> itemCollection;

        relatorioCollectionPeriodo = new ArrayList<>();
        totalPeriodo = 0;
        produtosVendidosPeriodo = 0;

        for (Categoria c : categoriaCollection) {
            itemCollection = itemJpaController.EventoCategoriaPeriodoPago(loginBean.getEventoSelecionado(), c, dataInicial, dataFinal);

            int qtdProdutos = 0;
            float total = 0;

            for (Item i : itemCollection) {
                total += i.getPrecoVenda() * i.getQuantidade();
                qtdProdutos += i.getQuantidade();
            }

            if (qtdProdutos > 0) {
                Relatorio relatorio = new Relatorio();
                relatorio.setProdutosVendidos(qtdProdutos);
                relatorio.setCategoria(c.getNome());
                relatorio.setTotal(total);

                relatorioCollectionPeriodo.add(relatorio);

                totalPeriodo += total;
                produtosVendidosPeriodo += qtdProdutos;
            }
        }
    }

    public Collection<Relatorio> getRelatorioCollectionPeriodo() {
        createRelatorioCollectionPeriodo();
        return relatorioCollectionPeriodo;
    }

    public void atualizaRelatorioPeriodo() {
        createPieVendasFinalizadasPeriodo();
        createRelatorioCollectionPeriodo();
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

    public PieChartModel getPieVendasFinalizadasGeral() {
        createPieVendasFinalizadasGeral();

        return pieVendasFinalizadasGeral;
    }

    public void setPieVendasFinalizadasGeral(PieChartModel pieVendasFinalizadasGeral) {
        this.pieVendasFinalizadasGeral = pieVendasFinalizadasGeral;
    }

    public PieChartModel getPieVendasFinalizadasPeriodo() {
        createPieVendasFinalizadasPeriodo();

        return pieVendasFinalizadasPeriodo;
    }

    public void setPieVendasFinalizadasPeriodo(PieChartModel pieVendasFinalizadasPeriodo) {
        this.pieVendasFinalizadasPeriodo = pieVendasFinalizadasPeriodo;
    }

    public java.util.Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(java.util.Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public java.util.Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(java.util.Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void setRelatorioCollectionPeriodo(Collection<Relatorio> relatorioCollectionPeriodo) {
        this.relatorioCollectionPeriodo = relatorioCollectionPeriodo;
    }

    public int getProdutosVendidosPeriodo() {
        return produtosVendidosPeriodo;
    }

    public void setProdutosVendidosPeriodo(int produtosVendidosPeriodo) {
        this.produtosVendidosPeriodo = produtosVendidosPeriodo;
    }

    public float getTotalPeriodo() {
        return totalPeriodo;
    }

    public void setTotalPeriodo(float totalPeriodo) {
        this.totalPeriodo = totalPeriodo;
    }
}
