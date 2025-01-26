package org.example.scrapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RaspadorDeDadosBoletimDiarioComSelenium implements RaspadorDeDadosBoletimDiario {

	private final static String PAGINA_PESQUISA_BOLETIM_DIARIO = "http://minas1.ceasa.mg.gov.br/detec/filtro_boletim/filtro_boletim.php";
	private final static String XPATH_SELECT_ELEMENT_DIAS_DE_COTACAO = "//select[@id='id_sc_field_datas']";
	private final static String XPATH_SELECT_ELEMENT_MERCADOS = "//select[@id='id_sc_field_mercado']";
	private final static String XPATH_BOTAO_SUBMIT = "//a[@id='sc_bbtboletim_bot']";

	private final WebDriver driver;
	private final WebDriverWait wait;
	private final SeletorDeMercados seletorDeMercados;
	private final SeletorDeDatasDeCotacao seletorDeDatasDeCotacao;
	private final PaginaDeBoletimDiarioCompleto paginaDeBoletimDiario;

	public RaspadorDeDadosBoletimDiarioComSelenium(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		this.seletorDeMercados = new SeletorDeMercados();
		this.seletorDeDatasDeCotacao = new SeletorDeDatasDeCotacao();
		this.paginaDeBoletimDiario = new PaginaDeBoletimDiarioCompleto();
	}

	@Override
	public List<DadosRaspadosSobreMercado> raspeDadosSobreMercados() {
		navegaParaPaginaDePesquisa();
		return seletorDeMercados.dadosRaspadosSobreMercados();
	}

	@Override
	public List<LocalDate> raspeDiasDeCotacaoParaMercado(String siglaDeMercado) {
		navegaParaPaginaDePesquisa();
		seletorDeMercados.selecione(siglaDeMercado);
		return seletorDeDatasDeCotacao.datas();
	}

	@Override
	public void raspeBoletimDiarioParaMercado(String siglaDeMercado, List<LocalDate> diasDeCotacao,
			ManipuladorDeDadosRaspados<DadosRaspadosDeBoletimDiario> manipulador) {
		navegaParaPaginaDePesquisa();
		var mercado = seletorDeMercados.selecione(siglaDeMercado);
		for (var dataDeCotacao : diasDeCotacao) {
			seletorDeDatasDeCotacao.selecione(dataDeCotacao);
			var cotacoesDeProdutos = paginaDeBoletimDiario.cotacoesDeProdutos();
			var boletimDiarioCompleto = new DadosRaspadosDeBoletimDiario(mercado, dataDeCotacao, cotacoesDeProdutos);
			manipulador.manipule(boletimDiarioCompleto);
			paginaDeBoletimDiario.voltarParaPaginaDePesquisa();
		}
	}

	private void navegaParaPaginaDePesquisa() {
		driver.get(PAGINA_PESQUISA_BOLETIM_DIARIO);
		wait.until(d -> !d.getTitle().isBlank());
	}

	// -------------------------------------------------
	// Classes auxiliares que abtraem o fluxo de navegação no navegador
	// indicando a navegação como ela é realizada usando o mouse em uma GUI.
	// A ordem em que os metodos são invocados é determinante para o sucesso
	// da navegação.
	// -------------------------------------------------
	class SeletorDeMercados {
		List<DadosRaspadosSobreMercado> dadosRaspadosSobreMercados() {
			navegaParaPaginaDePesquisa();
			var el = driver.findElement(By.xpath(XPATH_SELECT_ELEMENT_MERCADOS));
			var sel = new Select(el);
			wait.until(__ -> sel.getOptions().size() > 0);
			return sel.getOptions()
					.stream()
					.filter(o -> !(o.getDomProperty("value").matches("0")))
					.map(o -> {
						var val = o.getDomProperty("value");
						var sigla = o.getText().split(" - ")[1].toLowerCase();
						var m = new DadosRaspadosSobreMercado(o.getText(), val, sigla);
						return m;
					})
					.toList();
		}

		public DadosRaspadosSobreMercado selecione(String siglaDeMercado) {
			var mercado = busquePelaSigla(siglaDeMercado);
			navegaParaPaginaDePesquisa();
			var el = driver.findElement(By.xpath(XPATH_SELECT_ELEMENT_MERCADOS));
			var sel = new Select(el);
			wait.until(__ -> sel.getOptions().size() > 0);
			sel.selectByValue(mercado.value());
			return mercado;
		}

		public DadosRaspadosSobreMercado busquePelaSigla(String siglaDeMercado) {
			var mercados = dadosRaspadosSobreMercados();
			return mercados.stream()
					.filter(m -> m.sigla().equals(siglaDeMercado))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Mercado nao encontrado para sigla: " + siglaDeMercado));
		}
	}

	class SeletorDeDatasDeCotacao {
		List<LocalDate> datas() {
			var el = driver.findElement(By.xpath(XPATH_SELECT_ELEMENT_DIAS_DE_COTACAO));
			var sel = new Select(el);
			wait.until(__ -> sel.getOptions().size() > 0);
			return sel.getOptions()
					.stream()
					.filter(o -> !(o.getDomProperty("value").isBlank()))
					.map(WebElement::getText)
					.map(this::paraISODateStr)
					.map(LocalDate::parse)
					.toList();
		}

		void selecione(LocalDate data) {
			var el = driver.findElement(By.xpath(XPATH_SELECT_ELEMENT_DIAS_DE_COTACAO));
			var sel = new Select(el);
			wait.until(__ -> sel.getOptions().size() > 0);
			sel.selectByVisibleText(deDataParaRotulo(data));
			driver.findElement(By.xpath(XPATH_BOTAO_SUBMIT))
					.click();
			wait.until(ExpectedConditions.titleContains("Data"));
		}

		String paraISODateStr(String dataStr) {
			var strs = dataStr.split("/");
			return String.format("%s-%s-%s", strs[2], strs[1], strs[0]);
		}

		String deDataParaRotulo(LocalDate data) {
			return String.format("%d/%02d/%d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
		}
	}

	class PaginaDeBoletimDiarioCompleto {
		List<CotacaoDeProduto> cotacoesDeProdutos() {
			var locator = By.xpath("//table[@class='scGridTabela']/tbody/tr[not(@id='tit_boletim_completo__SCCS__1')]");
			wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, 0));
			var productRows = driver.findElements(locator);
			var cotacoes = new ArrayList<CotacaoDeProduto>();
			for (var row : productRows) {
				var cotacao = raspeCotacaoDeLinhaDeTabela(row);
				cotacoes.add(cotacao);
			}
			return cotacoes;
		}

		public void voltarParaPaginaDePesquisa() {
			var backBtn = driver.findElement(By.xpath("//a[@id='sai_top']"));
			backBtn.click();
			wait.until(ExpectedConditions.titleContains("Pesquisa"));
		}

		CotacaoDeProduto raspeCotacaoDeLinhaDeTabela(WebElement linha) {
			var cells = linha.findElements(By.xpath("./td/span"));
			String name = "", embalagem = "", situacao = "";
			Double pMin = 0.0, pCom = 0.0, pMax = 0.0;
			for (var field : cells) {
				var id = field.getDomProperty("id");
				var val = field.getText();
				if (id.contains("prdnom"))
					name = val;
				else if (id.contains("mersit"))
					situacao = val;
				else if (id.contains("embdesresu"))
					embalagem = val;
				else {
					var nVal = Double.parseDouble(val.replace(',', '.'));
					if (id.contains("pboprcmin"))
						pMin = nVal;
					else if (id.contains("pboprccomum"))
						pCom = nVal;
					else if (id.contains("pboprcmax"))
						pMax = nVal;
				}
			}
			return new CotacaoDeProduto(name, embalagem, pMin, pCom, pMax, situacao);
		}
	}
}
