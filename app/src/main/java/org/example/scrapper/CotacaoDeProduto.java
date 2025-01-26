package org.example.scrapper;

public record CotacaoDeProduto(
		String nomeDoProduto,
		String embalagem,
		double precoMinimo,
		double precoComum,
		double precoMaximo,
		String situacao) {
}
