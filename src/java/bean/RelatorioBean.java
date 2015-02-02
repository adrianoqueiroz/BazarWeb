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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import model.Categoria;
import model.Item;
import model.Relatorio;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
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

    private LineChartModel multiAxisModel;
    private LineChartModel multiAxisModelPeriodo;

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

    @PostConstruct
    public void init() {
        categoriaCollection = categoriaJpaController.findCategoriaEntities();
        createRelatorioCollection();
        createRelatorioCollectionPeriodo();
        createMultiAxisModel();
        createMultiAxisModelPeriodo();
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public LineChartModel getMultiAxisModel() {
        return multiAxisModel;
    }

    public LineChartModel getMultiAxisModelPeriodo() {
        return multiAxisModelPeriodo;
    }

    private void createMultiAxisModel() {
        multiAxisModel = new LineChartModel();
        multiAxisModel = initBarModel();

        multiAxisModel.setAnimate(true);
        multiAxisModel.setLegendPosition("ne");

    }

    private void createMultiAxisModelPeriodo() {
        multiAxisModelPeriodo = new LineChartModel();
        multiAxisModelPeriodo = initBarModelPeriodo();

        multiAxisModelPeriodo.setAnimate(true);
        multiAxisModelPeriodo.setLegendPosition("ne");

    }

    private LineChartModel initBarModel() {
        int maxProdutosVendidos;
        float maxFaturamento;

        BarChartSeries produtosVendidos = new BarChartSeries();
        LineChartSeries faturamento = new LineChartSeries();

        faturamento.setLabel("Faturamento");
        faturamento.setYaxis(AxisType.Y2);

        produtosVendidos.setLabel("Produtos Vendidos");

        maxProdutosVendidos = 0;
        maxFaturamento = 0;
        for (Relatorio r : relatorioCollection) {
            if (r.getProdutosVendidos() > maxProdutosVendidos) {
                maxProdutosVendidos = r.getProdutosVendidos();
            }
            if (r.getTotal() > maxFaturamento) {
                maxFaturamento = r.getTotal();
            }

            produtosVendidos.set(r.getCategoria(), r.getProdutosVendidos());
            faturamento.set(r.getCategoria(), r.getTotal());
        }

        multiAxisModel.addSeries(produtosVendidos);
        multiAxisModel.addSeries(faturamento);

        multiAxisModel.setTitle("Produtos Vendidos e Faturamento por Categoria");
        multiAxisModel.setMouseoverHighlight(false);

        multiAxisModel.getAxes().put(AxisType.X, new CategoryAxis("Categoria"));

        Axis yAxis = multiAxisModel.getAxis(AxisType.Y);
        yAxis.setLabel("Produtos Vendidos");
        yAxis.setMin(0);
        yAxis.setMax(maxProdutosVendidos + 10);

        Axis y2Axis = new LinearAxis("Faturamento em Reais");
        y2Axis.setMin(0);
        y2Axis.setMax(maxFaturamento + 1000);

        multiAxisModel.getAxes().put(AxisType.Y2, y2Axis);

        return multiAxisModel;
    }

    private LineChartModel initBarModelPeriodo() {
        int maxProdutosVendidos;
        float maxFaturamento;

        BarChartSeries produtosVendidos = new BarChartSeries();
        LineChartSeries faturamento = new LineChartSeries();

        faturamento.setLabel("Faturamento");
        faturamento.setYaxis(AxisType.Y2);

        produtosVendidos.setLabel("Produtos Vendidos");

        maxProdutosVendidos = 0;
        maxFaturamento = 0;
        for (Relatorio r : relatorioCollectionPeriodo) {
            if (r.getProdutosVendidos() > maxProdutosVendidos) {
                maxProdutosVendidos = r.getProdutosVendidos();
            }
            if (r.getTotal() > maxFaturamento) {
                maxFaturamento = r.getTotal();
            }

            produtosVendidos.set(r.getCategoria(), r.getProdutosVendidos());
            faturamento.set(r.getCategoria(), r.getTotal());
        }

        multiAxisModelPeriodo.addSeries(produtosVendidos);
        multiAxisModelPeriodo.addSeries(faturamento);

        multiAxisModelPeriodo.setTitle("Produtos Vendidos e Faturamento por Categoria");
        multiAxisModelPeriodo.setMouseoverHighlight(false);

        multiAxisModelPeriodo.getAxes().put(AxisType.X, new CategoryAxis("Categoria"));

        Axis yAxis = multiAxisModelPeriodo.getAxis(AxisType.Y);
        yAxis.setLabel("Produtos Vendidos");
        yAxis.setMin(0);
        yAxis.setMax(maxProdutosVendidos + 10);

        Axis y2Axis = new LinearAxis("Faturamento em Reais");
        y2Axis.setMin(0);
        y2Axis.setMax(maxFaturamento + 1000);

        multiAxisModelPeriodo.getAxes().put(AxisType.Y2, y2Axis);

        return multiAxisModelPeriodo;
    }

    private void createPieVendasFinalizadasGeral() {
        pieVendasFinalizadasGeral = new PieChartModel();

        for (Relatorio r : relatorioCollection) {
            pieVendasFinalizadasGeral.set(r.getCategoria(), r.getProdutosVendidos());
        }

        pieVendasFinalizadasGeral.setTitle("Produtos Vendidos");
        pieVendasFinalizadasGeral.setLegendPosition("w");
        pieVendasFinalizadasGeral.setFill(true);
        pieVendasFinalizadasGeral.setShowDataLabels(true);
        pieVendasFinalizadasGeral.setDiameter(50);
    }

    private void createPieVendasFinalizadasPeriodo() {
        pieVendasFinalizadasPeriodo = new PieChartModel();

        for (Relatorio r : relatorioCollectionPeriodo) {
            pieVendasFinalizadasPeriodo.set(r.getCategoria(), r.getProdutosVendidos());
        }
        pieVendasFinalizadasPeriodo.setTitle("Produtos Vendidos");
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
        createRelatorioCollectionPeriodo();
        createPieVendasFinalizadasPeriodo();
        createMultiAxisModelPeriodo();
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
