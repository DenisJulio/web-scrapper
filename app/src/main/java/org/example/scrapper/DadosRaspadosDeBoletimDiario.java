package org.example.scrapper;

import java.time.LocalDate;
import java.util.List;

public record DadosRaspadosDeBoletimDiario(DadosRaspadosSobreMercado mercado, LocalDate dataDaCotacao,
		List<CotacaoDeProduto> cotacoesDeProduto) {
}
