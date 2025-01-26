package org.example.scrapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class RaspadorDeDadosBoletimDiarioComSeleniumTest {

	private WebDriver driver;
	private RaspadorDeDadosBoletimDiarioComSelenium raspador;

	@BeforeEach
	void setup() {
		var chromeOptions = new ChromeOptions();
		chromeOptions.setBinary("/usr/bin/google-chrome-stable");
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-gpu");
		this.driver = new ChromeDriver(chromeOptions);
		this.raspador = new RaspadorDeDadosBoletimDiarioComSelenium(driver);
	}

	@Test
	void deveRasparDadosDeMercados() {
		var dadosSobreMercados = raspador.raspeDadosSobreMercados();
		assertTrue(dadosSobreMercados.size() > 0);
		var dadosSobreMercado = dadosSobreMercados.get(0);
		assertFalse(dadosSobreMercado.rotulo().isBlank());
		System.out.println(dadosSobreMercado);
	}

	@Test
	void deveRasparAsDatasDeCotacoesParaMercado() {
		var siglaMercado = "ceard";
		var diasDeCotacao = raspador.raspeDiasDeCotacaoParaMercado(siglaMercado);
		assertTrue(diasDeCotacao.size() > 0);
		var dataDeCotacao = diasDeCotacao.get(0);
		assertTrue(dataDeCotacao.getYear() > 2019);
		System.out.println("dataDeCotacao = " + dataDeCotacao);
	}

	@Test
	void deveRasparDadosDeBoletimDiarioParaMercado() {
		var siglaMercado = "ceard";
		var dataDeCotacao = raspador.raspeDiasDeCotacaoParaMercado(siglaMercado).get(0);
		raspador.raspeBoletimDiarioParaMercado(siglaMercado,
				List.of(dataDeCotacao),
				(bol) -> {
					assertTrue(bol.cotacoesDeProduto().size() > 0);
					assertTrue(bol.mercado().rotulo().toLowerCase().contains(siglaMercado));
					assertTrue(bol.dataDaCotacao().getYear() == dataDeCotacao.getYear());
					assertTrue(bol.cotacoesDeProduto().get(0).precoComum() > 0);
				});
	}
}
